/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.mail.MessagingException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author erwin
 */
public class ListadoAbonosEntradasDiarias extends javax.swing.JFrame {

    String usuario, permiso;
    DefaultTableModel modelo;

    /**
     * Creates new form ImprimirRecibo
     */
    public ListadoAbonosEntradasDiarias() {
        initComponents();
        IniciarCaracteristicasGenerales();

        ConfiguracionGralJFrame();
    }

    public ListadoAbonosEntradasDiarias(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

        if (!permiso.equals("Gerente")) {
            jButton_editar.setEnabled(false);
            jButton_eliminar.setEnabled(false);
        }
    }

    public void IniciarCaracteristicasGenerales() {

        jLabel_abono.setVisible(false);
        jLabel_observaciones.setVisible(false);
        jLabel_monto.setVisible(false);
        jLabel_fecha.setVisible(false);
        jLabel_idventa.setVisible(false);
        jTextField_idAbono.setEnabled(false);
        jTextField_cliente.setEnabled(false);
        SettearModelo();
        llenarTabla();
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado de abonos registrados *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void SettearModelo() {
        this.modelo = (DefaultTableModel) jTable_abonos.getModel();
    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_abonos.getRowCount(); i++) {
            this.modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void llenarTabla() {
        try {
            String consulta = "select a.idAbono, a.idVenta, a.fecha, c.nombreCliente, a.valor, a.observaciones, a.registradoPor\n"
                    + "from abonos a join ventas v on a.idVenta=v.Idventa\n"
                    + "join clientes c on v.Idcliente=c.idCliente\n"
                    + "where a.estado='Activo' order by a.idAbono desc";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable_abonos.getModel();

            while (rs.next()) {
                Object[] listadoAbonos = new Object[7];

                listadoAbonos[0] = rs.getString("a.idAbono");
                listadoAbonos[1] = rs.getString("a.idVenta");
                listadoAbonos[2] = rs.getString("a.fecha");
                listadoAbonos[3] = rs.getString("c.nombreCliente");
                listadoAbonos[4] = MetodosGenerales.ConvertirIntAMoneda(rs.getDouble("a.valor"));
                listadoAbonos[5] = rs.getString("a.observaciones");
                listadoAbonos[6] = rs.getString("a.registradoPor");

                modelo.addRow(listadoAbonos);
            }
            jTable_abonos.setModel(modelo);
            
            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable_abonos.setRowSorter(ordenador);
            
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos en la tabla abonos desde la base de datos."
                    + "ImprimirRecibos LlenarTabla()");
        }
    }

    public Object[] LeerDatosAbono(String idAbono, String idVenta) {

        Object datosRecibo[] = new Object[14];

        String consulta = "select a.fecha, c.nombreCliente, c.identificacion, c.telefono, v.descripcionTrabajo, v.Cantidad, v.tamaño, a.valor as valorAbono, v.precio, v.Idventa, a.registradoPor \n"
                + "from abonos a join ventas v on a.idVenta=v.Idventa \n"
                + "join clientes c on v.Idcliente=c.idCliente\n"
                + "where a.estado='Activo' and a.idAbono=? ";

        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, idAbono);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                datosRecibo[0] = rs.getDate("a.fecha");
                datosRecibo[1] = idAbono;
                datosRecibo[2] = rs.getString("c.nombreCliente");
                datosRecibo[3] = rs.getString("c.identificacion");
                datosRecibo[4] = rs.getString("c.telefono");
                datosRecibo[5] = rs.getString("v.descripcionTrabajo");
                datosRecibo[6] = rs.getDouble("v.Cantidad");
                datosRecibo[7] = rs.getString("v.tamaño");
                datosRecibo[8] = rs.getDouble("valorAbono");
                //datosRecibo[9] = rs.getString("totalAbonos");
                datosRecibo[10] = rs.getDouble("v.precio");
                //datosRecibo[11] = String.valueOf(Integer.parseInt(datosRecibo[10]) - Integer.parseInt(datosRecibo[9]));
                datosRecibo[12] = rs.getString("a.registradoPor");
                datosRecibo[13] = rs.getDouble("v.Idventa");

            }
            cn.close();
            pst.close();

            String consulta2 = "select sum(valor) as total from abonos where idVenta=?";
            Connection cn2 = Conexion.Conectar();
            PreparedStatement pst2 = cn2.prepareStatement(consulta2);
            pst2.setString(1, idVenta);

            ResultSet rs2 = pst2.executeQuery();

            if (rs2.next()) {
                datosRecibo[9] = rs2.getDouble("total");
                datosRecibo[11] = (Double) datosRecibo[10] - rs2.getDouble("total");
            }

            cn2.close();
            pst2.close();

            int i = 0;

        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer los datos del abono ImprimirRecibo LeerDatosAbono()","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return datosRecibo;
    }

    public void GenerarRecibo(Object[] datosRecibo) {

        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Recibo.xlsx";

        String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Recibo " + datosRecibo[1] + " Cliente " + datosRecibo[2] + ".xlsx";

        //Leemos el archivo que vamos a modificar y creamos el XSSFWorkbook
        try {

            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);

            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            //Datos de la primera fila
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda15 = fila1.getCell(5);
            celda15.setCellValue((Date) datosRecibo[0]);
            XSSFCell celda115 = fila1.getCell(15);
            celda115.setCellValue((String) datosRecibo[1]);
            
            //Datos de la 13
            
            XSSFRow fila13 = hoja.getRow(13);
            XSSFCell celda135 = fila13.getCell(5);
            celda135.setCellValue((Date) datosRecibo[0]);
            XSSFCell celda1315 = fila13.getCell(15);
            celda1315.setCellValue((String) datosRecibo[1]);
            
            

            //Datos segunda fila
            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda25 = fila2.getCell(5);
            celda25.setCellValue((String) datosRecibo[2]);
            XSSFCell celda210 = fila2.getCell(10);
            celda210.setCellValue((String) datosRecibo[3]);
            XSSFCell celda215 = fila2.getCell(15);
            celda215.setCellValue((String) datosRecibo[4]);

            //Datos de la 14
            XSSFRow fila14 = hoja.getRow(14);
            XSSFCell celda145 = fila14.getCell(5);
            celda145.setCellValue((String) datosRecibo[2]);
            XSSFCell celda1410 = fila14.getCell(10);
            celda1410.setCellValue((String) datosRecibo[3]);
            XSSFCell celda1415 = fila14.getCell(15);
            celda1415.setCellValue((String) datosRecibo[4]);
            
            //Datos tercera fila
            XSSFRow fila3 = hoja.getRow(3);
            XSSFCell celda35 = fila3.getCell(5);
            celda35.setCellValue((String) datosRecibo[5]);
            XSSFCell celda312 = fila3.getCell(12);
            celda312.setCellValue((Double) datosRecibo[6]);
            XSSFCell celda315 = fila3.getCell(15);
            celda315.setCellValue((String) datosRecibo[7]);

            //Datos de la fila 15
            
            XSSFRow fila15 = hoja.getRow(15);
            XSSFCell celda155 = fila15.getCell(5);
            celda155.setCellValue((String) datosRecibo[5]);
            XSSFCell celda1512 = fila15.getCell(12);
            celda1512.setCellValue((Double) datosRecibo[6]);
            XSSFCell celda1515 = fila15.getCell(15);
            celda1515.setCellValue((String) datosRecibo[7]);
            
            
            //Datos cuarta fila
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda45 = fila4.getCell(5);
            celda45.setCellValue((Double) datosRecibo[8]);
            XSSFCell celda49 = fila4.getCell(9);
            celda49.setCellValue((Double) datosRecibo[9]);
            
            //Datos fila 16
            XSSFRow fila16 = hoja.getRow(16);
            XSSFCell celda165 = fila16.getCell(5);
            celda165.setCellValue((Double) datosRecibo[8]);
            XSSFCell celda169 = fila16.getCell(9);
            celda169.setCellValue((Double) datosRecibo[9]);
            
            

            //Datos quinta fila
            XSSFRow fila5 = hoja.getRow(5);
            XSSFCell celda55 = fila5.getCell(5);
            celda55.setCellValue((Double) datosRecibo[10]);
            XSSFCell celda58 = fila5.getCell(8);
            celda58.setCellValue((Double) datosRecibo[11]);
            XSSFCell celda514 = fila5.getCell(14);
            celda514.setCellValue((Double) datosRecibo[13]);

            //Datos fila 17
            XSSFRow fila17 = hoja.getRow(17);
            XSSFCell celda175 = fila17.getCell(5);
            celda175.setCellValue((Double) datosRecibo[10]);
            XSSFCell celda178 = fila17.getCell(8);
            celda178.setCellValue((Double) datosRecibo[11]);
            XSSFCell celda1714 = fila17.getCell(14);
            celda1714.setCellValue((Double) datosRecibo[13]);
            
            //Datos octava fila
            XSSFRow fila8 = hoja.getRow(7);
            XSSFCell celda87 = fila8.getCell(5);
            celda87.setCellValue((String) datosRecibo[12]);
            
            //Datos fila 19
            XSSFRow fila19 = hoja.getRow(19);
            XSSFCell celda195 = fila19.getCell(5);
            celda195.setCellValue((String) datosRecibo[12]);

            FileOutputStream ultimo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(ultimo);
            ultimo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error al crear el documento. Un documento con el mismo nombre esta abierto."
                    + "\nCierrelo e intentelo nuevamente","Error",JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en general el recibo. Asegurate de no tenr abierto otro recibo. Si el"
                    + " problema persiste, contacta al administrador. ImprimirRecibo GenerarRecibo()","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public void EliminarAbono(String idAbono, String observaciones, String cliente) {
        String consulta = "delete from abonos where idAbono=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idAbono);

            pst.execute();
            JOptionPane.showMessageDialog(this, "Abono eliminado");
            cn.close();

            String asunto = "Abono " + idAbono + " eliminado - Entradas diarias";
            String mensaje = "El abono No. " + idAbono +" "+ cliente+" ha sido eliminado"
                    + "\nValor del abono = " + jLabel_monto.getText()
                    + "\nUsuario responsable: " + this.usuario
                    + "\nObservaciones: " + observaciones;

            MetodosGenerales.enviarEmail(asunto, mensaje);
            MetodosGenerales.registrarHistorial(usuario, mensaje);
            
        }catch(MessagingException ex){
            ex.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar el abono. Contacte al administrador","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public void limpiarCampos() {
        jTextField_cliente.setText("");
        jTextField_idAbono.setText("");
        jLabel_abono.setText("");
        jLabel_fecha.setText("");
        jLabel_idventa.setText("");
        jLabel_observaciones.setText("");
        jLabel_monto.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_abonos = new javax.swing.JTable();
        jLabel_abono = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTextField_idAbono = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jButton_generarRecibo = new javax.swing.JButton();
        jButton_editar = new javax.swing.JButton();
        jButton_eliminar = new javax.swing.JButton();
        jLabel_observaciones = new javax.swing.JLabel();
        jLabel_fecha = new javax.swing.JLabel();
        jLabel_idventa = new javax.swing.JLabel();
        jLabel_monto = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_abonos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Abono", "No. Venta", "Fecha abono", "Cliente", "Valor abono", "Observaciones", "Registrado por"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_abonos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_abonos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_abonosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_abonos);
        if (jTable_abonos.getColumnModel().getColumnCount() > 0) {
            jTable_abonos.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable_abonos.getColumnModel().getColumn(1).setPreferredWidth(70);
            jTable_abonos.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable_abonos.getColumnModel().getColumn(3).setPreferredWidth(200);
            jTable_abonos.getColumnModel().getColumn(4).setPreferredWidth(120);
            jTable_abonos.getColumnModel().getColumn(5).setPreferredWidth(200);
            jTable_abonos.getColumnModel().getColumn(6).setPreferredWidth(120);
        }

        jLabel_abono.setText("jLabel1");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Abono a imprimir", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("No. Abono");

        jLabel2.setText("Cliente");

        jButton_generarRecibo.setText("Generar Recibo");
        jButton_generarRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generarReciboActionPerformed(evt);
            }
        });

        jButton_editar.setText("Editar");
        jButton_editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_editarActionPerformed(evt);
            }
        });

        jButton_eliminar.setText("Eliminar");
        jButton_eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_eliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_idAbono, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_generarRecibo)
                .addGap(18, 18, 18)
                .addComponent(jButton_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_idAbono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_generarRecibo)
                    .addComponent(jButton_editar)
                    .addComponent(jButton_eliminar))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jLabel_observaciones.setText("jLabel3");

        jLabel_fecha.setText("jLabel3");

        jLabel_idventa.setText("jLabel3");

        jLabel_monto.setText("jLabel3");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel_abono)
                        .addGap(108, 108, 108)
                        .addComponent(jLabel_observaciones)
                        .addGap(48, 48, 48)
                        .addComponent(jLabel_fecha)
                        .addGap(62, 62, 62)
                        .addComponent(jLabel_idventa)
                        .addGap(125, 125, 125)
                        .addComponent(jLabel_monto))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 879, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_abono)
                    .addComponent(jLabel_observaciones)
                    .addComponent(jLabel_fecha)
                    .addComponent(jLabel_idventa)
                    .addComponent(jLabel_monto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_abonosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_abonosMouseClicked

        int fila = jTable_abonos.getSelectedRow();
        //Verificamos que se haya seleccionado un abono
        if (fila != -1) {
            //Capturamos los datos para hacer la consulta de la informacion del abono en la base de datos
            jTextField_idAbono.setText(jTable_abonos.getValueAt(fila, 0).toString().trim());
            jLabel_abono.setText(jTable_abonos.getValueAt(fila, 4).toString().trim());
            jTextField_cliente.setText(jTable_abonos.getValueAt(fila, 3).toString().trim());
            jLabel_observaciones.setText(jTable_abonos.getValueAt(fila, 5).toString());
            jLabel_fecha.setText(jTable_abonos.getValueAt(fila, 2).toString());
            jLabel_idventa.setText(jTable_abonos.getValueAt(fila, 1).toString());
            jLabel_monto.setText(jTable_abonos.getValueAt(fila, 4).toString());
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona una fila valida");
        }

    }//GEN-LAST:event_jTable_abonosMouseClicked

    private void jButton_generarReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generarReciboActionPerformed
        //Verificamos que se haya seleccionado un abono para imprimir
        String idAbono = jTextField_idAbono.getText().trim();
        if (!idAbono.equals("")) {
            //Una vez verificado, capturamos el idventa
            String idVenta = jLabel_idventa.getText().trim();
            Object[] datosRecibo = LeerDatosAbono(idAbono, idVenta);
            GenerarRecibo(datosRecibo);

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un abono para generar el recibo","Informacion",JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton_generarReciboActionPerformed

    private void jButton_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_editarActionPerformed
        // Verificamos que se haya seleccionado el abono
        String idAbono = jTextField_idAbono.getText().trim();
        String cliente = jTextField_cliente.getText().trim();
        Double abono = Math.abs(Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jLabel_abono.getText().trim())));
        String fecha = jLabel_fecha.getText().trim();
        String observaciones = jLabel_observaciones.getText().trim();
        String idVenta = jLabel_idventa.getText().trim();

        if (!idAbono.equals("")) {
            new EditarAbonoEntradasDiarias(usuario, permiso, cliente, idAbono, abono, observaciones, fecha, idVenta).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione el abono a editar","Informacion",JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton_editarActionPerformed

    private void jButton_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_eliminarActionPerformed
        String idAbono = jTextField_idAbono.getText().trim();
        String cliente = jTextField_cliente.getText().trim();
        
        if (!idAbono.equals("")) {
            int opcion = JOptionPane.showConfirmDialog(this, "¿Desea eliminar el abono seleccionado?","Confirmacion",JOptionPane.INFORMATION_MESSAGE);
            if (opcion == 0) {
                String observaciones = JOptionPane.showInputDialog(this, "Ingrese la razon por el cual se elimina el abono","Confirmacion",JOptionPane.ERROR_MESSAGE);

                if (!observaciones.equals("")) {
                    EliminarAbono(idAbono, observaciones, cliente);
                    limpiarTabla(modelo);
                    llenarTabla();
                    limpiarCampos();
                } else {
                    JOptionPane.showMessageDialog(this, "Es necesario indicar la razón del por que se elimina el abono","Informacion",JOptionPane.ERROR_MESSAGE);
                }

            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccion el abono a eliminar");
        }
    }//GEN-LAST:event_jButton_eliminarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ListadoAbonosEntradasDiarias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoAbonosEntradasDiarias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoAbonosEntradasDiarias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoAbonosEntradasDiarias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoAbonosEntradasDiarias().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_editar;
    private javax.swing.JButton jButton_eliminar;
    private javax.swing.JButton jButton_generarRecibo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel_abono;
    private javax.swing.JLabel jLabel_fecha;
    private javax.swing.JLabel jLabel_idventa;
    private javax.swing.JLabel jLabel_monto;
    private javax.swing.JLabel jLabel_observaciones;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_abonos;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_idAbono;
    // End of variables declaration//GEN-END:variables
}

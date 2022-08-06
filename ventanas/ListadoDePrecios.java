/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JTable;
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
public class ListadoDePrecios extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso;

    /**
     * Creates new form ListadoDePrecios
     */
    public ListadoDePrecios() {
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();
    }

    public ListadoDePrecios(String usuario, String permiso) {
        this.permiso = permiso;
        this.usuario = usuario;
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado de precios *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


    }

    public void IniciarCaracteristicasGenerales() {
        SetearModelo();
        LlenarTabla();
        InhabilitarCampos();
    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_listado.getRowCount(); i++) {
            this.modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void SetearModelo() {
        this.modelo = (DefaultTableModel) jTable_listado.getModel();
    }

    public void InhabilitarCampos() {
        jTextField_id.setEnabled(false);
        jTextField_descripcion.setEnabled(false);
        jLabel_fila.setVisible(false);
    }

    public void LimpiarCampos() {
        jTextField_descripcion.setText("");
        jTextField_id.setText("");
        jTextField_porcentaje.setText("");
        jTextField_precio.setText("");
    }

    public void LlenarTabla() {

        modelo = (DefaultTableModel) jTable_listado.getModel();

        String consulta = "select id, fecha, descripcion, cantidad, tipoTrabajo, tamano, precio, color, acabado, "
                + "papelOriginal, copia1, copia2, copia3, observaciones, registradoPor from listaprecios order by id desc";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String[] nuevo = new String[15];
                nuevo[0] = rs.getString("id");
                nuevo[1] = rs.getString("fecha");
                nuevo[2] = rs.getString("descripcion");
                nuevo[3] = rs.getString("cantidad");
                nuevo[4] = rs.getString("tipoTrabajo");
                nuevo[5] = rs.getString("tamano");
                nuevo[6] = MetodosGenerales.ConvertirIntAMoneda(rs.getDouble("precio"));
                nuevo[7] = rs.getString("color");
                nuevo[8] = rs.getString("acabado");
                nuevo[9] = rs.getString("papelOriginal");
                nuevo[10] = rs.getString("copia1");
                nuevo[11] = rs.getString("copia2");
                nuevo[12] = rs.getString("copia3");
                nuevo[13] = rs.getString("observaciones");
                nuevo[14] = rs.getString("registradoPor");

                modelo.addRow(nuevo);

            }

            jTable_listado.setModel(modelo);
            
            TableRowSorter<TableModel> ordenador = new TableRowSorter<>(modelo);
            jTable_listado.setRowSorter(ordenador);
            
            
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer la lista de precios");
        }
    }

    public void ActualizarPrecio(String id, String precio, String fecha, String usuario) {
        String consulta = "update listaprecios set precio=?, fecha=?, registradoPor=? where id=?";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, precio);
            pst.setString(2, fecha);
            pst.setString(3, usuario);
            pst.setString(4, id);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "Precio actualizado");

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el precio");
            e.printStackTrace();
        }
    }

    public void ActualizarPreciosMasivamente(JTable tabla, int aumento, String fecha, String usuario) {

        int numerofilas = jTable_listado.getRowCount();

        for (int i = 0; i < numerofilas; i++) {

            String id = jTable_listado.getValueAt(i, 0).toString();
            int valorActual = Integer.parseInt(MetodosGenerales.ConvertirMonedaAInt(jTable_listado.getValueAt(i, 6).toString()));
            int valorActualizo = (int) (valorActual * (1 + aumento / 100f));

            String consulta = "UPDATE listaprecios set precio=?, fecha=?, registradoPor=? WHERE id=?";

            Connection cn = Conexion.Conectar();
            try {
                PreparedStatement pst = cn.prepareStatement(consulta);
                pst.setInt(1, valorActualizo);
                pst.setString(2, fecha);
                pst.setString(3, usuario);
                pst.setString(4, id);

                pst.executeUpdate();

                cn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error en actualizar los precios masivamente");
                e.printStackTrace();
            }
        }

    }

    public void GenerarListaEnExcel(JTable tabla, String fecha, String usuario) {

        String rutaArchivoACopiar = "C:"+File.separator+"Users"+File.separator+"Erwin P"+File.separator+"Documents"+
                File.separator+"NetBeansProjects"+File.separator+"Gestion"+File.separator+"src"+File.separator+"Docs"
                +File.separator+"Lista de precios.xlsx";
        try {

            FileInputStream archivoATomar = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoATomar);
            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            XSSFRow fila3 = hoja.getRow(3);
            XSSFCell celda31 = fila3.getCell(1);
            celda31.setCellValue(fecha);

            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda41 = fila4.getCell(1);
            celda41.setCellValue(usuario);

            int filaInicial = 7;
            for (int i = 0; i < tabla.getRowCount(); i++) {
                XSSFRow filaInicioTabla = hoja.getRow(filaInicial);
                for (int j = 0; j < tabla.getColumnCount(); j++) {
                    XSSFCell celda = filaInicioTabla.getCell(j);
                    if (j == 0 || j == 3) {
                        celda.setCellValue(Double.parseDouble(tabla.getValueAt(i, j).toString()));
                    } else if (j == 1) {
                        celda.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(tabla.getValueAt(i, j).toString()));
                    } else if (j == 6) {
                        String numero = tabla.getValueAt(i, j).toString();
                        numero = MetodosGenerales.ConvertirMonedaAInt(numero);
                        celda.setCellValue(Double.parseDouble(numero));
                    } else {
                        celda.setCellValue(tabla.getValueAt(i, j).toString());
                    }

                }
                filaInicial++;
            }

            String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Listado de precios.xlsx";
            FileOutputStream ultimo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(ultimo);
            ultimo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error en generar la lista de precios en Excel");
            e.printStackTrace();
        } catch (ParseException ex) {
            Logger.getLogger(ListadoDePrecios.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ListadoDePrecios.class.getName()).log(Level.SEVERE, null, ex);
        }

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
        jTable_listado = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTextField_id = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField_descripcion = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jButton_actualizarUno = new javax.swing.JButton();
        jTextField_precio = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_porcentaje = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButton_actualizarMasivo = new javax.swing.JButton();
        jButton_generarReporte = new javax.swing.JButton();
        jButton_cargarVenta = new javax.swing.JButton();
        jLabel_fila = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_listado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Fecha de registro", "Descripcion", "Cant.", "Tipo trabajo", "Tamaño", "Precio", "Color", "Acabado", "Papel original", "Copia 1", "Copia 2", "Copia 3", "Observaciones", "Registra"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_listado.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_listado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_listadoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_listado);
        if (jTable_listado.getColumnModel().getColumnCount() > 0) {
            jTable_listado.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable_listado.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTable_listado.getColumnModel().getColumn(2).setPreferredWidth(180);
            jTable_listado.getColumnModel().getColumn(3).setPreferredWidth(50);
            jTable_listado.getColumnModel().getColumn(4).setPreferredWidth(110);
            jTable_listado.getColumnModel().getColumn(5).setPreferredWidth(70);
            jTable_listado.getColumnModel().getColumn(6).setPreferredWidth(90);
            jTable_listado.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTable_listado.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTable_listado.getColumnModel().getColumn(9).setPreferredWidth(150);
            jTable_listado.getColumnModel().getColumn(10).setPreferredWidth(150);
            jTable_listado.getColumnModel().getColumn(11).setPreferredWidth(150);
            jTable_listado.getColumnModel().getColumn(12).setPreferredWidth(150);
            jTable_listado.getColumnModel().getColumn(13).setPreferredWidth(200);
            jTable_listado.getColumnModel().getColumn(14).setPreferredWidth(150);
        }

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Actualizacion de precios (uno a uno)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel7.setText("Id");

        jLabel8.setText("Descripcion");

        jLabel9.setText("Precio");

        jButton_actualizarUno.setText("Actualizar");
        jButton_actualizarUno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_actualizarUnoActionPerformed(evt);
            }
        });

        jTextField_precio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_precioKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(jTextField_precio, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_actualizarUno, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jButton_actualizarUno)
                    .addComponent(jTextField_precio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Actualizacion de precios (masivo)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Porcentaje");

        jTextField_porcentaje.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_porcentajeKeyTyped(evt);
            }
        });

        jLabel2.setText("%");

        jButton_actualizarMasivo.setText("Actualizar");
        jButton_actualizarMasivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_actualizarMasivoActionPerformed(evt);
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
                .addComponent(jTextField_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jButton_actualizarMasivo, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jButton_actualizarMasivo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton_generarReporte.setText("Generar reporte");
        jButton_generarReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generarReporteActionPerformed(evt);
            }
        });

        jButton_cargarVenta.setText("Cargar venta");
        jButton_cargarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cargarVentaActionPerformed(evt);
            }
        });

        jLabel_fila.setText("jLabel3");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel_fila)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton_cargarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_generarReporte))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1049, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_cargarVenta)
                    .addComponent(jButton_generarReporte)
                    .addComponent(jLabel_fila))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_listadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_listadoMouseClicked
        int fila = jTable_listado.getSelectedRow();
        if (fila != -1) {
            jTextField_id.setText(jTable_listado.getValueAt(fila, 0).toString());
            jTextField_descripcion.setText(jTable_listado.getValueAt(fila, 2).toString());
            jLabel_fila.setText(String.valueOf(fila));
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un trabajo");
        }
    }//GEN-LAST:event_jTable_listadoMouseClicked

    private void jTextField_precioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_precioKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) || (c == '0' && jTextField_precio.getText().length() == 0)) {
            evt.consume();
        }
        if (jTextField_precio.getText().length() >= 11) {
            evt.consume();
        }

    }//GEN-LAST:event_jTextField_precioKeyTyped

    private void jTextField_porcentajeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_porcentajeKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
        if (Character.isDigit(c) && jTextField_porcentaje.getText().length() >= 2) {
            evt.consume();
        }

        if (c == '0' && jTextField_porcentaje.getText().length() == 0) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_porcentajeKeyTyped

    private void jButton_actualizarUnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_actualizarUnoActionPerformed
        //Se verifica que se haya seleccionado el elemento
        String id = jTextField_id.getText().trim();
        if (!id.equals("")) {
            //Verificamos que se haya completado el precio
            String precio = jTextField_precio.getText().trim();
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if (!precio.equals("")) {

                int opcion = JOptionPane.showConfirmDialog(this, "CONFIRMACION: ¿Desea actualizar el precio?");
                if (opcion == 0) {
                    ActualizarPrecio(id, precio, fecha, this.usuario);
                    LimpiarCampos();
                    limpiarTabla(modelo);
                    LlenarTabla();
                }

            } else {
                JOptionPane.showMessageDialog(this, "Complete el nuevo precio");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un trabajo");
        }
    }//GEN-LAST:event_jButton_actualizarUnoActionPerformed

    private void jButton_actualizarMasivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_actualizarMasivoActionPerformed
        //Verificamos que se haya ingresado el porcentaje
        String porcentaje = jTextField_porcentaje.getText().trim();
        if (!porcentaje.equals("")) {
            int opcion = JOptionPane.showConfirmDialog(this, "CONFIRMACION: ¿Desea actualizar todos los precios en "
                    + porcentaje + "%?");
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if (opcion == 0) {
                ActualizarPreciosMasivamente(this.jTable_listado, Integer.parseInt(porcentaje), fecha, this.usuario);
                limpiarTabla(modelo);
                LlenarTabla();
                LimpiarCampos();
                JOptionPane.showMessageDialog(this, "Listado de precios actualizado");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Ingresa el valor del porcentaje");
        }


    }//GEN-LAST:event_jButton_actualizarMasivoActionPerformed

    private void jButton_generarReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generarReporteActionPerformed
        //Verficamos que haya datos enla tabla para listar
        int filas = jTable_listado.getRowCount();
        if (filas != 0) {
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            GenerarListaEnExcel(jTable_listado, fecha, this.usuario);
        } else {
            JOptionPane.showMessageDialog(this, "No hay datos para generar el informe");

        }
    }//GEN-LAST:event_jButton_generarReporteActionPerformed

    private void jButton_cargarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cargarVentaActionPerformed
        //Verificamos que se haya seleccionado un pedido

        String idPedido = jTextField_id.getText().trim();
        if (!idPedido.equals("")) {
            new SeleccionClienteParaCargarVentaDeLista(usuario, permiso, idPedido).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido");
        }


    }//GEN-LAST:event_jButton_cargarVentaActionPerformed

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
            java.util.logging.Logger.getLogger(ListadoDePrecios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoDePrecios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoDePrecios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoDePrecios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoDePrecios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_actualizarMasivo;
    private javax.swing.JButton jButton_actualizarUno;
    private javax.swing.JButton jButton_cargarVenta;
    private javax.swing.JButton jButton_generarReporte;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_fila;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_listado;
    private javax.swing.JTextField jTextField_descripcion;
    private javax.swing.JTextField jTextField_id;
    private javax.swing.JTextField jTextField_porcentaje;
    private javax.swing.JTextField jTextField_precio;
    // End of variables declaration//GEN-END:variables
}

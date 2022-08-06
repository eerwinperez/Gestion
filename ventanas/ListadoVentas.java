/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import javax.mail.MessagingException;
import javax.swing.ImageIcon;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
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
public class ListadoVentas extends javax.swing.JFrame {

    TableRowSorter trs;
    DefaultTableModel modelo;
    String usuario, permiso;

    /**
     * Creates new form RegistroVentas
     */
    public ListadoVentas() {
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

    }

    public ListadoVentas(String usuario, String permiso) {
        initComponents();
        this.usuario = usuario;
        this.permiso = permiso;
        if (permiso.equals("Asistente")) {
            jButton_eliminar.setEnabled(false);
        }
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado historico de ventas *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void IniciarCaracteristicasGenerales() {
        InhabilitarCampos();
        SettearModelo();
        LlenarTabla();
    }

    public void SettearModelo() {
        modelo = (DefaultTableModel) jTable_tablapedisos.getModel();
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(SwingConstants.RIGHT);
        jTable_tablapedisos.getColumnModel().getColumn(6).setCellRenderer(tcr);
    }

    public void LlenarTabla() {

        try {
            String consulta = "select ventas.Idventa, ventas.FechaventaSistema, ventas.tipoVenta, clientes.nombreCliente, ventas.descripcionTrabajo, "
                    + "ventas.Cantidad, ventas.tipoTrabajo, ventas.precio, ventas.tamaño, ventas.colorTinta, "
                    + "ventas.numeracionInicial, ventas.numeracionFinal, ventas.clasificacion, ventas.acabado, ventas.papelOriginal, "
                    + "ventas.copia1, ventas.estado, ventas.copia2, ventas.copia3, ventas.observaciones, ventas.fechaEntrega, ventas.motivoNoAbono "
                    + " from ventas INNER JOIN clientes where ventas.Idcliente=clientes.idCliente order by ventas.Idventa desc";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable_tablapedisos.getModel();

            while (rs.next()) {

                Object[] listado = new Object[11];

                listado[0] = rs.getString("ventas.Idventa");
                listado[1] = rs.getString("ventas.FechaventaSistema");
                listado[2] = rs.getString("clientes.nombreCliente");
                listado[3] = rs.getString("ventas.tipoVenta");
                listado[4] = rs.getString("ventas.clasificacion");
                listado[5] = rs.getString("ventas.descripcionTrabajo") + " - " + rs.getString("ventas.tamaño") + " - " + rs.getString("ventas.colorTinta");
                listado[6] = rs.getString("ventas.Cantidad");
                listado[7] = MetodosGenerales.ConvertirIntAMoneda(rs.getDouble("ventas.precio"));
                listado[8] = rs.getString("ventas.observaciones");
                listado[9] = rs.getString("ventas.motivoNoAbono");
                listado[10] = rs.getString("ventas.estado");

                modelo.addRow(listado);
            }

            jTable_tablapedisos.setModel(modelo);

            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable_tablapedisos.setRowSorter(ordenador);
            cn.close();
            
            

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(this, "Error en llenar la tabla de pedidos. Contante al administrador "
                    + "ListadoVentas LlenarTabla()","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public void MostrarSeleccion(String seleccion) {

        String Todos = "select ventas.Idventa, ventas.FechaventaSistema, ventas.tipoVenta, clientes.nombreCliente, ventas.descripcionTrabajo, "
                + "ventas.Cantidad, ventas.tipoTrabajo, ventas.precio, ventas.tamaño, ventas.colorTinta, "
                + "ventas.numeracionInicial, ventas.numeracionFinal, ventas.acabado, ventas.estado, ventas.papelOriginal, "
                + "ventas.copia1, ventas.copia2, ventas.copia3, ventas.clasificacion, ventas.observaciones, ventas.fechaEntrega "
                + " from ventas INNER JOIN clientes ON ventas.Idcliente=clientes.idCliente order by ventas.Idventa desc";

        String EntradasDiarias = "select ventas.Idventa, ventas.FechaventaSistema, ventas.tipoVenta, clientes.nombreCliente, ventas.descripcionTrabajo, "
                + "ventas.Cantidad, ventas.tipoTrabajo, ventas.precio, ventas.tamaño, ventas.colorTinta, "
                + "ventas.numeracionInicial, ventas.numeracionFinal, ventas.estado, ventas.clasificacion, ventas.acabado, ventas.papelOriginal, "
                + "ventas.copia1, ventas.copia2, ventas.copia3, ventas.observaciones, ventas.fechaEntrega "
                + " from ventas INNER JOIN clientes on ventas.Idcliente=clientes.idCliente where ventas.tipoVenta='Entradas diarias' "
                + "order by ventas.Idventa desc";

        String Empresas = "select ventas.Idventa, ventas.FechaventaSistema, ventas.tipoVenta, clientes.nombreCliente, ventas.descripcionTrabajo, "
                + "ventas.Cantidad, ventas.tipoTrabajo, ventas.precio, ventas.tamaño, ventas.colorTinta, "
                + "ventas.numeracionInicial, ventas.numeracionFinal, ventas.estado, ventas.clasificacion, ventas.acabado, ventas.papelOriginal, "
                + "ventas.copia1, ventas.copia2, ventas.copia3, ventas.observaciones, ventas.fechaEntrega "
                + " from ventas INNER JOIN clientes on ventas.Idcliente=clientes.idCliente where ventas.tipoVenta='Empresas' "
                + "order by ventas.Idventa desc";

        String consulta = "";
        if (seleccion.equalsIgnoreCase("Todos")) {
            consulta = Todos;
        } else if (seleccion.equalsIgnoreCase("Entradas diarias")) {
            consulta = EntradasDiarias;
        } else if (seleccion.equalsIgnoreCase("Empresas")) {
            consulta = Empresas;
        }

        LimpiarTabla();
        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable_tablapedisos.getModel();

            while (rs.next()) {

                Object[] listado = new Object[10];

                listado[0] = rs.getString("ventas.Idventa");
                listado[1] = rs.getString("ventas.FechaventaSistema");
                listado[2] = rs.getString("clientes.nombreCliente");
                listado[3] = rs.getString("ventas.tipoVenta");
                listado[4] = rs.getString("ventas.clasificacion");
                listado[5] = rs.getString("ventas.descripcionTrabajo") + " - " + rs.getString("ventas.tamaño") + " - " + rs.getString("ventas.colorTinta");
                listado[6] = rs.getString("ventas.Cantidad");
                listado[7] = MetodosGenerales.ConvertirIntAMoneda(rs.getDouble("ventas.precio"));
                listado[8] = rs.getString("ventas.observaciones");
                listado[9] = rs.getString("ventas.estado");

                modelo.addRow(listado);
            }

            jTable_tablapedisos.setModel(modelo);

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en llenar la tabla de pedidos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public void Eliminar() {
        int fila = jTable_tablapedisos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona una fila");
        } else {
            modelo.removeRow(fila);
        }
    }

    public void LimpiarTabla() {

        for (int i = 0; i < jTable_tablapedisos.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }

    }

    public void InhabilitarCampos() {
        jTextField_cliente.setEnabled(false);
        jTextField_descripcion.setEnabled(false);
        jTextField_idVenta.setEnabled(false);
        jLabel_fila.setVisible(false);
        jLabel_monto.setVisible(false);
    }

    public static String ConvertirIntAMoneda(double dato) {
        String result = "";
        DecimalFormat objDF = new DecimalFormat("$ ###, ###");
        result = objDF.format(dato);

        return result;
    }

    public static String ConvertirMonedaAInt(String numero) {
        String MonedaParseada = "";

        try {
            MonedaParseada = new DecimalFormat("$ ###, ###").parse(numero).toString();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return MonedaParseada;
    }

    public boolean consultarSiAbono(String idVenta) {

        String consulta = "select * from abonos where idVenta=?";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idVenta);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return false;
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar la venta");
            e.printStackTrace();
        }

        return true;
    }

    public boolean consultarSiElementoRemision(String idVenta) {

        String consulta = "SELECT * FROM elementosremision where idVenta=? and estado='Activo'";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idVenta);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return false;
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar los remitos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return true;
    }

    public void eliminarVenta(String idVenta, String cliente, String monto, String observaciones) {
        String consulta = "update ventas set estado='Inactivo' where Idventa=?";
        //String consulta = "delete from ventas where Idventa=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idVenta);
            pst.execute();
            JOptionPane.showMessageDialog(this, "La venta " + idVenta + " ha sido eliminada");

            String asunto = "Venta " + idVenta + " Eliminada - " + cliente;
            String mensaje = "La venta No. " + idVenta + " ha sido Eliminada"
                    + "\nValor venta = " + monto
                    + "\nUsuario responsable: " + this.usuario
                    + "\nObservaciones: " + observaciones;

            MetodosGenerales.enviarEmail(asunto, mensaje);
            MetodosGenerales.registrarHistorial(usuario, mensaje);

        } catch (MessagingException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar la venta","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public Object[] LeerDatosAbono(String idVenta) {

        Object datosRecibo[] = new Object[10];

        String consulta = "select v.FechaventaSistema, c.nombreCliente, c.identificacion, "
                + "c.telefono, v.descripcionTrabajo, v.Cantidad, v.tamaño, v.precio, v.Idventa, "
                + "v.registradoPor\n"
                + "from ventas v join clientes c on v.idCliente=c.idCliente\n"
                + "where v.Idventa=?";

        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, idVenta);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                datosRecibo[0] = rs.getDate("v.FechaventaSistema");
                datosRecibo[1] = rs.getDouble("v.Idventa");
                datosRecibo[2] = rs.getString("c.nombreCliente");
                datosRecibo[3] = rs.getString("c.identificacion");
                datosRecibo[4] = rs.getString("c.telefono");
                datosRecibo[5] = rs.getString("v.descripcionTrabajo");
                datosRecibo[6] = rs.getDouble("v.Cantidad");
                datosRecibo[7] = rs.getString("v.tamaño");
                datosRecibo[8] = rs.getDouble("v.precio");
                datosRecibo[9] = rs.getString("v.registradoPor");

            }
            cn.close();
            pst.close();


        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en leer los datos del abono ImprimirRecibo LeerDatosAbono()");
            e.printStackTrace();
        }

        return datosRecibo;
    }

    public void GenerarRecibo(Object[] datosRecibo) {

        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Comprobante de venta.xlsx";

        String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Comprobante venta " + Math.round((Double)datosRecibo[1]) + " Cliente " + datosRecibo[2] + ".xlsx";

        try {

            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);

            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            //Datos de la primera fila
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda15 = fila1.getCell(5);
            celda15.setCellValue((Date) datosRecibo[0]);
            XSSFCell celda115 = fila1.getCell(15);
            celda115.setCellValue((Double) datosRecibo[1]);

            //Datos segunda fila
            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda25 = fila2.getCell(5);
            celda25.setCellValue((String) datosRecibo[2]);
            XSSFCell celda210 = fila2.getCell(10);
            celda210.setCellValue((String) datosRecibo[3]);
            XSSFCell celda215 = fila2.getCell(15);
            celda215.setCellValue((String) datosRecibo[4]);

            //Datos tercera fila
            XSSFRow fila3 = hoja.getRow(3);
            XSSFCell celda35 = fila3.getCell(5);
            celda35.setCellValue((String) datosRecibo[5]);
            XSSFCell celda312 = fila3.getCell(12);
            celda312.setCellValue((Double) datosRecibo[6]);
            XSSFCell celda315 = fila3.getCell(15);
            celda315.setCellValue((String) datosRecibo[7]);

            //Datos cuarta fila
//            XSSFRow fila4 = hoja.getRow(4);
//            XSSFCell celda45 = fila4.getCell(5);
//            celda45.setCellValue((Double) datosRecibo[8]);
//            XSSFCell celda49 = fila4.getCell(9);
//            celda49.setCellValue((Double) datosRecibo[9]);
            //Datos quinta fila
            XSSFRow fila5 = hoja.getRow(5);
            XSSFCell celda55 = fila5.getCell(5);
            celda55.setCellValue((Double) datosRecibo[8]);
            XSSFCell celda58 = fila5.getCell(8);
            celda58.setCellValue((Double) datosRecibo[8]);
//            XSSFCell celda514 = fila5.getCell(14);
//            celda514.setCellValue((Double) datosRecibo[13]);

            //Datos octava fila
            XSSFRow fila8 = hoja.getRow(7);
            XSSFCell celda87 = fila8.getCell(5);
            celda87.setCellValue((String) datosRecibo[9]);

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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_tablapedisos = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jTextField_filtroCliente = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_idVenta = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_descripcion = new javax.swing.JTextField();
        jButton_verdetalle = new javax.swing.JButton();
        jButton_cargarVenta = new javax.swing.JButton();
        jButton_eliminar = new javax.swing.JButton();
        jLabel_fila = new javax.swing.JLabel();
        jLabel_monto = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_tablapedisos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id venta", "Fecha", "Cliente", "Tipo Venta", "Clasif", "Descripcion", "Cant.", "Precio", "Observaciones", "Obs No Abono", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_tablapedisos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_tablapedisos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_tablapedisosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_tablapedisos);
        if (jTable_tablapedisos.getColumnModel().getColumnCount() > 0) {
            jTable_tablapedisos.getColumnModel().getColumn(0).setPreferredWidth(40);
            jTable_tablapedisos.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTable_tablapedisos.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTable_tablapedisos.getColumnModel().getColumn(3).setPreferredWidth(90);
            jTable_tablapedisos.getColumnModel().getColumn(4).setPreferredWidth(90);
            jTable_tablapedisos.getColumnModel().getColumn(5).setPreferredWidth(200);
            jTable_tablapedisos.getColumnModel().getColumn(6).setPreferredWidth(60);
            jTable_tablapedisos.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTable_tablapedisos.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTable_tablapedisos.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTable_tablapedisos.getColumnModel().getColumn(10).setPreferredWidth(60);
        }

        jLabel4.setText("Filtrar Cliente");

        jTextField_filtroCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_filtroClienteKeyTyped(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Venta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel2.setText("Id Venta");

        jLabel1.setText("Cliente");

        jLabel3.setText("Descripcion");

        jButton_verdetalle.setText("Ver detalle");
        jButton_verdetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_verdetalleActionPerformed(evt);
            }
        });

        jButton_cargarVenta.setText("Cargar venta");
        jButton_cargarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cargarVentaActionPerformed(evt);
            }
        });

        jButton_eliminar.setText("Eliminar");
        jButton_eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_eliminarActionPerformed(evt);
            }
        });

        jLabel_fila.setText("jLabel6");

        jLabel_monto.setText("jLabel6");

        jButton1.setText("Comprobante");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField_idVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(73, 73, 73)
                        .addComponent(jLabel_fila))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 597, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(jLabel_monto)
                        .addContainerGap(309, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton_verdetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_cargarVenta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_idVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_fila)
                    .addComponent(jLabel_monto))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_verdetalle)
                    .addComponent(jButton_cargarVenta)
                    .addComponent(jButton_eliminar)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.setText("Tipo Venta");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS", "ENTRADAS DIARIAS", "EMPRESAS" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_filtroCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_filtroCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_tablapedisosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_tablapedisosMouseClicked

        int fila = jTable_tablapedisos.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila para editar el pedido");
        } else {

            jTextField_cliente.setText(jTable_tablapedisos.getValueAt(fila, 2).toString());
            jTextField_descripcion.setText(jTable_tablapedisos.getValueAt(fila, 5).toString());
            jTextField_idVenta.setText(jTable_tablapedisos.getValueAt(fila, 0).toString());
            jLabel_fila.setText(jTable_tablapedisos.getValueAt(fila, 10).toString());
            jLabel_monto.setText(jTable_tablapedisos.getValueAt(fila, 7).toString());

        }


    }//GEN-LAST:event_jTable_tablapedisosMouseClicked

    private void jButton_verdetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_verdetalleActionPerformed
        //Verificamos que se haya seleccionado un pedido               
        String idVenta = jTextField_idVenta.getText().trim();
        if (!idVenta.equals("")) {
            new DetallePedido(usuario, permiso, idVenta).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una de las ventas","Informacion",JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton_verdetalleActionPerformed

    private void jButton_cargarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cargarVentaActionPerformed
        //Verificamos que se haya seleccionado un pedido               
        String idVenta = jTextField_idVenta.getText().trim();
        String cliente = jTextField_cliente.getText().trim();
        if (!idVenta.equals("")) {
            new RegistroVentas(usuario, permiso, cliente, idVenta).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una de las ventas","Informacion",JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton_cargarVentaActionPerformed

    private void jTextField_filtroClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_filtroClienteKeyTyped
        jTextField_filtroCliente.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                trs.setRowFilter(RowFilter.regexFilter("(?i)" + jTextField_filtroCliente.getText(), 2));
            }

        });

        trs = new TableRowSorter(jTable_tablapedisos.getModel());
        jTable_tablapedisos.setRowSorter(trs);
    }//GEN-LAST:event_jTextField_filtroClienteKeyTyped

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        String seleccion = (String) evt.getItem();
        MostrarSeleccion(seleccion);
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jButton_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_eliminarActionPerformed
        String idVenta = jTextField_idVenta.getText().trim();
        String estado = jLabel_fila.getText().trim();
        String cliente = jTextField_cliente.getText().trim();
        String monto = jLabel_monto.getText().trim();

        if (estado.equals("Activo")) {
            if (!idVenta.equals("")) {

                boolean verificarAbonos = consultarSiAbono(idVenta);
                if (verificarAbonos) {
                    boolean verificarElementosRemision = consultarSiElementoRemision(idVenta);
                    if (verificarElementosRemision) {
                        int opcion = JOptionPane.showConfirmDialog(this, "¿Desea eliminar la venta " + idVenta + "?","Confirmacion",JOptionPane.INFORMATION_MESSAGE);

                        if (opcion == 0) {

                            String motivo = JOptionPane.showInputDialog(this, "Indique la razón por la que desea eliminar esta compra","Informacion",JOptionPane.INFORMATION_MESSAGE);

                            if (!motivo.equals("")) {
                                eliminarVenta(idVenta, cliente, monto, motivo);
                                dispose();
                                new ListadoVentas(usuario, permiso).setVisible(true);
                            } else {
                                JOptionPane.showMessageDialog(this, "Indique la razón por la que desea eliminar esta compra","Informacion",JOptionPane.INFORMATION_MESSAGE);
                            }

                        }

                    } else {
                        JOptionPane.showMessageDialog(this, "La venta que intenta eliminar tiene remisiones asociadas."
                                + " Para poder eliminar la venta, debe eliminar primero sus remisiones","Error",JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "La venta que intenta eliminar tiene abonos en entradas diarias asociado."
                            + " Para poder elimanar la venta, debe eliminar primero sus abonos","Error",JOptionPane.ERROR_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Seleccione una venta","Informacion",JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "La venta ya ha sido eliminada","Informacion",JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton_eliminarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        //Verificamos que se haya seleccionado un abono para imprimir
        String idVenta = jTextField_idVenta.getText().trim();
        String estado = jLabel_fila.getText().trim();
        if (!idVenta.equals("")) {
            if (!estado.equals("Inactivo")) {
                //Una vez verificado, capturamos el idventa
                Object[] datosRecibo = LeerDatosAbono(idVenta);
                GenerarRecibo(datosRecibo);
            } else {
                JOptionPane.showMessageDialog(this, "No es posible general un comprobante de una venta eliminada","Error",JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione la venta para generar el comprobante de ventas","Informacion",JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(ListadoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoVentas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_cargarVenta;
    private javax.swing.JButton jButton_eliminar;
    private javax.swing.JButton jButton_verdetalle;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel_fila;
    private javax.swing.JLabel jLabel_monto;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_tablapedisos;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_descripcion;
    private javax.swing.JTextField jTextField_filtroCliente;
    private javax.swing.JTextField jTextField_idVenta;
    // End of variables declaration//GEN-END:variables
}

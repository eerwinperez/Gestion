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
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.mail.MessagingException;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;
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
public class ListadoTotalFacturas extends javax.swing.JFrame {

    String usuario, permiso;
    DefaultTableModel modelo;

    /**
     * Creates new form ListadoTotalFacturas
     */
    public ListadoTotalFacturas() {
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

    }

    public ListadoTotalFacturas(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

        if (permiso.equals("Asistente")) {
            jButton_anular.setEnabled(false);
        }
    }

    public void IniciarCaracteristicasGenerales() {
        SettearModelo();
        inhabilitarcampos();
        llenarTabla();
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado de facturas *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void SettearModelo() {
        modelo = (DefaultTableModel) jTable_facturas.getModel();
    }

    public void inhabilitarcampos() {
        jTextField_cliente.setEnabled(false);
        jTextField_factura.setEnabled(false);
        jLabel_estado.setVisible(false);
        jLabel_monto.setVisible(false);

    }

    public void llenarTabla() {

        modelo = (DefaultTableModel) jTable_facturas.getModel();

//        String consulta = "select f.idFactura, f.fechaFactura, c.nombreCliente, sum(ef.cantidad*v.unitario) as total, f.condiciondePago\n"
//                + "from facturas f  join elementosfactura ef on f.idFactura=ef.factura and f.estado='Activo' and ef.estado='Activo'\n"
//                + "left join elementosremision er on ef.idElementoRemito=er.id and er.estado='Activo'\n"
//                + "left join ventas v on er.idVenta=v.Idventa\n"
//                + "left join clientes c on v.Idcliente=c.idCliente\n"
//                + "where v.idCliente=37\n"
//                + "group by f.idFactura";
//        String consulta = "select distinct f.idFactura, f.fechaFactura, c.nombreCliente, f.monto, f.condiciondePago, f.estado \n"
//                + "from facturas f join elementosfactura ef on f.idFactura=ef.factura \n"
//                + "join elementosremision er on ef.idElementoRemito=er.id\n"
//                + "join ventas v on er.idVenta=v.Idventa \n"
//                + "join clientes c on v.Idcliente=c.idCliente"
//                + " order by f.idFactura desc";
        String consulta = "select f.idFactura, f.fechaFactura, c.nombreCliente, f.monto, f.condiciondePago, f.estado\n"
                + "from facturas f join clientes c on f.idCliente=c.idCliente\n"
                + "order by f.idFactura desc";

        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] listado = new Object[6];

                listado[0] = rs.getString("f.idFactura");
                listado[1] = rs.getString("f.fechaFactura");
                listado[2] = rs.getString("c.nombreCliente");
                listado[3] = MetodosGenerales.ConvertirIntAMoneda(rs.getDouble("f.monto"));
                listado[4] = rs.getString("f.condiciondePago");
                listado[5] = (rs.getString("f.estado").equals("Activo")) ? "Activa" : "Anulada";

                modelo.addRow(listado);
            }

            jTable_facturas.setModel(modelo);

            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable_facturas.setRowSorter(ordenador);

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el listado de facturas. ListadoTotalFacturas llenarTabla()");
            e.printStackTrace();
        }

    }

    public String[] consultarDatosCliente(String idCliente) {

        String[] datosCliente = new String[5];

        String consulta = "select nombreCliente, identificacion, direccion, municipio, telefono, email from "
                + "clientes where idCliente=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idCliente);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                datosCliente[0] = rs.getString("nombreCliente");
                datosCliente[1] = rs.getString("identificacion");
                datosCliente[2] = rs.getString("direccion") + " - " + rs.getString("municipio");
                datosCliente[3] = rs.getString("telefono");
                datosCliente[4] = rs.getString("email");

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en leer los datos del cliente ListadoFacturas ConsultarDatosCliente()");
        }

        return datosCliente;
    }

    public ArrayList<String[]> ConsultarElementosFactura(String factura) {
        //Declaramos un ArraList para agregar las cadenas de String con la informacion de los elementos de la factura
        ArrayList<String[]> informacion = new ArrayList<>();

        String consulta = "select elementosfactura.idVenta, elementosfactura.cantidadFacturada, ventas.descripcionTrabajo, "
                + "elementosfactura.precioUnitario, elementosfactura.subtotal, elementosfactura.factura from "
                + "elementosfactura inner join ventas on elementosfactura.idVenta=ventas.Idventa inner join facturas "
                + "on elementosfactura.factura=facturas.idFactura where facturas.idFactura=?";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, factura);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                String[] detalle = new String[6];
                detalle[0] = rs.getString("elementosfactura.idVenta");
                detalle[1] = rs.getString("elementosfactura.cantidadFacturada");
                detalle[2] = rs.getString("ventas.descripcionTrabajo");
                detalle[3] = rs.getString("elementosfactura.precioUnitario");
                detalle[4] = rs.getString("elementosfactura.subtotal");

                informacion.add(detalle);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en leer los elementos de la factura. Contacte al administrador "
                    + "ListadoFacturas ConsultarElementosFactura()");
            e.printStackTrace();

        }

        return informacion;
    }

    public void ReimprimirFactura(String numeroFactura, String fecha, String idCliente) {
        //Definimos las ruta de donde se tomara la plantilla de la factra
        //String rutaArchivoACopiar = "D:" + File.separator + "Erwin" + File.separator + "ApacheNetbeans" + File.separator + "GraficasJireh_1" + File.separator + "Factura.xlsx";
        String rutaArchivoACopiar = "C:" + File.separator + "Users" + File.separator + "Erwin P" + File.separator + "Documents"
                + File.separator + "NetBeansProjects" + File.separator + "Gestion" + File.separator + "src" + File.separator + "Docs"
                + File.separator + "Factura.xlsx";
        //String rutaArchivoACopiar ="Factura.xlsx";
        try {
            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);

            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();
            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            String[] datos = consultarDatosCliente(idCliente);
            //Definimos la ruta donde se guardara la factura            
            String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Factura "
                    + "" + numeroFactura + " - " + datos[0] + ".xlsx";

            //Datos de la cuarta fila
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda42 = fila4.getCell(2);
            celda42.setCellValue(datos[0]);
            XSSFCell celda44 = fila4.getCell(4);
            celda44.setCellValue(Integer.parseInt(numeroFactura));

            //Datos de la quinta fila
            XSSFRow fila5 = hoja.getRow(5);
            XSSFCell celda52 = fila5.getCell(2);
            celda52.setCellValue(datos[1]);
            XSSFCell celda54 = fila5.getCell(4);
            try {
                celda54.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(fecha));
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(null, "Error en la lectura de la fecha ListadoFacturas imprimirFactura()");
            }

            //Datos de la sexta fila
            XSSFRow fila6 = hoja.getRow(6);
            XSSFCell celda62 = fila6.getCell(2);
            celda62.setCellValue(datos[2]);
            XSSFCell celda64 = fila6.getCell(4);
            //celda64.setCellValue(jTable_facturas.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 6).toString());

            //Datos de la septima fila
            XSSFRow fila7 = hoja.getRow(7);
            XSSFCell celda72 = fila7.getCell(2);
            celda72.setCellValue(datos[3]);

            //Datos de la octava fila
            XSSFRow fila8 = hoja.getRow(8);
            XSSFCell celda82 = fila8.getCell(2);
            celda82.setCellValue(datos[4]);

            //Traemos el detalle de los items facturados
            ArrayList<String[]> listado = ConsultarElementosFactura(numeroFactura);

            //Declaramos una variable para capturar el total de la factura
            int total = 0;
            //Declaramos la variable con la fila donde se inicia a agregar la informacion
            int filaInicio = 11;

            //Recorremos la informacion de cada item y lo agregamos a las celdas correspondientes
            for (String[] elemento : listado) {
                XSSFRow fila = hoja.getRow(filaInicio);
                for (int i = 0; i < elemento.length; i++) {

                    if (i == 4) {
                        total += Double.parseDouble(elemento[i]);
                    }

                    XSSFCell celda = fila.getCell(i);
                    if (i == 0 || i == 1 || i == 3 || i == 4) {
                        celda.setCellValue(Double.parseDouble(elemento[i]));
                    } else if (i == 2) {
                        celda.setCellValue(elemento[i]);
                    }
                }
                filaInicio++;
            }

            XSSFRow fila40 = hoja.getRow(40);
            XSSFCell celda404 = fila40.getCell(4);
            celda404.setCellValue(total);

            FileOutputStream ultimo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(ultimo);
            ultimo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error en generar la factura. Asegurse se no tener una factura abierta y vuelta a intentarlo. ImprimirRecibo GenerarRecibo()");
            e.printStackTrace();
        }

    }

    public Object[] consultarDatosCabecera(String numeroFactura) {

        String consulta = "select distinct c.nombreCliente, c.identificacion, c.direccion, c.telefono, c.email, f.fechaFactura, f.condiciondePago\n"
                + "from facturas f join ventas v on f.idCliente=v.Idcliente \n"
                + "join clientes c on v.Idcliente=c.idCliente\n"
                + "where f.idFactura=?";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, numeroFactura);

            ResultSet rs = pst.executeQuery();
            Object[] datosCabecera = new Object[8];

            if (rs.next()) {
                datosCabecera[0] = rs.getString("c.nombreCliente");
                datosCabecera[1] = rs.getString("c.identificacion");
                datosCabecera[2] = rs.getString("c.direccion");
                datosCabecera[3] = rs.getString("c.telefono");
                datosCabecera[4] = rs.getString("c.email");
                datosCabecera[5] = numeroFactura;
                datosCabecera[6] = rs.getDate("f.fechaFactura");
                datosCabecera[7] = rs.getString("f.condiciondePago");
            }
            cn.close();
            return datosCabecera;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al leer los datos de cabecera", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Object[]> consultarElementosFactura(String numeroFactura) {
        String consulta = "select er.idVenta, er.id, ef.cantidad, v.colorTinta, v.descripcionTrabajo, v.papelOriginal, v.tamaño, v.unitario, ef.cantidad*v.unitario as total \n"
                + "from elementosfactura ef join elementosremision er on ef.idElementoRemito=er.id \n"
                + "join ventas v on er.idVenta=v.Idventa\n"
                + "where ef.factura=?";

        ArrayList<Object[]> listado = new ArrayList<>();
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, numeroFactura);

            ResultSet rs = pst.executeQuery();

//            listado[5] = rs.getString("ventas.descripcionTrabajo") + " - " + rs.getString("ventas.tamaño") + " - " + rs.getString("ventas.colorTinta")
//                    + " - " + rs.getString("ventas.papelOriginal");

            while (rs.next()) {
                Object[] elementos = new Object[6];

                String papel = (rs.getString("v.papelOriginal").equalsIgnoreCase("No aplica")) ? "" :" - " +rs.getString("v.papelOriginal");
                String tamaño = (rs.getString("v.tamaño").equalsIgnoreCase("No aplica")) ? "" :" - " +rs.getString("v.tamaño");
                String color = (rs.getString("v.colorTinta").equalsIgnoreCase("No aplica")) ? "" : " - "+rs.getString("v.colorTinta");
                
                elementos[0] = rs.getDouble("er.idVenta");
                elementos[1] = rs.getDouble("er.id");
                elementos[2] = rs.getDouble("ef.cantidad");
                elementos[3] = rs.getString("v.descripcionTrabajo")+tamaño+
                        color+papel;
                elementos[4] = rs.getDouble("v.unitario");
                elementos[5] = rs.getDouble("total");

                listado.add(elementos);
            }
            cn.close();
            return listado;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los elementos de la factura", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public void imprimirFactura(String numeroFactura, Object[] datosCabecera, ArrayList<Object[]> elementosFactura) {

        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Factura.xlsx";

        try {
            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);

            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();
            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            //Definimos la ruta donde se guardara la factura            
            String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Factura "
                    + "" + numeroFactura + " - " + datosCabecera[0] + ".xlsx";

            //Datos de la cuarta fila
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda42 = fila4.getCell(3);
            celda42.setCellValue((String) datosCabecera[0]);
            XSSFCell celda44 = fila4.getCell(5);
            celda44.setCellValue((String) datosCabecera[5]);

            //Datos de la quinta fila
            XSSFRow fila5 = hoja.getRow(5);
            XSSFCell celda52 = fila5.getCell(3);
            celda52.setCellValue((String) datosCabecera[1]);
            XSSFCell celda54 = fila5.getCell(5);
            celda54.setCellValue((Date) datosCabecera[6]);

            //Datos de la sexta fila
            XSSFRow fila6 = hoja.getRow(6);
            XSSFCell celda62 = fila6.getCell(3);
            celda62.setCellValue((String) datosCabecera[2]);
            XSSFCell celda64 = fila6.getCell(5);
            celda64.setCellValue((String) datosCabecera[7]);

            //Datos de la septima fila
            XSSFRow fila7 = hoja.getRow(7);
            XSSFCell celda72 = fila7.getCell(3);
            celda72.setCellValue((String) datosCabecera[3]);

            //Datos de la octava fila
            XSSFRow fila8 = hoja.getRow(8);
            XSSFCell celda82 = fila8.getCell(3);
            celda82.setCellValue((String) datosCabecera[4]);

            //Traemos el detalle de los items facturados
//            ArrayList<String[]> listado = ConsultarElementosFactura(numeroFactura);
            //Declaramos una variable para capturar el total de la factura
            double total = 0;
            //Declaramos la variable con la fila donde se inicia a agregar la informacion
            int filaInicio = 11;

            //Recorremos la informacion de cada item y lo agregamos a las celdas correspondientes
            for (Object[] elemento : elementosFactura) {
                XSSFRow fila = hoja.getRow(filaInicio);
                for (int i = 0; i < elemento.length; i++) {

                    XSSFCell celda = fila.getCell(i);
                    switch (i) {
                        case 0:
                            celda.setCellValue((Double) elemento[2]);
                            break;
                        case 3:
                            celda.setCellValue((String) elemento[i]);
                            break;
                        case 4:
                            celda.setCellValue((Double) elemento[i]);
                            break;
                        case 5:
                            celda.setCellValue((Double) elemento[i]);
                            total += (Double) elemento[i];
                            break;

                    }

                }
                filaInicio++;
            }

            XSSFRow fila40 = hoja.getRow(38);
            XSSFCell celda404 = fila40.getCell(5);
            celda404.setCellValue(total);

            FileOutputStream ultimo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(ultimo);
            ultimo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error al generar la factura. Es posible que tenga una factura con el mismo nombre abierto", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error en generar la factura. Asegurse se no tener una factura abierta y vuelta a intentarlo. ImprimirRecibo GenerarRecibo()", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public boolean comprobarDocumentosAsociados(String factura) {

        String consulta = "select abono from abonosfacturas where factura = ?";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, factura);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                return false;
            }

            cn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los abonos de la factura");
        }

        return true;
    }

    public void anularFacturaYElementos(String factura, String cliente, String razon, String monto) {
        String consulta = "update facturas set estado='Inactivo' where idFactura=?";
        String consulta2 = "update elementosfactura set estado='Inactivo' where factura=?";
        Connection cn = Conexion.Conectar();

        try {

            cn.setAutoCommit(false);
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, factura);
            pst.executeUpdate();

            PreparedStatement pst2 = cn.prepareStatement(consulta2);
            pst2.setString(1, factura);
            pst2.executeUpdate();

            cn.commit();
            cn.close();
            JOptionPane.showMessageDialog(this, "Factura anulada", "Informacion", JOptionPane.INFORMATION_MESSAGE);

            String asunto = "Factura " + factura + " anulada - " + cliente;
            String mensaje = "Factura anulada"
                    + "\nCliente: " + cliente
                    + "\nValor factura: " + monto
                    + "\nUsuario responsable: " + this.usuario
                    + "\nObservaciones: " + razon;

            MetodosGenerales.enviarEmail(asunto, mensaje);
            MetodosGenerales.registrarHistorial(usuario, mensaje);

        } catch (MessagingException e) {
            e.printStackTrace();

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar la factura", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public void limpiarCampos() {
        jTextField_cliente.setText("");
        jTextField_factura.setText("");
        jLabel_monto.setText("");
        jLabel_estado.setText("");
    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_facturas.getRowCount(); i++) {
            this.modelo.removeRow(i);
            i = i - 1;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by th e Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_facturas = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_factura = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton_anular = new javax.swing.JButton();
        jLabel_estado = new javax.swing.JLabel();
        jLabel_monto = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_facturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Fact.", "Fecha", "Cliente", "Valor facturado", "Condicion de pago", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_facturas.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_facturas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_facturasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_facturas);
        if (jTable_facturas.getColumnModel().getColumnCount() > 0) {
            jTable_facturas.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable_facturas.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_facturas.getColumnModel().getColumn(2).setPreferredWidth(220);
            jTable_facturas.getColumnModel().getColumn(3).setPreferredWidth(120);
            jTable_facturas.getColumnModel().getColumn(4).setPreferredWidth(200);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Factura", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Factura");

        jLabel3.setText("Cliente");

        jTextField_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_clienteActionPerformed(evt);
            }
        });

        jButton1.setText("Imprimir");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton_anular.setText("Anular");
        jButton_anular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_anularActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jTextField_factura, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_anular, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_factura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jButton_anular))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jLabel_estado.setText("jLabel2");

        jLabel_monto.setText("jLabel2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 790, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel_estado)
                            .addComponent(jLabel_monto))))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel_estado)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel_monto)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_clienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_clienteActionPerformed

    private void jTable_facturasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_facturasMouseClicked

        int fila = jTable_facturas.getSelectedRow();
        if (fila != -1) {

            jTextField_cliente.setText(jTable_facturas.getValueAt(fila, 2).toString());
            jTextField_factura.setText(jTable_facturas.getValueAt(fila, 0).toString());
            jLabel_estado.setText(jTable_facturas.getValueAt(fila, 5).toString());
            jLabel_monto.setText(jTable_facturas.getValueAt(fila, 3).toString());

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila");
        }
    }//GEN-LAST:event_jTable_facturasMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //Verificamos que se haya seleccionado una factura
        String numeroFactura = jTextField_factura.getText().trim();
        if (!numeroFactura.equals("")) {
            Object[] datosCabecera = consultarDatosCabecera(numeroFactura);
            ArrayList<Object[]> elementosFactura = consultarElementosFactura(numeroFactura);
            imprimirFactura(numeroFactura, datosCabecera, elementosFactura);

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione la factura que desea reimprimir", "Informacion", JOptionPane.INFORMATION_MESSAGE);

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton_anularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_anularActionPerformed
        String factura = jTextField_factura.getText().trim();

        if (!factura.equals("")) {
            String estado = jLabel_estado.getText().trim();
            String cliente = jTextField_cliente.getText().trim();
            String monto = jLabel_monto.getText().trim();

            if (!estado.equals("Anulada")) {
                boolean comprobacion = comprobarDocumentosAsociados(factura);
                if (comprobacion) {
                    int opcion = JOptionPane.showConfirmDialog(this, "¿Desea anular la factura " + factura + "?", "Confirmacion", JOptionPane.INFORMATION_MESSAGE);

                    if (opcion == 0) {

                        String razon = JOptionPane.showInputDialog(this, "Indique el motivo por el que anulará la factura", "Informacion", JOptionPane.INFORMATION_MESSAGE);

                        if (!razon.equals("")) {
                            anularFacturaYElementos(factura, cliente, razon, monto);
                            limpiarCampos();
                            limpiarTabla(modelo);
                            llenarTabla();
                        } else {
                            JOptionPane.showMessageDialog(this, "Indique el motivo por el que anulará la factura", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                        }

                    }

                } else {
                    JOptionPane.showMessageDialog(this, "No es posible eliminar la factura por que esta tiene abonos asociados. Para eliminar la factura, debe eliminar primero los abonos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "La factura ya ha sido anulada", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una factura", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton_anularActionPerformed

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
            java.util.logging.Logger.getLogger(ListadoTotalFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoTotalFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoTotalFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoTotalFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoTotalFacturas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_anular;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel_estado;
    private javax.swing.JLabel jLabel_monto;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_facturas;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_factura;
    // End of variables declaration//GEN-END:variables
}

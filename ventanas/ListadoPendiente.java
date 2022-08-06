/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import clases.Ventas;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author erwin
 */
public class ListadoPendiente extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso;

    /**
     * Creates new form ListadoPendiente
     */
    public ListadoPendiente() {

        initComponents();
        IniciarCaracteristicasGenerales();

        ConfiguracionGralJFrame();

    }

    public ListadoPendiente(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;

        initComponents();
        IniciarCaracteristicasGenerales();

        ConfiguracionGralJFrame();

    }

    public void SettearModelo() {
        modelo = (DefaultTableModel) jTable_pendientes.getModel();
    }

    public void InhabilitarCampos() {
        jTextField_idVenta.setEnabled(false);
        jTextField_trabajo.setEnabled(false);
    }

    public void IniciarCaracteristicasGenerales() {
        InhabilitarCampos();
        SettearModelo();
        llenarTabla();
    }

    public void limpiarFormulario() {

        jTextField_idVenta.setText("");
        jTextField_trabajo.setText("");
        jDateChooser_fechaTerminacion.setDate(null);

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado de pedidos pendientes por facturar *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_pendientes.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void llenarTabla() {
        try {
            String consulta = "select ventas.Idventa, ventas.FechaventaSistema, ventas.fechaEntrega, "
                    + "clientes.nombreCliente, ventas.Cantidad, ventas.clasificacion, ventas.tipoVenta, ventas.descripcionTrabajo, ventas.tamaño, "
                    + "ventas.colorTinta, ventas.observaciones from clientes inner join ventas "
                    + "where clientes.idCliente=ventas.Idcliente and "
                    + "ventas.fechaTerminacion is NULL and ventas.estado='Activo' order by ventas.Idventa desc";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable_pendientes.getModel();

            while (rs.next()) {
                Object[] empleados = new Object[11];

                empleados[0] = rs.getString("ventas.Idventa");
                empleados[1] = rs.getString("ventas.FechaventaSistema");
                empleados[2] = rs.getString("ventas.fechaEntrega");
                empleados[3] = rs.getString("clientes.nombreCliente");
                empleados[4] = rs.getString("ventas.tipoVenta");
                empleados[5] = rs.getString("ventas.clasificacion");
                empleados[6] = rs.getString("ventas.Cantidad");
                empleados[7] = rs.getString("ventas.descripcionTrabajo");
                empleados[8] = rs.getString("ventas.tamaño");
                empleados[9] = rs.getString("ventas.colorTinta");
                empleados[10] = rs.getString("ventas.observaciones");

                modelo.addRow(empleados);
            }
            jTable_pendientes.setModel(modelo);
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos en la tabla pendientes desde la base de datos");
            e.printStackTrace();
        }
    }

    public void generarInformePendientesCopia() {

        String rutaArchivoACopiar = "D:/Erwin/ApacheNetbeans/GraficasJireh_1/Listado pendientes.xlsx";
        String rutaParaGuardar = "C:/Users/erwin/Desktop";

        ArrayList<Integer> arrayListBytes = MetodosGenerales.CopiarArchivo(rutaArchivoACopiar);
        String ubicacionNuevoArchivo = MetodosGenerales.crearListadoPendientes(arrayListBytes, rutaParaGuardar);

        //Leemos el archivo que vamos a modificar y creamos el XSSFWorkbook
        FileInputStream archivoAModificar;
        XSSFWorkbook nuevoLibro = null;
        XSSFSheet hoja = null;

        try {
            archivoAModificar = new FileInputStream(ubicacionNuevoArchivo);
            nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();
            hoja = nuevoLibro.getSheetAt(0);

            //Agregamos la informacion que es general, fecha y 
            XSSFRow filaFecha = hoja.getRow(0);
            XSSFCell celdaFecha = filaFecha.getCell(2);
            celdaFecha.setCellValue(new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(new Date()));

            XSSFRow filaUsuario = hoja.getRow(1);
            XSSFCell celdaUsuario = filaUsuario.getCell(2);
            celdaUsuario.setCellValue(this.usuario);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error en crear XSSFWorkbook ListadoPendientes generarInformePendientes()");
        }

        //Leer datos de la base de datos
        try {

            String consulta = "select ventas.Idventa, ventas.FechaventaSistema, ventas.fechaEntrega, "
                    + "clientes.nombreCliente, ventas.Cantidad, ventas.descripcionTrabajo, ventas.tamaño, "
                    + "ventas.colorTinta, ventas.observaciones from clientes inner join ventas "
                    + "where clientes.idCliente=ventas.Idcliente and ventas.estadoElaboracion='Pendiente' order by "
                    + "ventas.fechaEntrega asc";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            int i = 4;
            while (rs.next()) {

                String[] empleados = new String[9];

                empleados[0] = rs.getString("ventas.Idventa");
                empleados[1] = rs.getString("ventas.FechaventaSistema");
                empleados[2] = rs.getString("ventas.fechaEntrega");
                empleados[3] = rs.getString("clientes.nombreCliente");
                empleados[4] = rs.getString("ventas.Cantidad");
                empleados[5] = rs.getString("ventas.descripcionTrabajo");
                empleados[6] = rs.getString("ventas.tamaño");
                empleados[7] = rs.getString("ventas.colorTinta");
                empleados[8] = rs.getString("ventas.observaciones");

                XSSFRow fila = hoja.getRow(i);

                for (int j = 0; j < empleados.length; j++) {
                    XSSFCell celda = fila.getCell(j);
                    if (j == 0 || j == 4) {
                        celda.setCellValue(Double.parseDouble(empleados[j]));
                    } else if (j == 1 || j == 2) {
                        celda.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(empleados[j]));
                    } else if (j == 4) {

                    } else {
                        celda.setCellValue(empleados[j]);
                    }

                }
                i++;
            }

            FileOutputStream ultimo = new FileOutputStream(ubicacionNuevoArchivo);
            nuevoLibro.write(ultimo);
            ultimo.close();

            MetodosGenerales.abrirArchivo(ubicacionNuevoArchivo);
            cn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, " "
                    + "Error al generar el informe de pendientes. Asegurese de no haya un archivo generado previamente y este abierto. ListadoPendiente generarInformePendientes()");
        }

    }

    public void generarInformePendientes() {

        String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Pendientes.xlsx";
        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Listado pendientes.xlsx";

        FileInputStream archivoAModificar;
        XSSFWorkbook nuevoLibro = null;
        XSSFSheet hoja = null;

        try {
            archivoAModificar = new FileInputStream(rutaArchivoACopiar);
            nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();
            hoja = nuevoLibro.getSheetAt(0);

            //Agregamos la informacion que es general, fecha y 
            XSSFRow filaFecha = hoja.getRow(0);
            XSSFCell celdaFecha = filaFecha.getCell(2);
            celdaFecha.setCellValue(new Date());

            XSSFRow filaUsuario = hoja.getRow(1);
            XSSFCell celdaUsuario = filaUsuario.getCell(2);
            celdaUsuario.setCellValue(this.usuario);

            ArrayList<Object[]> listado = consultarPendientes();

            XSSFRow fila2 = hoja.getRow(2);
            
            
            int filaInicial = 4;

            for (Object[] lista : listado) {
                XSSFRow fila = hoja.getRow(filaInicial);
                for (int i = 0; i < lista.length; i++) {
                    XSSFCell celda = fila.getCell(i);  
                    
                    switch (i) {
                        case 0:
                            celda.setCellValue((Double) lista[i]);
                            break;
                        case 1:
                            celda.setCellValue((Date) lista[i]);
                            break;
                        case 2:
                            celda.setCellValue((Date) lista[i]);
                            break;
                        case 3:
                            celda.setCellValue((String) lista[i]);
                            break;
                        case 4:
                            celda.setCellValue((String) lista[i]);
                            break;
                        case 5:
                            celda.setCellValue((Double) lista[i]);
                            break;
                        case 6:
                            celda.setCellValue((String) lista[i]);
                            break;
                        case 7:
                            celda.setCellValue((String) lista[i]);
                            break;
                        case 8:
                            celda.setCellValue((String) lista[i]);
                            break;
                        case 9:
                            celda.setCellValue((String) lista[i]);
                            break;
                        case 10:
                            celda.setCellValue((String) lista[i]);
                            break;
                        case 11:
                            celda.setCellValue((String) lista[i]);
                            break;
                        case 12:
                            celda.setCellValue((String) lista[i]);
                            break;
                        case 13:
                            celda.setCellValue((String) lista[i]);
                            break;
                        case 14:
                            celda.setCellValue((String) lista[i]);
                            break;
                        case 15:
                            celda.setCellValue((String) lista[i]);
                            break;

                    }
                }
                filaInicial++;
            }

            FileOutputStream ultimo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(ultimo);
            ultimo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "\"Error al crear el documento. Un documento con el mismo nombre esta abierto.\"\n"
                    + "                    + \"\\nCierrelo e intentelo nuevamente\"");
            ex.printStackTrace();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error en crear el informe de pendientes");
        }

    }

    public ArrayList<Object[]> consultarPendientes() {

        ArrayList<Object[]> listado = new ArrayList<>();

        String consulta = "select v.Idventa, v.FechaventaSistema, v.fechaEntrega,\n"
                + "c.nombreCliente, v.Cantidad, v.descripcionTrabajo, v.tamaño,\n"
                + "v.colorTinta, v.acabado, v.numeracionInicial, v.numeracionFinal, v.papelOriginal,"
                + " v.copia1, v.copia2, v.copia3, v.observaciones from clientes c join ventas v\n"
                + "on c.idCliente=v.Idcliente and v.fechaTerminacion is NULL and v.estado='Activo'";

        Connection cn = Conexion.Conectar();
        try {

            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] empleados = new Object[16];

                empleados[0] = rs.getDouble("v.Idventa");
                empleados[1] = rs.getDate("v.FechaventaSistema");
                empleados[2] = rs.getDate("v.fechaEntrega");
                empleados[3] = rs.getString("c.nombreCliente");
                empleados[4] = rs.getString("v.descripcionTrabajo");
                empleados[5] = rs.getDouble("v.Cantidad");
                empleados[6] = rs.getString("v.tamaño");
                empleados[7] = rs.getString("v.colorTinta");
                empleados[8] = rs.getString("v.acabado");
                empleados[9] = rs.getString("v.numeracionInicial");
                empleados[10] = rs.getString("v.numeracionFinal");
                empleados[11] = rs.getString("v.papelOriginal");
                empleados[12] = rs.getString("v.copia1");
                empleados[13] = rs.getString("v.copia2");
                empleados[14] = rs.getString("v.copia3");
                empleados[15] = rs.getString("v.observaciones");

                listado.add(empleados);
            }

            cn.close();
            return listado;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer los datos de trabajos pendientes");
            e.printStackTrace();
        }

        return null;
    }

    public void CambiarEstado(String fecha) {

        //Capturamos los datos del formulario
        //String fechaTerminacion = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_fechaTerminacion.getDate());
        String idVenta = jTextField_idVenta.getText().trim();

        try {
            String consulta = "update ventas set fechaTerminacion=?, terminadoPor=? where idVenta=?";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, fecha);
            pst.setString(2, this.usuario);
            pst.setString(3, idVenta);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "Estado del pedido No. " + idVenta + " actualizado a Terminado", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en cambiar el estado de terminacion del pedido. Contacte al administrador "
                    + "ListadoPendiente CambiarEstado()", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public Object[] LeerDatosparaOT(String idVenta) {
        Object[] nuevo = new Object[17]; //Cambie todo lo que decia String[] por Object[]

        try {

            String consulta = "SELECT v.FechaventaSistema, c.nombreCliente, v.descripcionTrabajo,\n"
                    + "v.Cantidad, v.tamaño, v.colorTinta, v.numeracionInicial,\n"
                    + "v.numeracionFinal, v.acabado, v.papelOriginal, v.copia1, v.copia2,\n"
                    + "v.copia3, v.observaciones, v.registradoPor \n"
                    + "from clientes c join ventas v on c.idCliente=v.Idcliente\n"
                    + "where v.Idventa=?";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idVenta);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                nuevo[0] = new Date();
                nuevo[1] = rs.getDate("v.FechaventaSistema");
                nuevo[2] = idVenta;
                nuevo[3] = rs.getString("c.nombreCliente");
                nuevo[4] = rs.getString("v.descripcionTrabajo");
                nuevo[5] = rs.getDouble("v.Cantidad");
                nuevo[6] = rs.getString("v.tamaño");
                nuevo[7] = rs.getString("v.colorTinta");
                nuevo[8] = rs.getString("v.numeracionInicial");
                nuevo[9] = rs.getString("v.numeracionFinal");
                nuevo[10] = rs.getString("v.acabado");
                nuevo[11] = rs.getString("v.papelOriginal");
                nuevo[12] = rs.getString("v.copia1");
                nuevo[13] = rs.getString("v.copia2");
                nuevo[14] = rs.getString("v.copia3");
                nuevo[15] = rs.getString("v.observaciones");
                nuevo[16] = rs.getString("v.registradoPor");

                cn.close();
            } else {
                JOptionPane.showMessageDialog(null, "Error en leer los datos para consignar en la OT. Metodo LeerDatosparaOT() Clase Ventas");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los datos del trabajo");
            e.printStackTrace();
        }

        return nuevo;
    }

    public void GenerarOT(String idVenta) {

        //Definimos las rutas de donde se tomará el archivo y donde se guardara
        //String rutaArchivoACopiar = "D:" + File.separator + "Erwin" + File.separator + "ApacheNetbeans" + File.separator + "GraficasJireh_1" + File.separator + "OT.xlsx";
        //String rutaArchivoACopiar = "Docs" + File.separator + "OT.xlsx";
//        String rutaArchivoACopiar = "C:"+File.separator+"Users"+File.separator+"Erwin P"+File.separator+"Documents"+
//                File.separator+"NetBeansProjects"+File.separator+"Gestion"+File.separator+"src"+File.separator+"Docs"
//                +File.separator+"OT.xlsx";
        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "OT.xlsx";

        Object[] datosOT = LeerDatosparaOT(idVenta);
        String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "OT- Venta " + idVenta + " - Cliente " + datosOT[3] + ".xlsx";

        try {

            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);

            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();
            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

//            ArrayList<XSSFCell> listadoCeldas = new ArrayList<>();
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda12 = fila1.getCell(2);
            celda12.setCellValue((Date) datosOT[0]);
            XSSFCell celda16 = fila1.getCell(6);
            celda16.setCellValue((Date) datosOT[1]);
//            listadoCeldas.add(celda12);
//            listadoCeldas.add(celda16);

            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda22 = fila2.getCell(2);
            celda22.setCellValue((String) datosOT[2]);
            XSSFCell celda24 = fila2.getCell(4);
            celda24.setCellValue((String) datosOT[3]);
//            listadoCeldas.add(celda22);
//            listadoCeldas.add(celda24);

            XSSFRow fila5 = hoja.getRow(5);
            XSSFCell celda51 = fila5.getCell(1);
            celda51.setCellValue((String) datosOT[4]);
            XSSFCell celda54 = fila5.getCell(4);
            celda54.setCellValue((Double) datosOT[5]);
            XSSFCell celda55 = fila5.getCell(5);
            celda55.setCellValue((String) datosOT[6]);
            XSSFCell celda56 = fila5.getCell(6);
            celda56.setCellValue((String) datosOT[7]);
//            listadoCeldas.add(celda51);
//            listadoCeldas.add(celda54);
//            listadoCeldas.add(celda55);
//            listadoCeldas.add(celda56);

            XSSFRow fila7 = hoja.getRow(7);
            XSSFCell celda71 = fila7.getCell(1);
            celda71.setCellValue((String) datosOT[8]);
            XSSFCell celda73 = fila7.getCell(3);
            celda73.setCellValue((String) datosOT[9]);
            XSSFCell celda75 = fila7.getCell(5);
            celda75.setCellValue((String) datosOT[10]);
//            listadoCeldas.add(celda71);
//            listadoCeldas.add(celda73);
//            listadoCeldas.add(celda75);

            XSSFRow fila10 = hoja.getRow(10);
            XSSFCell celda101 = fila10.getCell(1);
            celda101.setCellValue((String) datosOT[11]);
            XSSFCell celda104 = fila10.getCell(4);
            celda104.setCellValue((String) datosOT[12]);
//            listadoCeldas.add(celda101);
//            listadoCeldas.add(celda104);

            XSSFRow fila12 = hoja.getRow(12);
            XSSFCell celda121 = fila12.getCell(1);
            celda121.setCellValue((String) datosOT[13]);
            XSSFCell celda124 = fila12.getCell(4);
            celda124.setCellValue((String) datosOT[14]);
//            listadoCeldas.add(celda121);
//            listadoCeldas.add(celda124);

            XSSFRow fila14 = hoja.getRow(14);
            XSSFCell celda143 = fila14.getCell(3);
            celda143.setCellValue((String) datosOT[15]);

            XSSFRow fila15 = hoja.getRow(15);
            XSSFCell celda153 = fila15.getCell(3);
            celda153.setCellValue((String) datosOT[16]);

            FileOutputStream ultimo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(ultimo);
            ultimo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);

        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error en cargar datos a la OT. Asegurate de no tener ya el archivo abierto, si es así cierralo para poder volver a generarlo.");
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_pendientes = new javax.swing.JTable();
        jButton_GenerarOT = new javax.swing.JButton();
        jButton_pendientes = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_idVenta = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_trabajo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser_fechaTerminacion = new com.toedter.calendar.JDateChooser();
        jButton_marcarTerminado = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_pendientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id Venta", "F. Venta", "F. Entrega", "Cliente", "T. Venta", "Clasif.", "Cantidad", "Descripcion", "Tamaño", "Color tinta", "Observaciones"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_pendientes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_pendientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_pendientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_pendientes);
        if (jTable_pendientes.getColumnModel().getColumnCount() > 0) {
            jTable_pendientes.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable_pendientes.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTable_pendientes.getColumnModel().getColumn(2).setPreferredWidth(80);
            jTable_pendientes.getColumnModel().getColumn(3).setPreferredWidth(180);
            jTable_pendientes.getColumnModel().getColumn(4).setPreferredWidth(70);
            jTable_pendientes.getColumnModel().getColumn(5).setPreferredWidth(70);
            jTable_pendientes.getColumnModel().getColumn(6).setPreferredWidth(60);
            jTable_pendientes.getColumnModel().getColumn(7).setPreferredWidth(200);
            jTable_pendientes.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTable_pendientes.getColumnModel().getColumn(9).setPreferredWidth(100);
            jTable_pendientes.getColumnModel().getColumn(10).setPreferredWidth(200);
        }

        jButton_GenerarOT.setText("Generar OT");
        jButton_GenerarOT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_GenerarOTActionPerformed(evt);
            }
        });

        jButton_pendientes.setText("Informe pendientes");
        jButton_pendientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_pendientesActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Marcar pedido como completado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Id venta");

        jLabel2.setText("Trabajo");

        jLabel3.setText("Seleccionar fecha de terminacion");

        jButton_marcarTerminado.setText("Marcar como terminado");
        jButton_marcarTerminado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_marcarTerminadoActionPerformed(evt);
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextField_idVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 541, Short.MAX_VALUE))
                            .addComponent(jTextField_trabajo)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jDateChooser_fechaTerminacion, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_marcarTerminado, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_idVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_trabajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDateChooser_fechaTerminacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton_marcarTerminado))))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1078, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton_GenerarOT, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton_pendientes))))
                .addGap(0, 22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jButton_GenerarOT)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton_pendientes)))
                .addGap(24, 24, 24)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_pendientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_pendientesMouseClicked

        int fila = jTable_pendientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Seleccione una fila");
        } else {

            jTextField_idVenta.setText(jTable_pendientes.getValueAt(fila, 0).toString().trim());
            jTextField_trabajo.setText("Cant: " + jTable_pendientes.getValueAt(fila, 6).toString().trim() + " - "
                    + jTable_pendientes.getValueAt(fila, 7).toString().trim() + " - tamaño " + jTable_pendientes.getValueAt(fila, 8).toString().trim());

        }

    }//GEN-LAST:event_jTable_pendientesMouseClicked

    private void jButton_marcarTerminadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_marcarTerminadoActionPerformed

        if (!jTextField_idVenta.getText().equals("")) {
            try {
                String fechaTerminacion = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_fechaTerminacion.getDate());

                CambiarEstado(fechaTerminacion);
                limpiarTabla(modelo);
                llenarTabla();
                limpiarFormulario();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Seleccione la fecha correcta", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {

            JOptionPane.showMessageDialog(this, "Seleccione el trabajo a marcar como terminado", "Informacion", JOptionPane.INFORMATION_MESSAGE);

        }


    }//GEN-LAST:event_jButton_marcarTerminadoActionPerformed

    private void jButton_GenerarOTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_GenerarOTActionPerformed

        String idVenta = jTextField_idVenta.getText().trim();

        if (idVenta.equals("")) {
            JOptionPane.showMessageDialog(null, "Selecciona una venta para generar la OT");
        } else {
            GenerarOT(idVenta);
        }


    }//GEN-LAST:event_jButton_GenerarOTActionPerformed

    private void jButton_pendientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_pendientesActionPerformed
        if (jTable_pendientes.getRowCount() > 0) {
            generarInformePendientes();
        } else {
            JOptionPane.showMessageDialog(null, "No hay elementos para generar informe de pendientes");
        }


    }//GEN-LAST:event_jButton_pendientesActionPerformed

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
            java.util.logging.Logger.getLogger(ListadoPendiente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoPendiente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoPendiente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoPendiente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoPendiente().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_GenerarOT;
    private javax.swing.JButton jButton_marcarTerminado;
    private javax.swing.JButton jButton_pendientes;
    private com.toedter.calendar.JDateChooser jDateChooser_fechaTerminacion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_pendientes;
    private javax.swing.JTextField jTextField_idVenta;
    private javax.swing.JTextField jTextField_trabajo;
    // End of variables declaration//GEN-END:variables
}

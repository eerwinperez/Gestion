/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
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
public class ListadoFacturasPendientesPago extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso;

    /**
     * Creates new form ListadoFacturas
     */
    public ListadoFacturasPendientesPago() {
        initComponents();

        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
    }

    public ListadoFacturasPendientesPago(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();

        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
    }

    public void IniciarCaracteristicasGenerales() {
        jTextField_cliente.setEnabled(false);
        jTextField_factura.setEnabled(false);
        jLabel_fila.setVisible(false);
        jButton_imprimir.setVisible(false);

        SettearModelo();
        llenarTabla();

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado de facturas pendientes de pago *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void SettearModelo() {
        modelo = (DefaultTableModel) jTable_listafacturas.getModel();
    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_listafacturas.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void llenarTabla() {

        LinkedHashSet<String> clientes = new LinkedHashSet<>();

        try {

            String consulta = "select f.idFactura, f.fechaFactura, c.nombreCliente, ifnull(f.monto-sum(a.abono), f.monto) as saldo, f.condiciondePago\n"
                    + "from facturas f left join abonosfacturas a on f.idFactura=a.factura and a.estado='Activo'\n"
                    + "join clientes c on f.idCliente=c.idCliente\n"
                    + "where f.estado='Activo'\n"
                    + "group by f.idFactura, c.nombreCliente"
                    + " having saldo >0 order by f.idFactura desc";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable_listafacturas.getModel();
            jComboBox_cliente.removeAllItems();
            jComboBox_cliente.addItem("TODOS");

            while (rs.next()) {
                Object[] facturas = new Object[5];

                facturas[0] = rs.getString("f.idFactura");
                facturas[1] = rs.getString("f.fechaFactura");
                facturas[2] = rs.getString("c.nombreCliente");
                facturas[3] = MetodosGenerales.ConvertirIntAMoneda(rs.getDouble("saldo"));
                facturas[4] = rs.getString("f.condiciondePago");

                modelo.addRow(facturas);
                clientes.add(rs.getString("c.nombreCliente"));

            }
            jTable_listafacturas.setModel(modelo);

            TableRowSorter<TableModel> ordenador = new TableRowSorter<>(modelo);
            jTable_listafacturas.setRowSorter(ordenador);

            for (String cliente : clientes) {
                jComboBox_cliente.addItem(cliente);
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos en la tabla facturas desde la base de datos ListadoFacturas llenarTabla()", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public ArrayList<Object[]> consultarFacturas(String cliente) {

        String consulta = "";

        if (!cliente.equals("TODOS")) {
            consulta = "select f.idFactura, f.registradoPor, f.monto, f.fechaFactura, c.nombreCliente, ifnull(f.monto-sum(a.abono), f.monto) as saldo, f.condiciondePago\n"
                    + "from facturas f left join abonosfacturas a on f.idFactura=a.factura and a.estado='Activo'\n"
                    + "join clientes c on f.idCliente=c.idCliente and c.idCliente=? \n"
                    + "where f.estado='Activo'\n"
                    + "group by f.idFactura, c.nombreCliente"
                    + " having saldo >0";
            
            ArrayList<Object[]> listado = new ArrayList<>();

            Connection cn = Conexion.Conectar();
            try {
                PreparedStatement pst = cn.prepareStatement(consulta);
                pst.setString(1, cliente);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    Object[] facturas = new Object[8];

                    facturas[0] = rs.getInt("f.idFactura");
                    facturas[1] = rs.getDate("f.fechaFactura");
                    facturas[2] = rs.getString("c.nombreCliente");
                    facturas[3] = rs.getDouble("f.monto");
                    facturas[4] = "";
                    facturas[5] = rs.getDouble("saldo");
                    facturas[6] = rs.getString("f.condiciondePago");
                    facturas[7] = rs.getString("f.registradoPor");

                    listado.add(facturas);
                }
                cn.close();
                return listado;

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al leer las facturas y generar el informe");
                e.printStackTrace();
            }
        } else {
            consulta = "select f.idFactura, f.monto, f.registradoPor, f.fechaFactura, c.nombreCliente, ifnull(f.monto-sum(a.abono), f.monto) as saldo, f.condiciondePago\n"
                    + "from facturas f left join abonosfacturas a on f.idFactura=a.factura and a.estado='Activo'\n"
                    + "join clientes c on f.idCliente=c.idCliente\n"
                    + "where f.estado='Activo'\n"
                    + "group by f.idFactura, c.nombreCliente"
                    + " having saldo >0";
            ArrayList<Object[]> listado = new ArrayList<>();

            Connection cn = Conexion.Conectar();
            try {
                PreparedStatement pst = cn.prepareStatement(consulta);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    Object[] facturas = new Object[8];

                    facturas[0] = rs.getInt("f.idFactura");
                    facturas[1] = rs.getDate("f.fechaFactura");
                    facturas[2] = rs.getString("c.nombreCliente");
                    facturas[3] = rs.getDouble("f.monto");
                    facturas[4] = "";
                    facturas[5] = rs.getDouble("saldo");
                    facturas[6] = rs.getString("f.condiciondePago");
                    facturas[7] = rs.getString("f.registradoPor");

                    listado.add(facturas);
                }
                cn.close();
                return listado;

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al leer las facturas y generar el informa");
                e.printStackTrace();
            }
        }

        return null;
    }

    public void limpiarCampos() {
        jTextField_cliente.setText("");
        jTextField_factura.setText("");
        jTextField_ingreso.setText("");
        jTextField_observaciones.setText("");
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

        } catch (SQLException e) {
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
                            celda.setCellValue((Double) elemento[i]);
                            break;
                        case 1:
                            celda.setCellValue((Double) elemento[i]);
                            break;
                        case 2:
                            celda.setCellValue((Double) elemento[i]);
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
            JOptionPane.showMessageDialog(this, "Error al generar la factura. Es posible que tenga una factura con el mismo nombre abierto");
            ex.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error en generar la factura. Asegurse se no tener una factura abierta y vuelta a intentarlo. ImprimirRecibo GenerarRecibo()");
            e.printStackTrace();
        }

    }

    public void RegistrarIngreso(String numeroFactura, int ingreso, String fecha, String observaciones, String usuario) {

        String consulta = "insert into abonosfacturas (idFactura, valorAbono, fechaAbonoSistema, observaciones, registradoPor) "
                + "values ('" + numeroFactura + "', '" + ingreso + "', '" + fecha + "', '" + observaciones + "', '" + usuario + "')";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(null, "Abono registrado");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en registrar el abono de la factura. ListadoFacturas RegistrarIngreso()");
            e.printStackTrace();
        }
    }

    public void CambiarEstadoFactura(String idFactura) {
        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String consulta = "update facturas set estadoPago='Saldado', fechaSaldado=? where idFactura=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, fecha);
            pst.setString(2, idFactura);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "Estado de la factura actualizado a saldado");

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el estado de la factura ListadoFacturas CambiarEstadoFactura()");
        }

    }

    public void ImprimirInformeDeudaFacturas(ArrayList<Object[]> listado, String cliente) {

        String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Estado de cuenta Empresas.xlsx";
        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Estado de cuenta Empresas.xlsx";
        String facturas = "(";

        for (int i = 0; i < listado.size(); i++) {
            if (i != listado.size() - 1) {
                facturas += String.valueOf((Integer) listado.get(i)[0]) + ",";
            } else {
                facturas += String.valueOf((Integer) listado.get(i)[0]) + ")";
            }
        }

        String consultaAbonos = "SELECT idAbono, factura, abono, fecha, observaciones, "
                + "registradoPor from abonosfacturas where factura in " + facturas + " and estado='Activo'";

        ArrayList<Object[]> listadoAbonos = consultarAbonos(consultaAbonos);

        try {
            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();

            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            String texto = (cliente.equals("TODOS") ? "ESTADO DE CUENTA - TODOS" : "INFORME DE "
                    + "ESTADO DE CUENTA - CLIENTE: " + cliente);

            XSSFRow fila0 = hoja.getRow(0);
            XSSFCell celda00 = fila0.getCell(0);
            celda00.setCellValue(texto);

            //Completamos los datos del usuario y fecha            
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda11 = fila1.getCell(2);
            celda11.setCellValue(new Date());

            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda21 = fila2.getCell(2);
            celda21.setCellValue(this.usuario);

            //Dado que la informacion se empezara a cargar desde la fila 5, establecemos ese inicio
            int filaInicio = 5;
            //Recorremos el numero de filas que tiene la tabla

            double totalVenta = 0;
            double totalAbonos = 0;

            for (Object[] elemento : listado) {
                XSSFRow fila = hoja.getRow(filaInicio);
                //Recorremos ahora las columas para obtener los datos
                for (int i = 0; i < 8; i++) {
                    XSSFCell celda = fila.getCell(i);
                    //En funcion de la columna en la que nos encontremos se tomará el tipo de dato para el reporte
                    switch (i) {
                        case 0:
                            celda.setCellValue((Integer) elemento[i]);
                            break;
                        case 1:
                            celda.setCellValue((Date) elemento[i]);
                            break;
                        case 2:
                            celda.setCellValue((String) elemento[i]);
                            break;
                        case 3:
                            celda.setCellValue((Double) elemento[i]);
                            totalVenta += (Double) elemento[i];
                            break;
                        case 4:
                            celda.setCellValue("");
                            break;
                        case 5:
                            celda.setCellValue((Double) elemento[i]);
                            break;
                        case 6:
                            celda.setCellValue("Cond. Pago: " + (String) elemento[i]);
                            break;
                        case 7:
                            celda.setCellValue((String) elemento[i]);
                            break;
                    }
                }

                for (Object[] elemento2 : listadoAbonos) {
                    if (elemento2[8].equals(elemento[0])) {
                        XSSFRow filaAbono = hoja.getRow(++filaInicio);
                        for (int k = 0; k < 8; k++) {
                            XSSFCell celdaAbono = filaAbono.getCell(k);
                            switch (k) {
                                case 0:
                                    celdaAbono.setCellValue("Abono No. " + String.valueOf((Integer) elemento2[k]));
                                    break;
                                case 1:
                                    celdaAbono.setCellValue((Date) elemento2[k]);
                                    break;
                                case 2:
                                    celdaAbono.setCellValue((String) elemento[2]);
                                    break;
                                case 3:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 4:
                                    celdaAbono.setCellValue((Double) elemento2[k]);
                                    totalAbonos += (Double) elemento2[k];
                                    break;
                                case 5:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    //totalAbonos += (Double) elemento2[k];
                                    break;
                                case 6:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 7:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;

                            }

                        }

                    }

                }

                filaInicio++;
            }

            filaInicio++;

            XSSFRow filaSubtotales = hoja.getRow(filaInicio);
            XSSFCell textoSubt = filaSubtotales.getCell(2);
            textoSubt.setCellValue("TOTALES");
            XSSFCell totVenta = filaSubtotales.getCell(3);
            totVenta.setCellValue(totalVenta);
            XSSFCell totAbono = filaSubtotales.getCell(4);
            totAbono.setCellValue(totalAbonos);
            XSSFCell saldo = filaSubtotales.getCell(5);
            saldo.setCellValue(totalVenta - totalAbonos);

            XSSFCell celda24 = fila2.getCell(4);
            celda24.setCellValue(totalVenta - totalAbonos);

            FileOutputStream nuevo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(nuevo);
            nuevo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error al crear el documento. Un documento con el mismo nombre esta abierto."
                    + "\nCierrelo e intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error en generar el reporte de deuda de Empresas ", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public ArrayList<Object[]> consultarAbonos(String consultaAbonos) {

        ArrayList<Object[]> listado = new ArrayList<>();

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consultaAbonos);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[9];
                nuevo[0] = rs.getInt("idAbono");
                nuevo[1] = rs.getDate("fecha");
                nuevo[2] = "";
                nuevo[3] = "";
                nuevo[4] = rs.getDouble("abono");
                nuevo[5] = "";
                nuevo[6] = rs.getString("observaciones");
                nuevo[7] = rs.getString("registradoPor");
                nuevo[8] = rs.getInt("factura");

                listado.add(nuevo);

            }

            cn.close();
            return listado;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los abonos de las facturas", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public boolean ComprobarIngreso(String abono) {

        char[] cadena = abono.toCharArray();
        for (int i = 0; i < cadena.length; i++) {
            if (!Character.isDigit(cadena[i]) && cadena[i] != '.') {
                return false;
            }
        }
        return true;
    }

    public void RegistrarAbonoFactura(String factura, String abono, String observaciones, String cliente, String presupuesto) {

        String consulta = "insert into abonosfacturas (factura, abono, fecha, observaciones, registradoPor, presupuesto) values (?, ?, ?, ?, ?, ?);";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, factura);
            pst.setString(2, abono);
            pst.setString(3, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            pst.setString(4, observaciones);
            pst.setString(5, this.usuario);
            pst.setString(6, presupuesto);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Abono registrado", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            cn.close();

//            String asunto = "Abono registrado - Factura "+factura+" - "+cliente;
//            String mensaje = "Nuevo abono registrado"
//                    +"\nFactura "+factura+" "+cliente
//                    + "\nValor abono = " +MetodosGenerales.ConvertirIntAMoneda(Double.parseDouble(abono))+
//                    "\nUsuario responsable: "+this.usuario+
//                    "\nObservaciones: "+observaciones;
//            
//            MetodosGenerales.enviarEmail(asunto, mensaje);
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El monto ingresado no es valido. No incluir signos ni simbolos."
                    + "\nIncluya punto (.) para cantidades decimales", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "SQLException\nError en registrar abono a la factura", "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Error al leer los datos de cabecera");
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Object[]> consultarElementosFactura(String numeroFactura) {
        String consulta = "select er.idVenta, er.id, ef.cantidad, v.descripcionTrabajo, v.unitario, ef.cantidad*v.unitario as total \n"
                + "from elementosfactura ef join elementosremision er on ef.idElementoRemito=er.id and er.estado='Activo' and ef.estado='Activo'\n"
                + "join ventas v on er.idVenta=v.Idventa\n"
                + "where ef.factura=?";

        ArrayList<Object[]> listado = new ArrayList<>();
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, numeroFactura);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] elementos = new Object[6];
                elementos[0] = rs.getDouble("er.idVenta");
                elementos[1] = rs.getDouble("er.id");
                elementos[2] = rs.getDouble("ef.cantidad");
                elementos[3] = rs.getString("v.descripcionTrabajo");
                elementos[4] = rs.getDouble("v.unitario");
                elementos[5] = rs.getDouble("total");

                listado.add(elementos);
            }
            cn.close();
            return listado;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los elementos de la factura");
            e.printStackTrace();
        }

        return null;
    }

    public String SumaSaldo(String seleccion, JTable tabla) {

        if (seleccion.equals("TODOS")) {
            double suma = 0;

            for (int i = 0; i < tabla.getRowCount(); i++) {
                suma += Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(tabla.getValueAt(i, 3).toString().trim()));
            }

            return MetodosGenerales.ConvertirIntAMoneda(suma);

        } else {
            double suma = 0;

            for (int i = 0; i < tabla.getRowCount(); i++) {
                if (tabla.getValueAt(i, 2).toString().trim().equals(seleccion)) {
                    suma += Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(tabla.getValueAt(i, 3).toString().trim()));
                }
            }

            return MetodosGenerales.ConvertirIntAMoneda(suma);
        }

    }

    public String consultarIdCliente(String nombreCliente) {

        String consulta = "select idCliente from clientes where nombreCliente=?";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, nombreCliente);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("idCliente");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el id del cliente", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }
    
    public String consultarPresupuesto() {
        String consulta = "select max(idPresupuesto) as presup from presupuestos";

        Connection cn = Conexion.Conectar();
        try {

            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("presup");
            }

            cn.close();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el presupuesto en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        
        return null;
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
        jTable_listafacturas = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_factura = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_ingreso = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField_observaciones = new javax.swing.JTextField();
        jButton_registrarIngreso = new javax.swing.JButton();
        jButton_imprimir = new javax.swing.JButton();
        jLabel_fila = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jComboBox_cliente = new javax.swing.JComboBox<>();
        jButton_informe = new javax.swing.JButton();
        jTextField_deuda = new javax.swing.JTextField();
        jButton_calcular = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_listafacturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Factura", "Fecha", "Cliente", "Saldo", "Condicion de pago"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_listafacturas.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_listafacturas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_listafacturasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_listafacturas);
        if (jTable_listafacturas.getColumnModel().getColumnCount() > 0) {
            jTable_listafacturas.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable_listafacturas.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_listafacturas.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTable_listafacturas.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTable_listafacturas.getColumnModel().getColumn(4).setPreferredWidth(180);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Factura a abonar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("No. Fact");

        jTextField_factura.setEditable(false);

        jLabel2.setText("Cliente");

        jTextField_cliente.setEditable(false);

        jLabel4.setText("Valor abono");

        jTextField_ingreso.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_ingresoKeyTyped(evt);
            }
        });

        jLabel5.setText("Observaciones");

        jTextField_observaciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_observacionesKeyTyped(evt);
            }
        });

        jButton_registrarIngreso.setText("Registrar");
        jButton_registrarIngreso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_registrarIngresoActionPerformed(evt);
            }
        });

        jButton_imprimir.setText("Imprimir factura");
        jButton_imprimir.setEnabled(false);
        jButton_imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_imprimirActionPerformed(evt);
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
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_ingreso, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_observaciones)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton_registrarIngreso)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_factura, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton_imprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField_factura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButton_imprimir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_ingreso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_registrarIngreso))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel_fila.setText("jLabel3");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informe de deuda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jButton_informe.setText("Informe");
        jButton_informe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_informeActionPerformed(evt);
            }
        });

        jTextField_deuda.setEnabled(false);

        jButton_calcular.setText("Calcular");
        jButton_calcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_calcularActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_deuda, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(jButton_calcular, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_informe, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_deuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_calcular)
                    .addComponent(jButton_informe))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel_fila)
                .addGap(150, 150, 150))
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel_fila)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_imprimirActionPerformed

        String numeroFactura = jTextField_factura.getText().trim();
        //Verificamos que se haya seleccionado una factura
        if (numeroFactura.equals("")) {
            JOptionPane.showMessageDialog(null, "Seleccione la factura a imprimir");
        } else {

            Object[] datosCabecera = consultarDatosCabecera(numeroFactura);
            ArrayList<Object[]> elementosFactura = consultarElementosFactura(numeroFactura);
            imprimirFactura(numeroFactura, datosCabecera, elementosFactura);

        }


    }//GEN-LAST:event_jButton_imprimirActionPerformed

    private void jTable_listafacturasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_listafacturasMouseClicked

        int fila = jTable_listafacturas.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona una fila valida");
        } else {
            jTextField_cliente.setText(jTable_listafacturas.getValueAt(fila, 2).toString());
            jTextField_factura.setText(jTable_listafacturas.getValueAt(fila, 0).toString());
            jLabel_fila.setText(String.valueOf(fila));
            //jLabel_fila.setText(String.valueOf(fila));
        }

    }//GEN-LAST:event_jTable_listafacturasMouseClicked

    private void jButton_registrarIngresoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_registrarIngresoActionPerformed

        String numeroFactura = jTextField_factura.getText().trim();
        String cliente = jTextField_cliente.getText().trim();
        String abono = jTextField_ingreso.getText().trim();
        String observaciones = jTextField_observaciones.getText().trim();

        try {
            if (!numeroFactura.equals("")) {
                int fila = Integer.parseInt(jLabel_fila.getText());
                double deuda = Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jTable_listafacturas.getValueAt(fila, 3).toString()));
                if (!abono.equals("")) {

                    if (Double.parseDouble(abono) <= deuda) {
                        int opcion = JOptionPane.showConfirmDialog(this, "¿Desea registrar el abono de"
                                + MetodosGenerales.ConvertirIntAMoneda(Double.parseDouble(abono))
                                + " a la factura No. " + numeroFactura + " cliente "+cliente+"?");

                        if (opcion == 0) {
                            
                            String presupuesto = consultarPresupuesto();
                            RegistrarAbonoFactura(numeroFactura, abono, observaciones, cliente, presupuesto);
                            limpiarTabla(modelo);
                            llenarTabla();
                            limpiarCampos();
                        } else{
                            JOptionPane.showMessageDialog(this, "NO ha seleccionado la opcion SI, por lo tanto el abono no se ha registrado","Informacion",JOptionPane.WARNING_MESSAGE);
                        }

                    } else {
                        JOptionPane.showMessageDialog(this, "No es posible ingresar un abono superior a la deuda registrada", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Ingrese el abono a registrar", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Seleccione la factura a abonar", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El monto ingresado no es valido. No incluir signos ni simbolos."
                    + "\nIncluya punto (.) para cantidades decimales", "Error", JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_jButton_registrarIngresoActionPerformed

    private void jButton_informeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_informeActionPerformed
        //Verificamos que existan datos en la tabla
        int filas = jTable_listafacturas.getRowCount();
        if (filas > 0) {
            String cliente = jComboBox_cliente.getSelectedItem().toString();

            String idCliente = (!cliente.equals("TODOS") ? consultarIdCliente(cliente) : "TODOS");

            ArrayList<Object[]> listado = consultarFacturas(idCliente);
            ImprimirInformeDeudaFacturas(listado, cliente);

        } else {
            JOptionPane.showMessageDialog(this, "No existen deudas de empresas", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton_informeActionPerformed

    private void jTextField_ingresoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_ingresoKeyTyped
        char c = evt.getKeyChar();

        if (!Character.isDigit(c) && c != '.') {
            evt.consume();
        }

        if (c == '0' && jTextField_ingreso.getText().trim().length() == 0) {
            evt.consume();
        }

        if (c == '.' && jTextField_ingreso.getText().trim().length() == 0) {
            evt.consume();
        }

        int contador = 0;

        char[] cadena = jTextField_ingreso.getText().trim().toCharArray();
        for (int i = 0; i < jTextField_ingreso.getText().trim().length(); i++) {
            if (cadena[i] == '.') {
                contador++;
                break;
            }
        }

        if (contador > 0 && c == '.') {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_ingresoKeyTyped

    private void jTextField_observacionesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_observacionesKeyTyped
        if (jTextField_observaciones.getText().trim().length() == 250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_observacionesKeyTyped

    private void jButton_calcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_calcularActionPerformed

        if (jTable_listafacturas.getRowCount() > 0) {

            String seleccion = jComboBox_cliente.getSelectedItem().toString().trim();
            String todos = SumaSaldo(seleccion, jTable_listafacturas);
            jTextField_deuda.setText(todos);

        } else {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton_calcularActionPerformed

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
            java.util.logging.Logger.getLogger(ListadoFacturasPendientesPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoFacturasPendientesPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoFacturasPendientesPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoFacturasPendientesPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoFacturasPendientesPago().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_calcular;
    private javax.swing.JButton jButton_imprimir;
    private javax.swing.JButton jButton_informe;
    private javax.swing.JButton jButton_registrarIngreso;
    private javax.swing.JComboBox<String> jComboBox_cliente;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel_fila;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_listafacturas;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_deuda;
    private javax.swing.JTextField jTextField_factura;
    private javax.swing.JTextField jTextField_ingreso;
    private javax.swing.JTextField jTextField_observaciones;
    // End of variables declaration//GEN-END:variables
}

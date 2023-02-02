/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
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
public class RegistroAbonos extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso;

    /**
     * Creates new form RegistroAbonos
     */
    public RegistroAbonos() {
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

    }

    public RegistroAbonos(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Registro de abonos *** " + "Usuario: " + usuario + " - " + permiso);
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
        llenarTabla();
    }

    public void InhabilitarCampos() {
        jTextField_cliente.setEnabled(false);
        jTextField_idventa.setEnabled(false);
        jLabel_fila.setVisible(false);
    }

    public void SettearModelo() {
        modelo = (DefaultTableModel) jTable_pendientesdeAbono.getModel();
    }

    public void LimpiarFormulario() {

        jTextField_cliente.setText("");
        jTextField_observaciones.setText("");
        jTextField_valorabono.setText("");
        jTextField_idventa.setText("");
        jTextField_deuda.setText("");

    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_pendientesdeAbono.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void llenarTabla() {

        LinkedHashSet<String> clientes = new LinkedHashSet<>();

        try {
            String consulta = "select v.Idventa, v.precio, v.FechaventaSistema, v.Idcliente, c.nombreCliente, v.Cantidad, concat(v.descripcionTrabajo,' - ' ,v.tamaño) as descripcion, v.FechaventaSistema,\n"
                    + "ifnull(v.precio - SUM(a.valor), v.precio) as saldo, SUM(a.valor) as abonos \n"
                    + "from ventas v left join abonos a on v.Idventa=a.idVenta and a.estado='Activo'\n"
                    + "left join clientes c on v.Idcliente=c.idCliente \n"
                    + "where v.tipoVenta='Entradas diarias' and v.estado='Activo' \n"
                    + "group by v.Idventa, c.nombreCliente \n"
                    + " having saldo >0 "
                    + "ORDER by v.Idventa desc";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable_pendientesdeAbono.getModel();
            jComboBox_cliente.removeAllItems();
            jComboBox_cliente.addItem("TODOS");

            while (rs.next()) {
                Object[] datos = new Object[9];

                datos[0] = rs.getString("v.Idventa");
                datos[1] = rs.getString("v.FechaventaSistema");
                datos[2] = rs.getString("v.Idcliente");
                datos[3] = rs.getString("c.nombreCliente");
                datos[4] = rs.getString("descripcion");
                datos[5] = rs.getString("v.Cantidad");
                datos[6] = MetodosGenerales.ConvertirIntAMoneda(rs.getDouble("v.precio"));
                datos[7] = MetodosGenerales.ConvertirIntAMoneda(rs.getDouble("abonos"));
                datos[8] = MetodosGenerales.ConvertirIntAMoneda(rs.getDouble("saldo"));

                modelo.addRow(datos);
                clientes.add(rs.getString("c.nombreCliente"));

            }

            for (String cliente : clientes) {
                jComboBox_cliente.addItem(cliente);
            }

            jTable_pendientesdeAbono.setModel(modelo);

            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable_pendientesdeAbono.setRowSorter(ordenador);

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos en la tabla clientes desde la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void RegistrarAbono(String IdVenta, String valorAbono, String observaciones, String registradoPor, String cliente, String presupuesto) {

        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String consulta = "insert into abonos (idVenta, valor, fecha, observaciones, "
                + "registradoPor, presupuesto) values (?, ?, ?, ?, ?, ?)";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);

            pst.setString(1, IdVenta);
            pst.setString(2, valorAbono);
            pst.setString(3, fecha);
            pst.setString(4, observaciones);
            pst.setString(5, registradoPor);
            pst.setString(6, presupuesto);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Abono registrado", "Informacion", JOptionPane.INFORMATION_MESSAGE);

            ResultSet idGenerado = pst.getGeneratedKeys();
            idGenerado.next();
            int idAbono = idGenerado.getInt(1);
            cn.close();

//            String asunto = "Abono " + idAbono + " " + cliente + " registrado - Entradas diarias";
//            String mensaje = "Abono registrado"
//                    + "\nValor abono = " + MetodosGenerales.ConvertirIntAMoneda(Double.parseDouble(valorAbono))
//                    + "\nUsuario responsable: " + this.usuario
//                    + "\nObservaciones: " + observaciones;
//
//            MetodosGenerales.enviarEmail(asunto, mensaje);
//            MetodosGenerales.registrarHistorial(usuario, mensaje);
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "SQLException\nError al registrar el abono. RegistroAbonos RegistrarAbono()", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public void SaldarVenta(String idVenta, String Saldado) {

        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String consulta = "update ventas set fechaSaldado=?, estadoCuenta=? where Idventa=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, fecha);
            pst.setString(2, Saldado);
            pst.setString(3, idVenta);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "El estado del pedido ha sido actualizado a saldado");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en cambiar el estado del pedido. RegistroAbonos SaldarVenta()");
            e.printStackTrace();
        }

    }

    public String SumaSaldo(String seleccion, JTable tabla) {

        if (seleccion.equals("TODOS")) {
            double suma = 0;

            for (int i = 0; i < tabla.getRowCount(); i++) {
                suma += Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(tabla.getValueAt(i, 8).toString().trim()));
            }

            return MetodosGenerales.ConvertirIntAMoneda(suma);

        } else {
            double suma = 0;

            for (int i = 0; i < tabla.getRowCount(); i++) {
                if (tabla.getValueAt(i, 3).toString().trim().equals(seleccion)) {
                    suma += Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(tabla.getValueAt(i, 8).toString().trim()));
                }
            }

            return MetodosGenerales.ConvertirIntAMoneda(suma);
        }

    }

    public void GenerarInformeDeuda(String cliente, ArrayList<Object[]> listado, ArrayList<Object[]> listadoAbonos) {

        String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Estado de cuenta Entradas Diarias.xlsx";
        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Estado de cuenta Entradas Diarias.xlsx";

        try {
            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();

            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            String cabecera = (cliente.equals("TODOS") ? "ESTADO DE CUENTA / DEUDAS - TODOS"
                    : "ESTADO DE CUENTA - CLIENTE: " + cliente);

            XSSFRow fila0 = hoja.getRow(0);
            XSSFCell celda00 = fila0.getCell(0);
            celda00.setCellValue(cabecera);

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
                //System.out.println("Tamaño: " + elemento.length);
                for (int i = 0; i < 8; i++) {
                    XSSFCell celda = fila.getCell(i);
                    //En funcion de la columna en la que nos encontremos se tomará el tipo de dato para el reporte
                    switch (i) {
                        case 0:
                            celda.setCellValue((Double) elemento[i]);
                            break;
                        case 1:
                            celda.setCellValue((Date) elemento[i]);
                            break;
                        case 2:
                            celda.setCellValue((String) elemento[i]);
                            break;
                        case 3:
                            celda.setCellValue((String) elemento[i]);
                            break;
                        case 4:
                            celda.setCellValue((Double) elemento[i]);
                            totalVenta += (Double) elemento[i];
                            break;
                        case 6:
                            celda.setCellValue((Double) elemento[6]);
                            break;
                        case 7:
                            celda.setCellValue((String) elemento[5]);
                            break;
                    }
                    //filaInicio++;
                }

                for (Object[] elemento2 : listadoAbonos) {
                    if (elemento2[0].equals(elemento[0])) {
                        XSSFRow filaAbono = hoja.getRow(++filaInicio);
                        for (int k = 0; k < 8; k++) {
                            XSSFCell celdaAbono = filaAbono.getCell(k);
                            switch (k) {
                                case 0:
                                    celdaAbono.setCellValue((Double) elemento2[k]);
                                    break;
                                case 1:
                                    celdaAbono.setCellValue((Date) elemento2[k]);
                                    break;
                                case 2:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 3:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 4:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 5:
                                    celdaAbono.setCellValue((Double) elemento2[k]);
                                    totalAbonos += (Double) elemento2[k];
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
            XSSFCell textoSubt = filaSubtotales.getCell(3);
            textoSubt.setCellValue("TOTALES");
            XSSFCell totVenta = filaSubtotales.getCell(4);
            totVenta.setCellValue(totalVenta);
            XSSFCell totAbono = filaSubtotales.getCell(5);
            totAbono.setCellValue(totalAbonos);
            XSSFCell saldo = filaSubtotales.getCell(6);
            saldo.setCellValue(totalVenta - totalAbonos);

            XSSFCell celda24 = fila2.getCell(5);
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

    public ArrayList<Object[]> consultarDeudas(String IDcliente, String cliente) {

        ArrayList<Object[]> listado = new ArrayList<>();
        String consulta = "";

        if (!IDcliente.equals("TODOS")) {
            consulta = "select v.Idventa, v.precio, v.precio, v.registradoPor, v.FechaventaSistema, v.Idcliente, v.Cantidad, concat(v.descripcionTrabajo,' - ' ,v.tamaño) as descripcion, v.FechaventaSistema,\n"
                    + "ifnull(v.precio - SUM(a.valor), v.precio) as saldo\n"
                    + "from ventas v left join abonos a on v.Idventa=a.idVenta and a.estado='Activo' \n"
                    + "left join clientes c on v.Idcliente=c.idCliente  \n"
                    + "where v.tipoVenta='Entradas diarias' and v.estado='Activo' and v.Idcliente=? \n"
                    + "group by v.Idventa\n"
                    + "having saldo >0 \n"
                    + "ORDER by v.Idventa";

            try {
                Connection cn = Conexion.Conectar();
                PreparedStatement pst = cn.prepareStatement(consulta);
                pst.setString(1, IDcliente);

                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    Object[] nuevo = new Object[7];
                    nuevo[0] = rs.getDouble("v.Idventa");
                    nuevo[1] = rs.getDate("v.FechaventaSistema");
                    nuevo[2] = cliente;
                    nuevo[3] = rs.getString("descripcion");
                    nuevo[4] = rs.getDouble("v.precio");
                    nuevo[5] = rs.getString("v.registradoPor");
                    nuevo[6] = rs.getDouble("saldo");

                    listado.add(nuevo);

                }

                cn.close();
                return listado;

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al leer las deudas del cliente\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            consulta = "select v.Idventa, v.precio, v.registradoPor,  v.FechaventaSistema, v.Idcliente, c.nombreCliente, v.Cantidad, concat(v.descripcionTrabajo,' - ' ,v.tamaño) as descripcion, v.FechaventaSistema,\n"
                    + "ifnull(v.precio - SUM(a.valor), v.precio) as saldo\n"
                    + "from ventas v left join abonos a on v.Idventa=a.idVenta and a.estado='Activo' \n"
                    + "left join clientes c on v.Idcliente=c.idCliente  \n"
                    + "where v.tipoVenta='Entradas diarias' and v.estado='Activo' \n"
                    + "group by v.Idventa, c.nombreCliente \n"
                    + "having saldo >0 \n"
                    + "ORDER by v.Idventa";

            try {
                Connection cn = Conexion.Conectar();
                PreparedStatement pst = cn.prepareStatement(consulta);

                ResultSet rs = pst.executeQuery();

                while (rs.next()) {

                    Object[] nuevo = new Object[7];
                    nuevo[0] = rs.getDouble("v.Idventa");
                    nuevo[1] = rs.getDate("v.FechaventaSistema");
                    nuevo[2] = rs.getString("c.nombreCliente");
                    nuevo[3] = rs.getString("descripcion");
                    nuevo[4] = rs.getDouble("v.precio");
                    nuevo[5] = rs.getString("v.registradoPor");
                    nuevo[6] = rs.getDouble("saldo");

                    listado.add(nuevo);

                }

                cn.close();
                return listado;

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al leer las deudas del cliente\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

        }

        return null;
    }

    public ArrayList<Object[]> consultarAbonos(String IDcliente, String cliente) {

        ArrayList<Object[]> listado = new ArrayList<>();
        ArrayList<String> ventasAbonos = new ArrayList<>();

        String consulta = "";

        if (!IDcliente.equals("TODOS")) {
            consulta = "select v.Idventa, v.precio, v.FechaventaSistema, v.Idcliente, v.Cantidad, concat(v.descripcionTrabajo,' - ' ,v.tamaño) as descripcion, v.FechaventaSistema,\n"
                    + "ifnull(v.precio - SUM(a.valor), v.precio) as saldo\n"
                    + "from ventas v left join abonos a on v.Idventa=a.idVenta and a.estado='Activo' \n"
                    + "left join clientes c on v.Idcliente=c.idCliente  \n"
                    + "where v.tipoVenta='Entradas diarias' and v.estado='Activo' and v.Idcliente=? \n"
                    + "group by v.Idventa\n"
                    + "having saldo >0 \n"
                    + "ORDER by v.Idventa";

            try {
                Connection cn = Conexion.Conectar();
                PreparedStatement pst = cn.prepareStatement(consulta);
                pst.setString(1, IDcliente);

                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    ventasAbonos.add(rs.getString("v.Idventa"));

                }

                String abonos = "(";
                for (int i = 0; i < ventasAbonos.size(); i++) {
                    if (i != ventasAbonos.size() - 1) {
                        abonos += ventasAbonos.get(i) + ",";
                    } else {
                        abonos += ventasAbonos.get(i) + ")";
                    }
                }

                String consulta2 = "select idAbono, idVenta, fecha, valor, observaciones, registradoPor "
                        + "from abonos where idVenta in " + abonos + " and estado='Activo' order by idAbono";

                PreparedStatement pst2 = cn.prepareStatement(consulta2);
                ResultSet rs2 = pst2.executeQuery();

                while (rs2.next()) {

                    Object[] nuevo = new Object[8];
                    nuevo[0] = rs2.getDouble("idVenta");
                    nuevo[1] = rs2.getDate("fecha");
                    nuevo[2] = cliente;
                    nuevo[3] = "Abono No. " + rs2.getString("idAbono") + " " + rs2.getString("observaciones");
                    nuevo[4] = "";
                    nuevo[5] = rs2.getDouble("valor");
                    nuevo[6] = "";
                    nuevo[7] = rs2.getString("registradoPor");

                    listado.add(nuevo);
                }

                cn.close();
                return listado;

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al leer las deudas del cliente\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            consulta = "select v.Idventa, v.precio, v.FechaventaSistema, v.Idcliente, c.nombreCliente, v.Cantidad, concat(v.descripcionTrabajo,' - ' ,v.tamaño) as descripcion, v.FechaventaSistema,\n"
                    + "ifnull(v.precio - SUM(a.valor), v.precio) as saldo\n"
                    + "from ventas v left join abonos a on v.Idventa=a.idVenta and a.estado='Activo' \n"
                    + "left join clientes c on v.Idcliente=c.idCliente  \n"
                    + "where v.tipoVenta='Entradas diarias' and v.estado='Activo' \n"
                    + "group by v.Idventa, c.nombreCliente\n"
                    + "having saldo >0 \n"
                    + "ORDER by v.Idventa";

            try {
                Connection cn = Conexion.Conectar();
                PreparedStatement pst = cn.prepareStatement(consulta);
                //pst.setString(1, IDcliente);

                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    ventasAbonos.add(rs.getString("v.Idventa"));

                }

                String abonos = "(";
                for (int i = 0; i < ventasAbonos.size(); i++) {
                    if (i != ventasAbonos.size() - 1) {
                        abonos += ventasAbonos.get(i) + ",";
                    } else {
                        abonos += ventasAbonos.get(i) + ")";
                    }
                }

                String consulta2 = "select a.idAbono, a.idVenta, c.nombreCliente, a.fecha, a.valor, a.observaciones, a.registradoPor \n"
                        + "from abonos a join ventas v on a.idVenta=v.Idventa\n"
                        + "join clientes c on v.idCliente=c.idCliente\n"
                        + "where a.idVenta in " + abonos + " and a.estado='Activo' \n"
                        + "order by a.idAbono";

                PreparedStatement pst2 = cn.prepareStatement(consulta2);
                ResultSet rs2 = pst2.executeQuery();

                while (rs2.next()) {

                    Object[] nuevo = new Object[8];
                    nuevo[0] = rs2.getDouble("a.idVenta");
                    nuevo[1] = rs2.getDate("a.fecha");
                    nuevo[2] = rs2.getString("c.nombreCliente");
                    nuevo[3] = "Abono No. " + rs2.getString("a.idAbono") + " " + rs2.getString("a.observaciones");
                    nuevo[4] = "";
                    nuevo[5] = rs2.getDouble("a.valor");
                    nuevo[6] = "";
                    nuevo[7] = rs2.getString("a.registradoPor");

                    listado.add(nuevo);
                }

                cn.close();
                return listado;

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al leer las deudas del cliente", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

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
        jTable_pendientesdeAbono = new javax.swing.JTable();
        jButton_imprimirAbono = new javax.swing.JButton();
        jLabel_fila = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_idventa = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_valorabono = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_observaciones = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jComboBox_cliente = new javax.swing.JComboBox<>();
        jTextField_deuda = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton_generarInforme = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_pendientesdeAbono.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id venta", "Fecha", "Id Cliente", "Cliente", "Descripcion", "Cant.", "P.Venta", "Total abonos", "Saldo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_pendientesdeAbono.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_pendientesdeAbono.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_pendientesdeAbonoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_pendientesdeAbono);
        if (jTable_pendientesdeAbono.getColumnModel().getColumnCount() > 0) {
            jTable_pendientesdeAbono.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable_pendientesdeAbono.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTable_pendientesdeAbono.getColumnModel().getColumn(2).setPreferredWidth(70);
            jTable_pendientesdeAbono.getColumnModel().getColumn(3).setPreferredWidth(180);
            jTable_pendientesdeAbono.getColumnModel().getColumn(4).setPreferredWidth(200);
            jTable_pendientesdeAbono.getColumnModel().getColumn(5).setPreferredWidth(60);
            jTable_pendientesdeAbono.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTable_pendientesdeAbono.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTable_pendientesdeAbono.getColumnModel().getColumn(8).setPreferredWidth(100);
        }

        jButton_imprimirAbono.setText("Imprimir recibo");
        jButton_imprimirAbono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_imprimirAbonoActionPerformed(evt);
            }
        });

        jLabel_fila.setText("jLabel4");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Registrar abono", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Id venta");

        jTextField_idventa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_idventaActionPerformed(evt);
            }
        });

        jLabel4.setText("Cliente");

        jTextField_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_clienteActionPerformed(evt);
            }
        });

        jLabel2.setText("Valor abono");

        jTextField_valorabono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_valorabonoActionPerformed(evt);
            }
        });
        jTextField_valorabono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_valorabonoKeyTyped(evt);
            }
        });

        jLabel3.setText("Observaciones");

        jTextField_observaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_observacionesActionPerformed(evt);
            }
        });
        jTextField_observaciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_observacionesKeyTyped(evt);
            }
        });

        jButton1.setText("Registrar abono");
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
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_idventa, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_valorabono, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_idventa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_valorabono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informe de deuda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jTextField_deuda.setEnabled(false);

        jButton2.setText("Calcular");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton_generarInforme.setText("Informe");
        jButton_generarInforme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generarInformeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField_deuda, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_generarInforme, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_deuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jButton_generarInforme))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton_imprimirAbono, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 961, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(51, 51, 51)
                            .addComponent(jLabel_fila))))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(jLabel_fila))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_imprimirAbono)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField_observacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_observacionesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_observacionesActionPerformed

    private void jTextField_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_clienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_clienteActionPerformed

    private void jTextField_valorabonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_valorabonoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_valorabonoActionPerformed

    private void jTable_pendientesdeAbonoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_pendientesdeAbonoMouseClicked

        int fila = jTable_pendientesdeAbono.getSelectedRow();
        if (fila != -1) {

            jTextField_cliente.setText(jTable_pendientesdeAbono.getValueAt(fila, 3).toString());
            jTextField_idventa.setText(jTable_pendientesdeAbono.getValueAt(fila, 0).toString());
            jLabel_fila.setText(String.valueOf(fila));
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido");
        }

    }//GEN-LAST:event_jTable_pendientesdeAbonoMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        String IdVenta = jTextField_idventa.getText().trim();
        //Verificamos que el usuario haya seleccionado un pedido
        if (!IdVenta.equals("")) {
            //Capturamos el numero de la fila que seleccionamos en la tabla
            int fila = Integer.parseInt(jLabel_fila.getText().trim());
            //Verificamos que el usuario haya diligenciado el campo abono y capturamos el resto de datos
            String valorAbono = jTextField_valorabono.getText().trim();
            String observaciones = jTextField_observaciones.getText().trim().toUpperCase();
            String cliente = jTextField_cliente.getText().trim();
            try {
                if (!valorAbono.equals("") && Double.parseDouble(valorAbono) > 0) {
                    //Verificamos que el abono no sea superior al saldo adeudado
                    String saldo = jTable_pendientesdeAbono.getValueAt(fila, 8).toString();
                    if (Double.parseDouble(valorAbono) <= Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(saldo))) {
                        //Solicitados confirmacion del usuario para registrar el pedido
                        int confirmacion = JOptionPane.showConfirmDialog(this, "Confirmacion - ¿Desea registrar el abono"
                                + " de $" + valorAbono + " al cliente " + cliente + "?", "Confirmacion", JOptionPane.INFORMATION_MESSAGE);

                        if (confirmacion == 0) {

                            String presupuesto = consultarPresupuesto();
                            RegistrarAbono(IdVenta, valorAbono, observaciones, this.usuario, cliente, presupuesto);
                            LimpiarFormulario();
                            limpiarTabla(modelo);
                            llenarTabla();

                        } else {
                            JOptionPane.showMessageDialog(this, "NO ha seleccionado la opcion SI, por lo tanto el abono no se ha registrado", "Informacion", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No es posible registrar un valor de abono mayor al saldo adeudado", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Complete el campo abono con un valor superior a cero", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ingrese el valor del abono correctamente. Use punto (.) para ingresar una cantidad decimal", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido para registrar el abono", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton_imprimirAbonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_imprimirAbonoActionPerformed

        new ListadoAbonosEntradasDiarias(usuario, permiso).setVisible(true);

    }//GEN-LAST:event_jButton_imprimirAbonoActionPerformed

    private void jTextField_idventaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_idventaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_idventaActionPerformed

    private void jTextField_valorabonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_valorabonoKeyTyped
        char c = evt.getKeyChar();

        if (!Character.isDigit(c) && c != '.') {
            evt.consume();
        }

        if (c == '0' && jTextField_valorabono.getText().trim().length() == 0) {
            evt.consume();
        }

        if (c == '.' && jTextField_valorabono.getText().trim().length() == 0) {
            evt.consume();
        }

        int contador = 0;

        char[] cadena = jTextField_valorabono.getText().trim().toCharArray();
        for (int i = 0; i < jTextField_valorabono.getText().trim().length(); i++) {
            if (cadena[i] == '.') {
                contador++;
                break;
            }
        }

        if (contador > 0 && c == '.') {
            evt.consume();
        }

    }//GEN-LAST:event_jTextField_valorabonoKeyTyped

    private void jTextField_observacionesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_observacionesKeyTyped
        if (jTextField_observaciones.getText().trim().length() == 250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_observacionesKeyTyped

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        if (jTable_pendientesdeAbono.getRowCount() > 0) {

            String seleccion = jComboBox_cliente.getSelectedItem().toString().trim();
            String todos = SumaSaldo(seleccion, jTable_pendientesdeAbono);
            jTextField_deuda.setText(todos);

        } else {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton_generarInformeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generarInformeActionPerformed

        if (jTable_pendientesdeAbono.getRowCount() > 0) {
            String cliente = jComboBox_cliente.getSelectedItem().toString().trim();

            String idCliente = (!cliente.equals("TODOS") ? consultarIdCliente(cliente) : "TODOS");
            ArrayList<Object[]> listado = consultarDeudas(idCliente, cliente);
            ArrayList<Object[]> listadoAbonos = consultarAbonos(idCliente, cliente);

            GenerarInformeDeuda(cliente, listado, listadoAbonos);

        } else {
            JOptionPane.showMessageDialog(this, "No hay deudas para generar informe", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton_generarInformeActionPerformed

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
            java.util.logging.Logger.getLogger(RegistroAbonos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroAbonos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroAbonos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroAbonos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistroAbonos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton_generarInforme;
    private javax.swing.JButton jButton_imprimirAbono;
    private javax.swing.JComboBox<String> jComboBox_cliente;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel_fila;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_pendientesdeAbono;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_deuda;
    private javax.swing.JTextField jTextField_idventa;
    private javax.swing.JTextField jTextField_observaciones;
    private javax.swing.JTextField jTextField_valorabono;
    // End of variables declaration//GEN-END:variables
}

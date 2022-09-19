/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import java.awt.Frame;
import static java.awt.Frame.getFrames;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.mail.MessagingException;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 *
 * @author erwin
 */
public final class ListadoPresupuestos extends javax.swing.JFrame {

    String usuario, permiso;
    DefaultTableModel modelo;

    /**
     * Creates new form Presupuestos
     */
    public ListadoPresupuestos() {
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

    }

    public ListadoPresupuestos(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();

        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
        //Inhabilitamos los campos a los que no debe tener acceso los usuarios Asistentes
        if (permiso.equalsIgnoreCase("Asistente")) {
            InhabilitarParaAsistente();
        }
        //Inhabilitamos los campos a los que no debe tener acceso los usuarios Administradores
        if (permiso.equalsIgnoreCase("Administrador")) {
            InhabilitarParaAdministrador();
        }

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado de presupuestos *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void InhabilitarParaAsistente() {
        jButton_crear.setEnabled(false);
        jButton_cambiarEstado.setEnabled(false);
        jButton_cargarRubros.setEnabled(false);
    }

    public void InhabilitarParaAdministrador() {
        jButton_cambiarEstado.setEnabled(false);
    }

    public void IniciarCaracteristicasGenerales() {
        SettearTabla();
        InhabilitarCampos();
        llenarTabla();
        llenarCampos();
    }

    public void llenarCampos() {

        Date fechaInicioNuevoPresup = consultarFechaProximoPresupuesto(consultarMaxPresup());
//        jDateChooser_ini.setDate(fechaInicioNuevoPresup.);
        Calendar c = Calendar.getInstance();
        c.setTime(fechaInicioNuevoPresup);
        c.add(Calendar.DATE, 1);
        fechaInicioNuevoPresup = c.getTime();

        //System.out.println("La fecha es "+fechaInicioNuevoPresup);
        jDateChooser_ini.setDate(fechaInicioNuevoPresup);
        jDateChooser_ini.setEnabled(false);
    }

    public void InhabilitarCampos() {
        jTextField_NumeroPresup.setEnabled(false);
        jTextField_descripcion.setEnabled(false);
        jLabel_fila.setVisible(false);

    }

    public void limpiarCampos() {
        jTextField_NumeroPresup.setText("");
        jTextField_descripcion.setText("");
        jDateChooser_fin.setDateFormatString("");
        jDateChooser_ini.setDateFormatString("");
        jTextField_descrippresup.setText("");

    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_Presupuestos.getRowCount(); i++) {
            this.modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void SettearTabla() {
        modelo = (DefaultTableModel) jTable_Presupuestos.getModel();
    }

    public void llenarTabla() {

        modelo = (DefaultTableModel) jTable_Presupuestos.getModel();

        String consulta = "select  v.idPresupuesto, v.fecha, v.descripcion, v.fechaInicio, v.fechaFin, v.presup, ifnull(sum(g.valor), 0) as gastos, v.estado, v.registradoPor\n"
                + "from vistapresupuestos v left join gastospresupuestos g on v.idPresupuesto=g.idPrespuesto\n"
                + "group by v.idPresupuesto order by v.idPresupuesto desc";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[9];
                nuevo[0] = rs.getString("v.idPresupuesto");
                nuevo[1] = rs.getString("v.fecha");
                nuevo[2] = rs.getString("v.descripcion");
                nuevo[3] = rs.getString("v.fechaInicio");
                nuevo[4] = rs.getString("v.fechaFin");
                nuevo[5] = ConvertirIntAMoneda(rs.getDouble("v.presup"));
                nuevo[6] = ConvertirIntAMoneda(rs.getDouble("gastos"));
                nuevo[7] = rs.getString("v.estado");
                nuevo[8] = rs.getString("v.registradoPor");
                modelo.addRow(nuevo);
            }

            jTable_Presupuestos.setModel(modelo);

            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable_Presupuestos.setRowSorter(ordenador);

            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer la tabla de presupuestos ListadoPresupuestos llenarTabla()");
            e.printStackTrace();
        }

    }

//    public void InhabilitarParaAsistente() {
//        jButton_cambiarEstado.setEnabled(false);
//        jButton_agregarGastos.setEnabled(false);
//        jButton_editarRubros.setEnabled(false);
//    }
    public void CambiarEstadoPresupuesto(String idpresupuesto, String estado) {

        String consulta = "update presupuestos set estado=? where idPresupuesto=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, estado);
            pst.setString(2, idpresupuesto);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Estado de presupuesto actualizado", "Informacion", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en actualizar el estado del presupuesto", "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    public ArrayList<String[]> ConsultarGastos(String idPresupuesto) {

        ArrayList<String[]> listaDatos = new ArrayList<>();

        String consulta = "select itemspresupuesto.idGasto, maestrogastos.idGasto, tipogastos.tipoGasto, "
                + "maestrogastos.idRubro, modalidadgasto.Descripcion, maestrogastos.descripcion, "
                + "itemspresupuesto.valorPresupuestado from itemspresupuesto inner join maestrogastos on itemspresupuesto.idGasto=maestrogastos.id inner join "
                + "modalidadgasto on maestrogastos.idModalidad=modalidadgasto.idModalidad inner join tipogastos on "
                + "maestrogastos.idGasto=tipogastos.idGasto left join gastospresupuestos on itemspresupuesto.idGasto=gastospresupuestos.idConcepto "
                + "where itemspresupuesto.idPresupuesto=? group by itemspresupuesto.idGasto order by maestrogastos.idGasto, "
                + "maestrogastos.idRubro";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String[] dato = new String[5];
                dato[0] = rs.getString("itemspresupuesto.idGasto");
                dato[1] = rs.getString("tipogastos.tipoGasto");
                dato[2] = rs.getString("maestrogastos.descripcion");
                dato[3] = rs.getString("modalidadgasto.Descripcion");
                dato[4] = rs.getString("itemspresupuesto.valorPresupuestado");
//                dato[5] = rs.getString("sumaGastado");
                listaDatos.add(dato);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el presupuesto y sus gastos relacionados. "
                    + "ListadoPresupuestos GenerarInformeEconomico()\n" + e);
        }

        return listaDatos;
    }

    public ArrayList<String[]> ConsultarPartidas(String idPresupuesto) {

        ArrayList<String[]> lista = new ArrayList<>();
        String consulta = "select id, fecha, concepto, valor, observaciones, registradoPor from partidaspresupuestos "
                + "where idPresupuesto=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                String[] dato = new String[6];
                dato[0] = rs.getString("id");
                dato[1] = rs.getString("fecha");
                dato[2] = rs.getString("concepto");
                dato[3] = rs.getString("valor");
                dato[4] = rs.getString("observaciones");
                dato[5] = rs.getString("registradoPor");
                lista.add(dato);

            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer las partidas del presupuesto ListadoPresupuestos "
                    + "ConsultarPartidas()\n" + e);
        }

        return lista;
    }

    public ArrayList<String[]> ConsultarListadoDeGastos(String idPresupuesto) {

        ArrayList<String[]> listadoGastos = new ArrayList<>();
        String consulta = "select gastospresupuestos.fechaGasto, maestrogastos.descripcion, gastospresupuestos.observaciones, "
                + "gastospresupuestos.factura, gastospresupuestos.valor, gastospresupuestos.estado, gastospresupuestos.registradoPor, "
                + "gastospresupuestos.observAutoriza from gastospresupuestos inner join maestrogastos on "
                + "gastospresupuestos.idConcepto=maestrogastos.id where gastospresupuestos.idPrespuesto=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String[] gasto = new String[8];
                gasto[0] = rs.getString("gastospresupuestos.fechaGasto");
                gasto[1] = rs.getString("maestrogastos.descripcion");
                gasto[2] = rs.getString("gastospresupuestos.observaciones");
                gasto[3] = rs.getString("gastospresupuestos.factura");
                gasto[4] = rs.getString("gastospresupuestos.valor");
                gasto[5] = rs.getString("gastospresupuestos.estado");
                gasto[6] = rs.getString("gastospresupuestos.registradoPor");
                gasto[7] = rs.getString("gastospresupuestos.observAutoriza");

                listadoGastos.add(gasto);
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el listado de gastos\n" + e);
        }

        return listadoGastos;
    }

    public ArrayList<String[]> ConsultarGastosSumaConsolidada(String idPresupuesto) {

        ArrayList<String[]> listaGastosConsolidados = new ArrayList<>();
        String consulta = "SELECT maestrogastos.descripcion, SUM(valor) as suma from gastospresupuestos "
                + "inner join maestrogastos on maestrogastos.id=gastospresupuestos.idConcepto where "
                + "idPrespuesto=? and gastospresupuestos.estado='Registrado' group by idConcepto";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String[] nuevo = new String[2];
                nuevo[0] = rs.getString("maestrogastos.descripcion");
                nuevo[1] = rs.getString("suma");
                listaGastosConsolidados.add(nuevo);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los gastos consolidados del presupuesto\n" + e);
        }
        return listaGastosConsolidados;
    }

    public ArrayList<String[]> ConsultarAbonosEntradasDiarias(String fechaInicial, String fechaFinal) {

        ArrayList<String[]> listadeAbonosEntradasDiarias = new ArrayList<>();
        String consulta = "select abonos.idAbono, abonos.fechaAbonoSistema, abonos.idVenta, clientes.nombreCliente, abonos.observaciones, "
                + "abonos.valorAbono, abonos.registradoPor from abonos inner join ventas on "
                + "abonos.idVenta=ventas.Idventa inner join clientes on ventas.Idcliente=clientes.idCliente "
                + "where abonos.fechaAbonoSistema BETWEEN ? and ? ORDER BY abonos.idAbono desc";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaInicial);
            pst.setString(2, fechaFinal);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String[] abono = new String[7];
                abono[0] = rs.getString("abonos.idAbono");
                abono[1] = rs.getString("abonos.idVenta");
                abono[2] = rs.getString("abonos.fechaAbonoSistema");
                abono[3] = rs.getString("clientes.nombreCliente");
                abono[4] = rs.getString("abonos.observaciones");
                abono[5] = rs.getString("abonos.valorAbono");
                abono[6] = rs.getString("abonos.registradoPor");

                listadeAbonosEntradasDiarias.add(abono);
            }

            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el listado de abonos\n" + e);
        }
        return listadeAbonosEntradasDiarias;
    }

    public ArrayList<String[]> ConsultarAbonosFacturas(String fechaDesde, String fechaHasta) {
        ArrayList<String[]> listadoAbonosFacturas = new ArrayList<>();

        String consulta = "SELECT abonosfacturas.idAbono, abonosfacturas.idFactura, abonosfacturas.fechaAbonoSistema, "
                + "clientes.nombreCliente, abonosfacturas.observaciones, abonosfacturas.valorAbono, "
                + "abonosfacturas.registradoPor from abonosfacturas inner join facturas on "
                + "abonosfacturas.idFactura=facturas.idFactura inner join clientes on "
                + "facturas.idCliente=clientes.idCliente where abonosfacturas.fechaAbonoSistema "
                + "between ? and ?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaDesde);
            pst.setString(2, fechaHasta);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String[] abono = new String[7];
                abono[0] = rs.getString("abonosfacturas.idAbono");
                abono[1] = rs.getString("abonosfacturas.idFactura");
                abono[2] = rs.getString("abonosfacturas.fechaAbonoSistema");
                abono[3] = rs.getString("clientes.nombreCliente");
                abono[4] = rs.getString("abonosfacturas.observaciones");
                abono[5] = rs.getString("abonosfacturas.valorAbono");
                abono[6] = rs.getString("abonosfacturas.registradoPor");

                listadoAbonosFacturas.add(abono);
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el listado de abonos de facturas\n" + e);
        }

        return listadoAbonosFacturas;
    }

    public void RegistrarPresupuesto(String descripcion, String fechaIni, String fechaFin, double utilidad, String[] infoPresup, int presupAnterior) {

        String consulta = "insert into presupuestos (fecha, descripcion, estado, fechaInicio, fechaFin, registradoPor) values (?, ?, ?, ?, ?, ?)";
        String consulta2 = "update presupuestos set estado='CERRADO'";
        String registrarPartida = "insert into partidaspresupuestos (fecha, idPresupuesto, concepto, valor, registradoPor)\n"
                + "values (?, ?, ?, ?, ?);";

        Connection cn = Conexion.Conectar();

        try {

            cn.setAutoCommit(false);

            PreparedStatement pst2 = cn.prepareStatement(consulta2);
            pst2.executeUpdate();

            PreparedStatement pst = cn.prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            pst.setString(2, descripcion);
            pst.setString(3, "ABIERTO");
            pst.setString(4, fechaIni);
            pst.setString(5, fechaFin);
            pst.setString(6, this.usuario);
            pst.executeUpdate();
            ResultSet idGenerado = pst.getGeneratedKeys();
            idGenerado.next();
            int idPresup = idGenerado.getInt(1);

            PreparedStatement pst3 = cn.prepareStatement(registrarPartida);
            pst3.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            pst3.setInt(2, idPresup);
            pst3.setString(3, "UTILIDAD DEL PERIODO ANTERIOR " + presupAnterior + " - " + infoPresup[0]);
            pst3.setDouble(4, utilidad);
            pst3.setString(5, this.usuario);
            pst3.executeUpdate();

            cn.commit();
            cn.close();

            JOptionPane.showMessageDialog(this, "Presupuesto y utilidad registrados", "Informacion", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQLException\nError en registrar el presupuesto. Contacte al administrador", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public void RegistrarPresupuestoProvisional(String descripcion, String fechaIni, String fechaFin, double utilidad, String[] infoPresup, int presupAnterior) {

        String consulta = "insert into presupuestos (fecha, descripcion, estado, fechaInicio, fechaFin, registradoPor) values (?, ?, ?, ?, ?, ?)";
        String consulta2 = "update presupuestos set estado='CERRADO'";
        String registrarPartida = "insert into partidaspresupuestos (fecha, idPresupuesto, concepto, valor, registradoPor)\n"
                + "values (?, ?, ?, ?, ?);";

        Connection cn = Conexion.Conectar();

        try {

            cn.setAutoCommit(false);

            PreparedStatement pst2 = cn.prepareStatement(consulta2);
            pst2.executeUpdate();

            PreparedStatement pst = cn.prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            pst.setString(2, descripcion);
            pst.setString(3, "ABIERTO");
            pst.setString(4, fechaIni);
            pst.setString(5, fechaFin);
            pst.setString(6, this.usuario);
            pst.executeUpdate();
            ResultSet idGenerado = pst.getGeneratedKeys();
            idGenerado.next();
            int idPresup = idGenerado.getInt(1);

            PreparedStatement pst3 = cn.prepareStatement(registrarPartida);
            pst3.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            pst3.setInt(2, idPresup);
            pst3.setString(3, "(PROVISIONAL) UTILIDAD DEL PERIODO ANTERIOR " + presupAnterior + " - " + infoPresup[0]);
            pst3.setDouble(4, utilidad);
            pst3.setString(5, this.usuario);
            pst3.executeUpdate();

            cn.commit();
            cn.close();

            JOptionPane.showMessageDialog(this, "Presupuesto y utilidad provisional registrados", "Informacion", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQLException\nError en registrar el presupuesto. Contacte al administrador", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public String[] consultarDatosPresupuesto(String idPresupuesto) {

        String consulta = "select descripcion, fechaInicio, fechaFin, registradoPor from presupuestos "
                + "where idPresupuesto=?";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            ResultSet rs = pst.executeQuery();

            String[] datosPresupuesto = new String[4];
            if (rs.next()) {
                datosPresupuesto[0] = rs.getString("descripcion");
                datosPresupuesto[1] = rs.getString("fechaInicio");
                datosPresupuesto[2] = rs.getString("fechaFin");
                datosPresupuesto[3] = rs.getString("registradoPor");
            }

            cn.close();
            return datosPresupuesto;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer los datos del presupuesto", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Object[]> consolidarPresupuesto(String idPresupuesto) {

        ArrayList<Object[]> listado = new ArrayList<>();

        String consulta = "select i.idGasto, t.idGasto, t.tipoGasto, ma.descripcion, r.IdRubro, r.Descripcion,\n"
                + "mo.Descripcion, i.valorPresupuestado, ifnull(sum(g.valor), 0) as gastado\n"
                + "from itemspresupuesto i left join gastospresupuestos g on i.idGasto=g.idConcepto and g.estado='Registrado'  and g.idPrespuesto=?\n"
                + "left join maestrogastos ma on i.idGasto=ma.id \n"
                + "left join tipogastos t on ma.idGasto=t.idGasto\n"
                + "left join modalidadgasto mo on ma.idModalidad=mo.idModalidad\n"
                + "left join rubros r on ma.idRubro=r.IdRubro\n"
                + "where i.idPresupuesto=?\n"
                + "group by i.idGasto \n"
                + "order by t.idGasto asc, r.IdRubro asc";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            pst.setString(2, idPresupuesto);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] elemento = new Object[6];
                elemento[0] = rs.getDouble("i.idGasto");
                elemento[1] = rs.getString("t.tipoGasto");
                elemento[2] = rs.getString("ma.descripcion");
                elemento[3] = rs.getString("mo.Descripcion");
                elemento[4] = rs.getDouble("i.valorPresupuestado");
                elemento[5] = rs.getDouble("gastado");

                listado.add(elemento);
            }

            cn.close();

            return listado;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al leer los items del presupuesto " + idPresupuesto, "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Object[]> consultarPartidas(String idPresupuesto) {

        ArrayList<Object[]> listado = new ArrayList<>();
        String consulta = "select id, fecha, concepto, valor, registradoPor from partidaspresupuestos "
                + "where idPresupuesto=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                Object[] elemento = new Object[5];
                elemento[0] = rs.getDouble("id");
                elemento[1] = rs.getDate("fecha");
                elemento[2] = rs.getString("concepto");
                elemento[3] = rs.getDouble("valor");
                elemento[4] = rs.getString("registradoPor");

                listado.add(elemento);
            }

            cn.close();

            return listado;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer las partidas del presupuesto " + idPresupuesto, "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Object[]> consultarGastos(String idPresupuesto) {

        ArrayList<Object[]> listado = new ArrayList<>();
        String consulta = "select g.fechaGasto, m.descripcion, g.observaciones, g.factura, g.valor, g.registradoPor\n"
                + "from gastospresupuestos g join maestrogastos m on g.idConcepto=m.id\n"
                + "where g.estado='Registrado' and idPrespuesto=? \n"
                + "order by g.fechaGasto asc";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                Object[] elemento = new Object[6];
                elemento[0] = rs.getDate("g.fechaGasto");
                elemento[1] = rs.getString("m.descripcion");
                elemento[2] = rs.getString("g.observaciones");
                elemento[3] = rs.getString("g.factura");
                elemento[4] = rs.getDouble("g.valor");
                elemento[5] = rs.getString("g.registradoPor");

                listado.add(elemento);
            }

            cn.close();

            return listado;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer los gastos del presupuesto " + idPresupuesto, "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public double consultarGastosNoAutorizados(String idPresupuesto) {

        ArrayList<Object[]> listado = new ArrayList<>();
        String consulta = "select sum(valor) as total from gastospresupuestos where idPrespuesto=? and estado='Por Autorizar'";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                return rs.getDouble("total");
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer los gastos del presupuesto " + idPresupuesto, "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return 1;
    }

    public ArrayList<Object[]> consultarIngresosEntradasDiarias(String fechaInicial, String fechaFinal) {

        ArrayList<Object[]> listado = new ArrayList<>();
        String consulta = "select a.idAbono, a.idVenta, v.FechaventaSistema, v.descripcionTrabajo, a.fecha, c.nombreCliente, a.observaciones, a.valor, "
                + "a.registradoPor    \n"
                + "from abonos a join ventas v on a.idVenta=v.Idventa and a.estado='Activo'\n"
                + "join clientes c on v.Idcliente=c.idCliente\n"
                + "where a.fecha between ? and ?\n"
                + "order by a.idAbono";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaInicial);
            pst.setString(2, fechaFinal);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                Object[] elemento = new Object[10];

                elemento[0] = rs.getDouble("a.idAbono");
                elemento[1] = rs.getDouble("a.idVenta");
                elemento[2] = rs.getString("v.descripcionTrabajo");
                elemento[3] = rs.getDate("a.fecha");
                elemento[4] = rs.getString("c.nombreCliente");
                elemento[5] = rs.getString("a.observaciones");
                elemento[6] = rs.getDouble("a.valor");
                elemento[7] = rs.getString("a.registradoPor");
                elemento[9] = rs.getDate("v.FechaventaSistema");

                if (((Date) elemento[9]).before(new SimpleDateFormat("yyyy-MM-dd").parse(fechaInicial))) {
                    elemento[8] = "Ingresos meses anteriores";
                } else {
                    elemento[8] = "";
                }

                listado.add(elemento);
            }

            cn.close();

            return listado;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer los ingresos por entradas diarias", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Error al parsear la fecha", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        return null;
    }

    public ArrayList<Object[]> consultarIngresosFacturas(String fechaInicial, String fechaFinal) {

        ArrayList<Object[]> listado = new ArrayList<>();
        String consulta = "select a.idAbono, a.factura, a.fecha, c.nombreCliente, a.observaciones, a.abono, "
                + "a.registradoPor, f.fechaFactura \n"
                + "from abonosfacturas a left join facturas f on a.factura=f.idFactura and a.estado='Activo'\n"
                + "left join clientes c on f.idCliente=c.idCliente\n"
                + "where a.fecha between ? and ?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaInicial);
            pst.setString(2, fechaFinal);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                Object[] elemento = new Object[9];

                elemento[0] = rs.getDouble("a.idAbono");
                elemento[1] = rs.getDouble("a.factura");
                elemento[2] = rs.getDate("a.fecha");
                elemento[3] = rs.getString("c.nombreCliente");
                elemento[4] = rs.getString("a.observaciones");
                elemento[5] = rs.getDouble("a.abono");
                elemento[6] = rs.getString("a.registradoPor");
                elemento[8] = rs.getDate("f.fechaFactura");
                
                if (((Date)elemento[8]).before(new SimpleDateFormat("yyyy-MM-dd").parse(fechaInicial))) {
                    elemento[7]="Ingresos meses anteriores";
                } else {
                    elemento[7]="";
                }
                
                listado.add(elemento);
            }

            cn.close();

            return listado;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer los ingresos por entradas diarias", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Error al parsear la fecha inicial de ingresos facturas","Error",JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        return null;
    }

    public ArrayList<Object[]> consultarListadoGastosNoAutorizados(String idPresupuesto) {

        ArrayList<Object[]> listado = new ArrayList<>();

        String consulta = "select g.fechaGasto, m.descripcion, g.observaciones, g.factura, g.valor, g.registradoPor\n"
                + "from gastospresupuestos g join maestrogastos m on g.idConcepto=m.id\n"
                + "where g.estado='Por Autorizar' and idPrespuesto=?\n"
                + "order by g.fechaGasto asc";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[7];
                nuevo[0] = new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("g.fechaGasto"));
                nuevo[1] = rs.getString("m.descripcion");
                nuevo[2] = rs.getString("g.observaciones");
                nuevo[3] = rs.getString("g.factura");
                nuevo[4] = rs.getDouble("g.valor");
                nuevo[5] = rs.getString("g.registradoPor");
                nuevo[6] = "GASTO PENDIENTE DE AUTORIZACION";

                listado.add(nuevo);
            }

            return listado;

        } catch (SQLException | ParseException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los gastos no autorizados", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public void GenerarInformeEconomico(String idPresupuesto, String[] datosPresupuesto, ArrayList<Object[]> itemsPresupusto,
            ArrayList<Object[]> partidas, ArrayList<Object[]> gastos, ArrayList<Object[]> ingresosEntradasDiarias,
            ArrayList<Object[]> ingresosFacturas, double gastosNoAutorizados, ArrayList<Object[]> listaGastosNoAutorizados) {

        double totalPartidad = 0;
        double totalGastos = 0;
        double totalEntradasDiarias = 0;
        double totalFacturas = 0;
        double totalGastosParcial = 0;
        //Llenamos el archivo excel
        //String rutaPlantilla = "Docs" + File.separator + "Informe economico.xlsx";
        String rutaPlantilla = "C:" + File.separator + "Gestion" + File.separator + "Docs"
                + File.separator + "Informe economico.xlsx";
        String rutaAGuardar = System.getProperty("user.home") + File.separator + "Desktop"
                + File.separator + "Informe economico " + datosPresupuesto[0] + ".xlsx";

        try {
//********************************************************************************************************
            FileInputStream plantilla = new FileInputStream(rutaPlantilla);
            XSSFWorkbook wb = new XSSFWorkbook(plantilla);
            plantilla.close();
            XSSFSheet hoja = wb.getSheetAt(0);
            //Datos fila 1
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda10 = fila1.getCell(0);
            celda10.setCellValue("Desde " + datosPresupuesto[1] + " hasta " + datosPresupuesto[2] + " - " + datosPresupuesto[0]);
            //Datos fila 3
            XSSFRow fila3 = hoja.getRow(3);
            XSSFCell celda31 = fila3.getCell(1);
            celda31.setCellValue(new Date());
            //Datos fila 4
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda41 = fila4.getCell(1);
            celda41.setCellValue(this.usuario);

            //Llenamos los valores de la tabla
            //Los datos empiezan a cargarse desde la fila 15
            int filaInicial = 16;
//          int sumaGastos = 0;
            double sumaPresupuesto = 0;
            double consecutivo = 1;
            for (Object[] elemento : itemsPresupusto) {
                XSSFRow fila = hoja.getRow(filaInicial);
                for (int j = 0; j < elemento.length; j++) {
                    XSSFCell celda = fila.getCell(j);
                    switch (j) {
                        case 0:
                            celda.setCellValue(consecutivo);
                            consecutivo++;
                            break;
                        case 1:
                            celda.setCellValue((String) elemento[j]);
                            break;
                        case 2:
                            celda.setCellValue((String) elemento[j]);
                            break;
                        case 3:
                            celda.setCellValue((String) elemento[j]);
                            break;
                        case 4:
                            celda.setCellValue((Double) elemento[j]);
                            sumaPresupuesto += (Double) elemento[j];
                            break;
                        case 5:
                            celda.setCellValue((Double) elemento[j]);
                            totalGastosParcial += (Double) elemento[j];
                            break;
                    }
                }
                filaInicial++;
            }
            filaInicial++;
            //System.out.println("Valor = $"+sumaPresupuesto);
            //Agregamos los totales al final de la tabla
            XSSFRow filatotales = hoja.getRow(filaInicial);
            XSSFCell celdaTextoSubt = filatotales.getCell(3);
            celdaTextoSubt.setCellValue("SUBTOTAL");
            XSSFCell celdaSubGastos = filatotales.getCell(5);
            celdaSubGastos.setCellValue(totalGastosParcial);
            XSSFCell celdaSubPresup = filatotales.getCell(4);
            celdaSubPresup.setCellValue((Double) sumaPresupuesto);

            //********************************************HOJA 2
            //Agregamos los datos de la hoja partidas
            XSSFSheet hoja2 = wb.getSheetAt(1);

            //Datos fila1 hoja 2
            XSSFRow fila1Hoja2 = hoja2.getRow(1);
            XSSFCell celda10Hoja2 = fila1Hoja2.getCell(0);
            celda10Hoja2.setCellValue("Desde " + datosPresupuesto[1] + " hasta " + datosPresupuesto[2] + " - " + datosPresupuesto[0]);

            //Datos fila3 hoja 2
            XSSFRow fila3Hoja2 = hoja2.getRow(3);
            XSSFCell celda31Hoja2 = fila3Hoja2.getCell(1);
            celda31Hoja2.setCellValue(new Date());

            //Datos fila4 hoja 2
            XSSFRow fila4Hoja2 = hoja2.getRow(4);
            XSSFCell celda41Hoja2 = fila4Hoja2.getCell(1);
            celda41Hoja2.setCellValue(this.usuario);

            //Datos de la tabla empiezan en la fila 7
            int filaInicio = 7;

            for (Object[] elemento : partidas) {
                XSSFRow filaHoja2 = hoja2.getRow(filaInicio);
                for (int j = 0; j < elemento.length; j++) {
                    XSSFCell celdaHoja2 = filaHoja2.getCell(j);

                    switch (j) {
                        case 0:
                            celdaHoja2.setCellValue((Double) elemento[j]);
                            break;
                        case 1:
                            celdaHoja2.setCellValue((Date) elemento[j]);
                            break;
                        case 2:
                            celdaHoja2.setCellValue((String) elemento[j]);
                            break;
                        case 3:
                            celdaHoja2.setCellValue((Double) elemento[j]);
                            totalPartidad += (Double) elemento[j];
                            break;
                        case 4:
                            celdaHoja2.setCellValue((String) elemento[j]);
                            break;
                    }

                }
                filaInicio++;
            }

            filaInicio++;
            //Llenamos los datos de subtotal
            XSSFRow filatotalesHoja2 = hoja2.getRow(4);
            XSSFCell celdaTextoSubtHoja2 = filatotalesHoja2.getCell(4);
            celdaTextoSubtHoja2.setCellValue(totalPartidad);

            //Completamos el dato del resumen de la primera hoja que se calcularon en la segunda hoja
            //Fila 7
            XSSFRow fila7 = hoja.getRow(7);
            XSSFCell celda75 = fila7.getCell(5);
            celda75.setCellValue(totalPartidad);

            //********************************************HOJA 2            
            //Cargamos los datos de las hoja 3 (Gastos)
            XSSFSheet hoja3 = wb.getSheetAt(2);

            //Llenamos los valores de la fila 2
            XSSFRow fila1Hoja3 = hoja3.getRow(1);
            XSSFCell celda10Hoja3 = fila1Hoja3.getCell(0);
            celda10Hoja3.setCellValue("Desde " + datosPresupuesto[1] + " hasta " + datosPresupuesto[2] + " - " + datosPresupuesto[0]);

            //Llenamos los valores de la fila 4
            XSSFRow fila3Hoja3 = hoja3.getRow(3);
            XSSFCell celda31Hoja3 = fila3Hoja3.getCell(1);
            celda31Hoja3.setCellValue(new Date());

            //Llenamos los valores de la fila 5
            XSSFRow fila4Hoja3 = hoja3.getRow(4);
            XSSFCell celda41Hoja3 = fila4Hoja3.getCell(1);
            celda41Hoja3.setCellValue(this.usuario);

            XSSFCell celda45Hoja3 = fila4Hoja3.getCell(5);
            celda45Hoja3.setCellValue(gastosNoAutorizados);

            //Llenamos los datos de la tabla
            int filaInicioHoja3 = 8;

            for (Object[] gasto : gastos) {
                XSSFRow filaTablaHoja3 = hoja3.getRow(filaInicioHoja3);
                for (int j = 0; j < gasto.length; j++) {
                    XSSFCell celdaTablaHoja3 = filaTablaHoja3.getCell(j);
                    switch (j) {
                        case 0:
                            celdaTablaHoja3.setCellValue((Date) gasto[j]);
                            break;
                        case 1:
                            celdaTablaHoja3.setCellValue((String) gasto[j]);
                            break;

                        case 2:
                            celdaTablaHoja3.setCellValue((String) gasto[j]);
                            break;

                        case 3:
                            celdaTablaHoja3.setCellValue((String) gasto[j]);
                            break;

                        case 4:
                            celdaTablaHoja3.setCellValue((Double) gasto[j]);
                            totalGastos += (Double) gasto[j];
                            break;

                        case 5:
                            celdaTablaHoja3.setCellValue((String) gasto[j]);
                            break;

                        case 6:
                            celdaTablaHoja3.setCellValue((String) gasto[j]);
                            break;

                    }
                }
                filaInicioHoja3++;

            }
            //Cargamos en la misma hoja de gastos, los gastos no autorizados

            for (Object[] gasto : listaGastosNoAutorizados) {
                XSSFRow filaTablaHoja3 = hoja3.getRow(filaInicioHoja3);
                for (int j = 0; j < gasto.length; j++) {
                    XSSFCell celdaTablaHoja3 = filaTablaHoja3.getCell(j);
                    switch (j) {
                        case 0:
                            celdaTablaHoja3.setCellValue((Date) gasto[j]);
                            break;
                        case 1:
                            celdaTablaHoja3.setCellValue((String) gasto[j]);
                            break;

                        case 2:
                            celdaTablaHoja3.setCellValue((String) gasto[j]);
                            break;

                        case 3:
                            celdaTablaHoja3.setCellValue((String) gasto[j]);
                            break;

                        case 4:
                            celdaTablaHoja3.setCellValue((Double) gasto[j]);
                            break;

                        case 5:
                            celdaTablaHoja3.setCellValue((String) gasto[j]);
                            break;

                        case 6:
                            celdaTablaHoja3.setCellValue((String) gasto[j]);
                            break;

                    }
                }
                filaInicioHoja3++;

            }

            //Datos fila 11 hoja 1
            XSSFRow fila11 = hoja.getRow(11);
            XSSFCell celda115 = fila11.getCell(5);
            celda115.setCellValue(totalGastos);

            // Completamos el total de gastos en la hoja uno y la hoja 3
            XSSFCell celda46Hoja3 = fila3Hoja3.getCell(5);
            celda46Hoja3.setCellValue(totalGastos);
            celda115.setCellValue(totalGastos);

            //******completamos en la cabecera de la hoja1 los gastos no autorizados
            XSSFRow fila12Hoja1 = hoja.getRow(12);
            XSSFCell celda125Hoja1 = fila12Hoja1.getCell(5);
            celda125Hoja1.setCellValue(gastosNoAutorizados);

            //Completamos los campos de la hoja detalle de entradas diarias
            XSSFSheet hoja4 = wb.getSheetAt(3);
            XSSFRow fila1Hoja4 = hoja4.getRow(1);
            XSSFCell celda10Hoja4 = fila1Hoja4.getCell(0);
            celda10Hoja4.setCellValue("Desde " + datosPresupuesto[1] + " hasta " + datosPresupuesto[2] + " - " + datosPresupuesto[0]);

            XSSFRow fila3Hoja4 = hoja4.getRow(3);
            XSSFCell celda31Hoja4 = fila3Hoja4.getCell(1);
            celda31Hoja4.setCellValue(new Date());

            XSSFRow fila4Hoja4 = hoja4.getRow(4);
            XSSFCell celda41Hoja4 = fila4Hoja4.getCell(1);
            celda41Hoja4.setCellValue(this.usuario);

            //Completamos la tabla de datos
            int filaInicialHoja4 = 9;
            int sumaAbonos = 0;
            double ingresosMes = 0;
            double ingresosMesesAnteriores = 0;

            for (Object[] entrada : ingresosEntradasDiarias) {
                XSSFRow primeraFilaTablaHoja4 = hoja4.getRow(filaInicialHoja4);
                for (int j = 0; j < entrada.length; j++) {
                    XSSFCell primeraCeldaTablaHoja4 = primeraFilaTablaHoja4.getCell(j);

                    switch (j) {
                        case 0:
                            primeraCeldaTablaHoja4.setCellValue((Double) entrada[j]);
                            break;
                        case 1:
                            primeraCeldaTablaHoja4.setCellValue((Double) entrada[j]);
                            break;
                        case 2:
                            primeraCeldaTablaHoja4.setCellValue((String) entrada[j]);
                            break;
                        case 3:
                            primeraCeldaTablaHoja4.setCellValue((Date) entrada[j]);
                            break;
                        case 4:
                            primeraCeldaTablaHoja4.setCellValue((String) entrada[j]);
                            break;
                        case 5:
                            primeraCeldaTablaHoja4.setCellValue((String) entrada[j]);
                            break;
                        case 6:
                            primeraCeldaTablaHoja4.setCellValue((Double) entrada[j]);
                            totalEntradasDiarias += (Double) entrada[j];
                            break;
                        case 7:
                            primeraCeldaTablaHoja4.setCellValue((String) entrada[j]);
                            break;
                        case 8:
                            primeraCeldaTablaHoja4.setCellValue((String) entrada[j]);

                            if (((String) entrada[j]).equals("Ingresos meses anteriores")) {
                                ingresosMesesAnteriores += (Double) entrada[6];
                            } else {
                                ingresosMes += (Double) entrada[6];
                            }

                            break;

                    }
                }
                filaInicialHoja4++;
            }

            //Ponemos el valor de abonos del mes en  la hoja 4
            XSSFCell celda46Hoja4 = fila4Hoja4.getCell(6);
            celda46Hoja4.setCellValue(ingresosMes);

            //Ponemos el valor de abonos de meses anteriores en  la hoja 4
            XSSFRow fila5Hoja4 = hoja4.getRow(5);
            XSSFCell celda56Hoja4 = fila5Hoja4.getCell(6);
            celda56Hoja4.setCellValue(ingresosMesesAnteriores);

            //Ponemos el valor total de abonos del mes en  la hoja 4
            XSSFRow fila6Hoja4 = hoja4.getRow(6);
            XSSFCell celda66Hoja4 = fila6Hoja4.getCell(6);
            celda66Hoja4.setCellValue(totalEntradasDiarias);

            //Ponemos el valor total de abono en la hoja 1 (resumen)
            XSSFRow fila8Hoja1 = hoja.getRow(8);
            XSSFCell celda85Hoja1 = fila8Hoja1.getCell(5);
            celda85Hoja1.setCellValue(totalEntradasDiarias);

            //Completamos los datos de la hoja Ingreso de empresas
            XSSFSheet hoja5 = wb.getSheetAt(4);

            XSSFRow fila1Hoja5 = hoja5.getRow(1);
            XSSFCell celda10Hoja5 = fila1Hoja5.getCell(0);
            celda10Hoja5.setCellValue("Desde " + datosPresupuesto[1] + " hasta " + datosPresupuesto[2] + " - " + datosPresupuesto[0]);

            XSSFRow fila3Hoja5 = hoja5.getRow(3);
            XSSFCell celda31Hoja5 = fila3Hoja5.getCell(1);
            celda31Hoja5.setCellValue(new Date());

            XSSFRow fila4Hoja5 = hoja5.getRow(4);
            XSSFCell celda41Hoja5 = fila4Hoja5.getCell(1);
            celda41Hoja5.setCellValue(this.usuario);

            //Llenamos los datos de la tabla abonos facturas
            int filaInicialAbonosFactura = 9;
            int sumaAbonosFacturas = 0;
            double ingresosMesFacturas = 0;
            double ingresosMesesAnterioresFacturas = 0;

            for (Object[] factura : ingresosFacturas) {
                XSSFRow primeraFilaTablaHoja5 = hoja5.getRow(filaInicialAbonosFactura);
                for (int j = 0; j < factura.length; j++) {
                    XSSFCell primeraCeldaTablaHoja5 = primeraFilaTablaHoja5.getCell(j);

                    switch (j) {
                        case 0:
                            primeraCeldaTablaHoja5.setCellValue((Double) factura[j]);
                            break;
                        case 1:
                            primeraCeldaTablaHoja5.setCellValue((Double) factura[j]);
                            break;
                        case 2:
                            primeraCeldaTablaHoja5.setCellValue((Date) factura[j]);
                            break;
                        case 3:
                            primeraCeldaTablaHoja5.setCellValue((String) factura[j]);
                            break;
                        case 4:
                            primeraCeldaTablaHoja5.setCellValue((String) factura[j]);
                            break;
                        case 5:
                            primeraCeldaTablaHoja5.setCellValue((Double) factura[j]);
                            totalFacturas += (Double) factura[j];
                            break;
                        case 6:
                            primeraCeldaTablaHoja5.setCellValue((String) factura[j]);
                            break;                            
                        case 7:
                            primeraCeldaTablaHoja5.setCellValue((String) factura[j]);
                            
                            if (((String) factura[j]).equals("Ingresos meses anteriores")) {
                                ingresosMesesAnterioresFacturas+=(Double) factura[5];
                            } else {
                                ingresosMesFacturas+=(Double) factura[5];
                            }
                                                        
                            break;

                    }
                }
                filaInicialAbonosFactura++;
            }

            //ingresos del mes
            XSSFCell celda45Hoja5 = fila4Hoja5.getCell(5);
            celda45Hoja5.setCellValue(ingresosMesFacturas);
            
            //Ingresos de meses anteriores
            XSSFRow fila5Hoja5 = hoja5.getRow(5);
            XSSFCell celda55Hoja5 = fila5Hoja5.getCell(5);
            celda55Hoja5.setCellValue(ingresosMesesAnterioresFacturas);
            
            //total ingresos del periodo
            
            XSSFRow fila6Hoja5 = hoja5.getRow(6);
            XSSFCell celda65Hoja5 = fila6Hoja5.getCell(5);
            celda65Hoja5.setCellValue(totalFacturas);

            //Ponemos el valor total de abonos por factura en el resumen de la hoja 1
            XSSFRow fila9Hoja1 = hoja.getRow(9);
            XSSFCell celda95Hoja1 = fila9Hoja1.getCell(5);
            celda95Hoja1.setCellValue(totalFacturas);

            //Completamos la fila total efectivo que resulta de sumar las partidas y los ingresos por entradas diarias y empresas
            XSSFRow fila10Hoja1 = hoja.getRow(10);
            XSSFCell celda105Hoja1 = fila10Hoja1.getCell(5);
            celda105Hoja1.setCellValue(totalEntradasDiarias + totalFacturas + totalPartidad);

            //Completamos la ganancia que es la resta de total efectivo menos gastos
            XSSFRow fila13Hoja1 = hoja.getRow(13);
            XSSFCell celda135Hoja1 = fila13Hoja1.getCell(5);
            celda135Hoja1.setCellValue((double) (totalEntradasDiarias + totalFacturas + totalPartidad - totalGastos - gastosNoAutorizados));

            //***********************************
            FileOutputStream nuevo = new FileOutputStream(rutaAGuardar);
            wb.write(nuevo);
            nuevo.close();
            JOptionPane.showMessageDialog(this, "Informe generado");

            MetodosGenerales.abrirArchivo(rutaAGuardar);
            MetodosGenerales.enviarEmailConAdjunto("Envío automático: Informe economico "
                    + datosPresupuesto[0], "Informe enviado automaticamente por Software Gestion",
                    System.getProperty("user.home") + File.separator + "Desktop"
                    + File.separator + "Informe economico " + datosPresupuesto[0] + ".xlsx", "Informe economico " + datosPresupuesto[0] + ".xlsx");

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error en leer la plantilla y generar el informe. Es posible que tenga un archivo con el mismo nombre abierto. "
                    + "\nCierrelo e intente generar el informe nuevamente" + e, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error en generar el archivo\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

    }

    public Integer consultarMaxPresup() {

        String consulta = "select max(idPresupuesto) from presupuestos";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getInt("max(idPresupuesto)");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el presupuesto anterior", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public Date consultarFechaProximoPresupuesto(int presup) {

        String consulta = "select fechaFin from presupuestos where idPresupuesto=?";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, presup);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getDate("fechaFin");
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar la fecha inicial del nuevo presupuesto", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public boolean comprobarGastos() {

        int presup = consultarMaxPresup();

        String consulta = "select * from gastospresupuestos where idPrespuesto=? and estado='Por Autorizar'";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, presup);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return false;
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al comprobar gastos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return true;
    }

    public Double consultarSumaPartidas(int presupuesto) {

        String consulta = "select ifnull(sum(valor), 0) as total from partidaspresupuestos where idPresupuesto=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, presupuesto);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer las partida", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public Double consultarSumaIngresosEntradasDiarias(String fechaInicial, String fechaFinal) {

        String consulta = "select ifnull(sum(valor),0) as total from abonos where fecha between ? and ? and estado='Activo'";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaInicial);
            pst.setString(2, fechaFinal);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                return rs.getDouble("total");
            }

            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al leer los ingresos por entradas diarias", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public Double consultarSumaIngresosFacturas(String fechaInicial, String fechaFinal) {

        String consulta = "select ifnull(sum(abono),0) as total from abonosfacturas where fecha between ? and ? and estado='Activo'";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaInicial);
            pst.setString(2, fechaFinal);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                return rs.getDouble("total");
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer los ingresos por facturas", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;

    }

    public Double consultarSumaGastos(int presupuesto) {

        String consulta = "select ifnull(sum(valor),0) as total from gastospresupuestos where idPrespuesto=? and estado='Registrado'";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, presupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                return rs.getDouble("total");
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer los gastos", "Error", JOptionPane.ERROR_MESSAGE);
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
        jTable_Presupuestos = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel_fila = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_NumeroPresup = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_descripcion = new javax.swing.JTextField();
        jButton_cargarRubros = new javax.swing.JButton();
        jButton_agregarPartida = new javax.swing.JButton();
        jButton_editarRubros = new javax.swing.JButton();
        jButton_agregarGastos = new javax.swing.JButton();
        jComboBox_estado = new javax.swing.JComboBox<>();
        jButton_cambiarEstado = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jDateChooser_ini = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jDateChooser_fin = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jTextField_descrippresup = new javax.swing.JTextField();
        jButton_crear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_Presupuestos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Presup.", "Fecha", "Descripcion", "Desde", "Hasta", "Presupuest.", "Gastado", "Estado", "Registrado por"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_Presupuestos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_Presupuestos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_PresupuestosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_Presupuestos);
        if (jTable_Presupuestos.getColumnModel().getColumnCount() > 0) {
            jTable_Presupuestos.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable_Presupuestos.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable_Presupuestos.getColumnModel().getColumn(2).setPreferredWidth(250);
            jTable_Presupuestos.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTable_Presupuestos.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTable_Presupuestos.getColumnModel().getColumn(5).setPreferredWidth(110);
            jTable_Presupuestos.getColumnModel().getColumn(6).setPreferredWidth(110);
            jTable_Presupuestos.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTable_Presupuestos.getColumnModel().getColumn(8).setPreferredWidth(120);
        }

        jLabel_fila.setText("jLabel4");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Gestionar presupuesto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Presupuesto");

        jLabel3.setText("Descripcion");

        jButton_cargarRubros.setText("Cargar rubros");
        jButton_cargarRubros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cargarRubrosActionPerformed(evt);
            }
        });

        jButton_agregarPartida.setText("Agr. partida");
        jButton_agregarPartida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregarPartidaActionPerformed(evt);
            }
        });

        jButton_editarRubros.setText("Ver detalle");
        jButton_editarRubros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_editarRubrosActionPerformed(evt);
            }
        });

        jButton_agregarGastos.setText("Agr. gasto");
        jButton_agregarGastos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregarGastosActionPerformed(evt);
            }
        });

        jComboBox_estado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ABIERTO", "CERRADO" }));

        jButton_cambiarEstado.setText("Camb. estado");
        jButton_cambiarEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cambiarEstadoActionPerformed(evt);
            }
        });

        jButton1.setText("Inf. Economico");
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_NumeroPresup, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox_estado, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton_cambiarEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(200, 200, 200))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton_cargarRubros, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_editarRubros, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_agregarGastos, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_agregarPartida, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_NumeroPresup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox_estado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_cambiarEstado))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton_cargarRubros)
                        .addComponent(jButton_agregarGastos)
                        .addComponent(jButton_agregarPartida)
                        .addComponent(jButton_editarRubros)))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Crear presupuesto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel4.setText("F. Inic");

        jLabel5.setText("F. Fin");

        jLabel6.setText("Descripcion");

        jTextField_descrippresup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_descrippresupKeyTyped(evt);
            }
        });

        jButton_crear.setText("Crear");
        jButton_crear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_crearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField_descrippresup)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jDateChooser_ini, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jDateChooser_fin, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 19, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jDateChooser_ini, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jDateChooser_fin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField_descrippresup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_crear))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(420, 420, 420)
                        .addComponent(jLabel_fila)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(151, 151, 151)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel_fila)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_PresupuestosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_PresupuestosMouseClicked
        int fila = jTable_Presupuestos.getSelectedRow();
        if (fila != -1) {
            jTextField_NumeroPresup.setText(jTable_Presupuestos.getValueAt(fila, 0).toString());
            jTextField_descripcion.setText(jTable_Presupuestos.getValueAt(fila, 2).toString());
            jLabel_fila.setText(String.valueOf(fila));
            jComboBox_estado.setSelectedItem(jTable_Presupuestos.getValueAt(fila, 7).toString());

        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una fila");
        }
    }//GEN-LAST:event_jTable_PresupuestosMouseClicked

    private void jButton_cargarRubrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cargarRubrosActionPerformed

        String idPresupuesto = jTextField_NumeroPresup.getText().trim();
        String descripcionPresupuesto = jTextField_descripcion.getText().trim();
        //Verificamos que se haya seleccionado un presupuesto
        if (!idPresupuesto.equals("")) {
            //Verificamos que el presupuesto no tenga ya cargados sus rubros
            //Integer.parseInt(jTable_Presupuestos.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 5).toString()) > 0
            if (Integer.parseInt(ConvertirMonedaAInt(jTable_Presupuestos.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 5).toString())) > 0) {
                JOptionPane.showMessageDialog(this, "El presupuesto seleccionado ya tiene rubros cargados, para "
                        + "editarlos o adicionar rubros vaya a la opcion Ver Detalle.\nNota: Solo el Gerente "
                        + "puede editar los presupuestos", "Informacion", JOptionPane.INFORMATION_MESSAGE);

            } else {
                dispose();

                Frame[] ventanas = getFrames();

                for (Frame ventana : ventanas) {
                    if (ventana instanceof CargarRubrosPresupuesto) {
                        ventana.dispose();
                    }
                }

                new CargarRubrosPresupuesto(this.usuario, this.permiso, idPresupuesto, descripcionPresupuesto).setVisible(true);

            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un presupuesto", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton_cargarRubrosActionPerformed

    private void jButton_agregarPartidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregarPartidaActionPerformed
        //Verificamos que se haya seleccionado un presupuesto
        String idpresupuesto = jTextField_NumeroPresup.getText().trim();

        if (!idpresupuesto.equals("")) {
            //Verificamos que el presupuesto este abierto
            if (jTable_Presupuestos.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 7).toString().equals("ABIERTO")) {
                //Capturamos los datos faltantes
                String descripPresupuesto = jTextField_descripcion.getText().trim();

                Frame[] ventanas = getFrames();

                for (Frame ventana : ventanas) {
                    if (ventana instanceof AgregarDineroPresupuesto) {
                        ventana.dispose();
                    }
                }

                new AgregarDineroPresupuesto(this.usuario, this.permiso, idpresupuesto, descripPresupuesto).setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "Solo se pueden agregar partidas a presupuestos cuyo estado sea: Abierto", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione el presupuesto", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton_agregarPartidaActionPerformed

    private void jButton_editarRubrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_editarRubrosActionPerformed
        //Verificamos que se haya seleccionado un presupuesto
        String idpresupuesto = jTextField_NumeroPresup.getText().trim();

        if (!idpresupuesto.equals("")) {
            //Verificamos que el presupuesto este abierto
            //if (jTable_Presupuestos.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 6).toString().equals("Abierto")) {
            //Capturamos los datos faltantes
            String descripPresupuesto = jTextField_descripcion.getText().trim();

            Frame[] ventanas = getFrames();

            for (Frame ventana : ventanas) {
                if (ventana instanceof EdicionRubros) {
                    ventana.dispose();
                }
            }

            new EdicionRubros(usuario, permiso, idpresupuesto, descripPresupuesto).setVisible(true);
            //} else {
            //    JOptionPane.showMessageDialog(this, "Solo se pueden editar los conceptos de presupuestos cuyo estado sea: Abierto");
            //}

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione el presupuesto", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton_editarRubrosActionPerformed

    private void jButton_cambiarEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cambiarEstadoActionPerformed
        //Verificamos que se haya seleccionado un presupuesto
        String idpresupuesto = jTextField_NumeroPresup.getText().trim();

        if (!idpresupuesto.equals("")) {
            String estado = jComboBox_estado.getSelectedItem().toString();
            CambiarEstadoPresupuesto(idpresupuesto, estado);
            limpiarCampos();
            limpiarTabla(modelo);
            llenarTabla();

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un presupuesto", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton_cambiarEstadoActionPerformed

    private void jButton_agregarGastosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregarGastosActionPerformed
        //Verificamos que se haya seleccionado un presupuesto
        String idPresupuesto = jTextField_NumeroPresup.getText().trim();
        if (!idPresupuesto.equals("")) {
            //Verificamos que el presupuesto este abierto
            if (jTable_Presupuestos.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 7).toString().equalsIgnoreCase("Abierto")) {
                //Capturamos los datos faltantes
                String presupuesto = jTextField_descripcion.getText().trim();

                Frame[] ventanas = getFrames();

                for (Frame ventana : ventanas) {
                    if (ventana instanceof RegistrarGastosPresupuesto) {
                        ventana.dispose();
                    }
                }

                new RegistrarGastosPresupuesto(this.usuario, this.permiso, idPresupuesto, presupuesto).setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "Solo se pueden agregar gastos a los presupuestos cuyo estado sea: Abierto", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione el presupuesto", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton_agregarGastosActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //Verificamos que se haya seleccionado un presupuesto
        String idPresupuesto = jTextField_NumeroPresup.getText().trim();

        if (!idPresupuesto.equals("")) {

            String[] datosPresupuesto = consultarDatosPresupuesto(idPresupuesto);
            ArrayList<Object[]> itemsPresupusto = consolidarPresupuesto(idPresupuesto);
            ArrayList<Object[]> partidas = consultarPartidas(idPresupuesto);
            ArrayList<Object[]> gastos = consultarGastos(idPresupuesto);
            ArrayList<Object[]> listaGastosNoAutorizados = consultarListadoGastosNoAutorizados(idPresupuesto);
            double gastosNoAutorizados = consultarGastosNoAutorizados(idPresupuesto);
            ArrayList<Object[]> ingresosEntradasDiarias = consultarIngresosEntradasDiarias(datosPresupuesto[1], datosPresupuesto[2]);
            ArrayList<Object[]> ingresosFacturas = consultarIngresosFacturas(datosPresupuesto[1], datosPresupuesto[2]);

            GenerarInformeEconomico(idPresupuesto, datosPresupuesto, itemsPresupusto, partidas, gastos,
                    ingresosEntradasDiarias, ingresosFacturas, gastosNoAutorizados, listaGastosNoAutorizados);

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione el presupuesto", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton_crearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_crearActionPerformed
        //Verificamos que no haya presup abiertos o pend de aut

        int presupAnterior = consultarMaxPresup();
        String[] infoPresup = consultarDatosPresupuesto(String.valueOf(presupAnterior));
        //Fecha inicio y fin posicion 1 y 2 respectivamente   

//        System.out.println(infoPresup[1]);
//        System.out.println(infoPresup[2]);
        try {
            String fechaIni = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_ini.getDate());
            String fechaFin = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_fin.getDate());
            String descripcion = jTextField_descrippresup.getText().trim().toUpperCase();

            if (!descripcion.equals("")) {

                //Verificamos que la fecha inicio no se superior a la fecha fin
                if (jDateChooser_ini.getDate().before(jDateChooser_fin.getDate())) {

                    //Verificamos si hay gastos pendientes por autorizar
                    if (comprobarGastos()) {

                        double sumaPartidas = consultarSumaPartidas(presupAnterior);
                        double sumaEE = consultarSumaIngresosEntradasDiarias(infoPresup[1], infoPresup[2]);
                        double sumaFacturas = consultarSumaIngresosFacturas(infoPresup[1], infoPresup[2]);
                        double sumaGastos = consultarSumaGastos(presupAnterior);

                        double partidaUtilidad = sumaPartidas + sumaEE + sumaFacturas - sumaGastos;
                        RegistrarPresupuesto(descripcion, fechaIni, fechaFin, partidaUtilidad, infoPresup, presupAnterior);
                        limpiarCampos();
                        limpiarTabla(modelo);
                        llenarTabla();
                        llenarCampos();

                    } else {
                        int opcion = JOptionPane.showConfirmDialog(this, "Existen Gastos pendientes por autorizar. Se creará una partidad provisional por utilidad o perdida del presupuesto anterior.\n"
                                + "La partida provisoria puede ser actualizada por el Gerente una vez haya autorizado todos los gastos.\n\n"
                                + "¿Desea continuar?", "Confirmacion", JOptionPane.INFORMATION_MESSAGE);

                        if (opcion == 0) {

                            double sumaPartidas = consultarSumaPartidas(presupAnterior);
                            double sumaEE = consultarSumaIngresosEntradasDiarias(infoPresup[1], infoPresup[2]);
                            double sumaFacturas = consultarSumaIngresosFacturas(infoPresup[1], infoPresup[2]);
                            double sumaGastos = consultarSumaGastos(presupAnterior);

                            double partidaUtilidad = sumaPartidas + sumaEE + sumaFacturas - sumaGastos;
                            RegistrarPresupuestoProvisional(descripcion, fechaIni, fechaFin, partidaUtilidad, infoPresup, presupAnterior);
                            limpiarCampos();
                            limpiarTabla(modelo);
                            llenarTabla();
                            llenarCampos();

                        } else {
                            JOptionPane.showMessageDialog(this, "Presupuesto no registrado", "Informacion", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "La fecha inicial no puede ser mayor a la fecha final", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Complete la descripcion", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(this, "Formato o fecha no valida. El formato de fecha debe ser DD/MM/AAAA", "Error", JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_jButton_crearActionPerformed

    private void jTextField_descrippresupKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_descrippresupKeyTyped

        char c = evt.getKeyChar();

        if (!Character.isLetterOrDigit(c)) {

            if (c != ' ') {
                evt.consume();
            }
        }

    }//GEN-LAST:event_jTextField_descrippresupKeyTyped

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
            java.util.logging.Logger.getLogger(ListadoPresupuestos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoPresupuestos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoPresupuestos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoPresupuestos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoPresupuestos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_agregarGastos;
    private javax.swing.JButton jButton_agregarPartida;
    private javax.swing.JButton jButton_cambiarEstado;
    private javax.swing.JButton jButton_cargarRubros;
    private javax.swing.JButton jButton_crear;
    private javax.swing.JButton jButton_editarRubros;
    private javax.swing.JComboBox<String> jComboBox_estado;
    private com.toedter.calendar.JDateChooser jDateChooser_fin;
    private com.toedter.calendar.JDateChooser jDateChooser_ini;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel_fila;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Presupuestos;
    private javax.swing.JTextField jTextField_NumeroPresup;
    private javax.swing.JTextField jTextField_descripcion;
    private javax.swing.JTextField jTextField_descrippresup;
    // End of variables declaration//GEN-END:variables
}

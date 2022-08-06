package clases;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

public class Ventas {

    private String Vendedor;
    private String Cantidad;
    private String descripcionTrabajo;
    private String tipoTrabajo;
    private String precio;
    private String tamaño;
    private String fechaEntrega;
    private String colorTinta;
    private String numeracionInicial;
    private String numeracionFinal;
    private String acabado;
    private String papelOriginal;
    private String copia1;
    private String copia2;
    private String copia3;
    private String observaciones;
    private float unitario;

    public Ventas(String Vendedor, String Cantidad, String descripcionTrabajo, String tipoTrabajo, float unitario, String precio, String tamaño, String fechaEntrega, String colorTinta, String numeracionInicial, String numeracionFinal, String acabado, String papelOriginal, String copia1, String copia2, String copia3, String observaciones) {
        this.Vendedor = Vendedor;
        this.Cantidad = Cantidad;
        this.descripcionTrabajo = descripcionTrabajo;
        this.tipoTrabajo = tipoTrabajo;
        this.precio = precio;
        this.tamaño = tamaño;
        this.fechaEntrega = fechaEntrega;
        this.colorTinta = colorTinta;
        this.numeracionInicial = numeracionInicial;
        this.numeracionFinal = numeracionFinal;
        this.acabado = acabado;
        this.papelOriginal = papelOriginal;
        this.copia1 = copia1;
        this.copia2 = copia2;
        this.copia3 = copia3;
        this.observaciones = observaciones;
        this.unitario = unitario;
    }

    public String getVendedor() {
        return Vendedor;
    }

    public void setVendedor(String Vendedor) {
        this.Vendedor = Vendedor;
    }

    public String getCantidad() {
        return Cantidad;
    }

    public void setCantidad(String Cantidad) {
        this.Cantidad = Cantidad;
    }

    public String getDescripcionTrabajo() {
        return descripcionTrabajo;
    }

    public void setDescripcionTrabajo(String descripcionTrabajo) {
        this.descripcionTrabajo = descripcionTrabajo;
    }

    public String getTipoTrabajo() {
        return tipoTrabajo;
    }

    public void setTipoTrabajo(String tipoTrabajo) {
        this.tipoTrabajo = tipoTrabajo;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getTamaño() {
        return tamaño;
    }

    public void setTamaño(String tamaño) {
        this.tamaño = tamaño;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getColorTinta() {
        return colorTinta;
    }

    public void setColorTinta(String colorTinta) {
        this.colorTinta = colorTinta;
    }

    public String getNumeracionInicial() {
        return numeracionInicial;
    }

    public void setNumeracionInicial(String numeracionInicial) {
        this.numeracionInicial = numeracionInicial;
    }

    public String getNumeracionFinal() {
        return numeracionFinal;
    }

    public void setNumeracionFinal(String numeracionFinal) {
        this.numeracionFinal = numeracionFinal;
    }

    public String getAcabado() {
        return acabado;
    }

    public void setAcabado(String acabado) {
        this.acabado = acabado;
    }

    public String getPapelOriginal() {
        return papelOriginal;
    }

    public void setPapelOriginal(String papelOriginal) {
        this.papelOriginal = papelOriginal;
    }

    public String getCopia1() {
        return copia1;
    }

    public void setCopia1(String copia1) {
        this.copia1 = copia1;
    }

    public String getCopia2() {
        return copia2;
    }

    public void setCopia2(String copia2) {
        this.copia2 = copia2;
    }

    public String getCopia3() {
        return copia3;
    }

    public void setCopia3(String copia3) {
        this.copia3 = copia3;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public float getUnitario() {
        return unitario;
    }

    public void setUnitario(float unitario) {
        this.unitario = unitario;
    }

    public static ArrayList<Clientes> LeerClientes() {

        ArrayList<Clientes> nuevoArrayList = new ArrayList<>();
        try {
            String consulta = "select * from clientes";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                int idCliente = Integer.parseInt(rs.getString("idCliente"));
                String nombreCliente = rs.getString("nombreCliente");
                String identificacion = rs.getString("identificacion");
                String tipoCliente = rs.getString("tipoCliente");
                String direccion = rs.getString("direccion");
                String municipio = rs.getString("municipio");
                String telefono = rs.getString("telefono");
                String sector = rs.getString("sector");
                String email = rs.getString("email");
                String registradoPor = rs.getString("registradoPor");

                Clientes nuevoCliente = new Clientes(idCliente, nombreCliente, identificacion, direccion, municipio, telefono, email);
                nuevoArrayList.add(nuevoCliente);
            }

            cn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al leer la base de datos de clientes. Contacte al administrador");
        }

        return nuevoArrayList;
    }

    public void RegistroVentas(String registradoPor, String fechaSistema, String Idcliente, String aleatorio) {

        String consulta = "insert into ventas (Vendedor, FechaventaSistema, Idcliente, "
                + "Cantidad, descripcionTrabajo, tipoTrabajo, unitario, precio, abonos, saldo,  tamaño, fechaEntrega, colorTinta, "
                + "numeracionInicial, numeracionFinal, acabado, papelOriginal, copia1, copia2, copia3, "
                + "observaciones, registradoPor, estadoCuenta, estadoElaboracion, aleatorioSeguridad, saldoPorFacturar, estadoFacturacion) values ('" + this.Vendedor + "', '" + fechaSistema + "', "
                + "'" + Idcliente + "', '" + this.Cantidad + "', '" + this.descripcionTrabajo + "', '" + this.tipoTrabajo + "', "
                + "'" + this.precio + "', '0', '" + this.unitario + "', '" + this.precio + "', '" + this.tamaño + "', '" + this.fechaEntrega + "', '" + this.colorTinta + "', "
                + "'" + this.numeracionInicial + "', '" + this.numeracionFinal + "', '" + this.acabado + "', '" + this.papelOriginal + "', "
                + "'" + this.copia1 + "', '" + this.copia2 + "', '" + this.copia3 + "', '" + this.observaciones + "', '" + registradoPor + "', 'Pendiente', 'Pendiente', '" + aleatorio + "', '" + this.precio + "', 'Pendiente')";

        System.out.println(consulta);
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Venta registrada");
            cn.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "La venta no ha sido registrada en la base de datos. Contacte al administrador");
            ex.printStackTrace();
        }

    }

    //Este metodo actualiza el campo abonos y saldo en la tabla ventas para una venta en particular seleccionada
    public static void ActualizarAbonosYSaldos(String idVenta, int abonos, int saldo) {

        try {

            String consulta = "update ventas set abonos='" + abonos + "', saldo='" + saldo + "' where idVenta='" + idVenta + "'";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Abonos y saldo actualizado");
            cn.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "Error en actualizar los abonos y el saldo");
            e.printStackTrace();
        }

    }

    //Este metodo actualiza el campo estadoCuenta de la tabla ventas. 
    //Si el valor de abonos es igual al precio de venta pone Saldado, sino permanece el valor Pendiente
    public static void ActualizarEstadoCuenta(String idVenta, String fecha, String usuario) {

        try {

            String consulta = "update ventas set estadoCuenta='Saldado', fechaSaldado='" + fecha + "' where idVenta='" + idVenta + "'";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Estado de cuenta cambiado a Saldado");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "Error en cambiar el Estado de cuenta. Contacte al administrador");

        }

    }

    public static Object[] LeerDatosparaOT(String idVenta) {
        Object[] nuevo = new Object[17]; //Cambie todo lo que decia String[] por Object[]

        try {

//            String consulta = "SELECT ventas.FechaventaSistema, clientes.nombreCliente, ventas.descripcionTrabajo, "
//                    + "ventas.Cantidad, ventas.tamaño, ventas.colorTinta, ventas.numeracionInicial, "
//                    + "ventas.numeracionFinal, ventas.acabado, ventas.papelOriginal, ventas.copia1, ventas.copia2, "
//                    + "ventas.copia3, ventas.observaciones, ventas.registradoPor from clientes inner join ventas "
//                    + "where clientes.idCliente=ventas.Idcliente and ventas.Idventa=?";

            String consulta = "SELECT v.FechaventaSistema, c.nombreCliente, v.descripcionTrabajo,\n"
                    + "v.Cantidad, v.tamaño, v.colorTinta, v.numeracionInicial,\n"
                    + "v.numeracionFinal, v.acabado, v.papelOriginal, v.copia1, v.copia2,\n"
                    + "v.copia3, v.observaciones, v.registradoPor \n"
                    + "from clientes c join ventas v on c.idCliente=v.Idcliente\n"
                    + "where v.Idventa=119";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idVenta);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
//
//                Date fecha = new Date();
//                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
//                String fechaActual = formato.format(fecha);

                nuevo[0] = new SimpleDateFormat("yyyy-MM-dd hh:mm a").format(new Date());
                nuevo[1] = rs.getString("v.FechaventaSistema");
                nuevo[2] = idVenta;
                nuevo[3] = rs.getString("c.nombreCliente");
                nuevo[4] = rs.getString("v.descripcionTrabajo");
                nuevo[5] = rs.getString("v.Cantidad");
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
            //JOptionPane.showMessageDialog(this, "Error al consultar los datos del trabajo");
        }

        return nuevo;
    }

}

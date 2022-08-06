/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import java.awt.HeadlessException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;

/**
 *
 * @author erwin
 */
public class RegistroVentasDeLista extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso, cliente;
    ArrayList<String> datos;

    /**
     * Creates new form RegistroVentas
     */
    public RegistroVentasDeLista() {
        initComponents();
        llenarComboBoxes();
        ConfiguracionGralJFrame();

    }

    public RegistroVentasDeLista(String usuario, String permiso, ArrayList<String> datos) {
        initComponents();
        this.usuario = usuario;
        this.permiso = permiso;
        this.datos = datos;
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

    }

    public RegistroVentasDeLista(String usuario, String permiso, String cliente, String idVenta) {

        initComponents();
        this.usuario = usuario;
        this.permiso = permiso;
        this.cliente = cliente;
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();
        CargarDatos(idVenta);

    }

    public void IniciarCaracteristicasGenerales() {
        jLabel_cliente.setText(cliente);
        String tipoCliente = ConsultarTipoCliente(cliente);
        jLabel_tipoCliente.setText(tipoCliente);
        if (tipoCliente.equals("Empresa")) {
            jTextField_abono.setEnabled(false);
            jTextField_observacionAbono.setEnabled(false);
        }
        llenarComboBoxes();
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Registrar venta *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void llenarComboBoxes() {

        ArrayList<String> listaVendedores = new ArrayList<String>();

        try {

            String consultavendedor = "select nombreComisionista from comisionistas";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultavendedor);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String vendedor = rs.getString("nombreComisionista");
                listaVendedores.add(vendedor);
            }

            for (String vendedor : listaVendedores) {
                jComboBox_vendedor.addItem(vendedor);
            }

            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en leer los comisionistas RegistroVentas llenarComboBoxes()\n" + e);
        }

        ArrayList<String> listaTipoTrabajo = new ArrayList<String>();
        try {

            String consultaTipo = "select tipo from tipotrabajo";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaTipo);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                listaTipoTrabajo.add(tipo);
            }

            for (String tipo : listaTipoTrabajo) {
                jComboBox_tipotrabajo.addItem(tipo);
            }

            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en leer los tipos de trabajo RegistroVentas llenarComboBoxes()\n" + e);
        }

        ArrayList<String> listaPapeles = new ArrayList<String>();
        try {

            String consultaPapel = "select nombrePapel from papeles";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaPapel);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String tipo = rs.getString("nombrePapel");
                listaPapeles.add(tipo);
            }

            for (String tipo : listaPapeles) {
                jComboBox_original.addItem(tipo);
                jComboBox_copia1.addItem(tipo);
                jComboBox_copia2.addItem(tipo);
                jComboBox_copia3.addItem(tipo);

            }

            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en leer los tipos de trabajo RegistroVentas llenarComboBoxes()\n" + e);
        }

    }

    public int BuscarIdCliente(String cliente) {
        int IdCliente = -1;

        try {
            String consulta = "select idCliente from clientes where nombreCliente = '" + cliente + "'";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                IdCliente = rs.getInt("idCliente");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en encontrar el Id correspondiente al cliente seleccionado\n" + e);
            e.printStackTrace();
        }

        return IdCliente;
    }

    public void LimpiarFormulario() {

        jTextField_descripcion.setText("");
        jTextField_cantidad.setText("");
        jComboBox_tipotrabajo.setSelectedItem(0);
        jComboBox_vendedor.setSelectedIndex(0);
        jTextField_precio.setText("");
        jTextField_tamaño.setText("");
        jTextField_colorTinta.setText("");
        jTextField_numeroInicial.setText("");
        jTextField_numerofinal.setText("");
        jTextField_acabado.setText("");
        jComboBox_original.setSelectedIndex(0);
        jComboBox_copia1.setSelectedIndex(0);
        jComboBox_copia2.setSelectedIndex(0);
        jComboBox_copia3.setSelectedIndex(0);
        jTextField_observaciones.setText("");
        jDateChooser_fechaentrega.setDate(null);

    }

    public String ConsultarIdVenta(String idCliente, String fechaSistema, String cantidad, String descripcion, String precioVenta, String aleatorio) {

        String Idventa = "";

        try {
            String consulta = "select Idventa from ventas where Idcliente=? and FechaventaSistema=? and Cantidad=? and "
                    + "descripcionTrabajo=? and precio=? and aleatorioSeguridad=?";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idCliente);
            pst.setString(2, fechaSistema);
            pst.setString(3, cantidad);
            pst.setString(4, descripcion);
            pst.setString(5, precioVenta);
            pst.setString(6, aleatorio);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Idventa = rs.getString("Idventa");
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null, "Error en leer el Id de la venta RegistroVenta RegistroAbonoVenta()\n" + e);
        }

        return Idventa;
    }

    public void RegistrarVenta(String vendedor, String fechaSistema, String idCliente, int cantidad, String descripcion,
            String tipo, double unitario, int precioVentaParseado, String tamaño, String fechaEntrega, String color,
            String numeroinicial, String numerofinal, String acabado, String papeloriginal, String copia1, String copia2,
            String copia3, String observaciones, int aleatorio, String registradoPor) {

        String consulta = "INSERT INTO ventas (Idventa, Vendedor, FechaventaSistema, Idcliente, Cantidad, "
                + "descripcionTrabajo, tipoTrabajo, unitario, precio, tamaño, fechaEntrega, colorTinta, "
                + "numeracionInicial, numeracionFinal, acabado, papelOriginal, copia1, copia2, copia3, "
                + "observaciones, registradoPor, estadoCuenta, 	aleatorioSeguridadAbono) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, 0);
            pst.setString(2, vendedor);
            pst.setString(3, fechaSistema);
            pst.setString(4, idCliente);
            pst.setInt(5, cantidad);
            pst.setString(6, descripcion);
            pst.setString(7, tipo);
            pst.setDouble(8, unitario);
            pst.setInt(9, precioVentaParseado);
            pst.setString(10, tamaño);
            pst.setString(11, fechaEntrega);
            pst.setString(12, color);
            pst.setString(13, numeroinicial);
            pst.setString(14, numerofinal);
            pst.setString(15, acabado);
            pst.setString(16, papeloriginal);
            pst.setString(17, copia1);
            pst.setString(18, copia2);
            pst.setString(19, copia3);
            pst.setString(20, observaciones);
            pst.setString(21, registradoPor);
            pst.setString(22, "Pendiente");
            pst.setInt(23, aleatorio);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "Venta registrada");

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en registrar venta. Informe al administrador. RegistroVentas RegistrarVenta()\n" + e);
        }
    }

    public void RegistrarVentaConMotivo(String vendedor, String fechaSistema, String idCliente, int cantidad, String descripcion,
            String tipo, double unitario, int precioVentaParseado, String tamaño, String fechaEntrega, String color,
            String numeroinicial, String numerofinal, String acabado, String papeloriginal, String copia1, String copia2,
            String copia3, String observaciones, int aleatorio, String registradoPor, String razon) {

        String consulta = "INSERT INTO ventas (Idventa, Vendedor, FechaventaSistema, Idcliente, Cantidad, "
                + "descripcionTrabajo, tipoTrabajo, unitario, precio, tamaño, fechaEntrega, colorTinta, "
                + "numeracionInicial, numeracionFinal, acabado, papelOriginal, copia1, copia2, copia3, "
                + "observaciones, registradoPor, estadoCuenta, 	aleatorioSeguridadAbono, motivoNoAbono) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, 0);
            pst.setString(2, vendedor);
            pst.setString(3, fechaSistema);
            pst.setString(4, idCliente);
            pst.setInt(5, cantidad);
            pst.setString(6, descripcion);
            pst.setString(7, tipo);
            pst.setDouble(8, unitario);
            pst.setInt(9, precioVentaParseado);
            pst.setString(10, tamaño);
            pst.setString(11, fechaEntrega);
            pst.setString(12, color);
            pst.setString(13, numeroinicial);
            pst.setString(14, numerofinal);
            pst.setString(15, acabado);
            pst.setString(16, papeloriginal);
            pst.setString(17, copia1);
            pst.setString(18, copia2);
            pst.setString(19, copia3);
            pst.setString(20, observaciones);
            pst.setString(21, registradoPor);
            pst.setString(22, "Pendiente");
            pst.setInt(23, aleatorio);
            pst.setString(24, razon);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(null, "Venta registrada");
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en registrar venta. Informe al administrador. RegistroVentas RegistrarVenta() \n" + e);
        }
    }

    public void RegistrarAbono(String IdVenta, String valorAbono, String observaciones, String registradoPor) {

        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String consulta = "insert into abonos (idVenta, valorAbono, fechaAbonoSistema, fechaAbonoReal, observaciones, "
                + "registradoPor) values (?, ?, ?, ?, ?, ?)";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, IdVenta);
            pst.setString(2, valorAbono);
            pst.setString(3, fecha);
            pst.setString(4, fecha);
            pst.setString(5, observaciones);
            pst.setString(6, registradoPor);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Abono registrado");
            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al registrar el abono. RegistroAbonos RegistrarAbono()\n" + e);
            e.printStackTrace();
        }

    }

    public String ConsultarIdVenta(String fecha, int aleatorio) {
        String idVenta = "";
        String consulta = "select Idventa from ventas where FechaventaSistema=? and aleatorioSeguridadAbono=?";
        String consulta1 = "select Idventa from ventas where FechaventaSistema='" + fecha + "' and aleatorioSeguridadAbono='" + aleatorio + "'";
        System.out.println(consulta1);
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fecha);
            pst.setInt(2, aleatorio);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                idVenta = rs.getString("Idventa");
            }

            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el idVenta RegistroVentas ConsultarIdVenta\n" + e);
        }

        return idVenta;

    }

    public String ConsultarTipoCliente(String nombre) {
        String tipoCliente = "";
//        String consulta = "SELECT clientes.tipoCliente FROM clientes INNER JOIN ventas ON "
//                + "clientes.idCliente=ventas.Idcliente WHERE clientes.nombreCliente=?";

        String consulta = "SELECT clientes.tipoCliente FROM clientes WHERE clientes.nombreCliente=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, nombre);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                tipoCliente = rs.getString("clientes.tipoCliente");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el tipo de cliente RegistroVentas ConsultarTipoCliente\n" + e);
        }

        return tipoCliente;
    }

    public void CargarDatos(String idVenta) {
        String consulta = "select ventas.Idventa, ventas.FechaventaSistema, clientes.nombreCliente, ventas.Vendedor, "
                + "ventas.descripcionTrabajo, ventas.Cantidad, ventas.tipoTrabajo, ventas.precio, ventas.tamaño, ventas.colorTinta, "
                + "ventas.numeracionInicial, ventas.numeracionFinal, ventas.acabado, ventas.papelOriginal, "
                + "ventas.copia1, ventas.copia2, ventas.copia3, ventas.observaciones, ventas.fechaEntrega, clientes.tipoCliente from ventas INNER JOIN clientes on "
                + "ventas.Idcliente=clientes.idCliente where ventas.Idventa=?";

        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idVenta);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                jLabel_cliente.setText(rs.getString("clientes.nombreCliente"));
                jComboBox_vendedor.setSelectedItem(rs.getString("ventas.Vendedor"));
                jTextField_descripcion.setText(rs.getString("ventas.descripcionTrabajo"));
                jTextField_cantidad.setText(rs.getString("ventas.Cantidad"));
                jComboBox_tipotrabajo.setSelectedItem(rs.getString("ventas.tipoTrabajo"));
                jTextField_tamaño.setText(rs.getString("ventas.tamaño"));
                jTextField_colorTinta.setText(rs.getString("ventas.colorTinta"));
                jTextField_numeroInicial.setText(rs.getString("ventas.numeracionInicial"));
                jTextField_numerofinal.setText(rs.getString("ventas.numeracionFinal"));
                jTextField_acabado.setText(rs.getString("ventas.acabado"));
                jComboBox_original.setSelectedItem(rs.getString("ventas.papelOriginal"));
                jComboBox_copia1.setSelectedItem(rs.getString("ventas.copia1"));
                jComboBox_copia2.setSelectedItem(rs.getString("ventas.copia2"));
                jComboBox_copia3.setSelectedItem(rs.getString("ventas.copia3"));
                jTextField_observaciones.setText(rs.getString("ventas.observaciones"));
                jDateChooser_fechaentrega.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("ventas.fechaEntrega")));
                jTextField_precio.setText(rs.getString("ventas.precio"));
//                jLabel_idVenta.setText(rs.getString("ventas.Idventa"));
            }

            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en consultar el detalle del pedido en la base de datos. DetallePedidos CargarDatos()");
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

        jButton_registrarVenta = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel_cliente = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel_tipoCliente = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox_vendedor = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField_descripcion = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_cantidad = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jComboBox_tipotrabajo = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jTextField_tamaño = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField_colorTinta = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField_numeroInicial = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField_numerofinal = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jTextField_acabado = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jComboBox_original = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jComboBox_copia1 = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jComboBox_copia2 = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jComboBox_copia3 = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jTextField_observaciones = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jDateChooser_fechaentrega = new com.toedter.calendar.JDateChooser();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTextField_precio = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextField_abono = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTextField_observacionAbono = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton_registrarVenta.setText("Registrar venta");
        jButton_registrarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_registrarVentaActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel3.setText("Cliente: ");

        jLabel_cliente.setText("jLabel5");

        jLabel20.setText("Tipo Cliente:");

        jLabel_tipoCliente.setText("jLabel21");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel_cliente))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel_tipoCliente)))
                .addContainerGap(293, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel_cliente))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel_tipoCliente))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Vendedor / Comisionista", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel2.setText("Vendedor");

        jComboBox_vendedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No aplica" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jComboBox_vendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(177, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_vendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Detalle del pedido", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel5.setText("Descripcion");

        jTextField_descripcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_descripcionKeyTyped(evt);
            }
        });

        jLabel4.setText("Cantidad");

        jTextField_cantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_cantidadKeyTyped(evt);
            }
        });

        jLabel6.setText("Tipo");

        jComboBox_tipotrabajo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No aplica" }));
        jComboBox_tipotrabajo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_tipotrabajoActionPerformed(evt);
            }
        });

        jLabel8.setText("Tamaño");

        jTextField_tamaño.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_tamañoKeyTyped(evt);
            }
        });

        jLabel10.setText("Color tinta");

        jTextField_colorTinta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_colorTintaKeyTyped(evt);
            }
        });

        jLabel11.setText("Numero inicial");

        jTextField_numeroInicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_numeroInicialKeyTyped(evt);
            }
        });

        jLabel9.setText("Numero final");

        jTextField_numerofinal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_numerofinalKeyTyped(evt);
            }
        });

        jLabel13.setText("Acabado");

        jTextField_acabado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_acabadoKeyTyped(evt);
            }
        });

        jLabel14.setText("Papel original");

        jComboBox_original.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No aplica" }));

        jLabel15.setText("Copia 1");

        jComboBox_copia1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No aplica" }));
        jComboBox_copia1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_copia1ActionPerformed(evt);
            }
        });

        jLabel16.setText("Copia 2");

        jComboBox_copia2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No aplica" }));

        jLabel17.setText("Copia 3");

        jComboBox_copia3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No aplica" }));

        jLabel18.setText("Observaciones del pedido");

        jTextField_observaciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_observacionesKeyTyped(evt);
            }
        });

        jLabel12.setText("Fecha entrega");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addComponent(jLabel17))
                        .addGap(39, 39, 39)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jComboBox_copia2, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jComboBox_copia1, javax.swing.GroupLayout.Alignment.LEADING, 0, 280, Short.MAX_VALUE)
                                    .addComponent(jComboBox_original, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jComboBox_copia3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox_tipotrabajo, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_tamaño, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(22, 22, 22)
                        .addComponent(jTextField_colorTinta, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_numeroInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_numerofinal, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_acabado, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(27, 27, 27)
                        .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(jDateChooser_fechaentrega, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField_cantidad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(jLabel6)
                        .addComponent(jComboBox_tipotrabajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(jTextField_tamaño, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_colorTinta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField_numeroInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField_numerofinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jTextField_acabado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jComboBox_original, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jComboBox_copia1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jComboBox_copia2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jComboBox_copia3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18)
                        .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jDateChooser_fechaentrega, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Valor de venta y abonos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel7.setText("Precio de venta");

        jTextField_precio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_precioKeyTyped(evt);
            }
        });

        jLabel1.setText("Abono");

        jTextField_abono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_abonoKeyTyped(evt);
            }
        });

        jLabel19.setText("Observ Abono");

        jTextField_observacionAbono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_observacionAbonoKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_precio, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jTextField_abono, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel19)
                .addGap(18, 18, 18)
                .addComponent(jTextField_observacionAbono, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField_precio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_abono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jTextField_observacionAbono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(396, 396, 396)
                        .addComponent(jButton_registrarVenta)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_registrarVenta)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox_tipotrabajoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_tipotrabajoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_tipotrabajoActionPerformed

    private void jButton_registrarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_registrarVentaActionPerformed

        //Capturamos los datos del formulario
        String fechaSistema = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String idCliente = String.valueOf(BuscarIdCliente(jLabel_cliente.getText().trim()));
        String vendedor = jComboBox_vendedor.getSelectedItem().toString();
        String descripcion = jTextField_descripcion.getText().trim();
        String cantidad = jTextField_cantidad.getText().trim();
        String tipo = jComboBox_tipotrabajo.getSelectedItem().toString();
        String tamaño = jTextField_tamaño.getText().trim();
        String color = jTextField_colorTinta.getText().trim();
        String numeroinicial = jTextField_numeroInicial.getText().trim();
        String numerofinal = jTextField_numerofinal.getText().trim();
        String acabado = jTextField_acabado.getText().trim();
        String papeloriginal = jComboBox_original.getSelectedItem().toString().trim();
        String copia1 = jComboBox_copia1.getSelectedItem().toString().trim();
        String copia2 = jComboBox_copia2.getSelectedItem().toString().trim();
        String copia3 = jComboBox_copia3.getSelectedItem().toString().trim();
        String observaciones = jTextField_observaciones.getText().trim();
        String fechaEntrega = "";

        if (jDateChooser_fechaentrega.getDate() == null) {
            fechaEntrega = null;
        } else {
            fechaEntrega = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_fechaentrega.getDate());
        }
        String precioVenta = jTextField_precio.getText().trim();

        //Verificamos que todos los campos obligatorios esten completos
        if (!descripcion.equals("") && !cantidad.equals("") && !tipo.equals("") && !tamaño.equals("") && !color.equals("")
                && fechaEntrega != null && !precioVenta.equals("")) {

            //Verificamos que se haya ingresado el abono
            String abono = jTextField_abono.getText().trim();
            int aleatorio = (int) (Math.random() * 10000000);

            //Parseamos los datos numericos
            int cantidadParseada = Integer.parseInt(cantidad);
            int precioVentaParseado = Integer.parseInt(precioVenta);
            double unitario = (double) precioVentaParseado / (double) cantidadParseada;

            //Consultamos el tipo de cliente así determinamos si es requisito el abono o no
            String cliente = jLabel_cliente.getText().trim();
            String tipoCliente = jLabel_tipoCliente.getText().trim();

            if (tipoCliente.equals("Persona")) {
                if (!abono.equals("")) {
                    //Verificamos que el abono no sea supuerior al valor de venta
                    if (precioVentaParseado > Integer.parseInt(abono)) {
                        RegistrarVenta(vendedor, fechaSistema, idCliente, cantidadParseada, descripcion, tipo, unitario, precioVentaParseado,
                                tamaño, fechaEntrega, color, numeroinicial, numerofinal, acabado, papeloriginal, copia1, copia2,
                                copia3, observaciones, aleatorio, this.usuario);

                        String idVenta = ConsultarIdVenta(fechaSistema, aleatorio);

                        RegistrarAbono(idVenta, abono, observaciones, this.usuario);
                        dispose();
                        new ListadoAbonosEntradasDiarias(this.usuario, this.permiso).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "No es posible registrar un valor de abono superior al precio de venta");
                    }

                } else {
                    String motivoNoAbono = JOptionPane.showInputDialog(this, "Indique la razon por la que no se registrara el \nabono respectivo en esta venta.");

                    if (!motivoNoAbono.equals("") && motivoNoAbono.length() > 15) {

                        RegistrarVentaConMotivo(vendedor, fechaSistema, idCliente, cantidadParseada, descripcion, tipo, unitario, precioVentaParseado,
                                tamaño, fechaEntrega, color, numeroinicial, numerofinal, acabado, papeloriginal, copia1, copia2,
                                copia3, observaciones, aleatorio, this.usuario, motivoNoAbono);
                        LimpiarFormulario();

                    } else {
                        JOptionPane.showMessageDialog(this, "Debe completar la razon y esta debe ser especifica");
                    }
                }

                //Si el cliente es empresa se cumple el siguiente codigo
            } else {

                RegistrarVenta(vendedor, fechaSistema, idCliente, cantidadParseada, descripcion, tipo, unitario, precioVentaParseado,
                        tamaño, fechaEntrega, color, numeroinicial, numerofinal, acabado, papeloriginal, copia1, copia2,
                        copia3, observaciones, aleatorio, this.usuario);
                LimpiarFormulario();

                if (!abono.equals("")) {
                    JOptionPane.showMessageDialog(this, "El abono ingresado no fue registrado. Dado que el cliente " + cliente + " "
                            + "es una empresa. Antes de registrar un abono es obligatorio realizar la factura");
                    new ListadoParaFacturar(usuario, permiso, idCliente).setVisible(true);

                }

            }
    }//GEN-LAST:event_jButton_registrarVentaActionPerformed
          else {
            JOptionPane.showMessageDialog(this, "Los campos descripcion, cantidad, tipo de trabajo, tamaño, color y \nfecha de entrega son obligatorios");
        }

    }


    private void jComboBox_copia1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_copia1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_copia1ActionPerformed

    private void jTextField_descripcionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_descripcionKeyTyped
        if (jTextField_descripcion.getText().trim().length() == 250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_descripcionKeyTyped

    private void jTextField_cantidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_cantidadKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
        if (c == '0' && jTextField_cantidad.getText().trim().length() == 0) {
            evt.consume();
        }

        if (jTextField_cantidad.getText().trim().length() == 11) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_cantidadKeyTyped

    private void jTextField_tamañoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_tamañoKeyTyped
        if (jTextField_tamaño.getText().trim().length() == 50) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_tamañoKeyTyped

    private void jTextField_colorTintaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_colorTintaKeyTyped
        if (jTextField_colorTinta.getText().trim().length() == 150) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_colorTintaKeyTyped

    private void jTextField_numeroInicialKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_numeroInicialKeyTyped
        char c = evt.getKeyChar();
        if (jTextField_numeroInicial.getText().trim().length() == 50 || !Character.isDigit(c)) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_numeroInicialKeyTyped

    private void jTextField_numerofinalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_numerofinalKeyTyped
        char c = evt.getKeyChar();
        if (jTextField_numerofinal.getText().trim().length() == 50 || !Character.isDigit(c)) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_numerofinalKeyTyped

    private void jTextField_acabadoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_acabadoKeyTyped
        if (jTextField_acabado.getText().trim().length() == 70) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_acabadoKeyTyped

    private void jTextField_observacionesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_observacionesKeyTyped
        if (jTextField_observaciones.getText().trim().length() == 250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_observacionesKeyTyped

    private void jTextField_precioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_precioKeyTyped
        char c = evt.getKeyChar();

        if (!Character.isDigit(c)) {
            evt.consume();
        }
        if (c == '0' && jTextField_precio.getText().trim().length() == 0) {
            evt.consume();
        }

        if (jTextField_precio.getText().trim().length() == 11) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_precioKeyTyped

    private void jTextField_abonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_abonoKeyTyped
        char c = evt.getKeyChar();

        if (!Character.isDigit(c)) {
            evt.consume();
        }
        if (c == '0' && jTextField_abono.getText().trim().length() == 0) {
            evt.consume();
        }

        if (jTextField_abono.getText().trim().length() == 11) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_abonoKeyTyped

    private void jTextField_observacionAbonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_observacionAbonoKeyTyped
        if (jTextField_observacionAbono.getText().trim().length() == 250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_observacionAbonoKeyTyped

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
            java.util.logging.Logger.getLogger(RegistroVentasDeLista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroVentasDeLista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroVentasDeLista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroVentasDeLista.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistroVentasDeLista().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_registrarVenta;
    private javax.swing.JComboBox<String> jComboBox_copia1;
    private javax.swing.JComboBox<String> jComboBox_copia2;
    private javax.swing.JComboBox<String> jComboBox_copia3;
    private javax.swing.JComboBox<String> jComboBox_original;
    private javax.swing.JComboBox<String> jComboBox_tipotrabajo;
    private javax.swing.JComboBox<String> jComboBox_vendedor;
    private com.toedter.calendar.JDateChooser jDateChooser_fechaentrega;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_cliente;
    private javax.swing.JLabel jLabel_tipoCliente;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jTextField_abono;
    private javax.swing.JTextField jTextField_acabado;
    private javax.swing.JTextField jTextField_cantidad;
    private javax.swing.JTextField jTextField_colorTinta;
    private javax.swing.JTextField jTextField_descripcion;
    private javax.swing.JTextField jTextField_numeroInicial;
    private javax.swing.JTextField jTextField_numerofinal;
    private javax.swing.JTextField jTextField_observacionAbono;
    private javax.swing.JTextField jTextField_observaciones;
    private javax.swing.JTextField jTextField_precio;
    private javax.swing.JTextField jTextField_tamaño;
    // End of variables declaration//GEN-END:variables
}

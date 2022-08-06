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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author erwin
 */
public class ListadoParaFacturar extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso, cliente, idCliente;

    /**
     * Creates new form ListadoPendiente
     */
    public ListadoParaFacturar() {
        this.idCliente = "37";
        initComponents();

        CargarDatos("37");
        InhabilitarCampos();
        SettearModelo();
        llenarTabla();
        ConfiguracionGralJFrame();

    }

    public ListadoParaFacturar(String usuario, String permiso, String idCliente) {
        this.usuario = usuario;
        this.permiso = permiso;
        this.idCliente = idCliente;
        initComponents();
        CargarDatos(this.idCliente);
        IniciarCaracteristicasGenerales();

        ConfiguracionGralJFrame();

    }

    public void IniciarCaracteristicasGenerales() {
        SettearModelo();
        llenarTabla();
        InhabilitarCampos();
    }

    public void InhabilitarCampos() {
        jTextField_id.setEnabled(false);
        jTextField_cliente.setEnabled(false);
        jTextField_total.setEnabled(Boolean.FALSE);
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Facturacion *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void SettearModelo() {
        modelo = (DefaultTableModel) jTable1.getModel();

    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    public boolean VerificarSiHayFacturas() {
        boolean siHay = true;

        return siHay;
    }

    public void CargarDatos(String idCliente) {

        String consultarCliente = "select nombreCliente from clientes where idCliente=?";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consultarCliente);
            pst.setString(1, idCliente);

            ResultSet rs = pst.executeQuery();
            String cliente = new String();
            while (rs.next()) {
                cliente = rs.getString("nombreCliente");
            }

            jTextField_id.setText(idCliente);
            jTextField_cliente.setText(cliente);
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el nombre del cliente. Contacte al administrador");
            e.printStackTrace();
        }
    }

    public void llenarTabla() {

        String consulta = "select er.id, er.idRemision, er.idVenta, v.descripcionTrabajo, v.unitario, ifnull(er.cantidad - sum(ef.cantidad), er.cantidad) as saldo\n"
                + "from elementosremision er left join elementosfactura ef on er.id=ef.idElementoRemito and ef.estado='Activo' \n"
                + "left join ventas v on er.idVenta=v.Idventa where v.Idcliente=? and er.estado='Activo'\n"
                + "group by er.id\n"
                + "having saldo > 0;";

        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, this.idCliente);

            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable1.getModel();

            while (rs.next()) {
                Object[] pendientes = new Object[9];

                pendientes[0] = Boolean.FALSE;
                pendientes[1] = rs.getString("er.id");
                pendientes[2] = rs.getString("er.idRemision");
                pendientes[3] = rs.getString("er.idVenta");
                pendientes[4] = rs.getString("v.descripcionTrabajo");
                pendientes[5] = rs.getDouble("saldo");
                pendientes[6] = rs.getDouble("saldo");
                pendientes[7] = MetodosGenerales.ConvertirIntAMoneda(rs.getDouble("v.unitario"));
                double cant = rs.getDouble("saldo");
                double unita = rs.getDouble("v.unitario");

                pendientes[8] = MetodosGenerales.ConvertirIntAMoneda(cant * unita);

                modelo.addRow(pendientes);

            }
            jTable1.setModel(modelo);

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos en la tabla pendientes desde la base de datos");
            e.printStackTrace();
        }
    }

    public void EliminarItemDelListado(String fila) {

        modelo = (DefaultTableModel) jTable1.getModel();
        modelo.removeRow(Integer.parseInt(fila));
        jTable1.setModel(modelo);

    }

    public void limpiarTabla() {
//        jTextField_cantidad.setText("");
//        jTextField_condicionPago.setText("");
//        jTextField_descripcion.setText("");
//        jTextField_idVenta.setText("");
//        jTextField_subtotal.setText("");
//        jTextField_unitario.setText("");
//        jLabel_fila.setText("");
//        jLabel_cantidad.setText("");
    }

    public void CargarFactura(ArrayList<String[]> listado, String condición, double monto, String idCliente, String cliente) {

        String registrarFactura = "insert into facturas (fechaFactura, condiciondePago, registradoPor, monto, idCliente)"
                + " values (?, ?, ?, ?, ?)";

        String registrarElementosFactura = "insert into elementosfactura (idElementoRemito, cantidad, factura)"
                + " values (?, ?, ?)";

        Connection cn = Conexion.Conectar();

        try {

            cn.setAutoCommit(false);
            PreparedStatement pst = cn.prepareStatement(registrarFactura, PreparedStatement.RETURN_GENERATED_KEYS);
            pst.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            pst.setString(2, condición);
            pst.setString(3, this.usuario);
            pst.setDouble(4, monto);
            pst.setString(5, this.idCliente);
            pst.executeUpdate();

            ResultSet idGenerado = pst.getGeneratedKeys();
            idGenerado.next();
            int idFactura = idGenerado.getInt(1);

            pst.close();

            PreparedStatement pst2 = cn.prepareStatement(registrarElementosFactura);
            for (String[] factura : listado) {
                pst2.setString(1, factura[0]);
                pst2.setString(2, factura[1]);
                pst2.setInt(3, idFactura);
                pst2.executeUpdate();
            }

            pst2.close();
            cn.commit();
            cn.close();
            JOptionPane.showMessageDialog(this, "Factura registrada", "Informacion", JOptionPane.INFORMATION_MESSAGE);

            String asunto = "Factura No. " + idFactura + " Generada - " + cliente;
            String mensaje = "Se ha generado la factura " + idFactura + " - a nombre de : " + cliente
                    + "\nCondicion de pago: " + condición
                    + "\nValor facturado = " + MetodosGenerales.ConvertirIntAMoneda(monto)
                    + "\nUsuario responsable: " + this.usuario;

            //MetodosGenerales.enviarEmail(asunto, mensaje);
            //MetodosGenerales.registrarHistorial(usuario, mensaje);
            
            
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "SQLException\nError en registrar la factura. Contacte al administrador", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public static String ConvertirIntAMoneda(double dato) {
        String result = "";
        DecimalFormat objDF = new DecimalFormat("$ ###, ###");
        result = objDF.format(dato);

        return result;
    }

    public void LimpiarTabla() {

        for (int i = 0; i < jTable1.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }

    }

    public void ActualizarTabla(ArrayList<Object[]> listado) {

        modelo = (DefaultTableModel) jTable1.getModel();

        for (int i = 0; i < listado.size(); i++) {
            modelo.addRow(listado.get(i));
        }

    }

    public boolean comprobarGrilla(String cadena) {

        if (cadena.length() == 0) {
            return false;
        } else {
            for (int j = 0; j < cadena.length(); j++) {

                try {
                    Double.parseDouble(cadena);
                } catch (Exception e) {
                    return false;
                }

//                if (!Character.isDigit(cadena.charAt(j)) || cadena.charAt(0) == '0') {
//                    return false;
//                }
            }
        }
        return true;
    }

    public void limpiarCampos() {
        jTextField_cliente.setText("");
        jTextField_condicionPago.setText("");
        jTextField_id.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTextField_id = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_condicionPago = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jTextField_total = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel8.setText("Id:");

        jLabel12.setText("Cliente:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Seleccion", "Id elem.", "Id Rem.", "Id Venta", "Descripcion", "Cant.", "Cant. a fact", "Unitario", "Subtotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(70);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(100);
        }

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Condicion comercial", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Condicion de pago");

        jButton1.setText("Facturar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jTextField_condicionPago, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_condicionPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Total a facturar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jButton2.setText("Calcular");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(55, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(jTextField_total, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jTextField_total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(34, 34, 34)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 940, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(24, 24, 24)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        int contador = 0;
        int contadorTexto = 0;
        int contadorElementos = 0;
        int contadorMayorCantidad = 0;
        ArrayList<String[]> listado = new ArrayList<>();

        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if (jTable1.getValueAt(i, 0).equals(true)) {
                contador++;
                break;
            }
        }

        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if (jTable1.getValueAt(i, 0).equals(true)) {
                contadorElementos++;
            }
        }

        double monto = 0;
        //System.out.println(contadorElementos);
//Verificamos que se haya completado la condicion de pago
        String condicion = jTextField_condicionPago.getText().trim().toUpperCase();
        String clienteFactura = jTextField_cliente.getText().trim();
        if (!condicion.equals("")) {

            if (contadorElementos <= 15) {
                if (contador != 0) {
                    int opcion = JOptionPane.showConfirmDialog(this, "¿Desea facturar los items seleccionados?", "Confirmacion", JOptionPane.INFORMATION_MESSAGE);
                    if (opcion == 0) {
                        for (int i = 0; i < jTable1.getRowCount(); i++) {

                            if (jTable1.getValueAt(i, 0).equals(true)) {

                                if (comprobarGrilla(jTable1.getValueAt(i, 6).toString())) {

                                    double cantidad = Double.parseDouble(jTable1.getValueAt(i, 6).toString());
                                    double precio = Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jTable1.getValueAt(i, 7).toString()));
                                    monto += (cantidad * precio);

                                    String[] nuevo = new String[3];
                                    if (Double.parseDouble(jTable1.getValueAt(i, 6).toString())
                                            <= Double.parseDouble(jTable1.getValueAt(i, 5).toString())) {
                                        //int opcion = JOptionPane.showConfirmDialog(this, "¿Desea facturar los items seleccionados?");

                                        //if (opcion == 0) {
                                        nuevo[0] = jTable1.getValueAt(i, 1).toString();
                                        nuevo[1] = jTable1.getValueAt(i, 6).toString();
                                        nuevo[2] = jTable1.getValueAt(i, 7).toString();
                                        listado.add(nuevo);
                                        //}
                                    } else {

                                        int opcion1 = JOptionPane.showConfirmDialog(this, "¿Desea facturar mayor cantidad de la venta " + jTable1.getValueAt(i, 3).toString() + "?", "Confirmacion", JOptionPane.INFORMATION_MESSAGE);
                                        if (opcion1 == 0) {
                                            nuevo[0] = jTable1.getValueAt(i, 1).toString();
                                            nuevo[1] = jTable1.getValueAt(i, 6).toString();
                                            nuevo[2] = jTable1.getValueAt(i, 7).toString();
                                            listado.add(nuevo);
                                        } else {
                                            contadorMayorCantidad++;
                                        }
                                    }

                                } else {
                                    JOptionPane.showMessageDialog(this, "Las cantidades a remitir no pueden ser textos, \ncantidades negativas ni cero", "Error", JOptionPane.ERROR_MESSAGE);
                                    contadorTexto++;
                                }

                            }
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "No hay trabajos seleccionador para facturar", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Solo es posible incluir máximo 15 ventas por factura."
                        + " \nSi requiere facturar mas lineas, hagalo en una nueva factura", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Complete la condición de pago", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

        if (listado.size() > 0 && contadorTexto == 0 && monto > 0 && contadorMayorCantidad == 0) {

            CargarFactura(listado, condicion, monto, this.idCliente, clienteFactura);
            LimpiarTabla();
            llenarTabla();
            limpiarCampos();

        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        int filas = jTable1.getRowCount();
        ArrayList<Object[]> listado = new ArrayList<>();
        int contador = 0;

        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if (!comprobarGrilla(jTable1.getValueAt(i, 6).toString())) {
                contador++;
                break;
            }
        }

        try {
            if (contador == 0) {
                if (filas > 0) {
                    double suma = 0;
                    for (int i = 0; i < filas; i++) {
                        Object[] nuevo = new Object[9];

                        nuevo[0] = Boolean.parseBoolean(jTable1.getValueAt(i, 0).toString());
                        nuevo[1] = jTable1.getValueAt(i, 1).toString();
                        nuevo[2] = jTable1.getValueAt(i, 2).toString();
                        nuevo[3] = jTable1.getValueAt(i, 3).toString();
                        nuevo[4] = jTable1.getValueAt(i, 4).toString();
                        nuevo[5] = jTable1.getValueAt(i, 5).toString();
                        nuevo[6] = jTable1.getValueAt(i, 6).toString();
                        nuevo[7] = jTable1.getValueAt(i, 7).toString();
                        //double subtotal = Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jTable1.getValueAt(i, 5).toString()));
                        //nuevo[6] = (Object) (MetodosGenerales.ConvertirIntAMoneda(cantidad * subtotal));
                        double cantidad = Double.parseDouble(jTable1.getValueAt(i, 6).toString());
                        double unitario = Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jTable1.getValueAt(i, 7).toString()));
                        nuevo[8] = MetodosGenerales.ConvertirIntAMoneda(cantidad * unitario);

                        if (jTable1.getValueAt(i, 0).equals(true)) {
                            suma += (unitario * cantidad);
                        }

                        listado.add(nuevo);
                    }

                    LimpiarTabla();
                    ActualizarTabla(listado);
                    jTextField_total.setText(MetodosGenerales.ConvertirIntAMoneda(suma));

                } else {
                    JOptionPane.showMessageDialog(this, "No hay datos en la tabla", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Las cantidades a remitir no pueden ser textos, \ncantidades negativas ni cero", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Las cantidades a remitir no pueden ser textos, \ncantidades negativas ni cero", "Error", JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(ListadoParaFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoParaFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoParaFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoParaFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoParaFacturar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_condicionPago;
    private javax.swing.JTextField jTextField_id;
    private javax.swing.JTextField jTextField_total;
    // End of variables declaration//GEN-END:variables
}

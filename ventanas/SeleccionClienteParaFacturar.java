package ventanas;

import java.util.ArrayList;
import clases.Conexion;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import javax.swing.ImageIcon;

public class SeleccionClienteParaFacturar extends javax.swing.JFrame {

    String usuario, permiso;

    public SeleccionClienteParaFacturar() {
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

    }

    public SeleccionClienteParaFacturar(String usuario, String permiso) {
        initComponents();
        this.usuario = usuario;
        this.permiso = permiso;
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Seleccion de cliente *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void IniciarCaracteristicasGenerales() {
        llenarJComboBox();
    }

    public void llenarJComboBox() {

        HashSet<String> clientes = new HashSet<>();

        ArrayList<String> clientesFinal = new ArrayList<>();

        try {
//            String consulta = "select er.id, er.idRemision, er.idVenta, c.nombreCliente, v.descripcionTrabajo, v.unitario, ifnull(er.cantidad - sum(ef.cantidad), er.cantidad) as saldo\n"
//                    + "from elementosremision er left join elementosfactura ef on er.id=ef.idElementoRemito and ef.estado='Activo'\n"
//                    + "left join ventas v on er.idVenta=v.Idventa\n"
//                    + "left join clientes c on v.idCliente=c.idCliente\n"
//                    + "where er.estado='Activo'\n"
//                    + "group by er.id\n"
//                    + "having saldo > 0;";

            String consulta = "select er.id, er.idRemision, er.idVenta, v.descripcionTrabajo, v.unitario, ifnull(er.cantidad - sum(ef.cantidad), er.cantidad) as saldo\n"
                    + "from elementosremision er left join elementosfactura ef on er.id=ef.idElementoRemito and ef.estado='Activo'\n"
                    + "left join ventas v on er.idVenta=v.Idventa\n"
                    + "left join clientes c on v.idCliente=c.idCliente\n"
                    + "where er.estado='Activo'\n"
                    + "group by er.id\n"
                    + "having saldo > 0;";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String nuevo = rs.getString("er.idVenta");
                clientes.add(nuevo);
            }

            String cadena = "(";

            for (String cliente : clientes) {
                cadena += cliente+",";
            }

            int tamaño = cadena.length();
            
            String cadena2 = cadena.substring(0, tamaño - 1);

            String cadenaFinal =cadena2+=")";

            
            String consulta3 = "select distinct c.nombreCliente from clientes c inner join ventas "
                    + "v on c.idCliente=v.Idcliente and v.idVenta in " + cadenaFinal + ";";

            System.out.println("Cadena= "+cadenaFinal);
            
            PreparedStatement pst3 = cn.prepareStatement(consulta3);
            ResultSet rs3 = pst3.executeQuery();

            while (rs3.next()) {
                clientesFinal.add(rs3.getString("c.nombreCliente"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en leer el listado de clientes. SeleccionCliente LlenarJComboBox()\n"
                    + e.getMessage());
            e.printStackTrace();

        }

        for (String cliente : clientesFinal) {
            jComboBox_listaClientes.addItem(cliente);
        }
    }

    public String[] BuscarClienteenBDconNIT(String cedulaNit) {

        String[] datosCliente = new String[2];
        datosCliente[0] = "";
        datosCliente[1] = "";

        try {
            String consulta = "select nombreCliente, idCliente from clientes where identificacion=?";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, cedulaNit);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                datosCliente[0] = rs.getString("nombreCliente");
                datosCliente[1] = rs.getString("idCliente");
            }

            System.out.println(datosCliente[0]);
            System.out.println(datosCliente[1]);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en verificar si el NIT/Cedula ingresado ya existe. SeleccionCliente BuscarClienteenBD()");
        }

        return datosCliente;
    }

    public String BuscarClienteenBDconNombre(String nombreCliente) {

        String datosCliente2 = "";

        try {
            String consulta = "select idCliente from clientes where nombreCliente='" + nombreCliente + "'";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                datosCliente2 = rs.getString("idCliente");

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en verificar si el NIT/Cedula ingresado ya existe. SeleccionCliente BuscarClienteenBD()");
        }

        return datosCliente2;
    }

    public String BuscarIdClientePorNombre(String nombre) {

        String id = "";
        String consulta = "select idCliente from clientes where nombreCliente=?";
        Connection cn = Conexion.Conectar();

        try {

            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, nombre);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                id = rs.getString("idCliente");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en obtener el id del cliente, buscado por NIT."
                    + "Contacte al administrador");
            ex.printStackTrace();
        }

        return id;
    }

    public String BuscarClienteenBD(String cedulaNit) {

        String nombreCliente = "";

        try {
            String consulta = "select nombreCliente from clientes where identificacion=?";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, cedulaNit);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                nombreCliente = rs.getString("nombreCliente");

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en verificar si el NIT/Cedula ingresado ya existe. SeleccionCliente BuscarClienteenBD()");
        }

        return nombreCliente;
    }

    public String BuscarIdClientePorNit(String NIT) {

        String id = "";
        String consulta = "select idCliente from clientes where identificacion=?";
        Connection cn = Conexion.Conectar();

        try {

            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, NIT);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                id = rs.getString("idCliente");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en obtener el id del cliente, buscado por NIT."
                    + "Contacte al administrador");
            ex.printStackTrace();
        }

        return id;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton_cargarVenta = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jComboBox_listaClientes = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_nitcedula = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton_cargarVenta.setText("Cargar factura");
        jButton_cargarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cargarVentaActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Seleccion de cliente para facturacion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jComboBox_listaClientes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        jLabel1.setText("Seleccione el cliente:");

        jLabel2.setText("NIT/Cedula");

        jLabel3.setText("* Seleccione el nombre o razon social de la lista desplegable o bien, ingrese el NIT o cedula en el cuadro de texto");

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
                            .addComponent(jComboBox_listaClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_nitcedula, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel3))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_listaClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_nitcedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(242, 242, 242)
                        .addComponent(jButton_cargarVenta)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_cargarVenta)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_cargarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cargarVentaActionPerformed
        //
        String nombreCliente = jComboBox_listaClientes.getSelectedItem().toString().trim();
        String cedulaNIT = jTextField_nitcedula.getText().trim();

        if (!nombreCliente.equals("")) {

            String idCliente = BuscarIdClientePorNombre(nombreCliente);

            new ListadoParaFacturar(usuario, permiso, idCliente).setVisible(true);
            dispose();
        } else {
            if (!cedulaNIT.equals("")) {
                String Cliente = BuscarClienteenBD(cedulaNIT);
                if (!Cliente.equals("")) {

                    String idCliente = BuscarIdClientePorNit(cedulaNIT);

                    new ListadoParaFacturar(usuario, permiso, idCliente).setVisible(true);
                    dispose();

                } else {
                    JOptionPane.showMessageDialog(this, "El NIT/Cedula ingresado no se encuentra registrado en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente de la lista o ingrese un numero de documento", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        }

//        if (!nombreCliente.equals("")) {
//
//            String idCliente = BuscarClienteenBDconNombre(nombreCliente);
//            new ListadoParaFacturar(usuario, permiso, nombreCliente, idCliente).setVisible(true);
//            dispose();
//
//        } else {
//            if (!cedulaNIT.equals("")) {
//
//                String Cliente = BuscarClienteenBDconNIT(cedulaNIT)[0];
//                String idCliente = BuscarClienteenBDconNIT(cedulaNIT)[1];
//
//                if (!Cliente.equals("")) {
//
//                    new ListadoParaFacturar(usuario, permiso, Cliente, idCliente).setVisible(true);
//                    dispose();
//
//                } else {
//                    JOptionPane.showMessageDialog(null, "El NIT/Cedula ingresado no se encuentra registrado en la base de datos");
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "Seleccione un cliente de la lista o ingrese un numero de documento");
//            }
//        }

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
            java.util.logging.Logger.getLogger(SeleccionClienteParaFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SeleccionClienteParaFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SeleccionClienteParaFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SeleccionClienteParaFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SeleccionClienteParaFacturar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_cargarVenta;
    private javax.swing.JComboBox<String> jComboBox_listaClientes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField_nitcedula;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import java.util.ArrayList;
import clases.Conexion;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;

/**
 *
 * @author erwin
 */
public class SeleccionCliente extends javax.swing.JFrame {

    String usuario, permiso, idPedido;

    /**
     * Creates new form SeleccionCliente
     */
    public SeleccionCliente() {
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();
        


    }

    public SeleccionCliente(String usuario, String permiso) {
        initComponents();
        this.usuario = usuario;
        this.permiso = permiso;
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();
      

  
    }
    
    public SeleccionCliente(String usuario, String permiso, String idPedido) {
        initComponents();
        this.usuario = usuario;
        this.permiso = permiso;
        this.idPedido=idPedido;
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

        ArrayList<String> listaClientes = new ArrayList<String>();

        try {
            String consulta = "select nombreCliente from clientes order by nombreCliente asc";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String cliente = rs.getString("nombreCliente");
                listaClientes.add(cliente);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en leer el listado de clientes. SeleccionCliente LlenarJComboBox()");
        }

        for (String cliente : listaClientes) {
            jComboBox_listaClientes.addItem(cliente);

        }
    }

    public String BuscarClienteenBD(String cedulaNit) {
        
        String nombreCliente="";  
        
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
        jLabel1 = new javax.swing.JLabel();
        jComboBox_listaClientes = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jTextField_nitcedula = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton_cargarVenta.setText("Cargar venta");
        jButton_cargarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cargarVentaActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Seleccion de proveedor para cargar venta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Seleccione el cliente:");

        jComboBox_listaClientes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        jLabel2.setText("NIT/Cedula");

        jLabel3.setText("* Seleccione el nombre o razon social de la lista desplegable o bien, ingrese el NIT o cedula en el cuadro de texto");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField_nitcedula, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox_listaClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_listaClientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_nitcedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addComponent(jLabel3)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(249, 249, 249)
                        .addComponent(jButton_cargarVenta)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_cargarVenta)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_cargarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cargarVentaActionPerformed

        String nombreCliente = jComboBox_listaClientes.getSelectedItem().toString().trim();
        String cedulaNIT = jTextField_nitcedula.getText().trim();
        
        if (!nombreCliente.equals("")) {
            
            new RegistroVentas(usuario, permiso, nombreCliente).setVisible(true);
            dispose();
        } else {
            if (!cedulaNIT.equals("")) {
                String Cliente=BuscarClienteenBD(cedulaNIT);
                if (!Cliente.equals("")) {
                    
                    new RegistroVentas(usuario, permiso, Cliente).setVisible(true);
                    dispose();
                    
                } else {
                    JOptionPane.showMessageDialog(this, "El NIT/Cedula ingresado no se encuentra registrado en la base de datos","Error",JOptionPane.ERROR_MESSAGE);
                }                
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente de la lista o ingrese un numero de documento","Informacion",JOptionPane.INFORMATION_MESSAGE);
            }
        }
        

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
            java.util.logging.Logger.getLogger(SeleccionCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SeleccionCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SeleccionCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SeleccionCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SeleccionCliente().setVisible(true);
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

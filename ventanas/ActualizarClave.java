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
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.mail.MessagingException;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;

/**
 *
 * @author erwin
 */
public class ActualizarClave extends javax.swing.JFrame {

    String usuario, permiso, idUsuarioAActualizar;

    /**
     * Creates new form VentanaActualizarClave
     */
    public ActualizarClave() {
        initComponents();
        InhabilitarCampos();
        ConfiguracionGralJFrame();

    }

    public ActualizarClave(String usuario, String permiso, String idUsuarioAActualizar) {
        initComponents();
        this.usuario = usuario;
        this.permiso = permiso;
        this.idUsuarioAActualizar = idUsuarioAActualizar;
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Cambio de clave de acceso *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void InhabilitarCampos() {
        jTextField_id.setEnabled(false);
        jTextField_usuario.setEnabled(false);

    }

    public void IniciarCaracteristicasGenerales() {
        InhabilitarCampos();
        jTextField_id.setText(this.idUsuarioAActualizar);
        jTextField_usuario.setText(ConsultarNombreUsuario(this.idUsuarioAActualizar));
    }

    public String ConsultarNombreUsuario(String id) {

        String nombreUsuario = "";
        String consulta = "select usuario from empleados where id=?";
//        Conexion nuevo = new Conexion();
//        Connection cn = nuevo.Conectar();
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, id);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                nombreUsuario = rs.getString("usuario");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar el nombre de usuario", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return nombreUsuario;
    }

    public void ActualizarClave(String id, String contraseña, String registradoPor, String user) {

        String nombre = ConsultarNombreUsuario(id);
        String consulta = "update empleados set clave=? where id=?";
        Connection cn = Conexion.Conectar();
        try {

            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, contraseña);
            pst.setString(2, id);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "Clave de usuario actualizada");

            String asunto = "Contraseña del usuario " + user + " cambiada";
            String mensaje = "La contraseña del usuario " + user + " ha sido actualizada"
                    + "\nUsuario responsable: " + this.usuario;

            MetodosGenerales.enviarEmail(asunto, mensaje);
            MetodosGenerales.registrarHistorial(usuario, mensaje);

        } catch (MessagingException ex) {
            ex.printStackTrace();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "SQLException\nError en actualizar la clave del usuario seleccionado", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public String ConsultarPermiso(String id) {

        String tipoPermiso = "";

        String consulta = "select tipoPermiso from empleados where id=?";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, id);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tipoPermiso = rs.getString("tipoPermiso");
            }

            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el tipo de permiso del usuario a editar", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return tipoPermiso;
    }

    public String ConsultarUsuario(String id) {

        String consulta = "select nombreCompleto from empleados where id=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, id);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                return rs.getString("nombreCompleto");
            }

            cn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el nombre completo del usuario", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return "";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton_actualizar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_usuario = new javax.swing.JTextField();
        jLabel_nuevaContraseña = new javax.swing.JLabel();
        jPasswordField_nuevaclave = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jPasswordField_confirmacion = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_id = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton_actualizar.setText("Actualizar");
        jButton_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_actualizarActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Usuario a actualizar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Usuario");

        jTextField_usuario.setEnabled(false);

        jLabel_nuevaContraseña.setText("Nueva contraseña");

        jPasswordField_nuevaclave.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPasswordField_nuevaclaveKeyTyped(evt);
            }
        });

        jLabel2.setText("Confirmacion contraseña");

        jPasswordField_confirmacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPasswordField_confirmacionKeyTyped(evt);
            }
        });

        jLabel3.setText("Id");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel_nuevaContraseña)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jPasswordField_nuevaclave, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jPasswordField_confirmacion, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel_nuevaContraseña)
                    .addComponent(jPasswordField_nuevaclave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jPasswordField_confirmacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(134, 134, 134)
                        .addComponent(jButton_actualizar)))
                .addGap(19, 19, 19))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_actualizar)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_actualizarActionPerformed

        String id = jTextField_id.getText().trim();
        String tipoPermidoBD = ConsultarPermiso(id);
        String user = jTextField_usuario.getText().trim();
        //Verificamos que se hayan llenado los campos contraseña
        String contraseña = MetodosGenerales.encriptarContraseña(jPasswordField_nuevaclave.getText().trim());
        String confirmacion = MetodosGenerales.encriptarContraseña(jPasswordField_confirmacion.getText().trim());
        //Comprobamos que las contraseñas sean iguales
        if (contraseña.equals(confirmacion)) {
            if (!contraseña.equals("") && !confirmacion.equals("")) {
                //Comprobamos el nivel de permisos
                String usuarioLocal = ConsultarUsuario(id);
                if (this.permiso.equals("Gerente")) {
                    ActualizarClave(id, contraseña, this.usuario, user);

                    if (this.usuario.equals(usuarioLocal)) {
                        JOptionPane.showMessageDialog(this, "Dado el cambio de contraseña, debe ingresar nuevamente al programa", "Info", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    } else {
                        dispose();
                    }

                } else if (this.permiso.equals("Administrador")) {
                    if (!tipoPermidoBD.equals("Gerente")) {
                        ActualizarClave(id, contraseña, this.usuario, user);
                        if (this.usuario.equals(usuarioLocal)) {
                            JOptionPane.showMessageDialog(this, "Dado el cambio de contraseña, debe ingresar nuevamente al programa", "Info", JOptionPane.INFORMATION_MESSAGE);
                            System.exit(0);
                        } else {
                            dispose();
                        }

                    } else {
                        JOptionPane.showMessageDialog(this, "No tienes permisos suficientes para editar la contraseña a un perfil Gerente", "Alerta", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (this.permiso.equals("Asistente")) {
                    if (this.usuario.equals(usuarioLocal)) {
                        ActualizarClave(id, contraseña, this.usuario, user);
                        JOptionPane.showMessageDialog(this, "Dado el cambio de contraseña, debe ingresar nuevamente al programa");
                        System.exit(0);
                    } else {
                        JOptionPane.showMessageDialog(this, "Usted no tiene suficientes permisos para editar informacion de otros usuarios", "Alerta", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

            } else {
                JOptionPane.showMessageDialog(this, "Complete la contraseña y su confirmacion. Recuerde que deben ser iguales", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_jButton_actualizarActionPerformed

    private void jPasswordField_nuevaclaveKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField_nuevaclaveKeyTyped
        if (jPasswordField_nuevaclave.getText().trim().length() == 15) {
            evt.consume();
            JOptionPane.showMessageDialog(this, "La contraseña no puede exceder 15 caracteres", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jPasswordField_nuevaclaveKeyTyped

    private void jPasswordField_confirmacionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField_confirmacionKeyTyped
        if (jPasswordField_confirmacion.getText().trim().length() == 15) {
            evt.consume();
            JOptionPane.showMessageDialog(this, "La contraseña no puede exceder 15 caracteres", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jPasswordField_confirmacionKeyTyped

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
            java.util.logging.Logger.getLogger(ActualizarClave.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ActualizarClave.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ActualizarClave.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ActualizarClave.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ActualizarClave().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_actualizar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel_nuevaContraseña;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField_confirmacion;
    private javax.swing.JPasswordField jPasswordField_nuevaclave;
    private javax.swing.JTextField jTextField_id;
    private javax.swing.JTextField jTextField_usuario;
    // End of variables declaration//GEN-END:variables
}

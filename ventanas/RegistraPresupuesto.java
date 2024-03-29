/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.ImageIcon;

/**
 *
 * @author erwin
 */
public class RegistraPresupuesto extends javax.swing.JFrame {

    String usuario, permiso;

    /**
     * Creates new form RegistraPresupuesto
     */
    public RegistraPresupuesto() {
        initComponents();        
        ConfiguracionGralJFrame();
    }

    public RegistraPresupuesto(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        ConfiguracionGralJFrame();

    }
    
    public void ConfiguracionGralJFrame(){
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Crear nuevo presupuesto *** "+"Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        
    }
    
    
    public void RegistrarPresupuesto(String fechapresupuesto, String descripcion, String estado, String fechaInicio, 
            String fechaFin, String usuario){
        
        String consulta = "insert into presupuestos (fecha, descripcion, estado, fechaInicio, fechaFin, registradoPor) "
                + "values (?, ?, ?, ?, ?, ?)";
        
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            
            pst.setString(1, fechapresupuesto);
            pst.setString(2, descripcion);
            pst.setString(3, estado);
            pst.setString(4, fechaInicio);
            pst.setString(5, fechaFin);
            pst.setString(6, usuario);
            
            pst.executeUpdate();
            cn.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar el presupuesto. RegistrarPresupuesto RegistrarPresupuesto()");
            e.printStackTrace();
        }
        
    }

//    public void SoloNumero(JTextField jtxt) {
//        jtxt.addKeyListener(new KeyAdapter() {
//            public void keyTiped(KeyEvent e) {
//                char c = e.getKeyChar();
//                if (!Character.isDigit(c) && c != '-') {
//                    e.consume();
//                }
//                if (c == '-' && jtxt.getText().contains("-")) {
//                    e.consume();
//                }
//            }
//
//        });
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDateChooser_inicio = new com.toedter.calendar.JDateChooser();
        jDateChooser_fin = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField_descripcion = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Fecha Inicio");

        jLabel2.setText("Fecha Fin");

        jLabel3.setText("Descripcion");

        jTextField_descripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_descripcionActionPerformed(evt);
            }
        });
        jTextField_descripcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_descripcionKeyTyped(evt);
            }
        });

        jButton1.setText("Crear presupuesto");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(186, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(194, 194, 194))
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jDateChooser_inicio, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jDateChooser_fin, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jDateChooser_fin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDateChooser_inicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addComponent(jButton1)
                .addGap(37, 37, 37))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //Capturamos los datos del formulario

        String descripcion = jTextField_descripcion.getText().trim();
        
        //Verificamos que todos los campos esten completos
        if (jDateChooser_fin.getDate()!=null && jDateChooser_inicio.getDate()!=null 
                && !descripcion.equals("")) {
            
            //Capturamos los datos faltantes
            String fechaInicio = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_inicio.getDate());
            String fechaFin = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_fin.getDate());
            String fechapresupuesto = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            
            RegistrarPresupuesto(fechapresupuesto, descripcion, "Pendiente Aut.", fechaInicio, fechaFin, this.usuario);
            new ListadoPresupuestos(this.usuario, this.permiso).setVisible(true);
            dispose();
            
        } else {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios");
        }
        
        //

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField_descripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_descripcionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_descripcionActionPerformed

    private void jTextField_descripcionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_descripcionKeyTyped
        if (jTextField_descripcion.getText().trim().length()==100) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_descripcionKeyTyped

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
            java.util.logging.Logger.getLogger(RegistraPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistraPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistraPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistraPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistraPresupuesto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private com.toedter.calendar.JDateChooser jDateChooser_fin;
    private com.toedter.calendar.JDateChooser jDateChooser_inicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField_descripcion;
    // End of variables declaration//GEN-END:variables
}

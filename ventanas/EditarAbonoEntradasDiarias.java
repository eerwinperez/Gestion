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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 *
 * @author erwin
 */
public class EditarAbonoEntradasDiarias extends javax.swing.JFrame {

    String usuario, permiso, cliente, idAbono, observaciones, fecha, idVenta;
    Double abono;
    String[] datos;

    /**
     * Creates new form EditarAbonoEntradasDiarias
     */
    public EditarAbonoEntradasDiarias() {
        initComponents();
    }

    public EditarAbonoEntradasDiarias(String usuario, String permiso, String cliente, String idAbono, Double abono, String observaciones, String fecha, String idVenta) {

        initComponents();
        this.usuario = usuario;
        this.permiso = permiso;
        this.cliente = cliente;
        this.idAbono = idAbono;
        this.abono = abono;
        this.observaciones = observaciones;
        this.fecha = fecha;
        this.idVenta = idVenta;
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

        //Inhabilitamos botones y campos si el usuario no tiene los permisos necesarios
        if (!permiso.equals("Gerente")) {
            jButton_editar.setEnabled(false);
        }

    }

    public void InhabilitarCampos() {

        jTextField_Cliente.setEnabled(false);
        jTextField_idAbono.setEnabled(false);

    }

    public void CargarDatosEnFormulario() {
        jTextField_idAbono.setText(this.idAbono);
        jTextField_Cliente.setText(this.cliente);
        jTextField_valor.setText(String.valueOf(this.abono));
        jTextField_ObsRegistra.setText(this.observaciones);

        try {
            jDateChooser1.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(this.fecha));
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Error al parsear al fecha");
            ex.printStackTrace();
        }

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Editar abono *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void IniciarCaracteristicasGenerales() {

        InhabilitarCampos();
        CargarDatosEnFormulario();

    }

    public void EditarAbono(String idAbono, String valor, String observaciones, String fecha) {
        String consulta = "update abonos set valor=?, fecha=?, observaciones=? where idAbono=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, valor);
            pst.setString(2, fecha);
            pst.setString(3, observaciones);
            pst.setString(4, idAbono);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Abono actualizado");
            cn.close();

            String asunto = "Abono " + this.idAbono + " editado - Entradas diarias";
            String mensaje = "El abono No. " + this.idAbono + " ha sido editado"
                    + "\nValor anterior = " + MetodosGenerales.ConvertirIntAMoneda(this.abono)
                    + " - nuevo valor = " + MetodosGenerales.ConvertirIntAMoneda(Double.parseDouble(valor))
                    + "\nFecha anterior = " + this.fecha + " - nueva fecha = " + fecha
                    + "\nUsuario responsable: " + this.usuario
                    + "\nObservaciones: " + observaciones;

            MetodosGenerales.enviarEmail(asunto, mensaje);
            MetodosGenerales.registrarHistorial(usuario, mensaje);

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MessagingException ex) {
            ex.printStackTrace();
        } catch (HeadlessException | NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "SQLException\nError en actualizar el abono");
            e.printStackTrace();
        }
    }

    public boolean consultaMontos(String idVenta, Double nuevoAbono) {

        String consulta = "select sum(valor) as total from abonos where idVenta=?";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idVenta);

            ResultSet rs = pst.executeQuery();
            double abonos = 0;
            if (rs.next()) {
                abonos += rs.getDouble("total");
            }
            cn.close();

            double totalVenta = 0;

            Connection cn2 = Conexion.Conectar();
            String consulta2 = "select precio from ventas where Idventa=?";
            PreparedStatement pst2 = cn2.prepareStatement(consulta2);
            pst2.setString(1, idVenta);

            ResultSet rs2 = pst2.executeQuery();
            if (rs2.next()) {
                totalVenta = rs2.getDouble("precio");
            }
            cn2.close();

            if ((abonos - this.abono + nuevoAbono) <= totalVenta) {
                return true;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener el monto del abono");
            e.printStackTrace();
        }

        return false;
    }

//    public void ActualizarAbono(String idAbono, String abono, String observacionesRegistra, String observacionesGerente) {
//
//        String consulta = "update abonos set valorAbono=?, observaciones=?, observacionesGerente=? where idAbono=?";
//
//        Connection cn = Conexion.Conectar();
//        try {
//            PreparedStatement pst = cn.prepareStatement(consulta);
//            pst.setString(1, abono);
//            pst.setString(2, observacionesRegistra);
//            pst.setString(3, observacionesGerente);
//            pst.setString(4, idAbono);
//
//            pst.executeUpdate();
//            JOptionPane.showMessageDialog(this, "Abono actualizado");
//            cn.close();
//
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Error en actualizar la informacion del abono");
//        }
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_idAbono = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_valor = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_Cliente = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField_ObsRegistra = new javax.swing.JTextField();
        jButton_editar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Abono a editar", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Id Abono");

        jLabel3.setText("Valor");

        jTextField_valor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_valorKeyTyped(evt);
            }
        });

        jLabel4.setText("Cliente");

        jLabel5.setText("Observaciones");

        jTextField_ObsRegistra.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_ObsRegistraKeyTyped(evt);
            }
        });

        jButton_editar.setText("Editar");
        jButton_editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_editarActionPerformed(evt);
            }
        });

        jLabel2.setText("Fecha");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(206, 206, 206)
                        .addComponent(jButton_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField_ObsRegistra))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel3))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTextField_valor, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel2)
                                        .addGap(18, 18, 18)
                                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTextField_idAbono, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel4)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField_Cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(34, 34, 34)))))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_idAbono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_Cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jTextField_valor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField_ObsRegistra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(jButton_editar)
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField_valorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_valorKeyTyped
        char c = evt.getKeyChar();

        if (!Character.isDigit(c) && c != '.') {
            evt.consume();
        }

        if (c == '0' && jTextField_valor.getText().trim().length() == 0) {
            evt.consume();
        }

        if (c == '.' && jTextField_valor.getText().trim().length() == 0) {
            evt.consume();
        }

        int contador = 0;

        char[] cadena = jTextField_valor.getText().trim().toCharArray();
        for (int i = 0; i < jTextField_valor.getText().trim().length(); i++) {
            if (cadena[i] == '.') {
                contador++;
                break;
            }
        }

        if (contador > 0 && c == '.') {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_valorKeyTyped

    private void jTextField_ObsRegistraKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_ObsRegistraKeyTyped
        if (jTextField_ObsRegistra.getText().trim().length() == 250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_ObsRegistraKeyTyped

    private void jButton_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_editarActionPerformed
        //Verificamos que se haya ingresado el valor
        String valor = jTextField_valor.getText().trim();
        if (!valor.equals("")) {
            try {
                String fecha = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser1.getDate());
                String observaciones = jTextField_ObsRegistra.getText().trim().toUpperCase();
                int opcion = JOptionPane.showConfirmDialog(this, "¿Desea editar el valor del abono?", "Confirmacion", JOptionPane.INFORMATION_MESSAGE);
                if (opcion == 0) {
                    boolean comprobacion = consultaMontos(this.idVenta, Double.parseDouble(valor));
                    if (comprobacion) {

                        EditarAbono(this.idAbono, valor, observaciones, fecha);
                        dispose();
                        new ListadoAbonosEntradasDiarias(usuario, permiso).setVisible(true);

                    } else {
                        JOptionPane.showMessageDialog(this, "No es posible ingresar el monto del abono ya que la suma de abonos supera el monto de la venta", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Fecha o monto incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

        } else {
            JOptionPane.showMessageDialog(this, "Ingrese el valor del abono", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton_editarActionPerformed

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
            java.util.logging.Logger.getLogger(EditarAbonoEntradasDiarias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditarAbonoEntradasDiarias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditarAbonoEntradasDiarias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditarAbonoEntradasDiarias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EditarAbonoEntradasDiarias().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_editar;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField_Cliente;
    private javax.swing.JTextField jTextField_ObsRegistra;
    private javax.swing.JTextField jTextField_idAbono;
    private javax.swing.JTextField jTextField_valor;
    // End of variables declaration//GEN-END:variables
}

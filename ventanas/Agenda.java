/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import java.awt.HeadlessException;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author erwin
 */
public class Agenda extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso;

    /**
     * Creates new form Agenda
     */
    public Agenda() {
        initComponents();
        InhabilitarCampos();
        ConfiguracionGralJFrame();
        
        
    }

    public Agenda(String usuario, String permiso) {
        
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        llenarTabla();        
        InhabilitarCampos();
        ConfiguracionGralJFrame();
        
        if (!permiso.equals("Gerente") && !permiso.equals("Administrador")) {
            InhabilitarCamposPorPermiso();
        } 
  
    }

   
    
    
    
    public void InhabilitarCampos(){
        jTextField_id.setEnabled(false);     
        jLabe_fila.setVisible(false);
    }
    
    public void InhabilitarCamposPorPermiso(){
        jButton_actualizar.setEnabled(false);       
        
    }

    public void limpiarCampos() {

        jTextField_direccion.setText("");
        jTextField_email.setText("");
        jTextField_id.setText("");
        jTextField_nombre.setText("");
        jTextField_observaciones.setText("");
        jTextField_telefono.setText("");
        jLabe_fila.setText("");
        jButton_registrar.setEnabled(true);

    }
    
        
    public void ConfiguracionGralJFrame(){
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Agenda de contactos *** "+"Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        
    }

    public void RegistrarContacto(String nombre, String telefono, String email, String direccion, String observaciones,
            String usuario) {

        String consulta = "insert into agenda (nombre, telefono, email, direccion, observaciones, registradoPor) values "
                + "(?, ?, ?, ?, ?, ?)";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, nombre);
            pst.setString(2, telefono);
            pst.setString(3, email);
            pst.setString(4, direccion);
            pst.setString(5, observaciones);
            pst.setString(6, usuario);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Contacto registrado");
            cn.close();

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();            
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "SQLException\nError en registrar el contacto. Agenda RegistrarContacto()","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public boolean VerificarNombre(JTable tabla, String nombre) {
        boolean verificacion = false;
        for (int i = 0; i < tabla.getRowCount(); i++) {
            if (tabla.getValueAt(i, 1).toString().equals(nombre)) {
                verificacion = true;
                break;
            }
        }

        return verificacion;
    }
    
    public boolean VerificarNombreEnTabla(JTable tabla, String nombre, String fila){
        boolean verificacion=false;
        //Recorremos todas las filas de la tabla comparando en cada fila el nuevo nombre del contacto con los contactos
        //ya registrados, sin tener en cuenta la fila seleccionada
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if (i!=Integer.parseInt(fila)) {
                if (nombre.equals(jTable1.getValueAt(i, 1).toString())) {
                    verificacion=true;
                }
            }
        }
        return verificacion;
    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            this.modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void llenarTabla() {
        modelo = (DefaultTableModel) jTable1.getModel();

        String consulta = "select id, nombre, telefono, email, direccion, observaciones, registradoPor from agenda order by id desc";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[7];
                nuevo[0] = rs.getString("id");
                nuevo[1] = rs.getString("nombre");
                nuevo[2] = rs.getString("telefono");
                nuevo[3] = rs.getString("email");
                nuevo[4] = rs.getString("direccion");
                nuevo[5] = rs.getString("observaciones");
                nuevo[6] = rs.getString("registradoPor");

                modelo.addRow(nuevo);
            }
            
            jTable1.setModel(modelo);
            
            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable1.setRowSorter(ordenador);
            
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer los datos de la agenda. Agenda llenarTabla()");
            e.printStackTrace();
        }
    }
    
    public void ActualizarContacto(String id, String nombre, String telefono, String email, String direccion, 
            String observaciones, String usuario){
        
        String consulta="update agenda set nombre=?, telefono=?, email=?, direccion=?, observaciones=?,"
                + " registradoPor=? where id=?";
        Connection cn = Conexion.Conectar();
        try {
            
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, nombre);
            pst.setString(2, telefono);
            pst.setString(3, email);
            pst.setString(4, direccion);
            pst.setString(5, observaciones);
            pst.setString(6, usuario);
            pst.setString(7, id);
                    
            pst.executeUpdate();            
            cn.close();
            JOptionPane.showMessageDialog(this, "Contacto actualizado");
        
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "MySQLException\nError en actualizar el usuario. Agenda ActualizarContacto()");
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton_registrar = new javax.swing.JButton();
        jButton_actualizar = new javax.swing.JButton();
        jLabe_fila = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_id = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_nombre = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_telefono = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_email = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField_direccion = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField_observaciones = new javax.swing.JTextField();
        jButton_campos = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombre", "Telefono", "Email", "Direccion", "Observaciones", "Registrado por"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(120);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
        }

        jButton_registrar.setText("Registra nuevo");
        jButton_registrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_registrarActionPerformed(evt);
            }
        });

        jButton_actualizar.setText("Actualizar");
        jButton_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_actualizarActionPerformed(evt);
            }
        });

        jLabe_fila.setText("jLabel7");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos de contacto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Id ");

        jLabel2.setText("Nombre");

        jTextField_nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_nombreKeyTyped(evt);
            }
        });

        jLabel4.setText("Telefono");

        jTextField_telefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_telefonoKeyTyped(evt);
            }
        });

        jLabel3.setText("Email");

        jTextField_email.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_emailKeyTyped(evt);
            }
        });

        jLabel5.setText("Direccion");

        jTextField_direccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_direccionKeyTyped(evt);
            }
        });

        jLabel6.setText("Observaciones");

        jTextField_observaciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_observacionesKeyTyped(evt);
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
                        .addGap(21, 21, 21)
                        .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addGap(21, 21, 21)
                        .addComponent(jTextField_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(21, 21, 21)
                        .addComponent(jTextField_email, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jButton_campos.setText("Limpiar campos");
        jButton_campos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_camposActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabe_fila)
                        .addGap(187, 187, 187)
                        .addComponent(jButton_campos)
                        .addGap(27, 27, 27)
                        .addComponent(jButton_registrar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane1)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_campos)
                    .addComponent(jButton_registrar)
                    .addComponent(jButton_actualizar)
                    .addComponent(jLabe_fila))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int fila = jTable1.getSelectedRow();
        if (fila != -1) {

            jTextField_id.setText(jTable1.getValueAt(fila, 0).toString());
            jTextField_nombre.setText(jTable1.getValueAt(fila, 1).toString());
            jTextField_telefono.setText(jTable1.getValueAt(fila, 2).toString());
            jTextField_email.setText(jTable1.getValueAt(fila, 3).toString());
            jTextField_direccion.setText(jTable1.getValueAt(fila, 4).toString());
            jTextField_observaciones.setText(jTable1.getValueAt(fila, 5).toString());
            jLabe_fila.setText(String.valueOf(fila));
            jButton_registrar.setEnabled(false);

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto para edidtar");
        }

    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton_camposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_camposActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_jButton_camposActionPerformed

    private void jButton_registrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_registrarActionPerformed
        //Capturamos los datos ingresados
        String nombre = jTextField_nombre.getText().trim().toUpperCase();
        String telefono = jTextField_telefono.getText().trim().toUpperCase();
        String email = jTextField_email.getText().trim().toUpperCase();
        String direccion = jTextField_direccion.getText().trim().toUpperCase();
        String observaciones = jTextField_observaciones.getText().trim().toUpperCase();
         
        //Verificamos que todos los campos este completos
        if (!nombre.equals("") && !telefono.equals("") && !direccion.equals("")) {
            boolean verificacion = VerificarNombre(jTable1, nombre);
            if (!verificacion) {
                RegistrarContacto(nombre, telefono, email, direccion, observaciones, this.usuario);
                limpiarCampos();
                limpiarTabla(modelo);
                llenarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "El nombre de contacto que intenta ingresar ya existe en la base de datos","Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Los campos nombre, telefono y direccion direccion son obligatorios","Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton_registrarActionPerformed

    private void jButton_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_actualizarActionPerformed
        //Verificamos que se haya seleccionado un contacto
        String id = jTextField_id.getText().trim();
        if (!id.equals("")) {
            //Obtenemos el resto de datos
            String nombre = jTextField_nombre.getText().trim().toUpperCase();
            String telefono = jTextField_telefono.getText().trim().toUpperCase();
            String email = jTextField_email.getText().trim().toUpperCase();
            String direccion = jTextField_direccion.getText().trim().toUpperCase();
            String observaciones = jTextField_observaciones.getText().trim().toUpperCase();
            String fila =jLabe_fila.getText().trim().toUpperCase();

            if (!nombre.equals("") && !telefono.equals("") && !direccion.equals("")) {
                //Verificamos que nombre no exista en la Tabla
                boolean verificacion = VerificarNombreEnTabla(jTable1, nombre, fila);
                if (!verificacion) {
                    ActualizarContacto(id, nombre, telefono, email, direccion, observaciones, this.usuario);
                    limpiarCampos();
                    limpiarTabla(modelo);
                    llenarTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "El nombre de contacto que intenta ingresar ya existe en la base de datos", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Los campos nombre, telefono y direccion direccion son obligatorios", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto para editar", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton_actualizarActionPerformed

    private void jTextField_nombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_nombreKeyTyped
        if (jTextField_nombre.getText().trim().length()==250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_nombreKeyTyped

    private void jTextField_telefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_telefonoKeyTyped
        if (jTextField_telefono.getText().trim().length()==250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_telefonoKeyTyped

    private void jTextField_emailKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_emailKeyTyped
       if (jTextField_email.getText().trim().length()==250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_emailKeyTyped

    private void jTextField_direccionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_direccionKeyTyped
        if (jTextField_direccion.getText().trim().length()==250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_direccionKeyTyped

    private void jTextField_observacionesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_observacionesKeyTyped
        if (jTextField_observaciones.getText().trim().length()==250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_observacionesKeyTyped

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
            java.util.logging.Logger.getLogger(Agenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Agenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Agenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Agenda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Agenda().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_actualizar;
    private javax.swing.JButton jButton_campos;
    private javax.swing.JButton jButton_registrar;
    private javax.swing.JLabel jLabe_fila;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField_direccion;
    private javax.swing.JTextField jTextField_email;
    private javax.swing.JTextField jTextField_id;
    private javax.swing.JTextField jTextField_nombre;
    private javax.swing.JTextField jTextField_observaciones;
    private javax.swing.JTextField jTextField_telefono;
    // End of variables declaration//GEN-END:variables
}

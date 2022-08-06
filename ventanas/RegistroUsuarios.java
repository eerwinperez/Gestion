/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.Empleados;
import clases.MetodosGenerales;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.mail.MessagingException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author erwin
 */
public class RegistroUsuarios extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso;

    /**
     * Creates new form DetalleUsuarios
     */
    public RegistroUsuarios() {
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

    }

    public RegistroUsuarios(String usuario, String permiso) {
        this.permiso = permiso;
        this.usuario = usuario;
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

        if (permiso.equals("Asistente")) {
            InhabilitarPorPermiso();
        }

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Usuarios *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void IniciarCaracteristicasGenerales() {
        InhabilitarCampos();
        llenarTabla();
    }

    public void InhabilitarPorPermiso() {
        jButton_actualizarUsuario.setEnabled(false);
        jButton_crearUsuario.setEnabled(false);
    }

    public void limpiarCampos() {

        if (!this.permiso.equals("Asistente")) {
            jTextField_cargo.setText("");
            jTextField_nombre.setText("");
            jTextField_usuario.setText("");
            jComboBox_estado.setSelectedIndex(0);
            jComboBox_permiso.setSelectedIndex(0);
            jPasswordField_clave.setText("");
            jPasswordField_clave.setEnabled(true);
            jTextField_idUsuario.setText("");
            jButton_crearUsuario.setEnabled(true);
            jTextField_usuario.setEnabled(true);
            jLabel_permiso.setText("");
        }

    }

    public void limpiarLista(DefaultTableModel model) {
        for (int i = 0; i < jTable_Empleados.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void InhabilitarCampos() {
        jTextField_idUsuario.setEnabled(false);
        jLabel_permiso.setVisible(false);

    }

    public void llenarTabla() {
        try {
            String consulta = "select id, nombreCompleto, usuario, cargo, "
                    + "tipoPermiso, estado, registradoPor from empleados order by id desc";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable_Empleados.getModel();

            while (rs.next()) {
                Object[] empleados = new Object[7];

                empleados[0] = rs.getString("id");
                empleados[1] = rs.getString("nombreCompleto");
                empleados[2] = rs.getString("usuario");
                empleados[3] = rs.getString("cargo");
                empleados[4] = rs.getString("tipoPermiso");
                empleados[5] = rs.getString("estado");
                empleados[6] = rs.getString("registradoPor");

                modelo.addRow(empleados);
            }
            jTable_Empleados.setModel(modelo);
            
            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable_Empleados.setRowSorter(ordenador);
            
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos en la tabla usuarios desde la base de datos");
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
            JOptionPane.showMessageDialog(this, "Error en leer el tipo de permiso del usuario a editar");
        }
        return tipoPermiso;
    }

    public void ActualizarUsuario(String id, String nombre, String cargo, String permiso,
            String estado, String registradoPor) {

        String consulta = "update empleados set nombreCompleto=?, cargo=?, tipoPermiso=?, estado=?, "
                + "registradoPor=? where id=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, nombre);
            pst.setString(2, cargo);
            pst.setString(3, permiso);
            pst.setString(4, estado);
            pst.setString(5, registradoPor);
            pst.setString(6, id);

            pst.executeUpdate();
            cn.close();

            JOptionPane.showMessageDialog(this, "Usuario actualizado", "Informacion", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQLException\nError en actualizar el usuario \n" + e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean ConsultarNombreUsuario(String nombre) {

        String consulta = "select usuario from empleados where usuario=?";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, nombre);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                return true;

            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }
        return false;
    }

    public void IngresarEmpleado(String nombreCompleto, String usuario, String cargo, String permiso,
            String estado, String clave, String registradoPor) {

        try {

            String consulta = "insert into empleados (nombreCompleto, usuario , cargo, tipoPermiso, estado, clave, registradoPor)"
                    + " values (?, ?, ?, ?, ?, ?, ?)";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, nombreCompleto);
            pst.setString(2, usuario);
            pst.setString(3, cargo);
            pst.setString(4, permiso);
            pst.setString(5, estado);
            pst.setString(6, clave);
            pst.setString(7, registradoPor);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "Usuario ingresado. Para entrar al programa deberá introducir "
                    + "las credenciales exactas, incluidas las mayúsculas.\n\nUsuario: " + usuario + "\nClave: " + clave, "Informacion", JOptionPane.INFORMATION_MESSAGE);

            String asunto = "Nuevo usuario registrado :" + nombreCompleto;
            String mensaje = "Nuevo usuario registrado"
                    + "\nNombre completo: " + nombreCompleto
                    + "\nUsuario: " + usuario
                    + "\nPermiso: " + permiso
                    + "\nUsuario responsable: " + this.usuario;

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
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQLException\nError al registrar el usuario. Asegurese de no estar introduciendo un nombre de"
                    + " usuario ya existente. Si el problema persiste, comuniquese con el administrador", "Error", JOptionPane.ERROR_MESSAGE);
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
        jTable_Empleados = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_nombre = new javax.swing.JTextField();
        jTextField_usuario = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_cargo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jComboBox_permiso = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPasswordField_clave = new javax.swing.JPasswordField();
        jComboBox_estado = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jButton_crearUsuario = new javax.swing.JButton();
        jButton_actualizarUsuario = new javax.swing.JButton();
        jButton_actualizarClave = new javax.swing.JButton();
        jButton_limpiarCampos = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jTextField_idUsuario = new javax.swing.JTextField();
        jLabel_permiso = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_Empleados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombre", "Usuario", "Cargo", "Permiso", "Estado", "Registrado por"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_Empleados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_Empleados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_EmpleadosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_Empleados);
        if (jTable_Empleados.getColumnModel().getColumnCount() > 0) {
            jTable_Empleados.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable_Empleados.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable_Empleados.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTable_Empleados.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTable_Empleados.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTable_Empleados.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTable_Empleados.getColumnModel().getColumn(6).setPreferredWidth(150);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del usuario", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Nombre");

        jTextField_nombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_nombreKeyTyped(evt);
            }
        });

        jTextField_usuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_usuarioActionPerformed(evt);
            }
        });
        jTextField_usuario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_usuarioKeyTyped(evt);
            }
        });

        jLabel2.setText("Usuario");

        jTextField_cargo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_cargoKeyTyped(evt);
            }
        });

        jLabel3.setText("Cargo");

        jComboBox_permiso.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Asistente", "Administrador", "Gerente" }));

        jLabel4.setText("Permiso");

        jLabel6.setText("Clave");

        jPasswordField_clave.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPasswordField_claveKeyTyped(evt);
            }
        });

        jComboBox_estado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activo", "Inactivo" }));

        jLabel5.setText("Estado");

        jButton_crearUsuario.setText("Crear usuario");
        jButton_crearUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_crearUsuarioActionPerformed(evt);
            }
        });

        jButton_actualizarUsuario.setText("Actualizar usuario");
        jButton_actualizarUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_actualizarUsuarioActionPerformed(evt);
            }
        });

        jButton_actualizarClave.setText("Actualizar clave");
        jButton_actualizarClave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_actualizarClaveActionPerformed(evt);
            }
        });

        jButton_limpiarCampos.setText("Limpiar campos");
        jButton_limpiarCampos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_limpiarCamposActionPerformed(evt);
            }
        });

        jLabel7.setText("Id");

        jLabel_permiso.setText("jLabel8");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox_permiso, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(jPasswordField_clave, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox_estado, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(64, 64, 64)
                        .addComponent(jLabel_permiso))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_idUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_cargo, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jButton_crearUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_actualizarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_actualizarClave, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_limpiarCampos, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_cargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField_idUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox_permiso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jPasswordField_clave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox_estado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_permiso))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_crearUsuario)
                    .addComponent(jButton_actualizarUsuario)
                    .addComponent(jButton_actualizarClave)
                    .addComponent(jButton_limpiarCampos))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_actualizarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_actualizarUsuarioActionPerformed
        //Verificamos que se haya seleccionado un usuario
        String id = jTextField_idUsuario.getText().trim();

        if (!id.equals("")) {
            //Verificamos que se hayan completado todos los campos
            String ususarioLocal = jTextField_usuario.getText().trim().toUpperCase();
            String nombre = jTextField_nombre.getText().trim().toUpperCase();
            String cargo = jTextField_cargo.getText().trim().toUpperCase();
            String permisoLocal = jComboBox_permiso.getSelectedItem().toString().toUpperCase();
            String estado = jComboBox_estado.getSelectedItem().toString().toUpperCase();
            String permiso = jLabel_permiso.getText().trim().toUpperCase();

            if (!nombre.equals("") && !cargo.equals("") && !ususarioLocal.equals("")) {

                //Verificamos que segun el permiso se puedan o no editar los datos de un usuario
                //String permisoBD = ConsultarPermiso(id);
                if (this.permiso.equalsIgnoreCase("Gerente")) {

                    ActualizarUsuario(id, nombre, cargo, permisoLocal, estado, this.usuario);
                    limpiarCampos();
                    limpiarLista(modelo);
                    llenarTabla();

                } else if (this.permiso.equalsIgnoreCase("Administrador") && !permiso.equalsIgnoreCase("Gerente")) {
                    //Verificamos que no este intentando crear un usuario gerente con un usuario administrador
//                    if (!permisoLocal.equals("Gerente")) {
                    ActualizarUsuario(id, nombre, cargo, permisoLocal, estado, this.usuario);
                    limpiarCampos();
                    limpiarLista(modelo);
                    llenarTabla();

                } else {
                    JOptionPane.showMessageDialog(this, "No tiene permisos suficientes para editar los datos "
                            + "\ndel usuario seleccionado", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Complete todos los campos", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton_actualizarUsuarioActionPerformed

    private void jButton_crearUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_crearUsuarioActionPerformed

        //Verificamos que se hayan ingresado todos los campos
        String nombre = jTextField_nombre.getText().trim().toUpperCase();
        String usuariolocal = jTextField_usuario.getText().trim().toUpperCase();
        String cargo = jTextField_cargo.getText().trim().toUpperCase();
        String permisolocal = jComboBox_permiso.getSelectedItem().toString().trim().toUpperCase();
        String estado = jComboBox_estado.getSelectedItem().toString().trim().toUpperCase();
        String clave = MetodosGenerales.encriptarContraseña(jPasswordField_clave.getText().trim());

        if (!nombre.equals("") && !usuariolocal.equals("") && !cargo.equals("") && !permisolocal.equals("")
                && !estado.equals("") && !clave.equals("")) {

            if (this.permiso.equalsIgnoreCase("Gerente")) {
                IngresarEmpleado(nombre, usuariolocal, cargo, permisolocal, estado, clave, this.usuario);
                limpiarCampos();
                limpiarLista(modelo);
                llenarTabla();
            } else if (this.permiso.equalsIgnoreCase("Administrador") && !permisolocal.equalsIgnoreCase("Gerente")) {
                IngresarEmpleado(nombre, usuariolocal, cargo, permisolocal, estado, clave, this.usuario);
                limpiarCampos();
                limpiarLista(modelo);
                llenarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No tiene suficiente permiso para crear un usuario Gerente", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Complete todos los campos", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton_crearUsuarioActionPerformed

    private void jTextField_usuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_usuarioActionPerformed

    }//GEN-LAST:event_jTextField_usuarioActionPerformed

    private void jTable_EmpleadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_EmpleadosMouseClicked
        int fila = jTable_Empleados.getSelectedRow();

        if (fila != -1) {
            jTextField_idUsuario.setText(jTable_Empleados.getValueAt(fila, 0).toString());
            jTextField_nombre.setText(jTable_Empleados.getValueAt(fila, 1).toString());
            jTextField_usuario.setText(jTable_Empleados.getValueAt(fila, 2).toString());
            jTextField_cargo.setText(jTable_Empleados.getValueAt(fila, 3).toString());
            jComboBox_permiso.setSelectedItem(jTable_Empleados.getValueAt(fila, 4).toString());
            jComboBox_estado.setSelectedItem(jTable_Empleados.getValueAt(fila, 5).toString());
            jPasswordField_clave.setEnabled(false);
            jTextField_usuario.setEnabled(false);
            jButton_crearUsuario.setEnabled(false);
            jLabel_permiso.setText(jTable_Empleados.getValueAt(fila, 4).toString().trim());

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila");
        }


    }//GEN-LAST:event_jTable_EmpleadosMouseClicked

    private void jButton_limpiarCamposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_limpiarCamposActionPerformed
        limpiarCampos();

    }//GEN-LAST:event_jButton_limpiarCamposActionPerformed

    private void jButton_actualizarClaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_actualizarClaveActionPerformed
        //Verificamos que se haya seleccionado un usuario

        Frame[] ventanas = getFrames();

        for (Frame ventana : ventanas) {
            if (ventana instanceof ActualizarClave) {
                ventana.dispose();
            }
        }

        String id = jTextField_idUsuario.getText().trim();
        if (!id.equals("")) {

            limpiarCampos();
            new ActualizarClave(usuario, permiso, id).setVisible(true);

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton_actualizarClaveActionPerformed

    private void jTextField_nombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_nombreKeyTyped
        if (jTextField_nombre.getText().trim().length() == 100) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_nombreKeyTyped

    private void jTextField_usuarioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_usuarioKeyTyped
        if (jTextField_usuario.getText().trim().length() == 100) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_usuarioKeyTyped

    private void jTextField_cargoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_cargoKeyTyped
        if (jTextField_cargo.getText().trim().length() == 100) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_cargoKeyTyped

    private void jPasswordField_claveKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField_claveKeyTyped
        if (jPasswordField_clave.getText().trim().length() == 50) {
            evt.consume();
        }
    }//GEN-LAST:event_jPasswordField_claveKeyTyped

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
            java.util.logging.Logger.getLogger(RegistroUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroUsuarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistroUsuarios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_actualizarClave;
    private javax.swing.JButton jButton_actualizarUsuario;
    private javax.swing.JButton jButton_crearUsuario;
    private javax.swing.JButton jButton_limpiarCampos;
    private javax.swing.JComboBox<String> jComboBox_estado;
    private javax.swing.JComboBox<String> jComboBox_permiso;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel_permiso;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField_clave;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_Empleados;
    private javax.swing.JTextField jTextField_cargo;
    private javax.swing.JTextField jTextField_idUsuario;
    private javax.swing.JTextField jTextField_nombre;
    private javax.swing.JTextField jTextField_usuario;
    // End of variables declaration//GEN-END:variables
}

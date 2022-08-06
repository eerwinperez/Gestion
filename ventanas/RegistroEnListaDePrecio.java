/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import java.awt.HeadlessException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;

/**
 *
 * @author erwin
 */
public class RegistroEnListaDePrecio extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso, cliente;

    /**
     * Creates new form RegistroVentas
     */
    public RegistroEnListaDePrecio() {
        initComponents();
        llenarComboBoxes();
        ConfiguracionGralJFrame();

    }

    public RegistroEnListaDePrecio(String usuario, String permiso) {
        initComponents();
        this.usuario = usuario;
        this.permiso = permiso;
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

    }

    public void IniciarCaracteristicasGenerales() {
        llenarComboBoxes();
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Registrar en lista de precios *** " + "Usuario: " + usuario + " - " + permiso);
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

    public void LimpiarFormulario() {

        jTextField_descripcion.setText("");
        jTextField_cantidad.setText("");
        jComboBox_tipotrabajo.setSelectedItem(0);
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
        jTextField_precio.setText("");

    }

    public void Registrar(String descripcion, String cantidad, String tipo, String tamano, String color, 
            String acabado, String papelOriginal, String copia1, String copia2, String copia3,
            String observaciones, String precio, String fecha, String usuario) {

        String consulta = "insert into listaprecios (descripcion, cantidad, tipoTrabajo, tamano, color, acabado, "
                + "papelOriginal, copia1, copia2, copia3, observaciones, precio, fecha, registradoPor) values (?,?,?,?,?,"
                + "?,?,?,?,?,?,?,?,?)";

        Connection cn = Conexion.Conectar();
        try {
            
            PreparedStatement pst= cn.prepareStatement(consulta);
            pst.setString(1, descripcion);
            pst.setString(2, cantidad);
            pst.setString(3, tipo);
            pst.setString(4, tamano);
            pst.setString(5, color);
            pst.setString(6, acabado);
            pst.setString(7, papelOriginal);
            pst.setString(8, copia1);
            pst.setString(9, copia2);
            pst.setString(10, copia3);
            pst.setString(11, observaciones);
            pst.setString(12, precio);
            pst.setString(13, fecha);
            pst.setString(14, usuario);
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Trabajo registrado en la lista de precios");
            cn.close();
            
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en registrar el trabajo en la lista de precios");
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

        jButton_registrar = new javax.swing.JButton();
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
        jLabel1 = new javax.swing.JLabel();
        jTextField_precio = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton_registrar.setText("Registrar");
        jButton_registrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_registrarActionPerformed(evt);
            }
        });

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

        jLabel1.setText("Precio");

        jTextField_precio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_precioKeyTyped(evt);
            }
        });

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
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(27, 27, 27)
                                .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(22, 22, 22)
                                .addComponent(jTextField_colorTinta, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField_numeroInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jTextField_numerofinal, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13))
                            .addComponent(jTextField_precio))
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_acabado, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_precio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(424, 424, 424)
                        .addComponent(jButton_registrar)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_registrar)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox_tipotrabajoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_tipotrabajoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_tipotrabajoActionPerformed

    private void jButton_registrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_registrarActionPerformed
        //Capturamos los datos
        String descripcion = jTextField_descripcion.getText().trim();
        String cantidad = jTextField_cantidad.getText().trim();
        String tipo = jComboBox_tipotrabajo.getSelectedItem().toString().trim();
        String tamano = jTextField_tamaño.getText().trim();
        String color = jTextField_colorTinta.getText().trim();
        String acabado = jTextField_acabado.getText().trim();
        String papelOriginal = jComboBox_original.getSelectedItem().toString().trim();
        String copia1 = jComboBox_copia1.getSelectedItem().toString().trim();
        String copia2 = jComboBox_copia2.getSelectedItem().toString().trim();
        String copia3 = jComboBox_copia3.getSelectedItem().toString().trim();
        String observaciones = jTextField_observaciones.getText().trim();
        String precio = jTextField_precio.getText().trim();
        
        //Verificamos que se hayan completado todos los campos
        if (!descripcion.equals("") && !cantidad.equals("") && !tipo.equals("") && !tamano.equals("")
                && !color.equals("") && !acabado.equals("")
                && !papelOriginal.equals("") && !copia1.equals("") && !copia2.equals("") && !copia3.equals("")
                && !observaciones.equals("") && !precio.equals("")) {

            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Registrar(descripcion, cantidad, tipo, tamano, color, acabado, papelOriginal,
                    copia1, copia2, copia3, observaciones, precio, fecha, this.usuario);
            LimpiarFormulario();

        } else {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");

        }


    }//GEN-LAST:event_jButton_registrarActionPerformed


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

        if (jTextField_cantidad.getText().trim().length() == 10) {
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
        char c=evt.getKeyChar();
        if (jTextField_numeroInicial.getText().trim().length() == 50 || !Character.isDigit(c)) {
            evt.consume();
        }
        
    }//GEN-LAST:event_jTextField_numeroInicialKeyTyped

    private void jTextField_numerofinalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_numerofinalKeyTyped
        char c=evt.getKeyChar();
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
        if (!Character.isDigit(c) || (c=='0' && jTextField_precio.getText().length()==0)) {
            evt.consume();
        }
        if (jTextField_precio.getText().length()>=10) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_precioKeyTyped

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
            java.util.logging.Logger.getLogger(RegistroEnListaDePrecio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroEnListaDePrecio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroEnListaDePrecio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroEnListaDePrecio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistroEnListaDePrecio().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_registrar;
    private javax.swing.JComboBox<String> jComboBox_copia1;
    private javax.swing.JComboBox<String> jComboBox_copia2;
    private javax.swing.JComboBox<String> jComboBox_copia3;
    private javax.swing.JComboBox<String> jComboBox_original;
    private javax.swing.JComboBox<String> jComboBox_tipotrabajo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField jTextField_acabado;
    private javax.swing.JTextField jTextField_cantidad;
    private javax.swing.JTextField jTextField_colorTinta;
    private javax.swing.JTextField jTextField_descripcion;
    private javax.swing.JTextField jTextField_numeroInicial;
    private javax.swing.JTextField jTextField_numerofinal;
    private javax.swing.JTextField jTextField_observaciones;
    private javax.swing.JTextField jTextField_precio;
    private javax.swing.JTextField jTextField_tamaño;
    // End of variables declaration//GEN-END:variables
}

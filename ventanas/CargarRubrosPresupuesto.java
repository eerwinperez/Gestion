/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author erwin
 */
public class CargarRubrosPresupuesto extends javax.swing.JFrame {

    String usuario, permiso, idPresupuesto, descripcionPresupuesto;
    DefaultTableModel modelo;

    /**
     * Creates new form RubrosPresupuesto
     */
    public CargarRubrosPresupuesto() {
        initComponents();
        InhabilitarCamposGenerales();
        settearModelo();
        llenarTabla();
        llenarComboBoxTipoGastos();
        ConfiguracionGralJFrame();

    }

    public CargarRubrosPresupuesto(String usuario, String permiso, String idPresupuesto, String descripcionPresupuesto) {
        this.usuario = usuario;
        this.permiso = permiso;
        this.idPresupuesto = idPresupuesto;
        this.descripcionPresupuesto = descripcionPresupuesto;

        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

        if (permiso.equals("Asistente")) {
            jButton_cargar.setEnabled(false);
            jButton_eliminar.setEnabled(false);
        }

    }

    public void IniciarCaracteristicasGenerales() {
        InhabilitarCamposGenerales();
        llenarComboBoxTipoGastos();
        settearModelo();
        SetterDatos();
        llenarTabla();

    }

    public void InhabilitarCamposGenerales() {
        jTextField_concepto.setEnabled(false);
        jTextField_id.setEnabled(false);
        jLabel_fila.setVisible(false);
        jTextField_presupuesto.setEnabled(false);
        jTextField_suma.setEnabled(false);
        jTextField_descripcionPresupuesto.setEnabled(false);
    }

    public void SetterDatos() {
        jTextField_presupuesto.setText(idPresupuesto);
        jTextField_descripcionPresupuesto.setText(descripcionPresupuesto);
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Carga de rubros a presupuesto *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void llenarComboBoxTipoGastos() {

        try {

            String consultaTipoGasto = "select tipoGasto from tipogastos";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaTipoGasto);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String tipoTrabajo = rs.getString("tipoGasto");
                jComboBox_tipo.addItem(tipoTrabajo);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en leer el listado de tipo de gastos DatosGenerales llenarComboBoxTipoGastos()");
        }

    }

    public void settearModelo() {
        modelo = (DefaultTableModel) jTable_tablaRubros.getModel();
    }

    public void llenarTabla() {

        modelo = (DefaultTableModel) jTable_tablaRubros.getModel();

        String consulta = "select m.id, t.tipoGasto, m.descripcion, m.presupuestado from maestrogastos m "
                + "join tipogastos t on t.idGasto=m.idGasto";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                Object[] nuevo = new Object[4];
                nuevo[0] = rs.getString("m.id");
                nuevo[1] = rs.getString("t.tipoGasto");
                nuevo[2] = rs.getString("m.descripcion");
                nuevo[3] = rs.getString("m.presupuestado");

                modelo.addRow(nuevo);
            }

            jTable_tablaRubros.setModel(modelo);
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer los conceptos de la base de datos RubrosPresupuesto"
                    + " llenarTabla()");
        }
    }

    public void EliminarConcepto(JTable tabla, String fila) {

        modelo = (DefaultTableModel) jTable_tablaRubros.getModel();
        modelo.removeRow(Integer.parseInt(fila));
        jTable_tablaRubros.setModel(modelo);

    }

    public void limpiarFormulario() {
        jTextField_concepto.setText("");
        jTextField_id.setText("");
        jLabel_fila.setText("");

    }

    public void CargarItemsPresupuesto(JTable tabla, String numeroPresupuesto) {
        String consulta = "insert into itemspresupuesto (idPresupuesto, idGasto, valorPresupuestado) values (?, ?, ?)";
        try {

            for (int i = 0; i < tabla.getRowCount(); i++) {
                Connection cn = Conexion.Conectar();
                PreparedStatement pst = cn.prepareStatement(consulta);

                pst.setString(1, numeroPresupuesto);
                pst.setString(2, tabla.getValueAt(i, 0).toString());
                pst.setString(3, tabla.getValueAt(i, 3).toString());

                pst.executeUpdate();
                cn.close();
            }

            JOptionPane.showMessageDialog(this, "Conceptos cargados al presupuesto");

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "MySQLException\nError en cargar los items de presupuesto RubrosPresupuesto "
                    + "CargarItemsPresupuesto()");
            e.printStackTrace();
        }

    }

    public boolean ComprobacionDatos(JTable tabla) {
        boolean comprobacion = true;

        for (int i = 0; i < tabla.getRowCount(); i++) {
            try {
                Double.parseDouble(tabla.getValueAt(i, 3).toString());
            } catch (NumberFormatException e) {
                comprobacion = false;
                JOptionPane.showMessageDialog(this, "Error en los datos ingresados", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            }
        }

        return comprobacion;
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
        jTable_tablaRubros = new javax.swing.JTable();
        jButton_cargar = new javax.swing.JButton();
        jLabel_fila = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_presupuesto = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_descripcionPresupuesto = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jComboBox_tipo = new javax.swing.JComboBox<>();
        jButton_calcular = new javax.swing.JButton();
        jTextField_suma = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jTextField_id = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField_concepto = new javax.swing.JTextField();
        jButton_eliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_tablaRubros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Tipo", "Concepto", "Valor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_tablaRubros.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_tablaRubros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_tablaRubrosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_tablaRubros);
        if (jTable_tablaRubros.getColumnModel().getColumnCount() > 0) {
            jTable_tablaRubros.getColumnModel().getColumn(0).setPreferredWidth(70);
            jTable_tablaRubros.getColumnModel().getColumn(1).setPreferredWidth(160);
            jTable_tablaRubros.getColumnModel().getColumn(2).setPreferredWidth(270);
            jTable_tablaRubros.getColumnModel().getColumn(3).setPreferredWidth(100);
        }

        jButton_cargar.setText("Cargar");
        jButton_cargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cargarActionPerformed(evt);
            }
        });

        jLabel_fila.setText("jLabel1");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informacion presupuesto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Presupuesto");

        jLabel3.setText("Descripcion");

        jLabel2.setText("Totalizar tipo");

        jComboBox_tipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos" }));

        jButton_calcular.setText("Calcular");
        jButton_calcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_calcularActionPerformed(evt);
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
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_presupuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_descripcionPresupuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox_tipo, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_calcular, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_suma, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_presupuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_descripcionPresupuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox_tipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_calcular)
                    .addComponent(jTextField_suma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Item a eliminar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel6.setText("Id");

        jLabel7.setText("Concepto");

        jButton_eliminar.setText("Eliminar");
        jButton_eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_eliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(33, 33, 33)
                .addComponent(jTextField_concepto, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jButton_eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField_concepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_eliminar))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel_fila)
                    .addComponent(jButton_cargar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane1)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_fila)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton_cargar)
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_tablaRubrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_tablaRubrosMouseClicked
        int fila = jTable_tablaRubros.getSelectedRow();
        if (fila != -1) {
            jTextField_id.setText(jTable_tablaRubros.getValueAt(fila, 0).toString());
            jTextField_concepto.setText(jTable_tablaRubros.getValueAt(fila, 2).toString());
            jLabel_fila.setText(String.valueOf(fila));
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un rubro");
        }
    }//GEN-LAST:event_jTable_tablaRubrosMouseClicked

    private void jButton_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_eliminarActionPerformed

        //Verificamos inicialmente si la tabla tiene informacion cargada
        int filas = jTable_tablaRubros.getRowCount();

        if (filas > 0) {

            //Capturamos los datos
            String id = jTextField_id.getText().trim();

            //Verificamos que se haya seleccionado un elemento de la tabla
            if (!id.equals("")) {

                String fila = jLabel_fila.getText().trim();
                //Eliminamos de la tabla el elemento seleccionado
                EliminarConcepto(jTable_tablaRubros, fila);
                limpiarFormulario();

            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un concepto para eliminar");
            }

        } else {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla");
        }


    }//GEN-LAST:event_jButton_eliminarActionPerformed

    private void jButton_cargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cargarActionPerformed
        //Verificamos que haya datos en la tabla para cargar
        int numeroFilas = jTable_tablaRubros.getRowCount();

        if (numeroFilas > 0) {
            //Capturamos el el numero de presupuesto
            String numeroPresupuesto = jTextField_presupuesto.getText().trim();
            //Comprobamos que los valores ingresados esten correctos
            boolean comprobacion = ComprobacionDatos(jTable_tablaRubros);

            if (comprobacion == true) {
                CargarItemsPresupuesto(jTable_tablaRubros, numeroPresupuesto);
                dispose();
                new ListadoPresupuestos(usuario, permiso).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Verifique los datos ingresados. Los valores deben ser positivos"
                        + ", no deben incluir espacios ni puntos ni comas", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "No hay datos para cargar al presupuesto", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton_cargarActionPerformed

    private void jButton_calcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_calcularActionPerformed

        String tipo = jComboBox_tipo.getSelectedItem().toString();
        //Verificamos que haya datos en la tabla
        int numeroFilas = jTable_tablaRubros.getRowCount();
        if (numeroFilas > 0) {
            //Verificamos que se hayan ingresado los numeros correctamente
            int valor = 0;
            try {
                if (tipo.equals("Todos")) {

                    for (int i = 0; i < jTable_tablaRubros.getRowCount(); i++) {
                        valor += Integer.parseInt(jTable_tablaRubros.getValueAt(i, 3).toString());
                    }

                } else {
                    for (int i = 0; i < jTable_tablaRubros.getRowCount(); i++) {
                        if (jTable_tablaRubros.getValueAt(i, 1).toString().equals(tipo)) {
                            valor += Integer.parseInt(jTable_tablaRubros.getValueAt(i, 3).toString());
                        }
                    }
                }

                jTextField_suma.setText(MetodosGenerales.ConvertirIntAMoneda(valor));

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Asegurese de haber ingresado correctamente los valores.\n"
                        + "Los valores deben ser numericos, sin comas, puntos ni espacios");
            }

        } else {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla");
        }

    }//GEN-LAST:event_jButton_calcularActionPerformed

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
            java.util.logging.Logger.getLogger(CargarRubrosPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CargarRubrosPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CargarRubrosPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CargarRubrosPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CargarRubrosPresupuesto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_calcular;
    private javax.swing.JButton jButton_cargar;
    private javax.swing.JButton jButton_eliminar;
    private javax.swing.JComboBox<String> jComboBox_tipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel_fila;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_tablaRubros;
    private javax.swing.JTextField jTextField_concepto;
    private javax.swing.JTextField jTextField_descripcionPresupuesto;
    private javax.swing.JTextField jTextField_id;
    private javax.swing.JTextField jTextField_presupuesto;
    private javax.swing.JTextField jTextField_suma;
    // End of variables declaration//GEN-END:variables
}

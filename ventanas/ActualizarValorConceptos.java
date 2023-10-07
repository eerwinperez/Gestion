/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import static ventanas.RegistrarGastosPresupuesto.ConvertirIntAMoneda;

/**
 *
 * @author Erwin P
 */
public class ActualizarValorConceptos extends javax.swing.JFrame {

    String usuario, permiso;
    DefaultTableModel modelo;

    /**
     * Creates new form ActualizarValorConceptos
     */
    public ActualizarValorConceptos() {
        initComponents();
        IniciarCaracteristicasGenerales();
        
    }

    public ActualizarValorConceptos(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
        
        if (!permiso.equalsIgnoreCase("Gerente")) {
            jButton1.setEnabled(false);
        }
        
    }

    public void IniciarCaracteristicasGenerales() {
        llenarTabla();
    }

    public void llenarTabla() {

        modelo = (DefaultTableModel) jTable1.getModel();

        String consulta = "select id, descripcion, presupuestado from maestrogastos";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[3];
                nuevo[0] = rs.getString("id");
                nuevo[1] = rs.getString("descripcion");
                nuevo[2] = rs.getInt("presupuestado");

                modelo.addRow(nuevo);
            }

            jTable1.setModel(modelo);

            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable1.setRowSorter(ordenador);

            cn.close();

        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el maestro de gastos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public boolean VerificacionNumerica(JTable jTable1) {

        int filas = jTable1.getRowCount();

        for (int i = 0; i < filas; i++) {
            try {
                Integer numero = Integer.parseInt(jTable1.getValueAt(i, 2).toString().trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }
    
    public void LimpiarTabla(DefaultTableModel modelo1) {
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            modelo1.removeRow(i);
            i = i - 1;
        }
    }
    
    public void ActualizarValores(JTable jTable1){
        
        //Capturamos los valores numericos
        int filas = jTable1.getRowCount();
        
        ArrayList<Integer []> valores = new ArrayList<>();
        
        for (int i = 0; i < filas; i++) {
            Integer [] par = new Integer [2];
            par[0] = Integer.parseInt(jTable1.getValueAt(i, 0).toString().trim());
            par[1] = Integer.parseInt(jTable1.getValueAt(i, 2).toString().trim());
            
            valores.add(par);
        }
        
        Connection cn = Conexion.Conectar();
        String consulta = "update maestrogastos set presupuestado=? where id=?";
        
        try {
            cn.setAutoCommit(false);
            
            
            for (Integer [] valor : valores) {
                PreparedStatement pst = cn.prepareStatement(consulta);
                pst.setInt(1, valor[1]);
                pst.setInt(2, valor[0]);
                pst.executeUpdate();
            }
            
            cn.commit();
            JOptionPane.showMessageDialog(this, "Valores actualizados", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar los valores de los conceptos ActualizarValores()", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        
    }
    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Valores maestros presupuestados *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

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
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Concepto", "Valor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(500);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
        }

        jButton1.setText("Actualizar");
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
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 449, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        //Verificamos que todos los valores esten correctamente ingresasos (numeros sin punto, ni coma ni espacios)
        if (VerificacionNumerica(jTable1)) {
            
            ActualizarValores(jTable1);
            LimpiarTabla(modelo);
            llenarTabla();
            
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar los valores, debe ingresar solo numero sin espacios, puntos ni comas", "Error", JOptionPane.ERROR_MESSAGE);

        }

    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(ActualizarValorConceptos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ActualizarValorConceptos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ActualizarValorConceptos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ActualizarValorConceptos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ActualizarValorConceptos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}

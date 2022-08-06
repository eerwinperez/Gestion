/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.mail.MessagingException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author erwin
 */
public class ListadoDeAbonosFacturas extends javax.swing.JFrame {

    String usuario, permiso;
    DefaultTableModel modelo;
    TableRowSorter trs, trs1;

    /**
     * Creates new form ListadoDeAbonosFacturas
     */
    public ListadoDeAbonosFacturas() {
        initComponents();
        IniciarCaracteristicasGenerales();

        ConfiguracionGralJFrame();
    }

    public ListadoDeAbonosFacturas(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

        if (!permiso.equals("Gerente")) {
            jButton_editar.setEnabled(false);
            jButton_eliminar.setEnabled(false);
        }
    }

    public void IniciarCaracteristicasGenerales() {
        settearModelo();
        LlenarTabla();
        ocultarLabels();
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado abonos de facturas *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void settearModelo() {
        modelo = (DefaultTableModel) jTable_listadoAbonos.getModel();

    }

    public void LlenarTabla() {

        modelo = (DefaultTableModel) jTable_listadoAbonos.getModel();

        String consulta = "select af.idAbono, af.fecha, af.factura, af.abono, c.nombreCliente, af.observaciones, af.registradoPor\n"
                + "from abonosfacturas af join facturas f on af.factura=f.idFactura\n"
                + "join clientes c on f.idCliente=c.idCliente\n"
                + "where af.estado='Activo' order by af.idAbono desc";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] listado = new Object[7];
                listado[0] = rs.getString("af.idAbono");
                listado[1] = rs.getString("af.fecha");
                listado[2] = rs.getString("af.factura");
                listado[3] = MetodosGenerales.ConvertirIntAMoneda(rs.getInt("af.abono"));
//                listado[3] = rs.getString("abonosfacturas.valorAbono");
                listado[4] = rs.getString("c.nombreCliente");
                listado[5] = rs.getString("af.observaciones");
                listado[6] = rs.getString("af.registradoPor");

                modelo.addRow(listado);
            }

            jTable_listadoAbonos.setModel(modelo);
            
            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable_listadoAbonos.setRowSorter(ordenador);
            
            cn.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error en leer el listado de abonos de facturas. ListadoDeAbonosFacturas llenarTabla()");
        }

    }

    public void ocultarLabels() {
        jLabel_factura.setVisible(false);
        jLabel_fecha.setVisible(false);
        jLabel_monto.setVisible(false);
        jLabel_observaciones.setVisible(false);
    }

    public void limpiarCampos() {
        jTextField_cliente.setText("");
        jTextField_idAbono.setText("");
        jLabel_factura.setText("");
        jLabel_fecha.setText("");
        jLabel_monto.setText("");
        jLabel_observaciones.setText("");
    }

    public void EliminarAbonoFactura(String idAbono, String observaciones, String cliente) {

        String consulta = "delete from abonosfacturas where idAbono=?";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idAbono);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Abono eliminado");
            cn.close();
            
            String asunto = "Abono " + idAbono + " eliminado - "+cliente;
            String mensaje = "El abono No. " + idAbono + " ha sido eliminado"
                    + "\nValor del abono = " + jLabel_monto.getText().trim()+
                    "\nUsuario responsable: "+this.usuario+
                    "\nObservaciones: "+observaciones;
            
            MetodosGenerales.enviarEmail(asunto, mensaje);
            MetodosGenerales.registrarHistorial(usuario, mensaje);
            
        }catch(MessagingException ex){
            ex.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar el abono de la factura","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_listadoAbonos.getRowCount(); i++) {
            this.modelo.removeRow(i);
            i = i - 1;
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
        jTable_listadoAbonos = new javax.swing.JTable();
        jTextField_clienteFiltrar = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField_idAbono = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jButton_editar = new javax.swing.JButton();
        jButton_eliminar = new javax.swing.JButton();
        jLabel_monto = new javax.swing.JLabel();
        jLabel_observaciones = new javax.swing.JLabel();
        jLabel_fecha = new javax.swing.JLabel();
        jLabel_factura = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_listadoAbonos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Abono", "Fecha", "No. Factura", "Valor abono", "Cliente", "Observaciones", "Registrado Por"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_listadoAbonos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_listadoAbonos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_listadoAbonosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_listadoAbonos);
        if (jTable_listadoAbonos.getColumnModel().getColumnCount() > 0) {
            jTable_listadoAbonos.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable_listadoAbonos.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTable_listadoAbonos.getColumnModel().getColumn(2).setPreferredWidth(60);
            jTable_listadoAbonos.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTable_listadoAbonos.getColumnModel().getColumn(4).setPreferredWidth(200);
            jTable_listadoAbonos.getColumnModel().getColumn(5).setPreferredWidth(200);
            jTable_listadoAbonos.getColumnModel().getColumn(6).setPreferredWidth(100);
        }

        jTextField_clienteFiltrar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_clienteFiltrarKeyTyped(evt);
            }
        });

        jLabel1.setText("Filtrar cliente:");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informacion abono", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel3.setText("Abono");

        jTextField_idAbono.setEnabled(false);

        jLabel4.setText("Cliente");

        jTextField_cliente.setEnabled(false);

        jButton_editar.setText("Editar");
        jButton_editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_editarActionPerformed(evt);
            }
        });

        jButton_eliminar.setText("Eliminar");
        jButton_eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_eliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_idAbono, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addGap(12, 12, 12)
                .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_idAbono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_editar)
                    .addComponent(jButton_eliminar))
                .addGap(13, 13, 13))
        );

        jLabel_monto.setText("jLabel2");

        jLabel_observaciones.setText("jLabel2");

        jLabel_fecha.setText("jLabel2");

        jLabel_factura.setText("jLabel2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel_monto)
                            .addComponent(jLabel_observaciones)
                            .addComponent(jLabel_fecha)
                            .addComponent(jLabel_factura))
                        .addGap(149, 149, 149))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_clienteFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 797, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(21, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel_monto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel_observaciones)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel_fecha)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel_factura)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_clienteFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField_clienteFiltrarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_clienteFiltrarKeyTyped
        jTextField_clienteFiltrar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                trs.setRowFilter(RowFilter.regexFilter("(?i)" + jTextField_clienteFiltrar.getText(), 4));
            }

        });

        trs = new TableRowSorter(jTable_listadoAbonos.getModel());
        jTable_listadoAbonos.setRowSorter(trs);
    }//GEN-LAST:event_jTextField_clienteFiltrarKeyTyped

    private void jTable_listadoAbonosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_listadoAbonosMouseClicked
        int fila = jTable_listadoAbonos.getSelectedRow();

        if (fila != -1) {
            jTextField_cliente.setText(jTable_listadoAbonos.getValueAt(fila, 4).toString());
            jTextField_idAbono.setText(jTable_listadoAbonos.getValueAt(fila, 0).toString());
            jLabel_monto.setText(jTable_listadoAbonos.getValueAt(fila, 3).toString());
            jLabel_observaciones.setText(jTable_listadoAbonos.getValueAt(fila, 5).toString());
            jLabel_fecha.setText(jTable_listadoAbonos.getValueAt(fila, 1).toString());
            jLabel_factura.setText(jTable_listadoAbonos.getValueAt(fila, 2).toString());

        } else {
            JOptionPane.showMessageDialog(null, "Selecciona una fila");
        }
    }//GEN-LAST:event_jTable_listadoAbonosMouseClicked

    private void jButton_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_eliminarActionPerformed
        String idAbono = jTextField_idAbono.getText().trim();
        String cliente = jTextField_cliente.getText().trim();
        if (!idAbono.equals("")) {
            int opcion = JOptionPane.showConfirmDialog(this, "¿Desea eliminar el abono seleccionado?","Confirmacion",JOptionPane.INFORMATION_MESSAGE);
            if (opcion == 0) {

                String observaciones = JOptionPane.showInputDialog(this, "Ingrese la razon por el cual se elimina el abono de la factura","Informacion",JOptionPane.INFORMATION_MESSAGE);

                if (!observaciones.equals("")) {
                    EliminarAbonoFactura(idAbono, observaciones, cliente);
                    limpiarTabla(modelo);
                    limpiarCampos();
                    LlenarTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "Es necesario indicar la razón del por que se elimina el abono de la factura","Informacion",JOptionPane.INFORMATION_MESSAGE);
                }

            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione el abono a eliminar","Informacion",JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton_eliminarActionPerformed

    private void jButton_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_editarActionPerformed
        String idAbono = jTextField_idAbono.getText().trim();
        if (!idAbono.equals("")) {

            try {
                String cliente = jTextField_cliente.getText().trim();
                Object monto = Math.abs(Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jLabel_monto.getText().trim())));
                String observaciones = jLabel_observaciones.getText().trim();
                Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse(jLabel_fecha.getText().trim());
                String factura = jLabel_factura.getText().trim();
                new EditarAbonoFacturas(this.usuario, this.permiso, cliente, idAbono, monto, observaciones, fecha, factura).setVisible(true);
                dispose();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error en la fecha ingresada","Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione el abono a eliminar","Informacion",JOptionPane.INFORMATION_MESSAGE);
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
            java.util.logging.Logger.getLogger(ListadoDeAbonosFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoDeAbonosFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoDeAbonosFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoDeAbonosFacturas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoDeAbonosFacturas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_editar;
    private javax.swing.JButton jButton_eliminar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel_factura;
    private javax.swing.JLabel jLabel_fecha;
    private javax.swing.JLabel jLabel_monto;
    private javax.swing.JLabel jLabel_observaciones;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_listadoAbonos;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_clienteFiltrar;
    private javax.swing.JTextField jTextField_idAbono;
    // End of variables declaration//GEN-END:variables
}

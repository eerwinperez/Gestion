/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static ventanas.ListadoPresupuestos.ConvertirIntAMoneda;

/**
 *
 * @author Erwin P
 */
public class ListadoCotizaciones extends javax.swing.JFrame {

    static String usuario, permiso;
    DefaultTableModel modelo;

    /**
     * Creates new form ListadoCotizaciones
     */
    public ListadoCotizaciones(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();
    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_coti.getRowCount(); i++) {
            this.modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void limpiarCampos() {
        jTextField_descripcion.setText("");
        jTextField_idcotizacion.setText("");
    }

    public void IniciarCaracteristicasGenerales() {
        SettearTabla();
        llenarTabla();
    }

    public void SettearTabla() {
        modelo = (DefaultTableModel) jTable_coti.getModel();
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Cotizaciones *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void llenarTabla() {
        
        modelo = (DefaultTableModel) jTable_coti.getModel();

        String consulta = "select c.IdCoti, c.FechaventaSistema, cl.nombreCliente, c.tipoVenta, c.descripcionTrabajo, c.tamaño, \n"
                + "c.colorTinta, c.acabado, c.papelOriginal, c.copia1, c.copia2, c.copia3, c.cantidad, \n"
                + "c.unitario, c.precio, c.registradoPor\n"
                + "from cotizaciones c left join clientes cl on c.Idcliente=cl.idCliente order by c.IdCoti desc";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[10];
                nuevo[0] = Boolean.FALSE;
                nuevo[1] = rs.getString("c.IdCoti");
                nuevo[2] = rs.getString("c.FechaventaSistema");
                nuevo[3] = rs.getString("cl.nombreCliente");
                nuevo[4] = rs.getString("c.tipoVenta");

                String tamaño = (rs.getString("c.tamaño") != null) ? " TAMAÑO: " + rs.getString("c.tamaño") + " " : "";
                String colorTinta = (rs.getString("c.colorTinta") != null) ? "COLOR: " + rs.getString("c.colorTinta") + " " : "";
                String acabado = (rs.getString("c.acabado") != null) ? "ACABADO: " + rs.getString("c.acabado") + " " : "";
                String original = (!rs.getString("c.papelOriginal").equals("NO APLICA")) ? " P. ORIG: " + rs.getString("c.papelOriginal") + " " : "";
                String copia1 = (!rs.getString("c.copia1").equals("NO APLICA")) ? " COPIA 1: " + rs.getString("c.copia1") + " " : "";
                String copia2 = (!rs.getString("c.copia2").equals("NO APLICA")) ? " COPIA 2: " + rs.getString("c.copia2") + " " : "";
                String copia3 = (!rs.getString("c.copia3").equals("NO APLICA")) ? " COPIA 3: " + rs.getString("c.copia3") + " " : "";
                
                nuevo[5] = rs.getString("c.descripcionTrabajo") + " " + tamaño + colorTinta + acabado+original+copia1+copia2+copia3;
                nuevo[6] = rs.getString("c.cantidad");
                nuevo[7] = ConvertirIntAMoneda(rs.getDouble("c.unitario"));
                nuevo[8] = ConvertirIntAMoneda(rs.getDouble("c.precio"));
                nuevo[9] = rs.getString("c.registradoPor");
                modelo.addRow(nuevo);
            }

            jTable_coti.setModel(modelo);
            
            TableRowSorter<TableModel> ordenador = new TableRowSorter<>(modelo);
            jTable_coti.setRowSorter(ordenador);
            
            cn.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer la tabla de cotizaciones llenarTabla()","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }
    
    public void imprimirRemito(Object[] datosCabecera, ArrayList<Object[]> elementos) {

        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Cotizacion.xlsx";

        try {
            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);

            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();
            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            //Definimos la ruta donde se guardara la factura            
            String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Cotizacion.xlsx";

            //Datos de la cuarta fila
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda42 = fila4.getCell(3);
            celda42.setCellValue((String) datosCabecera[0]);
            XSSFCell celda44 = fila4.getCell(5);
            celda44.setCellValue(new Date());

            //Datos de la quinta fila
            XSSFRow fila5 = hoja.getRow(5);
            XSSFCell celda52 = fila5.getCell(3);
            celda52.setCellValue((String) datosCabecera[1]);
//            XSSFCell celda54 = fila5.getCell(5);
//            celda54.setCellValue();

            //Datos de la sexta fila
            XSSFRow fila6 = hoja.getRow(6);
            XSSFCell celda62 = fila6.getCell(3);
            celda62.setCellValue((String) datosCabecera[2]);
//            XSSFCell celda64 = fila6.getCell(5);
//            celda64.setCellValue((String) datosCabecera[7]);

            //Datos de la septima fila
            XSSFRow fila7 = hoja.getRow(7);
            XSSFCell celda72 = fila7.getCell(3);
            celda72.setCellValue((String) datosCabecera[3]);

            //Datos de la octava fila
            XSSFRow fila8 = hoja.getRow(8);
            XSSFCell celda82 = fila8.getCell(3);
            celda82.setCellValue((String) datosCabecera[4]);

            //Traemos el detalle de los items facturados
//            ArrayList<String[]> listado = ConsultarElementosFactura(numeroFactura);
            //Declaramos una variable para capturar el total de la factura
            double total = 0;
            //Declaramos la variable con la fila donde se inicia a agregar la informacion
            int filaInicio = 11;

            //Recorremos la informacion de cada item y lo agregamos a las celdas correspondientes
            for (Object[] elemento : elementos) {
                XSSFRow fila = hoja.getRow(filaInicio);
                for (int i = 0; i < 6; i++) {

                    XSSFCell celda = fila.getCell(i);
                    switch (i) {
                        case 0:
                            celda.setCellValue((Double) elemento[0]);
                            break;
                        case 3:
                            celda.setCellValue((String) elemento[1]);
                            break;
                        case 4:
                            celda.setCellValue((Double) elemento[2]);
                            break;
                        case 5:
                            celda.setCellValue((Double) elemento[3]);
                            total += (Double) elemento[3];
                            break;

                    }

                }
                filaInicio++;
            }

            XSSFRow fila40 = hoja.getRow(38);
            XSSFCell celda404 = fila40.getCell(5);
            celda404.setCellValue(total);

            FileOutputStream ultimo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(ultimo);
            ultimo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error al generar la factura. Es posible que tenga una factura con el mismo nombre abierto", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error en generar la factura. Asegurse se no tener una factura abierta y vuelta a intentarlo. ImprimirRecibo GenerarRecibo()", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }
    
    public Object[] consultarDatosCabecera(String cliente) {

        String consulta = "select nombreCliente, identificacion, direccion, telefono, email from clientes "
                + "where nombreCliente=?";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, cliente);

            ResultSet rs = pst.executeQuery();
            Object[] datosCabecera = new Object[5];

            if (rs.next()) {
                
                datosCabecera[0] = rs.getString("nombreCliente");
                datosCabecera[1] = rs.getString("identificacion");
                datosCabecera[2] = rs.getString("direccion");
                datosCabecera[3] = rs.getString("telefono");
                datosCabecera[4] = rs.getString("email");

            }
            cn.close();
            return datosCabecera;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al leer los datos de cabecera", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

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
        jTextField_idcotizacion = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_descripcion = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_coti = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cotizacion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Cotizacion");

        jTextField_idcotizacion.setEnabled(false);

        jLabel2.setText("Descripcion");

        jTextField_descripcion.setEnabled(false);

        jButton1.setText("Cargar venta");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Cliente");

        jTextField_cliente.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_idcotizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 938, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_idcotizacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jTable_coti.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Seleccion", "Id Cot", "Fecha", "Cliente", "Tipo", "Descripcion", "Cantidad", "Unitario", "Precio", "Registrado Por"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_coti.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_cotiMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_coti);
        if (jTable_coti.getColumnModel().getColumnCount() > 0) {
            jTable_coti.getColumnModel().getColumn(0).setPreferredWidth(40);
            jTable_coti.getColumnModel().getColumn(1).setPreferredWidth(30);
            jTable_coti.getColumnModel().getColumn(2).setPreferredWidth(70);
            jTable_coti.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable_coti.getColumnModel().getColumn(4).setPreferredWidth(80);
            jTable_coti.getColumnModel().getColumn(5).setPreferredWidth(270);
            jTable_coti.getColumnModel().getColumn(6).setPreferredWidth(50);
            jTable_coti.getColumnModel().getColumn(7).setPreferredWidth(80);
            jTable_coti.getColumnModel().getColumn(8).setPreferredWidth(80);
            jTable_coti.getColumnModel().getColumn(9).setPreferredWidth(80);
        }

        jButton2.setText("Imprimir cotizacion");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1054, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_cotiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_cotiMouseClicked

        int fila = jTable_coti.getSelectedRow();

        if (fila != -1) {
            jTextField_idcotizacion.setText(jTable_coti.getValueAt(fila, 1).toString());
            jTextField_cliente.setText(jTable_coti.getValueAt(fila, 3).toString());
            jTextField_descripcion.setText(jTable_coti.getValueAt(fila, 5).toString());
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jTable_cotiMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        int cotizacion = Integer.parseInt(jTextField_idcotizacion.getText().trim());
        String cliente = jTextField_cliente.getText().trim();
        
        if (!cliente.equals("")) {
            new RegistroVentas(usuario, permiso, cliente, cotizacion).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Selecciones una cotizacion para cargar la venta","Informacion",JOptionPane.INFORMATION_MESSAGE);
        }
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
        //Corroboramos que haya filas seleccionadas
        
        int numeroFilas = jTable_coti.getRowCount();
        int contador = 0;
        int contadorCliente = 0;
        ArrayList<String> lista = new ArrayList<>();
        for (int i = 0; i < numeroFilas; i++) {
            if(jTable_coti.getValueAt(i, 0).equals(Boolean.TRUE)){
                contador++;
                lista.add(jTable_coti.getValueAt(i, 3).toString());
                
            }              
            
        }
        
        String cliente = lista.get(0);
                
        for (String list : lista) {
            if (!list.equals(cliente)) {
                contadorCliente++;
                break;
            }
        }
        
        //Corroboramos que se haya seleccionado el mismo cliente
        
        ArrayList<Object []> elementos = new ArrayList<>();
        
        if (contador>0 && contadorCliente==0) {
            
            for (int i = 0; i < numeroFilas; i++) {
                if (jTable_coti.getValueAt(i, 0).equals(Boolean.TRUE) ) {
                    Object [] nuevo = new Object[4];
                    nuevo[0] = Double.parseDouble(jTable_coti.getValueAt(i, 6).toString());
                    nuevo[1] = jTable_coti.getValueAt(i, 5).toString();
                    nuevo[2] = Math.abs(Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jTable_coti.getValueAt(i, 7).toString())));
                    nuevo[3] = Math.abs(Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jTable_coti.getValueAt(i, 8).toString())));        
                    elementos.add(nuevo);
                }
            }
            
            Object[] datosCabecera = consultarDatosCabecera(cliente);
            imprimirRemito(datosCabecera, elementos);
            
            
            
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione al menos una cotizacion para imprimir y asegurese de que el cliente sea el mismo");
        }
        
        
    }//GEN-LAST:event_jButton2ActionPerformed

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
            java.util.logging.Logger.getLogger(ListadoCotizaciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoCotizaciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoCotizaciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoCotizaciones.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoCotizaciones(usuario, permiso).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_coti;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_descripcion;
    private javax.swing.JTextField jTextField_idcotizacion;
    // End of variables declaration//GEN-END:variables

}

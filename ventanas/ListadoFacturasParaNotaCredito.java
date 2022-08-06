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
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author erwin
 */
public class ListadoFacturasParaNotaCredito extends javax.swing.JFrame {

    String usuario, permiso;
    DefaultTableModel modelo;

    /**
     * Creates new form ListadoTotalFacturas
     */
    public ListadoFacturasParaNotaCredito() {
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
        
    }

    public ListadoFacturasParaNotaCredito(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
    }
    
    public void IniciarCaracteristicasGenerales(){
        SettearModelo();
        inhabilitarcampos();
        llenarTabla();     
    }
    
    public void ConfiguracionGralJFrame(){
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado de facturas *** "+"Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        
    }

    public void SettearModelo() {
        modelo = (DefaultTableModel) jTable_facturas.getModel();
    }

    public void inhabilitarcampos() {
        jTextField_cliente.setEnabled(false);
        jTextField_factura.setEnabled(false);
        jTextField_idcliente.setEnabled(false);
        jLabel_fila.setVisible(false);
    }

    public void llenarTabla() {

        modelo = (DefaultTableModel) jTable_facturas.getModel();

        String consulta = "select facturas.fechaFactura, facturas.idFactura, facturas.idCliente, clientes.nombreCliente, "
                + "facturas.valorFacturado, facturas.estadoPago, facturas.condiciondePago from facturas left join "
                + "clientes on facturas.idCliente=clientes.idCliente order by facturas.idFactura desc";

        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] listado = new Object[7];

                listado[0] = rs.getString("facturas.fechaFactura");
                listado[1] = rs.getString("facturas.idFactura");
                listado[2] = rs.getString("facturas.idCliente");
                listado[3] = rs.getString("clientes.nombreCliente");
                //listado[4] = rs.getString("facturas.valorFacturado");
                listado[4] = MetodosGenerales.ConvertirIntAMoneda(rs.getInt("facturas.valorFacturado"));
                listado[5] = rs.getString("facturas.estadoPago");
                listado[6] = rs.getString("facturas.condiciondePago");

                modelo.addRow(listado);
            }

            jTable_facturas.setModel(modelo);
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el listado de facturas. ListadoTotalFacturas llenarTabla()");
            e.printStackTrace();
        }

    }

    public String[] consultarDatosCliente(String idCliente) {

        String[] datosCliente = new String[5];

        String consulta = "select nombreCliente, identificacion, direccion, municipio, telefono, email from "
                + "clientes where idCliente=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idCliente);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                datosCliente[0] = rs.getString("nombreCliente");
                datosCliente[1] = rs.getString("identificacion");
                datosCliente[2] = rs.getString("direccion") + " - " + rs.getString("municipio");
                datosCliente[3] = rs.getString("telefono");
                datosCliente[4] = rs.getString("email");

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en leer los datos del cliente ListadoFacturas ConsultarDatosCliente()");
        }

        return datosCliente;
    }

    public ArrayList<String[]> ConsultarElementosFactura(String factura) {
        //Declaramos un ArraList para agregar las cadenas de String con la informacion de los elementos de la factura
        ArrayList<String[]> informacion = new ArrayList<>();

        String consulta = "select elementosfactura.idVenta, elementosfactura.cantidadFacturada, ventas.descripcionTrabajo, "
                + "elementosfactura.precioUnitario, elementosfactura.subtotal, elementosfactura.factura from "
                + "elementosfactura inner join ventas on elementosfactura.idVenta=ventas.Idventa inner join facturas "
                + "on elementosfactura.factura=facturas.idFactura where facturas.idFactura=?";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, factura);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                String[] detalle = new String[6];
                detalle[0] = rs.getString("elementosfactura.idVenta");
                detalle[1] = rs.getString("elementosfactura.cantidadFacturada");
                detalle[2] = rs.getString("ventas.descripcionTrabajo");
                detalle[3] = rs.getString("elementosfactura.precioUnitario");
                detalle[4] = rs.getString("elementosfactura.subtotal");

                informacion.add(detalle);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en leer los elementos de la factura. Contacte al administrador "
                    + "ListadoFacturas ConsultarElementosFactura()");
            e.printStackTrace();

        }

        return informacion;
    }

    public void ReimprimirFactura(String numeroFactura, String fecha, String idCliente) {
        //Definimos las ruta de donde se tomara la plantilla de la factra
        //String rutaArchivoACopiar = "D:" + File.separator + "Erwin" + File.separator + "ApacheNetbeans" + File.separator + "GraficasJireh_1" + File.separator + "Factura.xlsx";
        
        String rutaArchivoACopiar = "C:"+File.separator+"Users"+File.separator+"Erwin P"+File.separator+"Documents"+
                File.separator+"NetBeansProjects"+File.separator+"Gestion"+File.separator+"src"+File.separator+"Docs"
                +File.separator+"Factura.xlsx";
        
        try {
            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);

            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();
            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            String[] datos = consultarDatosCliente(idCliente);
            //Definimos la ruta donde se guardara la factura            
            String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Factura "
                    + "" + numeroFactura + " - " + datos[0] + ".xlsx";
            
            //Datos de la cuarta fila
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda42 = fila4.getCell(2);
            celda42.setCellValue(datos[0]);
            XSSFCell celda44 = fila4.getCell(4);
            celda44.setCellValue(Integer.parseInt(numeroFactura));

            //Datos de la quinta fila
            XSSFRow fila5 = hoja.getRow(5);
            XSSFCell celda52 = fila5.getCell(2);
            celda52.setCellValue(datos[1]);
            XSSFCell celda54 = fila5.getCell(4);
            try {
                celda54.setCellValue(new SimpleDateFormat("yyyy-MM-dd").parse(fecha));
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(null, "Error en la lectura de la fecha ListadoFacturas imprimirFactura()");
            }

            //Datos de la sexta fila
            XSSFRow fila6 = hoja.getRow(6);
            XSSFCell celda62 = fila6.getCell(2);
            celda62.setCellValue(datos[2]);
            XSSFCell celda64 = fila6.getCell(4);
            celda64.setCellValue(jTable_facturas.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 6).toString());

            //Datos de la septima fila
            XSSFRow fila7 = hoja.getRow(7);
            XSSFCell celda72 = fila7.getCell(2);
            celda72.setCellValue(datos[3]);

            //Datos de la octava fila
            XSSFRow fila8 = hoja.getRow(8);
            XSSFCell celda82 = fila8.getCell(2);
            celda82.setCellValue(datos[4]);

            //Traemos el detalle de los items facturados
            ArrayList<String[]> listado = ConsultarElementosFactura(numeroFactura);
            
            //Declaramos una variable para capturar el total de la factura
            int total = 0;
            //Declaramos la variable con la fila donde se inicia a agregar la informacion
            int filaInicio = 11;

            //Recorremos la informacion de cada item y lo agregamos a las celdas correspondientes
            for (String[] elemento : listado) {
                XSSFRow fila = hoja.getRow(filaInicio);
                for (int i = 0; i < elemento.length; i++) {

                    if (i == 4) {
                        total += Double.parseDouble(elemento[i]);
                    }

                    XSSFCell celda = fila.getCell(i);
                    if (i == 0 || i == 1 || i == 3 || i == 4) {
                        celda.setCellValue(Double.parseDouble(elemento[i]));
                    } else if (i == 2) {
                        celda.setCellValue(elemento[i]);
                    }
                }
                filaInicio++;
            }

            XSSFRow fila40 = hoja.getRow(40);
            XSSFCell celda404 = fila40.getCell(4);
            celda404.setCellValue(total);

            FileOutputStream ultimo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(ultimo);
            ultimo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error en generar la factura. Asegurse se no tener una factura abierta y vuelta a intentarlo. ImprimirRecibo GenerarRecibo()");
            e.printStackTrace();
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by th e Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_facturas = new javax.swing.JTable();
        jLabel_fila = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_factura = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_idcliente = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_facturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha", "No. Fact.", "Id Cliente", "Cliente", "Valor facturado", "Estado pago", "Condicion de pago"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_facturas.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_facturas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_facturasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_facturas);
        if (jTable_facturas.getColumnModel().getColumnCount() > 0) {
            jTable_facturas.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable_facturas.getColumnModel().getColumn(1).setPreferredWidth(70);
            jTable_facturas.getColumnModel().getColumn(2).setPreferredWidth(70);
            jTable_facturas.getColumnModel().getColumn(3).setPreferredWidth(220);
            jTable_facturas.getColumnModel().getColumn(4).setPreferredWidth(120);
            jTable_facturas.getColumnModel().getColumn(5).setPreferredWidth(120);
            jTable_facturas.getColumnModel().getColumn(6).setPreferredWidth(200);
        }

        jLabel_fila.setText("jLabel2");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Factura", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Factura");

        jLabel2.setText("Id Cliente");

        jLabel3.setText("Cliente");

        jTextField_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_clienteActionPerformed(evt);
            }
        });

        jButton1.setText("Nota de credito");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jTextField_factura, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jTextField_idcliente, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(202, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jTextField_idcliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jTextField_factura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel_fila)
                .addGap(229, 229, 229))
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addComponent(jLabel_fila))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_clienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_clienteActionPerformed

    private void jTable_facturasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_facturasMouseClicked

        int fila = jTable_facturas.getSelectedRow();
        if (fila != -1) {
            jLabel_fila.setText(String.valueOf(fila));
            jTextField_cliente.setText(jTable_facturas.getValueAt(fila, 3).toString());
            jTextField_factura.setText(jTable_facturas.getValueAt(fila, 1).toString());
            jTextField_idcliente.setText(jTable_facturas.getValueAt(fila, 2).toString());
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila");
        }
    }//GEN-LAST:event_jTable_facturasMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //Verificamos que se haya seleccionado una factura
        String factura = jTextField_factura.getText().trim();
        if (!factura.equals("")) {
            //Capturamos el resto de datos
            String fecha = jTable_facturas.getValueAt(Integer.parseInt(jLabel_fila.getText().trim()), 0).toString();
            String idCliente = jTextField_idcliente.getText().trim();
            ReimprimirFactura(factura, fecha, idCliente);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione la factura que desea reimprimir");

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
            java.util.logging.Logger.getLogger(ListadoFacturasParaNotaCredito.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoFacturasParaNotaCredito.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoFacturasParaNotaCredito.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoFacturasParaNotaCredito.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoFacturasParaNotaCredito().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel_fila;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_facturas;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_factura;
    private javax.swing.JTextField jTextField_idcliente;
    // End of variables declaration//GEN-END:variables
}

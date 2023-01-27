package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ConsolidadoPendienteFacturar extends javax.swing.JFrame {

    String usuario, permiso;
    DefaultTableModel modelo;

    public ConsolidadoPendienteFacturar() {
        initComponents();
        //LlenarComboBoxClientes();
        SettearModelo();
        llenarTabla();
        ConfiguracionGralJFrame();

    }

    public ConsolidadoPendienteFacturar(String usuario, String permiso) {
        initComponents();
        this.usuario = usuario;
        this.permiso = permiso;
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado de pedidos pendientes por facturar *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);


    }

    public void IniciarCaracteristicasGenerales() {
        //LlenarComboBoxClientes();
        SettearModelo();
        llenarTabla();
    }

    public void SettearModelo() {
        modelo = (DefaultTableModel) jTable_tablaPendientes.getModel();
    }

    public void llenarTabla() {

        modelo = (DefaultTableModel) jTable_tablaPendientes.getModel();

        String consulta = "select er.id, er.idRemision, er.idVenta, c.nombreCliente, v.FechaventaSistema, v.descripcionTrabajo, v.Cantidad, v.unitario, ifnull(er.cantidad - sum(ef.cantidad), er.cantidad) as saldo\n"
                + "from elementosremision er left join elementosfactura ef on er.id=ef.idElementoRemito and ef.estado='Activo'\n"
                + "left join ventas v on er.idVenta=v.Idventa \n"
                + "left join clientes c on v.Idcliente=c.idCliente\n"
                + "where er.estado='Activo'\n"
                + "group by er.id, c.nombreCliente\n"
                + "having saldo > 0 "
                + "order by er.id desc;";

        HashSet<String> clientes = new HashSet<>();

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                Object[] datos = new Object[7];
                datos[0] = rs.getString("er.idVenta");
                datos[1] = rs.getString("v.FechaventaSistema");
                datos[2] = rs.getString("c.nombreCliente");
                datos[3] = rs.getString("v.descripcionTrabajo");
                //datos[4] = rs.getString("v.Cantidad");
                datos[4] = rs.getString("saldo");
                datos[5] = MetodosGenerales.ConvertirIntAMoneda(rs.getDouble("v.unitario"));
                double cant = rs.getDouble("saldo");
                double unita = rs.getDouble("v.unitario");

                datos[6] = MetodosGenerales.ConvertirIntAMoneda(cant * unita);
                modelo.addRow(datos);

                clientes.add(rs.getString("c.nombreCliente"));

            }

            jTable_tablaPendientes.setModel(modelo);

            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable_tablaPendientes.setRowSorter(ordenador);
            
            for (String cliente : clientes) {
                jComboBox_cliente.addItem(cliente);
            }

        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer las ventas pendientes por facturar. Contacte al administrador. "
                    + "ConsolidadoPendienteFacturar, llenarTabla()");
            e.printStackTrace();
        }

    }

    public void GenerarReporte(ArrayList<Object[]> lista) {

//        String rutaArchivoACopiar = "C:" + File.separator + "Users" + File.separator + "Erwin P" + File.separator + "Documents"
//                + File.separator + "NetBeansProjects" + File.separator + "Gestion" + File.separator + "src" + File.separator + "Docs"
//                + File.separator + "Pendientes por facturar consolidado.xlsx";
        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs"+File.separator+"Pendientes por facturar consolidado.xlsx";

        String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Pendientes por facturar consolidado.xlsx";
        try {
            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();

            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            //Cargamos los datos de la cabecera
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda12 = fila1.getCell(2);
            celda12.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda22 = fila2.getCell(2);
            celda22.setCellValue(this.usuario);

            //Dado que la informacion se empezara a cargar desde la fila 5, establecemos ese inicio
            int filaInicio = 5;
            //Recorremos el numero de filas que tiene la tabla
            for (int i = 0; i < lista.size(); i++) {
                XSSFRow fila = hoja.getRow(filaInicio);
                //Recorremos ahora las columas para obtener los datos
                for (int j = 0; j < lista.get(0).length; j++) {
                    XSSFCell celda = fila.getCell(j);
                    //En funcion de la columna en la que nos encontremos se tomará el cliente de dato para el reporte
                    switch (j) {
                        case 0:
                            celda.setCellValue((Integer) lista.get(i)[j]);
                            break;
                        case 1:
                            celda.setCellValue((Date) lista.get(i)[j]);
                            break;
                        case 2:
                            celda.setCellValue((String) lista.get(i)[j]);
                            break;
                        case 3:
                            celda.setCellValue((String) lista.get(i)[j]);
                            break;
                        case 4:
                            celda.setCellValue((Double) lista.get(i)[j]);
                            break;
                        case 5:
                            celda.setCellValue((Double) lista.get(i)[j]);
                            break;
                        case 6:
                            celda.setCellValue((Double) lista.get(i)[j]);
                            break;

                    }

                }
                filaInicio++;

            }
            FileOutputStream nuevo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(nuevo);
            nuevo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);

        } catch(FileNotFoundException ex){
            JOptionPane.showMessageDialog(this, "Error al crear el documento. Un documento con el mismo nombre esta abierto."
                    + "\nCierrelo e intentelo nuevamente");
            ex.printStackTrace();
        } catch (IOException | NumberFormatException  e) {
            JOptionPane.showMessageDialog(null, "Error en generar el reporte de deuda de Empresas ");
            e.printStackTrace();
        }

    }

//    public void LlenarComboBoxClientes() {
//
//        ArrayList<String> listaClientes = new ArrayList<>();
//
//        String consulta = "select clientes.nombreCliente, ventas.Cantidad - "
//                + "IF(sum(elementosfactura.cantidadFacturada) is null, 0, sum(elementosfactura.cantidadFacturada)) "
//                + "as cantidadPendiente from ventas left join elementosfactura ON "
//                + "ventas.Idventa=elementosfactura.idVenta join clientes on ventas.Idcliente=clientes.idCliente "
//                + "where clientes.tipoCliente='Empresa' GROUP by ventas.Idventa HAVING cantidadPendiente>0";
//
//        Connection cn = Conexion.Conectar();
//        try {
//            PreparedStatement pst = cn.prepareStatement(consulta);
//            ResultSet rs = pst.executeQuery();
//
//            while (rs.next()) {
//                String nuevo = rs.getString("clientes.nombreCliente");
//                listaClientes.add(nuevo);
//            }
//            cn.close();
//
//            //Eliminamos los repetidos
//            Set<String> hashSet = new HashSet<String>(listaClientes);
//            listaClientes.clear();
//            listaClientes.addAll(hashSet);
//
//            for (String cliente : hashSet) {
//                jComboBox_cliente.addItem(cliente);
//            }
//
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(this, "Error en leer los clientes con deudas ConsolidadoPendientePorFacturar \n "
//                    + "LlenarComboBoxClientes() " + e);
//        }
//
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_tablaPendientes = new javax.swing.JTable();
        jButton_generarInforme = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jComboBox_cliente = new javax.swing.JComboBox<>();
        jButton_calcular = new javax.swing.JButton();
        jTextField_subtotal = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_tablaPendientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "IdVenta", "Fecha", "Cliente", "Descripcion", "Cant. pend. Fact", "P. Unitario", "Saldo pend."
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_tablaPendientes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(jTable_tablaPendientes);
        if (jTable_tablaPendientes.getColumnModel().getColumnCount() > 0) {
            jTable_tablaPendientes.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable_tablaPendientes.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTable_tablaPendientes.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTable_tablaPendientes.getColumnModel().getColumn(3).setPreferredWidth(250);
            jTable_tablaPendientes.getColumnModel().getColumn(4).setPreferredWidth(70);
            jTable_tablaPendientes.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTable_tablaPendientes.getColumnModel().getColumn(6).setPreferredWidth(100);
        }

        jButton_generarInforme.setText("Generar Informe");
        jButton_generarInforme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generarInformeActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Consultar total sin facturar por cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel8.setText("Totalizar cliente");

        jComboBox_cliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos" }));

        jButton_calcular.setText("Calcular");
        jButton_calcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_calcularActionPerformed(evt);
            }
        });

        jTextField_subtotal.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_calcular)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_calcular)
                    .addComponent(jTextField_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_generarInforme, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 860, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jButton_generarInforme)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_generarInformeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generarInformeActionPerformed
        //Verificamos que haya elementos en la tabla
        int cantidadFilas = jTable_tablaPendientes.getRowCount();
        if (cantidadFilas > 0) {

            ArrayList<Object[]> listado = new ArrayList<>();

            try {
                for (int i = 0; i < jTable_tablaPendientes.getRowCount(); i++) {
                    Object[] objeto = new Object[7];
                    objeto[0] = Integer.parseInt(jTable_tablaPendientes.getValueAt(i, 0).toString());
                    objeto[1] = new SimpleDateFormat("yyyy-MM-dd").parse(jTable_tablaPendientes.getValueAt(i, 1).toString());
                    objeto[2] = jTable_tablaPendientes.getValueAt(i, 2).toString();
                    objeto[3] = jTable_tablaPendientes.getValueAt(i, 3).toString();
                    objeto[4] = Double.parseDouble(jTable_tablaPendientes.getValueAt(i, 4).toString());
                    objeto[5] = Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jTable_tablaPendientes.getValueAt(i, 5).toString()));
                    objeto[6] = Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jTable_tablaPendientes.getValueAt(i, 6).toString()));

                    listado.add(objeto);
                }
            } catch (NumberFormatException | ParseException e) {
                JOptionPane.showMessageDialog(this, "Error al parsear datos","Error",JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

            GenerarReporte(listado);
        } else {
            JOptionPane.showMessageDialog(this, "No hay datos para generar el informe","Informacion",JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton_generarInformeActionPerformed

    private void jButton_calcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_calcularActionPerformed
        //Verificamos que la tabla tenga datos
        int numeroFilas = jTable_tablaPendientes.getRowCount();
        if (numeroFilas > 0) {
            //Sumamos los valor segun lo seleccionado
            String cliente = jComboBox_cliente.getSelectedItem().toString();
            //Declaramos la variable que acumulara la suma
            double suma = 0;
            if (!cliente.equals("Todos")) {

                for (int i = 0; i < numeroFilas; i++) {
                    if (jTable_tablaPendientes.getValueAt(i, 2).equals(cliente)) {
                        suma += Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jTable_tablaPendientes.getValueAt(i, 6).toString()));

                    }
                }

                jTextField_subtotal.setText(MetodosGenerales.ConvertirIntAMoneda(suma));
            } else {
                for (int i = 0; i < numeroFilas; i++) {
                    suma += Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jTable_tablaPendientes.getValueAt(i, 6).toString()));

                }

                jTextField_subtotal.setText(MetodosGenerales.ConvertirIntAMoneda(suma));
            }

        } else {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla","Informacion",JOptionPane.INFORMATION_MESSAGE);
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
            java.util.logging.Logger.getLogger(ConsolidadoPendienteFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsolidadoPendienteFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsolidadoPendienteFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsolidadoPendienteFacturar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ConsolidadoPendienteFacturar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_calcular;
    private javax.swing.JButton jButton_generarInforme;
    private javax.swing.JComboBox<String> jComboBox_cliente;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_tablaPendientes;
    private javax.swing.JTextField jTextField_subtotal;
    // End of variables declaration//GEN-END:variables
}

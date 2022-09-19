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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.management.remote.JMXConnectorFactory;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author erwin
 */
public class ListadoRemitos extends javax.swing.JFrame {

    String usuario, permiso;
    DefaultTableModel modelo;

    /**
     * Creates new form ListadoRemitos
     */
    public ListadoRemitos() {
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
    }

    public ListadoRemitos(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

    }

    public void IniciarCaracteristicasGenerales() {
        SetearModelo();
        LlenarTabla();
        InhabilitarCampos();
    }

    public void InhabilitarCampos() {
        jTextField_cliente.setEnabled(false);
        jTextField_numeroRemito.setEnabled(false);
        jLabel_fila.setVisible(false);
    }

    public void SetearModelo() {
        modelo = (DefaultTableModel) jTable1.getModel();
    }

    public void LimpiarCampos() {
        jTextField_cliente.setText("");
        jTextField_numeroRemito.setText("");
        jLabel_fila.setText("");
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado de remisiones *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void LlenarTabla() {
        modelo = (DefaultTableModel) jTable1.getModel();

        String consulta = "select distinct  r.id, r.fecha, c.nombreCliente, r.registradoPor, r.Entrega, r.telefono, r.observaciones\n"
                + "from remision r inner join elementosremision e on r.id=e.idRemision and r.estado='Activo'\n"
                + "inner join ventas v on e.idVenta=v.Idventa\n"
                + "inner join clientes c on v.Idcliente=c.idCliente order by r.id desc";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[7];

                nuevo[0] = rs.getString("r.id");
                nuevo[1] = rs.getString("r.fecha");
                nuevo[2] = rs.getString("c.nombreCliente");
                nuevo[3] = rs.getString("r.registradoPor");
                nuevo[4] = rs.getString("r.Entrega");
                nuevo[5] = rs.getString("r.telefono");
                nuevo[6] = rs.getString("r.observaciones");

                modelo.addRow(nuevo);
            }
            jTable1.setModel(modelo);

            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable1.setRowSorter(ordenador);

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer las remisiones ListadoRemitos LlenarTabla()\n" + e);
            e.printStackTrace();
        }

    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            this.modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void EliminarRemito(String idRemito) {
        String estadoRemito = "update remision set estado='Inactivo' where id=?;";
        String estadoElementosRemito = "update elementosremision set estado='Inactivo' where idRemision=?";

        Connection cn = Conexion.Conectar();

        try {
            cn.setAutoCommit(false);

            PreparedStatement pst = cn.prepareStatement(estadoRemito);
            pst.setString(1, idRemito);
            pst.executeUpdate();

            PreparedStatement pst2 = cn.prepareStatement(estadoElementosRemito);
            pst2.setString(1, idRemito);
            pst2.executeUpdate();

            cn.commit();
            cn.close();

            JOptionPane.showMessageDialog(this, "Remision eliminada", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al elimienar la remision ListadoRemitos EliminarRemito()\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void GenerarRemito(String idRemito, String[] datosCliente, Object[] datosRemito, ArrayList<Object[]> listado) {

        String rutaPlantilla = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Remision.xlsx";
        String rutaAGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Remision No. " + idRemito + " - " + datosCliente[0] + ".xlsx";

        try {
            FileInputStream ArchivoATomar = new FileInputStream(rutaPlantilla);
            XSSFWorkbook wb = new XSSFWorkbook(ArchivoATomar);
            ArchivoATomar.close();
            XSSFSheet hoja = wb.getSheetAt(0);

            //Datos de la cuarta fila
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda42 = fila4.getCell(2);
            celda42.setCellValue(datosCliente[0]);
            XSSFCell celda44 = fila4.getCell(4);
            celda44.setCellValue(idRemito);

            //Datos de la quinta fila
            XSSFRow fila5 = hoja.getRow(5);
            XSSFCell celda52 = fila5.getCell(2);
            celda52.setCellValue(datosCliente[1]);
            XSSFCell celda54 = fila5.getCell(4);
            celda54.setCellValue((Date) datosRemito[4]);

            //Datos de la sexta fila
            XSSFRow fila6 = hoja.getRow(6);
            XSSFCell celda62 = fila6.getCell(2);
            celda62.setCellValue(datosCliente[2]);

            //Datos de la septima fila
            XSSFRow fila7 = hoja.getRow(7);
            XSSFCell celda72 = fila7.getCell(2);
            celda72.setCellValue(datosCliente[3]);

            //Datos de la octava fila
            XSSFRow fila8 = hoja.getRow(8);
            XSSFCell celda82 = fila8.getCell(2);
            celda82.setCellValue(datosCliente[4]);

            //Datos de la fila 10
            XSSFRow fila10 = hoja.getRow(10);
            XSSFCell celda102 = fila10.getCell(2);
            celda102.setCellValue((String) datosRemito[0]);

            //Datos de la fila 11
            XSSFRow fila11 = hoja.getRow(11);
            XSSFCell celda111 = fila11.getCell(1);
            celda111.setCellValue((String) datosRemito[1]);
            XSSFCell celda114 = fila11.getCell(4);
            celda114.setCellValue((String) datosRemito[3]);

            //Datos de la fila 12
            XSSFRow fila12 = hoja.getRow(12);
            XSSFCell celda122 = fila12.getCell(2);
            celda122.setCellValue((String) datosRemito[2]);

            //Llenamos los datos de la tabla
            int filaInicial = 15;

            for (Object[] elemento : listado) {
                XSSFRow filaParaIniciar = hoja.getRow(filaInicial);
                for (int i = 0; i < 5; i++) {
                    XSSFCell celdaParaIniciar = filaParaIniciar.getCell(i);

                    switch (i) {
                        case 0:
                            celdaParaIniciar.setCellValue((Double) elemento[i]);
                            break;
                        case 1:
                            celdaParaIniciar.setCellValue((String) elemento[i]);
                            break;
                        case 4:
                            celdaParaIniciar.setCellValue((Double) elemento[2]);
                            break;
                    }
                }
                filaInicial++;
            }

            FileOutputStream libro = new FileOutputStream(rutaAGuardar);
            wb.write(libro);
            libro.close();
            JOptionPane.showMessageDialog(this, "Remision generada", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            MetodosGenerales.abrirArchivo(rutaAGuardar);

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "No es posible crear la remision. Si tiene una remision con el mismo nombre abierto,"
                    + " \ncierrelo e intente generarla nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al generar el informe", "Error", JOptionPane.ERROR_MESSAGE);
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

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en leer los datos del cliente ListadoFacturas ConsultarDatosCliente()");
        }

        return datosCliente;
    }

    public String ConsultarIdClienteDelRemito(String idRemito) {
        String idCliente = "";
        String consulta = "select idCliente from remision where id=?";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idRemito);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                idCliente = rs.getString("idCliente");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el id del cliente ListadoRemitos ConsultarIdClienteDelRemito()\n" + e);
        }

        return idCliente;
    }

    public ArrayList<String[]> ConsultarElementosRemito(String idRemito) {
        ArrayList<String[]> listadoDeElementos = new ArrayList<>();

        String consulta = "select elementosremision.idVenta, ventas.descripcionTrabajo, ventas.tamaño, "
                + "ventas.colorTinta, elementosremision.cantidad from elementosremision inner join ventas "
                + "on elementosremision.idVenta=ventas.Idventa and elementosremision.idRemision=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idRemito);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String[] nuevo = new String[3];
                nuevo[0] = rs.getString("elementosremision.idVenta");
                nuevo[1] = rs.getString("ventas.descripcionTrabajo") + " - " + rs.getString("ventas.tamaño") + " - " + rs.getString("ventas.colorTinta");
                nuevo[2] = rs.getString("elementosremision.cantidad");
                listadoDeElementos.add(nuevo);
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer los elementos de la remision ListadoRemitos ConsultarElementosRemito()\n" + e);
        }

        return listadoDeElementos;
    }

    public boolean ComprobarFacturas(String idRemito) {

        String consulta = "select ef.factura\n"
                + "from elementosfactura ef join elementosremision er\n"
                + "on er.id=ef.idElementoRemito and er.estado='Activo' and ef.estado='Activo' and er.idRemision=?";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idRemito);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return false;
            }
            cn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al buscar la factura asociada al remito", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return true;
    }

    public String[] consultarCliente(String cliente) {

        String[] datosCliente = new String[5];

        String consulta = "select nombreCliente, identificacion, concat(direccion,' - ',municipio) as direc, "
                + "telefono, email from clientes where nombreCliente=?";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, cliente);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                datosCliente[0] = rs.getString("nombreCliente");
                datosCliente[1] = rs.getString("identificacion");
                datosCliente[2] = rs.getString("direc");
                datosCliente[3] = rs.getString("telefono");
                datosCliente[4] = rs.getString("email");
            }

            cn.close();
            return datosCliente;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los datos del cliente", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public Object[] consultarRemision(String idRemito) {

        Object[] datosCliente = new Object[5];

        String consulta = "select Entrega, fecha, observaciones, registradoPor, telefono from remision where id=?";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idRemito);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                datosCliente[0] = rs.getString("registradoPor");
                datosCliente[1] = rs.getString("Entrega");
                datosCliente[2] = rs.getString("observaciones");
                datosCliente[3] = rs.getString("telefono");
                datosCliente[4] = rs.getDate("fecha");
            }

            cn.close();
            return datosCliente;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los datos de la remision", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Object[]> consultarElementosRemision(String idRemito) {

        ArrayList<Object[]> listado = new ArrayList<>();

        String consulta = "select e.idVenta, v.descripcionTrabajo, v.papelOriginal, v.tamaño, v.colorTinta, e.cantidad\n"
                + "from elementosremision e join ventas v on e.idVenta=v.Idventa\n"
                + "where e.idRemision=? and e.estado='Activo' ";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idRemito);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                String papel = (rs.getString("v.papelOriginal").equalsIgnoreCase("No aplica")) ? "" : " - " + rs.getString("v.papelOriginal");
                String tamaño = (rs.getString("v.tamaño").equalsIgnoreCase("No aplica")) ? "" : " - " + rs.getString("v.tamaño");
                String color = (rs.getString("v.colorTinta").equalsIgnoreCase("No aplica")) ? "" : " - " + rs.getString("v.colorTinta");

                Object[] nuevo = new Object[3];
                nuevo[0] = rs.getDouble("e.idVenta");
                nuevo[1] = rs.getString("v.descripcionTrabajo") + tamaño+ color + papel;;
                nuevo[2] = rs.getDouble("e.cantidad");

                listado.add(nuevo);
            }

            cn.close();
            return listado;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los elementos de la remision", "Error", JOptionPane.ERROR_MESSAGE);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_numeroRemito = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_cliente = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel_fila = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Remision", "Fecha", "Cliente", "Genera", "Entrega", "Telefono", "Observaciones"
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
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(200);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informacion remision", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("No. Remito");

        jLabel2.setText("Cliente");

        jButton1.setText("Imprimir");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Eliminar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_numeroRemito, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_numeroRemito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jLabel_fila.setText("jLabel3");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1014, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77)
                        .addComponent(jLabel_fila)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel_fila)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int fila = jTable1.getSelectedRow();
        if (fila != -1) {
            jTextField_cliente.setText(jTable1.getValueAt(fila, 2).toString());
            jTextField_numeroRemito.setText(jTable1.getValueAt(fila, 0).toString());
            jLabel_fila.setText(String.valueOf(fila));
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila");
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //Verificamos que se haya seleccionado un remito
        String idRemito = jTextField_numeroRemito.getText().trim();
        if (!idRemito.equals("")) {

            String cliente = jTextField_cliente.getText().trim();
            String[] datosCliente = consultarCliente(cliente);
            Object[] datosRemision = consultarRemision(idRemito);
            ArrayList<Object[]> elementosRemision = consultarElementosRemision(idRemito);

            GenerarRemito(idRemito, datosCliente, datosRemision, elementosRemision);
            LimpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una remision para imprimir", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //Verificamos que se haya seleccionado un remito
        String idRemito = jTextField_numeroRemito.getText().trim();
        if (!idRemito.equals("")) {
            int opcion = JOptionPane.showConfirmDialog(this, "¿Desea elimiminar la remision?", "Confirmacion", JOptionPane.INFORMATION_MESSAGE);
            if (opcion == 0) {

                if (ComprobarFacturas(idRemito)) {
                    EliminarRemito(idRemito);
                    limpiarTabla(modelo);
                    LlenarTabla();
                    LimpiarCampos();
                } else {
                    JOptionPane.showMessageDialog(this, "No es posible eliminar la remisión ya que el mismo tiene facturas asociadas.\n"
                            + "Anule la factura antes de poder proceder", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una remision para imprimir", "Informacion", JOptionPane.INFORMATION_MESSAGE);
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
            java.util.logging.Logger.getLogger(ListadoRemitos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoRemitos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoRemitos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoRemitos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoRemitos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel_fila;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField_cliente;
    private javax.swing.JTextField jTextField_numeroRemito;
    // End of variables declaration//GEN-END:variables
}

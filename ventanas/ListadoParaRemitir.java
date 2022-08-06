/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import com.mysql.cj.protocol.Resultset;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Erwin P
 */
public class ListadoParaRemitir extends javax.swing.JFrame {

    String usuario, permisos, idcliente;
    DefaultTableModel pendientesRemitir;

    /**
     * Creates new form ListadoRemitir2
     */
    public ListadoParaRemitir(String usuario, String permisos, String idcliente) {
        this.usuario = usuario;
        this.permisos = permisos;
        this.idcliente = idcliente;

        initComponents();
        SetearModeloTablas();
        LlenarTabla(this.idcliente);
        CargarDatos(this.idcliente);
        InhabilitarComponentes();
        ConfiguracionGralJFrame();
    }

    private ListadoParaRemitir() {

        initComponents();
        SetearModeloTablas();
        LlenarTabla(this.idcliente);
        InhabilitarComponentes();
    }

    public void SetearModeloTablas() {
        pendientesRemitir = (DefaultTableModel) jTable1.getModel();

    }

    public void InhabilitarComponentes() {
        jTextField_total.setEnabled(false);
        jTextField_id.setEnabled(false);
        jTextField_nombreCliente.setEnabled(false);
        jTextField_prueba.setVisible(false);
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Listado para remitir *** " + "Usuario: " + usuario + " - " + this.permisos);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void CargarDatos(String idCliente) {
        String consultarCliente = "select nombreCliente from clientes where idCliente=?";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consultarCliente);
            pst.setString(1, idCliente);

            ResultSet rs = pst.executeQuery();
            String cliente = new String();
            while (rs.next()) {
                cliente = rs.getString("nombreCliente");
            }

            jTextField_id.setText(this.idcliente);
            jTextField_nombreCliente.setText(cliente);
            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el nombre del cliente. Contacte al administrador");
        }
    }

    public void LlenarTabla(String idCliente) {

        String consulta = "select v.Idventa, v.descripcionTrabajo, ifnull(v.Cantidad - sum(e.cantidad), v.Cantidad) as saldo, v.unitario\n"
                + "from ventas v left join elementosremision e on v.Idventa=e.idVenta and e.estado='Activo'\n"
                + "left join remision r on e.idRemision=r.id  where v.Idcliente=? and v.estado='Activo' and v.tipoVenta='Empresas' \n"
                + "group by v.Idventa having saldo>0 order by v.Idventa desc";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idCliente);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                Object[] nuevo = new Object[7];

                nuevo[0] = Boolean.FALSE;
                nuevo[1] = rs.getString("v.Idventa");
                nuevo[2] = rs.getString("v.descripcionTrabajo");
                nuevo[3] = rs.getString("saldo");
                nuevo[4] = rs.getString("saldo");
                nuevo[5] = MetodosGenerales.ConvertirIntAMoneda(Double.parseDouble(rs.getString("v.unitario")));
                double cantidad = rs.getDouble("saldo");
                double unitario = rs.getDouble("v.unitario");
                double subtotal = cantidad * unitario;

                nuevo[6] = MetodosGenerales.ConvertirIntAMoneda(subtotal);

                pendientesRemitir.addRow(nuevo);

            }

            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en cargar datos en la tabla, contacte al administrador");
            e.printStackTrace();

        }

    }

    public void CargarRemito(ArrayList<String[]> listado, String entrega, String telefono, String observaciones) {

        String insertaremision = "insert into remision (fecha, Entrega, telefono, observaciones, registradoPor, "
                + "estado) values (?, ?, ?, ?, ?, ?)";

        String insertaElementos = "insert into elementosremision (idRemision, cantidad, idVenta) values (?, ?, ?)";

        Connection cn = Conexion.Conectar();

        try {

            cn.setAutoCommit(false);

            PreparedStatement pst = cn.prepareStatement(insertaremision, PreparedStatement.RETURN_GENERATED_KEYS);

            pst.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            pst.setString(2, entrega);
            pst.setString(3, telefono);
            pst.setString(4, observaciones);
            pst.setString(5, this.usuario);
            pst.setString(6, "Activo");
            pst.executeUpdate();

            ResultSet idGenerado = pst.getGeneratedKeys();
            idGenerado.next();
            int idremito = idGenerado.getInt(1);
            pst.close();

            PreparedStatement pst2 = cn.prepareStatement(insertaElementos);

            for (String[] strings : listado) {
                pst2.setInt(1, idremito);
                pst2.setString(2, strings[1]);
                pst2.setString(3, strings[0]);
                pst2.executeUpdate();
            }
            pst2.close();
            cn.commit();
            cn.close();
            JOptionPane.showMessageDialog(this, "Remision registrada","Informacion",JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al registrar el remito. Contacte al administrador","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public void LimpiarTabla() {

        for (int i = 0; i < jTable1.getRowCount(); i++) {
            pendientesRemitir.removeRow(i);
            i = i - 1;
        }

    }

    public void ActualizarTabla(ArrayList<Object[]> listado) {

        pendientesRemitir = (DefaultTableModel) jTable1.getModel();

        for (int i = 0; i < listado.size(); i++) {
            pendientesRemitir.addRow(listado.get(i));
        }

    }

    public boolean comprobarGrilla(String cadena) {

        if (cadena.length() == 0) {
            return false;
        } else {
            for (int j = 0; j < cadena.length(); j++) {
//                if (!Character.isDigit(cadena.charAt(j)) || cadena.charAt(0) == '0') {
//                    return false;
//                }
                try {
                    Double.parseDouble(cadena);
                } catch (Exception e) {
                    return false;
                }

            }
        }
        return true;
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
        jTextField_total = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_entrega = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_telefono = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_observaciones = new javax.swing.JTextField();
        jButton_generarRemito = new javax.swing.JButton();
        jTextField_prueba = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextField_id = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField_nombreCliente = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Seleccion", "Id Venta", "Descripcion", "Cant.", "Cant. a entr.", "P.U", "Subt"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(300);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(80);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Total a remitir", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jButton2.setText("Calcular total");
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
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_total, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos de entrega", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP)), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Persona que entrega");

        jLabel2.setText("Telefono");

        jLabel3.setText("Observaciones");

        jButton_generarRemito.setText("Generar remision");
        jButton_generarRemito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generarRemitoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_entrega, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_generarRemito)))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_entrega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_observaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_generarRemito))
                .addContainerGap())
        );

        jTextField_prueba.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_pruebaKeyTyped(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel4.setText("Id Cliente:");

        jLabel5.setText("Cliente");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jTextField_nombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField_nombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(jTextField_prueba, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 865, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jTextField_prueba, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 381, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_generarRemitoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generarRemitoActionPerformed

        int contador = 0;
        int contadorTexto = 0;
        int contadorElementos = 0;
        int contadorMayorCantidad = 0;

        ArrayList<String[]> listado = new ArrayList<>();
        String entrega = jTextField_entrega.getText().trim().toUpperCase();
        String telefono = jTextField_telefono.getText().trim().toUpperCase();
        String observaciones = jTextField_observaciones.getText().trim().toUpperCase();

        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if (jTable1.getValueAt(i, 0).equals(true)) {
                contador++;
                break;
            }
        }

        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if (jTable1.getValueAt(i, 0).equals(true)) {
                contadorElementos++;
            }
        }

        if (!entrega.equals("")) {

            if (contadorElementos <= 15) {
                if (contador != 0) {
                    for (int i = 0; i < jTable1.getRowCount(); i++) {

                        if (jTable1.getValueAt(i, 0).equals(true)) {

                            if (comprobarGrilla(jTable1.getValueAt(i, 4).toString())) {

                                String[] nuevo = new String[2];
                                if (Double.parseDouble(jTable1.getValueAt(i, 4).toString())
                                        > Double.parseDouble(jTable1.getValueAt(i, 3).toString())) {

                                    contadorMayorCantidad++;
                                    JOptionPane.showMessageDialog(this, "No es posible remitir mayor cantidad a la vendida", "Error", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    nuevo[0] = jTable1.getValueAt(i, 1).toString();
                                    nuevo[1] = jTable1.getValueAt(i, 4).toString();
                                    listado.add(nuevo);
                                }

                            } else {
                                JOptionPane.showMessageDialog(this, "Las cantidades a remitir no pueden ser textos, \ncantidades negativas ni cero","Error",JOptionPane.ERROR_MESSAGE);
                                contadorTexto++;
                            }

                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "No hay trabajos seleccionador para remitir","Informacion",JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Solo es posible inluir máximo 15 ventas por remito."
                        + " \nSi requiere remitir mas lineas, hagalo en una nueva factura","Error",JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Complete el nombre de la persona que entrega","Informacion",JOptionPane.INFORMATION_MESSAGE);
        }

        if (listado.size() > 0 && contadorTexto == 0 && contadorMayorCantidad == 0) {
            CargarRemito(listado, entrega, telefono, observaciones);
            LimpiarTabla();
            LlenarTabla(this.idcliente);
            dispose();
            //dispose();
            new ListadoRemitos(usuario, permisos).setVisible(true);
        }


    }//GEN-LAST:event_jButton_generarRemitoActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        int filas = jTable1.getRowCount();
        ArrayList<Object[]> listado = new ArrayList<>();

        try {

            if (filas > 0) {
                double suma = 0;
                for (int i = 0; i < filas; i++) {
                    Object[] nuevo = new Object[7];

                    nuevo[0] = Boolean.parseBoolean(jTable1.getValueAt(i, 0).toString());
                    nuevo[1] = jTable1.getValueAt(i, 1).toString();
                    nuevo[2] = jTable1.getValueAt(i, 2).toString();
                    nuevo[3] = jTable1.getValueAt(i, 3).toString();
                    double cantidad = Double.parseDouble(jTable1.getValueAt(i, 4).toString());
                    nuevo[4] = (Object) cantidad;
                    nuevo[5] = jTable1.getValueAt(i, 5).toString();
                    double subtotal = Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jTable1.getValueAt(i, 5).toString()));
                    nuevo[6] = (Object) (MetodosGenerales.ConvertirIntAMoneda(cantidad * subtotal));

                    if (jTable1.getValueAt(i, 0).equals(true)) {
                        suma += (subtotal * cantidad);
                    }

                    listado.add(nuevo);
                }

                LimpiarTabla();
                ActualizarTabla(listado);
                jTextField_total.setText(MetodosGenerales.ConvertirIntAMoneda(suma));

            } else {
                JOptionPane.showMessageDialog(this, "No hay datos en la tabla");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Las cantidades no pueden ser textos, \ncantidades negativas ni cero");
        }


    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField_pruebaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_pruebaKeyTyped

        String texto = jTextField_prueba.getText().trim();

        if (evt.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
            System.out.println("Hola");
        }

        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
        } else if (texto.equals("$")) {
            jTextField_prueba.setText("");
            jTextField_prueba.setText(MetodosGenerales.ConvertirIntAMoneda(Double.parseDouble(String.valueOf(evt.getKeyChar()))));
            evt.consume();
        } else if (texto.equals("")) {
            jTextField_prueba.setText(MetodosGenerales.ConvertirIntAMoneda(Double.parseDouble(String.valueOf(evt.getKeyChar()))));
            evt.consume();
        } else {
            String cadena = MetodosGenerales.ConvertirMonedaAInt(jTextField_prueba.getText().trim());
            cadena += String.valueOf(evt.getKeyChar());
            jTextField_prueba.setText(MetodosGenerales.ConvertirIntAMoneda(Double.parseDouble(cadena)));
            evt.consume();
            System.out.println(MetodosGenerales.ConvertirIntAMoneda(Double.parseDouble(cadena)));
        }

        if (evt.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
            String text = jTextField_prueba.getText().trim();

        }

    }//GEN-LAST:event_jTextField_pruebaKeyTyped

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
            java.util.logging.Logger.getLogger(ListadoParaRemitir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoParaRemitir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoParaRemitir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoParaRemitir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ListadoParaRemitir().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton_generarRemito;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField_entrega;
    private javax.swing.JTextField jTextField_id;
    private javax.swing.JTextField jTextField_nombreCliente;
    private javax.swing.JTextField jTextField_observaciones;
    private javax.swing.JTextField jTextField_prueba;
    private javax.swing.JTextField jTextField_telefono;
    private javax.swing.JTextField jTextField_total;
    // End of variables declaration//GEN-END:variables
}

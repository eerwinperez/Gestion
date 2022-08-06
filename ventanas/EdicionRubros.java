/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import java.awt.HeadlessException;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author erwin
 */
public class EdicionRubros extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso, idPresupuesto, descripcion;

    /**
     * Creates new form EdicionRubros
     */
    public EdicionRubros() {
        initComponents();
        InhabilitarCamposGenerales();
        llenarComboBox();
        ConfiguracionGralJFrame();

    }

    public EdicionRubros(String usuario, String permiso, String idPresupuesto, String descripcion) {
        this.usuario = usuario;
        this.permiso = permiso;
        this.idPresupuesto = idPresupuesto;
        this.descripcion = descripcion;
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();
        //Inabilitamos campos segun permisos
        if (!permiso.equals("Gerente")) {
            InhabilitarSegunPermiso();
        }

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Editar conceptos de presupuesto *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    //Este metodo agrupa todas las caracteristicas de la ventana al iniciarse
    public void IniciarCaracteristicasGenerales() {
        settearValores();
        InhabilitarCamposGenerales();
        llenarTabla(idPresupuesto);
        llenarComboBox();
        llenarComboBoxTipoGastos();
    }

    public void llenarTabla(String idPresupuesto) {

        modelo = (DefaultTableModel) jTable_conceptos.getModel();

//        String consulta = "select itemspresupuesto.idGasto, maestrogastos.descripcion, "
//                + "itemspresupuesto.valorPresupuestado from itemspresupuesto inner join maestrogastos "
//                + "on maestrogastos.id=itemspresupuesto.idGasto where itemspresupuesto.idPresupuesto=?";
        String consulta = "select itemspresupuesto.idGasto, tipogastos.tipoGasto, maestrogastos.descripcion, "
                + "itemspresupuesto.valorPresupuestado from itemspresupuesto inner join maestrogastos "
                + "on maestrogastos.id=itemspresupuesto.idGasto inner join tipogastos on "
                + "tipogastos.idGasto=maestrogastos.idGasto where itemspresupuesto.idPresupuesto=?";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                Object[] datos = new Object[4];
                datos[0] = rs.getString("itemspresupuesto.idGasto");
                datos[1] = rs.getString("tipogastos.tipoGasto");
                datos[2] = rs.getString("maestrogastos.descripcion");
                //datos[3] = rs.getString("itemspresupuesto.valorPresupuestado");
                datos[3] = MetodosGenerales.ConvertirIntAMoneda(rs.getInt("itemspresupuesto.valorPresupuestado"));
                modelo.addRow(datos);
            }

            jTable_conceptos.setModel(modelo);
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los rubros del presupuesto EdicionRubro "
                    + "llenarTabla()");
            e.printStackTrace();
        }

    }

    public void InhabilitarSegunPermiso() {

        jButton_editar.setEnabled(false);
        jButton_agregar.setEnabled(false);

    }

    public void llenarComboBox() {

        String consulta = "select descripcion from maestrogastos where id not in (select idGasto from itemspresupuesto where idPresupuesto=? order by descripcion asc)";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, this.idPresupuesto);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String concepto = rs.getString("descripcion");
                jComboBox_combonuevoconcepto.addItem(concepto);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer listado de conceptos EdicionRubros llenarComboBox()");
        }

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

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable_conceptos.getRowCount(); i++) {
            this.modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void InhabilitarCamposGenerales() {
        jTextField_idpresup.setEnabled(false);
        jTextField_descrippresup.setEnabled(false);
        jTextField_idconcepto.setEnabled(false);
        jTextField_concepto.setEnabled(false);
        jTextField_subtotal.setEnabled(false);
    }

    public void settearValores() {
        jTextField_idpresup.setText(this.idPresupuesto);
        jTextField_descrippresup.setText(this.descripcion);
    }

    //Metodo para actualizar el valor del concepto seleccionado en el presupuesto
    public void ActualizarValorConcepto(String numeroPresupuesto, String idconcepto, String nuevovalor) {
        String consulta = "update itemspresupuesto set valorPresupuestado=? where idPresupuesto=? and idGasto=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, nuevovalor);
            pst.setString(2, numeroPresupuesto);
            pst.setString(3, idconcepto);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "El concepto fue actualizado, nuevo valor $" + nuevovalor);

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en actualizar el valor del concepto EdicionRubros ActualizarValorConcepto(). Asegurse de ingresar un valor numerico mayor o igual a cero."
                    + " \n\nEl valor no debe contener puntos, comas ni espacios.");
        }

    }

    public void LimpiarCampos() {
        jTextField_concepto.setText("");
        jTextField_idconcepto.setText("");
        jTextField_valor.setText("");
        jTextField_valornuevoconcepto.setText("");

    }

    public String ConsultarValorNuevoConcepto(String concepto) {
        String valor = "";
        String consulta = "select presupuestado from maestrogastos where descripcion=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, concepto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                valor = rs.getString("presupuestado");
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en la lectura del presupuesto del concepto seleccionado EdicionRubros"
                    + " ConsultarValorNuevoConcepto()");
        }

        return valor;
    }

    public boolean VerificarConceptoAAgregar(JTable tabla, String nuevoconcepto) {

        boolean verificacion = false;
        for (int i = 0; i < tabla.getRowCount(); i++) {
            if (tabla.getValueAt(i, 2).toString().equals(nuevoconcepto)) {
                verificacion = true;
                break;
            }
        }

        return verificacion;
    }

    public String ConsultarDatosConcepto(String concepto) {
        String id = "";
        String consulta = "select id from maestrogastos where descripcion=? ";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, concepto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                id = rs.getString("id");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el id del concepto seleccionado EdicionRubros ConsultarDatosRegistro()");
        }

        return id;
    }

    public void RegistrarConcepto(String idPresupuesto, String idConcepto, String valor) {

        String consulta = "insert into itemspresupuesto (idPresupuesto, idGasto, valorPresupuestado) values (?, ?, ?)";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            pst.setString(2, idConcepto);
            pst.setString(3, valor);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "Concepto registrado");
            
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "SQLException\nError en registrar el nuevo concepto EdicionRubros RegistrarConcepto()");
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

        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_conceptos = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_idpresup = new javax.swing.JTextField();
        jTextField_descrippresup = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField_idconcepto = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_concepto = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField_valor = new javax.swing.JTextField();
        jButton_editar = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jComboBox_combonuevoconcepto = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jTextField_valornuevoconcepto = new javax.swing.JTextField();
        jButton_traerValor = new javax.swing.JButton();
        jButton_agregar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jComboBox_tipo = new javax.swing.JComboBox<>();
        jButton_calcular = new javax.swing.JButton();
        jTextField_subtotal = new javax.swing.JTextField();

        jButton3.setText("Agregar");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable_conceptos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id Concep.", "Tipo", "Concepto", "Valor presup."
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_conceptos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable_conceptos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_conceptosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_conceptos);
        if (jTable_conceptos.getColumnModel().getColumnCount() > 0) {
            jTable_conceptos.getColumnModel().getColumn(1).setPreferredWidth(200);
            jTable_conceptos.getColumnModel().getColumn(2).setPreferredWidth(280);
            jTable_conceptos.getColumnModel().getColumn(3).setPreferredWidth(120);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Presupuesto a editar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Id Presup.");

        jLabel2.setText("Descripcion");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(22, 22, 22)
                .addComponent(jTextField_idpresup, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jTextField_descrippresup, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jTextField_descrippresup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jTextField_idpresup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Item a editar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel3.setText("Id");

        jLabel4.setText("Descripcion");

        jLabel6.setText("Valor");

        jTextField_valor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_valorKeyTyped(evt);
            }
        });

        jButton_editar.setText("Editar");
        jButton_editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_editarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jTextField_idconcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jTextField_concepto, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jTextField_valor, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_idconcepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_concepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jButton_editar)
                    .addComponent(jLabel6)
                    .addComponent(jTextField_valor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Item a agregar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel5.setText("Descrip.");

        jLabel7.setText("Valor");

        jTextField_valornuevoconcepto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_valornuevoconceptoKeyTyped(evt);
            }
        });

        jButton_traerValor.setText("Traer valor");
        jButton_traerValor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_traerValorActionPerformed(evt);
            }
        });

        jButton_agregar.setText("Agregar");
        jButton_agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox_combonuevoconcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(jTextField_valornuevoconcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_traerValor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_agregar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_combonuevoconcepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jButton_agregar)
                    .addComponent(jLabel7)
                    .addComponent(jTextField_valornuevoconcepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_traerValor))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Consultar total por tipo de gasto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel8.setText("Totalizar tipo");

        jComboBox_tipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));

        jButton_calcular.setText("Calcular");
        jButton_calcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_calcularActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(57, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jComboBox_tipo, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_calcular)
                .addGap(18, 18, 18)
                .addComponent(jTextField_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_calcular)
                    .addComponent(jTextField_subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox_tipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_conceptosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_conceptosMouseClicked
        int fila = jTable_conceptos.getSelectedRow();
        if (fila != -1) {
            jTextField_idconcepto.setText(jTable_conceptos.getValueAt(fila, 0).toString());
            jTextField_concepto.setText(jTable_conceptos.getValueAt(fila, 2).toString());
            jTextField_valor.setText(MetodosGenerales.ConvertirMonedaAInt(jTable_conceptos.getValueAt(fila, 3).toString()));
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un rubro");
        }

    }//GEN-LAST:event_jTable_conceptosMouseClicked

    private void jButton_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_editarActionPerformed
        //verificamos que se haya seleccionado un item para editar
        String idconcepto = jTextField_idconcepto.getText().trim();

        if (!idconcepto.equals("")) {
            String nuevoValor = jTextField_valor.getText().trim();
            //Verificamos que se haya ingresado el valor correspondiente
            if (!nuevoValor.equals("")) {
                ActualizarValorConcepto(this.idPresupuesto, idconcepto, nuevoValor);
                LimpiarCampos();
                limpiarTabla(modelo);
                llenarTabla(this.idPresupuesto);
            } else {
                JOptionPane.showMessageDialog(this, "Ingrese el valor del concepto. En caso de no querer asignarle "
                        + "\n un valor, ingrese cero (0)", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un concepto del presupuesto para editar el valor", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton_editarActionPerformed

    private void jButton_agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregarActionPerformed

        try {
            //Verificamos que el rubro seleccionado no este ya en el presupuesto
            String nuevoconcepto = jComboBox_combonuevoconcepto.getSelectedItem().toString();
            boolean verificacion = VerificarConceptoAAgregar(jTable_conceptos, nuevoconcepto);
//            System.out.println(verificacion);
            if (!verificacion) {
                //Verificamos que el valor se haya completado
                String valor = jTextField_valornuevoconcepto.getText().trim();
                if (!valor.equals("")) {
                    //Capturamos los datos del concepto a sumar
                    String idConcepto = ConsultarDatosConcepto(nuevoconcepto);
                    RegistrarConcepto(this.idPresupuesto, idConcepto, valor);
                    dispose();
                    new EdicionRubros(this.usuario, this.permiso, this.idPresupuesto, this.descripcion).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Complete el valor del concepto a ingresar", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(this, "El concepto que intenta ingresar ya se encuentra dentro del presupuesto", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ingreso no valido", "Error", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton_agregarActionPerformed

    private void jButton_traerValorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_traerValorActionPerformed
        try {
            String concepto = jComboBox_combonuevoconcepto.getSelectedItem().toString();
            String valorConcepto = ConsultarValorNuevoConcepto(concepto);
            jTextField_valornuevoconcepto.setText(valorConcepto);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No es posible traer el valor del concepto seleccionado");
        }


    }//GEN-LAST:event_jButton_traerValorActionPerformed

    private void jButton_calcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_calcularActionPerformed
        //Verificamos que la tabla tenga datos
        int numeroFilas = jTable_conceptos.getRowCount();
        if (numeroFilas > 0) {
            //Sumamos los valor segun lo seleccionado
            String tipo = jComboBox_tipo.getSelectedItem().toString();
            //Declaramos la variable que acumulara la suma
            int suma = 0;
            if (!tipo.equalsIgnoreCase("Todos")) {

                for (int i = 0; i < numeroFilas; i++) {
                    if (jTable_conceptos.getValueAt(i, 1).equals(tipo)) {
                        suma += Integer.parseInt(MetodosGenerales.ConvertirMonedaAInt(jTable_conceptos.getValueAt(i, 3).toString()));
                        jTextField_subtotal.setText(MetodosGenerales.ConvertirIntAMoneda(suma));
                    }
                }
            } else {
                for (int i = 0; i < numeroFilas; i++) {
                    suma += Integer.parseInt(MetodosGenerales.ConvertirMonedaAInt(jTable_conceptos.getValueAt(i, 3).toString()));
                    jTextField_subtotal.setText(MetodosGenerales.ConvertirIntAMoneda(suma));
                }
            }

        } else {
            JOptionPane.showMessageDialog(this, "No hay datos en la tabla");
        }


    }//GEN-LAST:event_jButton_calcularActionPerformed

    private void jTextField_valorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_valorKeyTyped
        char c = evt.getKeyChar();

        if (!Character.isDigit(c) && c != '.') {
            evt.consume();
        }

        if (c == '0' && jTextField_valor.getText().trim().length() == 0) {
            evt.consume();
        }

        if (c == '.' && jTextField_valor.getText().trim().length() == 0) {
            evt.consume();
        }

        int contador = 0;

        char[] cadena = jTextField_valor.getText().trim().toCharArray();
        for (int i = 0; i < jTextField_valor.getText().trim().length(); i++) {
            if (cadena[i] == '.') {
                contador++;
                break;
            }
        }

        if (contador > 0 && c == '.') {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_valorKeyTyped

    private void jTextField_valornuevoconceptoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_valornuevoconceptoKeyTyped

        char c = evt.getKeyChar();

        if (!Character.isDigit(c) && c != '.') {
            evt.consume();
        }

        if (c == '0' && jTextField_valornuevoconcepto.getText().trim().length() == 0) {
            evt.consume();
        }

        if (c == '.' && jTextField_valornuevoconcepto.getText().trim().length() == 0) {
            evt.consume();
        }

        int contador = 0;

        char[] cadena = jTextField_valornuevoconcepto.getText().trim().toCharArray();
        for (int i = 0; i < jTextField_valornuevoconcepto.getText().trim().length(); i++) {
            if (cadena[i] == '.') {
                contador++;
                break;
            }
        }

        if (contador > 0 && c == '.') {
            evt.consume();
        }

    }//GEN-LAST:event_jTextField_valornuevoconceptoKeyTyped

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
            java.util.logging.Logger.getLogger(EdicionRubros.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EdicionRubros.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EdicionRubros.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EdicionRubros.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EdicionRubros().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton_agregar;
    private javax.swing.JButton jButton_calcular;
    private javax.swing.JButton jButton_editar;
    private javax.swing.JButton jButton_traerValor;
    private javax.swing.JComboBox<String> jComboBox_combonuevoconcepto;
    private javax.swing.JComboBox<String> jComboBox_tipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_conceptos;
    private javax.swing.JTextField jTextField_concepto;
    private javax.swing.JTextField jTextField_descrippresup;
    private javax.swing.JTextField jTextField_idconcepto;
    private javax.swing.JTextField jTextField_idpresup;
    private javax.swing.JTextField jTextField_subtotal;
    private javax.swing.JTextField jTextField_valor;
    private javax.swing.JTextField jTextField_valornuevoconcepto;
    // End of variables declaration//GEN-END:variables
}

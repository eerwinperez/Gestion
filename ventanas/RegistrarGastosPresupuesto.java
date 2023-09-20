/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import java.awt.HeadlessException;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author erwin
 */
public class RegistrarGastosPresupuesto extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso, idPresupuesto, presupuesto;

    /**
     * Creates new form RegistrarGastosPresupuesto
     */
    public RegistrarGastosPresupuesto() {
        initComponents();
        SettearDatos();
        InhabilitarCampos();

    }

    public RegistrarGastosPresupuesto(String usuario, String permiso, String idPresupuesto, String presupuesto) {
        this.usuario = usuario;
        this.permiso = permiso;
        this.idPresupuesto = idPresupuesto;
        this.presupuesto = presupuesto;
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

        if (!permiso.equals("Gerente")) {
            jButton_autorizar.setEnabled(false);
            jButton_eliminar.setEnabled(false);
        }

    }

    public void SettearDatos() {
        jTextField_idPresup.setText(this.idPresupuesto);
        jTextField_descripcionPresup.setText(this.presupuesto);

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Registrar gastos a presupuesto *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void IniciarCaracteristicasGenerales() {
        llenarTabla(idPresupuesto);
        SettearDatos();
        InhabilitarCampos();
        llenarComboBox(idPresupuesto);
    }

    public void limpiarCampos() {
        jComboBox_conceptos.setSelectedIndex(0);
        jComboBox_estadoDeuda.setSelectedIndex(0);
        jTextField_valor.setText("");
        jTextField_descripcionGasto.setText("");
        jTextField_idConcepto.setText("");
        jTextField_concepto.setText("");
        jTextField_comentarioAutoriza.setText("");
        jLabel3_fila.setText("");
        jTextField_factura.setText("");

    }

    public void llenarComboBox(String idpresupuesto) {
        String consulta = "select maestrogastos.descripcion from maestrogastos "
                + "inner join itemspresupuesto on maestrogastos.id=itemspresupuesto.idGasto where "
                + "itemspresupuesto.idPresupuesto=?";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idpresupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                jComboBox_conceptos.addItem(rs.getString("maestrogastos.descripcion"));
            }

            cn.close();
        } catch (Exception e) {
        }
    }

    public void llenarTabla(String idPresupuesto) {

        modelo = (DefaultTableModel) jTable1.getModel();

        String consulta = "select gastospresupuestos.idConcepto, gastospresupuestos.id, gastospresupuestos.fechaGasto, maestrogastos.descripcion, "
                + "gastospresupuestos.observaciones, gastospresupuestos.factura, gastospresupuestos.valor, gastospresupuestos.estado, "
                + "gastospresupuestos.registradoPor, gastospresupuestos.observAutoriza from gastospresupuestos "
                + "inner join maestrogastos on gastospresupuestos.idConcepto=maestrogastos.id where "
                + "gastospresupuestos.idPrespuesto=? order by gastospresupuestos.id desc";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[9];
                nuevo[0] = rs.getString("gastospresupuestos.id");
                nuevo[1] = rs.getString("gastospresupuestos.fechaGasto");
                nuevo[2] = rs.getString("maestrogastos.descripcion");
                nuevo[3] = rs.getString("gastospresupuestos.observaciones");
                nuevo[4] = rs.getString("gastospresupuestos.factura");
                nuevo[5] = rs.getString("gastospresupuestos.valor");
                nuevo[5] = ConvertirIntAMoneda(Double.parseDouble(nuevo[5].toString()));
                nuevo[6] = rs.getString("gastospresupuestos.estado");
                nuevo[7] = rs.getString("gastospresupuestos.registradoPor");
                nuevo[8] = rs.getString("gastospresupuestos.observAutoriza");

                modelo.addRow(nuevo);
            }

            jTable1.setModel(modelo);

            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable1.setRowSorter(ordenador);

            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer los gastos del presupuesto. RegistrarGastosPresupuestos llenarTabla()");
        }
    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            this.modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void InhabilitarCampos() {
        jTextField_idPresup.setEnabled(false);
        jTextField_descripcionPresup.setEnabled(false);
        jTextField_idConcepto.setEnabled(false);
        jTextField_concepto.setEnabled(false);
        jLabel3_fila.setVisible(false);
    }

    public void RegistrarGasto(String idPresupuesto, String fecha, int idconcepto, String factura, int ValorAIngresar, String observaciones, String estado, String registradoPor) {

        String consulta = "insert into gastospresupuestos (idPrespuesto, fechaGasto, idConcepto, factura, valor, observaciones, estado, registradoPor) values "
                + "(?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            pst.setString(2, fecha);
            pst.setInt(3, idconcepto);
            pst.setString(4, factura);
            pst.setInt(5, ValorAIngresar);
            pst.setString(6, observaciones);
            pst.setString(7, estado);
            pst.setString(8, registradoPor);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "Gasto registrado");

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falta completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQLException\nError en insertar el gasto. RegistrarGastoPresupuesto RegistrarGasto()");
            e.printStackTrace();
        }
    }

    public void AutorizarGasto(String idpresupuesto, String idConcepto, String observaciones) {
        String consulta = "update gastospresupuestos set estado=?, observAutoriza=? where id=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, "Registrado");
            pst.setString(2, observaciones);
            pst.setString(3, idConcepto);

            pst.executeUpdate();
            cn.close();

            JOptionPane.showMessageDialog(this, "Gasto autorizado", "Informacion", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en autorizar el gasto. RegistrarGastosPresupuestos AutorizarGasto()", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public int ConsultarIdConcepto(String concepto) {
        int idConcepto = -1;
        String consulta = "Select id from maestrogastos where descripcion=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, concepto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                idConcepto = rs.getInt("id");
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar el Id del concepto. RegistrarGastosPresupuesto ConsultarIdConcepto");

        }

        return idConcepto;
    }

    public int ConsultarPresupuestado(String idpresupuesto, int idConcepto) {

        int valorPresupuestado = -1;
        String consulta = "select valorPresupuestado from itemspresupuesto where idPresupuesto=? and idGasto=?";

        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idpresupuesto);
            pst.setInt(2, idConcepto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                valorPresupuestado = rs.getInt("valorPresupuestado");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar el valor presupuestado. RegistrarGastosPresupuesto ConsultarPresupuestado()");
            e.printStackTrace();
        }

        return valorPresupuestado;
    }

    public int ConsultarSumaYaGastada(String idpresupuesto, int idConcepto) {
        int yagastado = 0;
        String consulta = "select if(sum(gastospresupuestos.valor) is null, 0, sum(gastospresupuestos.valor)) "
                + "as suma from gastospresupuestos where gastospresupuestos.idPrespuesto=? and "
                + "gastospresupuestos.idConcepto=? and gastospresupuestos.estado='Registrado'";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idpresupuesto);
            pst.setInt(2, idConcepto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                yagastado = rs.getInt("suma");
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer la suma ya gastada. RegistrarGastosPresupuesto ConsultarSumaYaGastada()");
        }

        return yagastado;
    }

    public static String ConvertirIntAMoneda(double dato) {
        String result = "";
        DecimalFormat objDF = new DecimalFormat("$ ###, ###");
        result = objDF.format(dato);

        return result;
    }

    public static String ConvertirMonedaAInt(String numero) {
        String MonedaParseada = "";

        try {
            MonedaParseada = new DecimalFormat("$ ###, ###").parse(numero).toString();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return MonedaParseada;
    }

    public void EliminarGasto(String idGasto) {
        String consulta = "delete from gastospresupuestos where id=?";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idGasto);
            pst.execute();
            JOptionPane.showMessageDialog(this, "Gasto eliminado", "Informacion", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar el gasto", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public boolean ComprobarGastosSinAutorizacion(int presupuesto) {
        String consulta = "select estado from gastospresupuestos where idPrespuesto=? and estado='Por Autorizar'";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, presupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                return false;
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el estado de los gastos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return true;
    }

    public String consultarFechaFin(String idPresupuesto) {

        String consulta = "select fechaFin from presupuestos where idPresupuesto=?";
        Connection cn = Conexion.Conectar();

        try {

            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("fechaFin");
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar la fecha fin del presupuesto", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }

    public boolean ComprobarProximoPresupuesto(int presupuesto) {

        String consulta = "select idPresupuesto from presupuestos where idPresupuesto=?";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, presupuesto + 1);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return true;
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error al consultar si existe un presupuesto despues del actual", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return false;
    }

    public String consultarIdPartidadUtilidad(int presupuesto) {

        String consulta = "select min(id) from partidaspresupuestos where idPresupuesto=?";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, presupuesto);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("min(id)");
            }

            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el Id de la partidad utilidad", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public double consultarUtilidad(int presupuesto) {
        double utilidad = 0;

        String ingresosEE = "select ifnull(sum(valor), 0) as abonos from abonos where presupuesto=?";
        String ingresosFac = "select ifnull(sum(abono), 0) as abonos from abonosfacturas where presupuesto=?";
        String gastos = "select ifnull(sum(valor), 0) as abonos from gastospresupuestos where idPrespuesto=?";
        String partidas = "select ifnull(sum(valor), 0) as abonos from partidaspresupuestos where idPresupuesto=?";

        Connection cn = Conexion.Conectar();

        try {

            cn.setAutoCommit(false);

            PreparedStatement pst = cn.prepareStatement(ingresosEE);
            pst.setInt(1, presupuesto);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                utilidad += rs.getDouble("abonos");
            }

            PreparedStatement pst2 = cn.prepareStatement(ingresosFac);
            pst2.setInt(1, presupuesto);
            ResultSet rs2 = pst2.executeQuery();

            if (rs2.next()) {
                utilidad += rs2.getDouble("abonos");
            }

            PreparedStatement pst3 = cn.prepareStatement(partidas);
            pst3.setInt(1, presupuesto);
            ResultSet rs3 = pst3.executeQuery();

            if (rs3.next()) {
                utilidad += rs3.getDouble("abonos");
            }

            PreparedStatement pst4 = cn.prepareStatement(gastos);
            pst4.setInt(1, presupuesto);
            ResultSet rs4 = pst4.executeQuery();

            if (rs4.next()) {
                utilidad -= rs4.getDouble("abonos");
            }

            cn.commit();
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar la utilidad del presupuesto", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return utilidad;
    }

    public String consultarInfoPresupuesto(int presupuesto) {

        String consulta = "select concat('No. ', idPresupuesto, ' - ', descripcion) as descripcion from presupuestos where idPresupuesto=?";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, presupuesto);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("descripcion");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer la informacion del presupuesto", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }

    public void ActualizarUtilidadProvisional(String idPartidaUtilidad, double partidaUtilidad, String concepto) {

        String consulta = "update partidaspresupuestos set valor=?, concepto=? where id=?";

        Connection cn = Conexion.Conectar();

        try {

            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setDouble(1, partidaUtilidad);
            pst.setString(2, concepto);
            pst.setString(3, idPartidaUtilidad);

            pst.execute();

            cn.close();

            JOptionPane.showMessageDialog(this, "Partida provisional actualizada", "Error", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el valor de la partida provisional", "Error", JOptionPane.ERROR_MESSAGE);
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
        jTable1 = new javax.swing.JTable();
        jLabel3_fila = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_idPresup = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_descripcionPresup = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jComboBox_conceptos = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jTextField_valor = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField_descripcionGasto = new javax.swing.JTextField();
        jButton_agregar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jTextField_factura = new javax.swing.JTextField();
        jComboBox_estadoDeuda = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField_idConcepto = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_concepto = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField_comentarioAutoriza = new javax.swing.JTextField();
        jButton_autorizar = new javax.swing.JButton();
        jButton_eliminar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Fecha", "Concepto", "Descripcion gasto", "Factura/Recib.", "Pagado", "Estado", "Registrado Por", "Comentarios Autorizacion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(500);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(150);
        }

        jLabel3_fila.setText("jLabel3");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Presupuesto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Id Presup.");

        jLabel2.setText("Descripcion");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jTextField_idPresup, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_descripcionPresup, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_idPresup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_descripcionPresup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Agregar gasto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jComboBox_conceptos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        jLabel5.setText("Valor");

        jTextField_valor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_valorKeyTyped(evt);
            }
        });

        jLabel6.setText("Descrip. gasto");

        jTextField_descripcionGasto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_descripcionGastoKeyTyped(evt);
            }
        });

        jButton_agregar.setText("Agregar");
        jButton_agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregarActionPerformed(evt);
            }
        });

        jLabel8.setText("Fact/Recibo No.");

        jTextField_factura.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_facturaKeyTyped(evt);
            }
        });

        jComboBox_estadoDeuda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pagado", "Adeudado" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jComboBox_conceptos, 0, 426, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel8)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextField_factura, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_valor, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox_estadoDeuda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_agregar))
                    .addComponent(jTextField_descripcionGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 519, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_conceptos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField_descripcionGasto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField_valor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_agregar)
                    .addComponent(jTextField_factura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jComboBox_estadoDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Autorizar gastos que superan el valor presupuestado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel3.setText("Id");

        jLabel4.setText("Descripcion");

        jLabel7.setText("Comentarios");

        jButton_autorizar.setText("Autorizar gasto");
        jButton_autorizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_autorizarActionPerformed(evt);
            }
        });

        jButton_eliminar.setText("Eliminar");
        jButton_eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_eliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton_autorizar)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_idConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_concepto)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_comentarioAutoriza, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_idConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_concepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField_comentarioAutoriza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_autorizar)
                    .addComponent(jButton_eliminar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3_fila)
                        .addGap(179, 179, 179))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel3_fila))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregarActionPerformed

        //Validamos que se haya seleccionado un concepto de la lista
        String concepto = jComboBox_conceptos.getSelectedItem().toString().trim();
        if (!concepto.equals("")) {
            //Verificamos que se haya completado el valor y la descripcion del gasto
            String descripcionGasto = jTextField_descripcionGasto.getText().trim().toUpperCase();
            String valor = jTextField_valor.getText().trim();
            String factura = jTextField_factura.getText().trim().toUpperCase();
            int valorInt = Integer.parseInt(valor);
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            if (!descripcionGasto.equals("") && !valor.equals("") && !factura.equals("")) {

                int idConcepto = ConsultarIdConcepto(concepto);
                int valorPresupuestado = ConsultarPresupuestado(this.idPresupuesto, idConcepto);
                int SumaYaGastado = ConsultarSumaYaGastada(this.idPresupuesto, idConcepto);

                //Consultamos la fecha de cierre del presupuesto
                try {
                    String fechaFin = consultarFechaFin(this.idPresupuesto);
                    Date fechaFinDate = new SimpleDateFormat("yyyy-MM-dd").parse(fechaFin);
                    Date fechaGasto = new SimpleDateFormat("yyyy-MM-dd").parse(fecha);

                    if (fechaGasto.equals(fechaFinDate) || fechaGasto.before(fechaFinDate)) {

                        //Verificamos que la suma a ingresar mas a suma ya gastada en ese concepto, no sea superior al 
                        //valor presupuestado
                        if (valorInt + SumaYaGastado <= valorPresupuestado) {

                            //Verificamos si el usuario ha seleccionado una deuda para establecer el mensaje a mostrar en pantalla
                            String mensaje = "";
                            if (jComboBox_estadoDeuda.getSelectedItem().toString().trim().equals("Adeudado")) {
                                descripcionGasto="***Adeudado*** "+descripcionGasto;
                                mensaje = "¿Desea registrar un gasto por $" + valorInt
                                        + " bajo la descripcion: " + descripcionGasto + " al concepto " + concepto + "?"
                                        + "\n\n*** Tenga en cuenta que esta registrando una DEUDA ***";
                            } else {
                                mensaje = "¿Desea registrar un gasto por $" + valorInt
                                        + " bajo la descripcion: " + descripcionGasto + " al concepto " + concepto + "?";
                            }

                            //Si el valor a ingresar mas lo ya gastado no supera el presupuesto, se  solicita confirmacion y 
                            //registra el gasto
                            int confirmacion = JOptionPane.showConfirmDialog(this, mensaje, "Confirmacion", JOptionPane.INFORMATION_MESSAGE);

                            if (confirmacion == 0) {
                                String estado = "Registrado";
                                RegistrarGasto(this.idPresupuesto, fecha, idConcepto, factura, valorInt, descripcionGasto, estado, this.usuario);
                                limpiarTabla(modelo);
                                llenarTabla(idPresupuesto);
                                limpiarCampos();

                            } else {
                                JOptionPane.showMessageDialog(this, "Gasto no registrado", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                            }

                        } else {
                            //Verificamos si el usuario ha seleccionado una deuda para establecer el mensaje a mostrar en pantalla
                            String mensaje = "";

                            if (jComboBox_estadoDeuda.getSelectedItem().toString().trim().equals("Adeudado")) {
                                descripcionGasto="***Adeudado*** "+descripcionGasto;
                                mensaje = "La suma de los gastos registrados por el concepto "
                                        + concepto + " supera el valor presupuestado($" + valorPresupuestado + "). ¿Desea pedir autorizacion para cargar el gasto de $"
                                        + valorInt + " a dicho concepto?"
                                        + "\n\n*** Tenga en cuenta que esta registrando una DEUDA ***";
                            } else {
                                mensaje = "La suma de los gastos registrados por el concepto "
                                        + concepto + " supera el valor presupuestado($" + valorPresupuestado + "). ¿Desea pedir autorizacion para cargar el gasto de $"
                                        + valorInt + " a dicho concepto?";
                            }

                            //Le preguntamos al usuario si quiere pedir autorizacion de la gerencia para registrar un gasto
                            //que en suma supera el valor de los presupuestado
                            int eleccion = JOptionPane.showConfirmDialog(this, mensaje, "Informacion", JOptionPane.INFORMATION_MESSAGE);

                            if (eleccion == 0) {

                                String estado = "Por Autorizar";
                                RegistrarGasto(this.idPresupuesto, fecha, idConcepto, factura, valorInt, descripcionGasto, estado, this.usuario);
                                limpiarTabla(modelo);
                                llenarTabla(idPresupuesto);
                                limpiarCampos();
                            } else {
                                JOptionPane.showMessageDialog(this, "Gasto no registrado", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }

                    } else {

                        //Verificamos si el usuario ha seleccionado una deuda para establecer el mensaje a mostrar en pantalla
                        String mensaje = "";

                        if (jComboBox_estadoDeuda.getSelectedItem().toString().trim().equals("Adeudado")) {
                            descripcionGasto="***Adeudado*** "+descripcionGasto;
                            mensaje = "Esta intentando registrar un gasto posterior a la fecha de cierre del presupuesto actual.\n"
                                    + "¿Desea registrar el gasto como 'Pendiente de autorizacion'?"
                                    + "\n\n ***Nota importante***: Tenga en cuenta que el gasto quedará registrado como PENDIENTE DE AUTORIZACION\n"
                                    + "Cuando el Gerente autorice el gasto se deberán revisar todos los meses hacia adelante ya que se verán afectados por el \n"
                                    + "registro de un gasto posterior al cierre del mes en cuestión"
                                    + "\n\n*** Tenga en cuenta que esta registrando una DEUDA ***";
                        } else {
                            mensaje = "Esta intentando registrar un gasto posterior a la fecha de cierre del presupuesto actual.\n"
                                    + "¿Desea registrar el gasto como 'Pendiente de autorizacion'?"
                                    + "\n\n ***Nota importante***: Tenga en cuenta que el gasto quedará registrado como PENDIENTE DE AUTORIZACION\n"
                                    + "Cuando el Gerente autorice el gasto se deberán revisar todos los meses hacia adelante ya que se verán afectados por el \n"
                                    + "registro de un gasto posterior al cierre del mes en cuestión";
                        }

                        int opc = JOptionPane.showConfirmDialog(this, mensaje, "Confirmacion", JOptionPane.INFORMATION_MESSAGE);

                        if (opc == 0) {
                            String estado = "Por Autorizar";
                            RegistrarGasto(this.idPresupuesto, fecha, idConcepto, factura, valorInt, descripcionGasto, estado, this.usuario);
                            limpiarTabla(modelo);
                            llenarTabla(idPresupuesto);
                            limpiarCampos();
                        } else {
                            JOptionPane.showMessageDialog(this, "Gasto no registrado", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (HeadlessException | ParseException e) {
                    JOptionPane.showMessageDialog(this, "Error al parsear las fechas", "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(this, "Complete los campos descripcion y valor", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un concepto de la lista", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton_agregarActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int fila = jTable1.getSelectedRow();
        if (fila != -1) {
            jTextField_idConcepto.setText(jTable1.getValueAt(fila, 0).toString());
            jTextField_concepto.setText(jTable1.getValueAt(fila, 3).toString());
            jLabel3_fila.setText(String.valueOf(fila));
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un concepto");
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton_autorizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_autorizarActionPerformed
        //Verificamos que se haya seleccionado una fila
        String idConcepto = jTextField_idConcepto.getText().trim().toUpperCase();

        if (!idConcepto.equals("")) {
            String fila = jLabel3_fila.getText().trim();
            String comentarios = jTextField_comentarioAutoriza.getText().trim().toUpperCase();
            //Verificamos que haya completado los comentarios en la autorizacion
            if (!comentarios.equals("")) {
                //Verificamos que el estado de la fila seleccionada corresponda a pendiente de autorizacion
                String estado = jTable1.getValueAt(Integer.parseInt(fila), 6).toString();

                if (estado.equals("Por Autorizar")) {

                    AutorizarGasto(this.idPresupuesto, idConcepto, comentarios);
                    limpiarCampos();
                    limpiarTabla(modelo);
                    llenarTabla(idPresupuesto);

                    if (ComprobarGastosSinAutorizacion((int) Integer.valueOf(this.idPresupuesto))) {
                        if (ComprobarProximoPresupuesto((int) Integer.valueOf(this.idPresupuesto))) {

                            String idPartidaUtilidad = consultarIdPartidadUtilidad((int) (Integer.valueOf(this.idPresupuesto)) + 1);
                            double partidaUtilidad = consultarUtilidad((int) Integer.valueOf(this.idPresupuesto));

                            //System.out.println(partidaUtilidad);
                            String info = consultarInfoPresupuesto((int) Integer.valueOf(this.idPresupuesto));
                            String concepto = "UTILIDAD DEL PERIODO ANTERIOR " + info;

                            //System.out.println(concepto + " "+partidaUtilidad);
                            ActualizarUtilidadProvisional(idPartidaUtilidad, partidaUtilidad, concepto);
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "No es posible autorizar gastos ya registrados", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Indique un comentario sobre la autorizacion", "Informacion", JOptionPane.WARNING_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un gasto para autorizar", "Informacion", JOptionPane.ERROR_MESSAGE);
        }


    }//GEN-LAST:event_jButton_autorizarActionPerformed

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

    private void jTextField_descripcionGastoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_descripcionGastoKeyTyped
        if (jTextField_descripcionGasto.getText().trim().length() == 250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_descripcionGastoKeyTyped

    private void jTextField_facturaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_facturaKeyTyped
        if (jTextField_factura.getText().trim().length() == 150) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_facturaKeyTyped

    private void jButton_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_eliminarActionPerformed
        //Comprobamos que hay un gasto seleccionado
        String idGasto = jTextField_idConcepto.getText().trim();

        if (!idGasto.equals("")) {

            int opcion = JOptionPane.showConfirmDialog(this, "¿Desea eliminar el gasto seleccionado?", "Confirmacion", JOptionPane.INFORMATION_MESSAGE);

            if (opcion == 0) {
                EliminarGasto(idGasto);
                limpiarCampos();
                limpiarTabla(modelo);
                llenarTabla(idPresupuesto);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Selecciones un gasto para elimar", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton_eliminarActionPerformed

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
            java.util.logging.Logger.getLogger(RegistrarGastosPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistrarGastosPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistrarGastosPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistrarGastosPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistrarGastosPresupuesto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_agregar;
    private javax.swing.JButton jButton_autorizar;
    private javax.swing.JButton jButton_eliminar;
    private javax.swing.JComboBox<String> jComboBox_conceptos;
    private javax.swing.JComboBox<String> jComboBox_estadoDeuda;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel3_fila;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField_comentarioAutoriza;
    private javax.swing.JTextField jTextField_concepto;
    private javax.swing.JTextField jTextField_descripcionGasto;
    private javax.swing.JTextField jTextField_descripcionPresup;
    private javax.swing.JTextField jTextField_factura;
    private javax.swing.JTextField jTextField_idConcepto;
    private javax.swing.JTextField jTextField_idPresup;
    private javax.swing.JTextField jTextField_valor;
    // End of variables declaration//GEN-END:variables
}

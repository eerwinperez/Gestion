/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import java.awt.Color;
import java.awt.HeadlessException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import javax.mail.MessagingException;
import javax.swing.ImageIcon;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author erwin
 */
public class AgregarDineroPresupuesto extends javax.swing.JFrame {

    String usuario, permiso, idPresupuesto, descripcion;
    DefaultTableModel modelo;

    /**
     * Creates new form AgregarDineroPresupuesto
     */
    public AgregarDineroPresupuesto() {
        initComponents();
        InhabilitarCampos();
        CargarDatos();
        settearTabla();
        llenarTabla();
        ConfiguracionGralJFrame();
    }

    public AgregarDineroPresupuesto(String usuario, String permiso, String idPresupuesto, String descripcion) {
        this.usuario = usuario;
        this.permiso = permiso;
        this.idPresupuesto = idPresupuesto;
        this.descripcion = descripcion;

        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

        //Inabilitamos campos segun permisos
        if (!permiso.equals("Gerente")) {
            InhabilitarSegunPermiso();
        }
    }

    public void IniciarCaracteristicasGenerales() {
        InhabilitarCampos();
        CargarDatos();
        settearTabla();
        llenarTabla();
    }

    public void InhabilitarCampos() {
        jTextField_id.setEnabled(false);
        jTextField_descripcion.setEnabled(false);
        jTextField_idpartida.setEnabled(false);
        jLabel_idpartida.setVisible(false);
        jTextField_total.setEnabled(false);
        jLabel_conceptpactual.setVisible(false);
        jLabel_fechaactual.setVisible(false);
        jLabel_valoractual.setVisible(false);
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Agregar partida presupuestaria *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void InhabilitarSegunPermiso() {
//        jButton_editar.setEnabled(false);
//        jButton_eliminar.setEnabled(false);

    }

    public void settearTabla() {
        modelo = (DefaultTableModel) jTable1.getModel();
    }

    public void CargarDatos() {
        jTextField_id.setText(this.idPresupuesto);
        jTextField_descripcion.setText(this.descripcion);
        if (this.idPresupuesto.equalsIgnoreCase("1")) {
            jButton2_utilidad.setEnabled(false);
        }
    }

    public void RegistrarPartida(String idPresupuesto, String concepto, String valor, String usuario) {

        String consulta = "insert into partidaspresupuestos (fecha, idPresupuesto, concepto, valor, registradoPor) "
                + "values (?, ?, ?, ?, ?)";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            pst.setString(2, idPresupuesto);
            pst.setString(3, concepto);
            pst.setString(4, valor);
            pst.setString(5, usuario);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "Partida por $" + valor + " asignada al presupuesto No. " + idPresupuesto
                    + " - " + this.descripcion);

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio\n\n"
                    + "No es posible ingresar partidas con el mismo nombre.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "MySQLException\nError en registrar la partida. AgregarDineroPresupuesto RegistrarPartida()");
            e.printStackTrace();
        }
    }

    public void RegistrarPartidaActualizada(String idPartida, String concepto, String valor, String usuario) {

        String consulta = "update partidaspresupuestos set fecha=?, concepto=?, valor=?, registradoPor=? where id=?;";

        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            pst.setString(2, concepto);
            pst.setString(3, valor);
            pst.setString(4, usuario);
            pst.setString(5, idPartida);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "Partida actualizada por $" + valor + " asignada al presupuesto No. " + idPresupuesto
                    + " - " + this.descripcion);

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio\n\n"
                    + "No es posible ingresar partidas con el mismo nombre.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "MySQLException\nError en registrar la partida. AgregarDineroPresupuesto RegistrarPartida()");
            e.printStackTrace();
        }
    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void limpiarCampos() {
        jTextField_idpartida.setText("");
        jTextField_partida.setText("");
        jLabel_idpartida.setText("");
        jTextField_valoramodificar.setText("");
        jLabel_conceptpactual.setText("");
        jLabel_fechaactual.setText("");
        jLabel_valoractual.setText("");
        jTextField_concepto.setText("");
        jTextField_valor.setText("");

    }

    public void llenarTabla() {

        jButton2_utilidad.setEnabled(true);

        modelo = (DefaultTableModel) jTable1.getModel();
        ArrayList<Object[]> listado = new ArrayList<>();

        String consulta = "select id, fecha, concepto, valor, registradoPor from "
                + "partidaspresupuestos where idPresupuesto=? order by id desc";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, this.idPresupuesto);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                Object[] datos = new Object[5];
                datos[0] = rs.getString("id");
                datos[1] = rs.getString("fecha");
                datos[2] = rs.getString("concepto");
                datos[3] = MetodosGenerales.ConvertirIntAMoneda(rs.getDouble("valor"));
                datos[4] = rs.getString("registradoPor");
                listado.add(datos);
                modelo.addRow(datos);

            }

            //Si es el primer presupuesto el boton utilidad debe estar inhabilitado
            if (Integer.parseInt(this.idPresupuesto) > 1) {
//                if (listado.isEmpty()) {
//                    jButton2_utilidad.setBackground(Color.magenta);
//                } else {
                //Object[] infoPresup = consultarPresupuestosCalculoUtilidad(Integer.parseInt(this.idPresupuesto), this.descripcion);
                //String concepto = "UTILIDAD DEL PERIODO ANTERIOR " + infoPresup[0] + " " + infoPresup[1];
                for (Object[] info : listado) {

                    if (((String) info[2]).length() >= 13 && ((String) info[2]).substring(0, 13).equals("(PROVISIONAL)")) {
                        jButton2_utilidad.setEnabled(true);
                        jButton2_utilidad.setBackground(Color.magenta);
                        break;
                    } else {
                        jButton2_utilidad.setEnabled(false);
                    }

                }
//                }

            } else {
                jButton2_utilidad.setEnabled(false);
            }

            jTable1.setModel(modelo);

            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable1.setRowSorter(ordenador);

            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer las partidas del presupuesto AgregarDineroPresupuesto "
                    + "LlenarTabla()\n" + e);
        }
    }

    public void ActualizarPartida(String idPartida, String fecha, String idPresupuesto, String concepto, String valor, String presupuesto, String fechaActual, String partidaActal, String montoActual, String razon) {
        String consulta = "update partidaspresupuestos set fecha=?, concepto=?, valor=? where id=?";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fecha);
            pst.setString(2, concepto);
            pst.setString(3, valor);
            pst.setString(4, idPartida);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Partida actualizada");

            String asunto = "Partida " + idPartida + " editada - " + presupuesto;
            String mensaje = "La partida " + idPartida + " ha sido editada"
                    + "\nValor anterior = " + montoActual + " - Nuevo valor " + MetodosGenerales.ConvertirIntAMoneda(Double.parseDouble(valor))
                    + "\nDescripcion anterior: " + partidaActal + " - Nueva descripcion: " + concepto
                    + "\nFecha anterior = " + fechaActual + " - Nueva fecha = " + fecha
                    + "\nUsuario responsable: " + this.usuario
                    + "\nObservaciones: " + razon;

            MetodosGenerales.enviarEmail(asunto, mensaje);
            MetodosGenerales.registrarHistorial(usuario, mensaje);

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MessagingException ex) {
            ex.printStackTrace();
        } catch (HeadlessException | NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(this, "MySQLException\nError al autorizar los datos de la partida");
            e.printStackTrace();
        }

    }

    public void EliminarPartida(String idPpartid, String presupuesto, String valor, String razon) {
        String consulta = "delete from partidaspresupuestos where id=?";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPpartid);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Partida eliminada");

            String asunto = "Partida " + idPpartid + " eliminada - " + presupuesto;
            String mensaje = "Partida eliminada"
                    + "\nValor = " + valor
                    + "\nUsuario responsable: " + this.usuario
                    + "\nObservaciones: " + razon;

            MetodosGenerales.enviarEmail(asunto, mensaje);
            MetodosGenerales.registrarHistorial(this.usuario, mensaje);

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar la partida");
            e.printStackTrace();
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }

    public Object[] consultarPresupuestos(int presupuesto, String nombrePresupuesto) {

        String consulta = "select idPresupuesto, descripcion, fechaInicio, fechaFin, estado from presupuestos where idPresupuesto = ?";

        Object[] elemento = new Object[6];

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, presupuesto - 1);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                elemento[0] = rs.getInt("idPresupuesto");
                elemento[1] = rs.getString("descripcion");
                elemento[2] = rs.getString("fechaInicio");
                elemento[3] = rs.getString("fechaFin");
                elemento[4] = rs.getString("estado");

            }

            if (elemento[4].equals("CERRADO")) {
                int opcion = JOptionPane.showConfirmDialog(this, "¿Desea registrar la utilidad del presupuesto " + elemento[0] + " " + elemento[1] + "?", "Confirmacion", JOptionPane.INFORMATION_MESSAGE);
                if (opcion == 0) {

                    Double partidas = consultarPartidas((Integer) elemento[0]);
                    Double ingresosEntradasDiarias = consultarIngresosEntradasDiarias((String) elemento[2], (String) elemento[3]);
                    Double ingresosFacturas = consultarIngresosFacturas((String) elemento[2], (String) elemento[3]);
                    Double gastos = consultarGastos((Integer) elemento[0]);

                    elemento[5] = partidas + ingresosEntradasDiarias + ingresosFacturas - gastos;

                    return elemento;
                }

            } else {
                int opcion1 = JOptionPane.showConfirmDialog(this, "El presupuesto " + elemento[0] + " " + elemento[1]
                        + " no se encuentra cerrado, esto sugiere que aun falta informacion por ingresar "
                        + "\ny la utilidad a calcular podría no ser real. "
                        + "\n¿Desea continuar con el registro de la utilidad en el presupuesto " + presupuesto + " " + nombrePresupuesto + "?", "Confirmacion", JOptionPane.WARNING_MESSAGE);

                if (opcion1 == 0) {

                    Double partidas = consultarPartidas((Integer) elemento[0]);
                    Double ingresosEntradasDiarias = consultarIngresosEntradasDiarias((String) elemento[2], (String) elemento[3]);
                    Double ingresosFacturas = consultarIngresosFacturas((String) elemento[2], (String) elemento[3]);
                    Double gastos = consultarGastos((Integer) elemento[0]);

                    elemento[5] = partidas + ingresosEntradasDiarias + ingresosFacturas - gastos;

                    return elemento;
                } else {
                    JOptionPane.showMessageDialog(this, "La partida presupuestaria NO ha sido registrada.", "Informacion", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al leer los presupuestos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public Object[] consultarPresupuestosCalculoUtilidad(int presupuesto, String nombrePresupuesto) {

        String consulta = "select idPresupuesto, descripcion, fechaInicio, fechaFin, estado from presupuestos where idPresupuesto = ?";

        Object[] elemento = new Object[6];

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, presupuesto - 1);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                elemento[0] = rs.getInt("idPresupuesto");
                elemento[1] = rs.getString("descripcion");
                elemento[2] = rs.getString("fechaInicio");
                elemento[3] = rs.getString("fechaFin");
                elemento[4] = rs.getString("estado");

            }

            return elemento;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer los presupuestos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public Double consultarPartidas(int presupuesto) {

        String consulta = "select ifnull(sum(valor), 0) as total from partidaspresupuestos where idPresupuesto=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, presupuesto);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al leer las partida", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public Double consultarIngresosEntradasDiarias(String fechaInicial, String fechaFinal) {

        String consulta = "select ifnull(sum(valor),0) as total from abonos where fecha between ? and ? and estado='Activo'";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaInicial);
            pst.setString(2, fechaFinal);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                return rs.getDouble("total");
            }

            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al leer los ingresos por entradas diarias", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public Double consultarIngresosFacturas(String fechaInicial, String fechaFinal) {

        String consulta = "select ifnull(sum(abono),0) as total from abonosfacturas where fecha between ? and ? and estado='Activo'";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaInicial);
            pst.setString(2, fechaFinal);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                return rs.getDouble("total");
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer los ingresos por facturas", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;

    }

    public Double consultarGastos(int presupuesto) {

        String consulta = "select ifnull(sum(valor),0) as total from gastospresupuestos where idPrespuesto=? and estado='Registrado'";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, presupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                return rs.getDouble("total");
            }

            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer los gastos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public boolean comprobarGastos(int presupuesto) {

        String consulta = "select estado from gastospresupuestos where idPrespuesto=? and estado='Por Autorizar'";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setInt(1, presupuesto - 1);

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

    public String consultarNombrePartidaUtilida(String idPartidaUtilidad) {

        String consulta = "select concepto from partidaspresupuestos where id=?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPartidaUtilidad);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("concepto");
            }
            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el nombre de la partidad de Utilidad", "Error", JOptionPane.ERROR_MESSAGE);
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
        jLabel_idpartida = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_id = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_descripcion = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jTextField_concepto = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField_valor = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton_editar = new javax.swing.JButton();
        jTextField_partida = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField_idpartida = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField_valoramodificar = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jDateChooser_fecha = new com.toedter.calendar.JDateChooser();
        jButton_eliminar = new javax.swing.JButton();
        jLabel_conceptpactual = new javax.swing.JLabel();
        jLabel_valoractual = new javax.swing.JLabel();
        jLabel_fechaactual = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTextField_total = new javax.swing.JTextField();
        jButton_calcular = new javax.swing.JButton();
        jButton2_utilidad = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Fecha", "Concepto", "Valor", "Registra"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(550);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(100);
        }

        jLabel_idpartida.setText("jLabel9");

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
                .addGap(39, 39, 39)
                .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informacion de la partida presupuestaria a agregar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jTextField_concepto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_conceptoKeyTyped(evt);
            }
        });

        jLabel6.setText("Concepto");

        jTextField_valor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_valorActionPerformed(evt);
            }
        });
        jTextField_valor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_valorKeyTyped(evt);
            }
        });

        jLabel4.setText("Valor");

        jButton1.setText("Agregar partida");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                .addComponent(jTextField_concepto, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_valor, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField_concepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_valor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Editar partida presupuestaria", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jButton_editar.setText("Editar");
        jButton_editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_editarActionPerformed(evt);
            }
        });

        jLabel8.setText("Concepto");

        jTextField_idpartida.setEnabled(false);

        jLabel7.setText("Id");

        jLabel3.setText("Valor");

        jTextField_valoramodificar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_valoramodificarKeyTyped(evt);
            }
        });

        jLabel5.setText("Fecha");

        jButton_eliminar.setText("Eliminar");
        jButton_eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_eliminarActionPerformed(evt);
            }
        });

        jLabel_conceptpactual.setText("jLabel10");

        jLabel_valoractual.setText("jLabel11");

        jLabel_fechaactual.setText("jLabel12");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_idpartida, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_partida)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_valoramodificar, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(10, 10, 10)
                        .addComponent(jDateChooser_fecha, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_editar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton_eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(79, 79, 79)
                        .addComponent(jLabel_conceptpactual)
                        .addGap(104, 104, 104)
                        .addComponent(jLabel_valoractual)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel_fechaactual)
                .addGap(266, 266, 266))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_idpartida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jTextField_partida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_valoramodificar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jDateChooser_fecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addGap(3, 3, 3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton_eliminar)
                                .addComponent(jButton_editar)
                                .addComponent(jLabel_conceptpactual)
                                .addComponent(jLabel_valoractual)))))
                .addComponent(jLabel_fechaactual))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Consultar total partidas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel9.setText("Valor total partidas");

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
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(jTextField_total, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jButton_calcular)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField_total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_calcular))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jButton2_utilidad.setText("Recalcular Utilidad");
        jButton2_utilidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2_utilidadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel_idpartida)
                .addGap(336, 336, 336))
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                        .addComponent(jButton2_utilidad, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(72, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel_idpartida)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2_utilidad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        String concepto = jTextField_concepto.getText().trim().toUpperCase();
        String valor = jTextField_valor.getText().trim();
        double valorDouble = Double.parseDouble(valor);
        //Validamos que todos los campos hayan sido completados
        if (!concepto.equals("") && !valor.equals("")) {
            //Capturamos el resto de datos

            int opcion = JOptionPane.showConfirmDialog(this, "¿Desea registrar una partida de " + MetodosGenerales.ConvertirIntAMoneda(valorDouble) + " al presupuesto " + idPresupuesto + " - "
                    + descripcion + "?");
            if (opcion == 0) {

                Object[] infoPresup = consultarPresupuestosCalculoUtilidad(Integer.parseInt(this.idPresupuesto), this.descripcion);
                String conceptoSistema = "UTILIDAD DEL PERIODO ANTERIOR " + infoPresup[0] + " " + infoPresup[1];

                if (!concepto.equalsIgnoreCase(conceptoSistema)) {
                    RegistrarPartida(idPresupuesto, concepto, valor, this.usuario);
                    limpiarTabla(modelo);
                    llenarTabla();
                    limpiarCampos();
                } else {
                    JOptionPane.showMessageDialog(this, "El nombre de la partida que intenta registrar esta reservada para el sistema", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }

        } else {
            JOptionPane.showMessageDialog(this, "Complete todos los campos", "Informacion", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        //jButton_editar.setEnabled(true);
        int fila = jTable1.getSelectedRow();
        if (fila != -1) {
            try {
                jTextField_idpartida.setText(jTable1.getValueAt(fila, 0).toString());
                jTextField_partida.setText(jTable1.getValueAt(fila, 2).toString());
                jLabel_idpartida.setText(String.valueOf(fila));

                char[] precio = jTable1.getValueAt(fila, 3).toString().toCharArray();

                if (precio[0] != '-') {
                    jTextField_valoramodificar.setText(MetodosGenerales.ConvertirMonedaAInt(jTable1.getValueAt(fila, 3).toString()));
                } else {
                    String precio2 = jTable1.getValueAt(fila, 3).toString().substring(2);
                    jTextField_valoramodificar.setText(MetodosGenerales.ConvertirMonedaAInt(precio2));
                }

                jDateChooser_fecha.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(jTable1.getValueAt(fila, 1).toString()));
                jLabel_conceptpactual.setText(jTable1.getValueAt(fila, 2).toString());
                jLabel_fechaactual.setText(jTable1.getValueAt(fila, 1).toString());
                jLabel_valoractual.setText(jTable1.getValueAt(fila, 3).toString());

                if (this.permiso.equalsIgnoreCase("Gerente")) {

                    if ((jTable1.getValueAt(fila, 2).toString().length() >= 13 && jTable1.getValueAt(fila, 2).toString().substring(0, 13).equals("(PROVISIONAL)"))
                            || (jTable1.getValueAt(fila, 2).toString().length() >= 20 && jTable1.getValueAt(fila, 2).toString().substring(0, 20).equals("UTILIDAD DEL PERIODO"))) {

                        jButton_editar.setEnabled(false);
                        jButton_eliminar.setEnabled(false);

                    } else {
                        jButton_editar.setEnabled(true);
                        jButton_eliminar.setEnabled(true);
                    }

                } else {
                    jButton_editar.setEnabled(false);
                    jButton_eliminar.setEnabled(false);
                }

//                if ((jTable1.getValueAt(fila, 2).toString().length() >= 12
//                        && ((jTable1.getValueAt(fila, 2).toString().substring(0, 13).equals("(PROVISIONAL)"))
//                        || jTable1.getValueAt(fila, 2).toString().substring(0, 20).equals("UTILIDAD DEL PERIODO")))
//                        || !this.permiso.equalsIgnoreCase("Gerente")) {
//                    jButton_editar.setEnabled(false);
//                    jButton_eliminar.setEnabled(false);
//                } else {
//                    jButton_editar.setEnabled(true);
//                    jButton_eliminar.setEnabled(true);
//                }

            } catch (StringIndexOutOfBoundsException e) {
                jTextField_partida.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al cargar los datos de la partida para posterior edicion. Contacte al administrador");
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una fila");
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton_editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_editarActionPerformed
        //Verificamos que todos los campos esten completos
        String idPartida = jTextField_idpartida.getText().trim();
        String concepto = jTextField_partida.getText().trim().toUpperCase();
        String valor = jTextField_valoramodificar.getText().trim();
        String presupuesto = jTextField_descripcion.getText().trim();
        String fechaActual = jLabel_fechaactual.getText().trim();
        String partidaActual = jLabel_conceptpactual.getText().trim();
        String montoActual = jLabel_valoractual.getText().trim();

        try {
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_fecha.getDate());
            if (!idPartida.equals("") && !concepto.equals("") && !valor.equals("") && !fecha.equals("")) {

                int opcion = JOptionPane.showConfirmDialog(this, "¿Desea editar la partida?", "Confirmacion", JOptionPane.INFORMATION_MESSAGE);
                if (opcion == 0) {
                    String razon = JOptionPane.showInputDialog(this, "Indique la razón por la que se va a eliminar la partida", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                    if (!razon.equals("")) {
                        ActualizarPartida(idPartida, fecha, this.idPresupuesto, concepto, valor, presupuesto, fechaActual, partidaActual, montoActual, razon);

                        limpiarCampos();
                        limpiarTabla(modelo);
                        llenarTabla();
                    } else {
                        JOptionPane.showMessageDialog(this, "Indique la razón por la que se va a eliminar la partida", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

            } else {
                JOptionPane.showMessageDialog(this, "Seleccione la partida a editar", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Fecha invalida", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar los datos de la partida", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton_editarActionPerformed

    private void jButton_calcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_calcularActionPerformed
        //Verificamos que la tabla tenga datos
        int numerofilas = jTable1.getRowCount();
        //Declaramos la variable que acumulara la suma
        int suma = 0;
        if (numerofilas > 0) {
            for (int i = 0; i < numerofilas; i++) {
                suma += Integer.parseInt(MetodosGenerales.ConvertirMonedaAInt(jTable1.getValueAt(i, 3).toString()));
                jTextField_total.setText(String.valueOf(suma));
            }
        } else {
            JOptionPane.showMessageDialog(this, "No hay datos");
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

    private void jTextField_valorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_valorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_valorActionPerformed

    private void jTextField_conceptoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_conceptoKeyTyped
        if (jTextField_concepto.getText().trim().length() == 250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_conceptoKeyTyped

    private void jTextField_valoramodificarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_valoramodificarKeyTyped

        char c = evt.getKeyChar();

        if (!Character.isDigit(c) && c != '.') {
            evt.consume();
        }

        if (c == '0' && jTextField_valoramodificar.getText().trim().length() == 0) {
            evt.consume();
        }

        if (c == '.' && jTextField_valoramodificar.getText().trim().length() == 0) {
            evt.consume();
        }

        int contador = 0;

        char[] cadena = jTextField_valoramodificar.getText().trim().toCharArray();
        for (int i = 0; i < jTextField_valoramodificar.getText().trim().length(); i++) {
            if (cadena[i] == '.') {
                contador++;
                break;
            }
        }

        if (contador > 0 && c == '.') {
            evt.consume();
        }

    }//GEN-LAST:event_jTextField_valoramodificarKeyTyped

    private void jButton_eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_eliminarActionPerformed

        //Verificamos que se haya seleccionado una partida
        String idPpartid = jTextField_idpartida.getText().trim();
        String presupuesto = jTextField_descripcion.getText().trim();
        String valor = jLabel_valoractual.getText().trim();
        if (!idPpartid.equals("")) {
            //Pedir confirmacion
            int opcion = JOptionPane.showConfirmDialog(this, "¿Desea eliminar la partida " + idPpartid + "?", "Confirmacion", JOptionPane.INFORMATION_MESSAGE);
            if (opcion == 0) {

                String razon = JOptionPane.showInputDialog(this, "Indique la razon por la que elimina esta partida", "Informacion", JOptionPane.INFORMATION_MESSAGE);

                if (!razon.equals("")) {
                    EliminarPartida(idPpartid, presupuesto, valor, razon);
                    limpiarCampos();
                    limpiarTabla(modelo);
                    llenarTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "Indique la razon por la que elimina esta partida", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }

            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una partida", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton_eliminarActionPerformed

    private void jButton2_utilidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2_utilidadActionPerformed

        int presupuesto = Integer.parseInt(jTextField_id.getText().trim());
        String nombrePresupuesto = jTextField_descripcion.getText().trim();

        boolean comprobacion = comprobarGastos(presupuesto);
        Object[] info = consultarPresupuestos(presupuesto, nombrePresupuesto);

        String idPartidaUtilidad = consultarIdPartidadUtilidad(presupuesto);
        String nombrePartidaUtilidad = consultarNombrePartidaUtilida(idPartidaUtilidad);

        if (comprobacion) {

            String concepto = "UTILIDAD DEL PERIODO ANTERIOR " + info[0] + " " + info[1];
            RegistrarPartidaActualizada(idPartidaUtilidad, concepto, String.valueOf((Double) info[5]), this.usuario);
            limpiarCampos();
            limpiarTabla(modelo);
            llenarTabla();

        } else {

            JOptionPane.showMessageDialog(this, "No es posible recalcular la utilidad ya que el presupuesto anterior " + info[0] + " " + info[1] + ""
                    + " tiene gastos no autorizados.");
        }

    }//GEN-LAST:event_jButton2_utilidadActionPerformed

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
            java.util.logging.Logger.getLogger(AgregarDineroPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AgregarDineroPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AgregarDineroPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AgregarDineroPresupuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AgregarDineroPresupuesto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2_utilidad;
    private javax.swing.JButton jButton_calcular;
    private javax.swing.JButton jButton_editar;
    private javax.swing.JButton jButton_eliminar;
    private com.toedter.calendar.JDateChooser jDateChooser_fecha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_conceptpactual;
    private javax.swing.JLabel jLabel_fechaactual;
    private javax.swing.JLabel jLabel_idpartida;
    private javax.swing.JLabel jLabel_valoractual;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField_concepto;
    private javax.swing.JTextField jTextField_descripcion;
    private javax.swing.JTextField jTextField_id;
    private javax.swing.JTextField jTextField_idpartida;
    private javax.swing.JTextField jTextField_partida;
    private javax.swing.JTextField jTextField_total;
    private javax.swing.JTextField jTextField_valor;
    private javax.swing.JTextField jTextField_valoramodificar;
    // End of variables declaration//GEN-END:variables
}

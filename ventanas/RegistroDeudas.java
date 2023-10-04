/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import java.awt.Frame;
import static java.awt.Frame.getFrames;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import static ventanas.RegistrarGastosPresupuesto.ConvertirIntAMoneda;

/**
 *
 * @author Erwin P
 */
public class RegistroDeudas extends javax.swing.JFrame {

    String usuario, permiso;
    DefaultTableModel modelo, modelo1;

    /**
     * Creates new form RegistroDeudas
     */
    public RegistroDeudas() {
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();
        calcularTotalDeuda();
    }

    public RegistroDeudas(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        ConfiguracionGralJFrame();
        IniciarCaracteristicasGenerales();

        if (!permiso.equalsIgnoreCase("Gerente")) {
            jButton_eliminarDeuda.setEnabled(false);
        }
        calcularTotalDeuda();
    }

    public void IniciarCaracteristicasGenerales() {
        llenarTablaPartidas();
        llenarTablaGastosDeudas();
        jLabel_idPresupuestoDeudaPartida.setVisible(false);
        jLabel_idPresupuestoGastoDeuda.setVisible(false);

    }

    public void calcularTotalDeuda(){
        
        jTextField_totalDeuda.setText("");
        
        //Verificamos que la tabla tenga datos
        int numerofilas = jTable_partidas.getRowCount();
        //Declaramos la variable que acumulara la suma
        double suma = 0;
        
        if (numerofilas > 0) {
            for (int i = 0; i < numerofilas; i++) {
                suma += Math.abs(Integer.parseInt(MetodosGenerales.ConvertirMonedaAInt(jTable_partidas.getValueAt(i, 4).toString())));
            }
        } 
        
        int numerofilas2 = jTable_gastosDeudas.getRowCount();
        if (numerofilas2 > 0) {
            for (int i = 0; i < numerofilas2; i++) {
                suma += Math.abs(Integer.parseInt(MetodosGenerales.ConvertirMonedaAInt(jTable_gastosDeudas.getValueAt(i, 4).toString())));
            }
        }
        
        jTextField_totalDeuda.setText(MetodosGenerales.ConvertirIntAMoneda(suma));
        
    }

    public void llenarTablaGastosDeudas() {

        modelo1 = (DefaultTableModel) jTable_gastosDeudas.getModel();

        String consulta = "select d.iddeuda, d.fecha, d.idPresupuesto, d.descripcion, ifnull(d.valor -SUM(g.valor), d.valor) as saldo\n"
                + "from deudas d left join gastospresupuestos g on d.iddeuda=g.idDeuda\n"
                + "group by d.iddeuda\n"
                + "having saldo > 0\n"
                + "order by d.iddeuda asc";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            //pst.setString(1, idPresupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[5];
                nuevo[0] = rs.getString("d.iddeuda");
                nuevo[1] = rs.getString("d.fecha");
                nuevo[2] = rs.getString("d.idPresupuesto");
                nuevo[3] = rs.getString("d.descripcion");
                nuevo[4] = ConvertirIntAMoneda(Double.parseDouble(rs.getString("saldo")));

                modelo1.addRow(nuevo);
            }

            jTable_gastosDeudas.setModel(modelo1);

            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo1);
            jTable_gastosDeudas.setRowSorter(ordenador);

            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer las deudas de las partidas");
            e.printStackTrace();
        }

    }

    public void llenarTablaPartidas() {

        modelo = (DefaultTableModel) jTable_partidas.getModel();

        String consulta = "select p.id, p.fecha, p.idPresupuesto, p.concepto, ifnull(p.valor -SUM(g.valor), p.valor) as saldo\n"
                + "from partidaspresupuestos p left join gastospresupuestos g on p.id=g.idPartida\n"
                + "where p.tipo='Prestamo'\n"
                + "group by p.id\n"
                + "having saldo >0\n"
                + "order by p.id asc";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            //pst.setString(1, idPresupuesto);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[5];
                nuevo[0] = rs.getString("p.id");
                nuevo[1] = rs.getString("p.fecha");
                nuevo[2] = rs.getString("p.idPresupuesto");
                nuevo[3] = rs.getString("p.concepto");
                nuevo[4] = ConvertirIntAMoneda(Double.parseDouble(rs.getString("saldo")));

                modelo.addRow(nuevo);
            }

            jTable_partidas.setModel(modelo);

            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable_partidas.setRowSorter(ordenador);

            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer las deudas de las partidas");
            e.printStackTrace();
        }
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Deudas *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void limpiarCampos() {
        jTextField_descripcionPrestamo.setText("");
        jTextField_idDeudaPartida.setText("");
        jTextField_valorPartida.setText("");
        jLabel_idPresupuestoDeudaPartida.setText("");
        jTextField_valoraPagar.setText("");
        jTextField_comprobantedePago.setText("");
        jTextField_idGastoDeuda.setText("");
        jTextField_descripcionGastoDeuda.setText("");
        jTextField_valorGastoDeuda.setText("");
        jTextField_comprobantePagoGastoDeuda.setText("");
        jTextField_valoraPagarGastoDeuda.setText("");
    }

    public void limpiarTablaPartidas(DefaultTableModel modelo) {
        for (int i = 0; i < jTable_partidas.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void limpiarTablaGastosDeudas(DefaultTableModel modelo1) {
        for (int i = 0; i < jTable_gastosDeudas.getRowCount(); i++) {
            modelo1.removeRow(i);
            i = i - 1;
        }
    }

    public void CancelarDeudayRegistrarGasto(String idPartida, String concepto) {

        try {
            //Transaccion para asegurar que se ejecuten las dos consultas o no se ejecute ninguna
            Connection cn = Conexion.Conectar();
            cn.setAutoCommit(false);

            concepto = concepto.substring(15, concepto.length()) + "(DEUDA CANCELADA)";

            cn.commit();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "La deuda no ha sido cancelada. Intente nuevamente o contacte al "
                    + "administrador", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public boolean VerificarConcepto(int idConcepto, String idPresupuesto) {
        String consulta = "select id from itemspresupuesto where idPresupuesto=? and idGasto=?";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            pst.setInt(2, idConcepto);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar el concepto PRESTAMOS, contacte al administrador", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public String consultarPresupuesto() {
        String consulta = "select max(idPresupuesto) as presup from presupuestos";

        Connection cn = Conexion.Conectar();
        try {

            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("presup");
            }

            cn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el presupuesto en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
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

    public String ConsultarIdConcepto(String idGastoDeuda) {

        String consulta = "select idConcepto from gastospresupuestos where id=?";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idGastoDeuda);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("idConcepto");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el idConcepto", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public String ConsultarNombrePresup(String presup) {

        String consulta = "select descripcion from presupuestos where idPresupuesto=?";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, presup);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("descripcion");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el nombre del presupuesto, contacte al administrador", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return null;
    }

    public void RegistrarPagoPrestamoPartida(String ultimoPresup, int idConcepto, String idPartida, double valor,
            String descripcion,
            String estado, String comprobante, String usuario) {

        try {
            //Registramos el gasto
            String consulta = "insert into gastospresupuestos (fechaGasto, idPrespuesto, idConcepto, idPartida, valor, "
                    + "observaciones, "
                    + "estado, factura, registradoPor) \n"
                    + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Connection cn = Conexion.Conectar();

            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fecha);
            pst.setString(2, ultimoPresup);
            pst.setInt(3, idConcepto);
            pst.setString(4, idPartida);
            pst.setDouble(5, valor);
            pst.setString(6, descripcion);
            pst.setString(7, estado);
            pst.setString(8, comprobante);
            pst.setString(9, usuario);

            pst.executeUpdate();

            cn.close();
            JOptionPane.showMessageDialog(this, "Gasto registrado", "Informacion", JOptionPane.INFORMATION_MESSAGE);

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

    public void RegistrarGastoPagoParcial(String idPresupuesto, String fecha, int idconcepto, String factura,
            double ValorAIngresar, String observaciones, String estado,
            String registradoPor, String idPartida, String conceptoPartida, double saldoPartida) {

        try {
            //Registramos el gasto
            String consulta = "insert into gastospresupuestos (idPrespuesto, fechaGasto, idConcepto, factura, valor, observaciones, estado, registradoPor) values "
                    + "(?, ?, ?, ?, ?, ?, ?, ?)";
            Connection cn = Conexion.Conectar();
            cn.setAutoCommit(false);

            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idPresupuesto);
            pst.setString(2, fecha);
            pst.setInt(3, idconcepto);
            pst.setString(4, factura);
            pst.setDouble(5, ValorAIngresar);
            pst.setString(6, observaciones);
            pst.setString(7, estado);
            pst.setString(8, registradoPor);
//            pst.setDouble(9, saldo);

            pst.executeUpdate();

            //Registramos cuanto de saldo le queda al prestamos
            String consulta2 = "update partidaspresupuestos set concepto=?, saldo=? where id=?";
            //Redefinimos el concepto de la partida ya que deja de ser una deuda
            //String conceptoPartidaModificado = conceptoPartida.substring(15, conceptoPartida.length());

            PreparedStatement pst2 = cn.prepareStatement(consulta2);
            pst2.setString(1, conceptoPartida);
            pst2.setDouble(2, saldoPartida);
            pst2.setString(3, idPartida);

            pst2.executeUpdate();

            cn.commit();
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

    public void RegistrarPagoGastoDeuda(String idGastoDeuda, String observaciones) {

        try {
            //Registramos el gasto
            String consulta = "update gastospresupuestos set saldo=0, observaciones=? where id=?";
            Connection cn = Conexion.Conectar();

            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, observaciones);
            pst.setString(2, idGastoDeuda);
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

    public String ConsultarPresupGastoDeuda(String presupuestoGastoDeuda) {

        String consulta = "select descripcion from presupuestos where idPresupuesto=?";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, presupuestoGastoDeuda);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("descripcion");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar el nombre del presupuesto, contacte al administrador", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public void EliminarDeuda(String idDeuda) {

        String consulta = "delete from deudas where iddeuda=?";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idDeuda);
            pst.executeQuery();
            JOptionPane.showMessageDialog(this, "Deuda eliminada", "Informacion", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar la deuda EliminarDeuda()", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public String[] ConsultarInformacionDeuda(String idGastoDeuda) {

        String consulta = "select idConcepto, valor, descripcion, factura, registradoPor from deudas where iddeuda=?";
        String[] datos = new String[5];
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idGastoDeuda);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                datos[0] = rs.getString("idConcepto");
                datos[1] = rs.getString("valor");
                datos[2] = rs.getString("descripcion");
                datos[3] = rs.getString("factura");
                datos[4] = rs.getString("registradoPor");

                return datos;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los datos de la deuda ConsultarInformacionDeuda()", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public void RegistrarPagoGastoDeuda(String fecha, String ultimoPresup, String idConcepto, String idGastoDeuda,
            double valoraPagarGastoDeuda, String descripcion, String estado, String comprobantePagoGastoDeuda, String usuario) {

        String consulta = "INSERT INTO gastospresupuestos (fechaGasto, idPrespuesto, idConcepto, idDeuda, valor, "
                + "observaciones, estado, factura, registradoPor) \n"
                + "values (?,?,?,?,?,?,?,?,?);";

        try {

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            pst.setString(1, fecha);
            pst.setString(2, ultimoPresup);
            pst.setString(3, idConcepto);
            pst.setString(4, idGastoDeuda);
            pst.setDouble(5, valoraPagarGastoDeuda);
            pst.setString(6, descripcion);
            pst.setString(7, estado);
            pst.setString(8, comprobantePagoGastoDeuda);
            pst.setString(9, usuario);

            pst.execute();
            JOptionPane.showMessageDialog(this, "Gasto registrado", "Informacion", JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar el abono de la deuda RegistrarPagoGastoDeuda()", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public boolean VerificarSiHayGasto(String idGasto) {

        String consulta = "SELECT valor from gastospresupuestos where idDeuda=?";

        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idGasto);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return false;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los abonos de la deuda VerificarSiHayGasto()", "Error", JOptionPane.ERROR_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_partidas = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField_idDeudaPartida = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField_descripcionPrestamo = new javax.swing.JTextField();
        jTextField_valorPartida = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_comprobantedePago = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField_valoraPagar = new javax.swing.JTextField();
        jButton_verAbonosPrestamos = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable_gastosDeudas = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jTextField_idGastoDeuda = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField_descripcionGastoDeuda = new javax.swing.JTextField();
        jTextField_valorGastoDeuda = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField_valoraPagarGastoDeuda = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField_comprobantePagoGastoDeuda = new javax.swing.JTextField();
        jButton_pagarGastoDeuda = new javax.swing.JButton();
        jLabel_idPresupuestoGastoDeuda = new javax.swing.JLabel();
        jButton_eliminarDeuda = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel_idPresupuestoDeudaPartida = new javax.swing.JLabel();
        jTextField_totalDeuda = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()), "", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP), "Prestamos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jTable_partidas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Fecha", "Presupuesto", "Descripcion", "Valor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_partidas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_partidasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable_partidas);
        if (jTable_partidas.getColumnModel().getColumnCount() > 0) {
            jTable_partidas.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable_partidas.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable_partidas.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable_partidas.getColumnModel().getColumn(3).setPreferredWidth(450);
            jTable_partidas.getColumnModel().getColumn(4).setPreferredWidth(200);
        }

        jButton1.setText("Pagar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Id Partida/Prestamos");

        jTextField_idDeudaPartida.setEnabled(false);

        jLabel2.setText("Descripcion");

        jTextField_descripcionPrestamo.setEnabled(false);

        jTextField_valorPartida.setEnabled(false);

        jLabel3.setText("Comprobante de pago");

        jLabel4.setText("Valor a pagar");

        jTextField_valoraPagar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_valoraPagarKeyTyped(evt);
            }
        });

        jButton_verAbonosPrestamos.setText("Ver abonos");
        jButton_verAbonosPrestamos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_verAbonosPrestamosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_idDeudaPartida, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_descripcionPrestamo, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_valorPartida, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(16, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_valoraPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_comprobantedePago, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_verAbonosPrestamos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(122, 122, 122))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField_idDeudaPartida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_descripcionPrestamo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_valorPartida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_comprobantedePago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_valoraPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_verAbonosPrestamos))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Gastos adeudados", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP));

        jTable_gastosDeudas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Fecha", "Presupuesto", "Descripcion", "Valor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable_gastosDeudas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable_gastosDeudasMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable_gastosDeudas);
        if (jTable_gastosDeudas.getColumnModel().getColumnCount() > 0) {
            jTable_gastosDeudas.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable_gastosDeudas.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable_gastosDeudas.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable_gastosDeudas.getColumnModel().getColumn(3).setPreferredWidth(450);
            jTable_gastosDeudas.getColumnModel().getColumn(4).setPreferredWidth(200);
        }

        jLabel5.setText("Id deuda");

        jTextField_idGastoDeuda.setEnabled(false);

        jLabel6.setText("Descripcion");

        jTextField_descripcionGastoDeuda.setEnabled(false);

        jTextField_valorGastoDeuda.setEnabled(false);

        jLabel8.setText("Valor a pagar");

        jTextField_valoraPagarGastoDeuda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_valoraPagarGastoDeudaKeyTyped(evt);
            }
        });

        jLabel9.setText("Comprobante de pago");

        jButton_pagarGastoDeuda.setText("Pagar");
        jButton_pagarGastoDeuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_pagarGastoDeudaActionPerformed(evt);
            }
        });

        jLabel_idPresupuestoGastoDeuda.setText("jLabel7");

        jButton_eliminarDeuda.setText("Eliminar");
        jButton_eliminarDeuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_eliminarDeudaActionPerformed(evt);
            }
        });

        jButton2.setText("Ver abonos");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField_idGastoDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_descripcionGastoDeuda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_valorGastoDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_valoraPagarGastoDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel9)
                                .addGap(12, 12, 12)
                                .addComponent(jTextField_comprobantePagoGastoDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton_pagarGastoDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton_eliminarDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 754, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(362, 362, 362)
                .addComponent(jLabel_idPresupuestoGastoDeuda)
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField_idGastoDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField_descripcionGastoDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_valorGastoDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField_valoraPagarGastoDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField_comprobantePagoGastoDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_pagarGastoDeuda)
                    .addComponent(jButton_eliminarDeuda)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel_idPresupuestoGastoDeuda))
        );

        jLabel_idPresupuestoDeudaPartida.setText("jLabel5");

        jTextField_totalDeuda.setEnabled(false);

        jLabel7.setText("Total deuda");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel_idPresupuestoDeudaPartida)
                .addGap(200, 200, 200))
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField_totalDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel_idPresupuestoDeudaPartida)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_totalDeuda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable_partidasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_partidasMouseClicked
        // TODO add your handling code here:

        int fila = jTable_partidas.getSelectedRow();
        if (fila != -1) {
            jTextField_idDeudaPartida.setText(jTable_partidas.getValueAt(fila, 0).toString());
            jTextField_descripcionPrestamo.setText(jTable_partidas.getValueAt(fila, 3).toString());
            jTextField_valorPartida.setText(jTable_partidas.getValueAt(fila, 4).toString());
            jLabel_idPresupuestoDeudaPartida.setText(jTable_partidas.getValueAt(fila, 2).toString());
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un concepto");
        }

    }//GEN-LAST:event_jTable_partidasMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        //Verificamos que se haya seleccionado una partida
        String idPartida = jTextField_idDeudaPartida.getText().trim();
        String descripcion = jTextField_descripcionPrestamo.getText().trim();
        String verificacionvaloraPagar = jTextField_valoraPagar.getText().trim();
        String comprobante = jTextField_comprobantedePago.getText().trim();
        String idPresupuesto = jLabel_idPresupuestoDeudaPartida.getText().trim();
        String ultimoPresup = consultarPresupuesto();
        //String idPresupuesto = 

        if (!descripcion.equals("") && !verificacionvaloraPagar.equals("") && !comprobante.equals("")) {

            //Verificamos que haya concepto de presupuesto para poder cargar en el el gasto            
            if (VerificarConcepto(71, ultimoPresup)) {

                //Verificamos si el valor excede el presupuesto
                double valoraPagar = Double.parseDouble(jTextField_valoraPagar.getText().trim());
                double valorDeuda = Math.abs(Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jTextField_valorPartida.getText().trim())));
//                System.out.println("Valor a pagar " + valoraPagar);
//                System.out.println("Valor deuda " + valorDeuda);

                //Todos los pagos de deuda requieren autorizacion
                String estado = "Por Autorizar";

                //Definimos la descripcionGastoDeuda con la que se registra el gasto, ya no debe decir adeudado
                descripcion = "PAGO " + descripcion;

                //Verificamos si esta haciendo un pago parcial o total
                if (valoraPagar <= valorDeuda) {

                    int opcion = JOptionPane.showConfirmDialog(this, "¿Desea cancelar la deuda?\n\n***Se registrará como un gasto en el ultimo presupuesto***"
                            + "\n\n*** El gasto se registrará como: " + estado);

                    if (opcion == 0) {

                        RegistrarPagoPrestamoPartida(ultimoPresup, 71, idPartida, valoraPagar, descripcion, estado, comprobante, this.usuario);
                        limpiarTablaPartidas(modelo);
                        llenarTablaPartidas();
                        limpiarCampos();
                        calcularTotalDeuda();

                    } else {
                        JOptionPane.showMessageDialog(this, "El pago no ha sido registrado", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "No es posible cancelar una deuda por un valor mayor al adeudado", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Dentro del presupuesto actual no fue cargado el concepto ***PAGOS PRESTAMOS***\n"
                        + "Carguelo para poder cancelar el prestamo", usuario, HEIGHT);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar uno de los prestamos"
                    + "\n\n*** Los campos valor a pagar y compronate de pago son obligatorios ***", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField_valoraPagarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_valoraPagarKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();

        if (!Character.isDigit(c) && c != '.') {
            evt.consume();
        }

        if (c == '0' && jTextField_valoraPagar.getText().trim().length() == 0) {
            evt.consume();
        }

        if (c == '.' && jTextField_valoraPagar.getText().trim().length() == 0) {
            evt.consume();
        }

        int contador = 0;

        char[] cadena = jTextField_valoraPagar.getText().trim().toCharArray();
        for (int i = 0; i < jTextField_valoraPagar.getText().trim().length(); i++) {
            if (cadena[i] == '.') {
                contador++;
                break;
            }
        }

        if (contador > 0 && c == '.') {
            evt.consume();
        }


    }//GEN-LAST:event_jTextField_valoraPagarKeyTyped

    private void jTable_gastosDeudasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_gastosDeudasMouseClicked
        // TODO add your handling code here:        
        int fila = jTable_gastosDeudas.getSelectedRow();
        if (fila != -1) {
            jTextField_idGastoDeuda.setText(jTable_gastosDeudas.getValueAt(fila, 0).toString());
            jTextField_descripcionGastoDeuda.setText(jTable_gastosDeudas.getValueAt(fila, 3).toString());
            jTextField_valorGastoDeuda.setText(jTable_gastosDeudas.getValueAt(fila, 4).toString());
            jLabel_idPresupuestoGastoDeuda.setText(jTable_gastosDeudas.getValueAt(fila, 2).toString());
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un concepto");
        }
    }//GEN-LAST:event_jTable_gastosDeudasMouseClicked

    private void jButton_pagarGastoDeudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_pagarGastoDeudaActionPerformed
        //*******GASTO DEUDA*******
        //Verificamos que se haya seleccionado una partida
        String idGastoDeuda = jTextField_idGastoDeuda.getText().trim();
        String descripcionGastoDeuda = jTextField_descripcionGastoDeuda.getText().trim();
        String valorString = jTextField_valoraPagarGastoDeuda.getText().trim();
        String comprobantePagoGastoDeuda = jTextField_comprobantePagoGastoDeuda.getText().trim();
        //String presupuestoGastoDeuda = jLabel_idPresupuestoDeudaPartida.getText().trim();

        if (!descripcionGastoDeuda.equals("") && !valorString.equals("") && !comprobantePagoGastoDeuda.equals("")) {

            //Consultamos la informacion de la deuda idoncepto, valor, descripcion
            String[] infoDeuda = ConsultarInformacionDeuda(idGastoDeuda);
            //Verificamos que el ultimo presupuesto a donde va a quedar cargado el pago tenga el idConcepto respectivo
            String ultimoPresup = consultarPresupuesto();

            if (VerificarConcepto(Integer.parseInt(infoDeuda[0]), ultimoPresup)) {

                Double valorGastoDeuda = Math.abs(Double.parseDouble(MetodosGenerales.ConvertirMonedaAInt(jTextField_valorGastoDeuda.getText().trim())));
                Double valoraPagarGastoDeuda = Double.parseDouble(jTextField_valoraPagarGastoDeuda.getText().trim());

                //Consultamos datos generales
                //String nombreUltimoPresup = ConsultarNombrePresup(ultimoPresup);
                //String nombrePresupuestoGastoDeuda = ConsultarPresupGastoDeuda(presupuestoGastoDeuda);
                String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                //Todos los pagos de deuda requieren autorizacion
                String estado = "Por Autorizar";
                //Consultamos el idConcepto del gasto
                //String idConcepto = ConsultarIdConcepto(idGastoDeuda);

                //Verificamos si esta haciendo un pago parcial o total
                if ((double) valoraPagarGastoDeuda <= (double) valorGastoDeuda) {

                    //Redefinimos el nombre de la deuda
                    infoDeuda[2] = "PAGO " + infoDeuda[2];
                    int opcion = JOptionPane.showConfirmDialog(this, "¿Desea cancelar la deuda?\n\n***Se registrará como un gasto en el ultimo presupuesto***"
                            + "\n\n*** El gasto se registrará como: " + estado);

                    if (opcion == 0) {

                        RegistrarPagoGastoDeuda(fecha, ultimoPresup, infoDeuda[0], idGastoDeuda, valoraPagarGastoDeuda,
                                infoDeuda[2], estado, comprobantePagoGastoDeuda, this.usuario);
                        limpiarTablaGastosDeudas(modelo1);
                        llenarTablaGastosDeudas();
                        limpiarCampos();
                        calcularTotalDeuda();

                    } else {
                        JOptionPane.showMessageDialog(this, "El pago no ha sido registrado", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No es posible cancelar una deuda por un valor mayor al adeudado", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Dentro del presupuesto actual no fue cargado el concepto ***PAGOS PRESTAMOS***\n"
                        + "Carguelo para poder cancelar el prestamo", usuario, HEIGHT);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar uno de los valores adeudados"
                    + "\n\n*** Los campos valor a pagar y compronate de pago son obligatorios ***", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton_pagarGastoDeudaActionPerformed

    private void jButton_eliminarDeudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_eliminarDeudaActionPerformed

        //Verificamos que este seleccionado un gasto
        String idDeuda = jTextField_idGastoDeuda.getText().trim();
        if (!idDeuda.equals("")) {
            //Verificamos que ese gasto no tenga abonos registrados
            if (VerificarSiHayGasto(idDeuda)) {

                EliminarDeuda(idDeuda);
                limpiarCampos();
                limpiarTablaGastosDeudas(modelo1);
                llenarTablaGastosDeudas();

            } else {
                JOptionPane.showMessageDialog(this, "No es posible eliminar la deuda ya que tiene abonos registrados", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un gasto", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton_eliminarDeudaActionPerformed

    private void jTextField_valoraPagarGastoDeudaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_valoraPagarGastoDeudaKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar();

        if (!Character.isDigit(c) && c != '.') {
            evt.consume();
        }

        if (c == '0' && jTextField_valoraPagarGastoDeuda.getText().trim().length() == 0) {
            evt.consume();
        }

        if (c == '.' && jTextField_valoraPagarGastoDeuda.getText().trim().length() == 0) {
            evt.consume();
        }

        int contador = 0;

        char[] cadena = jTextField_valoraPagarGastoDeuda.getText().trim().toCharArray();
        for (int i = 0; i < jTextField_valoraPagarGastoDeuda.getText().trim().length(); i++) {
            if (cadena[i] == '.') {
                contador++;
                break;
            }
        }

        if (contador > 0 && c == '.') {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_valoraPagarGastoDeudaKeyTyped

    private void jButton_verAbonosPrestamosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_verAbonosPrestamosActionPerformed
        // TODO add your handling code here:
        Frame[] ventanas = getFrames();

        for (Frame ventana : ventanas) {
            if (ventana instanceof AbonosDeudas) {
                ventana.dispose();
            }
        }

        String idDeuda = jTextField_idDeudaPartida.getText().trim();

        if (!idDeuda.equals("")) {
            new AbonosDeudas(idDeuda, usuario, permiso).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una partida", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_jButton_verAbonosPrestamosActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        Frame[] ventanas = getFrames();

        for (Frame ventana : ventanas) {
            if (ventana instanceof AbonosDeudas) {
                ventana.dispose();
            }
        }

        String id = jTextField_idGastoDeuda.getText().trim();

        if (!id.equals("")) {

            int idDeuda = Integer.parseInt(jTextField_idGastoDeuda.getText().trim());
            new AbonosDeudas(idDeuda, usuario, permiso).setVisible(true);

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una deuda", "Informacion", JOptionPane.INFORMATION_MESSAGE);
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
            java.util.logging.Logger.getLogger(RegistroDeudas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroDeudas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroDeudas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroDeudas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistroDeudas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton_eliminarDeuda;
    private javax.swing.JButton jButton_pagarGastoDeuda;
    private javax.swing.JButton jButton_verAbonosPrestamos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_idPresupuestoDeudaPartida;
    private javax.swing.JLabel jLabel_idPresupuestoGastoDeuda;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable_gastosDeudas;
    private javax.swing.JTable jTable_partidas;
    private javax.swing.JTextField jTextField_comprobantePagoGastoDeuda;
    private javax.swing.JTextField jTextField_comprobantedePago;
    private javax.swing.JTextField jTextField_descripcionGastoDeuda;
    private javax.swing.JTextField jTextField_descripcionPrestamo;
    private javax.swing.JTextField jTextField_idDeudaPartida;
    private javax.swing.JTextField jTextField_idGastoDeuda;
    private javax.swing.JTextField jTextField_totalDeuda;
    private javax.swing.JTextField jTextField_valorGastoDeuda;
    private javax.swing.JTextField jTextField_valorPartida;
    private javax.swing.JTextField jTextField_valoraPagar;
    private javax.swing.JTextField jTextField_valoraPagarGastoDeuda;
    // End of variables declaration//GEN-END:variables
}

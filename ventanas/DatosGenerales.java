/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 *
 * @author erwin
 */
public class DatosGenerales extends javax.swing.JFrame {

    String usuario, permiso;

    /**
     * Creates new form DatosGenerales
     */
    public DatosGenerales() {
        initComponents();
        llenarTodosComboBoxes();

        ConfiguracionGralJFrame();

    }

    public DatosGenerales(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        llenarTodosComboBoxes();

        ConfiguracionGralJFrame();

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Datos Generales *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void llenarTodosComboBoxes() {
        LlenarComboBoxPapeles();
        LlenarComboBoxVendedores();
        LlenarComboBoxtipoTrabajo();
        llenarComboBoxTipoGastos();
        llenarComboBoxTipoRubro();
        llenarComboBoxTipoModalidad();
        llenarComboBoxConceptos();
        llenarComboBoxMunicipios();
    }

    public void LlenarComboBoxPapeles() {

        ArrayList<String> listaPapeles = new ArrayList<>();

        try {

            String consultaPapel = "select nombrePapel from papeles order by nombrePapel asc";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaPapel);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String papel = rs.getString("nombrePapel");
                listaPapeles.add(papel);
            }

            for (String papel : listaPapeles) {
                jComboBox_papeles.addItem(papel.toUpperCase());
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el listado de papeles DatosGenerales LlenarComboBoxPapeles()","Error",JOptionPane.ERROR_MESSAGE);
        }

    }

    public void LlenarComboBoxVendedores() {

        ArrayList<String> listaVendedores = new ArrayList<>();

        try {

            String consultaComisionistas = "select nombreComisionista from comisionistas order by nombreComisionista asc";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaComisionistas);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String comisionista = rs.getString("nombreComisionista");
                listaVendedores.add(comisionista);
            }

            for (String comisionista : listaVendedores) {
                jComboBox_comisionistas.addItem(comisionista.toUpperCase());
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el listado de comisionistas DatosGenerales LlenarComboBoxVendedores()","Error",JOptionPane.ERROR_MESSAGE);
        }

    }

    public void LlenarComboBoxtipoTrabajo() {

        ArrayList<String> listaTipoTrabajo = new ArrayList<>();

        try {

            String consultaTipoTrabajo = "select tipo from tipotrabajo order by tipo asc";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaTipoTrabajo);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String tipoTrabajo = rs.getString("tipo");
                listaTipoTrabajo.add(tipoTrabajo);
            }

            for (String tipo : listaTipoTrabajo) {
                jComboBox_tipoTrabajo.addItem(tipo.toUpperCase());
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el listado de comisionistas DatosGenerales LlenarComboBoxVendedores()","Error",JOptionPane.ERROR_MESSAGE);
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
                jComboBox_tipogasto.addItem(tipoTrabajo.toUpperCase());
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el listado de tipo de gastos DatosGenerales llenarComboBoxTipoGastos()","Error",JOptionPane.ERROR_MESSAGE);
        }

    }

    public void llenarComboBoxTipoRubro() {

        try {

            String consultaTipoRubro = "select Descripcion from rubros";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaTipoRubro);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String tipoRubro = rs.getString("Descripcion");
                jComboBox_rubro.addItem(tipoRubro.toUpperCase());
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el listado de tipos de rubro DatosGenerales llenarComboBoxTipoRubro()","Error",JOptionPane.ERROR_MESSAGE);
        }

    }

    public void llenarComboBoxTipoModalidad() {

        try {

            String consultaTipoModalidad = "select Descripcion from modalidadgasto";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaTipoModalidad);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String tipoModalidad = rs.getString("Descripcion");
                jComboBox_modalidad.addItem(tipoModalidad.toUpperCase());
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el listado de tipos de modalidad DatosGenerales llenarComboBoxTipoModalidad()","Error",JOptionPane.ERROR_MESSAGE);
        }

    }

    public void llenarComboBoxMunicipios() {
        String consulta = "select municipio from municipios";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String municipio = rs.getString("municipio");
                jComboBox_municipio.addItem(municipio.toUpperCase());
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los municipios\n" + e);
        }

    }

    public void llenarComboBoxConceptos() {

        try {

            String consultaConceptos = "select descripcion from maestrogastos";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaConceptos);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String conceptos = rs.getString("descripcion");
                jComboBox_conceptos.addItem(conceptos.toUpperCase());
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el listado de conceptos DatosGenerales llenarComboBoxConceptos()","Error",JOptionPane.ERROR_MESSAGE);
        }

    }

    //Verifica que el papel que se vaya a ingresar no esté ya en la base de datos
    public boolean verificarPapel(String papelAIngresar) {

        boolean existe = false;

        ArrayList<String> listaPapeles = new ArrayList<String>();

        try {

            String consultaPapel = "select nombrePapel from papeles order by nombrePapel asc";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaPapel);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String papel = rs.getString("nombrePapel");
                listaPapeles.add(papel);
            }

            for (String papel : listaPapeles) {
                if (papel.equalsIgnoreCase(papelAIngresar)) {
                    existe = true;
                    break;
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el listado de papeles DatosGenerales VerificarPapel()","Error",JOptionPane.ERROR_MESSAGE);
        }

        return existe;
    }

    //Verifica que el comisionista que se vaya a ingresar no esté ya en la base de datos
    public boolean verificarComisionita(String vendedorAIngresar) {

        boolean existe = false;

        ArrayList<String> listaVendedores = new ArrayList<>();

        try {

            String consultaVendedor = "select nombreComisionista from comisionistas order by nombreComisionista asc";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaVendedor);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String vendedor = rs.getString("nombreComisionista");
                listaVendedores.add(vendedor);
            }

            for (String vendedor : listaVendedores) {
                if (vendedor.equalsIgnoreCase(vendedorAIngresar)) {
                    existe = true;
                    break;
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el listado de vendedores DatosGenerales VerificarComisionista()","Error",JOptionPane.ERROR_MESSAGE);
        }

        return existe;
    }

    //Verifica que el tipo de trabajo que se vaya a ingresar no esté ya en la base de datos
    public boolean verificarTipoTrabajo(String tipoTrabajoAIngresar) {

        boolean existe = false;

        ArrayList<String> listaTipotrabajos = new ArrayList<String>();

        try {

            String consultaTipoTrabajo = "select tipo from tipotrabajo order by tipo asc";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consultaTipoTrabajo);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String tipoTrabajo = rs.getString("tipo");
                listaTipotrabajos.add(tipoTrabajo);
            }

            for (String tipoTrabajo : listaTipotrabajos) {
                if (tipoTrabajo.equalsIgnoreCase(tipoTrabajoAIngresar)) {
                    existe = true;
                    break;
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer el listado de tipos de trabajo DatosGenerales VerificarTipoTrabajo()","Error",JOptionPane.ERROR_MESSAGE);
        }

        return existe;
    }

    //Verificamos que el concepto no exista ya en la base de datos
    public boolean verificarConcepto(String nuevoConcepto) {

        boolean verificacion = false;
        ArrayList<String> listaConcepto = new ArrayList<>();
        for (int i = 0; i < jComboBox_conceptos.getItemCount(); i++) {
            if (jComboBox_conceptos.getItemAt(i).equals(nuevoConcepto)) {
                verificacion = true;
                break;
            }
        }
        return verificacion;
    }

    //Llenar base de datos de papeles
    public void RegistrarPapel(String PapelAIngresar, String usuario) {

        try {
            String consulta = "insert into papeles (nombrePapel, registradoPor) values (?, ?)";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, PapelAIngresar.toUpperCase());
            pst.setString(2, usuario);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Papel registrado");

        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLIntegrityConstraintViolationException er) {
            JOptionPane.showMessageDialog(this, "El papel que intenta ingresar ya se encuentra registrado en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "MySQLException\nError en registrar el papel DatosGenerales RegistrarPapel()", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    //Llenar base de datos de vendedores/comisionistas
    public void RegistrarVendedor(String vendedorAIngresar, String usuario) {

        try {
            String consulta = "insert into comisionistas (nombreComisionista, registradoPor) values (?, ?)";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, vendedorAIngresar.toUpperCase());
            pst.setString(2, usuario);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Vendedor registrado");

        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "El vendedor/comisionista que intenta ingresar ya se encuentra registrado en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "MySQLException\nError en registrar el vendedor DatosGenerales RegistrarVendedor()", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    //Llenar base de datos de vendedores/comisionistas
    public void RegistrarTipoTrabajo(String tipoTrabajoAIngresar, String usuario) {

        try {
            String consulta = "insert into tipotrabajo (tipo, registradoPor) values (?, ?)";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, tipoTrabajoAIngresar.toUpperCase());
            pst.setString(2, usuario);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Tipo de trabajo registrado");

        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "El tipo de trabajo que intenta ingresar ya se encuentra registrado en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "MySQLException\nError en registrar el tipo de trabajo DatosGenerales RegistrarTipoTrabajo()", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    //Llenar base de datos de maestro de gastos
    public void RegistrarGasto(String descripcion, String idGasto, String idRubro, String idModalidad, String presupuestado) {
        String Consulta = "insert into maestrogastos (descripcion, idGasto, idRubro, idModalidad, presupuestado) values "
                + "(?, ?, ?, ?, ?)";

        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(Consulta);

            pst.setString(1, descripcion);
            pst.setString(2, idGasto);
            pst.setString(3, idRubro);
            pst.setString(4, idModalidad);
            pst.setString(5, presupuestado);

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Rubro de gasto registrado");
        
        
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos","Error",JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "El gasto que intenta ingresar ya se encuentra registrado en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "MySQLException\nError en Registrar el concepto DatosGenerales RegistrarGasto()", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void RegistrarMunicipio(String municipio) {
        String consulta = "insert into municipios (municipio) values (?)";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, municipio.toUpperCase());

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Municipio registrado");
            cn.close();

        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "El municipio que intenta ingresar ya se encuentra registrado en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(this, "MySQLException\nError en registrar el municipio", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    //Consultamos el id del tipo de gasto seleccionado del combobox
    public String consultarIdTipoGasto(String tipoGasto) {
        String idTipoGasto = "";

        String consulta = "select idGasto from tipogastos where tipoGasto=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, tipoGasto);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                idTipoGasto = rs.getString("idGasto");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar el id del tipo de gasto DatosGenerales consultarIdTipoGasto()");
        }

        return idTipoGasto;
    }

    //Consultamos el id del tipo de rubro seleccionado del combobox
    public String consultarIdTipoRubro(String tipoRubro) {
        String idTipoRubro = "";

        String consulta = "select idRubro from rubros where Descripcion=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, tipoRubro);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                idTipoRubro = rs.getString("idRubro");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar el id del tipo de Rubro DatosGenerales consultarIdTipoRubro()");
        }

        return idTipoRubro;
    }

    //Consultamos el id del tipo de rubro seleccionado del combobox
    public String consultarIdTipoModalidad(String tipoModalidad) {
        String idTipoModalidad = "";

        String consulta = "select idModalidad from modalidadgasto where Descripcion=?";
        try {
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, tipoModalidad);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                idTipoModalidad = rs.getString("idModalidad");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar el id del tipo de modalidad DatosGenerales consultarIdTipoModalidad()");
        }

        return idTipoModalidad;
    }

    public boolean VerificarMunicipio(String municipio) {
        boolean verificacion = false;
        for (int i = 0; i < jComboBox_municipio.getItemCount(); i++) {
            if (municipio.equalsIgnoreCase(jComboBox_municipio.getItemAt(i))) {
                verificacion = true;
                break;
            }
        }
        return verificacion;
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
        jTextField_papeles = new javax.swing.JTextField();
        jButton_agregarPapeles = new javax.swing.JButton();
        jComboBox_papeles = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jTextField_vendedores = new javax.swing.JTextField();
        jButton_vendedores = new javax.swing.JButton();
        jComboBox_comisionistas = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jTextField_tipoTrabajo = new javax.swing.JTextField();
        jComboBox_tipoTrabajo = new javax.swing.JComboBox<>();
        jButton_tipoTrabajo = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jTextField_conceptonuevo = new javax.swing.JTextField();
        jComboBox_tipogasto = new javax.swing.JComboBox<>();
        jComboBox_rubro = new javax.swing.JComboBox<>();
        jComboBox_modalidad = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jTextField_valor = new javax.swing.JTextField();
        jComboBox_conceptos = new javax.swing.JComboBox<>();
        jButton_agregarconcepto = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jTextField_municipio = new javax.swing.JTextField();
        jButton_agregarMunicipio = new javax.swing.JButton();
        jComboBox_municipio = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()), "Ingresar papeles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jTextField_papeles.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_papelesKeyTyped(evt);
            }
        });

        jButton_agregarPapeles.setText("Agregar");
        jButton_agregarPapeles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregarPapelesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTextField_papeles, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_agregarPapeles)
                .addGap(18, 18, 18)
                .addComponent(jComboBox_papeles, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_papeles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_agregarPapeles)
                    .addComponent(jComboBox_papeles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Ingresar comisionitas / Vendedores", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jTextField_vendedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_vendedoresActionPerformed(evt);
            }
        });
        jTextField_vendedores.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_vendedoresKeyTyped(evt);
            }
        });

        jButton_vendedores.setText("Agregar");
        jButton_vendedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_vendedoresActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jTextField_vendedores, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_vendedores)
                .addGap(18, 18, 18)
                .addComponent(jComboBox_comisionistas, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_vendedores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_vendedores)
                    .addComponent(jComboBox_comisionistas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Ingresar tipos de trabajo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jTextField_tipoTrabajo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_tipoTrabajoKeyTyped(evt);
            }
        });

        jButton_tipoTrabajo.setText("Agregar");
        jButton_tipoTrabajo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_tipoTrabajoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jTextField_tipoTrabajo, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_tipoTrabajo)
                .addGap(18, 18, 18)
                .addComponent(jComboBox_tipoTrabajo, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_tipoTrabajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox_tipoTrabajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_tipoTrabajo))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Ingresar conceptos de presupuesto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jTextField_conceptonuevo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_conceptonuevoKeyTyped(evt);
            }
        });

        jLabel5.setText("Valor estimado");

        jTextField_valor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_valorKeyTyped(evt);
            }
        });

        jButton_agregarconcepto.setText("Agregar");
        jButton_agregarconcepto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregarconceptoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButton_agregarconcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(jComboBox_conceptos, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox_modalidad, 0, 232, Short.MAX_VALUE)
                            .addComponent(jTextField_conceptonuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addGap(18, 20, Short.MAX_VALUE)
                                .addComponent(jComboBox_tipogasto, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox_rubro, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(23, 23, 23))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField_valor, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_conceptonuevo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox_tipogasto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox_rubro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_modalidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField_valor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_conceptos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_agregarconcepto))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Municipios", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jTextField_municipio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_municipioKeyTyped(evt);
            }
        });

        jButton_agregarMunicipio.setText("Agregar");
        jButton_agregarMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_agregarMunicipioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jTextField_municipio, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton_agregarMunicipio)
                .addGap(18, 18, 18)
                .addComponent(jComboBox_municipio, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_municipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_agregarMunicipio)
                    .addComponent(jComboBox_municipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField_vendedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_vendedoresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_vendedoresActionPerformed

    private void jButton_agregarPapelesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregarPapelesActionPerformed

        String papelAIngresar = jTextField_papeles.getText().trim().toUpperCase();
        //Verificamos que se haya digitado un papel

        RegistrarPapel(papelAIngresar, this.usuario);
        jComboBox_papeles.removeAllItems();
        LlenarComboBoxPapeles();
        jTextField_papeles.setText("");


    }//GEN-LAST:event_jButton_agregarPapelesActionPerformed

    private void jButton_vendedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_vendedoresActionPerformed

        String vendedorAIngresar = jTextField_vendedores.getText().trim().toUpperCase();
        //Verificamos que se haya digitado un vendedor
        if (!vendedorAIngresar.equals("")) {

            RegistrarVendedor(vendedorAIngresar, this.usuario);
            jComboBox_comisionistas.removeAllItems();
            LlenarComboBoxVendedores();
            jTextField_vendedores.setText("");

        } else {
            JOptionPane.showMessageDialog(this, "Completa el campo del vendedor a ingresar", "Informacion", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton_vendedoresActionPerformed

    private void jButton_tipoTrabajoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_tipoTrabajoActionPerformed

        String tipoTrabajoAIngresar = jTextField_tipoTrabajo.getText().trim().toUpperCase();
        //Verificamos que se haya digitado un tipodeTrabajo
        if (!tipoTrabajoAIngresar.equals("")) {

            RegistrarTipoTrabajo(tipoTrabajoAIngresar, this.usuario);
            jComboBox_tipoTrabajo.removeAllItems();
            LlenarComboBoxtipoTrabajo();
            jTextField_tipoTrabajo.setText("");

        } else {
            JOptionPane.showMessageDialog(this, "Completa el campo del tipo de trabajo a ingresar", "Informacion", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton_tipoTrabajoActionPerformed

    private void jButton_agregarconceptoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregarconceptoActionPerformed

        String concepto = jTextField_conceptonuevo.getText().trim().toUpperCase().toUpperCase();
        String valor = jTextField_valor.getText().trim();
        String tipoGasto = jComboBox_tipogasto.getSelectedItem().toString();
        String tipoRubro = jComboBox_rubro.getSelectedItem().toString();
        String modalidad = jComboBox_modalidad.getSelectedItem().toString();

        //Verificamos que se haya completado el campo nuevo concepto
        if (!concepto.equals("") && !valor.equals("")) {
            //Verificamos que el nuevo concepto no este ya registrado en la base de datos

            String idtipoGasto = consultarIdTipoGasto(tipoGasto);
            String idtipoRubro = consultarIdTipoRubro(tipoRubro);
            String idtipoModalidad = consultarIdTipoModalidad(modalidad);

            RegistrarGasto(concepto, idtipoGasto, idtipoRubro, idtipoModalidad, valor);
            jComboBox_conceptos.removeAllItems();
            llenarComboBoxConceptos();
            jTextField_conceptonuevo.setText("");
            jTextField_valor.setText("");

        } else {
            JOptionPane.showMessageDialog(this, "Complete el nombre y el valor del nuevo concepto\n\n"
                    + "Nota: el valor es una base, al momento de cargar el presupuesto podrá ajustarlo", "Informacion", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton_agregarconceptoActionPerformed

    private void jButton_agregarMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_agregarMunicipioActionPerformed
        //Verificamos que se haya ingresado el municipio
        String municipio = jTextField_municipio.getText().trim().toUpperCase();
        if (!municipio.equals("")) {

            RegistrarMunicipio(municipio);
            jComboBox_municipio.removeAllItems();
            llenarComboBoxMunicipios();
            jTextField_municipio.setText("");

        } else {
            JOptionPane.showMessageDialog(this, "Ingrese el municipio");
        }
    }//GEN-LAST:event_jButton_agregarMunicipioActionPerformed

    private void jTextField_papelesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_papelesKeyTyped
        if (jTextField_papeles.getText().trim().length() == 150) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_papelesKeyTyped

    private void jTextField_vendedoresKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_vendedoresKeyTyped
        if (jTextField_vendedores.getText().trim().length() == 150) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_vendedoresKeyTyped

    private void jTextField_tipoTrabajoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_tipoTrabajoKeyTyped
        if (jTextField_tipoTrabajo.getText().trim().length() == 150) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_tipoTrabajoKeyTyped

    private void jTextField_municipioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_municipioKeyTyped
        if (jTextField_municipio.getText().trim().length() == 150) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_municipioKeyTyped

    private void jTextField_conceptonuevoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_conceptonuevoKeyTyped
        if (jTextField_conceptonuevo.getText().trim().length() == 150) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_conceptonuevoKeyTyped

    private void jTextField_valorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_valorKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
        if (c == '0' && jTextField_valor.getText().trim().length() == 0) {
            evt.consume();
        }

        if (jTextField_valor.getText().trim().length() == 11) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_valorKeyTyped

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
            java.util.logging.Logger.getLogger(DatosGenerales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DatosGenerales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DatosGenerales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DatosGenerales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DatosGenerales().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_agregarMunicipio;
    private javax.swing.JButton jButton_agregarPapeles;
    private javax.swing.JButton jButton_agregarconcepto;
    private javax.swing.JButton jButton_tipoTrabajo;
    private javax.swing.JButton jButton_vendedores;
    private javax.swing.JComboBox<String> jComboBox_comisionistas;
    private javax.swing.JComboBox<String> jComboBox_conceptos;
    private javax.swing.JComboBox<String> jComboBox_modalidad;
    private javax.swing.JComboBox<String> jComboBox_municipio;
    private javax.swing.JComboBox<String> jComboBox_papeles;
    private javax.swing.JComboBox<String> jComboBox_rubro;
    private javax.swing.JComboBox<String> jComboBox_tipoTrabajo;
    private javax.swing.JComboBox<String> jComboBox_tipogasto;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTextField jTextField_conceptonuevo;
    private javax.swing.JTextField jTextField_municipio;
    private javax.swing.JTextField jTextField_papeles;
    private javax.swing.JTextField jTextField_tipoTrabajo;
    private javax.swing.JTextField jTextField_valor;
    private javax.swing.JTextField jTextField_vendedores;
    // End of variables declaration//GEN-END:variables
}

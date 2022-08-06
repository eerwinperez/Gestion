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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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

/**
 *
 * @author erwin
 */
public class RegistroClientes extends javax.swing.JFrame {

    DefaultTableModel modelo;
    String usuario, permiso;

    /**
     * Creates new form RegistroClientes
     */
    public RegistroClientes() {
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();

    }

    public RegistroClientes(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
        jTextField_id.setEnabled(false);

        if (permiso.equals("Asistente")) {
            InhabilitarSegunPermiso();
        }

    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Clientes *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void IniciarCaracteristicasGenerales() {
        //InhabilitarCampos();
        llenarTabla();
        llenarComboBoxMunicipios();
    }

    public void InhabilitarSegunPermiso() {
        jButton_actualizar.setEnabled(false);
    }

    public void InhabilitarCampos() {
        jTextField_id.setEnabled(false);

    }

    public void HabilitarCampos() {
        jComboBox_municipio.setEnabled(true);
        //jComboBox_sector.setEnabled(true);
        //jComboBox_tipocliente.setEnabled(true);
        jTextField1_nombreCliente.setEnabled(true);
        jTextField3_direccion.setEnabled(true);
        jTextField3_identificacion.setEnabled(true);
        jTextField4_telefono.setEnabled(true);
        jTextField_email.setEnabled(true);
        jButton_crear.setEnabled(true);
    }

    public void llenarComboBoxMunicipios() {
        String consulta = "select municipio from municipios";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String municipio = rs.getString("municipio");
                jComboBox_municipio.addItem(municipio);
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los municipios\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void LimpiarFormulario() {

        jTextField_id.setText("");
        jTextField_id.setEnabled(false);
        jTextField1_nombreCliente.setText("");
        jTextField1_nombreCliente.setEnabled(true);
        jTextField3_direccion.setText("");
        jTextField3_direccion.setEnabled(true);
        jTextField3_identificacion.setText("");
        jTextField3_identificacion.setEnabled(true);
        jTextField4_telefono.setText("");
        jTextField4_telefono.setEnabled(true);
        jComboBox_municipio.setSelectedIndex(0);
        jComboBox_municipio.setEnabled(true);
        jTextField_contacto.setText("");
        jTextField_email.setText("");

    }

    public void limpiarTabla(DefaultTableModel model) {
        for (int i = 0; i < jTable1_clientes.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    public void llenarTabla() {
        try {
            String consulta = "select idCliente, nombreCliente, identificacion, tipoCliente, direccion, municipio, "
                    + "telefono, contacto, email, registradoPor from clientes order by idCliente desc";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            modelo = (DefaultTableModel) jTable1_clientes.getModel();

            while (rs.next()) {
                Object[] empleados = new Object[10];

                empleados[0] = rs.getString("idCliente");
                empleados[1] = rs.getString("nombreCliente");
                empleados[2] = rs.getString("identificacion");
                empleados[3] = rs.getString("tipoCliente");
                empleados[4] = rs.getString("direccion");
                empleados[5] = rs.getString("municipio");
                empleados[6] = rs.getString("telefono");
                empleados[7] = rs.getString("email");
                empleados[8] = rs.getString("contacto");
                empleados[9] = rs.getString("registradoPor");

                modelo.addRow(empleados);
            }

            jTable1_clientes.setModel(modelo);
            
            TableRowSorter<TableModel> ordenador = new TableRowSorter<TableModel>(modelo);
            jTable1_clientes.setRowSorter(ordenador);
            
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los datos en la tabla clientes desde la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void CapturarDatosTabla() {

        int fila = jTable1_clientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila");
        } else {

            jTextField_id.setText(jTable1_clientes.getValueAt(fila, 0).toString());
            jTextField1_nombreCliente.setText(jTable1_clientes.getValueAt(fila, 1).toString());
            jTextField3_identificacion.setText(jTable1_clientes.getValueAt(fila, 2).toString());
            jTextField3_direccion.setText(jTable1_clientes.getValueAt(fila, 4).toString());
            jComboBox_municipio.setSelectedItem(jTable1_clientes.getValueAt(fila, 5).toString());
            jTextField4_telefono.setText(jTable1_clientes.getValueAt(fila, 6).toString());
            jTextField_email.setText(jTable1_clientes.getValueAt(fila, 7).toString());
            jComboBox_tipoCliente.setSelectedItem(jTable1_clientes.getValueAt(fila, 3).toString());
            jTextField_contacto.setText(jTable1_clientes.getValueAt(fila, 8).toString());

            jTextField_id.setEnabled(false);
            jButton_crear.setEnabled(false);
        }
    }

    public boolean VerificarNIT(String NIT, String RazonSocial) {

        boolean verificacion = false;

        ArrayList<String> listaClientes = new ArrayList<>();
        ArrayList<String> listaRazonSocial = new ArrayList<>();

        try {
            String consulta = "select identificacion, nombreCliente from clientes";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String NITcliente = rs.getString("identificacion");
                String RazonesSociales = rs.getString("nombreCliente");
                listaClientes.add(NITcliente);
                listaRazonSocial.add(RazonesSociales);
            }

            for (int i = 0; i < listaClientes.size(); i++) {
                if (listaClientes.get(i).equals(NIT) || listaRazonSocial.get(i).equals(RazonSocial)) {
                    verificacion = true;
                    break;
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer los NIT de clientes. RegistroClientes VerificarNIT()", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return verificacion;
    }

    public void crearCliente(String nombreCliente, String identificacion, String direccion, String municipio,
            String telefono, String email, String tipoCliente, String contacto) {

        String consulta = "insert into clientes (nombreCliente, identificacion, direccion, municipio, "
                + "telefono, email, registradoPor, tipoCliente, contacto) values (?,  ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, nombreCliente);
            pst.setString(2, identificacion);
            //pst.setString(3, this.tipoCliente);
            pst.setString(3, direccion);
            pst.setString(4, municipio);
            pst.setString(5, telefono);
            pst.setString(6, email);
            pst.setString(7, this.usuario);
            pst.setString(8, tipoCliente);
            pst.setString(9, contacto);

            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(this, "Cliente registrado", "Informacion", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "SQLException\nError al registrar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public void actualizarCliente(String id, String nombreCliente, String identificacion, String direccion,
            String municipio, String telefono, String email, String tipoCliente, String contacto) {

        try {

            String consulta = "update clientes set nombreCliente=?, identificacion=?, direccion=?, municipio=?, "
                    + "telefono=?, tipoCliente=?, email=?, registradoPor=?, contacto=? where idCliente =?";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, nombreCliente);
            pst.setString(2, identificacion);
            pst.setString(3, direccion);
            pst.setString(4, municipio);
            pst.setString(5, telefono);
            pst.setString(6, tipoCliente);
            pst.setString(7, email);
            pst.setString(8, this.usuario);
            pst.setString(9, contacto);
            pst.setString(10, id);

            pst.executeUpdate();
            cn.close();

            JOptionPane.showMessageDialog(this, "Cliente actualizado", "Informacion", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nEs posible que este intengando ingresar un registro pero falte completar algun dato obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MysqlDataTruncation e) {
            JOptionPane.showMessageDialog(this, "Error."
                    + "\nAlgunos de los datos que intenta ingresar son demasiado extensos.\nIntente acortar los textos o no registrar numeros muy grande no logicos", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (HeadlessException | SQLException e) {

            JOptionPane.showMessageDialog(this, "SQLException\nError al actualizar el cliente", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }
    
    public void imprimirInforme(ArrayList<Object []> listado){
        
        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Agenda.xlsx";
        String rutaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Agenda.xlsx";

        try {

            FileInputStream archivo = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivo);
            archivo.close();
            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            int filaEmpieza = 2;
            
            for (Object[] dato : listado) {
                XSSFRow fila = hoja.getRow(filaEmpieza);
//                XSSFRow fila = hoja.createRow(filaEmpieza);
                for (int j = 0; j < dato.length; j++) {
                    XSSFCell celda = fila.getCell(j);
//                    XSSFCell celda = fila.createCell(j);
                    switch (j) {
                        case 0:
                            celda.setCellValue((Double) dato[j]);
                            break;
                        case 1:
                            celda.setCellValue((String) dato[j]);
                            break;
                        case 2:
                            celda.setCellValue((String) dato[j]);
                            break;
                        case 3:
                            celda.setCellValue((String) dato[j]);
                            break;
                        case 4:
                            celda.setCellValue((String) dato[j]);
                            break;
                        case 5:
                            celda.setCellValue((String) dato[j]);
                            break;
                        case 6:
                            celda.setCellValue((String) dato[j]);
                            break;
                        case 7:
                            celda.setCellValue((String) dato[j]);
                            break;
                        case 8:
                            celda.setCellValue((String) dato[j]);
                            break;
                        case 9:
                            celda.setCellValue((String) dato[j]);
                            break;

                    }
                }
                filaEmpieza++;
            }

            FileOutputStream salida = new FileOutputStream(rutaGuardar);
            nuevoLibro.write(salida);
            salida.close();
            JOptionPane.showMessageDialog(this, "Informe generado con exito");

            MetodosGenerales.abrirArchivo(rutaGuardar);

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo para generar el informe. \n"
                    + "Asegurese de no tener abierto un archivo con el mismo nombre. Si el problema persiste, "
                    + "contacte al administrador \nInformeVentas GenerarInforme() \n" + ex);
            ex.printStackTrace();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error en generar el Workbook/Informe. Asegurese de no tener "
                    + "abierto un archivo con el mismo nombre. Si el problema persiste, contacte al administrador. "
                    + "InformeVentas GenerarInforme()");
            ex.printStackTrace();
        }
        
        
    }
    
    public ArrayList<Object []> consultarClientes(){
        
        ArrayList<Object []> listado = new ArrayList<>();
        
        String consulta = "SELECT idCliente, nombreCliente, identificacion, tipoCliente, direccion, "
                + "municipio, telefono, contacto, email FROM clientes";
        
        Connection cn = Conexion.Conectar();
        try {
            
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {                
                Object[] nuevo= new Object[9];
                nuevo[0]=rs.getDouble("idCliente");
                nuevo[1]=rs.getString("nombreCliente");
                nuevo[2]=rs.getString("identificacion");
                nuevo[3]=rs.getString("tipoCliente");
                nuevo[4]=rs.getString("direccion");
                nuevo[5]=rs.getString("municipio");
                nuevo[6]=rs.getString("telefono");
                nuevo[7]=rs.getString("email");
                nuevo[8]=rs.getString("contacto");
                
                listado.add(nuevo);
            } 
            
            cn.close();
            
            return listado;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar la informacion de los clientes","Error",JOptionPane.ERROR_MESSAGE);
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
        jTable1_clientes = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTextField_id = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextField1_nombreCliente = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField3_identificacion = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3_direccion = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jComboBox_municipio = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jTextField4_telefono = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField_email = new javax.swing.JTextField();
        jButton_crear = new javax.swing.JButton();
        jButton_actualizar = new javax.swing.JButton();
        jButton_limpiarCampos = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jComboBox_tipoCliente = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jTextField_contacto = new javax.swing.JTextField();
        jButton_imprimir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1_clientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Cliente", "Cedula / NIT", "Tipo cliente", "Direccion", "Municipio", "Telefono", "Email", "Contacto", "Registrado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1_clientes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1_clientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1_clientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1_clientes);
        if (jTable1_clientes.getColumnModel().getColumnCount() > 0) {
            jTable1_clientes.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTable1_clientes.getColumnModel().getColumn(1).setPreferredWidth(170);
            jTable1_clientes.getColumnModel().getColumn(2).setPreferredWidth(80);
            jTable1_clientes.getColumnModel().getColumn(3).setPreferredWidth(90);
            jTable1_clientes.getColumnModel().getColumn(4).setPreferredWidth(200);
            jTable1_clientes.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTable1_clientes.getColumnModel().getColumn(6).setPreferredWidth(100);
            jTable1_clientes.getColumnModel().getColumn(7).setPreferredWidth(150);
            jTable1_clientes.getColumnModel().getColumn(8).setPreferredWidth(100);
            jTable1_clientes.getColumnModel().getColumn(9).setPreferredWidth(100);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel7.setText("Id");

        jLabel1.setText("Nombre Cliente: ");

        jTextField1_nombreCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1_nombreClienteKeyTyped(evt);
            }
        });

        jLabel2.setText("Cedula / NIT:");

        jTextField3_identificacion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField3_identificacionKeyTyped(evt);
            }
        });

        jLabel3.setText("Direccion:");

        jTextField3_direccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField3_direccionKeyTyped(evt);
            }
        });

        jLabel4.setText("Municipio:");

        jComboBox_municipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_municipioActionPerformed(evt);
            }
        });

        jLabel5.setText("Telefono:");

        jTextField4_telefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField4_telefonoKeyTyped(evt);
            }
        });

        jLabel9.setText("Email:");

        jTextField_email.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField_emailKeyTyped(evt);
            }
        });

        jButton_crear.setText("Crear");
        jButton_crear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_crearActionPerformed(evt);
            }
        });

        jButton_actualizar.setText("Actualizar");
        jButton_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_actualizarActionPerformed(evt);
            }
        });

        jButton_limpiarCampos.setText("Limpiar");
        jButton_limpiarCampos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_limpiarCamposActionPerformed(evt);
            }
        });

        jLabel6.setText("Tipo Cliente:");

        jComboBox_tipoCliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "EMPRESAS", "ENTRADAS DIARIAS" }));

        jLabel8.setText("Contacto:");

        jButton_imprimir.setText("Informe");
        jButton_imprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_imprimirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField3_direccion)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox_municipio, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField4_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField1_nombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField3_identificacion, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jButton_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField_email, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBox_tipoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField_contacto, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jButton_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton_imprimir)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton_limpiarCampos, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addGap(22, 22, 22))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1_nombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField3_identificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox_municipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3_direccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBox_tipoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField_contacto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_limpiarCampos)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton_crear)
                        .addComponent(jButton_actualizar)
                        .addComponent(jButton_imprimir)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1016, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox_municipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_municipioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_municipioActionPerformed

    private void jButton_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_actualizarActionPerformed

        String id = jTextField_id.getText().trim();

        //Verificamos que se haya seleccionado el cliente           
        if (!id.equals("")) {

            String nombreCliente = jTextField1_nombreCliente.getText().trim().toUpperCase();
            String identificacion = jTextField3_identificacion.getText().trim().toUpperCase();
            String direccion = jTextField3_direccion.getText().trim().toUpperCase();
            String municipio = jComboBox_municipio.getSelectedItem().toString().toUpperCase();
            String telefono = jTextField4_telefono.getText().trim().toUpperCase();
            String tipoCliente = jComboBox_tipoCliente.getSelectedItem().toString().toUpperCase();
            String contacto = jTextField_contacto.getText().trim().toUpperCase();
            String email = jTextField_email.getText().trim().toUpperCase();
            //Verificamos que se hayan completado el resto de datos
            if (!nombreCliente.equals("") && !identificacion.equals("") && !direccion.equals("") && !municipio.equals("")
                    && !telefono.equals("")) {

                int opcion = JOptionPane.showConfirmDialog(this, "¿Desea actualizar los datos del cliente?", "Confirmacion", JOptionPane.INFORMATION_MESSAGE);
                if (opcion == 0) {

                    actualizarCliente(id, nombreCliente, identificacion, direccion, municipio, telefono, email, tipoCliente, contacto);
                    LimpiarFormulario();
                    limpiarTabla(modelo);
                    llenarTabla();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Complete todos los campos", "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para actualizar sus datos", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton_actualizarActionPerformed

    private void jButton_crearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_crearActionPerformed

        //Verificamos que todos los datos esten ingresados
        jTextField_id.setText("");
        String nombreCliente = jTextField1_nombreCliente.getText().trim().toUpperCase();
        String identificacion = jTextField3_identificacion.getText().trim().toUpperCase();
        String tipoCliente = jComboBox_tipoCliente.getSelectedItem().toString().trim().toUpperCase();
        String direccion = jTextField3_direccion.getText().trim().toUpperCase();
        String municipio = jComboBox_municipio.getSelectedItem().toString().toUpperCase();
        String telefono = jTextField4_telefono.getText().trim().toUpperCase();
        String contacto = jTextField_contacto.getText().trim().toUpperCase();
        //String sector = jComboBox_sector.getSelectedItem().toString().trim();
        String email = jTextField_email.getText().trim().toUpperCase();

        if (!nombreCliente.equals("") && !identificacion.equals("") && !direccion.equals("") && !telefono.equals("")) {

            //Verificamos que se el cliente no exista ya en la base de datos
            boolean verificacion = VerificarNIT(identificacion, nombreCliente);
            if (!verificacion) {
                //Clientes nuevo = new Clientes(nombreCliente, identificacion, tipoCliente, direccion, municipio, telefono, sector, email);
                crearCliente(nombreCliente, identificacion, direccion, municipio, telefono, email, tipoCliente, contacto);
                limpiarTabla(modelo);
                llenarTabla();
                LimpiarFormulario();

            } else {
                JOptionPane.showMessageDialog(this, "El NIT/Cedula o el nombre del cliente que intenta registrar \nya existe en la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Complete todos los campos", "Informacion", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_jButton_crearActionPerformed

    private void jTable1_clientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1_clientesMouseClicked
        CapturarDatosTabla();

    }//GEN-LAST:event_jTable1_clientesMouseClicked

    private void jButton_limpiarCamposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_limpiarCamposActionPerformed
        HabilitarCampos();
        LimpiarFormulario();

    }//GEN-LAST:event_jButton_limpiarCamposActionPerformed

    private void jTextField1_nombreClienteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1_nombreClienteKeyTyped
        if (jTextField1_nombreCliente.getText().trim().length() == 100) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField1_nombreClienteKeyTyped

    private void jTextField3_identificacionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3_identificacionKeyTyped
        if (jTextField3_identificacion.getText().trim().length() == 50) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField3_identificacionKeyTyped

    private void jTextField3_direccionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3_direccionKeyTyped
        if (jTextField3_direccion.getText().trim().length() == 100) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField3_direccionKeyTyped

    private void jTextField4_telefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4_telefonoKeyTyped
        if (jTextField4_telefono.getText().trim().length() == 150) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField4_telefonoKeyTyped

    private void jTextField_emailKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField_emailKeyTyped
        if (jTextField_email.getText().trim().length() == 250) {
            evt.consume();
        }
    }//GEN-LAST:event_jTextField_emailKeyTyped

    private void jButton_imprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_imprimirActionPerformed
        int numeroFilas = jTable1_clientes.getRowCount();
        
        if (numeroFilas>0) {
            ArrayList<Object []> listado = consultarClientes();
            imprimirInforme(listado);
            
        } else {
            JOptionPane.showMessageDialog(this, "No hay datos para generar el informe", "Informacion", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton_imprimirActionPerformed

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
            java.util.logging.Logger.getLogger(RegistroClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroClientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistroClientes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_actualizar;
    private javax.swing.JButton jButton_crear;
    private javax.swing.JButton jButton_imprimir;
    private javax.swing.JButton jButton_limpiarCampos;
    private javax.swing.JComboBox<String> jComboBox_municipio;
    private javax.swing.JComboBox<String> jComboBox_tipoCliente;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1_clientes;
    private javax.swing.JTextField jTextField1_nombreCliente;
    private javax.swing.JTextField jTextField3_direccion;
    private javax.swing.JTextField jTextField3_identificacion;
    private javax.swing.JTextField jTextField4_telefono;
    private javax.swing.JTextField jTextField_contacto;
    private javax.swing.JTextField jTextField_email;
    private javax.swing.JTextField jTextField_id;
    // End of variables declaration//GEN-END:variables
}

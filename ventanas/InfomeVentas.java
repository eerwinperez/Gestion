/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventanas;

import clases.Conexion;
import clases.MetodosGenerales;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JOptionPane;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.LinkedHashSet;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;

/**
 *
 * @author erwin
 */
public class InfomeVentas extends javax.swing.JFrame {

    String usuario, permiso;

    /**
     * Creates new form InfomeVentas
     */
    public InfomeVentas() {
        initComponents();
        llenarComboBoxTiposDeTrabajo();
        llenarComboBoxClientes();
        ConfiguracionGralJFrame();
    }

    public InfomeVentas(String usuario, String permiso) {
        this.usuario = usuario;
        this.permiso = permiso;
        initComponents();
        IniciarCaracteristicasGenerales();
        ConfiguracionGralJFrame();
    }

    public void IniciarCaracteristicasGenerales() {
        llenarComboBoxTiposDeTrabajo();
        llenarComboBoxClientes();
    }

    public void ConfiguracionGralJFrame() {
        //Cambiar Icono Jframe
        setIconImage(new ImageIcon(getClass().getResource("/Imagenes/Icono.png")).getImage());
        //Titulo
        setTitle("Informes *** " + "Usuario: " + usuario + " - " + permiso);
        //Localizacion del JFram (Centrado)
        setLocationRelativeTo(null);
        //Tamaño fijo
        setResizable(false);
        //Al cerrar solo se cierra esta ventana, no las precedentes
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public void GenerarInformeVentas(String fechaDesde, String fechaHasta, String tipoCliente, String usuario) {

        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Informe de ventas.xlsx";
        String rutaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Informe de ventas.xlsx";

        try {

            FileInputStream archivo = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivo);
            archivo.close();
            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda10 = fila1.getCell(0);
            String rango = (tipoCliente.equalsIgnoreCase("Todos")) ? "TOTAL" : tipoCliente.toUpperCase();
            celda10.setCellValue("INFORME DE VENTAS " + rango);

            //Datos fila 1
            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda20 = fila2.getCell(0);
            celda20.setCellValue("Desde " + fechaDesde + " hasta " + fechaHasta);

            //Datos fila 3
            XSSFRow fila3 = hoja.getRow(3);
            XSSFCell celda31 = fila3.getCell(1);
            celda31.setCellValue(new Date());

            //Datos fila 4
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda41 = fila4.getCell(1);
            celda41.setCellValue(usuario);

            //Datos de las ventas, empieza en la fila 7
            int filaEmpieza = 7;
            ArrayList<Object[]> datos = ConsultarVentas(fechaDesde, fechaHasta, tipoCliente);

            //Declaramos una variable para acumular el total de ventas
            double sumaTotal = 0;

            for (Object[] dato : datos) {
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
                            celda.setCellValue((Date) dato[j]);
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
                            celda.setCellValue((Double) dato[j]);
                            break;
                        case 6:
                            celda.setCellValue((String) dato[j]);
                            break;
                        case 7:
                            celda.setCellValue((Double) dato[j]);
                            break;
                        case 8:
                            celda.setCellValue((Double) dato[j]);
                            sumaTotal += (Double) dato[j];
                            break;
                        case 9:
                            celda.setCellValue((String) dato[j]);
                            break;

                    }
                }
                filaEmpieza++;
            }

            //Incluimos en la fila 4 la suma de las ventas            
            XSSFCell celda48 = fila4.getCell(8);
            celda48.setCellValue(sumaTotal);

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

    public ArrayList<Object[]> ConsultarVentas(String fechaDesde, String fechaHasta, String tipoCliente) {
        ArrayList<Object[]> datos = new ArrayList<>();
        String consulta = "";

        if (tipoCliente.equalsIgnoreCase("Todos")) {
            consulta = "select ventas.Idventa, ventas.FechaventaSistema, ventas.Vendedor, clientes.nombreCliente, "
                    + "ventas.tipoVenta, ventas.Cantidad, ventas.clasificacion, ventas.descripcionTrabajo, ventas.unitario, "
                    + "ventas.precio from ventas inner join clientes on ventas.Idcliente=clientes.idCliente "
                    + "where ventas.estado='Activo' and ventas.FechaventaSistema between ? and ? order by ventas.Idventa";
        } else if (tipoCliente.equalsIgnoreCase("Empresas") || tipoCliente.equalsIgnoreCase("Entradas diarias")) {
            consulta = "select ventas.Idventa, ventas.FechaventaSistema, ventas.Vendedor, clientes.nombreCliente, "
                    + "ventas.tipoVenta, ventas.Cantidad, ventas.clasificacion, ventas.descripcionTrabajo, ventas.unitario, "
                    + "ventas.precio from ventas inner join clientes on ventas.Idcliente=clientes.idCliente "
                    + "where clientes.tipoCliente=? and ventas.estado='Activo' and ventas.FechaventaSistema between ? and ? order by ventas.Idventa";
        }

        Connection cn = Conexion.Conectar();
        try {

            PreparedStatement pst = cn.prepareStatement(consulta);
            if (tipoCliente.equalsIgnoreCase("Todos")) {
                pst.setString(1, fechaDesde);
                pst.setString(2, fechaHasta);
            } else {
                pst.setString(1, tipoCliente);
                pst.setString(2, fechaDesde);
                pst.setString(3, fechaHasta);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[10];
                nuevo[0] = rs.getDouble("ventas.Idventa");
                nuevo[1] = rs.getDate("ventas.FechaventaSistema");
                nuevo[2] = rs.getString("ventas.Vendedor");
                nuevo[3] = rs.getString("clientes.nombreCliente");
                nuevo[4] = rs.getString("ventas.tipoVenta");
                nuevo[5] = rs.getDouble("ventas.Cantidad");
                nuevo[6] = rs.getString("ventas.descripcionTrabajo");
                nuevo[7] = rs.getDouble("ventas.unitario");
                nuevo[8] = rs.getDouble("ventas.precio");
                nuevo[9] = rs.getString("ventas.clasificacion");

                datos.add(nuevo);

            }
            cn.close();
            return datos;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al leer las ventas en las fechas indicadas. InformeVentas ConsultarVentas()");

        }

        return null;
    }

    public void GenerarInformeIngresos(String fechaDesde, String fechaHasta, String tipoCliente, String usuario) {
//        String consulta1 = "";
//        String consulta2 = "";

        if (tipoCliente.equalsIgnoreCase("Todos")) {

            ArrayList<Object[]> DatosAbonosEntradasDiarias = ConsultarAbonosEntradasDiarias(fechaDesde, fechaHasta);
            ArrayList<Object[]> DatosAbonoFacturas = ConsultarAbonosFacturas(fechaDesde, fechaHasta);
            GenerarInformeConjunto(DatosAbonosEntradasDiarias, DatosAbonoFacturas, fechaDesde, fechaHasta);
        } else if (tipoCliente.equalsIgnoreCase("Entradas diarias")) {
            ArrayList<Object[]> DatosAbonosEntradasDiarias = ConsultarAbonosEntradasDiarias(fechaDesde, fechaHasta);
            GenerarInforme(DatosAbonosEntradasDiarias, fechaDesde, fechaHasta, "Entradas diarias");
        } else {
            ArrayList<Object[]> DatosAbonoFacturas = ConsultarAbonosFacturas(fechaDesde, fechaHasta);
            GenerarInforme(DatosAbonoFacturas, fechaDesde, fechaHasta, "Empresas");
        }

    }

    public ArrayList<Object[]> ConsultarAbonosEntradasDiarias(String fechaDesde, String fechaHasta) {

        ArrayList<Object[]> listaAbonos = new ArrayList<>();

        String consulta = "select a.fecha, a.idAbono, v.clasificacion, concat('E. Diaria ', a. idVenta) as venta, c.nombreCliente, a.valor, a.observaciones, a.registradoPor\n"
                + "from abonos a join ventas v on a.idVenta=v.Idventa and a.estado='Activo'\n"
                + "join clientes c on v.Idcliente=c.idCliente\n"
                + "where a.fecha between ? and ?";

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaDesde);
            pst.setString(2, fechaHasta);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Object[] infoAbono = new Object[8];
                infoAbono[0] = rs.getDate("a.fecha");
                infoAbono[1] = rs.getDouble("a.idAbono");
                infoAbono[2] = rs.getString("venta");
                infoAbono[3] = rs.getString("c.nombreCliente");
                infoAbono[4] = rs.getDouble("a.valor");
                infoAbono[5] = rs.getString("a.observaciones");
                infoAbono[6] = rs.getString("a.registradoPor");
                infoAbono[7] = rs.getString("v.clasificacion");

                listaAbonos.add(infoAbono);
            }

            cn.close();
            return listaAbonos;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer la informacion de los abonos. "
                    + "ConsultarAbonosEntradasDiarias() InformeVentas");
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Object[]> ConsultarAbonosFacturas(String fechaDesde, String fechaHasta) {

        ArrayList<Object[]> listaAbonosFactura = new ArrayList<>();
        String consulta = "select distinct a.fecha, a.idAbono, v.clasificacion, concat('Factura ',a.factura) as factura, c.nombreCliente, a.abono, a.observaciones, a.registradoPor "
                + "from abonosfacturas a join elementosfactura ef on a.factura=ef.factura\n"
                + "join elementosremision er on ef.idElementoRemito=er.id\n"
                + "join ventas v on er.idVenta=v.Idventa\n"
                + "join clientes c on v.Idcliente=c.idCliente\n"
                + "where a.fecha between ? and ? and a.estado='Activo'";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, fechaDesde);
            pst.setString(2, fechaHasta);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {

                Object[] infoAbono = new Object[8];
                infoAbono[0] = rs.getDate("a.fecha");
                infoAbono[1] = rs.getDouble("a.idAbono");
                infoAbono[2] = rs.getString("factura");
                infoAbono[3] = rs.getString("c.nombreCliente");
                infoAbono[4] = rs.getDouble("a.abono");
                infoAbono[5] = rs.getString("a.observaciones");
                infoAbono[6] = rs.getString("a.registradoPor");
                infoAbono[7] = rs.getString("v.clasificacion");

                listaAbonosFactura.add(infoAbono);
            }

            cn.close();
            return listaAbonosFactura;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer la informacion de los abonos de las facturas. "
                    + "ConsultarAbonosEntradasDiarias() InformeVentas");
            e.printStackTrace();
        }

        return null;
    }

    public void GenerarInformeConjunto(ArrayList<Object[]> DatosAbonosEntradasDiarias, ArrayList<Object[]> DatosAbonoFacturas, String fechaDesde, String fechaHasta) {
        String Plantilla = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Informe de Ingresos.xlsx";
        String rutaAGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Informe de Ingresos.xlsx";

        try {
            FileInputStream plantillaAUtilizar = new FileInputStream(Plantilla);
            XSSFWorkbook Libro = new XSSFWorkbook(plantillaAUtilizar);
            XSSFSheet hoja = Libro.getSheetAt(0);

            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda10 = fila1.getCell(0);
            celda10.setCellValue("INFORME DE INGRESOS TOTAL");

            //Datos fila 2
            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda20 = fila2.getCell(0);
            celda20.setCellValue("Desde " + fechaDesde + " hasta " + fechaHasta);

            //Datos fila 3
            XSSFRow fila3 = hoja.getRow(3);
            XSSFCell celda31 = fila3.getCell(1);
            celda31.setCellValue(new Date());

            //Datos fila 4
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda41 = fila4.getCell(1);
            celda41.setCellValue(this.usuario);

            //La fila desde donde se empiezan a agregar los datos al informe
            int filaEmpieza = 7;
            double total = 0;
            //Empezamos agregando los datos de los abonos de entradas diarias
            for (Object[] datos : DatosAbonosEntradasDiarias) {
                XSSFRow fila = hoja.getRow(filaEmpieza);
                for (int j = 0; j < datos.length; j++) {
                    XSSFCell celda = fila.getCell(j);
                    switch (j) {
                        case 0:
                            celda.setCellValue((Date) datos[j]);
                            break;
                        case 1:
                            celda.setCellValue((Double) datos[j]);
                            break;
                        case 2:
                            celda.setCellValue((String) datos[j]);
                            break;
                        case 3:
                            celda.setCellValue((String) datos[j]);
                            break;
                        case 4:
                            celda.setCellValue((Double) datos[j]);
                            total += (Double) datos[j];
                            break;
                        case 5:
                            celda.setCellValue((String) datos[j]);
                            break;
                        case 6:
                            celda.setCellValue((String) datos[j]);
                            break;
                        case 7:
                            celda.setCellValue((String) datos[j]);
                            break;

                    }
                }
                filaEmpieza++;
            }
            //Agregamos la informacion concerniente a los pagos en las facturas
            for (Object[] datos : DatosAbonoFacturas) {
                XSSFRow fila = hoja.getRow(filaEmpieza);
                for (int j = 0; j < datos.length; j++) {
                    XSSFCell celda = fila.getCell(j);
                    switch (j) {
                        case 0:
                            celda.setCellValue((Date) datos[j]);
                            break;
                        case 1:
                            celda.setCellValue((Double) datos[j]);
                            break;
                        case 2:
                            celda.setCellValue((String) datos[j]);
                            break;
                        case 3:
                            celda.setCellValue((String) datos[j]);
                            break;
                        case 4:
                            celda.setCellValue((Double) datos[j]);
                            total += (Double) datos[j];
                            break;
                        case 5:
                            celda.setCellValue((String) datos[j]);
                            break;
                        case 6:
                            celda.setCellValue((String) datos[j]);
                            break;
                    }
                }
                filaEmpieza++;
            }

            XSSFCell celda44 = fila4.getCell(4);
            celda44.setCellValue(total);

            FileOutputStream salida = new FileOutputStream(rutaAGuardar);
            Libro.write(salida);
            salida.close();
            JOptionPane.showMessageDialog(this, "Informe generado con exito");
            MetodosGenerales.abrirArchivo(rutaAGuardar);

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo para generar el informe. \n"
                    + "Asegurese de no tener abierto un archivo con el mismo nombre. Si el problema persiste, "
                    + "contacte al administrador \nInformeVentas GenerarInforme() \n" + ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error en generar el Workbook/Informe. Asegurese de no tener "
                    + "abierto un archivo con el mismo nombre. Si el problema persiste, contacte al administrador. "
                    + "InformeVentas GenerarInforme() \n" + ex);
        }

    }

    public void GenerarInforme(ArrayList<Object[]> DatosAbonosEntradasDiarias, String fechaDesde, String fechaHasta, String tipoVenta) {
        //String Plantilla = "Docs" + File.separator + "Informe de Ingresos.xlsx";
        String Plantilla = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Informe de Ingresos.xlsx";

        String rutaAGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Informe de Ingresos.xlsx";

        try {
            FileInputStream plantillaAUtilizar = new FileInputStream(Plantilla);
            XSSFWorkbook Libro = new XSSFWorkbook(plantillaAUtilizar);
            XSSFSheet hoja = Libro.getSheetAt(0);

            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda10 = fila1.getCell(0);
            celda10.setCellValue("INFORME DE INGRESOS " + tipoVenta.toUpperCase());

            //Datos fila 2
            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda20 = fila2.getCell(0);
            celda20.setCellValue("Desde " + fechaDesde + " hasta " + fechaHasta);

            //Datos fila 3
            XSSFRow fila3 = hoja.getRow(3);
            XSSFCell celda31 = fila3.getCell(1);
            celda31.setCellValue(new Date());

            //Datos fila 4
            XSSFRow fila4 = hoja.getRow(4);
            XSSFCell celda41 = fila4.getCell(1);
            celda41.setCellValue(this.usuario);

            //La fila desde donde se empiezan a agregar los datos al informe
            int filaEmpieza = 7;
            double total = 0;

            //Empezamos agregando los datos de los abonos de entradas diarias
            for (Object[] datos : DatosAbonosEntradasDiarias) {
                XSSFRow fila = hoja.getRow(filaEmpieza);
                for (int j = 0; j < datos.length; j++) {
                    XSSFCell celda = fila.getCell(j);
                    switch (j) {
                        case 0:
                            celda.setCellValue((Date) datos[j]);
                            break;
                        case 1:
                            celda.setCellValue((Double) datos[j]);
                            break;
                        case 2:
                            celda.setCellValue((String) datos[j]);
                            break;
                        case 3:
                            celda.setCellValue((String) datos[j]);
                            break;
                        case 4:
                            celda.setCellValue((Double) datos[j]);
                            total += (Double) datos[j];
                            break;
                        case 5:
                            celda.setCellValue((String) datos[j]);
                            break;
                        case 6:
                            celda.setCellValue((String) datos[j]);
                            break;
                        case 7:
                            celda.setCellValue((String) datos[j]);
                            break;
                    }
                }
                filaEmpieza++;
            }

            XSSFCell celda44 = fila4.getCell(4);
            celda44.setCellValue(total);

            FileOutputStream salida = new FileOutputStream(rutaAGuardar);
            Libro.write(salida);
            salida.close();
            JOptionPane.showMessageDialog(this, "Informe generado con exito");
            MetodosGenerales.abrirArchivo(rutaAGuardar);

        } catch (FileNotFoundException ex) {

            JOptionPane.showMessageDialog(this, "Error al leer el archivo para generar el informe. \n"
                    + "Asegurese de no tener abierto un archivo con el mismo nombre. Si el problema persiste, "
                    + "contacte al administrador \nInformeVentas GenerarInforme() \n" + ex);
            ex.printStackTrace();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error en generar el Workbook/Informe. Asegurese de no tener "
                    + "abierto un archivo con el mismo nombre. Si el problema persiste, contacte al administrador. "
                    + "InformeVentas GenerarInforme() \n" + ex);
            ex.printStackTrace();

        }

    }

    public void llenarComboBoxTiposDeTrabajo() {
        String consulta = "select tipo from tipotrabajo";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                jComboBox_tipoTrabajo.addItem(tipo.toUpperCase());
                jComboBox_tipoTrabajoTiempo.addItem(tipo.toUpperCase());
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer los tipos de trabajo. InformeVentas llevanComboBoxTiposDeTrabajo()");
        }

    }

    public void llenarComboBoxClientes() {
        String consulta = "select nombreCliente from clientes order by nombreCliente";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String cliente = rs.getString("nombreCliente");
                jComboBox_estadocuenta.addItem(cliente);
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer los clientes. InformeVentas llenarComboBoxClientes()");
        }

    }

    public void GenerarInformeTrabajosMasVendidos(String fechaDesde, String fechaHasta, String tipotrabajo) {

        //String Plantilla = "Docs" + File.separator + "Informe de tipos de trabajo.xlsx";
        String Plantilla = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Informe de tipos de trabajo.xlsx";
        String rutaAGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Informe de tipos de trabajo.xlsx";

        try {
            FileInputStream nuevo = new FileInputStream(Plantilla);
            XSSFWorkbook wb = new XSSFWorkbook(nuevo);
            XSSFSheet hoja = wb.getSheetAt(0);

            //Datos de la fila 1
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda10 = fila1.getCell(0);
            celda10.setCellValue("Desde " + fechaDesde + " hasta " + fechaHasta);

            //  Datos tomados de la consuta
            ArrayList<Object[]> lista = ConsultaInformacionTipoTrabajos(fechaDesde, fechaHasta, tipotrabajo);

            //Fila Inicial es la 4
            int filaInicial = 4;

            for (Object[] dato : lista) {
                XSSFRow fila = hoja.getRow(filaInicial);
                for (int j = 0; j < dato.length; j++) {
                    XSSFCell celda = fila.getCell(j);
                    switch (j) {
                        case 0:
                            celda.setCellValue((String) dato[j]);
                            break;
                        case 1:
                            celda.setCellValue((Double) dato[j]);
                    }
                }
                filaInicial++;
            }

            FileOutputStream salida = new FileOutputStream(rutaAGuardar);
            wb.write(salida);
            salida.close();

            JOptionPane.showMessageDialog(this, "Informe generado");
            MetodosGenerales.abrirArchivo(rutaAGuardar);

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Object[]> ConsultaInformacionTipoTrabajos(String fechaDesde, String fechaHasta, String tipotrabajo) {

        ArrayList<Object[]> lista = new ArrayList<>();
        String consulta = "";
        if (tipotrabajo.equalsIgnoreCase("Todos")) {
            consulta = "select tipoTrabajo, sum(precio) as total from ventas \n"
                    + "where FechaventaSistema between ? and ? and estado='Activo'\n"
                    + "group by tipoTrabajo";

        } else {
            consulta = "select tipoTrabajo, ifnull(sum(precio), 0) as total  from ventas \n"
                    + "where FechaventaSistema between ? and ? and estado='Activo' and tipoTrabajo=?\n"
                    + "group by tipoTrabajo";

        }

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);

            if (tipotrabajo.equalsIgnoreCase("Todos")) {
                pst.setString(1, fechaDesde);
                pst.setString(2, fechaHasta);
            } else {
                pst.setString(1, fechaDesde);
                pst.setString(2, fechaHasta);
                pst.setString(3, tipotrabajo);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[2];
                nuevo[0] = rs.getString("tipoTrabajo");
                nuevo[1] = rs.getDouble("total");
                lista.add(nuevo);
            }

            cn.close();
            return lista;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error en leer las ventas por tipo de trabajo en la base de datos. \n"
                    + "InformeVentas ConsultaInformacionTipoTrabajos() \n" + ex);
        }

        return null;
    }

    public void CalcularTiempoPromedioDeProduccion(String fechaDesde, String fechaHasta, String tipotrabajo) {

        ArrayList<String[]> lista = ConsultarTiemposDeProduccion(fechaDesde, fechaHasta, tipotrabajo);
        //Verificamos que la lista no este vacia para evitar error por division cero
        long promedio = 0;
        int suma = 0;

        if (lista.size() > 0) {
            for (int i = 0; i < lista.size(); i++) {

                suma += MetodosGenerales.RestarFechas(lista.get(i)[0], lista.get(i)[1]);
            }

            promedio = Math.round(suma / lista.size());

            JOptionPane.showMessageDialog(this, "Fechas desde: " + fechaDesde + " hasta: " + fechaHasta + "\n\n"
                    + "Tipo de trabajo: " + tipotrabajo + "\n\n" + "Tiempo promedio de produccion: " + promedio + " días");

        } else {
            JOptionPane.showMessageDialog(this, "No hay trabajos en las fechas indicadas");
        }

    }

    public ArrayList<String[]> ConsultarTiemposDeProduccion(String fechaDesde, String fechaHasta, String tipotrabajo) {
        ArrayList<String[]> lista = new ArrayList<>();
        String consulta = "";

        if (tipotrabajo.equalsIgnoreCase("Todos")) {
            consulta = "select FechaventaSistema, fechaTerminacion from ventas where fechaTerminacion is not null and estado='Activo' and FechaventaSistema between ? and ?";

        } else {
            consulta = "select FechaventaSistema, fechaTerminacion from ventas where tipoTrabajo=? "
                    + "and fechaTerminacion is not null and estado='Activo' and FechaventaSistema between ? and ?";
        }

        Connection cn = Conexion.Conectar();
        try {

            PreparedStatement pst = cn.prepareStatement(consulta);

            if (tipotrabajo.equalsIgnoreCase("Todos")) {
                pst.setString(1, fechaDesde);
                pst.setString(2, fechaHasta);

            } else {
                pst.setString(1, tipotrabajo);
                pst.setString(2, fechaDesde);
                pst.setString(3, fechaHasta);
            }

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String[] dato = new String[2];
                dato[0] = rs.getString("FechaventaSistema");
                dato[1] = rs.getString("fechaTerminacion");

                lista.add(dato);
            }

            cn.close();
            return lista;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer la lista de ventas y sus tiempos de produccion. "
                    + "InformeVentas ConsultarTiemposDeProduccion()\n" + e);
        }

        return null;
    }

    public void CalcularTiempoPromedioDeCobro(String fechaDesde, String fechaHasta, String cliente) {

        if (cliente.equals("Todos")) {
            CalcularTiempoPromedioDeCobroTodosCliente(fechaDesde, fechaHasta);
        } else {
            CalcularTiempoPromedioDeCobroCliente(fechaDesde, fechaHasta, cliente);
        }
    }

    public String[] ConsultarTipoCliente(String cliente) {
        String[] datos = new String[2];

        String consulta = "select tipoCliente, idCliente from clientes where nombreCliente=?";
        Connection cn = Conexion.Conectar();

        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, cliente);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                datos[0] = rs.getString("tipoCliente");
                datos[1] = rs.getString("idCliente");
            }
            cn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en consultar el tipo de cliente de la BD\n " + e);
        }

        return datos;
    }

    public void CalcularTiempoPromedioDeCobroCliente(String fechaDesde, String fechaHasta, String cliente) {

        String consulta = "";
        String[] datosCliente = ConsultarTipoCliente(cliente);
        ArrayList<String[]> listaFechas = new ArrayList<>();

        if (datosCliente[0].equals("Empresa")) {
            consulta = "select fechaFactura, fechaSaldado from facturas where idCliente=? and estadoPago='Saldado'";
        } else {
            consulta = "select FechaventaSistema, fechaSaldado from ventas where estadoCuenta='Saldado' and Idcliente=?";
        }

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, datosCliente[1]);
            ResultSet rs = pst.executeQuery();

            if (datosCliente[0].equals("Empresa")) {
                while (rs.next()) {
                    String[] fechas = new String[2];
                    fechas[0] = rs.getString("fechaFactura");
                    fechas[1] = rs.getString("fechaSaldado");
                    listaFechas.add(fechas);
                }
            } else {
                while (rs.next()) {
                    String[] fechas = new String[2];
                    fechas[0] = rs.getString("FechaventaSistema");
                    fechas[1] = rs.getString("fechaSaldado");
                    listaFechas.add(fechas);
                }
            }
            //Declaramos una variable suma para calcular el promedio
            int suma = 0;
            //Chequeamos que haya datos para mostrar o no
            if (listaFechas.size() > 0) {
                for (int i = 0; i < listaFechas.size(); i++) {
                    suma += MetodosGenerales.RestarFechas(listaFechas.get(i)[0], listaFechas.get(i)[1]);
                }

                int promedio = suma / listaFechas.size();

                JOptionPane.showMessageDialog(this, "Fechas desde: " + fechaDesde + " hasta: " + fechaHasta + "\n\n"
                        + "Cliente: " + cliente + "\n\n" + "Tiempo promedio de cobro: " + promedio + " días");
            } else {
                JOptionPane.showMessageDialog(this, "No hay datos en las fechas y para el cliente seleccionado");
            }

            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error en leer las fechas del cliente\n" + e);
        }

    }

    public void CalcularTiempoPromedioDeCobroTodosCliente(String fechaDesde, String fechaHasta) {
        //Consultamos los datos (fechas) de todos los clientes personas
        ArrayList<String[]> listaEntradasDiarias = new ArrayList<>();
        String consulta = "select FechaventaSistema, fechaSaldado from ventas where estadoCuenta='Saldado'";
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String[] nuevo = new String[2];
                nuevo[0] = rs.getString("FechaventaSistema");
                nuevo[1] = rs.getString("fechaSaldado");
                listaEntradasDiarias.add(nuevo);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar las fechas e informacion de clientes "
                    + "(Entradas duarias) InformeVentas CalcularTiempoPromedioDeCobroTodosCliente()\n" + e);
        }
        //Consultamos los datos (fechas) de todos los clientes Empresas
        ArrayList<String[]> listaEmpresas = new ArrayList<>();
        String consulta1 = "select fechaFactura, fechaSaldado from facturas where estadoPago='Saldado'";
        Connection cn2 = Conexion.Conectar();
        try {
            PreparedStatement pst2 = cn2.prepareStatement(consulta1);
            ResultSet rs2 = pst2.executeQuery();
            while (rs2.next()) {
                String[] nuevo = new String[2];
                nuevo[0] = rs2.getString("fechaFactura");
                nuevo[1] = rs2.getString("fechaSaldado");
                listaEmpresas.add(nuevo);
            }
            cn2.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar las fechas e informacion de clientes "
                    + "(Empresas) InformeVentas CalcularTiempoPromedioDeCobroTodosCliente()\n" + e);
        }

        //Comprobamos que las colecciones no estan vacias
        if ((listaEmpresas.size() + listaEntradasDiarias.size()) > 0) {
            int sumaEntradasDiarias = 0;
            for (int i = 0; i < listaEntradasDiarias.size(); i++) {
                sumaEntradasDiarias += MetodosGenerales.RestarFechas(listaEntradasDiarias.get(i)[0], listaEntradasDiarias.get(i)[1]);
            }

            int sumaEmpresas = 0;
            for (int i = 0; i < listaEmpresas.size(); i++) {
                sumaEntradasDiarias += MetodosGenerales.RestarFechas(listaEmpresas.get(i)[0], listaEmpresas.get(i)[1]);
            }

            int promedio = (sumaEmpresas + sumaEntradasDiarias) / (listaEntradasDiarias.size() + listaEmpresas.size());

            JOptionPane.showMessageDialog(this, "Fechas desde: " + fechaDesde + " hasta: " + fechaHasta + "\n\n"
                    + "Cliente: Todos\n\nTiempo promedio de cobro: " + promedio + " días");

        } else {
            JOptionPane.showMessageDialog(this, "No hay datos en las fechas seleccionadas");
        }

    }

    public void GenerarInforme(String cliente, String fechaDesde, String fechaHasta) {

        if (cliente.equalsIgnoreCase("EMPRESAS")) {

            LinkedHashSet<Integer> abonos = new LinkedHashSet<>();
            ArrayList<Object[]> listado = new ArrayList<>();

            String consulta = "select f.idFactura, f.registradoPor, f.monto, f.fechaFactura, c.nombreCliente, ifnull(f.monto-sum(a.abono), f.monto) as saldo, f.condiciondePago\n"
                    + "from facturas f left join abonosfacturas a on f.idFactura=a.factura and a.estado='Activo'\n"
                    + "join clientes c on f.idCliente=c.idCliente\n"
                    + "where f.estado='Activo' and fechaFactura between ? and ?\n"
                    + "group by f.idFactura\n"
                    + " order by f.idFactura asc";

            Connection cn = Conexion.Conectar();
            try {
                PreparedStatement pst = cn.prepareStatement(consulta);
                pst.setString(1, fechaDesde);
                pst.setString(2, fechaHasta);

                ResultSet rs = pst.executeQuery();

                while (rs.next()) {

                    Object[] facturas = new Object[8];

                    facturas[0] = rs.getInt("f.idFactura");
                    facturas[1] = rs.getDate("f.fechaFactura");
                    facturas[2] = rs.getString("c.nombreCliente");
                    facturas[3] = rs.getDouble("f.monto");
                    facturas[4] = "";
                    facturas[5] = rs.getDouble("saldo");
                    facturas[6] = rs.getString("f.condiciondePago");
                    facturas[7] = rs.getString("f.registradoPor");

                    listado.add(facturas);
                    abonos.add((Integer) facturas[0]);

                }

                String facturas = "(";

                for (int i = 0; i < abonos.size(); i++) {
                    if (i != listado.size() - 1) {
                        facturas += String.valueOf((Integer) listado.get(i)[0]) + ",";
                    } else {
                        facturas += String.valueOf((Integer) listado.get(i)[0]) + ")";
                    }
                }

                String consultaAbonos = "SELECT idAbono, factura, abono, fecha, observaciones, "
                        + "registradoPor from abonosfacturas where factura in " + facturas + " and estado='Activo' order by idAbono asc";

                ArrayList<Object[]> listadoAbonos = consultarAbonos(consultaAbonos);

                ImprimirInformeDeudaFacturas(listado, listadoAbonos, cliente, fechaDesde, fechaHasta);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al generar el estado de cuenta de todas las empresas", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

        } else if (cliente.equalsIgnoreCase("ENTRADAS DIARIAS")) {

            LinkedHashSet<Integer> abonos = new LinkedHashSet<>();
            ArrayList<Object[]> listado = new ArrayList<>();

            String consulta = "select v.Idventa, v.precio, v.precio, c.nombreCliente, v.registradoPor, v.FechaventaSistema, v.Idcliente, v.Cantidad, concat(v.descripcionTrabajo,' - ' ,v.tamaño) as descripcion, v.FechaventaSistema,\n"
                    + "ifnull(v.precio - SUM(a.valor), v.precio) as saldo\n"
                    + "from ventas v left join abonos a on v.Idventa=a.idVenta and a.estado='Activo'\n"
                    + "left join clientes c on v.Idcliente=c.idCliente\n"
                    + "where v.tipoVenta='Entradas diarias' and v.estado='Activo' and FechaventaSistema between ? and ? \n"
                    + " group by v.Idventa \n"
                    + " ORDER by v.Idventa";

            Connection cn = Conexion.Conectar();

            try {
                PreparedStatement pst = cn.prepareStatement(consulta);
                pst.setString(1, fechaDesde);
                pst.setString(2, fechaHasta);

                ResultSet rs = pst.executeQuery();

                while (rs.next()) {

                    Object[] nuevo = new Object[7];
                    nuevo[0] = rs.getInt("v.Idventa");
                    nuevo[1] = rs.getDate("v.FechaventaSistema");
                    nuevo[2] = rs.getString("nombreCliente");
                    nuevo[3] = rs.getString("descripcion");
                    nuevo[4] = rs.getDouble("v.precio");
                    nuevo[5] = rs.getString("v.registradoPor");
                    nuevo[6] = rs.getDouble("saldo");

                    listado.add(nuevo);

                    abonos.add((Integer) nuevo[0]);

                }

                String ventas = "(";

                for (int i = 0; i < abonos.size(); i++) {
                    if (i != listado.size() - 1) {
                        ventas += String.valueOf((Integer) listado.get(i)[0]) + ",";
                    } else {
                        ventas += String.valueOf((Integer) listado.get(i)[0]) + ")";
                    }
                }

                String consultaAbonos = "select a.idAbono, a.idVenta, c.nombreCliente, a.fecha, a.valor, a.observaciones, a.registradoPor\n"
                        + "from abonos a left join ventas v on a.idVenta=v.idVenta\n"
                        + "left join clientes c on v.idCliente=c.idCliente\n"
                        + "where a.idVenta in " + ventas + " and a.estado='Activo' order by a.idAbono";

                ArrayList<Object[]> listadoAbonos = consultarAbonosEntradasDiarias(consultaAbonos);

                ImprimirInformeDeudaEntradasDiarias(listado, listadoAbonos, cliente, fechaDesde, fechaHasta);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al generar el estado de cuenta de todas las empresas", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

        } else {

            String[] datosCliente = consultarCliente(cliente);

            if (datosCliente[1].equalsIgnoreCase("EMPRESAS")) {

                LinkedHashSet<Integer> abonos = new LinkedHashSet<>();
                ArrayList<Object[]> listado = new ArrayList<>();

                String consulta = "select f.idFactura, f.registradoPor, f.monto, f.fechaFactura, c.nombreCliente, ifnull(f.monto-sum(a.abono), f.monto) as saldo, f.condiciondePago\n"
                        + "from facturas f left join abonosfacturas a on f.idFactura=a.factura and a.estado='Activo'\n"
                        + "join clientes c on f.idCliente=c.idCliente and c.nombreCliente=?  \n"
                        + "where f.estado='Activo' and fechaFactura between ? and ?\n"
                        + "group by f.idFactura"
                        + " order by f.idFactura asc";

                Connection cn = Conexion.Conectar();
                try {
                    PreparedStatement pst = cn.prepareStatement(consulta);
                    pst.setString(1, cliente);
                    pst.setString(2, fechaDesde);
                    pst.setString(3, fechaHasta);

                    ResultSet rs = pst.executeQuery();

                    while (rs.next()) {

                        Object[] facturas = new Object[8];

                        facturas[0] = rs.getInt("f.idFactura");
                        facturas[1] = rs.getDate("f.fechaFactura");
                        facturas[2] = rs.getString("c.nombreCliente");
                        facturas[3] = rs.getDouble("f.monto");
                        facturas[4] = "";
                        facturas[5] = rs.getDouble("saldo");
                        facturas[6] = rs.getString("f.condiciondePago");
                        facturas[7] = rs.getString("f.registradoPor");

                        listado.add(facturas);
                        abonos.add((Integer) facturas[0]);

                    }

                    String facturas = "(";

                    for (int i = 0; i < abonos.size(); i++) {
                        if (i != listado.size() - 1) {
                            facturas += String.valueOf((Integer) listado.get(i)[0]) + ",";
                        } else {
                            facturas += String.valueOf((Integer) listado.get(i)[0]) + ")";
                        }
                    }

                    String consultaAbonos = "SELECT idAbono, factura, abono, fecha, observaciones, "
                            + "registradoPor from abonosfacturas where factura in " + facturas + " and estado='Activo' order by idAbono asc";

                    ArrayList<Object[]> listadoAbonos = consultarAbonosClienteEmpresas(consultaAbonos);

                    ImprimirInformeDeudaFacturas(listado, listadoAbonos, cliente, fechaDesde, fechaHasta);

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error al generar el estado de cuenta de todas las empresas", "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }

                //Si es cliente entradas diarias
            } else {

                LinkedHashSet<Integer> abonos = new LinkedHashSet<>();
                ArrayList<Object[]> listado = new ArrayList<>();

                String consulta = "select v.Idventa, v.precio, v.precio, c.nombreCliente, v.registradoPor, v.FechaventaSistema, v.Idcliente, v.Cantidad, concat(v.descripcionTrabajo,' - ' ,v.tamaño) as descripcion, v.FechaventaSistema,\n"
                        + "ifnull(v.precio - SUM(a.valor), v.precio) as saldo\n"
                        + "from ventas v left join abonos a on v.Idventa=a.idVenta and a.estado='Activo'\n"
                        + "left join clientes c on v.Idcliente=c.idCliente \n"
                        + "where v.tipoVenta='Entradas diarias' and v.estado='Activo' and FechaventaSistema between ? and ? and v.Idcliente=?\n"
                        + "group by v.Idventa\n"
                        + "ORDER by v.Idventa asc";

                Connection cn = Conexion.Conectar();

                try {
                    PreparedStatement pst = cn.prepareStatement(consulta);
                    pst.setString(1, fechaDesde);
                    pst.setString(2, fechaHasta);
                    pst.setString(3, datosCliente[0]);

                    ResultSet rs = pst.executeQuery();

                    while (rs.next()) {

                        Object[] nuevo = new Object[7];
                        nuevo[0] = rs.getInt("v.Idventa");
                        nuevo[1] = rs.getDate("v.FechaventaSistema");
                        nuevo[2] = rs.getString("nombreCliente");
                        nuevo[3] = rs.getString("descripcion");
                        nuevo[4] = rs.getDouble("v.precio");
                        nuevo[5] = rs.getString("v.registradoPor");
                        nuevo[6] = rs.getDouble("saldo");

                        listado.add(nuevo);

                        abonos.add((Integer) nuevo[0]);

                    }

                    String ventas = "(";

                    for (int i = 0; i < abonos.size(); i++) {
                        if (i != listado.size() - 1) {
                            ventas += String.valueOf((Integer) listado.get(i)[0]) + ",";
                        } else {
                            ventas += String.valueOf((Integer) listado.get(i)[0]) + ")";
                        }
                    }

                    String consultaAbonos = "select a.idAbono, a.idVenta, c.nombreCliente, a.fecha, a.valor, a.observaciones, a.registradoPor\n"
                            + "from abonos a left join ventas v on a.idVenta=v.idVenta\n"
                            + "left join clientes c on v.idCliente=c.idCliente\n"
                            + "where a.idVenta in " + ventas + " and a.estado='Activo' order by a.idAbono asc";

                    ArrayList<Object[]> listadoAbonos = consultarAbonosEntradasDiarias(consultaAbonos);

                    ImprimirInformeDeudaClienteEntradasDiarias(listado, listadoAbonos, cliente, fechaDesde, fechaHasta);

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error al generar el estado de cuenta de todas las empresas", "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }

            }

        }

    }

    public ArrayList<Object[]> consultarAbonosClienteEmpresas(String consultaAbonos) {

        ArrayList<Object[]> listado = new ArrayList<>();

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consultaAbonos);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[9];
                nuevo[0] = rs.getInt("idAbono");
                nuevo[1] = rs.getDate("fecha");
                nuevo[2] = "";
                nuevo[3] = "";
                nuevo[4] = rs.getDouble("abono");
                nuevo[5] = "";
                nuevo[6] = rs.getString("observaciones");
                nuevo[7] = rs.getString("registradoPor");
                nuevo[8] = rs.getInt("factura");

                listado.add(nuevo);

            }

            cn.close();
            return listado;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los abonos de las facturas", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;

    }

    public void ImprimirInformeDeudaEntradasDiarias(ArrayList<Object[]> listado, ArrayList<Object[]> listadoAbonos, String cliente, String fechaDesde, String fechaHasta) {

        String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Estado de cuenta Entradas Diarias.xlsx";
        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Estado de cuenta Entradas Diarias.xlsx";

        try {

            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();

            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            String cabecera = (cliente.equals("ENTRADAS DIARIAS") ? "ESTADO DE CUENTA / DEUDAS - TODAS LAS ENTRADAS DIARIAS"
                    + " DESDE " + fechaDesde + " HASTA " + fechaHasta
                    : "ESTADO DE CUENTA - CLIENTE: " + cliente);

            XSSFRow fila0 = hoja.getRow(0);
            XSSFCell celda00 = fila0.getCell(0);
            celda00.setCellValue(cabecera);

            //Completamos los datos del usuario y fecha            
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda11 = fila1.getCell(2);
            celda11.setCellValue(new Date());

            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda21 = fila2.getCell(2);
            celda21.setCellValue(this.usuario);

            //Dado que la informacion se empezara a cargar desde la fila 5, establecemos ese inicio
            int filaInicio = 5;
            //Recorremos el numero de filas que tiene la tabla

            double totalVenta = 0;
            double totalAbonos = 0;
            for (Object[] elemento : listado) {
                XSSFRow fila = hoja.getRow(filaInicio);
                //Recorremos ahora las columas para obtener los datos
                //System.out.println("Tamaño: " + elemento.length);
                for (int i = 0; i < 8; i++) {
                    XSSFCell celda = fila.getCell(i);
                    //En funcion de la columna en la que nos encontremos se tomará el tipo de dato para el reporte
                    switch (i) {
                        case 0:
                            celda.setCellValue((Integer) elemento[i]);
                            break;
                        case 1:
                            celda.setCellValue((Date) elemento[i]);
                            break;
                        case 2:
                            celda.setCellValue((String) elemento[i]);
                            break;
                        case 3:
                            celda.setCellValue((String) elemento[i]);
                            break;
                        case 4:
                            celda.setCellValue((Double) elemento[i]);
                            totalVenta += (Double) elemento[i];
                            break;
                        case 6:
                            celda.setCellValue((Double) elemento[i]);
                            break;
                        case 7:
                            celda.setCellValue((String) elemento[5]);
                            break;
                    }
                    //filaInicio++;
                }

                for (Object[] elemento2 : listadoAbonos) {
                    if (elemento2[0].equals(elemento[0])) {
                        XSSFRow filaAbono = hoja.getRow(++filaInicio);
                        for (int k = 0; k < 8; k++) {
                            XSSFCell celdaAbono = filaAbono.getCell(k);
                            switch (k) {
                                case 0:
                                    celdaAbono.setCellValue((Integer) elemento2[k]);
                                    break;
                                case 1:
                                    celdaAbono.setCellValue((Date) elemento2[k]);
                                    break;
                                case 2:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 3:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 4:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 5:
                                    celdaAbono.setCellValue((Double) elemento2[k]);
                                    totalAbonos += (Double) elemento2[k];
                                    break;
                                case 6:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 7:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;

                            }

                        }

                    }

                }
                filaInicio++;

            }

            filaInicio++;

            XSSFRow filaSubtotales = hoja.getRow(filaInicio);
            XSSFCell textoSubt = filaSubtotales.getCell(3);
            textoSubt.setCellValue("TOTALES");
            XSSFCell totVenta = filaSubtotales.getCell(4);
            totVenta.setCellValue(totalVenta);
            XSSFCell totAbono = filaSubtotales.getCell(5);
            totAbono.setCellValue(totalAbonos);
            XSSFCell saldo = filaSubtotales.getCell(6);
            saldo.setCellValue(totalVenta - totalAbonos);

            XSSFCell celda24 = fila2.getCell(5);
            celda24.setCellValue(totalVenta - totalAbonos);

            FileOutputStream nuevo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(nuevo);
            nuevo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error al crear el documento. Un documento con el mismo nombre esta abierto."
                    + "\nCierrelo e intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error en generar el reporte de deuda de Empresas ", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public void ImprimirInformeDeudaClienteEntradasDiarias(ArrayList<Object[]> listado, ArrayList<Object[]> listadoAbonos, String cliente, String fechaDesde, String fechaHasta) {

        String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Estado de cuenta Entradas Diarias.xlsx";
        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Estado de cuenta Entradas Diarias.xlsx";

        try {

            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();

            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            String cabecera = "ESTADO DE CUENTA - CLIENTE: " + cliente + " DESDE " + fechaDesde + " HASTA " + fechaHasta;

            XSSFRow fila0 = hoja.getRow(0);
            XSSFCell celda00 = fila0.getCell(0);
            celda00.setCellValue(cabecera);

            //Completamos los datos del usuario y fecha            
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda11 = fila1.getCell(2);
            celda11.setCellValue(new Date());

            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda21 = fila2.getCell(2);
            celda21.setCellValue(this.usuario);

            //Dado que la informacion se empezara a cargar desde la fila 5, establecemos ese inicio
            int filaInicio = 5;
            //Recorremos el numero de filas que tiene la tabla

            double totalVenta = 0;
            double totalAbonos = 0;
            for (Object[] elemento : listado) {
                XSSFRow fila = hoja.getRow(filaInicio);
                //Recorremos ahora las columas para obtener los datos
                //System.out.println("Tamaño: " + elemento.length);
                for (int i = 0; i < 8; i++) {
                    XSSFCell celda = fila.getCell(i);
                    //En funcion de la columna en la que nos encontremos se tomará el tipo de dato para el reporte
                    switch (i) {
                        case 0:
                            celda.setCellValue((Integer) elemento[i]);
                            break;
                        case 1:
                            celda.setCellValue((Date) elemento[i]);
                            break;
                        case 2:
                            celda.setCellValue((String) elemento[i]);
                            break;
                        case 3:
                            celda.setCellValue((String) elemento[i]);
                            break;
                        case 4:
                            celda.setCellValue((Double) elemento[i]);
                            totalVenta += (Double) elemento[i];
                            break;
                        case 7:
                            celda.setCellValue((String) elemento[5]);
                            break;
                    }
                    //filaInicio++;
                }

                for (Object[] elemento2 : listadoAbonos) {
                    if (elemento2[0].equals(elemento[0])) {
                        XSSFRow filaAbono = hoja.getRow(++filaInicio);
                        for (int k = 0; k < 8; k++) {
                            XSSFCell celdaAbono = filaAbono.getCell(k);
                            switch (k) {
                                case 0:
                                    celdaAbono.setCellValue((Integer) elemento2[k]);
                                    break;
                                case 1:
                                    celdaAbono.setCellValue((Date) elemento2[k]);
                                    break;
                                case 2:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 3:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 4:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 5:
                                    celdaAbono.setCellValue((Double) elemento2[k]);
                                    totalAbonos += (Double) elemento2[k];
                                    break;
                                case 6:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 7:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;

                            }

                        }

                    }

                }
                filaInicio++;

            }

            filaInicio++;

            XSSFRow filaSubtotales = hoja.getRow(filaInicio);
            XSSFCell textoSubt = filaSubtotales.getCell(3);
            textoSubt.setCellValue("TOTALES");
            XSSFCell totVenta = filaSubtotales.getCell(4);
            totVenta.setCellValue(totalVenta);
            XSSFCell totAbono = filaSubtotales.getCell(5);
            totAbono.setCellValue(totalAbonos);
            XSSFCell saldo = filaSubtotales.getCell(6);
            saldo.setCellValue(totalVenta - totalAbonos);

            XSSFCell celda24 = fila2.getCell(5);
            celda24.setCellValue(totalVenta - totalAbonos);

            FileOutputStream nuevo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(nuevo);
            nuevo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error al crear el documento. Un documento con el mismo nombre esta abierto."
                    + "\nCierrelo e intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error en generar el reporte de deuda de Empresas ", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public ArrayList<Object[]> consultarAbonosEntradasDiarias(String consultaAbonos) {

        ArrayList<Object[]> listado = new ArrayList<>();
        Connection cn = Conexion.Conectar();

        try {

            PreparedStatement pst2 = cn.prepareStatement(consultaAbonos);
            ResultSet rs2 = pst2.executeQuery();

            while (rs2.next()) {

                Object[] nuevo = new Object[8];
                nuevo[0] = rs2.getInt("idVenta");
                nuevo[1] = rs2.getDate("fecha");
                nuevo[2] = rs2.getString("c.nombreCliente");
                nuevo[3] = "Abono No. " + rs2.getString("idAbono") + " " + rs2.getString("observaciones");
                nuevo[4] = "";
                nuevo[5] = rs2.getDouble("valor");
                nuevo[6] = "";
                nuevo[7] = rs2.getString("registradoPor");

                listado.add(nuevo);
            }

            cn.close();
            return listado;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los abonos de entradas diarias", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public String[] consultarCliente(String cliente) {

        String consulta = "select idCliente, tipoCliente from clientes where nombreCliente='" + cliente + "'";

        //System.out.println(consulta);
        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consulta);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return new String[]{rs.getString("idCliente"), rs.getString("tipoCliente")};
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el id y tipo del cliente", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Object[]> consultarAbonos(String consultaAbonos) {

        ArrayList<Object[]> listado = new ArrayList<>();

        Connection cn = Conexion.Conectar();
        try {
            PreparedStatement pst = cn.prepareStatement(consultaAbonos);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Object[] nuevo = new Object[9];
                nuevo[0] = rs.getInt("idAbono");
                nuevo[1] = rs.getDate("fecha");
                nuevo[2] = "";
                nuevo[3] = "";
                nuevo[4] = rs.getDouble("abono");
                nuevo[5] = "";
                nuevo[6] = rs.getString("observaciones");
                nuevo[7] = rs.getString("registradoPor");
                nuevo[8] = rs.getInt("factura");

                listado.add(nuevo);

            }

            cn.close();
            return listado;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al consultar los abonos de las facturas", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return null;
    }

    public void ImprimirInformeDeudaFacturas(ArrayList<Object[]> listado, ArrayList<Object[]> listadoAbonos, String cliente, String fechaDesde, String fechaHasta) {

        String rutaParaGuardar = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Estado de cuenta Empresas.xlsx";
        String rutaArchivoACopiar = "C:" + File.separator + "Gestion" + File.separator + "Docs" + File.separator + "Estado de cuenta Empresas.xlsx";

        try {
            FileInputStream archivoAModificar = new FileInputStream(rutaArchivoACopiar);
            XSSFWorkbook nuevoLibro = new XSSFWorkbook(archivoAModificar);
            archivoAModificar.close();

            XSSFSheet hoja = nuevoLibro.getSheetAt(0);

            String texto = "";

            if (cliente.equalsIgnoreCase("EMPRESAS")) {
                texto = "ESTADO DE CUENTA - TODAS LAS EMPRESAS DESDE " + fechaDesde + " HASTA " + fechaHasta;
            } else {
                texto = "ESTADO CUENTA - EMPRESA: " + cliente + " DESDE " + fechaDesde + " HASTA " + fechaHasta;
            }

            XSSFRow fila0 = hoja.getRow(0);
            XSSFCell celda00 = fila0.getCell(0);
            celda00.setCellValue(texto);

            //Completamos los datos del usuario y fecha            
            XSSFRow fila1 = hoja.getRow(1);
            XSSFCell celda11 = fila1.getCell(2);
            celda11.setCellValue(new Date());

            XSSFRow fila2 = hoja.getRow(2);
            XSSFCell celda21 = fila2.getCell(2);
            celda21.setCellValue(this.usuario);

            //Dado que la informacion se empezara a cargar desde la fila 5, establecemos ese inicio
            int filaInicio = 5;
            //Recorremos el numero de filas que tiene la tabla

            double totalVenta = 0;
            double totalAbonos = 0;

            for (Object[] elemento : listado) {
                XSSFRow fila = hoja.getRow(filaInicio);
                //Recorremos ahora las columas para obtener los datos
                for (int i = 0; i < 8; i++) {
                    XSSFCell celda = fila.getCell(i);
                    //En funcion de la columna en la que nos encontremos se tomará el tipo de dato para el reporte
                    switch (i) {
                        case 0:
                            celda.setCellValue((Integer) elemento[i]);
                            break;
                        case 1:
                            celda.setCellValue((Date) elemento[i]);
                            break;
                        case 2:
                            celda.setCellValue((String) elemento[i]);
                            break;
                        case 3:
                            celda.setCellValue((Double) elemento[i]);
                            totalVenta += (Double) elemento[i];
                            break;
                        case 4:
                            celda.setCellValue("");
                            break;
                        case 5:
                            celda.setCellValue((Double) elemento[i]);
                            break;
                        case 6:
                            celda.setCellValue("Cond. Pago: " + (String) elemento[i]);
                            break;
                        case 7:
                            celda.setCellValue((String) elemento[i]);
                            break;
                    }
                }

                for (Object[] elemento2 : listadoAbonos) {
                    if (elemento2[8].equals(elemento[0])) {
                        XSSFRow filaAbono = hoja.getRow(++filaInicio);
                        for (int k = 0; k < 8; k++) {
                            XSSFCell celdaAbono = filaAbono.getCell(k);
                            switch (k) {
                                case 0:
                                    celdaAbono.setCellValue("Abono No. " + String.valueOf((Integer) elemento2[k]));
                                    break;
                                case 1:
                                    celdaAbono.setCellValue((Date) elemento2[k]);
                                    break;
                                case 2:
                                    celdaAbono.setCellValue((String) elemento[2]);
                                    break;
                                case 3:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 4:
                                    celdaAbono.setCellValue((Double) elemento2[k]);
                                    totalAbonos += (Double) elemento2[k];
                                    break;
                                case 5:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    //totalAbonos += (Double) elemento2[k];
                                    break;
                                case 6:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;
                                case 7:
                                    celdaAbono.setCellValue((String) elemento2[k]);
                                    break;

                            }

                        }

                    }

                }

                filaInicio++;
            }

            filaInicio++;

            XSSFRow filaSubtotales = hoja.getRow(filaInicio);
            XSSFCell textoSubt = filaSubtotales.getCell(2);
            textoSubt.setCellValue("TOTALES");
            XSSFCell totVenta = filaSubtotales.getCell(3);
            totVenta.setCellValue(totalVenta);
            XSSFCell totAbono = filaSubtotales.getCell(4);
            totAbono.setCellValue(totalAbonos);
            XSSFCell saldo = filaSubtotales.getCell(5);
            saldo.setCellValue(totalVenta - totalAbonos);

            XSSFCell celda24 = fila2.getCell(4);
            celda24.setCellValue(totalVenta - totalAbonos);

            FileOutputStream nuevo = new FileOutputStream(rutaParaGuardar);
            nuevoLibro.write(nuevo);
            nuevo.close();

            MetodosGenerales.abrirArchivo(rutaParaGuardar);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error al crear el documento. Un documento con el mismo nombre esta abierto."
                    + "\nCierrelo e intentelo nuevamente", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error en generar el reporte de deuda de Empresas ", "Error", JOptionPane.ERROR_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jDateChooser_desde = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser_hasta = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jComboBox_clienteVentas = new javax.swing.JComboBox<>();
        jButton_generarVentas = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jComboBox_clienteIngresos = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jButton_generarIngresos = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jComboBox_tipoTrabajo = new javax.swing.JComboBox<>();
        jButton_tipoTrabajo = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox_tipoTrabajoTiempo = new javax.swing.JComboBox<>();
        jButton_tipoTrabajoTiempo = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox_estadocuenta = new javax.swing.JComboBox<>();
        jButton_estadodeCuenta = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Rango de fechas del informe", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel1.setText("Desde");

        jDateChooser_desde.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jDateChooser_desdeKeyTyped(evt);
            }
        });

        jLabel2.setText("Hasta");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jDateChooser_desde, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jDateChooser_hasta, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jDateChooser_desde, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jDateChooser_hasta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informe de ventas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel4.setText("Tipo de cliente");

        jComboBox_clienteVentas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS", "EMPRESAS", "ENTRADAS DIARIAS" }));

        jButton_generarVentas.setText("Generar");
        jButton_generarVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generarVentasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jComboBox_clienteVentas, 0, 241, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_generarVentas)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox_clienteVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jButton_generarVentas))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informe de ingresos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jComboBox_clienteIngresos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS", "EMPRESAS", "ENTRADAS DIARIAS" }));

        jLabel5.setText("Tipo de cliente");

        jButton_generarIngresos.setText("Generar");
        jButton_generarIngresos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generarIngresosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jComboBox_clienteIngresos, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_generarIngresos)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_generarIngresos)
                    .addComponent(jComboBox_clienteIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informe de venta por tipo de trabajo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel6.setText("Trabajo");

        jComboBox_tipoTrabajo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));

        jButton_tipoTrabajo.setText("Generar");
        jButton_tipoTrabajo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_tipoTrabajoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jComboBox_tipoTrabajo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_tipoTrabajo)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_tipoTrabajo)
                    .addComponent(jComboBox_tipoTrabajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Informe de tiempos de produccion", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel7.setText("Tiempos de produccion");

        jComboBox_tipoTrabajoTiempo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS" }));

        jButton_tipoTrabajoTiempo.setText("Generar");
        jButton_tipoTrabajoTiempo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_tipoTrabajoTiempoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(jComboBox_tipoTrabajoTiempo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_tipoTrabajoTiempo)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_tipoTrabajoTiempo)
                    .addComponent(jComboBox_tipoTrabajoTiempo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Estados de cuenta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));

        jLabel3.setText("Tipo de cliente");

        jComboBox_estadocuenta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "EMPRESAS", "ENTRADAS DIARIAS" }));

        jButton_estadodeCuenta.setText("Generar");
        jButton_estadodeCuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_estadodeCuentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox_estadocuenta, 0, 252, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton_estadodeCuenta)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox_estadocuenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_estadodeCuenta))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 32, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_generarVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generarVentasActionPerformed
        //Verificamos que las fechas se hayan seleccionado
        try {

            String fechaDesde = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_desde.getDate());
            String fechaHasta = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_hasta.getDate());
            String tipoCliente = jComboBox_clienteVentas.getSelectedItem().toString();

            GenerarInformeVentas(fechaDesde, fechaHasta, tipoCliente, this.usuario);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Seleccione las fechas correctas", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton_generarVentasActionPerformed

    private void jButton_generarIngresosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generarIngresosActionPerformed
        //Verificamos que las fechas se hayan seleccionado
        try {

            String fechaDesde = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_desde.getDate());
            String fechaHasta = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_hasta.getDate());
            String tipoCliente = jComboBox_clienteIngresos.getSelectedItem().toString();

            GenerarInformeIngresos(fechaDesde, fechaHasta, tipoCliente, this.usuario);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Seleccione las fechas correctas", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton_generarIngresosActionPerformed

    private void jButton_tipoTrabajoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_tipoTrabajoActionPerformed

        //Verificamos que las fechas se hayan seleccionado
        try {

            String fechaDesde = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_desde.getDate());
            String fechaHasta = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_hasta.getDate());
            String tipotrabajo = jComboBox_tipoTrabajo.getSelectedItem().toString();

            GenerarInformeTrabajosMasVendidos(fechaDesde, fechaHasta, tipotrabajo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Seleccione las fechas correctas", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton_tipoTrabajoActionPerformed

    private void jDateChooser_desdeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jDateChooser_desdeKeyTyped

    }//GEN-LAST:event_jDateChooser_desdeKeyTyped

    private void jButton_tipoTrabajoTiempoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_tipoTrabajoTiempoActionPerformed

        //Verificamos que las fechas se hayan seleccionado
        try {
            String fechaDesde = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_desde.getDate());
            String fechaHasta = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_hasta.getDate());
            String tipotrabajo = jComboBox_tipoTrabajoTiempo.getSelectedItem().toString();

            CalcularTiempoPromedioDeProduccion(fechaDesde, fechaHasta, tipotrabajo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Seleccione las fechas correctas", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton_tipoTrabajoTiempoActionPerformed

    private void jButton_estadodeCuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_estadodeCuentaActionPerformed

        try {
            String fechaDesde = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_desde.getDate());
            String fechaHasta = new SimpleDateFormat("yyyy-MM-dd").format(jDateChooser_hasta.getDate());
            String cliente = jComboBox_estadocuenta.getSelectedItem().toString();

            GenerarInforme(cliente, fechaDesde, fechaHasta);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Seleccione las fechas correctas", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }


    }//GEN-LAST:event_jButton_estadodeCuentaActionPerformed

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
            java.util.logging.Logger.getLogger(InfomeVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InfomeVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InfomeVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InfomeVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InfomeVentas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_estadodeCuenta;
    private javax.swing.JButton jButton_generarIngresos;
    private javax.swing.JButton jButton_generarVentas;
    private javax.swing.JButton jButton_tipoTrabajo;
    private javax.swing.JButton jButton_tipoTrabajoTiempo;
    private javax.swing.JComboBox<String> jComboBox_clienteIngresos;
    private javax.swing.JComboBox<String> jComboBox_clienteVentas;
    private javax.swing.JComboBox<String> jComboBox_estadocuenta;
    private javax.swing.JComboBox<String> jComboBox_tipoTrabajo;
    private javax.swing.JComboBox<String> jComboBox_tipoTrabajoTiempo;
    private com.toedter.calendar.JDateChooser jDateChooser_desde;
    private com.toedter.calendar.JDateChooser jDateChooser_hasta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    // End of variables declaration//GEN-END:variables
}

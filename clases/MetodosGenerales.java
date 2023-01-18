package clases;

import java.awt.Desktop;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

public class MetodosGenerales {

    public static String ConvertirPrimeraLetraMayus(String texto) {

        String primera = texto.substring(0, 1);
        String restantes = texto.substring(1, texto.length());

        primera = primera.toUpperCase();
        String nuevoTexto = primera + restantes;

        return nuevoTexto;
    }

    public static String ConvertirPrimerasLetrasMayus(String texto) {

        char[] nuevotexto = texto.toCharArray();

        for (int i = 0; i < nuevotexto.length - 1; i++) {

            if (i == 0) {
                nuevotexto[i] = Character.toUpperCase(nuevotexto[i]);
            } else if (nuevotexto[i] == ' ') {
                nuevotexto[i + 1] = Character.toUpperCase(nuevotexto[i + 1]);

            }
        }
        String textoCambiado = "";
        for (int i = 0; i < nuevotexto.length; i++) {
            textoCambiado += nuevotexto[i];
        }
        return textoCambiado;

    }

    public static boolean validacionCriterioRegistroUsuario(String tipoPermiso, String tipoPermisoARegistrar) {

        boolean bandera = false;
        if (tipoPermiso.equals(tipoPermisoARegistrar)) {
            bandera = true;
        }
        return bandera;
    }

    public static ArrayList<Integer> CopiarArchivo(String rutaArchivoACopiar) {

        ArrayList<Integer> nuevo = new ArrayList<Integer>();

        //"D:/Erwin/ApacheNetbeans/GraficasJireh_1/OT.xlsx"
        try {
            FileInputStream OT = new FileInputStream(rutaArchivoACopiar);
            int byteArchivoEntrada = OT.read();

            while (byteArchivoEntrada != -1) {
                nuevo.add(byteArchivoEntrada);
                byteArchivoEntrada = OT.read();
            }

            OT.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return nuevo;
    }

    public static String crearOT(ArrayList<Integer> nuevo, String rutaParaGuardar, String Idventa, String Cliente) {

        String ruta = rutaParaGuardar + "/" + "OT - IdVenta  " + Idventa + " - Cliente " + Cliente + ".xlsx";

        //C:/Users/erwin/Desktop
        try {

            FileOutputStream nuevaOT = new FileOutputStream(ruta);

            for (Integer integer : nuevo) {
                nuevaOT.write(integer);
            }

            nuevaOT.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return ruta;
    }

    public static String crearListadoPendientes(ArrayList<Integer> nuevo, String rutaParaGuardar) {

        String ruta = rutaParaGuardar + "/Listado pendientes.xlsx";

        //C:/Users/erwin/Desktop
        try {

            FileOutputStream Listado = new FileOutputStream(ruta);

            for (Integer integer : nuevo) {
                Listado.write(integer);
            }

            Listado.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return ruta;
    }

    public static String crearReciboAbono(ArrayList<Integer> nuevo, String rutaParaGuardar) {

        String ruta = rutaParaGuardar + "/Recibo.xlsx";

        //C:/Users/erwin/Desktop
        try {

            FileOutputStream Listado = new FileOutputStream(ruta);

            for (Integer integer : nuevo) {
                Listado.write(integer);
            }

            Listado.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return ruta;
    }

    public static String crearFactura(ArrayList<Integer> nuevo, String rutaParaGuardar) {

        String ruta = rutaParaGuardar + "/Factura.xlsx";

        //C:/Users/erwin/Desktop
        try {

            FileOutputStream Listado = new FileOutputStream(ruta);

            for (Integer integer : nuevo) {
                Listado.write(integer);
            }

            Listado.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return ruta;
    }

    //Este metodo abre el archivo excel generado con la informacion de la OT
    public static void abrirArchivo(String rutaDocumento) {
        try {

            //Objeto de tipo File y como argumento la ruta.
            File archivoAAbrir = new File(rutaDocumento);
            boolean estaAbierto = false; //Asumimos de entrada que el archivo esta siendo usado por otro programo

            try {

                FileUtils.touch(archivoAAbrir); //Verificamos si el archivo esta abierto por o no por otro programo
                estaAbierto = true;

            } catch (IOException e) {
                estaAbierto = false;
                JOptionPane.showMessageDialog(null, "El archivo que intentas sobreescribir se encuentra abierto o esta siendo usado por otra aplicacion");
            }

            if (estaAbierto = true) {
                Desktop.getDesktop().open(archivoAAbrir);
            }

        } catch (HeadlessException | IOException e) {
            JOptionPane.showMessageDialog(null, "No se ha podido abrir el archivo. Asegurese que el archivo exista");
        }

    }

    public static void cambiarFecha() {
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat nueva = new SimpleDateFormat("dd-MM-yyyy");
        String strFecha = "2007-12-25";
        Date fecha = null;

        try {

            fecha = formatoDelTexto.parse(strFecha);
            SimpleDateFormat formatear = new SimpleDateFormat("dd-MM-yyyy");
            String fechanueva = formatear.format(fecha);

            System.out.println(fechanueva);

        } catch (ParseException ex) {

            ex.printStackTrace();

        }

    }

    public static long RestarFechas(String primerafecha, String segundafecha) {
        long diferencia = 0;
        try {
            Date primera = new SimpleDateFormat("yyyy-MM-dd").parse(primerafecha);
            Date segunda = new SimpleDateFormat("yyyy-MM-dd").parse(segundafecha);

            long dif = segunda.getTime() - primera.getTime();
            TimeUnit tiempo = TimeUnit.DAYS;
            diferencia = tiempo.convert(dif, TimeUnit.MILLISECONDS);

        } catch (Exception e) {

            e.printStackTrace();
        }
        return diferencia;
    }

    public static String ConvertirIntAMoneda(double dato) {

        if (dato >= 0.0) {
            String result = "";
            DecimalFormat objDF = new DecimalFormat("$ ###, ### .##");
            result = objDF.format(dato);

            return result;
        } else {
            
            String result = "";
            DecimalFormat objDF = new DecimalFormat("$ ###, ### .##");
            result = objDF.format(dato);

            return "- "+result;
            
        }

    }

    public static String ConvertirMonedaAInt(String numero) {
        String MonedaParseada = "";

        try {
            MonedaParseada = new DecimalFormat("$ ###, ### .##").parse(numero).toString();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return MonedaParseada;
    }

    public static String ConvertirIntAMonedaSinSimbolo(double dato) {
        String result = "";
        DecimalFormat objDF = new DecimalFormat("###, ### .##");
        result = objDF.format(dato);

        return result;
    }

    public static String ConvertirMonedaAIntSinSimbolo(String numero) {
        String MonedaParseada = "";

        try {
            MonedaParseada = new DecimalFormat("###, ### .##").parse(numero).toString();
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return MonedaParseada;
    }

    public static void enviarEmail(String asunto, String textoMensaje) throws AddressException, MessagingException {

        String remitente = "infograficasjireh@gmail.com";
        String contraseña = "enuupmhmgcebjubf";
        //String destinatario = "eperez.alean@gmail.com";
        String destinatario = "infograficasjireh@gmail.com";

        Properties p = new Properties();
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.setProperty("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        p.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        p.setProperty("mail.smtp.port", "587");
        p.setProperty("mail.smtp.user", remitente);
        p.setProperty("mail.smtp.auth", "true");

        Session s = Session.getDefaultInstance(p);
        MimeMessage mensaje = new MimeMessage(s);
        mensaje.setFrom(new InternetAddress(remitente));
        mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
        mensaje.setSubject(asunto);
        mensaje.setText(textoMensaje);

        Transport t = s.getTransport("smtp");
        t.connect(remitente, contraseña);
        t.sendMessage(mensaje, mensaje.getAllRecipients());
        t.close();

    }

    public static void enviarEmailConAdjunto(String asunto, String textoMensaje, String ruta, String nombreArchivo) throws AddressException, MessagingException {

        String remitente = "infograficasjireh@gmail.com";
        String contraseña = "enuupmhmgcebjubf";
        //String destinatario = "eperez.alean@gmail.com";
        String destinatario = "infograficasjireh@gmail.com";
        
        Properties p = new Properties();
        p.put("mail.smtp.host", "smtp.gmail.com");
        p.setProperty("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        p.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        p.setProperty("mail.smtp.port", "587");
        p.setProperty("mail.smtp.user", remitente);
        p.setProperty("mail.smtp.auth", "true");

        Session s = Session.getDefaultInstance(p);
        BodyPart texto = new MimeBodyPart();
        texto.setText("Informe enviado automaticamente por Software Gestion");
        BodyPart adjunto = new MimeBodyPart();
        adjunto.setDataHandler(new DataHandler(new FileDataSource(ruta)));
        adjunto.setFileName(nombreArchivo);
        MimeMultipart m = new MimeMultipart();
        m.addBodyPart(texto);
        m.addBodyPart(adjunto);

        MimeMessage mensaje = new MimeMessage(s);
        mensaje.setFrom(new InternetAddress(remitente));
        mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
        mensaje.setSubject(asunto);
        mensaje.setContent(m);

        Transport t = s.getTransport("smtp");
        t.connect(remitente, contraseña);
        t.sendMessage(mensaje, mensaje.getAllRecipients());
        t.close();

    }

    public static void registrarHistorial(String usuario, String operacion) {

        String consulta3 = "insert into desconexion (gestion, usuario, fecha) values (?, ?, ?)";

        Connection cn3 = Conexion.Conectar();
        try {
            PreparedStatement pst3 = cn3.prepareStatement(consulta3);
            pst3.setString(1, operacion);
            pst3.setString(2, usuario);
            pst3.setString(3, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            pst3.executeUpdate();
            cn3.close();

        } catch (SQLException exe) {
            exe.printStackTrace();
        }

    }

    public static String encriptarContraseña(String contraseña) {

        return DigestUtils.md5Hex(contraseña).substring(0, 10);

    }

    public static void main(String[] args) {

        //System.out.println(encriptarContraseña("Luis"));
        String frase="(PROVISIONAL) UTILIDAD DEL PERIODO ANTERIOR 4 - SEPTIEMBRE 2022";
        System.out.println(frase.substring(0, 13));
    }

}

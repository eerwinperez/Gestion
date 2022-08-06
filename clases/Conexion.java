package clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.swing.JOptionPane;

public class Conexion {

    public static Connection Conectar() {

        try {
            //String url = "jdbc:sqlite:D:\\Erwin\\ApacheNetbeans\\GraficasJireh\\Base de datos\\BD_Jireh.db";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/jireh", "root", "admin");
            //JOptionPane.showMessageDialog(null, "Conexion exitosa");
            //System.out.println("Exitosa");
            return conexion;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return (null);

//    }
//    Connection conn = null;
//    String usuario = "root";
//    String contrasenia = "admin";
//    String basededatos = "jireh";
//    String ip = "localhost";
//    String puerto = "3306";
//
//    String cadena = "jdbc:mysql://" + ip + ":" + puerto + "/" + basededatos;
//
//    public Connection Conectar() {
//
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            conn = DriverManager.getConnection(cadena, usuario, contrasenia);
//            JOptionPane.showMessageDialog(null, "Conexion exitosa");
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null, "No se conector a la base de datos" + e.toString());
//        }
//
//        return conn;
//    }
    }
    
//    public static void main(String[] args) {
//        Conexion.Conectar();
//    }

}

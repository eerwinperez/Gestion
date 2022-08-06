package clases;

import java.awt.HeadlessException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Clientes {

    private int Id;
    private String nombreCliente;
    private String identificacion;
    private String tipoCliente;
    private String direccion;
    private String municipio;
    private String telefono;
    private String sector;
    private String email;

    public Clientes(String nombreCliente, String identificacion, String direccion, String municipio, String telefono, String email) {
        this.nombreCliente = nombreCliente;
        this.identificacion = identificacion;
        this.tipoCliente=tipoCliente;
        this.direccion = direccion;
        this.municipio = municipio;
        this.telefono = telefono;
        this.sector = sector;
        this.email=email;

    }

    public Clientes(int Id, String nombreCliente, String identificacion, String direccion, String municipio, String telefono, String email) {
        this.Id = Id;
        this.nombreCliente = nombreCliente;
        this.identificacion = identificacion;
        this.tipoCliente=tipoCliente;
        this.direccion = direccion;
        this.municipio = municipio;
        this.telefono = telefono;
        this.sector = sector;
        this.email=email;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void CrearCliente(String usuarioQueRegistra) {

        try {

            String consulta = "insert into clientes (nombreCliente, identificacion, direccion, municipio, "
                    + "telefono, email, registradoPor) values (?,  ?, ?, ?, ?, ?, ?)";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, this.nombreCliente);
            pst.setString(2, this.identificacion);
            //pst.setString(3, this.tipoCliente);
            pst.setString(3, this.direccion);
            pst.setString(4, this.municipio);
            pst.setString(5, this.telefono);
            pst.setString(6, this.email);
            pst.setString(7, usuarioQueRegistra);
            
            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(null, "Cliente registrado");

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "El cliente no ha sido registrado. Si el problema persiste, contacte al administrador Cliente CrearCliente()");
            e.printStackTrace();
        }

    }

    public void ActualizarCliente(String usuarioQueActualiza) {

        try {

            String consulta = "update clientes set nombreCliente=?, identificacion=?, direccion=?, municipio=?, "
                    + "telefono=?, sector=?, email=?, registradoPor=? where idCliente =?";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, this.nombreCliente);
            pst.setString(2, this.identificacion);
            //pst.setString(3, this.tipoCliente);
            pst.setString(3, this.direccion);
            pst.setString(4, this.municipio);
            pst.setString(5, this.telefono);
            pst.setString(6, this.sector);
            pst.setString(7, this.email);
            pst.setString(8, usuarioQueActualiza);
            pst.setInt(9, this.Id);
            
            
            
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Cliente actualizado");
            
        } catch (Exception e) {
            
            JOptionPane.showMessageDialog(null, "El cliente no ha sido actualizado. Contacte al administrador");
            e.printStackTrace();
        }

    }

    

}

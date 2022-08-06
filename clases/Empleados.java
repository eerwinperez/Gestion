package clases;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import javax.swing.JOptionPane;

public class Empleados {

    private String nombreCompleto;
    private String usuario;
    private String cargo;
    private String tipoPermiso;
    private String estado;
    private String clave;

    //METODO CONSTRUCTOR
    public Empleados(String nombreCompleto, String usuario, String cargo, String tipoPermiso, String estado, String clave) {
        this.nombreCompleto = nombreCompleto;
        this.usuario = usuario;
        this.cargo = cargo;
        this.tipoPermiso = tipoPermiso;
        this.estado = estado;
        this.clave = clave;
    }

    public Empleados(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public Empleados(String nombreCompleto, String usuario, String cargo, String tipoPermiso, String estado) {
        this.nombreCompleto = nombreCompleto;
        this.usuario = usuario;
        this.cargo = cargo;
        this.tipoPermiso = tipoPermiso;
        this.estado = estado;
    }
    
    

    //METODOS GETTERS AND SETTERS
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getTipoPermiso() {
        return tipoPermiso;
    }

    public void setTipoPermiso(String tipoPermiso) {
        this.tipoPermiso = tipoPermiso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    //METODOS RELACIONADOS CON LA CARGA EN LA BASE DE DATOS
    public void IngresarEmpleado(String usuarioqueRegistra) {

        try {

            String consulta = "insert into empleados (nombreCompleto, usuario , cargo, tipoPermiso, estado, clave, registradoPor)"
                    + " values (?, ?, ?, ?, ?, ?, ?)";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, this.nombreCompleto);
            pst.setString(2, this.usuario);
            pst.setString(3, this.cargo);
            pst.setString(4, this.tipoPermiso);
            pst.setString(5, this.estado);
            pst.setString(6, this.clave);
            pst.setString(7, usuarioqueRegistra);
                        
            
            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(null, "Usuario ingresado. Para entrar al programa deberá introducir "
                    + "las credenciales exactas, incluidas las mayúsculas.\n\nUsuario: " + this.usuario + "\nClave: " + this.clave);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar el usuario. Asegurese de no estar introduciendo un nombre de"
                    + " usuario ya existente. Si el problema persiste, comuniquese con el administrador");
            e.printStackTrace();
            
        }

    }
    
    public void ActualizarEmpleado(String usuarioqueActualiza) {

        try {

            String consulta = "update empleados set nombreCompleto=?, cargo=?, tipoPermiso=?, "
                    + "estado=?, registradoPor=? where usuario=?";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, this.nombreCompleto);
            pst.setString(2, this.cargo);
            pst.setString(3, this.tipoPermiso);
            pst.setString(4, this.estado);
            pst.setString(5, usuarioqueActualiza);
            pst.setString(6, this.usuario);
            
            
            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(null, "Usuario actualizado");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar los datos del usuario. Comuniquese con el administrador");
            
        }

    }
    
    public void ActualizarClave(String usuarioqueActualizaClave){
        try {
            
            String consulta = "update empleados set clave='"+this.clave+"', '"+usuarioqueActualizaClave+"' where usuario='"+this.usuario+"'";
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, this.clave);
            pst.setString(2, usuarioqueActualizaClave);
            pst.setString(3, this.usuario);
            
            pst.executeUpdate();
            cn.close();
            JOptionPane.showMessageDialog(null, "Clave actualizada. Las nuevas credenciales son: \n\nUsuario: "+this.usuario+""
                    + "\nClave: " + this.clave);
            
        } catch (Exception e) {
        }
        
    }
    
//    public static void main(String[] args) {
//        Date nueva = new Date();
//        SimpleDateFormat f1= new SimpleDateFormat("yyyy-MM-dd");
//        String fechaSistema = f1.format(nueva);
//        
//        System.out.println(fechaSistema);
//    }
    
    

}


package clases;

import java.sql.PreparedStatement;
import java.sql.Connection;
import javax.swing.JOptionPane;


public class Abonos {
    
    private int valorAbono;
    private String fechaAbonoReal;
    private String observaciones;

    public Abonos(int valorAbono, String fechaAbonoReal, String observaciones) {
        this.valorAbono = valorAbono;
        this.fechaAbonoReal = fechaAbonoReal;
        this.observaciones = observaciones;
    }

    public int getValorAbono() {
        return valorAbono;
    }

    public void setValorAbono(int valorAbono) {
        this.valorAbono = valorAbono;
    }

    public String getFechaAbonoReal() {
        return fechaAbonoReal;
    }

    public void setFechaAbonoReal(String fechaAbonoReal) {
        this.fechaAbonoReal = fechaAbonoReal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public void RegistrarAbono(String idVenta, String fechaAbonoSistema, String registradoPor){
        
        try {
            String consulta = "insert into abonos (idVenta, valorAbono, fechaAbonoSistema, fechaAbonoReal, observaciones, "
                + "registradoPor) values (?, ?, ?, ?, ?, ?)";

            System.out.println(consulta);
            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idVenta);
            pst.setInt(2, this.valorAbono);
            pst.setString(3, fechaAbonoSistema);
            pst.setString(4, this.fechaAbonoReal);
            pst.setString(5, this.observaciones);
            pst.setString(6, registradoPor);
                        
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Abono registrado");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en registrar el abono. Abonos RegistrarAbono()");
            e.printStackTrace();
        }
        
    }
    
    public void RegistrarAbonoconFactura(String idVenta, String fechaAbonoSistema, String registradoPor, String factura){
        
        try {
            String consulta = "insert into abonos (idVenta, valorAbono, fechaAbonoSistema, fechaAbonoReal, observaciones, "
                + "registradoPor, factura) values (?, ?, ?, ?, ?, ?, ?)";

            Connection cn = Conexion.Conectar();
            PreparedStatement pst = cn.prepareStatement(consulta);
            pst.setString(1, idVenta);
            pst.setInt(2, this.valorAbono);
            pst.setString(3, fechaAbonoSistema);
            pst.setString(4, this.fechaAbonoReal);
            pst.setString(5, this.observaciones);
            pst.setString(6, registradoPor);
            pst.setString(7, factura);
            
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Abono registrado");
            
        } catch (Exception e) {
        }
        
    }
    
//    public static void main(String[] args) {
//        Abonos nuevo = new Abonos(5000, "1-4-2021", "Pagado a Luis Carlos Correa");
//        nuevo.RegistrarAbono("1", "2021-4-6", "Erwin");
//    }
    
}

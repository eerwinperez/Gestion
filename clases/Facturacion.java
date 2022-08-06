/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.awt.HeadlessException;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author erwin
 */
public class Facturacion {

    private String idVenta;
    private String cantidadFacturada;
    private String precioUnitario;
    private String fechaFactura;
    private String idCliente;
    private String condiciondePago;
    private String valorFacturado;
    private String registradoPor;

    public Facturacion(String idVenta, String cantidadFacturada, String precioUnitario, String fechaFactura, String idCliente, String condiciondePago, String valorFacturado, String registradoPor) {
        this.idVenta = idVenta;
        this.cantidadFacturada = cantidadFacturada;
        this.precioUnitario = precioUnitario;
        this.fechaFactura = fechaFactura;
        this.idCliente = idCliente;
        this.condiciondePago = condiciondePago;
        this.valorFacturado = valorFacturado;
        this.registradoPor=registradoPor;
        
    }

    public String getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(String idVenta) {
        this.idVenta = idVenta;
    }

    public String getCantidadFacturada() {
        return cantidadFacturada;
    }

    public void setCantidadFacturada(String cantidadFacturada) {
        this.cantidadFacturada = cantidadFacturada;
    }

    public String getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(String precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(String fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getCondiciondePago() {
        return condiciondePago;
    }

    public void setCondiciondePago(String condiciondePago) {
        this.condiciondePago = condiciondePago;
    }

    public String getValorFacturado() {
        return valorFacturado;
    }

    public void setValorFacturado(String valorFacturado) {
        this.valorFacturado = valorFacturado;
    }
    
    

//    public void RegistrarFactura() {
//
//        String consulta = "insert into facturas (idVenta, cantidadFacturada, precioUnitario, fechaFactura, idCliente, "
//                + "condiciondePago, valorFacturado, registradoPor) values (?, ?, ?, ?, ?, ?, ?, ?)";
//
//        try {
//            Connection cn = Conexion.Conectar();
//            PreparedStatement pst = cn.prepareStatement(consulta);
//            pst.setString(1, this.idVenta);
//            pst.setString(2, this.cantidadFacturada);
//            pst.setString(3, this.precioUnitario);
//            pst.setString(4, this.fechaFactura);
//            pst.setString(5, this.idCliente);
//            pst.setString(6, this.condiciondePago);
//            pst.setString(7, this.condiciondePago);
//            pst.setString(8, this.registradoPor);
//                    
//            
//            pst.executeUpdate();
//            cn.close();
//            JOptionPane.showMessageDialog(null, "Factura registrada");
//
//        } catch (HeadlessException | SQLException e) {
//            JOptionPane.showMessageDialog(null, "Error en registrar la factura en la base de datos Facturacion RegistrarFactura()");
//            e.printStackTrace();
//        }
//
//    }

    public String getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(String registradoPor) {
        this.registradoPor = registradoPor;
    }
    
    static public void Generar(){
        System.out.println("");
    }

}

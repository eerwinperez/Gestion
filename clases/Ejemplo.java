/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Erwin P
 */
public class Ejemplo {
    public static void main(String[] args) {
        
        Ejemplo uno = new Ejemplo();
        uno.metodo2(uno.metodo1());
        
        
        
    }
    
    public void metodo2(int numero){
        System.out.println("El numero ingresado es: "+numero);
    }
    
    public int metodo1(){
        return 1;
    }
}

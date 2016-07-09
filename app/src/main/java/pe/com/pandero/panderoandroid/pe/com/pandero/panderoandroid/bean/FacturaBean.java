package pe.com.pandero.panderoandroid.pe.com.pandero.panderoandroid.bean;

/**
 * Created by JORGE on 01/07/2016.
 */
public class FacturaBean {

    private String idfactura;
    private double montoPagado;
    private String fecha_registro;
    private String idobligaciones;


    public String getIdfactura() {
        return idfactura;
    }

    public void setIdfactura(String idfactura) {
        this.idfactura = idfactura;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public String getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(String fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public String getIdobligaciones() {
        return idobligaciones;
    }

    public void setIdobligaciones(String idobligaciones) {
        this.idobligaciones = idobligaciones;
    }

    public String toString(){
        return idfactura+" - "+montoPagado+" -  "+fecha_registro+" - "+idobligaciones;
    }
}

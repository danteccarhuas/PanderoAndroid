package pe.com.pandero.panderoandroid.pe.com.pandero.panderoandroid.bean;

/**
 * Created by MIKE on 29/06/16.
 */
public class ObligacionesBean {
    private String idobligaciones,fecha_registro;
    private int cuotas;
    private double tasa_interes,monoTotal;
    private String tipo_obligacion;

    public int getCuotas() {
        return cuotas;
    }

    public void setCuotas(int cuotas) {
        this.cuotas = cuotas;
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

    public double getMonoTotal() {
        return monoTotal;
    }

    public void setMonoTotal(double monoTotal) {
        this.monoTotal = monoTotal;
    }

    public double getTasa_interes() {
        return tasa_interes;
    }

    public void setTasa_interes(double tasa_interes) {
        this.tasa_interes = tasa_interes;
    }

    public String getTipo_obligacion() {
        return tipo_obligacion;
    }

    public void setTipo_obligacion(String tipo_obligacion) {
        this.tipo_obligacion = tipo_obligacion;
    }
    public String toString(){
        return idobligaciones+" - "+fecha_registro+" - "+cuotas+" - "+tasa_interes;
    }
}



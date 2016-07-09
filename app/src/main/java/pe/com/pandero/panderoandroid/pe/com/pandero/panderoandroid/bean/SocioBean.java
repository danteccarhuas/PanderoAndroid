package pe.com.pandero.panderoandroid.pe.com.pandero.panderoandroid.bean;

/**
 * Created by Joni on 03/07/2016.
 */
public class SocioBean {
    private String idsocio,nombres,apellido_paterno,apellido_materno,documento,correo,idPais;

    public String getIdsocio() {
        return idsocio;
    }
    public void setIdsocio(String idsocio) {
        this.idsocio = idsocio;
    }

    public String getNombres() {
        return nombres;
    }
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }
    public String getApellido_paterno() {
        return apellido_paterno;
    }
    public void setApellido_paterno(String apellido_paterno) {
        this.apellido_paterno = apellido_paterno;
    }
    public String getApellido_materno() {
        return apellido_materno;
    }
    public void setApellido_materno(String apellido_materno) {
        this.apellido_materno = apellido_materno;
    }
    public String getDocumento() {
        return documento;
    }
    public void setDocumento(String documento) {
        this.documento = documento;
    }
    public String getCorreo() {
        return correo;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getIdPais() {
        return idPais;
    }
    public void setIdPais(String idPais) {
        this.idPais = idPais;
    }


    public String NombreCompleto()
    {
        return nombres+ ' ' + apellido_paterno +' ' + apellido_materno;
    }
}

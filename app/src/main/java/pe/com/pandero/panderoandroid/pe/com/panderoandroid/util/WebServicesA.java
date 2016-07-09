package pe.com.pandero.panderoandroid.pe.com.panderoandroid.util;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

import pe.com.pandero.panderoandroid.pe.com.pandero.panderoandroid.bean.SocioBean;

/**
 * Created by Joni on 03/07/2016.
 */
public class WebServicesA {

    String NAMESPACE = "http://www.cibertec.com/";
    String METHOD_NAME = "registraCampeonato";
    String SOAP_ACTION = NAMESPACE + METHOD_NAME;
    String URL = "http://10.0.2.2:8088/WSSimulacro";

    public List<SocioBean> ConsultaSocioPorPais(String codigo){
        List<SocioBean> data = new ArrayList<>();
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        PropertyInfo prop1 = new PropertyInfo();
        prop1.setName("descripcion");
        prop1.setValue(codigo);
        prop1.setType(String.class);
        request.addProperty(prop1);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE transport = new HttpTransportSE(URL);

        int salida = -1;
        try {
            transport.call(SOAP_ACTION, envelope);
            SoapPrimitive object = (SoapPrimitive) envelope.getResponse();
            data = (List<SocioBean>) object;
        }catch (Exception e){
            System.out.println("--->" + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }
}

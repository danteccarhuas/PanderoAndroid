package pe.com.pandero.panderoandroid;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pe.com.pandero.panderoandroid.pe.com.pandero.panderoandroid.bean.JSONParserA;
import pe.com.pandero.panderoandroid.pe.com.pandero.panderoandroid.bean.SocioBean;

public class activity_reporteSocioPais extends AppCompatActivity implements View.OnClickListener{

    private EditText etCodigo;
    private Button btnConsultar;
    private ListView lstListSocios;
    private LinearLayout linear_layoutDetalleSocio;
    private ScrollView scrollView;

    /*text para llenar Detalle*/
    private TextView tvCodigo, tvSocio, tvDocumento, tvCorreo, tvPais;

    private String codigo;
    List<SocioBean> lisSocio = null;

    private ProgressDialog pDialog;
    // Creating JSON Parser object
    JSONParserA jParser = new JSONParserA();
    // url to get all socios list
    private static String url_socio = "http://serviceandroid.esy.es/serviceconnect/get_SocioPorPais.php";
    private static String url_Detallesocio = "http://serviceandroid.esy.es/serviceconnect/ObtenerDetalleObligacionXCodigo.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SOCIO = "SociosPorPais";
    private static final String TAG_DETALLESOCIO = "Detallesocio";

    // socios JSONArray
    JSONArray socios = null;
    JSONArray Detallesocios = null;

    /*idObigacion*/
    String idSocios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_socio_pais);
        etCodigo = (EditText) findViewById(R.id.etCodigo);
        btnConsultar = (Button) findViewById(R.id.btnConsultar);
        lstListSocios = (ListView) findViewById(R.id.lstListSocios);

        linear_layoutDetalleSocio = (LinearLayout) findViewById(R.id.linear_layoutSocio);

        btnConsultar.setOnClickListener(this);

        tvCodigo = (TextView) findViewById(R.id.tvCodigo);
        tvSocio = (TextView) findViewById(R.id.tvSocio);
        tvDocumento = (TextView) findViewById(R.id.tvDocumento);
        tvCorreo = (TextView) findViewById(R.id.tvCorreo);
        tvPais = (TextView) findViewById(R.id.tvPais);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

          /*Ocultar el scroll view*/
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setHorizontalScrollBarEnabled(false);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConsultar:
                /*Limpiar TextView*/
                //LimpiarDetalleTextView();
                /*Oculte LayoutDetalleSocio*/
                //ocultarLayoutDetalleSocio();


                codigo = etCodigo.getText().toString();
                new BuscarSocioXPais().execute();
                break;
        }
    }

    class BuscarSocioXPais extends AsyncTask<String, String, String> {

        /**
         * Antes de empezar el background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity_reporteSocioPais.this);
            pDialog.setMessage("Cargando Obligaciones. Por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * obteniendo todos los productos
         * */
        protected String doInBackground(String... args) {

            lisSocio=new ArrayList<SocioBean>();
            // Building Parameters
            List params = new ArrayList();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_socio, "GET", params,codigo);

            // Check your log cat for JSON reponse
            Log.d("All socios: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Getting Array of obligaciones
                    socios = json.getJSONArray(TAG_SOCIO);

                    SocioBean bean=null;

                    for (int i = 0; i < socios.length(); i++) {
                        JSONObject c = socios.getJSONObject(i);

                        bean = new SocioBean();
                        bean.setIdsocio(c.getString("idsocio"));
                        bean.setNombres(c.getString("Socio"));
                        bean.setDocumento(c.getString("documento"));
                        bean.setCorreo(c.getString("correo"));
                        lisSocio.add(bean);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all obligaciones
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    if(lisSocio.size()==0 && codigo.toString()!=""){
                        Toast.makeText(activity_reporteSocioPais.this,"No se econtraron Socios del Codigo "+codigo.toString(),
                                Toast.LENGTH_SHORT).show();
                    }else if(lisSocio.size()==0 && codigo.toString()==""){
                        Toast.makeText(activity_reporteSocioPais.this,"Ingresar un CODIGO para la busqueda de Socio",
                                Toast.LENGTH_SHORT).show();
                    }
                    ArrayAdapter<SocioBean> adapter=
                            new ArrayAdapter<SocioBean>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line,lisSocio);

                    lstListSocios.setAdapter(adapter);
                }
            });
        }
    }

}

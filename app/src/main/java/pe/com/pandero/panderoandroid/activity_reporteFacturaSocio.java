package pe.com.pandero.panderoandroid;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pe.com.pandero.panderoandroid.pe.com.pandero.panderoandroid.bean.FacturaBean;
import pe.com.pandero.panderoandroid.pe.com.pandero.panderoandroid.bean.JSONParser;

public class activity_reporteFacturaSocio extends AppCompatActivity implements View.OnClickListener{

    private EditText etSocio;
    private Button btnBuscar;
    private ListView lstListaFacturas;

    private String codigo;

    List<FacturaBean> lisFactura=null;

    // Progress Dialog
    private ProgressDialog pDialog;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    // url to get all products list
    private static String url_all_empresas = "http://serviceandroid.esy.es/serviceconnect/ObtenerFacturasXSocio.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_FACTURAS = "factura";
    private static final String TAG_ID = "id";
    private static final String TAG_NOMBRE = "nombre";

    // products JSONArray
    JSONArray facturas = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_factura_socio);
        etSocio=(EditText)findViewById(R.id.etSocio);
        btnBuscar=(Button)findViewById(R.id.btnBuscar);
        lstListaFacturas=(ListView)findViewById(R.id.lstListaFacturas);

        btnBuscar.setOnClickListener(this);



        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBuscar:
                // Cargar los productos en el Background Thread
                codigo=etSocio.getText().toString();
                new CargarFacturas().execute();
                break;
        }
    }

    class CargarFacturas extends AsyncTask<String, String, String> {

        /**
         * Antes de empezar el background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity_reporteFacturaSocio.this);
            pDialog.setMessage("Cargando facturas. Por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * obteniendo todos los productos
         * */
        protected String doInBackground(String... args) {

            lisFactura=new ArrayList<FacturaBean>();
            // Building Parameters
            List params = new ArrayList();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequestFac(url_all_empresas, "GET", params,codigo);

            // Check your log cat for JSON reponse
            Log.d("Todas Facturas: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    facturas = json.getJSONArray(TAG_FACTURAS);

                    FacturaBean bean=null;

                    for (int i = 0; i < facturas.length(); i++) {
                        JSONObject c = facturas.getJSONObject(i);
                        bean = new FacturaBean();
                        bean.setIdfactura(c.getString("idfactura"));
                        bean.setMontoPagado(c.getDouble("montoPagado"));
                        bean.setFecha_registro(c.getString("fecha_registro"));
                        bean.setIdobligaciones(c.getString("idobligaciones"));

                        lisFactura.add(bean);
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
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    /*ListAdapter adapter = new SimpleAdapter(
                            MyActivity.this,
                            empresaList,
                            R.layout.single_post,
                            new String[] {
                                    TAG_ID,
                                    TAG_NOMBRE,
                            },
                            new int[] {
                                    R.id.single_post_tv_id,
                                    R.id.single_post_tv_nombre,
                            });*/
                    if(lisFactura.size()==0){
                        Toast.makeText(activity_reporteFacturaSocio.this,"No se econtraron Facturas del Codigo "+codigo.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                    ArrayAdapter<FacturaBean>adapter=
                            new ArrayAdapter<FacturaBean>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line,lisFactura);
                    // updating listview
                    //setListAdapter(adapter);
                    lstListaFacturas.setAdapter(adapter);
                }
            });
        }
    }


}

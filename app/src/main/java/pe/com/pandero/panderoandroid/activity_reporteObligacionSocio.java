package pe.com.pandero.panderoandroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pe.com.pandero.panderoandroid.pe.com.pandero.panderoandroid.bean.JSONParser;
import pe.com.pandero.panderoandroid.pe.com.pandero.panderoandroid.bean.ObligacionesBean;
import pe.com.pandero.panderoandroid.pe.com.panderoandroid.util.WebServices;

public class activity_reporteObligacionSocio extends AppCompatActivity implements View.OnClickListener{

    private EditText etCodigo;
    private Button btnConsultar;
    private ListView lstListObligaciones;

    private String codigo;

    List<ObligacionesBean> lisObligacion=null;

    // Progress Dialog
    private ProgressDialog pDialog;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    // url to get all products list
    private static String url_all_empresas = "http://serviceandroid.esy.es/serviceconnect/get_ObligacionXSocio.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "obligacion";
    private static final String TAG_ID = "id";
    private static final String TAG_NOMBRE = "nombre";

    // products JSONArray
    JSONArray products = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_obligacion_socio);
        etCodigo=(EditText)findViewById(R.id.etCodigo);
        btnConsultar=(Button)findViewById(R.id.btnConsultar);
        lstListObligaciones=(ListView)findViewById(R.id.lstListObligaciones);

        btnConsultar.setOnClickListener(this);



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
            case R.id.btnConsultar:
                // Cargar los productos en el Background Thread
                codigo=etCodigo.getText().toString();
                new LoadAllProducts().execute();
                break;
        }
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Antes de empezar el background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity_reporteObligacionSocio.this);
            pDialog.setMessage("Cargando comercios. Por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * obteniendo todos los productos
         * */
        protected String doInBackground(String... args) {

            lisObligacion=new ArrayList<ObligacionesBean>();
            // Building Parameters
            List params = new ArrayList();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_empresas, "GET", params,codigo);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_PRODUCTS);

                    ObligacionesBean bean=null;

                    // looping through All Products
                    //Log.i("ramiro", "produtos.length" + products.length());
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Storing each json item in variable
                       /* String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NOMBRE);*/
                        // creating new HashMap
                        bean = new ObligacionesBean();
                        bean.setIdobligaciones(c.getString("idobligaciones"));
                        bean.setFecha_registro(c.getString("fecha_registro"));
                        bean.setCuotas(c.getInt("cuotas"));
                        bean.setTasa_interes(c.getDouble("tasa_interes"));
                        // adding each child node to HashMap key => value
                        /*map.put(TAG_ID, id);
                        map.put(TAG_NOMBRE, name);*/

                        lisObligacion.add(bean);
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
                    if(lisObligacion.size()==0){
                        Toast.makeText(activity_reporteObligacionSocio.this,"No se econtraron Obligaciones del Codigo "+codigo.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                    ArrayAdapter<ObligacionesBean>adapter=
                            new ArrayAdapter<ObligacionesBean>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line,lisObligacion);
                    // updating listview
                    //setListAdapter(adapter);
                    lstListObligaciones.setAdapter(adapter);
                }
            });
        }
    }


}

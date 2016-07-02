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
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
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
    private LinearLayout linear_layoutDetalleObligacion;

    private ScrollView scrollView;

    /*text para llenar Detalle*/
    private TextView tvSocio,tvFechaRegistro,tvCuotas,tvTasaInteres,tvMontoTotal,tvTipoOblgacion;

    private String codigo;

    List<ObligacionesBean> lisObligacion=null;

    // Progress Dialog
    private ProgressDialog pDialog;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    // url to get all obligaciones list
    private static String url_obligacion = "http://serviceandroid.esy.es/serviceconnect/get_ObligacionXSocio.php";
    private static String url_Detalleobligacion = "http://serviceandroid.esy.es/serviceconnect/ObtenerDetalleObligacionXCodigo.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_OBLIGACION = "obligacion";
    private static final String TAG_DETALLEOBLIGACION = "Detalleobligacion";

    // obligaciones JSONArray
    JSONArray obligaciones = null;
    JSONArray Detalleobligaciones = null;

    /*idObigacion*/
    String idObligacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_obligacion_socio);
        etCodigo=(EditText)findViewById(R.id.etCodigo);
        btnConsultar=(Button)findViewById(R.id.btnConsultar);
        lstListObligaciones=(ListView)findViewById(R.id.lstListObligaciones);

        linear_layoutDetalleObligacion=(LinearLayout)findViewById(R.id.linear_layoutDetalleObligacion);

        btnConsultar.setOnClickListener(this);

        tvSocio=(TextView)findViewById(R.id.tvSocio);
        tvFechaRegistro=(TextView)findViewById(R.id.tvFechaRegistro);
        tvCuotas=(TextView)findViewById(R.id.tvCuotas);
        tvTasaInteres=(TextView)findViewById(R.id.tvTasaInteres);
        tvMontoTotal=(TextView)findViewById(R.id.tvMontoTotal);
        tvTipoOblgacion=(TextView)findViewById(R.id.tvTipoOblgacion);

        scrollView=(ScrollView)findViewById(R.id.scrollView);

        /*Ocultar el scroll view*/
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setHorizontalScrollBarEnabled(false);

        lstListObligaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {

                idObligacion = ((TextView)view).getText().toString();
                //Toast.makeText(activity_reporteObligacionSocio.this,item.toString(),Toast.LENGTH_SHORT).show();
                ObtenerDetalleObligacion();
            }

        });


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
                /*Limpiar TextView*/
                LimpiarDetalleTextView();
                /*Oculte LayoutDetalleObligacion*/
                ocultarLayoutDetalleObligacion();


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
            pDialog.setMessage("Cargando Obligaciones. Por favor espere...");
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
            JSONObject json = jParser.makeHttpRequest(url_obligacion, "GET", params,codigo);

            // Check your log cat for JSON reponse
            Log.d("All obligaciones: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Getting Array of obligaciones
                    obligaciones = json.getJSONArray(TAG_OBLIGACION);

                    ObligacionesBean bean=null;

                    for (int i = 0; i < obligaciones.length(); i++) {
                        JSONObject c = obligaciones.getJSONObject(i);

                        bean = new ObligacionesBean();
                        bean.setIdobligaciones(c.getString("idobligaciones"));
                        bean.setFecha_registro(c.getString("fecha_registro"));
                        bean.setCuotas(c.getInt("cuotas"));
                        bean.setTasa_interes(c.getDouble("tasa_interes"));

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
            // dismiss the dialog after getting all obligaciones
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    if(lisObligacion.size()==0 && codigo.toString()!=""){
                        Toast.makeText(activity_reporteObligacionSocio.this,"No se econtraron Obligaciones del Codigo "+codigo.toString(),
                                Toast.LENGTH_SHORT).show();
                    }else if(lisObligacion.size()==0 && codigo.toString()==""){
                        Toast.makeText(activity_reporteObligacionSocio.this,"Ingresar un CODIGO para la busqueda de Obligaciones",
                                Toast.LENGTH_SHORT).show();
                    }
                    ArrayAdapter<ObligacionesBean>adapter=
                            new ArrayAdapter<ObligacionesBean>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line,lisObligacion);

                    lstListObligaciones.setAdapter(adapter);
                }
            });
        }
    }

    public void ObtenerDetalleObligacion(){
        new DetalleObligacion().execute();
    }

    class DetalleObligacion extends AsyncTask<String, String, String> {

        String socio="",fechaRegistro="",cuotas="",tasaInteres="",MontoTotal="",tipo_obligacion="";
        /**
         * Antes de empezar el background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity_reporteObligacionSocio.this);
            pDialog.setMessage("Cargando Detalle de Obligaciones. Por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * obteniendo todos los productos
         * */
        protected String doInBackground(String... args) {

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequestDetalleObligacion(url_Detalleobligacion, "GET",idObligacion);
            // Check your log cat for JSON reponse
            Log.d("All Deltalle Obligac : ", json.toString());
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // Getting Array of DetalleObligacion
                    Detalleobligaciones = json.getJSONArray(TAG_DETALLEOBLIGACION);

                    for (int i = 0; i < Detalleobligaciones.length(); i++) {
                        JSONObject c = Detalleobligaciones.getJSONObject(i);
                        socio=c.getString("socio");
                        fechaRegistro=c.getString("fecha_registro");
                        cuotas=c.getString("cuotas");
                        tasaInteres=c.getString("tasa_interes");
                        MontoTotal=c.getString("monoTotal");
                        tipo_obligacion=c.getString("tipo_obligacion");
                    }
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
            return  null;
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

                    /*LLenamos los textview*/
                    tvSocio.setText(socio.toString());
                    tvFechaRegistro.setText(fechaRegistro.toString());
                    tvCuotas.setText(cuotas.toString());
                    tvTasaInteres.setText(tasaInteres.toString());
                    tvMontoTotal.setText(MontoTotal.toString());
                    tvTipoOblgacion.setText(tipo_obligacion.toString());

                    /*MostrarLinearLayoutDetalleObligacion*/

                    mostrarLayoutDetalleObligacion();
                }
            });
        }
    }

    void LimpiarDetalleTextView(){
        tvSocio.setText("");
        tvFechaRegistro.setText("");
        tvCuotas.setText("");
        tvTasaInteres.setText("");
        tvMontoTotal.setText("");
        tvTipoOblgacion.setText("");
    }
    public void mostrarLayoutDetalleObligacion()
    {
        if (linear_layoutDetalleObligacion.getVisibility() == View.GONE)
        {
            animar(true);
            linear_layoutDetalleObligacion.setVisibility(View.VISIBLE);
        }
    }

    public void ocultarLayoutDetalleObligacion()
    {
        if (linear_layoutDetalleObligacion.getVisibility() == View.VISIBLE)
        {
            animar(false);
            linear_layoutDetalleObligacion.setVisibility(View.GONE);
        }

    }

    private void animar(boolean mostrar)
    {
        AnimationSet set = new AnimationSet(true);
        Animation animation = null;
        if (mostrar)
        {
            //desde la esquina inferior derecha a la superior izquierda
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        }
        else
        {    //desde la esquina superior izquierda a la esquina inferior derecha
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        }
        //duración en milisegundos
        animation.setDuration(500);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.25f);

        linear_layoutDetalleObligacion.setLayoutAnimation(controller);
        linear_layoutDetalleObligacion.startAnimation(animation);
    }
}

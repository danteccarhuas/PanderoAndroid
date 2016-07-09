package pe.com.pandero.panderoandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pe.com.pandero.panderoandroid.pe.com.pandero.panderoandroid.bean.JSONParser;
import pe.com.pandero.panderoandroid.pe.com.pandero.panderoandroid.bean.ObligacionesBean;

public class activity_login extends Activity implements View.OnClickListener{

    private Button btn_login;

    // Progress Dialog
    private ProgressDialog pDialog;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_UsuarioLogueado = "UsuarioLogueado";


    // url to get all obligaciones list
    private static String url_obligacion = "http://serviceandroid.esy.es/serviceconnect/Logueo.php";

    private String usuario="",passoword="";

    // obligaciones JSONArray
    JSONArray obligaciones = null;

    ObligacionesBean bean=null;

    private EditText etusuario,etpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        etusuario=(EditText)findViewById(R.id.etusuario);
        etpassword=(EditText)findViewById(R.id.etpassword);
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
    }



    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                /*Intent intent = new Intent(activity_login.this, activity_menu.class);
                startActivity(intent);*/
                usuario=etusuario.getText().toString();
                passoword=etpassword.getText().toString();
                if(bean!=null){
                    bean=null;
                }
                new Loguear().execute();
                break;
        }
    }


    class Loguear extends AsyncTask<String, String, String> {

        /**
         * Antes de empezar el background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity_login.this);
            pDialog.setMessage("Cargando Obligaciones. Por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * obteniendo todos los productos
         * */
        protected String doInBackground(String... args) {

            //lisObligacion=new ArrayList<ObligacionesBean>();
            // Building Parameters
            List params = new ArrayList();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequestLoguear(url_obligacion, "GET", params,usuario,passoword);

            // Check your log cat for JSON reponse
            Log.d("All obligaciones: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Getting Array of obligaciones
                    obligaciones = json.getJSONArray(TAG_UsuarioLogueado);
                    for (int i = 0; i < obligaciones.length(); i++) {
                        JSONObject c = obligaciones.getJSONObject(i);
                        bean = new ObligacionesBean();
                        bean.setIdobligaciones(c.getString("idobligaciones"));
                        bean.setFecha_registro(c.getString("fecha_registro"));
                        bean.setCuotas(c.getInt("cuotas"));
                        bean.setTasa_interes(c.getDouble("tasa_interes"));

                        //lisObligacion.add(bean);
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
                    if(bean==null){
                        Toast.makeText(activity_login.this,"Usuario Incorrecto",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }else if(bean!=null){
                        Toast.makeText(activity_login.this,"Usuario Correcto",
                                Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(activity_login.this,activity_menu.class);
                        startActivity(intent);
                        return;
                    }
                    /*if(lisObligacion.size()==0 && codigo.toString()!=""){
                        Toast.makeText(activity_reporteObligacionSocio.this,"No se econtraron Obligaciones del Codigo "+codigo.toString(),
                                Toast.LENGTH_SHORT).show();
                    }else if(lisObligacion.size()==0 && codigo.toString()==""){
                        Toast.makeText(activity_reporteObligacionSocio.this,"Ingresar un CODIGO para la busqueda de Obligaciones",
                                Toast.LENGTH_SHORT).show();
                    }
                    ArrayAdapter<ObligacionesBean> adapter=
                            new ArrayAdapter<ObligacionesBean>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line,lisObligacion);

                    lstListObligaciones.setAdapter(adapter);*/
                }
            });
        }
    }
}

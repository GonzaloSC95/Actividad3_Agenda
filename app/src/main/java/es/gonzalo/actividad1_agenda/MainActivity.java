package es.gonzalo.actividad1_agenda;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import es.gonzalo.actividad1_agenda.db.ControladorDB;

public class MainActivity extends AppCompatActivity {
    private ControladorDB controladorDB;
    //PARA RELLENAR UN LISTVIEW NECESITAMOS UN OBJETO ADAPTER
    private ArrayAdapter<String> adapter;
    ///REFERENCIA AL LISTVIEW
    private ListView listaTareas;
    /////////////////RECOGEMOS EL NOMBRE DEL USUARIO/////////////////////////////
    private Bundle bundle;
    private String nombreUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //////////////////////////////////////////////////
        initComponents();
        ////////////////////////////////////////////////////
        controladorDB = new ControladorDB(this);
        /////////ACTUALIZAMOS LA LISTVIEW///////////////////////////////////////
        actualizarGUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //Solo es necesario en caso de utilizar un menú contextual
        /*Un menú contextual es un menú flotante que aparece cuando el usuario hace un clic largo en un elemento.
        Proporciona acciones que afectan el contenido seleccionado o el marco contextual.*/
        //TO DO
        switch (item.getItemId()) {

            case R.id.new_task:

                Toast tareaAnadida = Toast.makeText(this, "Task added", Toast.LENGTH_SHORT);
                Toast error = Toast.makeText(this,"You must write something\n" +
                        "if you want to add a task",Toast.LENGTH_SHORT);
                EditText cajaTexto = new EditText(this);
                /////////////////////////////////////////////////
                AlertDialog dialogoAnadirTarea = new AlertDialog.Builder(this)
                        .setTitle("New Task")
                        .setMessage("Set your new task below:")
                        .setView(cajaTexto)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    //TO DO
                                    String tarea = cajaTexto.getText().toString();
                                    StringBuilder primeraMayuscula = new StringBuilder(tarea);
                                    primeraMayuscula.replace(0, 1, String.valueOf(tarea.charAt(0)).toUpperCase());
                                    controladorDB.addTarea(primeraMayuscula.toString(), nombreUser);
                                    /////////////ACTUALIZAMOS LA LISTVIEW////////////////////////////////////////////////////
                                    actualizarGUI();
                                    //////////MOSTRAMOS EL TOAST DE CONIRMACION DE TAREA AÑADIDA/////////////////////
                                    tareaAnadida.show();
                                } catch (StringIndexOutOfBoundsException e) {
                                    error.show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                //////////////////////////////////////////////////////////
                dialogoAnadirTarea.show();
                break;
            /////////////////////////////////////////////////////////////////
            case R.id.exit:
                backToLogin();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////METODO BACK TO LOGIN/////////////
    private void backToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    ////////////////METODO PARA ACTUALIZAR LA INTERFAZ CON LOS DATOS DE LA BBBD
    private void actualizarGUI() {
        try {
            //SI EL NUMERO DE REGISTROS DE LA BBDD ES 0
            if (controladorDB.numeroDeRegistros() == 0) {
                listaTareas.setAdapter(null);
            } else {
                /////////SI EL NÚMERO DE REGISTROS ES MAYOR QUE 0
                adapter = new ArrayAdapter<>(this
                        , R.layout.item_tarea
                        , R.id.tareaTitulo
                        , controladorDB.mostrarTareas(nombreUser));
                ///////////////////////////////////////
                listaTareas.setAdapter(adapter);
            }
        } catch (NullPointerException e) {
            Log.e("ERROR", e.getMessage());
            listaTareas.setAdapter(null);
        }
    }

    ////METODO PARA INICIALIZAR LOS COMPONENTES DE LA GUI
    private void initComponents() {
        try {
            listaTareas = (ListView) findViewById(R.id.listaDeTareas);
            ///////////////////////////////////////////////////////////
            bundle = getIntent().getExtras();
            nombreUser = bundle.getString("user");
            //////////////////////////////////////////////
            Toast.makeText(this, "Welcome " + nombreUser, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.i("NOMBRE DE USUARIO NO CREADO", nombreUser);
        }
    }

    //////METODO PARA BORRAR LOS REGISTROS DE LA BBDD
    public void deleteTarea(View v) {
        //////////LOCALIZAMOS AL ELEMENTO PADRE DEL BOTON/////////////////////
        View padre = (View) v.getParent();
        ////////BUSCAMOS EL TEXVIEW (TAREA) A BORRAR POR SU ID///////////////
        TextView tareaABorrar = (TextView) padre.findViewById(R.id.tareaTitulo);
        //////////PASAMOS EL TEXTO DE DICHO TEXTVIEW COMO PARAMETRO
        //DEL METODO DELETE DEL CONTROLADOR DE LA BBDD///////////////////
        String tarea = tareaABorrar.getText().toString();
        ////////////////////////////////////////
        controladorDB.borrarTarea(tarea, nombreUser);
        ///ACTUALIZAMOS LA GUI/////////////////////////////////////
        actualizarGUI();
        /////////////////////////////////////////////////////
        Toast.makeText(this, "Task done", Toast.LENGTH_SHORT).show();
    }


}
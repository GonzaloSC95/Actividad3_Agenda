package es.gonzalo.actividad1_agenda.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.constraintlayout.motion.widget.Debug;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import es.gonzalo.actividad1_agenda.MainActivity;

public class ControladorDB extends SQLiteOpenHelper {
    /////////////////CONSTRUCTOR//////////////////////////////////////
    public ControladorDB(Context context) {
        super(context, "es.gonzalo.actividad1_agenda.db", null, 1);
    }

    ///////////////////METODO PARA CREAR LA BBDD-TABLA//////////////////////////////
    @Override
    public void onCreate(SQLiteDatabase db) {

        ///////////////////TABLA USUARIOS////////////////
        db.execSQL("CREATE TABLE USUARIOS (" +
                "NOMBRE TEXT UNIQUE NOT NULL," +
                "PSW TEXT NOT NULL" +
                ");");

        Log.i("USUARIOS", "TABLA USUARIOS CREADA");

        /////////////TABLA TAREAS///////////////////////////////////
        db.execSQL("CREATE TABLE TAREAS (" +
                "TAREA TEXT NOT NULL," +
                "NOMBRE TEXT NOT NULL);");

        Log.i("TAREAS", "TABLA TAREAS CREADA");

        ////////////////////USUARIO GONZALO CREADO////////////////////
        db.execSQL("INSERT INTO USUARIOS (NOMBRE,PSW)" +
                "VALUES ('Master','" + contraseniaHash("master1234") + "'); ");

        Log.i("Master", "USUARIO INSERTADO");


    }

    /////////////////MATODO PARA ACTUALIZAR BBDD DE UNA VERSION A OTRA//////////////////////////////
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //LO DEJAMOS EN BLANCO.
        //SIRVE PARA HACER UNA MIGRACIÓN DE LA BBDD ANTIGUA, A LA NUEVA,
        //AL HACER UNA ACTUALIZACIÓN DE LA APP.
    }

    //METODO PARA ISERTAR UN REGISTRO EN LA BBDD
    public void addTarea(String tarea, String nombreUser) {
        ////////////////ABRIMOS LA BBDD EN MODO LESCTURA Y ESCRITURA//////////////////////////////
        SQLiteDatabase db = this.getWritableDatabase();//ESTE METODO DEVUELVE UNA REFERNCIA A LA BBDD
        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////CREAMOS UN OBJETO REGISTRO (CLAVE-VALOR) CON LA CLASE ContentValues //////////////////////////////
        ContentValues registro = new ContentValues();
        //////AL OBJETO REGISTRO LE ASIGNAMOS EL NOMBRE DE LA COLUMNA DE NUESTRA TABLA Y EL NEW VALOR/////////////////////
        registro.put("TAREA", tarea);
        registro.put("NOMBRE", nombreUser);
        ///////////////INSERTAMOS LA SENTENCIA SQL CORRESPONDIENTE PARA CREAR UN NUEVO REGISTRO/////////////////////////////////////////
        db.insert("TAREAS", null, registro);//SENTENCIA SQL INSERT
        //CERRAMOS LA BBDD
        db.close();

        /*TODO ESTO TAMBIEN SE PUEDE HACER DE LA SIGUIENTE MANERA
        //db.execSQL("INSERT INTO TAREAS VALUES (NULL,"+tarea+");");
        //db.close();*/

    }

    public String[] mostrarTareas(String nombreUser) {
        ////////////////ABRIMOS LA BBDD EN MODO LESCTURA Y ESCRITURA//////////////////////////////
        SQLiteDatabase db = this.getReadableDatabase();//ESTE METODO DEVUELVE UNA REFERNCIA A LA BBDD
        ///////////////CREAMOS UN OBJETO CURSOR PARA RECORRER TODOS LOS REGISTROS DE LA TABLA/////////////////////////////////////////
        Cursor cursor = db.rawQuery("SELECT * FROM TAREAS WHERE NOMBRE = '" + nombreUser + "';", null);//SENTENCIA SQL SELECT
        //////////////////////////////////////////
        int numRegistros = cursor.getCount(); //NUMERO DE REGISTROS EN LA TABLA
        //CREAMOS UN ARRAY
        String[] tareas = new String[numRegistros];
        /////////////////////////////////////////////////////////////////////////
        if (numRegistros == 0) {
            //LA TABLA ESTA VACIA
            db.close();
            return tareas;
        } else {
            //CREAMOS UN ARRAY
            //String[] tareas = new String[numRegistros];
            //VAMOS AL PRIMER REGISTRO DE LA TABLA
            cursor.moveToFirst();
            ///RECORREMOS LA TABLA
            for (int i = 0; i < numRegistros; i++) {
                tareas[i] = cursor.getString(0);
                //////////////////////
                cursor.moveToNext();
            }
            //CERRAMOS LA BBDD
            db.close();
            return tareas;

        }
    }

    //METODO QUE BORRA LOS REGISTROS DE LA BBDD
    public void borrarTarea(String tarea, String nombreUser) {
        //INVOCAMOS A LA BBDD
        SQLiteDatabase db = this.getWritableDatabase();
        ///////////////////EJECUTAMOS UN DELETE
        db.delete("TAREAS", "TAREA=? AND NOMBRE=?", new String[]{tarea, nombreUser});
        //db.execSQL("DELETE FROM TAREAS WHERE TAREA LIKE '"
        // +tarea+"' AND NOMBRE LIKE '"+nombreUser+"';");
        ///CERRAMOS LA BBDD
        db.close();
    }


    //METODO QUE DEVUELVE EL NUMERO DE REGISTROS
    public int numeroDeRegistros() {
        SQLiteDatabase db = this.getReadableDatabase();
        ///////SENTENCIA SQL SELECT/////////////////////////////////////////
        Cursor cursor = db.rawQuery("SELECT * FROM TAREAS", null);
        return cursor.getCount();
    }

    //METODO PARA CREAR USUARIOS
    public boolean nuevoUsuario(String nombre, String psw) {
        SQLiteDatabase db = this.getWritableDatabase();//ESTE METODO DEVUELVE UNA REFERNCIA A LA BBDD
        Cursor cursor = db.rawQuery("SELECT * FROM USUARIOS WHERE NOMBRE = '" + nombre + "';", null);
        if (cursor.getCount() >= 1) {
            //CERRAMOS LA BBDD
            db.close();
            return false;
        } else if (nombre.isEmpty() || psw.isEmpty()) {
            db.close();
            return false;
        } else {
            ////////////////CREAMOS UN OBJETO REGISTRO (CLAVE-VALOR) CON LA CLASE ContentValues //////////////////////////////
            ContentValues registro = new ContentValues();
            //////AL OBJETO REGISTRO LE ASIGNAMOS EL NOMBRE DE LA COLUMNA DE NUESTRA TABLA Y EL NEW VALOR/////////////////////
            registro.put("NOMBRE", nombre);
            registro.put("PSW", psw);
            ////////////////ABRIMOS LA BBDD EN MODO LESCTURA Y ESCRITURA//////////////////////////////

            ///////////////INSERTAMOS LA SENTENCIA SQL CORRESPONDIENTE PARA CREAR UN NUEVO REGISTRO/////////////////////////////////////////
            db.insert("USUARIOS", null, registro);//SENTENCIA SQL INSERT
            //CERRAMOS LA BBDD
            db.close();
            return true;
        }

    }

    /////CHECK PARA COMPROBAR SI UN USUARIO ESTA REGISTRADO O NO////////////////////
    public boolean usuarioEncontrado(String nombreUser, String passWord) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM USUARIOS WHERE NOMBRE = '" + nombreUser + "' " +
                "AND PSW LIKE '" + passWord + "';", null);
        if (cursor.getCount() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /////////////////////////////HASH////////////////////////
    public String contraseniaHash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(password.getBytes());
            byte[] hash = md.digest();
            password = Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            Log.e("ERROR LOG", e.getMessage());
        }
        return password;
    }
}

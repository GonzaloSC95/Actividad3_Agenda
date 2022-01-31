package es.gonzalo.actividad1_agenda;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import es.gonzalo.actividad1_agenda.db.ControladorDB;

public class LoginActivity extends AppCompatActivity {

    private ControladorDB db;//BBDD
    private TextInputEditText name;
    private TextInputEditText passwd;
    private TextView logo;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ////////////////////////////
        db = new ControladorDB(this);
        ////////////////////////////
        initComponets();
        /////////////////////////////
        setAnims();
        ////////////////////////////
        actions();
    }

    public void initComponets() {
        login = (Button) findViewById(R.id.login);
        logo = (TextView) findViewById(R.id.logoLogin);
        ////////////////////////////////////////////////
        name = (TextInputEditText) findViewById(R.id.name);
        passwd = (TextInputEditText) findViewById(R.id.psw);
    }

    public void setAnims() {
        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.anim_logo_ciudad);
        logoAnim.setFillAfter(true);
        logo.startAnimation(logoAnim);
    }

    public void actions() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnims();
                login();
            }
        });
    }

    //Funcion crear cuenta al pulsar el TextView
    public void crearCuenta(View view) {
        String nombre = name.getText().toString();
        String password = passwd.getText().toString();
        ///////////////////////////////////////////////////
        String contraseniaHasheada = "";
        if (!password.isEmpty()) {
            contraseniaHasheada = contraseniaHash(password);
        }
        /////////////////////////////////////////////////////
        if (db.nuevoUsuario(nombre, contraseniaHasheada) == false
                && nombre.isEmpty() == false
                && password.isEmpty() == false) {
            //////////////////////////////////////////////////////////////
            Toast.makeText(this, "That user name\n" +
                    "is already using\nby someone", Toast.LENGTH_LONG).show();
        } else if (nombre.isEmpty() || password.isEmpty()) {
            /////////////////////////////////////////////////////
            Toast aviso = Toast.makeText(this, "You must put a name\n" +
                    "and a password", Toast.LENGTH_LONG); //Toast.LENGTH_SHORT
            aviso.show();
            ////////////////////////////////
        } else {
            /////////////////////////////////////////////////////
            Toast aviso = Toast.makeText(this, "Account created", Toast.LENGTH_LONG); //Toast.LENGTH_SHORT
            aviso.show();
            ////////////////////////////////
        }
    }

    //Funcion login al pulsar el botón de LOGIN
    public void login() {
        String nombre = name.getText().toString();
        String password = passwd.getText().toString();
        //////////////////////////////////////////
        if (db.usuarioEncontrado(nombre, contraseniaHash(password)) == true) {
            ///////////////AL PULAR EL BOTON DE LOGIN, RECOGEMOS EL NOMBRE DE USUARIO Y SU ID///////////////////////
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", nombre);
            startActivity(intent);
            finish();
        } else {
            Toast wrong_user_psw = Toast.makeText(this, "Wrong user or password", Toast.LENGTH_SHORT);
            wrong_user_psw.show();
        }

    }

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
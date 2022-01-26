package es.gonzalo.actividad1_agenda;

import android.content.Intent;
import android.os.Bundle;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener {

    private TextView logoBatman;
    private TextView tituloSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        ////////////////////////
        initComponets();
        setAnims();
        /////////////////////


    }

    public void initComponets() {

        logoBatman = (TextView) findViewById(R.id.splashlogo);
        tituloSplash = (TextView) findViewById(R.id.splashTitle);
    }

    public void setAnims() {
        Animation anim_logo_batman = AnimationUtils.loadAnimation(this, R.anim.anim_logo_batman);
        logoBatman.setAnimation(anim_logo_batman);
        ///////////////////////////////////////
        Animation anim_titulo_spalsh = AnimationUtils.loadAnimation(this,R.anim.anim_titulo_app);
        tituloSplash.setAnimation(anim_titulo_spalsh);
        /////////////////////////////////////////////////////
        anim_logo_batman.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        ///////////////////////////////////////
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
package com.donedeal.donedeal;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import com.donedeal.donedeal.domain.Car;
import com.donedeal.donedeal.fragments.CarFragment;
import com.donedeal.donedeal.fragments.LuxuryCarFragment;
import com.donedeal.donedeal.fragments.OldCarFragment;
import com.donedeal.donedeal.fragments.PopularCarFragment;
import com.donedeal.donedeal.fragments.SportCarFragment;
import me.drakeet.materialdialog.MaterialDialog;


public class CarActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    private Car car;
    private Drawer.Result navigationDrawerLeft;
    private MaterialDialog mMaterialDialog;
    private TextView tvDescription;
    private ViewGroup mRoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TRANSITIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                /*Explode trans1 = new Explode();
                trans1.setDuration(3000);
                Fade trans2 = new Fade();
                trans2.setDuration(3000);

                getWindow().setEnterTransition( trans1 );
                getWindow().setReturnTransition( trans2 );*/

            TransitionInflater inflater = TransitionInflater.from(this);
            Transition transition = inflater.inflateTransition(R.transition.transitions);

            getWindow().setSharedElementEnterTransition(transition);

            Transition transition1 = getWindow().getSharedElementEnterTransition();
            transition1.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onTransitionEnd(Transition transition) {
                    TransitionManager.beginDelayedTransition(mRoot, new Slide());
                    tvDescription.setVisibility(View.VISIBLE);
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        if (savedInstanceState != null) {
            car = savedInstanceState.getParcelable("car");
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getParcelable("car") != null) {
                car = getIntent().getExtras().getParcelable("car");
            } else {
                Toast.makeText(this, "Fail!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(car.getModel());
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        mRoot = (ViewGroup) findViewById(R.id.ll_tv_description);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        ImageView ivCar = (ImageView) findViewById(R.id.iv_car);
        TextView tvModel = (TextView) findViewById(R.id.tv_model);
        TextView tvBrand = (TextView) findViewById(R.id.tv_brand);
        Button solicitTrade = (Button) findViewById(R.id.bt_trade);
        Button btPhone = (Button) findViewById(R.id.bt_phone);

        solicitTrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog = new MaterialDialog(new ContextThemeWrapper(CarActivity.this, R.style.MyAlertDialog))
                        .setTitle("Atenção!")
                        .setMessage("Tem certeza que deseja solicitar a troca?")
                        .setPositiveButton("Sim", new View.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(CarActivity.this, "Sua troca foi solicitada ao João Carlos, ele agora pode ver seus produtos disponíveis para troca.", Toast.LENGTH_LONG).show();
                                mMaterialDialog.dismiss();
                            }
                        })
                        .setNegativeButton("Não", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.show();
            }
        });

        btPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog = new MaterialDialog(new ContextThemeWrapper(CarActivity.this, R.style.MyAlertDialog))
                        .setTitle("Telefone do João")
                        .setMessage(car.getTel())
                        .setPositiveButton("Ligar", new View.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(View v) {
                                Intent it = new Intent(Intent.ACTION_CALL);
                                it.setData(Uri.parse("tel:" + car.getTel().trim()));
                                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for Activity#requestPermissions for more details.
                                    return;
                                }
                                startActivity(it);
                            }
                        })
                        .setNegativeButton("Voltar", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.show();
            }
        });

        ivCar.setImageResource(car.getPhoto());
        tvModel.setText(car.getModel());
        tvBrand.setText(car.getBrand());
        tvDescription.setText( car.getDescription() );

        navigationDrawerLeft = new Drawer()
            .withActivity(this)
            .withToolbar(mToolbar)
            .withActionBarDrawerToggle(false)
            .withCloseOnClick(true)
            .withActionBarDrawerToggleAnimated(false)
                .withActionBarDrawerToggle(new ActionBarDrawerToggle(this, new DrawerLayout(this), R.string.drawer_open, R.string.drawer_close){
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        super.onDrawerSlide(drawerView, slideOffset);
                        navigationDrawerLeft.closeDrawer();
                        finish();
                    }
                })
            .build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("car", car);
    }


    @Override
    public void onBackPressed() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            TransitionManager.beginDelayedTransition(mRoot, new Slide());
            tvDescription.setVisibility( View.INVISIBLE );
        }

        super.onBackPressed();
    }
}

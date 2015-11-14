package com.donedeal.donedeal;

import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.List;

import com.donedeal.donedeal.adapters.TabsAdapter;
import com.donedeal.donedeal.domain.Car;
import com.donedeal.donedeal.domain.Person;
import com.donedeal.donedeal.extras.SlidingTabLayout;


public class MainActivity extends ActionBarActivity {
    private static String TAG = "LOG";

    private Toolbar mToolbar;
    private Drawer.Result navigationDrawerLeft;
    private AccountHeader.Result headerNavigationLeft;
    //private FloatingActionMenu fab;
    private int mItemDrawerSelected;
    private int mProfileDrawerSelected;

    private List<PrimaryDrawerItem> listCatefories;
    private List<Person> listProfile;
    private List<Car> listCars;

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TRANSITIONS
            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
                /*Explode trans1 = new Explode();
                trans1.setDuration(3000);
                Fade trans2 = new Fade();
                trans2.setDuration(3000);

                getWindow().setExitTransition( trans1 );
                getWindow().setReenterTransition( trans2 );*/

                TransitionInflater inflater = TransitionInflater.from( this );
                Transition transition = inflater.inflateTransition( R.transition.transitions );

                getWindow().setSharedElementExitTransition( transition );
            }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            mItemDrawerSelected = savedInstanceState.getInt("mItemDrawerSelected", 0);
            mProfileDrawerSelected = savedInstanceState.getInt("mProfileDrawerSelected", 0);
            listCars = savedInstanceState.getParcelableArrayList("listCars");
        }
        else{
            listCars = getSetCarList(50);
        }

        // TOOLBAR
            mToolbar = (Toolbar) findViewById(R.id.tb_main);
            mToolbar.setTitle("Done Deal");
            //mToolbar.setSubtitle("just a subtitle");
            //mToolbar.setLogo(R.drawable.ic_launcher);
            setSupportActionBar(mToolbar);

            /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(false);*/


        // FRAGMENT
            /*Fragment frag = getSupportFragmentManager().findFragmentByTag("mainFrag");
            if(frag == null) {
                frag = new CarFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.rl_fragment_container, frag, "mainFrag");
                ft.commit();
            }*/


        // TABS
            mViewPager = (ViewPager) findViewById(R.id.vp_tabs);
            mViewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), this));

            mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
            //mSlidingTabLayout.setDistributeEvenly(true);
            mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorFAB));
            mSlidingTabLayout.setCustomTabView(R.layout.tab_view, R.id.tv_tab);
            mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    navigationDrawerLeft.setSelection(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            mSlidingTabLayout.setViewPager(mViewPager);
            //mSlidingTabLayout.setHorizontalFadingEdgeEnabled(true);
            //mSlidingTabLayout.setHorizontalScrollBarEnabled(true);


        // NAVIGATION DRAWER
            // HEADER
                headerNavigationLeft = new AccountHeader()
                    .withActivity(this)
                    .withCompactStyle(false)
                    .withSavedInstance(savedInstanceState)
                    .withThreeSmallProfileImages(true)
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                            Person aux = getPersonByEmail(listProfile, (ProfileDrawerItem) iProfile);
                            mProfileDrawerSelected = getPersonPositionByEmail(listProfile, (ProfileDrawerItem) iProfile);
                            headerNavigationLeft.setBackgroundRes(aux.getBackground());
                            return true;
                        }
                    })
                    .build();

                listProfile = getSetProfileList();
                if(listProfile != null && listProfile.size() > 0){
                    if(mProfileDrawerSelected != 0){
                        Person aux = listProfile.get(mProfileDrawerSelected);
                        listProfile.set(mProfileDrawerSelected, listProfile.get(0));
                        listProfile.set(0, aux);
                    }
                    for(int i = 0; i < listProfile.size(); i++){
                        headerNavigationLeft.addProfile(listProfile.get(i).getProfile(), i);
                    }
                    headerNavigationLeft.setBackgroundRes(listProfile.get(0).getBackground());
                }

            // BODY
                navigationDrawerLeft = new Drawer()
                    .withActivity(this)
                    .withToolbar(mToolbar)
                    .withDisplayBelowToolbar(false)
                    .withActionBarDrawerToggleAnimated(true)
                    .withDrawerGravity(Gravity.START)
                    .withSavedInstance(savedInstanceState)
                    .withActionBarDrawerToggle(true)
                    .withAccountHeader(headerNavigationLeft)
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                            mViewPager.setCurrentItem( i );

                            /*Fragment frag = null;
                            mItemDrawerSelected = i;

                            if(i == 0){ // ALL CARS
                                frag = new CarFragment();
                            }
                            else if(i == 1){ // LUXURY CAR
                                frag = new LuxuryCarFragment();
                            }
                            else if(i == 2){ // SPORT CAR
                                frag = new SportCarFragment();
                            }
                            else if(i == 3){ // OLD CAR
                                frag = new OldCarFragment();
                            }
                            else if(i == 4){ // POPULAR CAR
                                frag = new PopularCarFragment();
                            }

                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.rl_fragment_container, frag, "mainFrag");
                            ft.commit();

                            mToolbar.setTitle( ((PrimaryDrawerItem) iDrawerItem).getName() );*/
                        }
                    })
                    .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                            Toast.makeText(MainActivity.this, "onItemLongClick: " + i, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    })
                    .build();

                listCatefories = getSetCategoryList();
                if(listCatefories != null && listCatefories.size() > 0){
                    for( int i = 0; i < listCatefories.size(); i++ ){
                        navigationDrawerLeft.addItem( listCatefories.get(i) );
                    }
                    navigationDrawerLeft.setSelection(mItemDrawerSelected);
                }


        // FLOATING ACTION BUTTON
            //fab = (FloatingActionMenu) findViewById(R.id.fab);



    }

    /*@Override
    protected void onResume() {
        super.onResume();

        mToolbar.setTitle("Alterei");
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }



    // CATEGORIES
        private List<PrimaryDrawerItem> getSetCategoryList(){
            String[] names = new String[]{"Amigos", "Meus produtos", "Meus Interesses", "Indicar produto", "Compartilhar um produto", "Configurações"};
            int[] icons = new int[]{R.drawable.car_2, R.drawable.car_2, R.drawable.car_2, R.drawable.car_2, R.drawable.car_2, R.drawable.ic_settings};
            int[] iconsSelected = new int[]{R.drawable.car_selected_2, R.drawable.car_selected_2, R.drawable.car_selected_2, R.drawable.car_selected_2, R.drawable.car_selected_2, R.drawable.ic_settings};
            List<PrimaryDrawerItem> list = new ArrayList<>();

            for(int i = 0; i < names.length; i++){
                PrimaryDrawerItem aux = new PrimaryDrawerItem();
                aux.setName( names[i] );
                aux.setIcon(getResources().getDrawable(icons[i]));
                aux.setTextColor(getResources().getColor(R.color.colorPrimarytext));
                aux.setSelectedIcon(getResources().getDrawable(iconsSelected[i]));
                aux.setSelectedTextColor(getResources().getColor(R.color.colorPrimary));

                list.add( aux );
            }
            return(list);
        }



    // PERSON
        private Person getPersonByEmail( List<Person> list, ProfileDrawerItem p ){
            Person aux = null;
            for(int i = 0; i < list.size(); i++){
                if( list.get(i).getProfile().getEmail().equalsIgnoreCase( p.getEmail() ) ){
                    aux = list.get(i);
                    break;
                }
            }
            return( aux );
        }

        private List<Person> getSetProfileList(){
            String[] names = new String[]{"Carolina Ramalho", "Vitor", "Ester", "Pedro"};
            String[] emails = new String[]{"carolinaramalhom@gmail.com", "vitor@sga.pucminas.br", "ester@sga.pucminas.br", "pedro@sga.pucminas.br"};
            int[] photos = new int[]{R.drawable.carol, R.drawable.user, R.drawable.user, R.drawable.user};
            int[] background = new int[]{R.color.fab_material_grey_500, R.color.fab_material_grey_500, R.color.fab_material_grey_500 ,R.color.fab_material_grey_500};
            List<Person> list = new ArrayList<>();

            for(int i = 0; i < names.length; i++){
                ProfileDrawerItem aux = new ProfileDrawerItem();
                aux.setName(names[i]);
                aux.setEmail(emails[i]);
                aux.setIcon(getResources().getDrawable(photos[i]));

                Person p = new Person();
                p.setProfile(aux);
                p.setBackground(background[i]);

                list.add( p );
            }
            return(list);
        }

        private int getPersonPositionByEmail( List<Person> list, ProfileDrawerItem p ){
            for(int i = 0; i < list.size(); i++){
                if( list.get(i).getProfile().getEmail().equalsIgnoreCase( p.getEmail() ) ){
                    return(i);
                }
            }
            return( -1 );
        }



    // CAR
        public List<Car> getSetCarList(int qtd){
            return(getSetCarList(qtd, 0));
        }

        public List<Car> getSetCarList(int qtd, int category){
            String[] models = new String[]{"Teclado Microsoft", "Apple Iphone 3", "Mouse Logitech", "Camera Digital Nikon", "Mp3 hitashi", "Vestido Vermelho", "The Sims", "Monitor Samsung", "Sandália Verde", "Dvd Player Bourachi"};
            String[] brands = new String[]{"Teclados", "Celulares", "Mouses", "Cameras", "Audio", "Vestidos", "Simuladores", "Monitores", "Calçados", "Aparelhos de DVD"};
            int[] categories = new int[]{2, 1, 2, 1, 1, 4, 3, 2, 4, 1};
            int[] photos = new int[]{R.drawable.teclado, R.drawable.iphone3, R.drawable.mouse, R.drawable.camera, R.drawable.mp3, R.drawable.vestido, R.drawable.thesims, R.drawable.monitor, R.drawable.sandalia, R.drawable.dvd};
            String description = "Lorem Ipsum é simplesmente uma simulação de texto da indústria tipográfica e de impressos, e vem sendo utilizado desde o século XVI, quando um impressor desconhecido pegou uma bandeja de tipos e os embaralhou para fazer um livro de modelos de tipos. Lorem Ipsum sobreviveu não só a cinco séculos, como também ao salto para a editoração eletrônica, permanecendo essencialmente inalterado. Se popularizou na década de 60, quando a Letraset lançou decalques contendo passagens de Lorem Ipsum, e mais recentemente quando passou a ser integrado a softwares de editoração eletrônica como Aldus PageMaker.";
            List<Car> listAux = new ArrayList<>();

            for(int i = 0; i < qtd; i++){
                Car c = new Car( models[i % models.length], brands[ i % brands.length], photos[i % models.length] );
                c.setDescription( description );
                c.setCategory( categories[ i % brands.length ] );
                c.setTel("33221155");

                if(category != 0 && c.getCategory() != category){
                    continue;
                }

                listAux.add(c);
            }
            return(listAux);
        }

        public List<Car> getCarsByCategory(int category){
            List<Car> listAux = new ArrayList<>();
            for(int i = 0; i < listCars.size() ; i++){
                if(category != 0 && listCars.get(i).getCategory() != category){
                    continue;
                }

                listAux.add(listCars.get(i));
            }
            return(listAux);
        }

        public List<Car> getListCars(){
            return(listCars);
        }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("mItemDrawerSelected", mItemDrawerSelected);
        outState.putInt("mProfileDrawerSelected", mProfileDrawerSelected);
        outState.putParcelableArrayList("listCars", (ArrayList<Car>) listCars);
        outState = navigationDrawerLeft.saveInstanceState(outState);
        outState = headerNavigationLeft.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if(navigationDrawerLeft.isDrawerOpen()){
            navigationDrawerLeft.closeDrawer();
        }
        /*else if(fab.isOpened()){
            fab.close(true);
        }*/
        else{
            super.onBackPressed();
        }
    }
}

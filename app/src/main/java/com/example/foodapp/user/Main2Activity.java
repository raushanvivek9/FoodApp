package com.example.foodapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.foodapp.About;
import com.example.foodapp.Fragments.AccountFragment;
import com.example.foodapp.Fragments.CartFragment;
import com.example.foodapp.Fragments.HomeFragment;
import com.example.foodapp.Fragments.PNRFragment;
import com.example.foodapp.Help;
import com.example.foodapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main2Activity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private Integer value=1,value1;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment=null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment=new HomeFragment();
                    break;
                case R.id.cart:
                    selectedFragment=new CartFragment();
                    break;
                case R.id.account:
                    selectedFragment=new AccountFragment();
                    break;

                case R.id.pnr:
                    selectedFragment=new PNRFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        checksetup();
        Intent i=getIntent();
        value1=i.getIntExtra("value",0);

        //botom navigation
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Food App");
        setSupportActionBar(toolbar);
        //start from Cart fragment
            if(value1==1 && value==1)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CartFragment()).commit();
                navView.setSelectedItemId(R.id.cart);
            }
            //start from home fragment
            else{
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                navView.setSelectedItemId(R.id.navigation_home);
            }
    }

    private void checksetup() {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser==null)
        {
            Intent i = new Intent(Main2Activity.this, User_login.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Menu; this adds items to the action bar if it is present.
        MenuInflater inflater=getMenuInflater();
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_about)
        {
            Intent intent=new Intent(getApplicationContext(), About.class);
            startActivity(intent);
        }
        else  if(id==R.id.action_help){
            Intent intent=new Intent(getApplicationContext(), Help.class);
            startActivity(intent);
        }
        return true;
    }

    //    @Override
//    public void onBackPressed() {
//        if(backPressedTime + 2000 >System.currentTimeMillis())
//        {
//            backtoast.cancel();
//            super.onBackPressed();
//            return;
//        }
//        else {
//            backtoast=Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
//            backtoast.show();
//        }
//        backPressedTime=System.currentTimeMillis();
//    }


}

package inspection.management.workplace.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inspection.management.workplace.R;
import inspection.management.workplace.fragments.Actions;
import inspection.management.workplace.fragments.Inspection;
import inspection.management.workplace.fragments.Notification;
import inspection.management.workplace.utils.UserData;

public class HomeActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.logout)
    ImageView logout;
    @BindView(R.id.profileImg)
    ImageView profileImg;
    Window window;
    Bundle data;
    SharedPreferences loginPref;
    SharedPreferences.Editor loginPrefsEditor;
    private UserData ud;
    String BaseUrl="http://demo.mywim.nl/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        ud = new UserData(this);
        ud = ud.readFromFile();
        if (ud!=null){
            String imgUri = BaseUrl+"uploads/"+ud.imageUri;
            Log.e("imgUri", " ==  "+ud.imageUri);
            Glide.with(HomeActivity.this).load(imgUri).placeholder(R.drawable.insimg).into(profileImg);

        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //nameToolbarText.setText("");
        loginPref = getSharedPreferences("loginPref", Context.MODE_PRIVATE);
        loginPrefsEditor = loginPref.edit();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Alert!")
                        .setCancelable(false)
                        .setMessage("Do you want to logout")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loginPrefsEditor.clear();
                                loginPrefsEditor.commit();
                                Toast.makeText(HomeActivity.this, "Successfully Logout", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(HomeActivity.this, WorkplaceLogin.class));
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
            }
        });
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch(position){
                case 0 :
                    Inspection inspection = new Inspection();
                    fragment = inspection;
                    break;
                case 1 :
                    Actions actions = new Actions();
                    fragment = actions;
                    break;
                case 2 :
                    Notification notification = new Notification();
                    fragment = notification;
                    break;
            }
            return fragment;
        }
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

}

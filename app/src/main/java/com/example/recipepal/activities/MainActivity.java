package com.example.recipepal.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.example.recipepal.R;
import com.example.recipepal.dialogs.AddGroceryDialog;
import com.example.recipepal.dialogs.AddRecipeDialog;
import com.example.recipepal.fragments.GroceryListFragment;
import com.example.recipepal.fragments.RecipeListFragment;
import com.example.recipepal.helpers.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

//<div>Icons made by <a href="https://www.flaticon.com/authors/those-icons" title="Those Icons">Those Icons</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>
//<div>Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>
//<div>Icons made by <a href="https://www.flaticon.com/authors/eucalyp" title="Eucalyp">Eucalyp</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>
//<div>Icons made by <a href="https://www.flaticon.com/authors/nikita-golubev" title="Nikita Golubev">Nikita Golubev</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>

//https://stackoverflow.com/questions/47714606/viewpager-using-bottom-navigation-view-is-not-swiping-the-fragments (referenced for viewpager with bottom navigation)
//https://stackoverflow.com/questions/29847194/setting-action-bar-title-in-viewpager (referenced for setting fragments's titles)

public class MainActivity extends AppCompatActivity implements AddRecipeDialog.AddRecipeDialogListener, AddGroceryDialog.AddGroceryDialogListener {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    MenuItem prevMenuItem;
    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    RecipeListFragment recipeListFragment;
    GroceryListFragment groceryListFragment;
    static String[] titles = {"Recipes", "Grocery List"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(titles[0]); // initially Recipes

        //BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                //R.id.navigation_recipes, R.id.navigation_grocery_list)
                //.build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(navView, navController);

        bottomNavigationView = findViewById(R.id.nav_view);
        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_recipes:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.navigation_grocery_list:
                        viewPager.setCurrentItem(1);
                        break;
                }

                return false;
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

                // set correct action bar title
                getSupportActionBar().setTitle(titles[position]);
            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        recipeListFragment = new RecipeListFragment();
        viewPagerAdapter.addFragment(recipeListFragment);
        groceryListFragment = new GroceryListFragment();
        viewPagerAdapter.addFragment(groceryListFragment);
        viewPager.setAdapter(viewPagerAdapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

    @Override
    public void applyTexts(String name) {
        recipeListFragment.applyTexts(name);
    }

    @Override
    public void applyTexts(String name, String amount) {
        groceryListFragment.applyTexts(name, amount);
    }
}

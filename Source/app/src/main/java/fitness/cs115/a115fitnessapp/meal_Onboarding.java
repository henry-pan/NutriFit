package fitness.cs115.a115fitnessapp;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Matthew on 11/22/16.
 */

public class meal_Onboarding extends Activity {
    GridView grid;
    ArrayList selectedCategories = new ArrayList<>();//used to store strings that contain user selected genre
    ArrayList selectedPositions = new ArrayList<>();//used to store actual positions that things in the grid are in
    meal_CustomGrid adapter;
    private boolean debug = false;
    String[] genreString = {
            "Scan Bar Code",
            "Manually Add Food",
            "Meals",
            "Add new meal",
            "Day log",
            "Graphs",

    };
    int[] imageId = {
            R.drawable.barcodescanner48,
            R.drawable.vegetarianfoodfilled50,
            R.drawable.meal50,
            R.drawable.diningroom,
            R.drawable.addlist50,
            R.drawable.barchartfilled50,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        ); //makes sure keyboard is hidden on this view as it is not needed
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);


        grid = (GridView) findViewById(R.id.grid);
        //deals with settings the number of Columns
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        double dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        if (debug) {
            Toast.makeText(this, "Debug: the screenwidth is " + dpWidth, Toast.LENGTH_LONG).show();
        }
        if (dpWidth <= 400) {
            grid.setNumColumns(2);
        } else {
            grid.setNumColumns(3);
        }
        adapter = new meal_CustomGrid(meal_Onboarding.this, genreString, imageId);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //    Toast.makeText(meal_Onboarding.this, "You Clicked at position"+position+" " + genreString[+position], Toast.LENGTH_SHORT).show(); //debug
                //the position is the actual position that is pressed. but the getChildAt returns gets the relative position, ie what's being drawn
                //on the grid.
                //to fix this, the first visible position is obtained and the difference is subtracted
                int firstPos = grid.getFirstVisiblePosition();
                View tv = grid.getChildAt(position - firstPos);
                if (debug) {
                    Toast.makeText(meal_Onboarding.this, "You Clicked at position" + position + " " + genreString[+position] +
                            " firstpos is " + firstPos, Toast.LENGTH_SHORT).show();
                }
                if (tv == null) { //this shouldn't/doesn't happen
                    Log.e("OnBoarding.java", "Issue with null view at line " + Thread.currentThread().getStackTrace()[2].getLineNumber() +
                            "grid is having a problem");
                    return;
                }
                if (selectedCategories.contains(genreString[+position]) == false) {//add the item if it doesn't exist
                    selectedCategories.add(genreString[+position]);
                    selectedPositions.add(Integer.toString(position));
              //      tv.setBackgroundColor(Color.parseColor("#FFE5CD"));
                } else { //remove the item if it already exists, as the user is removing it by touching it again
                    selectedCategories.remove(genreString[+position]);
                    selectedPositions.remove(Integer.toString(position));
                  //  tv.setBackgroundColor(Color.parseColor("#EEEEEE"));
                }
            }
        });

        grid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Log.d("meal_Onboarding 7331", "onScroll: onScroll " + firstVisibleItem + " " + selectedPositions.toString()); //debug
                for (int i = 0; i < totalItemCount; i++) {
                    View tv = grid.getChildAt(i - firstVisibleItem);
                    if (tv == null) {
                        continue;
                    }
                    if (selectedPositions.contains(Integer.toString(i))) {
                    //    tv.setBackgroundColor(Color.parseColor("#FFE5CD"));
                    } else {
                    //    tv.setBackgroundColor(Color.parseColor("#EEEEEE"));
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //     Log.d("meal_Onboarding 7331", "onScroll: state changed "+selectedPositions.toString()); //debug
            }
        });
    }




}
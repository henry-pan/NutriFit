package fitness.cs115.a115fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Matthew on 10/6/16.
 */

public class addItemToMeal extends AppCompatActivity {
    private static final boolean DEBUG = true;
    private EditText foodName;
    private EditText calories;

    private DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_to_meal);

        foodName = (EditText) findViewById(R.id.foodName);
        calories = (EditText) findViewById(R.id.calories);
        //get data passed into intent? maybe which meal an item is being added to is passed in????
        Intent intent = getIntent();
        //item = intent.getStringExtra("stuffz");


        //not all DBHelper functions tested yet
        //hardcoded stuff to test database
        mydb = new DBHelper(this);
        if (DEBUG) {
            mydb.insertFood("hotdog", 200.2);
            mydb.insertFood("hotdog", 400.87);//shouldn't be added since hotdog already in database
            mydb.insertFood("cat", 300.0);
            mydb.insertFood("orange", 120.5);
        }
        System.out.println(mydb.getAllFoods());
        System.out.println(mydb.getAllFoodInfo());
        System.out.println("number of rows/items is: " + mydb.getNumberOfRows());

        Button confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check that both fields are filled in and display error message if not
                String name = foodName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    foodName.setError("Enter a food's name");
                    return;
                }
                String caloriesString = calories.getText().toString();
                if (TextUtils.isEmpty(caloriesString)) {
                    calories.setError("Enter the number of calories");
                    return;
                }

                //hide keyboard
                View view1 = getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }

                double numberOfCalories = Double.parseDouble(caloriesString);
                mydb.insertFood(name, numberOfCalories);//insert food to database
                if (DEBUG) {
                    System.out.println(mydb.getAllFoods());
                    System.out.println(mydb.getAllFoodInfo());
                    System.out.println("number of rows/items is: " + mydb.getNumberOfRows());


                    Intent intent = new Intent(addItemToMeal.this, MainActivity.class);
                    startActivity(intent);

                }

                //launch intent here


            }
        });

    }

}
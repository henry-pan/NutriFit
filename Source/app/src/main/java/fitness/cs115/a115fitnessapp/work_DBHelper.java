package fitness.cs115.a115fitnessapp;

/**
 * Created by James Kennedy on 10/17/16.
 *
 * parts of this file were adapted from:
 * https://www.tutorialspoint.com/android/android_sqlite_database.htm
 */
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.PriorityQueue;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
/**
 * This class serves as an interface for adding, modifying and retrieving data
 * from the user workout database.
 *
 * @author James Kennedy
 * @version %I%   %G%
 */
public class work_DBHelper extends SQLiteOpenHelper {

    private static final Boolean DEBUG = false;
    public static final String DATABASE_NAME = "user_work.db";

    /*
    WORK_INDEX: This table will serve as an index of all of the user's created
     workouts. The fields are: work_id (integer unique to each workout);
     name(of the workout, given by user); created_on (datetime string);
     last_used (datetime string).
    */
    public static final String WORK_INDEX_TABLE_NAME = "work_index";
    public static final String WORK_INDEX_COL_WORK_ID = "work_id";
    public static final String WORK_INDEX_COL_WORK_NAME = "work_name";
    public static final String WORK_INDEX_COL_CREATED_ON = "created_on";
    public static final String WORK_INDEX_COL_LAST_USED = "last_used";
    /*
    WORK_DETAIL: this table will store the list of exercises that make up each
    of the user's workouts. Each row will represent one exercise. The fields
    are: work_id (corresponding to the id assigned to each workout in
    WORK_INDEX table); exercise_id (exercise id, to retrieve data from the
    master exercise database);  name (exercise name given by user); type
    ("rep" or "timed", repetition based vs. timed exercise); sets (goal # of
    sets that should be done for that exercise); reps(goal repetitions per set);
    weight.
    */
    public static final String WORK_DETAIL_TABLE_NAME = "work_detail";
    public static final String WORK_DETAIL_COL_WORK_ID = "work_id";
    public static final String WORK_DETAIL_COL_EXER_ID = "exer_id";
    public static final String WORK_DETAIL_COL_EXER_NAME = "exer_name";
    public static final String WORK_DETAIL_COL_TYPE = "exer_type";
    public static final String WORK_DETAIL_COL_SETS = "sets";
    public static final String WORK_DETAIL_COL_REPS = "reps";
    public static final String WORK_DETAIL_COL_WEIGHT = "weight";
    /*
    WORK_LOG: This table stores the actual exercise log for each time the user
     chooses to do a workout. Each row corresponds to one set of an exercise in
     a workout. Fields: session_id(unique id assigned to each workout iteration.
     Rows have the same session_id if they belong to the same workout session);
     exercise_id (corresponds to exercise_id field of that exercise in
     work_detail table); set_num (current set number for the row); goal(goal
     time or goal number of reps, depending on exercise_type for the given
     exercise); actual (time or reps actually completed); weight.
     */
    public static final String WORK_LOG_TABLE_NAME = "work_log";
    public static final String WORK_LOG_COL_SESSION_ID = "session_id";
    public static final String WORK_LOG_COL_EXER_ID = "exer_id";
    public static final String WORK_LOG_COL_SET_NUM = "set_num";
    public static final String WORK_LOG_COL_GOAL = "goal";
    public static final String WORK_LOG_COL_ACTUAL = "actual";
    public static final String WORK_LOG_COL_WEIGHT = "weight";
    /*
    WORK_SESSIONS: This table stores a log for each time the user chooses to
    begin a workout. The fields are: session_id (unique id assigned to each
    workout iteration); work_id (corresponding to work_id of the workout from
    work_index table); datetime (time session was initiated, stored as text
    in ISO-8601 date/time format).
     */
    public static final String WORK_SESSIONS_TABLE_NAME = "work_sessions";
    public static final String WORK_SESSIONS_COL_SESSION_ID = "session_id";
    public static final String WORK_SESSIONS_COL_WORK_ID = "work_id";
    public static final String WORK_SESSIONS_COL_DATETIME = "date_time";
    /*
    PROG_INDEX: stores a list of the program(s) that have been saved.
     */
    public static final String PROG_INDEX_TABLE_NAME = "prog_index";
    public static final String PROG_INDEX_COL_PROG_ID = "prog_id";
    public static final String PROG_INDEX_COL_PROG_NAME = "prog_name";
    public static final String PROG_INDEX_COL_CREATED_ON = "created_on";
    public static final String PROG_INDEX_COL_LAST_USED = "last_used";
    /*
    PROG_DETAIL: stores a list of workouts according to the program(s) they
     belong to.
     */
    public static final String PROG_DETAIL_TABLE_NAME = "prog_detail";
    public static final String PROG_DETAIL_COL_PROG_ID = "prog_id";
    public static final String PROG_DETAIL_COL_WORK_ID = "work_id";
    public static final String PROG_DETAIL_COL_LAST_USED = "last_used";

    //List of all tables in the work DB
    public static final String[] WORK_DB_TABLES= {WORK_INDEX_TABLE_NAME,
        WORK_DETAIL_TABLE_NAME,WORK_LOG_TABLE_NAME, WORK_SESSIONS_TABLE_NAME,
        PROG_DETAIL_TABLE_NAME, PROG_INDEX_TABLE_NAME};

    public static final ArrayList<String> TABLE_LIST =
            new ArrayList<String>(Arrays.asList(WORK_DB_TABLES));

    public work_DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (DEBUG) {
            System.out.println("work_DBHelper: onCreate-ed");
        }
        //create tables if they don't yet exist
        db.execSQL( "CREATE TABLE IF NOT EXISTS " + WORK_INDEX_TABLE_NAME + " ("
            + WORK_INDEX_COL_WORK_ID + " integer primary key, "
            + WORK_INDEX_COL_WORK_NAME + " text, "
            + WORK_INDEX_COL_CREATED_ON + " text, "
            + WORK_INDEX_COL_LAST_USED + " text )"
        );

        db.execSQL( "CREATE TABLE IF NOT EXISTS " + WORK_DETAIL_TABLE_NAME + " "
            + "(" + WORK_DETAIL_COL_WORK_ID + " integer, "
            + WORK_DETAIL_COL_EXER_ID + " integer, "
            + WORK_DETAIL_COL_EXER_NAME + " text, "
            + WORK_DETAIL_COL_TYPE + " text, "
            + WORK_DETAIL_COL_SETS + " text, "
            + WORK_DETAIL_COL_REPS + " text, "
            + WORK_DETAIL_COL_WEIGHT + " text,"
            + "primary key (" +WORK_DETAIL_COL_WORK_ID +", "
            + WORK_DETAIL_COL_EXER_NAME
            + ") )"
                //TODO: set primary key to EXER_ID once Exer table implemented
        );
        db.execSQL( "CREATE TABLE IF NOT EXISTS " + WORK_LOG_TABLE_NAME + " "
            + "(" + WORK_LOG_COL_SESSION_ID + " integer, "
            + WORK_LOG_COL_EXER_ID + " integer, "
            + WORK_LOG_COL_SET_NUM + " integer, "
            + WORK_LOG_COL_GOAL + " integer, "
            + WORK_LOG_COL_ACTUAL + " integer, "
            + WORK_LOG_COL_WEIGHT + " integer, "
            + "primary key (" +WORK_LOG_COL_SESSION_ID +", "
            + WORK_LOG_COL_EXER_ID
            + ") )"
        );

        db.execSQL( "CREATE TABLE IF NOT EXISTS " + WORK_SESSIONS_TABLE_NAME
            + " (" + WORK_SESSIONS_COL_SESSION_ID + " integer primary key, "
            + WORK_SESSIONS_COL_WORK_ID + " integer, "
            + WORK_SESSIONS_COL_DATETIME + " text "
            + ")"
        );
        db.execSQL( "CREATE TABLE IF NOT EXISTS " + PROG_INDEX_TABLE_NAME + " "
                + "(" + PROG_INDEX_COL_PROG_ID + " integer primary key, "
                + PROG_INDEX_COL_PROG_NAME + " text, "
                + PROG_INDEX_COL_CREATED_ON + " text, "
                + PROG_INDEX_COL_LAST_USED + " text "
                + ")"
        );
        db.execSQL( "CREATE TABLE IF NOT EXISTS " + PROG_DETAIL_TABLE_NAME + " "
                + "(" + PROG_DETAIL_COL_PROG_ID + " integer, "
                + PROG_DETAIL_COL_WORK_ID + " integer, "
                + PROG_DETAIL_COL_LAST_USED + " text, "
                + "primary key (" + PROG_DETAIL_COL_PROG_ID + ", "
                + PROG_DETAIL_COL_WORK_ID
                + ") )"
        );

    }

    public void reset_default_values() {
        if (DEBUG)
        {
            System.out.println( "" + new Exception().getStackTrace()[0]);
            System.out.println("starting work_DBHelper.reset_default_values()");
        }

        clear_all_tables();

        create_program("Default Program 1");
        create_program("Default Program 2");
        create_program("Default Program 3");

        create_workout("Chest and Tri's");
        create_workout("Back and Biceps");
        create_workout("Leg Day");
        create_workout("Easy Day");
        create_workout("EXXTREEME");

        add_work_to_prog("Default Program 1", "Chest and Tri's");
        add_work_to_prog("Default Program 1", "Back and Biceps");
        add_work_to_prog("Default Program 1", "Leg Day");
        add_work_to_prog("Default Program 2", "Easy Day");
        add_work_to_prog("Default Program 2", "EXXTREEME");
        add_work_to_prog("Default Program 3","Leg Day");

        add_exer_to_work("Chest and Tri's","Bench Press","rep",3,10,100);
        add_exer_to_work("Chest and Tri's","Dumbell Press","rep",3,10,80);
        add_exer_to_work("Chest and Tri's","Pushups","rep",4,20,0);
        add_exer_to_work("Chest and Tri's","Dumbell Tricep Extension, Overhead",
                "rep",4,15,50);
        add_exer_to_work("Back and Biceps","Pullups","rep",4,10,0);
        add_exer_to_work("Back and Biceps","Dumbell Curls","rep",4,10,30);
        add_exer_to_work("Back and Biceps","Planks","timed",4,60,0);
        add_exer_to_work("Leg Day","Squats","rep",4,10,60);
        add_exer_to_work("Leg Day","Lunges","rep",4,10,30);
        add_exer_to_work("Leg Day","Planks","rep",4,60,0);
        add_exer_to_work("Leg Day","Run a mile","timed",1,10,0);
        add_exer_to_work("EXXTREEME","Bench Press","rep",3,10,100);
        add_exer_to_work("EXXTREEME","Dumbell Press","rep",3,10,80);
        add_exer_to_work("EXXTREEME","Pushups","rep",4,20,0);
        add_exer_to_work("EXXTREEME","Dumbell Tricep Extension, Overhead",
                "rep",4,15,50);
        add_exer_to_work("EXXTREEME","Pullups","rep",4,10,0);
        add_exer_to_work("EXXTREEME","Dumbell Curls","rep",4,10,30);
        add_exer_to_work("EXXTREEME","Planks","timed",4,60,0);
        add_exer_to_work("EXXTREEME","Squats","rep",4,10,60);
        add_exer_to_work("EXXTREEME","Lunges","rep",4,10,30);
        add_exer_to_work("EXXTREEME","Run a mile","timed",1,10,0);

        if (DEBUG)
        {
            //mark current line num
            System.out.println( "" + new Exception().getStackTrace()[0]);
            System.out.println("work_DBHelper.reset_default_values() finished");
            dump_tables();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        drop_all_tables();

        onCreate(db);
    }

    //returns true if programName is found in the prog_index
    public boolean is_taken_prog_name(String programName) {
        int found = get_prog_id_from_name(programName);

        if (found == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    //returns true if workName is found in the work_index
    public boolean is_taken_work_name(String workName) {
        int found = get_work_id_from_name(workName);
        if (found == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * creates a new entry in prog_index and returns it's prog_id.
     *
     * Precondition: programName must not be taken. Check using
     * is_valid_prog(String) before using this function. Throws unchecked
     * RuntimeException if the precondition is violated.
     */
    public int create_program(String program_name) {
        long new_prog_id = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROG_INDEX_COL_PROG_NAME, program_name);
        contentValues.put(PROG_INDEX_COL_CREATED_ON, current_time_ISO8601());
        contentValues.put(PROG_INDEX_COL_LAST_USED, "never");

        //invariant: program_name doesn't already exist in prog_index
        if (is_taken_prog_name(program_name))
        {
            throw new RuntimeException("Error creating program: name \"" +
                    program_name + "\" is already taken.");
        }
        new_prog_id = db.insert(PROG_INDEX_TABLE_NAME, null, contentValues);

        if (DEBUG) {
            if (new_prog_id > 0) {
                System.out.println(program_name+" successfully inserted into "
                        + PROG_INDEX_TABLE_NAME + " with prog_id: "+new_prog_id
                );
            } else {
                System.out.println(program_name + "create_program failed for: "
                        + PROG_INDEX_TABLE_NAME);
            }
        }
        db.close();

        return (int) new_prog_id;
    }

    /**
     * Adds an existing workout to an existing program.
     *
     * @param prog_name program to be added to. must already exist, use
     *                  is_taken_prog_name(String) to check.
     * @param work_name workout to be added. If it doesn't already exist, an
     *                  empty workout with this name will be created.
     * @return the work_id assigned to the workout. -1 for failure.
     */
    public int add_work_to_prog(String prog_name, String work_name)
    {
        long conf_prog_id = -1;
        int prog_id = get_prog_id_from_name(prog_name);
        int work_id = get_work_id_from_name(work_name);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (DEBUG) {
            System.out.println("starting add_work_to_prog: "
                    + new Exception().getStackTrace()[0]);
        }
        //invariant: workout and program must exist
        if ( !is_taken_prog_name(prog_name)) {
            throw new RuntimeException("Error adding work to prog: program\"" +
                    prog_name + "\" was not found.");
        }
        if ( !is_taken_work_name(work_name)) {
            throw new RuntimeException("Error adding work to prog: workout \"" +
                    work_name + "\" was not found.");
        }

        contentValues.put(PROG_DETAIL_COL_PROG_ID, prog_id);
        contentValues.put(PROG_DETAIL_COL_WORK_ID, work_id);
        contentValues.put(PROG_DETAIL_COL_LAST_USED, "never");

        //TODO: insert invariant that workout isnt already part of program
        conf_prog_id = db.insert(PROG_DETAIL_TABLE_NAME, null, contentValues);
        if (DEBUG) {
            if (conf_prog_id == -1)
                System.out.println(work_name + "failed to insert in  "
                        + prog_name);
            else
                System.out.println(work_name + " inserted in  " + prog_name);
        }
        db.close();
        return (int) conf_prog_id;
    }
    /*
    This function returns a String ArrayList containing the user's program names
     */
    public ArrayList<String> get_user_program_list() {
        ArrayList<String> p_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery(
                "select " + PROG_INDEX_COL_PROG_NAME + " from " +
                        PROG_INDEX_TABLE_NAME, null);

        res.moveToFirst();
        while ( !res.isAfterLast() ) {
            //add the current row in the table
            p_list.add(res.getString(res.getColumnIndex(
                    PROG_INDEX_COL_PROG_NAME) ) );
            res.moveToNext();
        }
        res.close();

        return p_list;
    }

    /**
     * creates a new workout in work_index with the name passed to the function.
     * Workout name must not match an already existing workout. Use
     * @see is_taken_work_name(String) to check precondition.
     * note: defaults "never" for last_used field.
     * @param workout_name must not match the name of any existing workout.
     *        Cannot be null.
     * @return work_id of inserted workout, or -1 if an error occurred
     */
    public int create_workout(String workout_name) {
        long inserted_id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //invariant: workout name must not be empty
        if (workout_name == null || workout_name.length() == 0)
        {
            throw new RuntimeException ("Error creating workout: "
                    + "no workout name has been specified.");
        }

        contentValues.put(WORK_INDEX_COL_WORK_NAME, workout_name);
        contentValues.put(WORK_INDEX_COL_CREATED_ON, current_time_ISO8601() );
        contentValues.put(WORK_INDEX_COL_LAST_USED, "never");

        //TODO: include insert invariant that db is not full


        inserted_id = db.insert(WORK_INDEX_TABLE_NAME, null, contentValues);
        db.close();
        return (int) inserted_id;
    }

    /**
     * Adds an exercise to an existing workout.
     *
     * @param work_name name of the workout to be added to.
     * @see is_valid_workout(String) to check preconditions.
     * @param exer_type exercise type, either "rep" or "timed"
     * @param num_of_sets goal. use 1 for timed exercise types.
     * @param reps_per_set goal. used to populate work log targets.
     * @param weight weight lifted, for weighted exercises.
     * @return inserted item's work_id, or -1 if insertion fails.
     */
    public int add_exer_to_work(String work_name, String exer_name,
                String exer_type, int num_of_sets, int reps_per_set, int weight)
    {
        long confirm_row_id = -1;
        int work_id = get_work_id_from_name(work_name);
        int exer_id = get_exer_id_from_name(exer_name);
        ArrayList<String> exers_in_work;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //invariant: workout already exists
        if (!is_taken_work_name(work_name)) {
            throw new RuntimeException("Error adding exercise \"" + exer_name +
                    "\" to workout \"" + work_name +
                    "\": workout does not exist.");
        }
        //invariant: exercise is not already part of program
        exers_in_work = get_exers_from_work(work_name);
        if (exers_in_work.contains(exer_name)){
            throw new RuntimeException("Error adding exercise \"" + exer_name +
                    "\" to workout \"" + work_name + "\": " +
                    "this exercise is already part of this workout.");
        }

        contentValues.put(WORK_DETAIL_COL_WORK_ID, work_id);
        contentValues.put(WORK_DETAIL_COL_EXER_ID, exer_id);
        contentValues.put(WORK_DETAIL_COL_EXER_NAME, exer_name);
        contentValues.put(WORK_DETAIL_COL_TYPE, exer_type);
        contentValues.put(WORK_DETAIL_COL_SETS, num_of_sets);
        contentValues.put(WORK_DETAIL_COL_REPS, reps_per_set);
        contentValues.put(WORK_DETAIL_COL_WEIGHT, weight);

        //TODO:amend to create exercise if it doesnt already exist

        confirm_row_id = db.insert(WORK_DETAIL_TABLE_NAME, null, contentValues);
        db.close();
        return (int) confirm_row_id;
    }

    /**
     * Retrieves the names of the exercises assigned to the given workout.
     *
     * @param work_name the workout to retrieve. Must have already been created.
     * @return list of exercise names that have been assigned to work_name. Will
     *         return an empty list if no exercises have been added yet.
     * @see is_taken_work_name(String) to check that work_name has been created.
     */
    public ArrayList<String> get_exers_from_work(String work_name) {
        ArrayList<String> exer_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        int work_id = get_work_id_from_name(work_name);
        String exer;
        //invariant: workout already exists
        if (!is_taken_work_name(work_name)) {
            throw new RuntimeException("Error getting exercises from workout \""
                    + work_name + "\": workout does not exist.");
        }

        //retrieve the names of all exercises from the desired workout
        Cursor res = db.rawQuery(
                "select " + WORK_DETAIL_COL_EXER_NAME + " from "
                        + WORK_DETAIL_TABLE_NAME +    " where "
                        + WORK_DETAIL_COL_WORK_ID + " = ?",
                new String[] {String.valueOf(work_id)});

        res.moveToFirst();
        while ( !res.isAfterLast() ) {
            //add the current workout name to the list of names
            exer = res.getString(res.getColumnIndex(WORK_DETAIL_COL_EXER_NAME));
            exer_list.add(exer);
            res.moveToNext();
        }
        res.close();

        return exer_list;
    }


    /**
     * Retrieves the names of the exercise assigned to the given program.
     *
     * @param prog_name program to retrieve. Must have already been created.
     * @return list of workout names that have been assigned to the program.
     *         Returns an empty list if no workouts have been added yet.
     * @see is_taken_prog_name(String) to check that prog_name has been created.
     */
    public ArrayList<String> get_workouts_from_prog (String prog_name) {
        ArrayList<String> w_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        int prog_id = get_prog_id_from_name(prog_name);
        int work_id = -1;
        //retrieve the id's of all workouts from the desired program
        Cursor res = db.rawQuery(
                "select " + PROG_DETAIL_COL_WORK_ID + " from "
                + PROG_DETAIL_TABLE_NAME + " where "
                + PROG_DETAIL_COL_PROG_ID + " = ?",
                new String[] {String.valueOf(prog_id)}
        );

        res.moveToFirst();
        while ( !res.isAfterLast() ) {
            //add the current workout name to the list of names
            work_id = res.getInt(res.getColumnIndex(PROG_DETAIL_COL_WORK_ID));
            w_list.add(get_work_name_from_id(work_id));
            res.moveToNext();
        }
        res.close();

        return w_list;
    }

    /*
    This function creates a new session entry in work_sessions for the workout
    specified and returns it's session_id.
     */
    public int create_session(String work_name) {
        long session_id = -1;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        int work_id = get_work_id_from_name(work_name);

        contentValues.put("work_id", work_id);
        contentValues.put("date_time", current_time_ISO8601());

        //TODO: include insert invariant that db is not full
        //invariant:work_name exists in work_index
        if (! is_taken_work_name(work_name)) {
            throw new RuntimeException("Error creating session: workout \""
                    + work_name + "\" does not exist.");
        }

        session_id = db.insert(WORK_SESSIONS_TABLE_NAME, null, contentValues);

        db.close();
        return (int) session_id;
    }

    public int create_work_log (int session_id)
    {
        //TODO: fill this function
        return -1;
    }

    //gets contents of the table specified as a String
    private String get_table(String table_name) {
        //col_padding used to add empty space between columns
        int col_padding = 2;
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();

        //invariant: table_name is a valid table
        if ( !TABLE_LIST.contains(table_name))
        {
            throw new RuntimeException("Error getting table: \""
                    + table_name + "\": not a valid table name.");
        }

        Cursor res = db.rawQuery("select * from " + table_name, null);
        String[] col_names = res.getColumnNames();
        String output="";
        String row_str="", col_entry ="";
        int num_cols= res.getColumnCount();
        int num_rows= res.getCount();
        int max_col_widths[] = new int [num_cols];
        //str_len_priorityQs used to find length of longest string in each column
        ArrayList<PriorityQueue<Integer>> str_len_priorityQs =
                new ArrayList<PriorityQueue<Integer>>(num_cols);
        //col_data contains one ArrayList to represent each column of data
        ArrayList<ArrayList<String>> col_data =
                new ArrayList<ArrayList<String>>(num_cols);

        //initialize column data list and priority Qs
        for (int col_i =0; col_i < num_cols; ++col_i){
            //size of each column List is num_rows + 1 for column names
            col_data.add( new ArrayList<String> (num_rows+1));
            str_len_priorityQs.add( new PriorityQueue<Integer>(num_rows+1,
                    Collections.reverseOrder() ));
        }
        //load column names
        for (int col_j = 0; col_j < num_cols; ++col_j) {
            col_data.get(col_j).add(col_names[col_j]);
            str_len_priorityQs.get(col_j).add(Integer.valueOf(
                    col_names[col_j].length()));
        }

        res.moveToFirst();
        //retrieve all column entries to calc col_width for each column
        while (!res.isAfterLast() ) {
            //retrieve the current row in the table
            for (int col_i = 0; col_i < num_cols; ++col_i)
            {
                //add data to its respective column List
                col_entry = res.getString(res.getColumnIndex(col_names[col_i]));
                col_data.get(col_i).add(col_entry);
                str_len_priorityQs.get(col_i).add(col_entry.length());
            }
            res.moveToNext();
        }
        res.close();

        //get max string length of each column from Priority Q if one exists
        for (int col_i = 0; col_i < num_cols; ++col_i) {
            max_col_widths[col_i] = str_len_priorityQs.get(col_i).size() > 0 ?
                str_len_priorityQs.get(col_i).peek().intValue() + col_padding :
                    col_padding;
        }

        //print formatted columns to output String
        for (int row_i = 0; row_i < num_rows; ++row_i){
            row_str ="";
            for (int col_j = 0; col_j < num_cols; ++col_j){
                col_entry = col_data.get(col_j).get(row_i);
                //print entries with the width associated with their column
                row_str += String.format("%-"+max_col_widths[col_j]+"s",
                        col_entry);
            }
            output += row_str +"\n";
        }

        return output;

    }

    //gets number of items (rows) in the table specified
    public int get_num_rows(String table_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, table_name);
        return numRows;
    }

    public void clear_all_tables() {
        SQLiteDatabase db = this.getWritableDatabase();
        int num_tables = WORK_DB_TABLES.length;

        if (DEBUG) {
            System.out.println("Clearing all tables.");
        }

        for (int ind= 0; ind < num_tables; ++ind)
        {
            db.execSQL("delete from " + WORK_DB_TABLES[ind]);
        }
        db.close();
    }

    //prints all tables to console
    public void dump_tables(){
        System.out.println("prog_index: \n" +
                this.get_table(work_DBHelper.PROG_INDEX_TABLE_NAME));
        System.out.println("prog_detail: \n" +
                this.get_table(work_DBHelper.PROG_DETAIL_TABLE_NAME));
        System.out.println("work_index: \n" +
                this.get_table(work_DBHelper.WORK_INDEX_TABLE_NAME));
        System.out.println("work_detail: \n" +
                this.get_table(work_DBHelper.WORK_DETAIL_TABLE_NAME));
        System.out.println("work_sessions: \n" +
                this.get_table(work_DBHelper.WORK_SESSIONS_TABLE_NAME));
        System.out.println("work_log: \n" +
                this.get_table(work_DBHelper.WORK_LOG_TABLE_NAME));

    }

    /*
    Returns current date and time as ISO 8601 formatted String
    Date conversion found here:
    http://beginnersbook.com/2013/05/current-date-time-in-java/
     */
    private String current_time_ISO8601() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-DD HH:mm");
        Date dateobj = new Date();
        String date_str = df.format(dateobj);

        return date_str;
    }

    //returns prog_id if found, -1 if not found
    private int get_prog_id_from_name(String prog_name) {

        if (DEBUG) {
            System.out.println("starting: " + new Exception().getStackTrace()[0]);
        }

        SQLiteDatabase db = this.getReadableDatabase();
        int prog_id = -1;

        Cursor res = db.query(PROG_INDEX_TABLE_NAME, new String[] {PROG_INDEX_COL_PROG_ID},
                PROG_INDEX_COL_PROG_NAME + " = ?", new String[] {prog_name},
                null, null, null);

        res.moveToFirst();

        if(!res.isAfterLast() ) {
            prog_id = res.getInt(res.getColumnIndex(PROG_INDEX_COL_PROG_ID));
        }
        if (DEBUG) {
            System.out.println("prog_id: " + prog_id + "  found for prog_name: "
                    + prog_name);
        }
        res.close();

        return prog_id;
    }

    //returns work_id if found, -1 if not found
    private int get_work_id_from_name(String work_name) {

        if (DEBUG) {
            System.out.println("starting: " +
                    new Exception().getStackTrace()[0]);
        }

        SQLiteDatabase db = this.getReadableDatabase();
        int work_id = -1;

        Cursor res = db.rawQuery("select " + WORK_INDEX_COL_WORK_ID + " from "
                + WORK_INDEX_TABLE_NAME + " where " + WORK_INDEX_COL_WORK_NAME
                + " = ?", new String[] {work_name} );

        res.moveToFirst();
        if(!res.isAfterLast() ) {
            work_id = res.getInt(res.getColumnIndex(WORK_INDEX_COL_WORK_ID));
        }
        if (DEBUG) {
            System.out.println("work_id: " + work_id + "  found for work_name: " + work_name);
        }
        res.close();

        return work_id;
    }

    private String get_work_name_from_id(int work_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String work_name="";

        //retrieve row with desired work_id
        Cursor res = db.query(WORK_INDEX_TABLE_NAME, new String[] {
                WORK_INDEX_COL_WORK_NAME},  WORK_INDEX_COL_WORK_ID + " =? ",
                new String[] {String.valueOf(work_id)},
                null, null, null);

        res.moveToFirst();
        if(!res.isAfterLast() ) {
            work_name = res.getString(res.getColumnIndex(WORK_INDEX_COL_WORK_NAME));
        }
        res.close();

        if (DEBUG) {
            System.out.println("work_name: " + work_name + "  found for work_id: " + work_id);
        }
        return work_name;
    }

    //returns work_id if found, -1 if not found
    private int get_exer_id_from_name(String exer_name) {

        //TODO: stub for now. retrieve exercise name from master exercise DB when implemented.
        return -1;
    }

    private void drop_all_tables() {
        SQLiteDatabase db = this.getWritableDatabase();
        int num_tables = WORK_DB_TABLES.length;
        if (DEBUG)
        {
            System.out.println("dropping tables like flies");
        }
        for (int ind= 0; ind < num_tables; ++ind)
        {
            db.execSQL("DROP TABLE IF EXISTS " + WORK_DB_TABLES[ind]);
        }
        db.close();
    }
}
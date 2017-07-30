package com.example.android.waitlist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.android.waitlist.data.WaitlistContract;
import com.example.android.waitlist.data.WaitlistDbHelper;


public class MainActivity extends AppCompatActivity
    implements GuestListAdapter.GuestAdapterOnClickListerner  {

    private GuestListAdapter mAdapter;
    private SQLiteDatabase mDb;
    private EditText mNewGuestNameEditText;
    private EditText mNewPartySizeEditText;
    //private EditText mNewPhoneNoEditText;
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView waitlistRecyclerView;

        // Set local attributes to corresponding views
        waitlistRecyclerView = (RecyclerView) this.findViewById(R.id.all_guests_list_view);
        mNewGuestNameEditText = (EditText) this.findViewById(R.id.person_name_edit_text);
        mNewPartySizeEditText = (EditText) this.findViewById(R.id.party_count_edit_text);
       // mNewPhoneNoEditText=(EditText) this.findViewById(R.id.phone_no_edit_text);

        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        waitlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Create a DB helper (this will create the DB if run for the first time)
        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();

        // Get all guest info from the database and save in a cursor
        Cursor cursor = getAllGuests();

        // Create an adapter for that cursor to display the data
        mAdapter = new GuestListAdapter(this, cursor,this);

        // Link the adapter to the RecyclerView
        waitlistRecyclerView.setAdapter(mAdapter);


        //TODO (3) Create a new ItemTouchHelper with a SimpleCallback that handles both LEFT and RIGHT swipe directions
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

              long id=(long) viewHolder.itemView.getTag();
                removeGuest(id);
                mAdapter.swapCursor(getAllGuests());
            }

        }).attachToRecyclerView(waitlistRecyclerView);

        // TODO (4) Override onMove and simply return false inside

        // TODO (5) Override onSwiped

        // TODO (8) Inside, get the viewHolder's itemView's tag and store in a long variable id

        // TODO (9) call removeGuest and pass through that id
        // TODO (10) call swapCursor on mAdapter passing in getAllGuests() as the argument

        //TODO (11) attach the ItemTouchHelper to the waitlistRecyclerView

    }

    /**
     * This method is called when user clicks on the Add to waitlist button
     *
     * @param view The calling view (button)
     */
    public void addToWaitlist(View view) {
        if (mNewGuestNameEditText.getText().length() == 0 ||
                mNewPartySizeEditText.getText().length() == 0) {
            return;
        }
        //default party size to 1
      //  int partySize = 1;
        //int phoneNo=1;
      //  try {
            //mNewPartyCountEditText inputType="number", so this should always work
          //  partySize = Integer.parseInt(mNewPartySizeEditText.getText().toString());
            //phoneNo=Integer.parseInt(mNewPhoneNoEditText.getText().toString());
      //  } catch (NumberFormatException ex) {
       //     Log.e(LOG_TAG, "Failed to parse party size text to number: " + ex.getMessage());
      //  }

        // Add guest info to mDb
        addNewGuest(mNewGuestNameEditText.getText().toString(), mNewPartySizeEditText.getText().toString());

        // Update the cursor in the adapter to trigger UI to display the new list
        mAdapter.swapCursor(getAllGuests());

        //clear UI text fields
        mNewPartySizeEditText.clearFocus();
        mNewGuestNameEditText.getText().clear();
        mNewPartySizeEditText.getText().clear();
        //mNewPhoneNoEditText.getText().clear();
    }



    /**
     * Query the mDb and get all guests from the waitlist table
     *
     * @return Cursor containing the list of guests
     */
    private Cursor getAllGuests() {
        return mDb.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
    }

    /**
     * Adds a new guest to the mDb including the party count and the current timestamp
     *
     * @param name  Guest's name
     * @param partySize Number in party
     * @return id of new record added
     */
    private long addNewGuest(String name, String partySize) {
        ContentValues cv = new ContentValues();
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
        cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);
        //cv.put(WaitlistContract.WaitlistEntry.COLUMN_PHONE_N0,phoneNo);
        return mDb.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);
    }


    // TODO (1) Create a new function called removeGuest that takes long id as input and returns a boolean
    private boolean removeGuest(long id){
       return mDb.delete(WaitlistContract.WaitlistEntry.TABLE_NAME,WaitlistContract.WaitlistEntry._ID+"="+id,null)>0;

    }

    @Override
    public void onClick(String phoneNo,String name) {
        //ShareCompat.IntentBuilder.from(this)
          //         .setChooserTitle("come")
            //        .setType("text/plain")
              //      .setText("Table is ready")
                //    .startChooser();
       // SmsManager smsManager = SmsManager.getDefault();
      //  smsManager.sendTextMessage("+918826823346", null, "sms message", null, null);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNo));
        intent.putExtra("sms_body", name+ " your table is ready!");
        startActivity(intent);
    }
    // TODO (2) Inside, call mDb.delete to pass in the TABLE_NAME and the condition that WaitlistEntry._ID equals id


}
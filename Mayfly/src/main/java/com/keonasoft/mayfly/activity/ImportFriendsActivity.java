package com.keonasoft.mayfly.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.keonasoft.mayfly.MyException;
import com.keonasoft.mayfly.R;
import com.keonasoft.mayfly.helper.HttpHelper;
import com.keonasoft.mayfly.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class ImportFriendsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_friends);
        Button importContactBtn = (Button) findViewById(R.id.importContactBtn);
        importContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String URI = getString(R.string.conn) + getString(R.string.friends_bulk_create);
                final JSONArray EMAILS = getNameEmailDetails();
                new AsyncTask<Void, Void, Boolean>(){
                    int succeeded;
                    int failed;
                    @Override
                    protected Boolean doInBackground(Void... params){
                        JSONObject result = new JSONObject();
                        try {
                            result.put("emails", EMAILS);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return false;
                        }
                        try {
                            result = HttpHelper.httpPost(URI, result);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                        try {
                            succeeded = result.getInt("succeeded");
                            failed = result.getInt("failed");
                            return true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                    @Override
                    protected void onPostExecute(final Boolean success){
                        String msg;
                        if(success)
                            msg = succeeded + " of " + (succeeded + failed) + " friend(s) successfully added";
                        else
                            msg = getString(R.string.error_network);
                        Toast.makeText(ImportFriendsActivity.this, msg, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }.execute(null, null, null);
            }
        });
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.import_friends, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public JSONArray getNameEmailDetails() {
        JSONArray emlRecs = new JSONArray();
        HashSet<String> emlRecsHS = new HashSet<String>();
        Context context = ImportFriendsActivity.this;
        ContentResolver cr = context.getContentResolver();
        String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
        if (cur.moveToFirst()) {
            do {
                // names comes in hand sometimes
                //String name = cur.getString(1);
                String emlAddr = cur.getString(3);

                // keep unique only
                if (emlRecsHS.add(emlAddr.toLowerCase())) {
                    emlRecs.put(emlAddr);
                }
            } while (cur.moveToNext());
        }

        cur.close();
        return emlRecs;
    }
}

package williamamills.colorify;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


import java.util.Map;

public class LoginActivity extends Activity {
    EditText userNameEditText;
    EditText passwordEditText;
    Button loginButton;
    Button newUserButton;
    private static final String TAG = LoginActivity.class.getSimpleName();

    /* TextView that is used to display information about the logged in user */
    private TextView mLoggedInStatusTextView;

    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;

    /* A reference to the Firebase */
    private Firebase mFirebaseRef;

    /* Data from the authenticated user */
    private AuthData mAuthData;

    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyApplication.getInstance(this);
        userNameEditText = (EditText) findViewById(R.id.login_user_name);
        passwordEditText = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(userName.equals("")||password.equals("")){
                    return;
                }
                loginWithPassword(userName, password);
                /*
                ParseUser.logInInBackground(userName, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(user!=null){
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                        }else{
                            Toast.makeText(getApplicationContext(), "Not Correct Credentials", Toast.LENGTH_LONG).show();
                        }

                    }
                });*/

            }
        });
        newUserButton = (Button) findViewById(R.id.create_user_button);
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
        /* Create the Firebase ref that is used for all authentication with Firebase */
        mFirebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(LoginActivity.this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);
        //mAuthProgressDialog.show();

        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthProgressDialog.hide();
                setAuthenticatedUser(authData);
            }
        };
        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
         * user and hide hide any login buttons */
        mFirebaseRef.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    private void logout() {
        if (this.mAuthData != null) {
            /* logout of Firebase */
            mFirebaseRef.unauth();
            /* Update authenticated user and show login buttons */
            setAuthenticatedUser(null);
        }
    }

    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     */
    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            /* Hide all the login buttons */
            //mLoggedInStatusTextView.setVisibility(View.VISIBLE);
            /* show a provider specific status text */
            String name = null;

            name = authData.getUid();

            if (name != null) {
                // mLoggedInStatusTextView.setText("Logged in as " + name + " (" + authData.getProvider() + ")");
            }
        } else {
            /* No authenticated user show all the login buttons */
            // mLoggedInStatusTextView.setVisibility(View.GONE);
        }
        this.mAuthData = authData;
        /* invalidate options menu to hide/show the logout button */
        //supportInvalidateOptionsMenu(); //// FIXME: 2/16/2016
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Utility class for authentication results
     */
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.hide();
            Log.i(TAG, provider + " auth successful");
            setAuthenticatedUser(authData);
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.hide();
            showErrorDialog(firebaseError.toString());
        }
    }
    public void loginWithPassword(String username, String password) {
        mAuthProgressDialog.show();
        mFirebaseRef.authWithPassword(username, password, new AuthResultHandler("password"));
    }

    public void createUser(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittextUser = new EditText(getApplicationContext());
        edittextUser.setHint("Email");
        edittextUser.setHintTextColor(Color.parseColor("#000000"));
        edittextUser.setTextColor(Color.parseColor("#000000"));
        final EditText edittextPassword = new EditText(getApplicationContext());
        edittextPassword.setHint("New Password");
        edittextPassword.setTextColor(Color.parseColor("#000000"));
        edittextPassword.setHintTextColor(Color.parseColor("#000000"));
        alert.setMessage("Create Username and Password");
        alert.setTitle("User Creation");
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        edittextUser.setId(R.id.my_edit_text_1);
        edittextUser.setId(R.id.my_edit_text_2);

        layout.addView(edittextUser);
        layout.addView(edittextPassword);

        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                //Editable YouEditTextValue = edittext.getText();
                //OR
                String user = edittextUser.getText().toString().trim();
                String pass = edittextPassword.getText().toString();
                mFirebaseRef.createUser(user, pass, new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Toast.makeText(getApplicationContext(), "Error creating user", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }

}

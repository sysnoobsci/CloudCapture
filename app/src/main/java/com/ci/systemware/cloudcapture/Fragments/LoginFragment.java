package com.ci.systemware.cloudcapture.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ci.systemware.cloudcapture.MainActivity;
import com.ci.systemware.cloudcapture.R;
import com.ci.systemware.cloudcapture.aSyncTasks.LoginTask;
import com.ci.systemware.cloudcapture.supportingClasses.APIQueries;
import com.ci.systemware.cloudcapture.supportingClasses.QueryArguments;
import com.squareup.picasso.Picasso;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
public class LoginFragment extends Fragment {
    static View rootView;
    ImageView cloudBackground;
    Button loginButton;
    Button hSettingsButton;
    EditText usernameInput;
    EditText passwordInput;
    EditText hostNameInput;
    EditText domainNameInput;
    EditText portNumberInput;
    View settingsDialogView;
    AlertDialog.Builder alertDialogBuilder;
    Context context;
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        cloudBackground = (ImageView) rootView.findViewById(R.id.imageView2);
        context = getActivity();
        setCloudBackground();
        getPreferences();
        instantiateViews();
        loginButtonListener();
        hSettingsButtonListener();
        return rootView;
    }

    private void instantiateViews() {
        usernameInput = (EditText) rootView.findViewById(R.id.usernameInput);
        passwordInput = (EditText) rootView.findViewById(R.id.passwordInput);
        loginButton = (Button) rootView.findViewById(R.id.loginButton);
        hSettingsButton = (Button) rootView.findViewById(R.id.hSettingsButton);
    }

    private void getPreferences(){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d("LoginFragment","hostname value: " + preferences.getString("hostname",null));
        Log.d("LoginFragment","domain value: " + preferences.getString("domain",null));
        Log.d("LoginFragment","portnumber value: " + preferences.getString("portnumber",null));
    }

    private void setCloudBackground() {
        Picasso.with(getActivity())
                .load(R.drawable.clouds_parlx_bg1)
                .fit()
                .centerInside()
                .into(cloudBackground);
    }

    private void loginButtonListener() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LoginFragment", "loginButton clicked");
                try {
                    login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void hSettingsButtonListener() {
        hSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LoginFragment", "hSettingsButton clicked");
                hSettingsDialog();//setup the
            }
        });
    }

    private void login() throws Exception {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", String.valueOf(usernameInput.getText()));
        editor.putString("password",String.valueOf(passwordInput.getText()));
        editor.apply();//commit the changes and store them in a background thread
        new LoginTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void hSettingsDialog(){
        LayoutInflater li = LayoutInflater.from(context);
        settingsDialogView = li.inflate(R.layout.settingsdialog,null);

        // get settingsdialog.xml view
        hostNameInput = (EditText) settingsDialogView
                .findViewById(R.id.hostNameInput);
        domainNameInput = (EditText) settingsDialogView
                .findViewById(R.id.domainNameInput);
        portNumberInput = (EditText) settingsDialogView
                .findViewById(R.id.portNumberInput);
        hostNameInput.setText(preferences.getString("hostname",null));
        domainNameInput.setText(preferences.getString("domain",null));
        portNumberInput.setText(preferences.getString("portnumber",null));

        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(settingsDialogView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //save user inputs to preferences
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("hostname", String.valueOf(hostNameInput.getText()));
                                editor.putString("domain",String.valueOf(domainNameInput.getText()));
                                editor.putString("portnumber",String.valueOf(portNumberInput.getText()));
                                editor.apply();//commit the changes and store them in a background thread
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog loginDialog = alertDialogBuilder.create();
        loginDialog.show();
    }


}


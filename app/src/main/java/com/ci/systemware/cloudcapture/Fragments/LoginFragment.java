package com.ci.systemware.cloudcapture.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ci.systemware.cloudcapture.R;
import com.ci.systemware.cloudcapture.aSyncTasks.LoginTask;
import com.ci.systemware.cloudcapture.aSyncTasks.ReadAppConfigTask;
import com.ci.systemware.cloudcapture.interfaces.LoginTaskInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
public class LoginFragment extends Fragment implements LoginTaskInterface {
    static View rootView;
    ImageView cloudBackground;
    Button loginButton;
    Button hSettingsButton;
    Button camTemplateSettingsButton;
    EditText passwordInput;
    EditText hostNameInput;
    EditText domainNameInput;
    EditText portNumberInput;
    EditText usernameInput;
    EditText templateNameInput;
    TextView templateNameView;
    ArrayList<String> templates = new ArrayList<String>();
    View settingsDialogView;
    AlertDialog.Builder alertDialogBuilder;
    Context context;
    LoginTask lTask;
    SharedPreferences preferences;
    static Boolean isFirst_open = true;//flag if fragment is opened for the first time

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        cloudBackground = (ImageView) rootView.findViewById(R.id.imageView2);
        setCloudBackground();
        context = getActivity();
        lTask = new LoginTask(context,this);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(isFirst_open){//if this is the first time the fragment is viewed in this app instance, clear pw and username
            clearUserAndPW();
        }
        instantiateViews();
        loginButtonListener();
        hSettingsButtonListener();
        camTemplateSettingsButtonListener();
        isFirst_open = false;
        return rootView;
    }

    private void clearUserAndPW(){//clears the username and password from preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", null);
        editor.putString("password",null);
        editor.apply();//commit the changes and store them in a background thread
    }

    private void instantiateViews() {
        usernameInput = (EditText) rootView.findViewById(R.id.usernameInput);
        passwordInput = (EditText) rootView.findViewById(R.id.passwordInput);
        loginButton = (Button) rootView.findViewById(R.id.loginButton);
        hSettingsButton = (Button) rootView.findViewById(R.id.hSettingsButton);
        camTemplateSettingsButton = (Button) rootView.findViewById(R.id.camTemplateSettingsButton);
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
                hSettingsDialog();
            }
        });
    }

    private void camTemplateSettingsButtonListener() {
        camTemplateSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LoginFragment", "camTemplateSettingsButton clicked");
                templateSettingsDialog();
            }
        });
    }

    private void login() throws Exception {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", String.valueOf(usernameInput.getText()));
        editor.putString("password",String.valueOf(passwordInput.getText()));
        editor.apply();//commit the changes and store them in a background thread
        new LoginTask(context,lTask.listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void hSettingsDialog(){
        LayoutInflater li = LayoutInflater.from(context);
        settingsDialogView = li.inflate(R.layout.hostsettingsdialog,null);

        // get hostsettingsdialog.xml.xml view
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

    private String getTemplateNamesString(ArrayList<String> templates){
        StringBuilder templatesSB = new StringBuilder();
        for(String template : templates){
            if (templates.indexOf(template) == templates.size()-1){//if last template in the list, don't add comma after it
                templatesSB.append(template);
            }
            else{
                templatesSB.append(template)
                           .append(",");
            }
        }
        Log.d("getTemplateNamesString()","Value of templatesSB: " + templatesSB.toString());
        return templatesSB.toString();
    }

    private void templateSettingsDialog(){
        LayoutInflater li = LayoutInflater.from(context);
        settingsDialogView = li.inflate(R.layout.camtemplatesettingsdialog,null);

        // get hostsettingsdialogalog.xml view
        templateNameInput = (EditText) settingsDialogView
                .findViewById(R.id.templateNameInput);
        templateNameView = (TextView) settingsDialogView
                .findViewById(R.id.templateNames);
        templateNameView.setText(preferences.getString("templatenames",null));
        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(settingsDialogView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setNeutralButton("Add Template",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //overriding after dialog is shown
                            }
                        })
                .setPositiveButton("Finish",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //save template name inputs to preferences
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("templatenames", String.valueOf(templateNameView.getText()));
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
        loginDialog.getButton(AlertDialog.BUTTON_NEUTRAL)//overriding neutral button so the dialog doesn't get closed
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("templateSettingsDialog()","Inert button pressed");
                        templates.add(String.valueOf(templateNameInput.getText()));
                        templateNameView.setText(getTemplateNamesString(templates));
                        templateNameInput.setText("");//empty the field after each button press
                    }
                });
    }


    @Override
    public void loginTaskProcessFinish(String output) {//fired after LoginTask completes - if successful login, unlock nav bar and drawer toggle
            Log.d("LoginFragment.loginTaskProcessFinish()","LoginFragment.loginTaskProcessFinish() called.");
            if(Boolean.valueOf(output)){
                NavigationDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//enable drawer slide gesture
                NavigationDrawerFragment.mDrawerToggle.setDrawerIndicatorEnabled(true);//enable drawer toggle
                Log.d("MainActivity.loginTaskProcessFinish()","Drawer slide gesture and toggle enabled.");
            }
            Log.d("MainActivity.loginTaskProcessFinish()","Starting ReadAppConfigTask");
            new ReadAppConfigTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"gensrch_create.9020.a.xml");
    }
}


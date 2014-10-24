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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ci.systemware.cloudcapture.R;
import com.ci.systemware.cloudcapture.aSyncTasks.ListAppConfigTask;
import com.ci.systemware.cloudcapture.aSyncTasks.LoginTask;
import com.ci.systemware.cloudcapture.aSyncTasks.RASTemplateFileTask;
import com.ci.systemware.cloudcapture.aSyncTasks.ToastMsgTask;
import com.ci.systemware.cloudcapture.interfaces.ListAppConfigTaskInterface;
import com.ci.systemware.cloudcapture.interfaces.LoginTaskInterface;
import com.ci.systemware.cloudcapture.interfaces.RASTemplateFileTaskInterface;
import com.ci.systemware.cloudcapture.supportingClasses.FileUtility;
import com.ci.systemware.cloudcapture.supportingClasses.TemplateXMLFileTracker;
import com.ci.systemware.cloudcapture.supportingClasses.XMLParser;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
public class LoginFragment extends Fragment implements LoginTaskInterface,ListAppConfigTaskInterface,RASTemplateFileTaskInterface {
    static View rootView;
    ImageView cloudBackground;
    Context context;
    Button loginButton;
    Button hSettingsButton;
    Button camTemplateSettingsButton;
    EditText passwordInput;
    EditText hostNameInput;
    EditText domainNameInput;
    EditText portNumberInput;
    EditText usernameInput;
    EditText camidInput;
    ArrayList<String> templateNames = new ArrayList<String>();
    View settingsDialogView;
    AlertDialog.Builder alertDialogBuilder;

    //task objects
    LoginTask logonTask;
    ListAppConfigTask lacTask;
    RASTemplateFileTask rasTask;

    SharedPreferences preferences;
    static Boolean isFirst_open = true;//flag if fragment is opened for the first time

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        cloudBackground = (ImageView) rootView.findViewById(R.id.imageView2);
        setCloudBackground();
        context = getActivity();
        logonTask = new LoginTask(context,this);
        lacTask = new ListAppConfigTask(context,this);
        rasTask = new RASTemplateFileTask(context,this);
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
                camidSettingsDialog();
            }
        });
    }

    private Boolean areSettingsGood(){//check if all needed prefs are set i.e. not empty or null
        String usernameStr = preferences.getString("username",null).trim();
        String passwordStr = preferences.getString("password",null).trim();
        String camidStr = preferences.getString("camid",null).trim();
        return (!TextUtils.isEmpty(usernameStr) && !TextUtils.isEmpty(passwordStr) && !TextUtils.isEmpty(camidStr));
    }

    private void login() throws Exception {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", String.valueOf(usernameInput.getText()));
        editor.putString("password",String.valueOf(passwordInput.getText()));
        editor.apply();//commit the changes and store them in a background thread
        if(areSettingsGood()) {
            new LoginTask(context, logonTask.listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            TemplateXMLFileTracker.clearTemplateXMLFiles(context);//clear out the CAM template XML files prior to writing the new ones
        }
        else{
            ToastMsgTask.areSettingsGoodMessage(context);
        }

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

    private void camidSettingsDialog(){
        LayoutInflater li = LayoutInflater.from(context);
        settingsDialogView = li.inflate(R.layout.camidsettingsdialog,null);
        // get hostsettingsdialogalog.xml view
        camidInput = (EditText) settingsDialogView
                .findViewById(R.id.camidInput);
        camidInput.setText(preferences.getString("camid", null));
        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(settingsDialogView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //save template name inputs to preferences
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("camid", String.valueOf(camidInput.getText()));
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

    @Override
    public void loginTaskProcessFinish(String output) {//fired after LoginTask completes - if successful login, unlock nav bar and drawer toggle
            Log.d("LoginFragment.loginTaskProcessFinish()","LoginFragment.loginTaskProcessFinish() called.");
            if(Boolean.valueOf(output)){
                NavigationDrawerFragment.mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);//enable drawer slide gesture
                NavigationDrawerFragment.mDrawerToggle.setDrawerIndicatorEnabled(true);//enable drawer toggle
                Log.d("MainActivity.loginTaskProcessFinish()","Drawer slide gesture and toggle enabled.");
                //after successful login, read the entire CI app config
                FileUtility.directoryCheck(context);//make sure directories exist for app to function properly
                Log.d("MainActivity.loginTaskProcessFinish()","Starting ListAppConfigTask");
                new ListAppConfigTask(context,lacTask.listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
    }

    @Override
    public void listAppConfigTaskProcessFinish(String output) {//fired after listAppConfigTask completes
        Log.d("listAppConfigTaskProcessFinish()","Value of output: " + output);
        String templateIDs = "";
        try {
            templateIDs = XMLParser.getCAMTemplateIDs(preferences.getString("camid",""),output);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("MainActivity.listAppConfigTaskProcessFinish()","templateIDs value: " + templateIDs);
        String [] templateNamesArr = templateIDs.split(",");
        for(String templateName : templateNamesArr){
            new RASTemplateFileTask(context,rasTask.listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,templateName);
        }
    }

    @Override
    public void RASConfigFileTaskProcessFinish(String output) {

    }
}


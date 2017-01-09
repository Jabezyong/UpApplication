package com.application.upapplication.Views;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.upapplication.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    public static final String bundleTAG = "FACEBOOKDATA";
    private LoginButton mLoginBtn;
    private CallbackManager callbackManager;
    private Bundle bFacebookData;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_login
                , container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoginBtn = (LoginButton) view.findViewById(R.id.btn_facebook_login);
        mLoginBtn.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        mLoginBtn.setFragment(this);

        mLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                Set<String> permissions = loginResult.getAccessToken().getPermissions();
//                Log.i("Response : ",permissions.toArray().toString()+"");
                setFacebookData(loginResult);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
    private void getData(LoginResult loginResult){
//        GraphRequest.newMeRequest(
//                loginResult.getAccessToken(),
//                new Request
//        )
    }
    private void setFacebookData(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                       new getFacebookData().execute(object);


                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday"); // Parámetros que pedimos a facebook
        request.setParameters(parameters);
        request.executeAsync();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private class getFacebookData extends AsyncTask<JSONObject,Void,Bundle>{

        @Override
        protected Bundle doInBackground(JSONObject... params) {
            Bundle bundle = new Bundle();
            JSONObject object = params[0];
            try {
                if (object.has("email")) {
                    bundle.putString("email", object.getString("email"));
                }
                if (object.has("first_name")) {
                    bundle.putString("first_name", object.getString("first_name"));
                }
                if (object.has("last_name")) {
                    bundle.putString("last_name", object.getString("last_name"));
                }
                if (object.has("gender")) {
                    bundle.putString("gender", object.getString("gender"));
                }
                if (object.has("birthday")) {
                    bundle.putString("birthday", object.getString("birthday"));
                }
                if (object.has("id")) {
                    bundle.putString("id", object.getString("id"));
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
           bFacebookData = bundle;
            Intent intent = new Intent(getApplicationContext(), AccountActivity.class);;
            intent.putExtra(bundleTAG,bFacebookData);
            getActivity().finish();
            startActivity(intent);

        }
    }
}

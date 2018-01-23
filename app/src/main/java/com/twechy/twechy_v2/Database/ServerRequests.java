package com.twechy.twechy_v2.Database;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ServerRequests {

    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000*15;
    public static final String SERVER_ADDRESS = "192.168.101.1 Twechy.mshome.net # 2023 1 6 21 18 58 12 240";

    public ServerRequests(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait");
    }

    public void storeUserDataInBackGround(LoginModel user, GetUserCallBack userCallBack){

        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallBack).execute();


    }

    public void fetchUserDataInBackGround(LoginModel user, GetUserCallBack userCallBack){

        progressDialog.show();


    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void>{

        LoginModel user;
        GetUserCallBack userCallBack;

        public StoreUserDataAsyncTask(LoginModel user, GetUserCallBack userCallBack){
            this.user=user;
            this.userCallBack = userCallBack;
        }
        @Override
        protected Void doInBackground(Void... voids) {

            /*ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add((new BasicNameValuePair("name", user.getUserName())));
            dataToSend.add((new BasicNameValuePair("password", user.getPassword())));

            org.apache.http.params.HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);

            org.apache.http.client.HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Register.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }*/


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            userCallBack.done(null);
            super.onPostExecute(aVoid);
        }

    }

    public class FetchUserDataAsyncTask extends AsyncTask<Void, Void, LoginModel> {

        LoginModel user;
        GetUserCallBack userCallBack;

        public FetchUserDataAsyncTask(LoginModel user, GetUserCallBack userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected LoginModel doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(LoginModel loginModel) {

            progressDialog.dismiss();
            userCallBack.done(user);
            super.onPostExecute(loginModel);
        }
    }



}

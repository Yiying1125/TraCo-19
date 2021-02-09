package mmu.edu.my.traco_19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;

import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.NumberFormat;

import static mmu.edu.my.traco_19.Activities.Register.SHARED_PREFS;

public class LatestUpdateActivity extends AppCompatActivity {


    Context context = this;
    private TextView tv_totalnumber1, tv_activenumber1, tv_activenumberNew, tv_deathnumber1, tv_deathnumberNew, tv_recoverednumber1, tv_recoverednumberNew, tv_todaynumber1;
    private SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progressDialog;

    public void back(View view) {
        onBackPressed();
    }

    public void setTHeme(int pos) {
        int style = R.style.Theme_TraCo19;

        if (pos == 1) {
            style = R.style.Theme_TraCo191;
        } else if (pos == 2) {
            style = R.style.Theme_TraCo192;
        } else if (pos == 3) {
            style = R.style.Theme_TraCo193;
        } else if (pos == 4) {
            style = R.style.Theme_TraCo194;
        } else if (pos == 5) {
            style = R.style.Theme_TraCo195;
        } else if (pos == 6) {
            style = R.style.Theme_TraCo196;
        }
        setTheme(style);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTHeme(getInt(loadData("Theme")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.latest_update);


        Init();
        //Fetch data from API
        FetchData();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            FetchData();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void FetchData() {
        ShowDialog();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String apiUrl = "https://api.apify.com/v2/datasets/7Fdb90FMDLZir2ROo/items?format=json&clean=1";

        String lastData = "https://api.apify.com/v2/key-value-stores/6t65lJVfs3d8s6aKc/records/LATEST?disableRedirect=true";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,
                response -> {
                    try {
                        String str_total = response.getString("testedPositive");
                        String str_active = response.getString("activeCases");
                        String str_death = response.getString("deceased");
                        String str_recovered = response.getString("recovered");

                        Handler delayToShowProgress = new Handler();
                        delayToShowProgress.postDelayed(() -> {
                            //Setting text in the textview
                            tv_totalnumber1.setText(NumberFormat.getInstance().format(Integer.parseInt(str_total)));
                            tv_activenumber1.setText(NumberFormat.getInstance().format(Integer.parseInt(str_active)));
                            tv_recoverednumber1.setText(NumberFormat.getInstance().format(Integer.parseInt(str_recovered)));
                            tv_deathnumber1.setText(NumberFormat.getInstance().format(Integer.parseInt(str_death)));
                            /*int_today = Integer.parseInt(str_total)
                                    - (Integer.parseInt(str_total) + Integer.parseInt(str_death));*/
//                                    tv_todaynumber1.setText(NumberFormat.getInstance().format(int_today));
                            progressDialog.dismiss();
                        }, 1000);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                },
                error -> {
                    error.printStackTrace();
                    String message = "";
                    if (error instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (error instanceof AuthFailureError) {
                        message = "error connect to Internet...Please check your connection!";
                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

        );
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);
    }


//    private void FetchData() {
//        ShowDialog();
//        String URL = "https://api.apify.com/v2/datasets/7Fdb90FMDLZir2ROo/items?format=json&clean=1";
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, response -> {
//
////            try {
////                // Loop through the array elements
////                for (int i = 0; i < response.length(); i++) {
////                    // Get current json object
////
////                }
////
////                JSONObject student = response.getJSONObject(i);
//
//                Handler delayToShowProgress = new Handler();
//                delayToShowProgress.postDelayed(() -> {
////                    //Setting text in the textview
////                    tv_totalnumber1.setText(NumberFormat.getInstance().format(Integer.parseInt(str_total)));
////                    tv_activenumber1.setText(NumberFormat.getInstance().format(Integer.parseInt(str_active)));
////                    tv_recoverednumber1.setText(NumberFormat.getInstance().format(Integer.parseInt(str_recovered)));
////                    tv_deathnumber1.setText(NumberFormat.getInstance().format(Integer.parseInt(str_death)));
////                                /*int_today = Integer.parseInt(str_total)
////                                        - (Integer.parseInt(str_total) + Integer.parseInt(str_death));*/
////                    tv_todaynumber1.setText(NumberFormat.getInstance().format(int_today));
//
//                    try {
//                        tv_totalnumber1.setText(response.getJSONObject(response.length()-1).getString("testedPositive"));
//                        tv_activenumber1.setText(response.getJSONObject(response.length()-1).getString("testedPositive"));
//                        tv_activenumberNew.setText(response.getJSONObject(response.length()-1).getString("testedPositive"));
//                        tv_deathnumber1.setText(response.getJSONObject(response.length()-1).getString("testedPositive"));
//                        tv_deathnumberNew.setText(response.getJSONObject(response.length()-1).getString("testedPositive"));
//                        tv_recoverednumber1.setText(response.getJSONObject(response.length()-1).getString("testedPositive"));
//                        tv_recoverednumberNew.setText(response.getJSONObject(response.length()-1).getString("testedPositive"));
//                        tv_todaynumber1.setText(response.getJSONObject(response.length()-1).getString("testedPositive"));
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    progressDialog.dismiss();
//                }, 1000);
//
//
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
//        }, error -> {
//            String message = "";
//            if (error instanceof NetworkError) {
//                message = "Cannot connect to Internet...Please check your connection!";
//            } else if (error instanceof ServerError) {
//                message = "The server could not be found. Please try again after some time!!";
//            } else if (error instanceof AuthFailureError) {
//                message = "error connect to Internet...Please check your connection!";
//            } else if (error instanceof ParseError) {
//                message = "Parsing error! Please try again after some time!!";
//            } else if (error instanceof TimeoutError) {
//                message = "Connection TimeOut! Please check your internet connection.";
//            }
//            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//        });
//        Volley.newRequestQueue(context).add(jsonArrayRequest);
//    }

    private void Init() {
        tv_totalnumber1 = findViewById(R.id.totalnumber2);
        tv_activenumber1 = findViewById(R.id.activenumber2);
        tv_activenumberNew = findViewById(R.id.activenumberNew);
        tv_deathnumber1 = findViewById(R.id.deathnumber2);
        tv_deathnumberNew = findViewById(R.id.deathnumberNew);
        tv_recoverednumber1 = findViewById(R.id.recoverednumber2);
        tv_recoverednumberNew = findViewById(R.id.recoverednumberNew);
        tv_todaynumber1 = findViewById(R.id.todaynumber2);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
    }

    public void ShowDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public int getInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception ignored) {
            return 0;
        }

    }

    public String loadData(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences == null) {
            return "";
        }
        return sharedPreferences.getString(name, "");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
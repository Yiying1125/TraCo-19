package mmu.edu.my.traco_19.Fragments;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import mmu.edu.my.traco_19.R;

import static android.content.Context.DOWNLOAD_SERVICE;

public class LatestUpdates extends Fragment{


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_latest_updates, container, false);
        }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getView()!=null){
            setWebView(getView().findViewById(R.id.webView));
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setWebView(WebView webView) {
        String url = "http://covid-19.moh.gov.my/";
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        setTitle(url);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView.loadUrl(url);

        webView.setDownloadListener((url1, userAgent, contentDisposition, mimeType, contentLength) -> {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url1));

            request.setMimeType(mimeType);
            //------------------------COOKIE!!------------------------
            String cookies = CookieManager.getInstance().getCookie(url1);
            request.addRequestHeader("cookie", cookies);
            //------------------------COOKIE!!------------------------
            request.addRequestHeader("User-Agent", userAgent);
            request.setDescription("Downloading file...");
            request.setTitle(URLUtil.guessFileName(url1, contentDisposition, mimeType));
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url1, contentDisposition, mimeType));
            DownloadManager dm = (DownloadManager) Objects.requireNonNull(getActivity()).getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            Toast.makeText(getContext(), "Downloading File", Toast.LENGTH_LONG).show();
        });
    }
}

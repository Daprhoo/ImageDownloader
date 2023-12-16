package com.example.imagedownloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

class DownloadTask extends AsyncTask<String, Integer, Bitmap> {

    private ProgressDialog PD;
    private Context context;

    // Constructor to receive the context
    public DownloadTask(Context context) {
        this.context = context;
        PD = new ProgressDialog(context);
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String fileName = "temp.jpg";
        String imagePath = (Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS)).toString()
                + "/" + fileName;
        downloadFile(urls[0], imagePath);
        return BitmapFactory.decodeFile(imagePath);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        ((MainActivity) PD.getContext()).preview(result);
        PD.dismiss();
    }

    void downloadFile(String strURL, String imagePath) {
        try {
            URL url = new URL(strURL);
            URLConnection connection = url.openConnection();
            connection.connect();
            int fileSize = connection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            OutputStream output = new FileOutputStream(imagePath);
            byte data[] = new byte[1024];
            long total = 0;
            int count;

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
                total += count;
                publishProgress((int) ((total * 100) / fileSize));
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PD = new ProgressDialog(context);
        PD.setMax(100);
        PD.setIndeterminate(false);
        PD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        PD.setTitle("Downloading");
        PD.setMessage("Please wait..");
        PD.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        PD.setProgress(progress[0]);
    }
}

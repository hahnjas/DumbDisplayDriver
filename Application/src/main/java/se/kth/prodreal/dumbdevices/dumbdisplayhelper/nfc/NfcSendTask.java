package se.kth.prodreal.dumbdevices.dumbdisplayhelper.nfc;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Build;
import android.widget.Toast;

import se.kth.prodreal.dumbdevices.dumbdisplayhelper.util.AsyncTask;
import se.kth.prodreal.dumbdevices.dumbdisplayhelper.R;
import java.io.ByteArrayOutputStream;

/**
 * Created by jasperh on 08/12/14.
 */
public class NfcSendTask extends AsyncTask<Bitmap, Void, Integer> {

    private final Context context;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void send(Bitmap image) {

        byte[] byteArray = this.createByteArray(image);

        NdefMessage img = new NdefMessage(NdefRecord.createMime("Test Image", byteArray));

    }

    public NfcSendTask(Context context){
        this.context = context;
    }

    private byte[] createByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    @Override
    protected Integer doInBackground(Bitmap... params) {
        if(params != null && params[0] != null) {
            send(params[0]);
            return R.string.send_image_complete_toast;
        }
        return R.string.send_image_fail_toast;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result != null) {
            Toast.makeText(
                    context, result , Toast.LENGTH_SHORT).show();
        }
    }
}

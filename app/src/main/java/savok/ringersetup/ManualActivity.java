package savok.ringersetup;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import java.io.InputStream;
import java.io.OutputStream;

public class ManualActivity extends AppCompatActivity {

    private final InputStream is = MainActivity.is;
    private final OutputStream os = MainActivity.os;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        ImageButton ringButton = findViewById(R.id.btn_ring);

        ringButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        new SendTurnSignalTask(true).execute();
                        view.performClick();
                        break;
                    case MotionEvent.ACTION_UP:
                        new SendTurnSignalTask(false).execute();
                        break;
                }
                return false;
            }
        });
    }

    private class SendTurnSignalTask extends AsyncTask<Void, Void, Boolean> {
        private final boolean isOn;

        SendTurnSignalTask(boolean isOn) {
            this.isOn = isOn;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                os.write(isOn ? 0x03 : 0x04);
                os.flush();

                return is.read() == 0x01;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean == null || !aBoolean) finish();
        }
    }
}

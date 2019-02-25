package pripremazagetup.riteh.hr.pripremazagetup;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class BeforeMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_main);

        new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {
                /*
                // one second till the countdown
                if (millisUntilFinished/1000 == 1) {
                }*/
            }

            public void onFinish() {
                Intent myIntent = new Intent(BeforeMain.this, MainActivity.class);
                BeforeMain.this.startActivity(myIntent);
            }
        }.start();


    }
}

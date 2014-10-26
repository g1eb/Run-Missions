package nl.gleb.runmissions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Gleb on 26/10/14.
 */
public class Splash extends Activity {

    private static long MAX_SPLASH_TIME = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Thread() {
            @Override
            public void run() {
                synchronized (Main.SPLASH_LOCK) {
                    try {
                        Main.SPLASH_LOCK.wait(MAX_SPLASH_TIME);
                    }
                    catch (InterruptedException ignored) {
                        ignored.printStackTrace();
                    } finally {
                        startActivity(new Intent("nl.gleb.runmissions.MAIN"));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
                finish();
            }
        }.start();
    }
}

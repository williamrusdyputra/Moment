package edu.bluejack19_1.moment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import edu.bluejack19_1.moment.util.DataUtil;

public class SplashScreen extends AwesomeSplash {

    private SharedPreferences sharedPref;

    @Override
    public void initSplash(ConfigSplash configSplash) {
        configSplash.setBackgroundColor(R.color.colorBlack);
        configSplash.setAnimCircularRevealDuration(2000);
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM);

        configSplash.setLogoSplash(R.mipmap.ic_launcher);
        configSplash.setAnimLogoSplashDuration(2000);
        configSplash.setAnimLogoSplashTechnique(Techniques.ZoomIn);

        configSplash.setOriginalHeight(350);
        configSplash.setOriginalWidth(350);
        configSplash.setAnimPathStrokeDrawingDuration(3000);
        configSplash.setPathSplashStrokeSize(3);
        configSplash.setPathSplashStrokeColor(R.color.colorAccent);
        configSplash.setAnimPathFillingDuration(3000);
        configSplash.setPathSplashFillColor(R.color.white);

        configSplash.setTitleSplash("Moment");
        configSplash.setTitleTextColor(R.color.gray_scale);
        configSplash.setTitleTextSize(30f);
        configSplash.setAnimTitleDuration(2000);
        configSplash.setAnimTitleTechnique(Techniques.Tada);
    }

    @Override
    public void animationsFinished() {
        sharedPref = this.getSharedPreferences(getString(R.string.user_pref_key), MODE_PRIVATE);
        if(sharedPref.contains(getString(R.string.user_pref))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    DataUtil.user.username = sharedPref.getString(getString(R.string.user_pref), "error").split("@")[0];
                    Intent homeIntent = new Intent(SplashScreen.this, HomeActivity.class);
                    homeIntent.putExtra(HomeActivity.EXTRA_DATA, DataUtil.user.username);
                    startActivity(homeIntent);
                    finish();
                }
            }, 1000);
        } else {
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

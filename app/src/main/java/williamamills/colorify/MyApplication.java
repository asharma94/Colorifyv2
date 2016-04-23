package williamamills.colorify;

import android.app.Application;
import android.content.Context;

import com.firebase.client.Firebase;

/**
 * Created by Alexander on 1/27/2016.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

    }
    private static MyApplication mInstance;
    private  Context mCtx;

    public MyApplication(Context ctx){
        Firebase.setAndroidContext(ctx);
    }
    public static MyApplication getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you don't accidentally leak an Activity's
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new MyApplication(ctx);
        }
        return mInstance;
    }
}

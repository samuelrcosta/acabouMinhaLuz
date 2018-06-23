package acabouminhaluz.devapps.es.inf.ufg.br.acabouminhaluz.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.SharedPreferencesCompat;

/**
 * Created by marceloquinta on 10/02/17.
 */

public class EasySharedPreferences {

    public static String KEY_EMAIL = "email";
    public static String KEY_TOKEN = "token";

    public static String getStringFromKey(Context context, String key){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key,"");
    }

    public static void setStringFromKey(Context context, String key, String value){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key,value);
        editor.commit();
    }
}

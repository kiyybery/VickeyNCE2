package cn.ttsk.library;


import cn.ttsk.MumianApplication;
import android.content.SharedPreferences;

/**
 * Created with IntelliJ IDEA.
 * User: liuyu
 * Date: 13-10-12
 * Time: 上午11:26
 * To change this template use File | Settings | File Templates.
 */
public class PreferencesUtil {

    public static <T> void put(String key, T value){
        SharedPreferences.Editor editor = MumianApplication.preferences.edit();
        if(value instanceof String){
            editor.putString(key, value.toString());
        }else if(value instanceof Boolean){
            editor.putBoolean(key, ((Boolean) value).booleanValue());
        }else if(value instanceof Integer){
            editor.putInt(key, ((Integer) value).intValue());
        }else if(value instanceof Float){
            editor.putFloat(key, ((Float) value).floatValue());
        }else if(value instanceof Long){
            editor.putLong(key, ((Long) value).longValue());
        }
        editor.commit();
    }

    public static <T> T get(String key, T value){
        Object o = null;
        if(value instanceof String){
            o =  MumianApplication.preferences.getString(key, value.toString());
        }else if(value instanceof Boolean){
            o = MumianApplication.preferences.getBoolean(key, ((Boolean) value).booleanValue());
        }else if(value instanceof Integer){
            o = MumianApplication.preferences.getInt(key, ((Integer) value).intValue());
        }else if(value instanceof Float){
            o = MumianApplication.preferences.getFloat(key, ((Float) value).floatValue());
        }else if(value instanceof Long){
            o = MumianApplication.preferences.getLong(key, ((Long) value).longValue());
        }
        T t = (T) o;
        return t;
    }
}

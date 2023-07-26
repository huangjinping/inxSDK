package cdax.naindex.datalib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {



    public static int getSignature(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        StringBuilder sb = new StringBuilder();

        try {
            pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures = pi.signatures;
            for (Signature signature : signatures) {
                sb.append(signature.toCharsString());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return sb.toString().hashCode();
    }








    public static String stringIllegalFilter(String str) {
        try {
            str = str.replace("/[\u2190-\u21FF]|[\u2600-\u26FF]|[\u2700-\u27BF]|[\u3000-\u303F]|[\u1F300-\u1F64F]|[\u1F680-\u1F6FF]/g", "");
            str = filterEmoji(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return
                str;
    }

    public static String filterEmoji(String nick_name) {
        //nick_name 所获取的用户昵称
        if (nick_name == null) {
            return nick_name;
        }
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher emojiMatcher = emoji.matcher(nick_name);
        if (emojiMatcher.find()) {
            //将所获取的表情转换为*
            nick_name = emojiMatcher.replaceAll("");
            return nick_name;
        }
        return nick_name;
    }

}

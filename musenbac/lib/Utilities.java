package musenbac.lib;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
public class Utilities{
    public static synchronized String arrayToString(String[] args){
        String str="";
        try{
        for(int i=0;i<args.length;i++)str+=URLEncoder.encode(args[i],"UTF-8")+"+";
        }catch(UnsupportedEncodingException e){e.printStackTrace();}
        return str;
    }
}

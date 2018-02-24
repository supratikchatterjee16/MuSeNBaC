package musenbac.lib;
import java.io.*;
public class Problems{
    public static String fetch(int n){
        String res="";
        try{
        File f=new File("./problems/"+Integer.toString(n)+".txt");
        if(f.exists()){
            FileReader fr=new FileReader(f);
            int i=0;
            while((i=fr.read())!=-1)res+=(char)i;
            System.out.println(res);
        }
        else{
          f=new File("./problems/");
          if(!f.exists())f.mkdirs();
          FileWriter fw=new FileWriter("./problems/0.txt");
          String s="There are no problems to be found\n\n";
          int i=0;
          while(i<s.length())fw.write((int)s.charAt(i++));
          fw.flush();
          fw.close();
          res="0";
        }
        }catch(IOException e){System.out.println("File R/W error.\nDescription:"+e);}
        return res;
    }
}

package musenbac.lib; 
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
public class chatStorage{
    private static String chatdata[][]=new String[1000][2];
    private static int top=0;
    public static String[][] fetch(){
        String ret[][]=new String[top][2];
        for(int i=0;i<top;i++){
            ret[i][0]=chatdata[i][0];
            ret[i][1]=chatdata[i][1];
            System.out.println(ret[i][0]+":"+ret[i][1]);
        }
        return ret;
    }
    public static String[][] fetch(int a, int b){
        String ret[][]=new String[b-a+1][2];
        for(int i=a;i<b;i++){
            ret[i-a][0]=chatdata[i][0];
            ret[i-a][1]=chatdata[i][1];
            System.out.println(ret[i-a][0]+":"+ret[i-a][1]);
        }
        return ret;
    }
    public static void store(String uname,String data){
        File f=new File("./accounts/"+uname);
        String name="";
        if(f.exists()){
            try{
                FileReader fr=new FileReader("./accounts/"+uname+"/meta.inf");
                Scanner sc=new Scanner(fr);
                name=sc.nextLine();
            }catch(IOException e){System.out.println("Error in reading meta.inf file");}
        }
        if(top==999){
            for(int i=0;i<999;i++){
                chatdata[i][0]=chatdata[i+1][0];
                chatdata[i][1]=chatdata[i+1][1];
            }
            chatdata[top][0]=name;
            chatdata[top][1]=data;
        }
        else{
            chatdata[top][0]=name;
            chatdata[top++][1]=data;
        }
    }
}

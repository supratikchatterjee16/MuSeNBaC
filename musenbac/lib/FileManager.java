package musenbac.lib;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;

import java.util.*;

public class FileManager{
    private static synchronized String[] concatenatedArray(String[] arr1,String[] arr2){
        String[] res=new String[arr1.length+arr2.length];
        String filterWord[]={"password","pswd"};
        int greater=(arr1.length>arr2.length)?arr1.length:arr2.length;
        boolean accepted=true;
        for(int i=0;i<greater;i++){
            if(i<arr1.length){
                accepted=true;
                for(int j=0;j<filterWord.length;j++)if(arr1[i].equals(filterWord[j])){accepted=false;break;}
                if(accepted)res[i]=arr1[i];
                else res[i]="";
            }
            if(i<arr2.length){
                accepted=true;
                for(int j=0;j<filterWord.length;j++)if(arr2[i].equals(filterWord[j])){accepted=false;break;}
                if(accepted)res[i+arr1.length]=arr2[i];
                else res[i+arr1.length]="";
            }
        }
        return res;
    }
    public synchronized static String[] ls(String dir){
        File f=new File(dir);
        String res[]=new String[2],typeDir[]={"folder"},typeFile[]={"file"};
        if(f.isDirectory()){res=concatenatedArray(typeDir,f.list());}
        else {
            int n=0;
            String content="";
            try{
                FileReader fr=new FileReader(f);
                while((n=fr.read())!=-1){content+=(char)n;}
            }catch(IOException e){e.printStackTrace();}
            res[0]="file";
            res[1]=content;
        }
        if(dir.indexOf("password")!=-1||dir.indexOf("pswd")!=-1||!f.exists()){res=new String[2];res[0]="note";res[1]="Not Found";}
        return res;
    }
    public static int save(String uaccount,String dir,String content){
        int success=0;
        String directory="accounts/"+uaccount+"/",filename="temp.text";
        try{
        File f=null;
        if(dir.indexOf("/")!=-1){
            directory+=dir.substring(0,dir.lastIndexOf("/"));
            filename=dir.substring(dir.lastIndexOf("/")+1);
            f=new File(directory);
            if(!f.exists()&&!f.isDirectory())f.mkdirs();
        }
        f=new File(directory+filename);
            if(f.exists()){
                File back=new File(f.getPath()+".bak");
                FileReader fr=new FileReader(f);
                FileWriter fw=new FileWriter(back);
                int n=0;
                while((n=fr.read())!=-1){fw.write(n);}
                fw.flush();
                fw.close();
            }
        FileWriter fw=new FileWriter(f);
        PrintWriter pw=new PrintWriter(fw,true);
        pw.println(content);
        pw.flush();
        pw.close();
        success=1;
        }catch(IOException e){success=0;System.out.println("Error in saving file : "+dir+"\nReason : "+e);}
        return success;
    }
    public static int delete(String uname,String filepath){
        File f=new File("accounts/"+uname+"/"+filepath);
        int n=0;
        if(f.exists()){
            n=(f.delete())?1:0;
        }
        return n;
    }
    public static int createDir(String uname, String filepath){
        if(uname.length()>0){
            File f=new File("accounts/"+uname+"/"+filepath);
            f.mkdirs();
            return 1;
        }
        return 0;
    }
    public static String read(String path){
        File f = new File(path);
        String str="";
        if(!f.exists())return "File not locatable";
        try{
            FileReader fr =new FileReader(f);
            Scanner sc=new Scanner(fr);
            while(sc.hasNext())str = sc.next();
        }catch(IOException e){e.printStackTrace();}
        return str;
    }
    public static void main(String[] args){
        System.out.println("Welcome to the FileManager tester : ");
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter a directory path to begin :");
        String str=sc.nextLine();
        String ar[]=ls(str);
        for(int i=0;i<ar.length;i++){System.out.println(ar[i]);}
    }
}

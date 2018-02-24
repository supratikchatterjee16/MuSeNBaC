package musenbac;

import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;

import java.io.*;

import java.util.Scanner;
import java.util.Date;

import musenbac.lib.*;
class Client implements Runnable{
    private Socket s;
    private static String ipid[][][]=new String[255][255][255],chat[][]=new String[2][100];
    private final String encoding="UTF-8";//use this with URLEncoder and URLDecoder
    private int address[];
    private String account;
    public Client(Socket a){
        s=a;
        address=new int[4];
        int ctr=0;
        String add=s.getRemoteSocketAddress().toString();
        add=add.substring(1);
        int i=0;
        while(ctr<4){
            if(Character.isDigit(add.charAt(i)))address[ctr]=(address[ctr]*10)+Integer.parseInt(add.substring(i,i+1));
            else ctr++;
            i++;
        }
        try{
            account=ipid[address[1]][address[2]][address[3]];
        }catch(Exception e){System.out.println("Unallocated query");}
    }
    private String mainLogic(String str){
        System.out.println(str);
        //The incoming string has two parts to it. First two digits select the function.
        //The other part holds the data. Hence, we shall divide it in this way.
        String selector=str.substring(0,2),data=str.substring(2);
        //The selector uses the logic 0_ for entry.html functions. 1_ for main.html functions.
        //0_ 0 is for login data. 1 is for new form data
        int flag=0;
        String res="0";
        switch(Integer.parseInt(str.substring(0,1))){
            case 0:{//entry logic
                System.out.println("Entry logic executed");
                switch(Integer.parseInt(str.substring(1,str.indexOf("data")))){
                    case 0:{
                        System.out.print("Challenge : ");
                        String arr[]=extract(data,2);
                        File f=new File("accounts/"+arr[0]);
                        if(f.exists()){
                            try{
                                FileReader fr=new FileReader("pswd/"+arr[0]+".txt");
                                Scanner sc=new Scanner(fr);
                                if(arr[1].equals(sc.nextLine())){
                                    ipid[address[1]][address[2]][address[3]]=arr[0];
                                    //System.out.println(ipid[address[1]][address[2]][address[3]]);
                                    System.out.println("Success.");
                                    return "11";
                                }
                                else {System.out.println("Failed. Incorrect password.");return "10";}
                            }catch(IOException e){System.out.println("File has been removed.");}
                        }
                        else{System.out.println("Failed. Unknown account.");return "1";}
                    };break;
                    case 1:{
                        String arr[]=extract(data,4);
                        System.out.println("Making new account for : "+arr[2]);
                        try{
                        File f=new File("pswd");
                        if(!f.exists())f.mkdir();
                        f=new File("accounts");
                        if(!f.exists())f.mkdir();
                        f=new File("accounts/"+arr[2]+"/res");
                        f.mkdirs();
                        FileWriter fw=new FileWriter("pswd/"+arr[2]+".txt");
                        fw.write(arr[3]+"\n");
                        fw.flush();
                        fw.close();
                        fw=new FileWriter("accounts/"+arr[2]+"/meta.inf");
                        fw.write(arr[0]+"\n");
                        fw.write(arr[1]);
                        fw.flush();
                        fw.close();
                        copyBlob("/res/bg2.jpg","accounts/"+arr[2]+"/res/bg.jpg");
                        }catch(IOException e){System.out.println("\n\nFile creation error. Error : "+e);}
                    };break;
                    default:flag=1;
                }
                };break;
            case 1:{//main logic
                System.out.println("Main logic executed");
                switch(Integer.parseInt(str.substring(1,2))){
                    case 0:{
                        switch(Integer.parseInt(str.substring(2,3))){
                            case 0:{//File list logic
                                if(str.substring(3).equals("/.."))res="Faulty input";
                                else res=Utilities.arrayToString(FileManager.ls("./accounts/"+account+str.substring(3)));
                            };break;
                            case 1://Folder structure creation logic
                            {};break;
                            case 3://File commit logic
                            {FileManager.save(account,"","");};break;
                            case 5://File deletion logic
                            {};break;
                        }
                    };break;
                    case 1:{//Problems fetch logic
                        System.out.print("Problem lookup response :");
                        res=Problems.fetch(Integer.parseInt(str.substring(2)));
                        System.out.println(str);
                    };break;
                    case 2:{
                        switch(Integer.parseInt(str.substring(2,3))){
                            case 0:{//chat data transmit logic
                                try{
                                    if(account.length()>0){
                                        String arr[][]=chatStorage.fetch();
                                        res="";
                                        for(int i=0;i<arr.length;i++){res+=arr[i][0]+"&"+arr[i][1]+"&";}
                                    }
                                }catch(Exception e){
                                    System.out.println("Chat data transmit error.\nReason : "+e);
                                    if(res.length()<5)res="0";
                                }
                                if(res.length()<=1)res="0";
                                return res;
                            }
                            case 1:{//chat data store and transmit logic
                                System.out.println("Chat data from : "+account+". Text : "+str);
                                if(account.length()>0){
                                chatStorage.store(account,str.substring(3));
                                String arr[][]=chatStorage.fetch();
                                res="";
                                for(int i=0;i<arr.length;i++){res+=arr[i][0]+"&"+arr[i][1]+"&";}
                                return res;}
                            };break;
                        }
                    };break;
                    case 3:{
                        switch(Integer.parseInt(str.substring(2,3))){
                            case 0://fetch files list
                            ;break;
                            case 1://fetch a single file with name
                            ;break;
                            case 2://fetch background
                            ;break;
                        }
                    };break;
                    case 4:{
                        ipid[address[1]][address[2]][address[3]]=null;
                        res="1";
                    };break;
                }
            };break;//main logic ends here
            default:flag=1;
        }
        if(flag==1){
            String address=s.getInetAddress().toString();
            System.out.println("Bad Inputs detected. Address : "+address);
            try{log("Bad request. Possible malicious intent. Address: "+address);}catch(IOException e){System.out.println("Logging of event failed.");};
        }
        return res;
    }
    private String[] extract(String str,int n){
        Scanner sc=new Scanner(str.substring(str.indexOf("data")));
        sc.useDelimiter("&");
        String arr[]=new String[n];
        for(int i=0;i<n;i++){
            String temp=sc.next();
            temp=temp.substring(6);
            try{arr[i]=URLDecoder.decode(temp,encoding);}catch(UnsupportedEncodingException e){System.out.println("Check Decoding format");}
            //System.out.println(arr[i]);
        }
        return arr;
    }
    private synchronized void log(String str)throws IOException{
        Date d=new Date();
        String date=d.toString();
        File f=new File("logs/"+date.substring(0,date.indexOf(":")-2)+date.substring(date.length()-4)+".txt");
        if(!f.exists()){
            File fn=new File("logs");
            if(!fn.isDirectory())fn.mkdir();
            f.createNewFile();
        }
        FileWriter fw=new FileWriter(f,true);
        fw.write("["+d.toString()+"]\t"+s.getRemoteSocketAddress().toString()+"\t"+str+"\r\n");
        fw.flush();
        fw.close();
    }
    private synchronized String read(){//Note. Do not under any circumstances modify this part.
        String str="",postdata="";
        boolean flag=true;
        int n=0,ctr=0,arr[]=new int[4];
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
            while((n=br.read())!=-1){
                //System.out.print(n+" ");
                if(ctr==0){
                    ctr++;
                    if((char)n=='G'){
                        str='G'+br.readLine();
                        break;
                    }
                    else str='P'+br.readLine();
                }
                for(int i=0;i<3;i++)arr[i]=arr[i+1];
                arr[3]=n;
                if(arr[0] == arr[2] && arr[2] == 13 && arr[1] == arr[3] && arr[3] == 10)ctr++;
                if(ctr==2)postdata+=(char)n;//includes the \r\n\r\n part as well. That is 8 characters
                if(ctr==3)break;
            }
            if(postdata.length()>2)str=str.concat(postdata.substring(0,postdata.length()-2));
         }catch(IOException e){System.out.println(e);}
        return str;
    }
    private synchronized String[] evaluated(String in){
        Scanner sc=new Scanner(in);
        String data[]=new String[5];
        String err[]={"","","Something went wrong"};
        int ctr=0;
        try{while(sc.hasNext())data[ctr++]=sc.nextLine();
        sc=new Scanner(data[0]);}catch(NullPointerException e){System.out.println("Null pointer exception 197 198 : "+e+"\nData : "+in);return err;}
        String method=sc.next(),path=sc.next(),mimetype="text/plain",dat="";
        //GET handler section:
        if(method.equalsIgnoreCase("GET")){
            if(path.endsWith("css")){path="styles"+path;mimetype="text/css";}
            else if(path.endsWith("html")||path.endsWith("htm")){path="html"+path;mimetype="text/html";}
            else if(path.endsWith("js")){path="scripts"+path;mimetype="text/javascript";}
            else if(path.endsWith("jpg")||path.endsWith("jpeg")){path="res"+path;mimetype="image/jpeg";}
            else if(path.endsWith("ico")||path.endsWith("png")){path="res"+path;mimetype="image/png";}
            else if(path.endsWith("svg")){path="res"+path;mimetype="image/svg+xml";}
            else if(path.endsWith("ttf")){path="fonts"+path;mimetype="font/ttf";}
            try{
               log(data[0]);
               //System.out.println(data[0]);
               if(path.length()<3){
                    path="html/entry.html";mimetype="text/html";
                    if(ipid[address[1]][address[2]][address[3]]!=null)path="html/main.html";
               }
            }catch(IOException e){System.out.println("Error in reading required file : "+e);}
        }//POST data section comes next:
        else if(method.equalsIgnoreCase("POST")){
            path="";
            mimetype="text/plain";
            System.out.println("\nMain logic.\nData recieved: "+data[0]+":"+data[1]+":"+data[2]);
            dat=mainLogic(data[1]);
            //try{dat=URLEncoder.encode(dat,encoding);}catch(UnsupportedEncodingException e){System.out.println("The encoding was changed hence, it malfunctioned.");}
        }
        String arr[]={path,mimetype,dat};
        return arr;
    }
    private synchronized void transmit(String[] data){
        //data 0 should contain filename, if there is a file to be sent otherwise, it should have nothing. lenght = 0
        //data 1 should contain the mimetype to be sent....
        //data 2 should contain the response.
        System.out.println(data[0]+", "+data[1]+", "+data[2]);
        int n=0;
        try{
            PrintStream pw = new PrintStream(new BufferedOutputStream(s.getOutputStream()));
            pw.print("HTTP/1.1 200 OK\r\n");
            pw.print("Content-type: "+data[1]+"\r\n\r\n");
            //pw.print(data[2]);
            File f=new File(data[0]);
            if(data[0].length()!=0){
                if(f.exists()){
                    InputStream fr=new FileInputStream(data[0]);
                    byte b[]=new byte[4096]; 
                    while((n=fr.read(b))!=-1){
                        pw.write(b,0,n);
                    }
                }
                else{
                    System.out.println("\nFile not found : "+data[0]+".\nCreate one or place right files in the right locations.");
                    pw.print("Nothing to be found...\r\n");
                }
            }//Get data transmit section
            else{
               //System.out.println(data[2]);
               pw.print(data[2]);
            }
            pw.close();  
        }catch(IOException e){System.out.println("Transmit failure: "+e);}
    }
    private void copyBlob(String path1,String path2)throws IOException{
        InputStream is=null;
        OutputStream os=null;
        try{
            is=new FileInputStream(path1);
            os=new FileOutputStream(path2);
            byte b[]=new byte[1024];int n=0;
            while((n=is.read(b))>0)os.write(b,0,n);
           }finally{
            is.close();
            os.close();
            }
    }
    public void run(){transmit(evaluated(read()));}
}

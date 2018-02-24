//Part : Variables
var account="";//user details are to be stored in variables in this section
var files=[""];//environment details are to be stored in this section
var CLE=["new","newproject","newclass","open","welcome"],
    CME=["classes"],
    CRE=["chat", "problem", "compiler"];//This stands for custom position elements(C_E)
var lang="Language to be used";
var set1=[],set2=[];
var supportedLanguages=["Brainfuck","C","C++","Java","Python"];
var keywords=[[[],[]],[["#include<","#define ","#undef ", "#if","#ifdef","#ifndef","#error","use ", "_FILE_","_LINE_","_DATE_","_TIME_", "_TIMESTAMP_", "#pragma ", "__asm","__int8","__int16","__int32","__int64","__leave","__inline","__finally", "dllexport2 ", "__declspec", "__cdecl","__based1","__fastcall","__stdcall","__try"], ["void ","int ","long ","longint ","char ","double ","float ","public ","extern ","private ","protected ","case ","switch","enum ","short ","default:","do{","}while","while","union ","sizeof","static "]],[["#include ","#define ","#undef ","#if","#ifdef","#ifndef","#error","_FILE_","_LINE_","_DATE_","_TIME_","_TIMESTAMP_","#pragma "],["void ","int ","long ","longint ","char ","double ","float ","public ","extern ","private ","protected ","case ","switch","enum ","short ","default:","do{","}while","while","union ","sizeof","static "]],[["import ","implements ","extends ","throw ","throws ","try{","}catch","switch","do{","}while","for","assert ","break;","case ","default:","byte "],["public ","class ","static ","void ","int ","long ","double ","char ","private ","protected ","final "]],[["import "," as ","lambda"],["if ","def ","else ","elif "," and "," or ","for "," in "," range","while "," is ","yield","not ","return"]]];
var res="";
//Part-End

//Part : Code
//Section : Miscellaneous
String.prototype.replaceAt=function(index,ch){
    var str1=this.substring(0,index), str2="";
    if(index<this.length-1)str2=this.substring(index+1);
    return (str1+ch+str2);
}
//Section-End
//Section : Selection for pane display
function right(ctr){
    for(var i=0;i<CRE.length;i++)
        if(i!=ctr) document.getElementById(CRE[i]).style.display="none";
        else document.getElementById(CRE[i]).style.display="block";
}
function left(ctr){
    for(var i=0;i<CLE.length;i++)
        if(i==ctr)document.getElementById(CLE[i]).style.display="block";
        else document.getElementById(CLE[i]).style.display="none";
}
//Section-End

//Section : About toggling
var aboutState=0;
function about(){
    if(aboutState%2==0){
        document.getElementById("about").style.display="inline";
        about=0;
        }
    else document.getElementById("about").style.display="none";
    aboutState++;
}
//Section-End

//Section : Background
function backChange(){
    var x=0;
    while(x<1||x>65)x=Math.floor(Math.random()*100);
    document.body.style.background="url('bg"+x.toString()+".jpg')";
    document.body.style.backgroundPosition="center";
    document.body.style.backgroundSize="cover";
}
//Section-End
//Section : Language Selection
var opt=0;
function option(){
    if(opt%2==0){
    document.getElementById("language-selector").innerHTML="";
    for(var i=0;i<supportedLanguages.length;i++)
    document.getElementById("language-selector").innerHTML+="<button onclick='select("+i+")'>"+supportedLanguages[i]+"</button>";
    opt=0;
    }
    else document.getElementById("language-selector").innerHTML="<button>"+lang+"</button>";
    opt++;
}
function select(i){
    lang=supportedLanguages[i];
    set1=keywords[i][0];
    set2=keywords[i][1];
}
//Section-End

//Section : Syntax Highlighting
function sync(){
    var elem1=document.getElementById("codespace"),elem2=document.getElementById("highlightArea");
    var text=elem1.innerHTML,mod=text;
    var i=0,j=0;
    var alpha=/^[A-Za-z]+$/;
    elem2.scrollTop=elem1.scrollTop;
    elem2.scrollLeft=elem1.scrollLeft;
    var pos1=0,pos2=0;
    for(i=0;i<set1.length;i++){
        var t="<hl1>"+set1[i]+"</hl1>";
        mod=mod.replace(new RegExp(set1[i],"g"),t);
    }
    for(i=0;i<set2.length;i++){
        var t="<hl2>"+set2[i]+"</hl2>";
        mod=mod.replace(new RegExp(set2[i],"g"),t);
    }
    elem2.innerHTML=mod;
}
//Section-End

//Section : Problems
//Note: requires 2 as argument to send
var problemNum=0;
function prevproblem(){if(problemNum>0)problemNum--;send(2,"11"+problemNum);}
function nextproblem(){problemNum++;send(2,"11"+problemNum);}
function setProblem(text){
    var x=document.getElementById("statement");
    text="<center>"+text;
    if(text=="0")x.innerHTML="Nothing found. Please go back";
    else x.innerHTML=text+"</center>";
    text=decodeURIComponent(text);
}
//Section-End

//Section : Chat Logic
//Formatting note : <!--div id="message"><n>Shubham</n>Hello </div-->
var chatRefresh;
function fetchChat(){send(0,"120");}
function refreshChatWith(text){
    var ctr=0;
    //console.log(text);
    if(text!="0"){
        text="<div id=\"message\"><n>"+text;
        for(var i=0;i<text.length-1;i++){
            if(text.charAt(i)=="&"&&i!=text.length-1){
                if(ctr%2==0)text=text.replaceAt(i,"</n>");
                else text=text.replaceAt(i,"</div><div id=\"message\"><n>");
                ctr++;}
        }
        text=text.replaceAt(text.lastIndexOf("&"),"</div>");
        document.getElementById("messages").innerHTML=decodeURIComponent(text);
        }
    else{
        clearInterval(chatRefresh);
        text="<center>Looks like no data was found. Send something to restart chat</center>";
        document.getElementById("messages").innerHTML=text;
        }
}
function activateChat(){console.log("Chat process initiated");document.getElementById("messages").value=0;chatRefresh=setInterval(function(){fetchChat()},100);}
function uploadChat(){send(0,"121"+encodeURIComponent(document.getElementById("sendmessage").value));document.getElementById("sendmessage").value="";clearInterval(chatRefresh);chatRefresh=setInterval(function(){fetchChat()},100);}
function triggerChat(e){var code=e.keyCode || e.which;if(code==13)uploadChat();}
//Section-End

//Section : File and Folders
var currentDirectory="/";
function createFile(){
    var x=document.getElementById("").value;
}
function saveFile(){
    var filename=document.getElementById("");
}
function listFiles(x){
console.log("listFiles parameter"+x);
if(x==".."){currentDirectory=currentDirectory.substring(0,currentDirectory.lastIndexOf("/"));}
else if(x==""){}
else{
if(currentDirectory.charAt(currentDirectory.length-1)!="/")currentDirectory=currentDirectory+"/"+x;
else currentDirectory=currentDirectory+x;
}
send(1,"100"+currentDirectory);
console.log("currentDirectory : "+currentDirectory);
if(currentDirectory("")){}
}
//The html structure of elements to be generated
//<button onclick="listFiles(this.innerHTML)">Sample Folder</button>
function loadFiles(str){
console.log("LoadFiles : "+str);
left(3);
if(str.indexOf("folder+")!=-1){
    var temp="<h3>Open</h3><button onclick=\"listFiles(this.innerHTML)\">..</button><button onclick=\"listFiles(this.innerHTML)\">";
    for(var i=7;i<str.length-1;i++){
        if(str.charAt(i)=="+")temp+="</button><button onclick=\"listFiles(this.innerHTML)\">";
        else temp+=str.charAt(i);
    }
    document.getElementById("open").innerHTML=temp;
}
else if(str.indexOf("file+")!=-1){
    left(4);
    currentDirectory="/";
    var temp=str.substring(5);
    for(var i=0;i<temp.length;i++){
        if(temp.charAt(i)=="+")temp=temp.replaceAt(i," ");
        else if(temp.charAt(i)=="%"&&temp.substring(i,i+3)=="%0A"){
            temp=temp.replaceAt(i+1,"");
            temp=temp.replaceAt(i+1,"");
            temp=temp.replaceAt(i,"<br>");
        }
    }
    temp=decodeURIComponent(temp);
    console.log(temp);
    document.getElementById("codespace").innerHTML=temp;
}
}
//Section-End

//Section : Sign Out
function signOut(){send(3,"14");}
//Section-End

//Section : Main functions
var response="";
function makeSenseOf(select,text){
    switch(select){
        case 0:{/*Chat*/refreshChatWith(text);};break;
        case 1:{/*Files List*/loadFiles(text);};break;
        case 2:{/*Problem*/setProblem(text);};break;
        case 3:{/*Sign out*/if(text=="1")location.reload();else alert("Unsuccesful");};break;
        case 4:{};break;
        case 5:{};break;
        default:console.error("Wrong function, or send format. Check main.js for errors.");
    }
}
function send(selector,str){
    var xhr=new XMLHttpRequest();
    xhr.open("POST","",true);
    xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xhr.send(str+"\r\n\r\n");
    var res="";
    xhr.onreadystatechange=function(){
        if(xhr.readyState==4 && xhr.status==200){makeSenseOf(selector,xhr.responseText);}
    };
}
function fullscreen(){window.open('/', 'newFullScreenWindow', 'fullscreen=yes, channelmode=yes,scrollbars=yes');}
function init(){console.log("Musenbac server login succesful");setInterval(function(){sync()},40);}
//Section-End

//Part-End

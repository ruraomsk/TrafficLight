package ru.list.ruraomsk.trafficlight.slot;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Slot extends Thread implements Runnable{
    private String address;
    private int port;
    private Socket socket=null;
    private boolean work;
    private PrintWriter bufferOut=null;
    private BufferedReader bufferIn=null;
    private ConcurrentLinkedQueue<String> toWrite;
    private ConcurrentLinkedQueue<String> toRead;
    private String readMessage() throws IOException {
        return bufferIn.readLine();
    }
    private void sendMessage(String message){
        if(!work) return;
        bufferOut.println(message);
        bufferOut.flush();
    }
    private void close(){
        work=false;
        if(socket!=null){
            try {
                socket.close();
                if (bufferOut!=null){
                    bufferOut.close();
                }
                if (bufferIn!=null){
                    bufferIn.close();
                }
            } catch (IOException e) {
                Log.d("litrDebug",e.getMessage());
            }
        }

    }
    public Slot(String address,int port){
        toRead=new ConcurrentLinkedQueue<>();
        toWrite=new ConcurrentLinkedQueue<>();
        this.address=address;
        this.port=port;
    }
    @Override
    public void run(){
        try {
            socket=new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getByName(address),port),5000);
            socket.setKeepAlive(true);
            socket.setSoTimeout(1000);
            bufferIn=new BufferedReader((new InputStreamReader(socket.getInputStream())));
            bufferOut=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()),64*1024));
            work=true;
            Thread reader =new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        while(work){
                            if(!bufferIn.ready()){
                                sleep(100);
                                continue;
                            }
                            String message=bufferIn.readLine();
                            Log.d("litrDebug","Read:"+message+":");

                            toRead.add(message);
                        }
                    } catch (IOException | InterruptedException e) {
                        Log.d("litrDebug",e.getMessage());
                    }
                    Log.d("litrDebug","close slot reader");
                    close();
                }
            });
            reader.start();
            String lastMessage="";
            while(work){
                int counter=0;
                while(toWrite.isEmpty() || counter<5){
                    counter++;
                    sleep(100);
                }
                if (!toWrite.isEmpty()){
                    lastMessage=toWrite.poll();

                } else {
                    if (lastMessage.length()==0) {
                        continue;
                    }
                }

                sendMessage(lastMessage);
                Log.d("litrDebug","Send:"+lastMessage);
                if(lastMessage.startsWith("exit")) {
                    sleep(1000);
                    break;
                }
            }
        } catch (InterruptedException | SocketException | UnknownHostException e) {
            Log.d("litrDebug",e.getMessage());
        } catch (IOException e) {
            Log.d("litrDebug",e.getMessage());
        }
        Log.d("litrDebug","close slot");

        close();
    }
    public boolean isWork (){
        return work;
    }
    public void writeMessage(String message){

        toWrite.add(message);
    }
    public String getMessage(){
        return toRead.poll();
    }
    public boolean isMessage(){
        return !toRead.isEmpty();
    }
}

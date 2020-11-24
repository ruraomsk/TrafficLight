package ru.list.ruraomsk.trafficlight.DB;

import android.content.Context;
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

public class UpdateDb {
    private DB db;
    private Socket socket;

    private PrintWriter bufferOut=null;
    private BufferedReader bufferIn=null;

    private String readMessage() throws IOException, InterruptedException {
        if(!socket.isConnected()) return null;
        int count=0;
        while (!bufferIn.ready()) {
            Thread.sleep(100);
            if (++count >20) return null;
        }
        return bufferIn.readLine();
    }

    public UpdateDb(DB db,String host,int port){
        try {
            socket=new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getByName(host),port),1000);
            socket.setKeepAlive(true);
            socket.setSoTimeout(1000);
            bufferIn=new BufferedReader((new InputStreamReader(socket.getInputStream())));
            bufferOut=new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()),64*1024));
            db.clearAll();
            while(true){
                String message=readMessage();
                if (message==null) break;
                if (message.startsWith("end")) break;
                db.appendString(message);
            }
        } catch (IOException | InterruptedException ex) {
            Log.d("litrDebug",ex.getMessage());
        }
        close();
    }
    private void close(){
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

}

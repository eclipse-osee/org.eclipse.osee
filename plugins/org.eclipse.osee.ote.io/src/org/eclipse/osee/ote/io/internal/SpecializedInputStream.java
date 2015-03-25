package org.eclipse.osee.ote.io.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SpecializedInputStream extends InputStream {

   private StringBuffer buffer = new StringBuffer();
   private ReentrantLock lock;
   private Condition newData;
   private InputStream monitorIn;
   private Thread th;
   
   public SpecializedInputStream(InputStream in){
      monitorIn = in;
      lock = new ReentrantLock();
      newData = lock.newCondition();
      th = new Thread(new Runnable(){

         @Override
         public void run() {
            InputStreamReader isr=new InputStreamReader(monitorIn);
            BufferedReader br=new BufferedReader(isr);
            boolean validSystemIn = true;
            while(validSystemIn){
               try{
                  String line = br.readLine();
                  if(line != null){
                     add(line); 
                  } else {
                     validSystemIn = false;
                  }
               } catch (Throwable th){
                  
               }
            }
         }
         
      });
      th.setDaemon(true);
      th.setName("OTE System.in monitor");
      th.start();
   }
   
   @Override
   public int read() throws IOException {
      int mychar = 0;
      lock.lock();
      try{
         if(buffer.length() == 0){
            newData.awaitUninterruptibly();
         }
         mychar = buffer.charAt(0) & 0xFF;
         buffer.deleteCharAt(0);
      } catch (Throwable th){
      } finally {
         lock.unlock();
      }
      return mychar;
   }
   
   public void add(String str){
      lock.lock();
      try{
         buffer.append(str);
         newData.signalAll();
      } finally {
         lock.unlock();
      }
   }
 
}

package org.eclipse.osee.ote.core.framework.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.ILoggerListener;
import org.eclipse.osee.framework.logging.OseeLog;

public class BatchLog implements ILoggerListener {
   
   private File fileToWriteTo;
   private FileOutputStream fos;
   private PrintWriter out;
   
   public BatchLog(File fileToWriteTo){
      this.fileToWriteTo = fileToWriteTo;
   }
   
   public void open() throws FileNotFoundException{
      fos = new FileOutputStream(fileToWriteTo);
      out = new PrintWriter(fileToWriteTo);
      OseeLog.registerLoggerListener(this);
   }
   
   public void close(){
      try {
         fos.close();
         out.close();
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         OseeLog.unregisterLoggerListener(this);
      }
   }
   
   public void flush(){
      out.flush();
   }
   
   @Override
   public void log(String loggerName, Level level, String message, Throwable th) {
      if(level.intValue() >= Level.SEVERE.intValue()){
         out.printf("%s %s\n", level.getName(), message);
         if(th != null){
            th.printStackTrace(out);
         }
      }
   }

}

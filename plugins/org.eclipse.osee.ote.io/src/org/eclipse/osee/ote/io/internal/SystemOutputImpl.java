package org.eclipse.osee.ote.io.internal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.osee.ote.io.OTEServerFolder;
import org.eclipse.osee.ote.io.SystemOutput;
import org.eclipse.osee.ote.io.SystemOutputListener;
import org.eclipse.osee.ote.properties.OtePropertiesCore;

public class SystemOutputImpl implements SystemOutput {

   private static final String SYSTEM_OUT_FILE = "systemout.txt";

   private BufferedOutputStream outputStream;
 
   private OTEServerFolder serverFolder;
   
   public void start() {
      if(OtePropertiesCore.ioRedirect.getBooleanValue(false)){
         if(OtePropertiesCore.ioRedirectFile.getBooleanValue(false)){
            File wd = serverFolder.getCurrentServerFolder();
            wd.mkdirs();
            if(wd.exists() && wd.isDirectory()){
               try {
                  outputStream = new BufferedOutputStream(new FileOutputStream(new File(wd, SYSTEM_OUT_FILE)));
                  EarlyIORedirect.getInstance().addListener(new SystemOutputListerImpl(outputStream));
               } catch (FileNotFoundException e) {
                  e.printStackTrace();
               }
            }            
         }
      }
   }

   public void stop(){
      try {
         if(outputStream != null){
            outputStream.flush();
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   public void bindOTEServerFolder(OTEServerFolder folder) {
      this.serverFolder = folder;
   }
   
   public void unbindOTEServerFolder(OTEServerFolder folder) {
      this.serverFolder = null;
   }
   
   @Override
   public void addListener(SystemOutputListener listener){
      EarlyIORedirect.getInstance().addListener(listener);
   }
   
   @Override
   public void removeListener(SystemOutputListener listener){
      EarlyIORedirect.getInstance().removeListener(listener);
   }
   
   @Override
   public synchronized void write(String input){
      EarlyIORedirect.getInstance().write(input);
   }
}

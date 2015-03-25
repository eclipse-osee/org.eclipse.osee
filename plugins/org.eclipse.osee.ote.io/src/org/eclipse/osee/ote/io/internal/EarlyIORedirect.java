package org.eclipse.osee.ote.io.internal;

import java.io.InputStream;
import java.io.PrintStream;

import org.eclipse.osee.ote.io.SystemOutputListener;
import org.eclipse.osee.ote.properties.OtePropertiesCore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class EarlyIORedirect implements BundleActivator {

   private static EarlyIORedirect instance;

   public static EarlyIORedirect getInstance(){
      return instance;
   }
   
   private PrintStream oldErr;
   private PrintStream oldOut;
   private InputStream oldIn;
   private SpecializedOut out;
   private SpecializedInputStream in;
   private String newline;
   
   @Override
   public void start(BundleContext context) throws Exception {
      instance = this;
      oldErr = System.err;
      oldOut = System.out;
      oldIn = System.in;
      if(OtePropertiesCore.ioRedirect.getBooleanValue(false)){
         out = new SpecializedOut(new SpecializedOutputStream());//outputStream == null ? oldOut : outputStream));
         newline = OtePropertiesCore.lineSeparator.getValue();
         in = new SpecializedInputStream(oldIn);
         if(!OtePropertiesCore.ioRedirectFile.getBooleanValue(false)){
            out.addListener(new SystemOutputListerImpl(System.out));
         }
         System.setIn(in);
         System.setOut(out);
         System.setErr(out);      
      }
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if(out != null){
         out.flush();    
      }
   }
   
   public void addListener(SystemOutputListener listener){
      if(out != null){
         out.addListener(listener);
      }
   }
   
   public void removeListener(SystemOutputListener listener){
      if(out != null){
         out.removeListener(listener);
      }
   }
   
   public void write(String input){
      if(in != null){
//         System.out.println(input);
         in.add(input+newline);
      }
   }
   
   public void resetIO(){
      System.setIn(oldIn);
      System.setOut(oldOut);
      System.setErr(oldErr);
   }

}

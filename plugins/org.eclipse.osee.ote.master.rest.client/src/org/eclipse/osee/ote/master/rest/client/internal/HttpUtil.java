package org.eclipse.osee.ote.master.rest.client.internal;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;

public class HttpUtil {

   public static boolean canConnect(URI targetUri) {
      try{
         HttpURLConnection connection = (HttpURLConnection)targetUri.toURL().openConnection();
         connection.setRequestMethod("HEAD");
         int responseCode = connection.getResponseCode();
         if(responseCode == 200){
            return true;
         }
      } catch (Throwable th){
         OseeLog.log(HttpUtil.class, Level.INFO, th);
      }
      return false;
   }
}

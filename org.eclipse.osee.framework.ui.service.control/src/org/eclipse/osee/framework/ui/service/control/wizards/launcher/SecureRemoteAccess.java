/*
 * Created on Apr 4, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.service.control.wizards.launcher;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * @author Roberto E. Escobar
 */
public class SecureRemoteAccess {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(SecureRemoteAccess.class);
   private Session session;

   private SecureRemoteAccess(String host, String user, String password) throws Exception {
      JSch.setLogger(new LogForwarding());
      JSch jsch = new JSch();
      session = jsch.getSession(user, host);
      session.setPassword(password);
      session.connect(30000);
   }

   public String executeCommandList(String[] commands) throws Exception {
      StringBuilder toReturn = new StringBuilder();
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      Channel channel = null;
      try {
         channel = session.openChannel("shell");
         channel.setOutputStream(output);
         channel.connect(3000);
         PrintWriter cmds = new PrintWriter(channel.getOutputStream());
         for (int i = 0; i < commands.length; i++) {
            cmds.println(commands[i]);
            cmds.flush();
         }
         toReturn.append(output.toString());
      } finally {
         output.close();
         channel.disconnect();
      }
      return toReturn.toString();
   }

   public void uploadFiles(String[] localFiles, String destinationDirectory) throws Exception {
      // we need to break out the local files so we'll build a tree
      //      SCPClient client = connection.createSCPClient();
      //      client.put(localFiles, destinationDirectory);
   }

   public static SecureRemoteAccess getRemoteAccessAuthenticateWithPassword(String host, String username, String password) throws Exception {
      return new SecureRemoteAccess(host, username, password);
   }

   private static class LogForwarding implements com.jcraft.jsch.Logger {
      static Map<Integer, Level> levelMap = new HashMap<Integer, Level>();
      static {
         levelMap.put(com.jcraft.jsch.Logger.INFO, Level.INFO);
         levelMap.put(com.jcraft.jsch.Logger.WARN, Level.WARNING);
         levelMap.put(com.jcraft.jsch.Logger.FATAL, Level.SEVERE);
         levelMap.put(com.jcraft.jsch.Logger.DEBUG, Level.FINE);
         levelMap.put(com.jcraft.jsch.Logger.ERROR, Level.SEVERE);
      }

      public boolean isEnabled(int level) {
         return true;
      }

      public void log(int level, String message) {
         Level logLevel = levelMap.get(level);
         if (logLevel == null) {
            logLevel = Level.SEVERE;
         }
         logger.log(logLevel, message);
      }
   }
}

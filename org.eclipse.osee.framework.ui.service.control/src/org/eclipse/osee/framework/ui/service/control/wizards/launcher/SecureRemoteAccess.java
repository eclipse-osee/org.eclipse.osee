/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.service.control.wizards.launcher;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

/**
 * @author Roberto E. Escobar
 */
public class SecureRemoteAccess {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(SecureRemoteAccess.class);
   private Session session;

   private SecureRemoteAccess(String host, String user) throws Exception {
      JSch.setLogger(new LogForwarding());
      try {
         JSch jsch = new JSch();
         jsch.setKnownHosts(host);
         session = jsch.getSession(user, host);
         session.setUserInfo(new PromptUserInfo());
         session.connect(30000);
      } catch (JSchException ex) {
         throw new Exception("Error connecting to server.", ex);
      }
   }

   public String executeCommandList(String[] commands) throws Exception {
      Channel channel = null;
      CharBackedInputStream inputStream = new CharBackedInputStream();
      OutputStream outputStream = new ByteArrayOutputStream();
      try {
         channel = session.openChannel("shell");
         channel.setInputStream(inputStream);
         channel.setOutputStream(outputStream);

         for (String cmd : commands) {
            inputStream.append(cmd);
            inputStream.append("\n");
         }

         channel.connect();
         Thread.sleep(5000);

      } catch (JSchException ex) {
         throw new Exception("Error executing commands on server.", ex);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
         if (outputStream != null) {
            outputStream.close();
         }
         if (channel != null) {
            channel.disconnect();
         }
      }
      return outputStream.toString();
   }

   public ChannelSftp getScpConnection() throws Exception {
      Channel channel = session.openChannel("sftp");
      channel.connect();
      return (ChannelSftp) channel;
   }

   public static SecureRemoteAccess getRemoteAccessAuthenticateWithPassword(String host, String username) throws Exception {
      return new SecureRemoteAccess(host, username);
   }

   private static class LogForwarding implements com.jcraft.jsch.Logger {
      private static Map<Integer, Level> levelMap = new HashMap<Integer, Level>();
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

   private final class PromptUserInfo implements UserInfo, UIKeyboardInteractive {
      private String password;

      public String getPassword() {
         return password;
      }

      public boolean promptYesNo(String message) {
         Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
         MessageDialog dialog =
               new MessageDialog(shell, "Warning", null, message, MessageDialog.WARNING, new String[] {
                     IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 0);
         return dialog.open() == 0;
      }

      public String getPassphrase() {
         return null;
      }

      public boolean promptPassphrase(String message) {
         return true;
      }

      public boolean promptPassword(String message) {
         Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
         MultiTextDialog inputDialog =
               new MultiTextDialog(shell, "Password", "Enter password: ", new String[] {"password:"},
                     new boolean[] {false});
         inputDialog.setBlockOnOpen(true);
         int result = inputDialog.getReturnCode();
         if (result == Window.OK) {
            String[] inputs = inputDialog.getValue();
            if (inputs != null && inputs.length == 1) {
               password = inputs[0];
               return true;
            }
         }
         return false;
      }

      public void showMessage(String message) {
         Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
         MessageDialog.openInformation(shell, "Log-in Message", message);
      }

      public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
         Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
         MultiTextDialog inputDialog =
               new MultiTextDialog(shell, destination + ": " + name, "Enter Password: ", prompt, echo);
         inputDialog.setBlockOnOpen(true);
         int result = inputDialog.open();
         if (result == Window.OK) {
            return inputDialog.getValue();
         }
         return null;
      }
   }
}

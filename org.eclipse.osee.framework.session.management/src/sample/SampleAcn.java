/*
 * Created on Oct 27, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.util.Arrays;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class SampleAcn {

   /**
    * Attempt to authenticate the user.
    * <p>
    * 
    * @param args input arguments for this application. These are ignored.
    */
   public static void main(String[] args) {
      String filePath =
            "C:\\UserData\\latestWorkspace3.4\\org.eclipse.osee.framework.session.management\\src\\sample\\sample_jaas.config";
      System.setProperty("java.security.auth.login.config", filePath);

      Configuration configuration = Configuration.getConfiguration();

      AppConfigurationEntry[] loginModuleEntries = configuration.getAppConfigurationEntry("Sample");
      if (loginModuleEntries == null) {
         // There are no entries for the specified login-app name
      }

      // List the login modules
      for (int i = 0; i < loginModuleEntries.length; i++) {
         // Get login module name
         String name = loginModuleEntries[i].getLoginModuleName();

         // Get login module flag
         AppConfigurationEntry.LoginModuleControlFlag flag = loginModuleEntries[i].getControlFlag();

         if (flag == AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL) {
            // The login module is not required to succeed.
            // Whether it succeeds or not, the next login module is invoked.
         } else if (flag == AppConfigurationEntry.LoginModuleControlFlag.REQUIRED) {
            // The login module is required to succeed.
            // Whether it succeeds or not, the next login module is invoked.
         } else if (flag == AppConfigurationEntry.LoginModuleControlFlag.REQUISITE) {
            // The login module is required to succeed. If it succeeds, the next
            // login module is invoked; otherwise, authentication fails and
            // no more login modules are invoked.
         } else if (flag == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT) {
            // If this login module succeeds, authentication succeeds and no
            // more login modules are invoked
         }
      }

      // Obtain a LoginContext, needed for authentication. Tell it 
      // to use the LoginModule implementation specified by the 
      // entry named "Sample" in the JAAS login configuration 
      // file and to also use the specified CallbackHandler.
      LoginContext lc = null;
      try {
         lc = new LoginContext("Sample", new MyCallbackHandler());
      } catch (LoginException le) {
         System.err.println("Cannot create LoginContext. " + le.getMessage());
         System.exit(-1);
      } catch (SecurityException se) {
         System.err.println("Cannot create LoginContext. " + se.getMessage());
         System.exit(-1);
      }

      // the user has 3 attempts to authenticate successfully
      int i;
      for (i = 0; i < 3; i++) {
         try {

            // attempt authentication
            lc.login();

            // if we return with no exception, authentication succeeded
            break;

         } catch (LoginException le) {

            System.err.println("Authentication failed:");
            System.err.println("  " + le.getMessage());
            try {
               Thread.currentThread().sleep(3000);
            } catch (Exception e) {
               // ignore
            }

         }
      }

      // did they fail three times?
      if (i == 3) {
         System.out.println("Sorry");
         System.exit(-1);
      }

      System.out.println("Authentication succeeded!");

   }
}

/**
 * The application implements the CallbackHandler.
 * <p>
 * This application is text-based. Therefore it displays information to the user using the OutputStreams System.out and
 * System.err, and gathers input from the user using the InputStream System.in.
 */
class MyCallbackHandler implements CallbackHandler {

   /**
    * Invoke an array of Callbacks.
    * <p>
    * 
    * @param callbacks an array of <code>Callback</code> objects which contain the information requested by an
    *           underlying security service to be retrieved or displayed.
    * @exception java.io.IOException if an input or output error occurs.
    *               <p>
    * @exception UnsupportedCallbackException if the implementation of this method does not support one or more of the
    *               Callbacks specified in the <code>callbacks</code> parameter.
    */
   public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

      for (int i = 0; i < callbacks.length; i++) {
         if (callbacks[i] instanceof TextOutputCallback) {

            // display the message according to the specified type
            TextOutputCallback toc = (TextOutputCallback) callbacks[i];
            switch (toc.getMessageType()) {
               case TextOutputCallback.INFORMATION:
                  System.out.println(toc.getMessage());
                  break;
               case TextOutputCallback.ERROR:
                  System.out.println("ERROR: " + toc.getMessage());
                  break;
               case TextOutputCallback.WARNING:
                  System.out.println("WARNING: " + toc.getMessage());
                  break;
               default:
                  throw new IOException("Unsupported message type: " + toc.getMessageType());
            }

         } else if (callbacks[i] instanceof NameCallback) {

            // prompt the user for a username
            NameCallback nc = (NameCallback) callbacks[i];

            System.err.print(nc.getPrompt());
            System.err.flush();
            nc.setName((new BufferedReader(new InputStreamReader(System.in))).readLine());

         } else if (callbacks[i] instanceof PasswordCallback) {

            // prompt the user for sensitive information
            PasswordCallback pc = (PasswordCallback) callbacks[i];
            System.err.print(pc.getPrompt());
            System.err.flush();
            pc.setPassword(readPassword(System.in));

         } else {
            throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
         }
      }
   }

   // Reads user password from given input stream.
   private char[] readPassword(InputStream in) throws IOException {

      char[] lineBuffer;
      char[] buf;
      int i;

      buf = lineBuffer = new char[128];

      int room = buf.length;
      int offset = 0;
      int c;

      loop: while (true) {
         switch (c = in.read()) {
            case -1:
            case '\n':
               break loop;

            case '\r':
               int c2 = in.read();
               if ((c2 != '\n') && (c2 != -1)) {
                  if (!(in instanceof PushbackInputStream)) {
                     in = new PushbackInputStream(in);
                  }
                  ((PushbackInputStream) in).unread(c2);
               } else
                  break loop;

            default:
               if (--room < 0) {
                  buf = new char[offset + 128];
                  room = buf.length - offset - 1;
                  System.arraycopy(lineBuffer, 0, buf, 0, offset);
                  Arrays.fill(lineBuffer, ' ');
                  lineBuffer = buf;
               }
               buf[offset++] = (char) c;
               break;
         }
      }

      if (offset == 0) {
         return null;
      }

      char[] ret = new char[offset];
      System.arraycopy(buf, 0, ret, 0, offset);
      Arrays.fill(buf, ' ');

      return ret;
   }
}

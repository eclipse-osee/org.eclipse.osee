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
package org.eclipse.osee.framework.ui.plugin.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.ui.plugin.security.OseeAuthentication.AuthenticationStatus;
import org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum;

/**
 * @author Roberto E. Escobar
 */
public class AuthenticationConsole {

   private static final int MAX_RETRIES = 3;
   private static final OseeAuthentication oseeAuthentication = OseeAuthentication.getInstance();

   private enum PromptEnums {
      USERNAME("Enter UserName: "), PASSWORD("Enter Password: "), DOMAIN("Enter Domain: ");

      private String prompt;

      PromptEnums(String prompt) {
         this.prompt = prompt;
      }

      public String getPrompt() {
         return prompt;
      }
   }

   private OutputStream outWriter;
   private OutputStream errorWriter;
   private BufferedReader bufferedReader;

   public AuthenticationConsole() {
      this(System.in, new PrintStream(System.out), new PrintStream(System.err));
   }

   public AuthenticationConsole(InputStream outStream, OutputStream outWriter, OutputStream errorWriter) {
      this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
      this.outWriter = outWriter;
      this.errorWriter = errorWriter;
   }

   private String getDetailMessage(AuthenticationStatus status) {
      String toReturn = "";
      switch (status) {
         case UserNotFound:
            toReturn = "User Id not found.\n" + "Enter your user id.";
            break;
         case InvalidPassword:
            toReturn = "Invalid Password.\n" + "Make sure <CAPS LOCK> is not enabled.\n" + "Enter a valid password.";
            break;
         case NoResponse:
            toReturn = "Please enter a valid user id and password.";
            break;
         default:
            break;
      }
      return toReturn;
   }

   private String waitForUserResponse(PromptEnums promptEnum) {
      String line = "";
      try {
         displayMessage(promptEnum.getPrompt() + "\n");
         line = bufferedReader.readLine();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
      return line;
   }

   public void run() {
      String message = "";
      for (int numberOfTries = 0; numberOfTries < MAX_RETRIES; numberOfTries++) {
         Map<PromptEnums, String> userInputs = getInputs();
         AuthenticationStatus status = authenticate(userInputs);
         switch (status) {
            case Success:
               numberOfTries = MAX_RETRIES;
               displayMessage(String.format("Authenticated. \nLogged in as: %s \n",
                     oseeAuthentication.getCredentials().getField(UserCredentialEnum.Name)));
               break;
            default:
               if (numberOfTries >= MAX_RETRIES - 1) {
                  message = "Maximum number of Retries reached.\n";
               } else {
                  message = getDetailMessage(status);
               }
               displayError(String.format("Authentication Failed.\n%s\n\n\n", message));
               break;
         }
      }
   }

   private void displayError(String value) {
      try {
         errorWriter.write(value.getBytes());
         errorWriter.flush();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   private void displayMessage(String value) {
      try {
         outWriter.write(value.getBytes());
         outWriter.flush();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   private Map<PromptEnums, String> getInputs() {
      Map<PromptEnums, String> inputMap = new HashMap<PromptEnums, String>();
      for (PromptEnums promptEnum : PromptEnums.values()) {
         String input = waitForUserResponse(promptEnum);
         inputMap.put(promptEnum, ((input != null && input.length() > 0) ? input : ""));
      }
      return inputMap;
   }

   private AuthenticationStatus authenticate(Map<PromptEnums, String> userInputs) {
      String user = userInputs.get(PromptEnums.USERNAME);
      String password = userInputs.get(PromptEnums.PASSWORD);
      String domain = userInputs.get(PromptEnums.DOMAIN);
      oseeAuthentication.authenticate(user, password, domain, true);
      return oseeAuthentication.getAuthenticationStatus();
   }

   public static void main(String[] args) {
      AuthenticationConsole authenticationConsole = new AuthenticationConsole();
      authenticationConsole.run();
      System.exit(0);
   }
}

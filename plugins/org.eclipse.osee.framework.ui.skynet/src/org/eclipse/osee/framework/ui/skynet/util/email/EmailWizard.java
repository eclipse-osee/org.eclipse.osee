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
package org.eclipse.osee.framework.ui.skynet.util.email;

import java.util.ArrayList;
import java.util.Arrays;
import javax.mail.Message;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.util.OseeEmail.BodyType;

/**
 * @author Donald G. Dunne
 */
public class EmailWizard extends Wizard {
   private EmailWizardPage wizardPage;
   private String htmlMessage = null;
   private String subject = null;
   private ArrayList<EmailGroup> emailableGroups;
   private ArrayList<Object> initialAddress = null;

   public EmailWizard() {
      super();
   }

   /**
    * @param htmlMessage
    * @param subject
    * @param emailableFields
    * @param initialAddress - User, AtsEmailGroup or String
    */
   public EmailWizard(String htmlMessage, String subject, ArrayList<EmailGroup> emailableFields, ArrayList<Object> initialAddress) {
      this.htmlMessage = htmlMessage;
      this.subject = subject;
      this.emailableGroups = emailableFields;
      this.initialAddress = initialAddress;
   }

   @Override
   public void addPages() {
      wizardPage = new EmailWizardPage("Page1", emailableGroups, initialAddress);
      addPage(wizardPage);
   }

   @Override
   public boolean performFinish() {
      try {
         if (UserManager.getUser().getEmail().equals("")) {
            AWorkbench.popup(String.format(
                  "Current user [%s] has no email address configured.\n\nEmail can not be sent", UserManager.getUser()));
            return true;
         }
         if (wizardPage.getToAddresses().length == 0) {
            AWorkbench.popup(String.format("Emails can not be resolved for recipients.\n\nEmail not be sent",
                  UserManager.getUser()));
            return true;
         }
         OseeEmail emailMessage =
               new OseeEmail(Arrays.asList(wizardPage.getToAddresses()), UserManager.getUser().getEmail(),
                     UserManager.getUser().getEmail(), subject, "", BodyType.Html);
         emailMessage.setRecipients(Message.RecipientType.CC, wizardPage.getCcAddresses());
         emailMessage.setRecipients(Message.RecipientType.BCC, wizardPage.getBccAddresses());
         String otherText = wizardPage.getText();
         if (!otherText.equals("")) emailMessage.setHTMLBody("<p>" + AHTML.textToHtml(wizardPage.getText()) + "</p><p>--------------------------------------------------------</p>");
         // Remove hyperlinks cause they won't work in email.
         emailMessage.addHTMLBody(htmlMessage);
         emailMessage.send();
      } catch (Exception e) {
         MessageDialog.openInformation(null, "Message Could Not Be Sent",
               "Your Email Message could not be sent.\n\n" + e.getLocalizedMessage());

         // e.printStackTrace();
         return false;
      }

      return true;
   }

   public void setEmailableGroups(ArrayList<EmailGroup> emailableGroups) {
      this.emailableGroups = emailableGroups;
   }

   public void setHtmlMessage(String htmlMessage) {
      this.htmlMessage = htmlMessage;
   }

   public void setSubject(String subject) {
      this.subject = subject;
   }

   public ArrayList<Object> getInitialAddress() {
      return initialAddress;
   }

   public void setInitialAddress(ArrayList<Object> initialAddress) {
      this.initialAddress = initialAddress;
   }

}

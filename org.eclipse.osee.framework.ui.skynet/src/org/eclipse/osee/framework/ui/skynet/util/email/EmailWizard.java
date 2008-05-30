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
import javax.mail.Message;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.ui.skynet.util.AEmail;

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

   public void addPages() {
      wizardPage = new EmailWizardPage("Page1", emailableGroups, initialAddress);
      addPage(wizardPage);
   }

   public boolean performFinish() {
      try {
         AEmail emailMessage =
               new AEmail(null, SkynetAuthentication.getUser().getEmail(), SkynetAuthentication.getUser().getEmail(),
                     subject);
         emailMessage.setRecipients(Message.RecipientType.TO, wizardPage.getToAddresses());
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

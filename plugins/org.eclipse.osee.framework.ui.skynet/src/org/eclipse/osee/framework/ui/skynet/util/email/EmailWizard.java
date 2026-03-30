/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.util.email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.framework.core.util.OseeEmail.BodyType;
import org.eclipse.osee.framework.core.util.SendEmailRequest;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.EmailGroup;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Donald G. Dunne
 */
public class EmailWizard extends Wizard {
   private static final String SUBJECT_WARNING =
      "Email subject lines are NOT encrypted.\n\nDo NOT include Critical Technical Information, Finance, US Export - EAR, LR, US Export - ITAR, LR, Highly Sensitive PII, Regulated PII, US National Security Information, CUI, CDI, or SUI in the Subject.\n\nDo you certify that the Subject does not contain restricted or sensitive data?";

   private EmailWizardPage wizardPage;
   private String htmlMessage = null;
   private String subject = null;
   private List<EmailGroup> emailableGroups;
   private List<Object> initialAddress = null;
   private final String title;

   /**
    * @param initialAddress - User, AtsEmailGroup or String
    */
   public EmailWizard(String htmlMessage, String title, String subject, List<EmailGroup> emailableGroups, List<Object> initialAddress) {
      this.htmlMessage = htmlMessage;
      this.title = title;
      this.subject = subject;
      this.emailableGroups = emailableGroups;
      this.initialAddress = initialAddress;
   }

   @Override
   public void addPages() {
      wizardPage = new EmailWizardPage("Page1", title, subject, emailableGroups, initialAddress);
      addPage(wizardPage);
   }

   @Override
   public boolean performFinish() {
      try {
         if (OseeApiService.user().getEmail().equals("")) {
            AWorkbench.popup(String.format(
               "Current user [%s] has no email address configured.\n\nEmail can not be sent", OseeApiService.user()));
            return true;
         }
         if (wizardPage.getToAddresses().length == 0 && wizardPage.getCcAddresses().length == 0 && wizardPage.getBccAddresses().length == 0) {
            AWorkbench.popup(String.format("Emails can not be resolved for recipients.\n\nEmail not sent"));
            return true;
         }

         boolean confirmed = MessageDialog.openConfirm(getShell(), "Confirm Subject Content", SUBJECT_WARNING);
         if (!confirmed) {
            return false;
         }

         String useSubject = wizardPage.getSubject();
         /**
          * Do not handle abridged emails for EmailWizard. If desired to do this, the UI would need to be updated to
          * allow/enforce a sanitized abridged subject.
          */
         Collection<String> toAbridgedAddresses = Collections.emptyList();
         String abridgedSubject = "";

         String finalHtml = getFinalHtml();

         SendEmailRequest request = new SendEmailRequest();
         request.setToAddresses(Arrays.asList(wizardPage.getToAddresses()));
         request.setCcAddresses(Arrays.asList(wizardPage.getCcAddresses()));
         request.setBccAddresses(Arrays.asList(wizardPage.getBccAddresses()));
         request.setFromAddress(OseeApiService.user().getEmail());
         request.setReplyToAddress(OseeApiService.user().getEmail());
         request.setSubject(useSubject);
         request.setBody(finalHtml);
         request.setBodyType(BodyType.Html);
         request.setEmailAddressesAbridged(toAbridgedAddresses);
         request.setSubjectAbridged(abridgedSubject);
         request.setBodyAbridged(OseeEmail.EMAIL_BODY_REDACTED_FOR_ABRIDGED_EMAIL);

         OseeApiService.serverEnpoints().getOrcsUserEndpoint().sendEmail(request);
      } catch (Exception e) {
         MessageDialog.openInformation(null, "Message Could Not Be Sent",
            "Your Email Message could not be sent.\n\n" + e.getLocalizedMessage());

         return false;
      }

      return true;
   }

   public String getFinalHtml() {
      StringBuilder sb = new StringBuilder();
      String otherText = wizardPage.getText();
      if (Strings.isValid(otherText)) {
         sb.append("<p>");
         sb.append(AHTML.textToHtml(wizardPage.getText()));
         sb.append("</p><p>--------------------------------------------------------</p>");
      }
      sb.append(htmlMessage);
      String finalHtml = sb.toString();
      return finalHtml;
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

   public List<Object> getInitialAddress() {
      return initialAddress;
   }

   public void setInitialAddress(ArrayList<Object> initialAddress) {
      this.initialAddress = initialAddress;
   }

   public List<EmailGroup> getEmailableGroups() {
      return emailableGroups;
   }

}

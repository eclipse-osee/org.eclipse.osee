/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.ats.rest.internal.workitem.journal;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.journal.JournalData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class JournalOperations {

   private final AtsApi atsApi;
   private IAtsWorkItem workItem;
   private final JournalData journalData;
   private final String atsId;
   private final String CONFIG_KEY = "JournalUrl";

   public JournalOperations(JournalData journalData, String atsId, AtsApi atsApi) {
      this.journalData = journalData;
      this.atsId = atsId;
      this.atsApi = atsApi;
   }

   public JournalData validate() {
      workItem = atsApi.getWorkItemService().getWorkItemByAtsId(atsId);
      if (workItem == null) {
         journalData.getResults().errorf("Invalid ATS Id [%s]", atsId);
      }
      return journalData;
   }

   public JournalData addJournal() {
      validate();
      if (journalData.getResults().isErrors()) {
         return journalData;
      }
      try {
         String journalStr =
            atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.Journal, "");
         String timeName =
            String.format("====== %s on %s =================", journalData.getUser().getName(), new Date());

         String msg = journalData.getAddMsg();
         if (Strings.isValid(msg)) {
            msg = msg.replaceFirst("^[ \n]+", "");
            msg = msg.replaceFirst("[ \n]+$", "");
            journalStr = String.format("%s\n%s\n\n%s", timeName, msg, journalStr);
            IAtsChangeSet changes = atsApi.createChangeSet("Post to Journal", journalData.getUser());
            changes.setSoleAttributeValue(workItem, AtsAttributeTypes.Journal, journalStr);

            Collection<ArtifactId> subscribed =
               atsApi.getAttributeResolver().getAttributeValues(workItem, AtsAttributeTypes.JournalSubscriber);
            if (!subscribed.contains(journalData.getUser())) {
               changes.addAttribute(workItem, AtsAttributeTypes.JournalSubscriber,
                  journalData.getUser().getArtifactId());
            }

            TransactionId transaction = changes.execute();
            // Reload latest work item
            workItem = atsApi.getWorkItemService().getWorkItem(workItem.getId());

            journalData.setCurrentMsg(
               atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.Journal, ""));
            atsApi.getWorkItemService().getJournalSubscribed(workItem, journalData);
            journalData.setTransaction(TransactionId.valueOf(transaction.getId()));

            sendNotifications();
         }
      } catch (Exception ex) {
         journalData.getResults().errorf("Exception adding journal %s", Lib.exceptionToString(ex));
      }
      return journalData;
   }

   public JournalData sendNotifications() {
      try {
         Collection<AtsUser> subscribedUsers = atsApi.getNotificationService().getJournalSubscribedUsers(workItem);
         // Don't email user who added journal entry
         subscribedUsers.remove(journalData.getUser());
         if (subscribedUsers.isEmpty()) {
            return journalData;
         }
         String fromEmail = atsApi.getConfigValue("NoReplyEmail");

         if (EmailUtil.isEmailValid(fromEmail)) {
            for (AtsUser user : subscribedUsers) {
               atsApi.getNotificationService().sendNotifications(fromEmail, Collections.singleton(user.getEmail()),
                  getSubject(), getBody(user));
            }
         }
      } catch (Exception ex) {
         journalData.getResults().errorf("Exception sending Journal notification %s", Lib.exceptionToString(ex));
      }
      return journalData;
   }

   private String getBody(AtsUser user) {
      String journalUrl = atsApi.getConfigValue(CONFIG_KEY);
      if (Strings.isValid(journalUrl)) {
         journalUrl = journalUrl.replaceFirst("ATSID", workItem.getAtsId());
         journalUrl = journalUrl.replaceFirst("USERARTID", user.getIdString());
      }
      StringBuilder sb = new StringBuilder();
      sb.append("<html><body><b>Do not respond or forward this email!</b><br/><br/>");
      if (Strings.isValid(journalUrl)) {
         sb.append("<a href=\"");
         sb.append(journalUrl);
         sb.append("\">Select to Respond</a>");
         sb.append("<br/>");
      }
      sb.append("<br/>");
      sb.append("<pre>");
      JournalData jData = atsApi.getWorkItemService().getJournalData(workItem, new JournalData());
      String comments = jData.getResults().isErrors() ? jData.getResults().toString() : jData.getCurrentMsg();
      sb.append(comments);
      sb.append("</pre>");
      sb.append("</body></html>");
      return sb.toString();
   }

   private String getSubject() {
      return String.format("OSEE ATS Journal for \"%s\"", workItem.toStringWithAtsId());
   }

}

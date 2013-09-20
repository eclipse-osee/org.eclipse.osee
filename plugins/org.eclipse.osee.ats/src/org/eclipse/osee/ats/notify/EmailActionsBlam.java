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
package org.eclipse.osee.ats.notify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.notify.EmailActionsData.EmailRecipient;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.EmailUtil;
import org.eclipse.osee.framework.skynet.core.utility.OseeNotificationEvent;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonPush;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class EmailActionsBlam extends AbstractBlam {
   public final static String TEAM_WORKFLOW = "Team Workflows (drop here)";

   @Override
   public String getName() {
      return "Email Message to Action(s) Assignees or Originator";
   }

   private EmailActionsData getEmailActionsData(final VariableMap variableMap) {
      final EmailActionsData data = new EmailActionsData();
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               data.getWorkflows().addAll(variableMap.getArtifacts(TEAM_WORKFLOW));
               String recipientStr = variableMap.getString("Recipient");
               if (Strings.isValid(recipientStr)) {
                  try {
                     EmailRecipient recipient = EmailRecipient.valueOf(recipientStr);
                     data.setEmailRecipient(recipient);
                  } catch (IllegalArgumentException ex) {
                     // do nothing
                  }
               }
               data.setSubject(variableMap.getString("Subject"));
               data.setBody(variableMap.getString("Body"));
            } catch (OseeArgumentException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });
      return data;
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      EmailActionsData data = getEmailActionsData(variableMap);
      Result result = data.isValid();
      if (result.isFalse()) {
         AWorkbench.popup(result);
         return;
      }
      sendEmailNotifications(data);
   }

   private void sendEmailNotifications(EmailActionsData data) throws Exception {

      OseeNotificationManager oseeNotificationManager = OseeNotificationManager.getInstance();
      for (Artifact art : data.getWorkflows()) {
         if (art instanceof AbstractWorkflowArtifact) {
            addNotification(data, (AbstractWorkflowArtifact) art, oseeNotificationManager);
         }
      }
      oseeNotificationManager.sendNotifications(data.getSubject(), data.getBody());
      int sent = oseeNotificationManager.getNotificationEvents().size();
      logf("Sent %s notifications.", sent);
   }

   private void addNotification(EmailActionsData data, final AbstractWorkflowArtifact awa, OseeNotificationManager oseeNotificationManager) throws OseeCoreException {
      Collection<User> recipients = getRecipients(data.getEmailRecipient(), awa);
      Collection<User> activeEmailUsers = EmailUtil.getActiveEmailUsers(recipients);
      if (recipients.isEmpty()) {
         logf("No active " + data.getEmailRecipient() + " for workflow [%s].", awa.toStringWithId());
         return;
      }

      List<String> emailAddresses = new ArrayList<String>();
      for (User basicUser : activeEmailUsers) {
         if (EmailUtil.isEmailValid(basicUser.getEmail())) {
            emailAddresses.add(basicUser.getEmail());
         }
      }

      if (emailAddresses.isEmpty()) {
         logf("No valid emails for workflow [%s].", awa.toStringWithId());
         return;
      }

      if (!EmailUtil.isEmailValid(AtsClientService.get().getUserAdmin().getCurrentOseeUser())) {
         logf("Can't email from user account [%s] cause email not valid.",
            AtsClientService.get().getUserAdmin().getCurrentUser());
         return;
      }

      oseeNotificationManager.addNotificationEvent(new OseeNotificationEvent(recipients, getIdString(awa),
         data.getEmailRecipient().name(), String.format(
            "You are the %s of [%s] in state [%s] titled [%s] created on [%s]", data.getEmailRecipient().name(),
            awa.getArtifactTypeName(), awa.getStateMgr().getCurrentStateName(), awa.getName(),
            DateUtil.get(awa.getCreatedDate(), DateUtil.MMDDYYHHMM))));

   }

   private Collection<User> getRecipients(EmailRecipient emailRecipient, AbstractWorkflowArtifact awa) {
      List<User> recipients = new ArrayList<User>();
      if (emailRecipient == EmailRecipient.Assignees) {
         try {
            recipients.addAll(AtsClientService.get().getUserAdmin().getOseeUsers(awa.getAssignees()));
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      } else if (emailRecipient == EmailRecipient.Originator) {
         try {
            IAtsUser createdBy = awa.getCreatedBy();
            if (createdBy.isActive()) {
               recipients.add(AtsClientService.get().getUserAdmin().getOseeUser(awa.getCreatedBy()));
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      if (recipients.isEmpty()) {
         try {
            recipients.add(AtsClientService.get().getUserAdmin().getCurrentOseeUser());
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return recipients;
   }

   private static String getIdString(AbstractWorkflowArtifact sma) {
      try {
         String legacyPcrId = sma.getSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, "");
         if (!legacyPcrId.equals("")) {
            return "HRID: " + sma.getHumanReadableId() + " / LegacyId: " + legacyPcrId;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return "HRID: " + sma.getHumanReadableId();
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreating(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("Preview Message")) {
         XButtonPush button = (XButtonPush) xWidget;
         button.setDisplayLabel(false);
      }
   }

   @Override
   public String getXWidgetsXml() {
      // @formatter:off
      return "<xWidgets>" +
            "<XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + TEAM_WORKFLOW + "\" />" + 
            "<XWidget xwidgetType=\"XText\" displayName=\"Subject\" />" +
            "<XWidget xwidgetType=\"XCombo("+EmailRecipient.Assignees.toString()+","+EmailRecipient.Originator.toString()+")\" defaultValue=\""+EmailRecipient.Assignees.toString()+"\" displayName=\"Recipient\" />" +
      		"<XWidget xwidgetType=\"XText\" displayName=\"Body\" fill=\"Vertically\" />" +
      		"</xWidgets>";
      // @formatter:on
   }

   @Override
   public String getDescriptionUsage() {
      return "Loop through all dropped Team Workflows and email to assignee(s) with message.  " //
         + "Note: User will get one email containing all items they are assigned/originated.  " //
         + "Note: Body is plain text and will be shown as is.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS/Util");
   }

}
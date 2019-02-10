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
package org.eclipse.osee.ats.ide.notify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.users.AtsUsersUtility;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.notify.EmailActionsData.EmailRecipient;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.EmailUtil;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
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
   public final static String ATS_WORKFLOWS = "ATS Workflows (drop here)";
   boolean includeCancelHyperlink;

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
               data.getWorkflows().addAll(variableMap.getArtifacts(ATS_WORKFLOWS));
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
               includeCancelHyperlink = variableMap.getBoolean("Include Cancel Hyperlink");
               data.setIncludeCancelHyperlink(includeCancelHyperlink);
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
      AtsNotificationCollector notifications = new AtsNotificationCollector();
      for (Artifact art : data.getWorkflows()) {
         if (art instanceof AbstractWorkflowArtifact) {
            addNotification(data, (AbstractWorkflowArtifact) art, notifications);
         }
      }
      int sent = notifications.getNotificationEvents().size();
      notifications.setSubject(data.getSubject());
      notifications.setBody(data.getBody());
      notifications.setIncludeCancelHyperlink(data.isIncludeCancelHyperlink());
      AtsClientService.get().sendNotifications(notifications);
      logf("Sent %s notifications.", sent);
   }

   private void addNotification(EmailActionsData data, final AbstractWorkflowArtifact workItem, AtsNotificationCollector notifications) {
      Collection<IAtsUser> recipients = getRecipients(data.getEmailRecipient(), workItem);
      Collection<IAtsUser> activeEmailUsers = AtsUsersUtility.getActiveEmailUsers(recipients);
      if (recipients.isEmpty()) {
         logf("No active " + data.getEmailRecipient() + " for workflow [%s].", workItem.toStringWithId());
         return;
      }

      List<String> emailAddresses = new ArrayList<>();
      for (IAtsUser basicUser : activeEmailUsers) {
         if (EmailUtil.isEmailValid(basicUser.getEmail())) {
            emailAddresses.add(basicUser.getEmail());
         }
      }

      if (emailAddresses.isEmpty()) {
         logf("No valid emails for workflow [%s].", workItem.toStringWithId());
         return;
      }

      if (!EmailUtil.isEmailValid(AtsClientService.get().getUserServiceClient().getCurrentOseeUser())) {
         logf("Can't email from user account [%s] cause email not valid.",
            AtsClientService.get().getUserService().getCurrentUser());
         return;
      }

      AtsNotificationEvent notificationEvent =
         AtsNotificationEventFactory.getNotificationEvent(AtsClientService.get().getUserService().getCurrentUser(),
            recipients, getIdString(workItem), data.getEmailRecipient().name(),
            String.format("You are the %s of [%s] in state [%s] titled [%s] created on [%s]",
               data.getEmailRecipient().name(), workItem.getArtifactTypeName(),
               workItem.getStateMgr().getCurrentStateName(), workItem.getName(),
               DateUtil.get(workItem.getCreatedDate(), DateUtil.MMDDYYHHMM)));
      notificationEvent.setUrl(
         AtsClientService.get().getWorkItemService().getHtmlUrl(workItem, AtsClientService.get()));
      if (includeCancelHyperlink) {
         if (AtsClientService.get().getWorkItemService().isCancelHyperlinkConfigured()) {
            notificationEvent.setCancelUrl(
               AtsClientService.get().getWorkItemService().getCancelUrl(workItem, AtsClientService.get()));
         } else {
            AWorkbench.popup("CancelHyperlinkUrl not configured.  Can not include cancel link. Aborting...");
            return;
         }
      }
      notifications.addNotificationEvent(notificationEvent);

   }

   private Collection<IAtsUser> getRecipients(EmailRecipient emailRecipient, AbstractWorkflowArtifact awa) {
      List<IAtsUser> recipients = new ArrayList<>();
      if (emailRecipient == EmailRecipient.Assignees) {
         try {
            recipients.addAll(awa.getAssignees());
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      } else if (emailRecipient == EmailRecipient.Originator) {
         try {
            IAtsUser createdBy = awa.getCreatedBy();
            if (createdBy.isActive()) {
               recipients.add(awa.getCreatedBy());
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      if (recipients.isEmpty()) {
         try {
            recipients.add(AtsClientService.get().getUserService().getCurrentUser());
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
            return sma.getAtsId() + " / LegacyId: " + legacyPcrId;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return sma.getAtsId();
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
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
            "<XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + ATS_WORKFLOWS + "\" />" +
            "<XWidget xwidgetType=\"XText\" displayName=\"Subject\" />" +
            "<XWidget xwidgetType=\"XCombo("+EmailRecipient.Assignees.toString()+","+EmailRecipient.Originator.toString()+")\" defaultValue=\""+EmailRecipient.Assignees.toString()+"\" displayName=\"Recipient\" />" +
            "<XWidget xwidgetType=\"XText\" displayName=\"Body\" fill=\"Vertically\" />" +
            "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Cancel Hyperlink\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      		"</xWidgets>";
      // @formatter:on
   }

   @Override
   public String getDescriptionUsage() {
      return "Loop through all dropped ATS Workflows and email to assignee(s) with message.  " //
         + "Note: User will get one email containing all items they are assigned/originated.  " //
         + "Note: Body is plain text and will be shown as is.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS.Util");
   }

   @Override
   public String getTarget() {
      return TARGET_ALL;
   }

}
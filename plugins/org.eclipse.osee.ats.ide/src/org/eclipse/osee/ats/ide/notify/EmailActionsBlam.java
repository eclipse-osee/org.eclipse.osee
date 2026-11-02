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

package org.eclipse.osee.ats.ide.notify;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.LegacyPcrId;
import static org.eclipse.osee.ats.ide.notify.EmailActionsData.EmailRecipient.Assignees;
import static org.eclipse.osee.ats.ide.notify.EmailActionsData.EmailRecipient.Originator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.users.AtsUsersUtility;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.notify.EmailActionsData.EmailRecipient;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.EmailUtil;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXCheckBoxWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class EmailActionsBlam extends AbstractAtsBlam {

   private static final String BLAM_NAME = "Email Action(s) to Assignees or Originator";
   protected static final String I_CERTIFY_NO_RESTRICTED_OR_SENSITIVE_DATA =
      "I certify the subject lines do not contain restricted or sensitive data.";
   protected static final String INCLUDE_CANCEL_HYPERLINK = "Include Cancel Hyperlink";
   protected static final String BODY = "Body";
   protected static final String RECIPIENT = "Recipient";
   protected static final String SUBJECT = "Subject";
   public final static String ATS_WORKFLOWS = "ATS Workflows (drop here)";

   boolean includeCancelHyperlink;
   private XXCheckBoxWidget certifyNoSensitiveDataCheckbox;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public EmailActionsBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andCheckBoxWidget(I_CERTIFY_NO_RESTRICTED_OR_SENSITIVE_DATA).andDefault(false).andRequired();
      wb.andListDropWidget(ATS_WORKFLOWS);
      wb.andTextWidget(SUBJECT);
      wb.andLabelWidget("      - WARNING: Email subject lines are NOT encrypted.");
      wb.andLabelWidget("      - Do NOT include " + //
         EmailUtil.SUBJECT_LINE_PROHIBITED_CLASSIFICATIONS + //
         " in the subject line.");
      wb.andStringsSelWidget(RECIPIENT).andSelectable(Assignees.name(), Originator.name()).andDefault(Assignees.name());
      wb.andTextWidget(BODY).fillVert();
      wb.andCheckBoxWidget(INCLUDE_CANCEL_HYPERLINK);
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      EmailActionsData data = getEmailActionsData(variableMap);
      Result result = data.isValid();
      if (result.isFalse()) {
         if (AtsApiService.get().getStoreService().isProductionDb()) {
            AWorkbench.popup(result);
         } else {
            throw new OseeArgumentException("Error running operation %s", result.toString());
         }
         return;
      }
      if (certifyNoSensitiveDataCheckbox == null || !certifyNoSensitiveDataCheckbox.isChecked()) {
         AWorkbench.popup(
            "You must certify that email subject lines do not contain " + EmailUtil.SUBJECT_LINE_PROHIBITED_CLASSIFICATIONS + " before sending.");
         return;
      }
      if (AtsApiService.get().getStoreService().isProductionDb()) {
         sendEmailNotifications(data);
      }
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   private EmailActionsData getEmailActionsData(final VariableMap variableMap) {
      final EmailActionsData data = new EmailActionsData();
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               List<Artifact> artifacts = variableMap.getArtifacts(ATS_WORKFLOWS);
               data.getWorkflows().addAll(artifacts);
               String recipientStr = variableMap.getString(RECIPIENT);
               if (Strings.isValid(recipientStr)) {
                  try {
                     EmailRecipient recipient = EmailRecipient.valueOf(recipientStr);
                     data.setEmailRecipient(recipient);
                  } catch (IllegalArgumentException ex) {
                     // do nothing
                  }
               }
               data.setSubject(variableMap.getString(SUBJECT));
               data.setBody(variableMap.getString(BODY));
               includeCancelHyperlink = variableMap.getBoolean(INCLUDE_CANCEL_HYPERLINK);
               data.setIncludeCancelHyperlink(includeCancelHyperlink);
            } catch (OseeArgumentException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });
      return data;
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
      XResultData rd = AtsApiService.get().getNotificationService().sendNotifications(notifications, new XResultData());
      if (rd.isErrors()) {
         XResultDataUI.report(rd, getName());
      } else {
         logf("Sent %s notifications.", sent);
      }
   }

   private void addNotification(EmailActionsData data, final AbstractWorkflowArtifact workItem,
      AtsNotificationCollector notifications) {
      Collection<AtsUser> recipients = getRecipients(data.getEmailRecipient(), workItem);
      Collection<AtsUser> activeEmailUsers = AtsUsersUtility.getActiveEmailUsers(recipients);
      if (activeEmailUsers.isEmpty()) {
         logf("No active " + data.getEmailRecipient() + " for workflow [%s].", workItem.toStringWithId());
         return;
      }

      List<String> emailAddresses = new ArrayList<>();
      for (AtsUser basicUser : activeEmailUsers) {
         if (EmailUtil.isEmailValid(basicUser.getEmail())) {
            emailAddresses.add(basicUser.getEmail());
         }
      }

      if (emailAddresses.isEmpty()) {
         logf("No valid emails for workflow [%s].", workItem.toStringWithId());
         return;
      }

      if (!EmailUtil.isEmailValid(AtsApiService.get().getUserService().getCurrentUser().getEmail())) {
         logf("Can't email from user account [%s] cause email not valid.",
            AtsApiService.get().getUserService().getCurrentUser());
         return;
      }

      String msgAbridged = String.format("You are the %s of [%s] in state [%s] - [%s] created on [%s]",
         data.getEmailRecipient().name(), workItem.getArtifactTypeName(), workItem.getCurrentStateName(),
         workItem.getAtsId(), DateUtil.get(workItem.getCreatedDate(), DateUtil.MMDDYYHHMM));
      String msg = String.format("%s titled [%s]", msgAbridged, workItem.getName());

      AtsNotificationEvent notificationEvent =
         AtsNotificationEventFactory.getNotificationEvent(AtsApiService.get().getUserService().getCurrentUser(),
            recipients, getIdString(workItem), data.getEmailRecipient().name(), msg, msgAbridged);

      notificationEvent.setUrl(AtsApiService.get().getWorkItemService().getHtmlUrl(workItem, AtsApiService.get()));
      if (includeCancelHyperlink) {
         if (AtsApiService.get().getWorkItemService().isCancelHyperlinkConfigured()) {
            notificationEvent.setCancelUrl(
               AtsApiService.get().getWorkItemService().getCancelUrl(workItem, AtsApiService.get()));
         } else {
            AWorkbench.popup("CancelHyperlinkUrl not configured.  Can not include cancel link. Aborting...");
            return;
         }
      }
      notifications.addNotificationEvent(notificationEvent);

   }

   private Collection<AtsUser> getRecipients(EmailRecipient emailRecipient, AbstractWorkflowArtifact awa) {
      List<AtsUser> recipients = new ArrayList<>();
      if (emailRecipient == EmailRecipient.Assignees) {
         try {
            recipients.addAll(awa.getAssignees());
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      } else if (emailRecipient == EmailRecipient.Originator) {
         try {
            AtsUser createdBy = awa.getCreatedBy();
            if (createdBy.isActive()) {
               recipients.add(awa.getCreatedBy());
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      if (recipients.isEmpty()) {
         try {
            recipients.add(AtsApiService.get().getUserService().getCurrentUser());
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return recipients;
   }

   private static String getIdString(AbstractWorkflowArtifact sma) {
      try {
         String legacyPcrId = sma.getSoleAttributeValue(LegacyPcrId, "");
         if (!legacyPcrId.equals("")) {
            return sma.getAtsId() + " / LegacyId: " + legacyPcrId;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return sma.getAtsId();
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener modListener, boolean isEditable) {
      super.widgetCreating(xWidget, toolkit, art, xWidgetSwtRenderer, modListener, isEditable);
      if (xWidget.getLabel().equals(I_CERTIFY_NO_RESTRICTED_OR_SENSITIVE_DATA)) {
         certifyNoSensitiveDataCheckbox = (XXCheckBoxWidget) xWidget;
      }
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_UTIL);
   }

   @Override
   public String getDescriptionUsage() {
      return "Loop through all dropped ATS Workflows and email to assignee(s) with message.  " //
         + "Note: User will get one email containing all items they are assigned/originated.  " //
         + "Note: Body is plain text and will be shown as is.";
   }

}

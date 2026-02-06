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

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.SubscriptionGroup;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.framework.core.util.OseeEmail.BodyType;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.EmailUtil;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmailIde;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactList;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonPush;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.HtmlDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ryan D. Brooks
 */
public class EmailGroupsBlam extends AbstractBlam {
   private XArtifactList templateList, groupsList;
   private XText bodyTextBox;
   private XText subjectTextBox, abridgedSubjectTextBox, replyToAddressTextBox;
   private XCheckBox isBodyHtmlCheckbox;
   private ExecutorService emailTheadPool;
   private final Collection<Future<String>> futures = new ArrayList<>(300);
   private final XModifiedListener listener = new ModificationListerner();

   @Override
   public String getName() {
      return "Email Groups";
   }

   private EmailGroupsData getEmailGroupsData() {
      final EmailGroupsData data = new EmailGroupsData();
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            data.setSubject(subjectTextBox.get());
            data.setSubjectAbridged(abridgedSubjectTextBox.get());
            data.setReplyToAddress(replyToAddressTextBox.get());
            data.setFromAddress(OseeApiService.user().getEmail());
            data.setBody(bodyTextBox.get());
            data.setBodyIsHtml(isBodyHtmlCheckbox.isChecked());
            Collection<Artifact> groups = groupsList.getSelectedArtifacts();
            data.getGroups().addAll(groups);
         }
      });
      return data;
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      EmailGroupsData data = getEmailGroupsData();
      Result result = data.isValid();
      if (result.isFalse()) {
         AWorkbench.popup(result);
         return;
      }
      /**
       * Reload group artifact to get updates to users that have un-subscribe since un-subscribe is done via the server
       * and changes tables directly, clients won't know. This can be removed when server uses artifact framework to
       * un-subscribe users and send appropriate events.
       */
      ArtifactQuery.reloadArtifacts(data.getGroups());
      sendEmailViaThreadPool(data);
   }

   private void sendEmailViaThreadPool(EmailGroupsData data) throws Exception {
      emailTheadPool = Executors.newFixedThreadPool(30);
      futures.clear();

      TreeSet<Artifact> users = new TreeSet<>(data.getUserToGroupMap().keySet());
      for (Artifact user : users) {
         sendEmailTo(data, user);
      }
      emailTheadPool.shutdown();
      emailTheadPool.awaitTermination(100, TimeUnit.MINUTES);
   }

   private void sendEmailTo(EmailGroupsData data, final Artifact user) {
      if (user.isOfType(CoreArtifactTypes.User)) {
         String emailAddress = user.getSoleAttributeValue(CoreAttributeTypes.Email, "");
         if (EmailUtil.isEmailValid(emailAddress)) {
            final OseeEmail emailMessage = OseeEmailIde.create(Arrays.asList(emailAddress), data.getFromAddress(),
               data.getReplyToAddress(), data.getSubject(), data.getHtmlResult(user), BodyType.Html, null, null, null);

            String logDescription = String.format("%s - [%s]", user, emailAddress);
            logf(logDescription);

            futures.add(emailTheadPool.submit(new SendEmailCall(emailMessage, logDescription)));
         } else {
            logf("ERROR: The email address \"%s\" for user %s is not valid.", emailAddress, user.getName());
         }

         // Handle abridged if necessary; Only send it abridged subject (default empty) and abridged email
         if (Strings.isValid(data.getSubjectAbridged())) {
            String abridgedEmail = user.getSoleAttributeValue(CoreAttributeTypes.AbridgedEmail, null);
            if (EmailUtil.isEmailValid(abridgedEmail)) {
               final OseeEmail abridgedEmailMessage = OseeEmailIde.create(Arrays.asList(abridgedEmail),
                  data.getFromAddress(), data.getReplyToAddress(), data.getSubjectAbridged(),
                  "Abridged - See Primary Email for Details", BodyType.Html, null, null, null);

               String logDescriptionAbridged = String.format("%s - [%s] (Abridged)", user, abridgedEmail);
               logf(logDescriptionAbridged);

               futures.add(emailTheadPool.submit(new SendEmailCall(abridgedEmailMessage, logDescriptionAbridged)));
            }
         }
      }
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art,
      SwtXWidgetRenderer swtXWidgetRenderer , XModifiedListener modListener, boolean isEditable) {
      super.widgetCreating(xWidget, toolkit, art, swtXWidgetRenderer, modListener, isEditable);
      if (xWidget.getLabel().equals("Groups")) {
         groupsList = (XArtifactList) xWidget;
         XArtifactList listViewer = (XArtifactList) xWidget;

         List<Artifact> groups = new ArrayList<>();
         for (Artifact group : ArtifactQuery.getArtifactListFromType(SubscriptionGroup, COMMON)) {
            if (!group.getTags().contains("Archive")) {
               groups.add(group);
            }
         }
         Collections.sort(groups);
         listViewer.setInputArtifacts(groups);
         listViewer.addXModifiedListener(listener);
      } else if (xWidget.getLabel().equals("Template")) {
         templateList = (XArtifactList) xWidget;
         templateList.addXModifiedListener(listener);
      } else if (xWidget.getLabel().equals("Body")) {
         bodyTextBox = (XText) xWidget;
      } else if (xWidget.getLabel().equals("Body is HTML")) {
         isBodyHtmlCheckbox = (XCheckBox) xWidget;
      } else if (xWidget.getLabel().equals("Subject")) {
         subjectTextBox = (XText) xWidget;
      } else if (xWidget.getLabel().equals("Abridged Subject")) {
         abridgedSubjectTextBox = (XText) xWidget;
      } else if (xWidget.getLabel().equals("Reply-To Address")) {
         replyToAddressTextBox = (XText) xWidget;
         replyToAddressTextBox.set(OseeApiService.user().getEmail());
      } else if (xWidget.getLabel().equals("Preview Message")) {
         XButtonPush button = (XButtonPush) xWidget;
         button.setDisplayLabel(false);
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art,
      SwtXWidgetRenderer swtXWidgetRenderer , XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, swtXWidgetRenderer, modListener, isEditable);
      if (xWidget.getLabel().equals("Preview Message")) {
         XButtonPush button = (XButtonPush) xWidget;
         button.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               handlePreviewMessage();
            }
         });
      }
      if (xWidget.getLabel().equals("Body")) {
         XText xText = (XText) xWidget;
         GridData data1 = new GridData(SWT.FILL, SWT.FILL, true, true);
         data1.heightHint = 300;
         data1.widthHint = 300;
         xText.getStyledText().setLayoutData(data1);
      }
   }

   private void handlePreviewMessage() {
      try {
         EmailGroupsData data = getEmailGroupsData();
         Result result = data.isValid();
         if (result.isFalse()) {
            AWorkbench.popup(result);
            return;
         }
         String htmlResult = data.getHtmlResult(OseeApiService.userArt());
         HtmlDialog dialog = new HtmlDialog("Email Groups - Preview",
            String.format("Subject: %s\n\nSending message to [%d] users from groups [%s]", data.getSubject(),
               data.getUserToGroupMap().keySet().size(),
               org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", data.getGroups())),
            htmlResult);
         dialog.open();
      } catch (OseeCoreException ex) {
         log(ex);
      }
   }

   @Override
   public List<XWidgetData> getXWidgetItems() {
      XWidgetBuilder wb = new XWidgetBuilder();
      wb.andWidget("Groups", "XArtifactList").andMultiSelect().endWidget();
      wb.andWidget("Template", "XArtifactList").endWidget();
      wb.andXText("Reply-To Address").endWidget();
      wb.andXText("Subject").endWidget();
      wb.andXText("Abridged Subject").endWidget();
      // @formatter:off
      wb.andXLabel("      - If an Abridged Subject is included, emails will be sent to users with potential external email.").endWidget();
      wb.andXLabel("      - Abridged Subject MUST be sanitized for general consumption and should give useful information.").endWidget();
      wb.andXLabel("      - NOTE: Email Body will NOT be included in these emails, just the Abridged Subject.").endWidget();
      wb.andXLabel("      - If no entry is given, NO abridged emails will be sent to users with Abridged Email set.").endWidget();
      // @formatter:on
      wb.andXCheckbox("Body is HTML").andDefault(true).andHorizLabel().andLabelAfter().endWidget();
      wb.andXText("Body").andFillVertically().endWidget();
      wb.andXButtonPush("Preview Message").endWidget();
      return wb.getXWidgetDatas();
   }

   @Override
   public String getDescriptionUsage() {
      return "Send individual emails to everyone in the selected groups with an unsubscribe option";
   }

   private class ModificationListerner implements XModifiedListener {

      @Override
      public void widgetModified(XWidget xWidget) {
         try {
            if (xWidget == templateList) {
               Artifact template = (Artifact) templateList.getSelected().iterator().next();
               subjectTextBox.set(template.getName());
               String body = template.getSoleAttributeValue(CoreAttributeTypes.GeneralStringData);
               bodyTextBox.set(body);
            } else {
               XArtifactList groupList = (XArtifactList) xWidget;
               Collection<Artifact> templates = new ArrayList<>();
               for (Object group : groupList.getSelected()) {
                  templates.addAll(((Artifact) group).getChildren());
               }
               templateList.setInputArtifacts(templates);
            }
         } catch (OseeCoreException ex) {
            log(ex);
         }
      }
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.EMAIL_NOTIFICATIONS);
   }

}
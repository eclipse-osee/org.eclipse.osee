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
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.skynet.core.utility.EmailUtil;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmail.BodyType;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactList;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonPush;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
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
   private XText subjectTextBox, replyToAddressTextBox;
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
            data.setReplyToAddress(replyToAddressTextBox.get());
            data.setFromAddress(UserManager.getUser().getEmail());
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
         sendEmailTo(data, (User) user);
      }
      emailTheadPool.shutdown();
      emailTheadPool.awaitTermination(100, TimeUnit.MINUTES);
      for (Future<String> future : futures) {
         logf(future.get());
      }

   }

   private void sendEmailTo(EmailGroupsData data, final User user) {
      final String emailAddress = user.getSoleAttributeValue(CoreAttributeTypes.Email, "");
      if (!EmailUtil.isEmailValid(emailAddress)) {
         logf("The email address \"%s\" for user %s is not valid.", emailAddress, user.getName());
         return;
      }
      final OseeEmail emailMessage = new OseeEmail(Arrays.asList(emailAddress), data.getFromAddress(),
         data.getReplyToAddress(), data.getSubject(), "", BodyType.Html);
      emailMessage.addHTMLBody(data.getHtmlResult(user));
      String description = String.format("[%s] for [%s]", emailAddress, user);
      futures.add(emailTheadPool.submit(new SendEmailCall(emailMessage, description)));
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      super.widgetCreating(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("Groups")) {
         groupsList = (XArtifactList) xWidget;
         XArtifactList listViewer = (XArtifactList) xWidget;

         List<Artifact> groups = ArtifactQuery.getArtifactListFromType(SubscriptionGroup, COMMON);
         Collections.sort(groups);
         listViewer.setInputArtifacts(groups);
         listViewer.addXModifiedListener(listener);
      } else if (xWidget.getLabel().equals("Template")) {
         templateList = (XArtifactList) xWidget;
         templateList.addXModifiedListener(listener);
      } else if (xWidget.getLabel().equals("Body")) {
         bodyTextBox = (XText) xWidget;
      } else if (xWidget.getLabel().equals("Body is html")) {
         isBodyHtmlCheckbox = (XCheckBox) xWidget;
      } else if (xWidget.getLabel().equals("Subject")) {
         subjectTextBox = (XText) xWidget;
      } else if (xWidget.getLabel().equals("Reply-To Address")) {
         replyToAddressTextBox = (XText) xWidget;
         replyToAddressTextBox.set(UserManager.getUser().getEmail());
      } else if (xWidget.getLabel().equals("Preview Message")) {
         XButtonPush button = (XButtonPush) xWidget;
         button.setDisplayLabel(false);
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
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
         HtmlDialog dialog = new HtmlDialog("Email Groups - Preview",
            String.format("Subject: %s\n\nSending message to [%d] users from groups [%s]", data.getSubject(),
               data.getUserToGroupMap().keySet().size(), Artifacts.commaArts(data.getGroups())),
            data.getHtmlResult(UserManager.getUser()));
         dialog.open();
      } catch (OseeCoreException ex) {
         log(ex);
      }
   }

   @Override
   public String getXWidgetsXml() {
      // @formatter:off
      return "<xWidgets>" +
      "<XWidget xwidgetType=\"XArtifactList\" displayName=\"Groups\" multiSelect=\"true\" />" +
      "<XWidget xwidgetType=\"XArtifactList\" displayName=\"Template\" />" +
      "<XWidget xwidgetType=\"XText\" displayName=\"Reply-To Address\" />" +
      "<XWidget xwidgetType=\"XText\" displayName=\"Subject\" />" +
      "<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Body is html\" defaultValue=\"true\" />" +
      "<XWidget xwidgetType=\"XText\" displayName=\"Body\" fill=\"Vertically\" />" +
      "<XWidget xwidgetType=\"XButtonPush\" displayName=\"Preview Message\" />" +
      "</xWidgets>";
      // @formatter:on
   }

   @Override
   public String getDescriptionUsage() {
      return "Send individual emails to everyone in the selected groups with an unsubscribe option";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
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
   public String getTarget() {
      return TARGET_ALL;
   }

}
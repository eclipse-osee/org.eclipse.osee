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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.util.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.util.OseeEmail.BodyType;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactList;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonPush;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.HtmlDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ryan D. Brooks
 */
public class EmailGroupsBlam extends AbstractBlam implements XModifiedListener {
   private XArtifactList templateList, groupsList;
   private XText bodyTextBox;
   private XText subjectTextBox;
   private XCheckBox isBodyHtmlCheckbox;
   private ExecutorService emailTheadPool;
   private final Collection<Future<String>> futures = new ArrayList<Future<String>>(300);

   @Override
   public String getName() {
      return "Email Groups";
   }

   private EmailGroupsData getEmailGroupsData() {
      EmailGroupsData data = new EmailGroupsData();
      data.setSubject(subjectTextBox.get());
      data.setBody(bodyTextBox.get());
      data.setBodyIsHtml(isBodyHtmlCheckbox.get());
      Collection<Artifact> groups = groupsList.getSelectedArtifacts();
      data.getGroups().addAll(groups);
      return data;
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      EmailGroupsData data = getEmailGroupsData();
      Result result = data.isValid();
      if (result.isFalse()) {
         result.popup();
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

      TreeSet<Artifact> users = new TreeSet<Artifact>(data.getUserToGroupMap().keySet());
      for (Artifact user : users) {
         sendEmailTo(data, (User) user);
      }
      emailTheadPool.shutdown();
      emailTheadPool.awaitTermination(100, TimeUnit.MINUTES);
      for (Future<String> future : futures) {
         println(future.get());
      }

   }

   private void sendEmailTo(EmailGroupsData data, final User user) throws OseeCoreException {
      final String emailAddress = user.getSoleAttributeValue(CoreAttributeTypes.Email, "");
      if (!EmailUtil.isEmailValid(emailAddress)) {
         println(String.format("The email address \"%s\" for user %s is not valid.", emailAddress, user.getName()));
         return;
      }
      final OseeEmail emailMessage = new OseeEmail(emailAddress, data.getSubject(), "", BodyType.Html);
      emailMessage.addHTMLBody(data.getHtmlResult(user));
      futures.add(emailTheadPool.submit(new SendEmailCall(user, emailMessage, emailAddress)));
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreating(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("Groups")) {
         groupsList = (XArtifactList) xWidget;
         XArtifactList listViewer = (XArtifactList) xWidget;
         List<Artifact> groups =
            ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.UserGroup, BranchManager.getCommonBranch());
         Collections.sort(groups);
         listViewer.setInputArtifacts(groups);
         listViewer.addXModifiedListener(this);
      } else if (xWidget.getLabel().equals("Template")) {
         templateList = (XArtifactList) xWidget;
         templateList.addXModifiedListener(this);
      } else if (xWidget.getLabel().equals("Body")) {
         bodyTextBox = (XText) xWidget;
      } else if (xWidget.getLabel().equals("Body is html")) {
         isBodyHtmlCheckbox = (XCheckBox) xWidget;
      } else if (xWidget.getLabel().equals("Subject")) {
         subjectTextBox = (XText) xWidget;
      } else if (xWidget.getLabel().equals("Preview Message")) {
         XButtonPush button = (XButtonPush) xWidget;
         button.setDisplayLabel(false);
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
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
   }

   private void handlePreviewMessage() {
      try {
         EmailGroupsData data = getEmailGroupsData();
         Result result = data.isValid();
         if (result.isFalse()) {
            result.popup();
            return;
         }
         HtmlDialog dialog =
            new HtmlDialog("Email Groups - Preview", String.format(
               "Subject: %s\n\nSending message to [%d] users from groups [%s]", data.getSubject(),
               data.getUserToGroupMap().keySet().size(), Artifacts.commaArts(data.getGroups())),
               data.getHtmlResult(UserManager.getUser()));
         dialog.open();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public String getXWidgetsXml() {
      // @formatter:off
      return "<xWidgets>" +
      		"<XWidget xwidgetType=\"XArtifactList\" displayName=\"Groups\" multiSelect=\"true\" />" +
      		"<XWidget xwidgetType=\"XArtifactList\" displayName=\"Template\" />" +
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
            Collection<Artifact> templates = new ArrayList<Artifact>();
            for (Object group : groupList.getSelected()) {
               templates.addAll(((Artifact) group).getChildren());
            }
            templateList.setInputArtifacts(templates);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

}
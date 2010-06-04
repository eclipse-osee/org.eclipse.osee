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
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.util.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.util.OseeEmail.BodyType;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactList;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ryan D. Brooks
 */
public class EmailGroupsBlam extends AbstractBlam implements XModifiedListener {
   private XArtifactList templateList;
   private XText bodyTextBox;
   private XText subjectTextBox;
   private ExecutorService emailTheadPool;
   private final Collection<Future<String>> futures = new ArrayList<Future<String>>(300);

   @Override
   public String getName() {
      return "Email Groups";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      String subject = variableMap.getString("Subject");
      String body = variableMap.getString("Body");
      boolean bodyIsHtml = variableMap.getBoolean("Body is html");
      Collection<Artifact> groups = variableMap.getCollection(Artifact.class, "Groups");
      emailTheadPool = Executors.newFixedThreadPool(30);
      futures.clear();

      HashCollection<Artifact, Artifact> userToGroupMap = new HashCollection<Artifact, Artifact>();

      for (Artifact group : groups) {
         for (Artifact user : group.getRelatedArtifacts(CoreRelationTypes.Users_User)) {
            if (user.getSoleAttributeValue(CoreAttributeTypes.Active)) {
               userToGroupMap.put(user, group);
            } else {
               println(String.format("The user %s is not active but is in group %s.", user.getName(), group.getName()));
            }
         }
      }

      TreeSet<Artifact> users = new TreeSet<Artifact>(userToGroupMap.keySet());
      for (Artifact user : users) {
         sendEmailTo(userToGroupMap.getValues(user), user, subject, body, bodyIsHtml);
      }
      emailTheadPool.shutdown();
      emailTheadPool.awaitTermination(100, TimeUnit.MINUTES);
      for (Future<String> future : futures) {
         println(future.get());
      }
   }

   private void sendEmailTo(Collection<Artifact> groups, final Artifact user, String subject, String body, boolean bodyIsHtml) throws OseeCoreException {
      final String emailAddress = user.getSoleAttributeValue(CoreAttributeTypes.EMAIL, "");
      if (!EmailUtil.isEmailValid(emailAddress)) {
         println(String.format("The email address \"%s\" for user %s is not valid.", emailAddress, user.getName()));
         return;
      }
      final OseeEmail emailMessage = new OseeEmail(emailAddress, subject, "", BodyType.Html);

      StringBuilder html = new StringBuilder();

      if (bodyIsHtml) {
         html.append(body);
      } else {
         html.append("<pre>");
         html.append(body);
         html.append("</pre>");
      }

      for (Artifact group : groups) {
         html.append(String.format(
               "</br>Click <a href=\"%sosee/unsubscribe/group/%d/user/%d\">unsubscribe</a> to stop receiving all emails for the topic \"%s\"",
               HttpUrlBuilderClient.getInstance().getApplicationServerPrefix(), group.getArtId(), user.getArtId(),
               group.getName()));
      }
      emailMessage.addHTMLBody(html.toString());

      futures.add(emailTheadPool.submit(new SendEmailCall(user, emailMessage, emailAddress)));
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreating(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("Groups")) {
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
      } else if (xWidget.getLabel().equals("Subject")) {
         subjectTextBox = (XText) xWidget;
      }
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XArtifactList\" displayName=\"Groups\" multiSelect=\"true\" /><XWidget xwidgetType=\"XArtifactList\" displayName=\"Template\" /><XWidget xwidgetType=\"XText\" displayName=\"Subject\" /><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Body is html\" defaultValue=\"true\" /><XWidget xwidgetType=\"XText\" displayName=\"Body\" fill=\"Vertically\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Send individual emails to everyone in the selected groups with an unsubscribe option";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }

   public void widgetModified(XWidget xWidget) {
      try {
         if (xWidget == templateList) {
            Artifact template = (Artifact) templateList.getSelected().iterator().next();
            subjectTextBox.set(template.getName());
            String body = template.getSoleAttributeValue(CoreAttributeTypes.GENERAL_STRING_DATA);
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
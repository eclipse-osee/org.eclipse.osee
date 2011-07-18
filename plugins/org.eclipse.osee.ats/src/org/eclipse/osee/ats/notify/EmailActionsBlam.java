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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.Overview.PreviewStyle;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.EmailUtil;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.SendEmailCall;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmail.BodyType;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonPush;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class EmailActionsBlam extends AbstractBlam {
   private XText bodyTextBox;
   private XText subjectTextBox;
   private ExecutorService emailTheadPool;
   private final Collection<Future<String>> futures = new ArrayList<Future<String>>(300);
   public final static String TEAM_WORKFLOW = "Team Workflows (drop here)";

   @Override
   public String getName() {
      return "Email Message to Action(s) Assignees";
   }

   private EmailActionsData getEmailActionsData(final VariableMap variableMap) {
      final EmailActionsData data = new EmailActionsData();
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               data.getWorkflows().addAll(variableMap.getArtifacts(TEAM_WORKFLOW));
            } catch (OseeArgumentException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
            data.setSubject(subjectTextBox.get());
            data.setBody(bodyTextBox.get());
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
      sendEmailViaThreadPool(data);
   }

   private void sendEmailViaThreadPool(EmailActionsData data) throws Exception {
      emailTheadPool = Executors.newFixedThreadPool(30);
      futures.clear();

      for (Artifact user : data.getWorkflows()) {
         sendEmailTo(data, (TeamWorkFlowArtifact) user);
      }
      emailTheadPool.shutdown();
      emailTheadPool.awaitTermination(100, TimeUnit.MINUTES);
      for (Future<String> future : futures) {
         report(future.get());
      }

   }

   private void sendEmailTo(EmailActionsData data, final TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Set<IBasicUser> assignees = new HashSet<IBasicUser>();
      assignees.addAll(teamArt.getStateMgr().getAssignees());
      Collection<IBasicUser> activeEmailUsers = EmailUtil.getActiveEmailUsers(assignees);
      if (assignees.isEmpty()) {
         report(String.format("No active assignees for workflow [%s].", teamArt.toStringWithId()));
         return;
      }

      List<String> emailAddresses = new ArrayList<String>();
      for (IBasicUser basicUser : activeEmailUsers) {
         User oseeUser = UserManager.getUser(basicUser);
         if (EmailUtil.isEmailValid(oseeUser.getEmail())) {
            emailAddresses.add(oseeUser.getEmail());
         }
      }

      if (emailAddresses.isEmpty()) {
         report(String.format("No valid emails for workflow [%s].", teamArt.toStringWithId()));
         return;
      }

      if (!EmailUtil.isEmailValid(UserManager.getUser())) {
         report(String.format("Can't email from user account [%s] cause email not valid.", UserManager.getUser()));
         return;
      }

      final OseeEmail emailMessage =
         new OseeEmail(emailAddresses, UserManager.getUser().getEmail(), UserManager.getUser().getEmail(),
            data.getSubject(), "", BodyType.Html);
      emailMessage.setHTMLBody("<p>" + AHTML.textToHtml(data.getBody()) + "</p><p>--------------------------------------------------------</p>");
      emailMessage.addHTMLBody(getHtmlMessage(data, teamArt));
      String description = String.format("%s for %s", teamArt.toStringWithId(), emailAddresses);
      futures.add(emailTheadPool.submit(new SendEmailCall(emailMessage, description)));
   }

   private String getHtmlMessage(EmailActionsData data, TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return AtsNotificationManagerUI.getPreviewHtml(teamArt, PreviewStyle.HYPEROPEN,
         PreviewStyle.NO_SUBSCRIBE_OR_FAVORITE);
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreating(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("Body")) {
         bodyTextBox = (XText) xWidget;
      } else if (xWidget.getLabel().equals("Subject")) {
         subjectTextBox = (XText) xWidget;
      } else if (xWidget.getLabel().equals("Preview Message")) {
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
      		"<XWidget xwidgetType=\"XText\" displayName=\"Body\" fill=\"Vertically\" />" +
      		"</xWidgets>";
      // @formatter:on
   }

   @Override
   public String getDescriptionUsage() {
      return "Loop through all dropped Team Workflows and email to assignee(s) with message.  Note: User will get one email per item they are assigned.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS/Util");
   }

}
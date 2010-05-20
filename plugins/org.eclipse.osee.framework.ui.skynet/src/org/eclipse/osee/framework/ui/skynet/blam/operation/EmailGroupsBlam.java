/*
 * Created on Dec 8, 2007
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;
import javax.mail.MessagingException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.util.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.util.OseeEmail.BodyType;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactList;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ryan D. Brooks
 */
public class EmailGroupsBlam extends AbstractBlam {

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
   }

   private void sendEmailTo(Collection<Artifact> groups, Artifact user, String subject, String body, boolean bodyIsHtml) throws OseeCoreException {
      String emailAddress = user.getSoleAttributeValue(CoreAttributeTypes.EMAIL, "");
      if (!EmailUtil.isEmailValid(emailAddress)) {
         println(String.format("The email address \"%s\" for user %s is not valid.", emailAddress, user.getName()));
         return;
      }
      OseeEmail emailMessage =
            new OseeEmail(emailAddress, subject, "", BodyType.Html);

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
               HttpUrlBuilderClient.getInstance().getApplicationServerPrefix(), group.getArtId(),
               user.getArtId(), group.getName()));
      }
      emailMessage.addHTMLBody(html.toString());
      try {
         emailMessage.sendLocalThread();
      } catch (MessagingException ex) {
         println(String.format("An exception occured with sending the email for address \"%s\" for user %s.  %s",
               emailAddress, user.getName(), ex));
      }
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreating(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("Groups")) {
         XArtifactList listViewer = (XArtifactList) xWidget;
         listViewer.setInputArtifacts(ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.UserGroup,
               BranchManager.getCommonBranch()));
      }
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XArtifactList\" displayName=\"Groups\" multiSelect=\"true\" /><XWidget xwidgetType=\"XText\" displayName=\"Subject\" /><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Body is html\" defaultValue=\"true\" /><XWidget xwidgetType=\"XText\" displayName=\"Body\" fill=\"Vertically\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Send individual emails to everyone in the selected groups with an unsubscribe option";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }
}
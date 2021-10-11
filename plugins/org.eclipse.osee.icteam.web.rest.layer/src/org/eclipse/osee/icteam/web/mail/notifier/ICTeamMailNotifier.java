/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.web.mail.notifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.clientserver.dependent.datamodel.TransferableArtifact;
import org.eclipse.osee.icteam.server.access.core.OseeCoreData;
import org.eclipse.osee.icteam.web.mail.notifier.ICTeamEmail.BodyType;
import org.eclipse.osee.icteam.web.rest.data.write.TranferableArtifactLoader;
import org.eclipse.osee.icteam.web.rest.layer.util.CommonUtil;
import org.eclipse.osee.icteam.web.rest.layer.util.UserUtility;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Mail notifier for task related notifications
 *
 * @author Ajay Chandrahasan
 */
public class ICTeamMailNotifier {
   public final static int TABLE_WIDTH = 95;
   private static ArtifactReadable artifact;
   private static List<ICTeamNotifyType> types;
   private static List<String> assignees;
   private static OrcsApi orcsApi;
   private static String artiafctLink;
   private static String update;
   private static TransferableArtifact parentArtifact;
   public final static String labelFont = "<font color=\"darkcyan\" face=\"Arial\" size=\"-1\">";
   public final static String normalColor = "#EEEEEE";
   private final static String activeColor = "#9CCCFF";

   /*
    * configure from address, sample value is shown below
    */
   private final static String fromAddresss = "ICTeam-Admin@icteam.com";

   /**
    * @param notifyUsers only valid for assignees notifyType. if null or any other type, the users will be computed
    */
   public static void notify(final OrcsApi orcs, final ArtifactReadable awa, final List<String> users, final List<String> _mailIds, final String updates, final String link, final ICTeamNotifyType... notifyTypes) {
      update = updates;
      assignees = new ArrayList<String>(new HashSet<String>(users));
      orcsApi = orcs;
      // project = ProjectModelUtil.getActiveproject();
      artifact = awa;
      artiafctLink = link;

      List<String> mailIds = new ArrayList<String>(new HashSet<String>(_mailIds));

      try {
         types = Arrays.asList(notifyTypes);

         if (types.contains(ICTeamNotifyType.Subscribed)) {
            if (mailIds.size() > 0) {
               ICTeamEmail emailMessage = new ICTeamEmail(mailIds, fromAddresss, fromAddresss,
                  getNotificationEmailSubject(), getBody(), BodyType.Html);
               emailMessage.send();
            }
         }

         if (types.contains(ICTeamNotifyType.Cancelled) || types.contains(ICTeamNotifyType.Completed)) {
            if (mailIds.size() > 0) {
               ICTeamEmail emailMessage = new ICTeamEmail(mailIds, fromAddresss, fromAddresss,
                  getNotificationEmailSubject(), getBody(), BodyType.Html);
               emailMessage.send();
            }
         }

         if (types.contains(ICTeamNotifyType.Updated)) {
            if (mailIds.size() > 0) {
               ICTeamEmail emailMessage = new ICTeamEmail(mailIds, fromAddresss, fromAddresss,
                  getNotificationEmailSubject(), getBody(), BodyType.Html);
               emailMessage.send();
            }
         }

         if (types.contains(ICTeamNotifyType.Created)) {
            if (mailIds.size() > 0) {
               ICTeamEmail emailMessage = new ICTeamEmail(mailIds, fromAddresss, fromAddresss,
                  getNotificationEmailSubject(), getBody(), BodyType.Html);
               emailMessage.send();
            }
         }
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }
   }

   /*
    * Contents in the mail
    */
   private static String getBody() {
      StringBuffer buffer = new StringBuffer();

      try {
         buffer.append(AHTML.newline());
         buffer.append(AHTML.simplePage("---------------------------------------------------------------"));
         buffer.append(AHTML.newline());

         if (types.contains(ICTeamNotifyType.Originator)) {
            buffer.append(AHTML.simplePage("Status : Created"));
         } else if (types.contains(ICTeamNotifyType.Assigned)) {
            buffer.append(AHTML.simplePage("Status : Assigned"));
         } else if (types.contains(ICTeamNotifyType.Subscribed)) {
            buffer.append(AHTML.simplePage("Status : Subscribed"));
         } else if (types.contains(ICTeamNotifyType.Completed) && types.contains(ICTeamNotifyType.predecessor)) {
            buffer.append(AHTML.simplePage("Status : Predecessor Task Completed"));
         } else if (types.contains(ICTeamNotifyType.Cancelled) && types.contains(ICTeamNotifyType.predecessor)) {
            buffer.append(AHTML.simplePage("Status : Predecessor Task Cancelled"));
         } else if (types.contains(ICTeamNotifyType.Cancelled)) {
            buffer.append(AHTML.simplePage("Status : Cancelled"));
         } else if (types.contains(ICTeamNotifyType.Completed)) {
            buffer.append(AHTML.simplePage("Status : Completed"));
         } else if (types.contains(ICTeamNotifyType.Peer_Reviewers_Completed)) {
            buffer.append(AHTML.simplePage("Status : Review Completed"));
         } else if (types.contains(ICTeamNotifyType.Updated) && !types.contains(ICTeamNotifyType.predecessor)) {
            buffer.append(AHTML.simplePage("Status : Updated"));
         } else if (types.contains(ICTeamNotifyType.Created)) {
            buffer.append(AHTML.simplePage("Status : Created"));
         }

         if ((update != null) && !update.isEmpty()) {
            buffer.append(AHTML.newline());

            if (artifact.getArtifactType().getName().equals("Team Workflow")) {
               buffer.append(AHTML.italics(update));
            }

            if (artifact.getArtifactType().getName().equals("Task")) {
               buffer.append(AHTML.italics(update));
            }

            buffer.append(AHTML.newline());
         }

         if (types.contains(ICTeamNotifyType.predecessor)) {
            buffer.append(AHTML.newline());
            buffer.append(AHTML.newline());
            buffer.append(AHTML.newline());
         }

         buffer.append(AHTML.newline());
         buffer.append(AHTML.simplePage("---------------------------------------------------------------"));
         buffer.append(AHTML.newline());

         if (artifact.getArtifactType().getName().equals("Team Workflow")) {
            ArtifactReadable projectArtifact = null;
            ResultSet<ArtifactReadable> relatedProjects =
               artifact.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project);

            if (relatedProjects.size() > 0) {
               for (ArtifactReadable project : relatedProjects) {
                  projectArtifact = project;
               }
            }

            buffer.append(getContentForProject());
         }

         if (artiafctLink != null) {
            buffer.append(AHTML.newline());

            try {
               buffer.append("Link: ");
               buffer.append(AHTML.newline());
               artiafctLink = artiafctLink.replaceAll(" ", "%20");
               buffer.append(AHTML.simplePage(artiafctLink));
            } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }

            buffer.append(AHTML.newline());
         }

         buffer.append(AHTML.simplePage("---------------------------------------------------------------"));

         buffer.append(AHTML.newline());
         buffer.append(AHTML.newline());

         buffer.append(AHTML.italics("This is automatically generated message."));
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return buffer.toString();
   }

   /**
    * This method will have Contents need to be added in mail notification
    *
    * @return
    */
   public static StringBuffer getContentForProject() {
      StringBuffer buffer = new StringBuffer();
      ArtifactReadable projectArtifact =
         artifact.getRelated(AtsRelationTypes.ProjectToTeamWorkFlow_Project).getExactlyOne();
      String shortName = projectArtifact.getAttributes(AtsAttributeTypes.Shortname).getExactlyOne().toString();
      String workPackageId = artifact.getAttributes(AtsAttributeTypes.WorkPackage).getExactlyOne().toString();
      String taskId = shortName + "-" + workPackageId;
      buffer.append(AHTML.addSpace(5) + AHTML.simplePage("Task ID:" + taskId));

      String actionableItemId = artifact.getSoleAttributeAsString(AtsAttributeTypes.ActionableItemReference);
      ArtifactReadable componentArt = getActionalBleItemsByGuid(actionableItemId);
      buffer.append(AHTML.addSpace(5) + AHTML.simplePage("Packages:          " + componentArt.getName()));

      buffer.append(AHTML.addSpace(5) + AHTML.simplePage(
         "CreatedBy:          " + getUserNameByUserId(artifact.getSoleAttributeAsString(AtsAttributeTypes.CreatedBy))));

      if (assignees.isEmpty()) {
         List<ITransferableArtifact> assignees2 =
            CommonUtil.getAssignees(artifact.getSoleAttributeAsString(AtsAttributeTypes.CurrentState));
         List<String> userList = new ArrayList<String>();

         for (ITransferableArtifact transferableArtifact : assignees2) {
            userList.add(transferableArtifact.getAttributes(CoreAttributeTypes.UserId.toString()).get(0));
         }

         assignees.addAll(userList);
      }

      StringBuffer stringBuffer = new StringBuffer();

      for (String assignee : assignees) {
         stringBuffer.append(getUserNameByUserId(assignee));
         stringBuffer.append(", ");
      }

      buffer.append(AHTML.addSpace(5) + AHTML.simplePage("Assignee:          " + stringBuffer.toString()));
      buffer.append(AHTML.addSpace(5) + AHTML.simplePage(
         "Status:          " + artifact.getSoleAttributeAsString(AtsAttributeTypes.CurrentState).substring(0,
            artifact.getSoleAttributeAsString(AtsAttributeTypes.CurrentState).indexOf(";"))));

      buffer.append(AHTML.addSpace(5) + AHTML.simplePage(
         "Request Type:          " + artifact.getSoleAttributeAsString(AtsAttributeTypes.ChangeType)));

      String teamDefId = artifact.getSoleAttributeAsString(AtsAttributeTypes.TeamDefinitionReference);
      ArtifactReadable teamArt = getTeamDefinitionByGuid(teamDefId);
      buffer.append(AHTML.addSpace(5) + AHTML.simplePage("Team:          " + teamArt.getName()));

      buffer.append(AHTML.addSpace(5) + AHTML.simplePage(
         "Description:          " + artifact.getSoleAttributeAsString(AtsAttributeTypes.Description)));

      return buffer;
   }

   /*
    * To get team definition
    */
   private static ArtifactReadable getTeamDefinitionByGuid(final String teamDefId) {
      ResultSet<ArtifactReadable> list = null;

      try {
         list = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.TeamDefinition).andUuid(Long.valueOf(teamDefId)).getResults();

         return list.getExactlyOne();
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /*
    * The method to get actionable item
    */
   private static ArtifactReadable getActionalBleItemsByGuid(final String actionableItemId) {
      ResultSet<ArtifactReadable> list = null;

      try {
         list = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(
            AtsArtifactTypes.ActionableItem).andUuid(Long.valueOf(actionableItemId)).getResults();

         return list.getExactlyOne();
      } catch (OseeCoreException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return null;
   }

   /**
    * @param datas
    * @param headers
    * @param numColumns
    * @param cellPadding
    * @param border
    * @param table
    * @return
    */
   public static String createTable(final List<String> datas, final String[] headers, final int numColumns, final int cellPadding, final int border, final StringBuffer table) {
      if (datas == null) {
         throw new IllegalArgumentException("The data can not be null");
      }

      if ((datas.size() % numColumns) != 0) {
         throw new IllegalArgumentException(
            "The table could not be created becuase the data does not match the column size");
      }

      if (border > 0) {
         table.append("border=\"" + border + "\"");
      }

      if (cellPadding > 0) {
         table.append("cellpadding=\"" + cellPadding + "\"");
      }

      table.append(">");

      if ((headers != null) && (headers.length == numColumns)) {
         table.append("<tr>");

         for (String header : headers) {
            table.append("<th>" + header + "</th>");
         }

         table.append("</tr>");
      }

      int colIndex = 0;

      for (String data : datas) {
         if (colIndex == 0) {
            table.append("<tr>");
         }

         table.append("<td>" + data + "</td>");
         colIndex++;

         if (colIndex == numColumns) {
            table.append("</tr>");
            colIndex = 0;
         }
      }

      return table.toString();
   }

   /**
    * method for label font
    *
    * @param label
    * @return
    */
   public static String getLabel(final String label) {
      return AHTML.getLabelStr(labelFont, label + ": ");
   }

   /**
    * method for table building
    *
    * @param buffer
    */
   public static void endBorderTable(final StringBuffer buffer) {
      buffer.append(AHTML.endBorderTable());
   }

   public static void startBorderTable(final boolean active, final String caption, final StringBuffer buffer) {
      buffer.append(startBorderTable(TABLE_WIDTH, active ? activeColor : normalColor, caption));
   }

   public static String startBorderTable(final int width, final String bgcolor, final String caption) {
      String capStr = "";

      if (!caption.equals("")) {
         capStr = "<caption ALIGN=top>" + caption + "</caption>";
      }

      return "<table border=\"1\" align=\"left\" bgcolor=\"" + bgcolor + "\" cellpadding=\"3\" cellspacing=\"0\" width=\"" + width + "%\">" + capStr;
   }

   public static String endBorderTable() {
      return "</td></tr></table>";
   }

   /**
    * Method for table building
    *
    * @param str
    * @param width
    * @return
    */
   public static String addHeaderRowMultiColumnTable(final String[] str, final Integer[] width) {
      StringBuilder s = new StringBuilder("<tr>");
      String widthStr = "";

      for (int i = 0; i < str.length; i++) {
         if (width != null) {
            widthStr = " width =\"" + width[i] + "\"";
         }

         String s1234 = " align=\"left\"";
         s.append("<th");
         s.append(widthStr);
         s.append(s1234);
         s.append(">");
         s.append(str[i]);
         s.append("</th>");
      }

      s.append("</tr>");

      return s.toString();
   }

   /*
    * Method with subject for mail notification
    */
   private static String getNotificationEmailSubject() {
      String buffer = "";

      if (types.contains(ICTeamNotifyType.Originator)) {
         buffer = "ICTeam Notification : Created - " + artifact.getName();
      } else if (types.contains(ICTeamNotifyType.Assigned)) {
         buffer = "ICTeam Notification : Updated - " + artifact.getName();
      } else if (types.contains(ICTeamNotifyType.Subscribed)) {
         buffer = "ICTeam Notification : Subscribed - " + artifact.getName();
      } else if (types.contains(ICTeamNotifyType.Completed) && types.contains(ICTeamNotifyType.predecessor)) {
         buffer = "ICTeam Notification : Predecessor Task Completed - " + artifact.getName();
      } else if (types.contains(ICTeamNotifyType.Cancelled) && types.contains(ICTeamNotifyType.predecessor)) {
         buffer = "ICTeam Notification : Predecessor Task Cancelled - " + artifact.getName();
      } else if (types.contains(ICTeamNotifyType.Cancelled)) {
         buffer = "ICTeam Notification : Cancelled - " + artifact.getName();
      } else if (types.contains(ICTeamNotifyType.Completed)) {
         buffer = "ICTeam Notification : Completed - " + artifact.getName();
      } else if (types.contains(ICTeamNotifyType.Peer_Reviewers_Completed)) {
         buffer = "ICTeam Notification : Review Completed - " + artifact.getName();
      } else if (types.contains(ICTeamNotifyType.Updated) && !types.contains(ICTeamNotifyType.predecessor)) {
         buffer = "ICTeam Notification : Updated - " + artifact.getName();
      } else if (types.contains(ICTeamNotifyType.Created)) {
         buffer = "ICTeam Notification : Created - " + artifact.getName();
      } else if (types.contains(ICTeamNotifyType.UserCreated)) {
         buffer = "ICTeam Notification : User Created - " + artifact.getName();
      } else if (types.contains(ICTeamNotifyType.AdminUserCreated)) {
         buffer = "ICTeam Notification : Admin User Created - " + artifact.getName();
      } else if (types.contains(ICTeamNotifyType.PasswordChangeRequest)) {
         buffer = "ICTeam Notification : Password Modification Request Created - " + artifact.getName();
      }

      return buffer.toString();
   }

   /*
    * Get User name by Id
    */
   public static String getUserNameByUserId(final String userId) {
      if (!("".equals(userId.trim()))) {
         OrcsApi orcsApi = OseeCoreData.getOrcsApi();
         ArtifactReadable artifactReadable = UserUtility.getUserById(orcsApi, userId);
         TransferableArtifact tr = new TransferableArtifact();
         TranferableArtifactLoader.copyBasicInfoToTransferableArtifact(artifactReadable, tr);

         return tr.getName();
      } else {
         return "";
      }
   }

   /**
    * Method to send notification mail
    *
    * @param orcsApi2
    * @param readableArtifact
    * @param currentLoggedInUser
    * @param userId
    * @param changes
    * @param rapLink
    */
   public static void notify(final OrcsApi orcsApi2, final ArtifactReadable readableArtifact, final String currentLoggedInUser, final String mailId, final String changes, final String rapLink, final ICTeamNotifyType type) {
      types = Arrays.asList(type);
      assignees = new ArrayList<String>();
      assignees.add(mailId);
      orcsApi = orcsApi2;
      // project = ProjectModelUtil.getActiveproject();
      artifact = readableArtifact;
      artiafctLink = rapLink;

      String senderMailId = getMailIdByType(orcsApi2, currentLoggedInUser);

      if (assignees.size() > 0) {
         ICTeamEmail emailMessage = new ICTeamEmail(assignees, senderMailId, fromAddresss,
            getNotificationEmailSubject(), getUserMailBody(), BodyType.Html);
         emailMessage.send();
      }
   }

   /**
    * Method to get the mail id
    *
    * @param orcsApi2
    * @param currentLoggedInUser
    * @return
    */
   private static String getMailIdByType(final OrcsApi orcsApi2, final String currentLoggedInUser) {
      String buffer = "";

      if (types.contains(ICTeamNotifyType.UserCreated) || types.contains(ICTeamNotifyType.AdminUserCreated)) {
         buffer = getMailIdByUserId(orcsApi2, currentLoggedInUser);
      } else if (types.contains(ICTeamNotifyType.PasswordChangeRequest)) {
         buffer = currentLoggedInUser;
      }

      return buffer.toString();
   }

   /**
    * to get mail body
    *
    * @return
    */
   private static String getUserMailBody() {
      StringBuffer buffer = new StringBuffer();

      try {
         buffer.append(AHTML.newline());
         buffer.append(AHTML.simplePage("---------------------------------------------------------------"));
         buffer.append(AHTML.newline());

         buffer.append(getBodyMessage());
         buffer.append(AHTML.newline());

         if (artiafctLink != null) {
            buffer.append(AHTML.newline());

            try {
               buffer.append("Link: ");
               buffer.append(AHTML.newline());
               artiafctLink = artiafctLink.replaceAll(" ", "%20");
               buffer.append(AHTML.simplePage(artiafctLink));
            } catch (Exception e) {
               e.printStackTrace();
            }

            buffer.append(AHTML.newline());
         }

         buffer.append(AHTML.simplePage("---------------------------------------------------------------"));

         buffer.append(AHTML.newline());
         buffer.append(AHTML.newline());

         buffer.append(AHTML.italics("This is automatically generated message."));
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return buffer.toString();
   }

   /*
    * method to get mailid by userid
    */
   private static String getMailIdByUserId(final OrcsApi orcsApi, final String userId) {
      try {
         ArtifactReadable artifactReadable = UserUtility.getUserById(orcsApi, userId);
         ResultSet<? extends AttributeReadable<Object>> attrList = artifactReadable.getAttributes();

         for (AttributeReadable<Object> attributeReadable : attrList) {
            if (attributeReadable.getValue().toString().contains("@")) {
               return attributeReadable.getValue().toString();
            }
         }
      } catch (OseeCoreException e) {
         e.printStackTrace();
      }

      return null;
   }

   /*
    * Method with messages in the mail
    */
   private static String getBodyMessage() {
      String buffer = "";

      if (types.contains(ICTeamNotifyType.UserCreated)) {
         buffer = "Please use the below link to activate the user.";
      } else if (types.contains(ICTeamNotifyType.AdminUserCreated)) {
         buffer = "Please use the below link to activate the admin user.";
      } else if (types.contains(ICTeamNotifyType.PasswordChangeRequest)) {
         buffer = "Please use the below link to change the password.";
      }

      return buffer.toString();
   }
}

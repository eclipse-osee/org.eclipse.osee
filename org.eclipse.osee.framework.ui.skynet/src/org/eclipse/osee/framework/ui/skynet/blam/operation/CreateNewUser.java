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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.CoreArtifacts;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailGroupsAndUserGroups;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailGroupsAndUserGroups.GroupType;
import org.eclipse.osee.framework.ui.skynet.widgets.XList.XListItem;

/**
 * @author Ryan D. Brooks
 */
public class CreateNewUser extends AbstractBlam {

   private final static List<String> attrNames =
         Arrays.asList("Company", "Company Title", "Street", "City", "State", "Zip", "Phone", "Mobile Phone",
               "Fax Phone", "Website", "Notes");
   private Set<Artifact> groupArts;

   @Override
   public String getName() {
      return "Create New User";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Create New User", IProgressMonitor.UNKNOWN);

      User user = (User) ArtifactTypeManager.addArtifact(CoreArtifacts.User.getName(), BranchManager.getCommonBranch());

      String name = variableMap.getString("Name (Last, First)");
      if (name.equals("")) {
         AWorkbench.popup("ERROR", "Must Enter Name");
         monitor.done();
         return;
      }
      user.setName(name);

      String userId = variableMap.getString("UserId (unique)");
      if (userId.equals("")) {
         AWorkbench.popup("ERROR", "Must Enter UserId");
         monitor.done();
         return;
      }
      try {
         User existingUser = UserManager.getUserByUserId(userId);
         if (existingUser != null) {
            AWorkbench.popup("ERROR", "User with userId \"" + userId + "\" already exists.");
            monitor.done();
            return;
         }
      } catch (UserNotInDatabase ex) {
         // good that is why we are creating it
      }
      user.setSoleAttributeValue("User Id", userId);

      boolean active = variableMap.getBoolean("Active");
      user.setSoleAttributeValue("Active", active);

      String email = variableMap.getString("Email");
      if (email.equals("")) {
         AWorkbench.popup("ERROR", "Must Enter Email");
         monitor.done();
         return;
      }
      user.setSoleAttributeValue("Email", email);

      // Process string attribute names
      for (String attrName : attrNames) {
         String value = variableMap.getString(attrName);
         if (!value.equals("")) {
            user.setSoleAttributeValue(attrName, value);
         }
      }
      // Add user to selected User Group and Universal Group
      for (XListItem groupNameListItem : variableMap.getCollection(XListItem.class, "Groups")) {
         for (Artifact groupArt : groupArts) {
            if (groupNameListItem.getName().equals(groupArt.getName())) {
               if (groupArt.getArtifactTypeName().equals("Universal Group")) {
                  groupArt.addRelation(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS, user);
               } else if (groupArt.getArtifactTypeName().equals("User Group")) {
                  groupArt.addRelation(CoreRelationEnumeration.Users_User, user);
               }
            }
         }
      }

      user.persist();
      ArtifactEditor.editArtifact(user);
      monitor.done();
   }

   @Override
   public String getXWidgetsXml() {
      String widgetXml = "<xWidgets>" +
      //
      "<XWidget xwidgetType=\"XText\" displayName=\"Name (Last, First)\" required=\"true\"/>" +
      //
      "<XWidget xwidgetType=\"XText\" displayName=\"UserId (unique)\" required=\"true\"/>" +
      //
      "<XWidget xwidgetType=\"XText\" displayName=\"Email\" required=\"true\"/>" +
      //
      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Active\" required=\"true\" defaultValue=\"true\"/>";

      // Add all rest of attributes to fill
      for (String attrName : attrNames) {
         widgetXml += "<XWidget xwidgetType=\"XText\" displayName=\"" + attrName + "\"/>";
      }
      // Add groups to belong to
      try {
         groupArts = EmailGroupsAndUserGroups.getEmailGroupsAndUserGroups(UserManager.getUser(), GroupType.Both);
         String groupStr = "";
         for (Artifact art : groupArts) {
            groupStr += art.getName() + ",";
         }
         groupStr = groupStr.replaceFirst(",$", "");
         widgetXml +=
               "<XWidget xwidgetType=\"XList(" + groupStr + ")\" displayName=\"Groups\" defaultValue=\"Everyone\"/>";
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      //
      widgetXml += "</xWidgets>";
      return widgetXml;
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}
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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailUserGroups;
import org.eclipse.osee.framework.ui.skynet.widgets.XList.XListItem;

/**
 * @author Ryan D. Brooks
 */
public class CreateNewUser extends AbstractBlam {
   private final static List<AttributeTypeToken> attributeTypes = Arrays.asList(CoreAttributeTypes.Company,
      CoreAttributeTypes.CompanyTitle, CoreAttributeTypes.Street, CoreAttributeTypes.City, CoreAttributeTypes.State,
      CoreAttributeTypes.Zip, CoreAttributeTypes.Phone, CoreAttributeTypes.MobilePhone, CoreAttributeTypes.FaxPhone,
      CoreAttributeTypes.Country, CoreAttributeTypes.Website, CoreAttributeTypes.Notes);
   private Set<Artifact> groupArts;

   @Override
   public String getName() {
      return "Admin - Create New User";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Create New User", IProgressMonitor.UNKNOWN);

      User user = (User) ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, COMMON);

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
      user.setSoleAttributeValue(CoreAttributeTypes.UserId, userId);

      boolean active = variableMap.getBoolean("Active");
      user.setSoleAttributeValue(CoreAttributeTypes.Active, active);

      String email = variableMap.getString("Email");
      if (email.equals("")) {
         AWorkbench.popup("ERROR", "Must Enter Email");
         monitor.done();
         return;
      }
      user.setSoleAttributeValue(CoreAttributeTypes.Email, email);

      // Process string attribute names
      for (AttributeTypeToken attributeType : attributeTypes) {
         String value = variableMap.getString(attributeType.getName());
         if (Strings.isValid(value)) {
            user.setSoleAttributeValue(attributeType, value);
         }
      }
      // Add user to selected User Group and Universal Group
      for (XListItem groupNameListItem : variableMap.getCollection(XListItem.class, "Groups")) {
         for (Artifact groupArt : groupArts) {
            if (groupNameListItem.getName().equals(groupArt.getName())) {
               if (groupArt.isOfType(CoreArtifactTypes.UniversalGroup)) {
                  groupArt.addRelation(CoreRelationTypes.Universal_Grouping__Members, user);
               } else if (groupArt.isOfType(CoreArtifactTypes.UserGroup)) {
                  groupArt.addRelation(CoreRelationTypes.Users_User, user);
               }
            }
         }
      }

      user.persist(getClass().getSimpleName());
      RendererManager.open(user, PresentationType.DEFAULT_OPEN);
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
      for (AttributeTypeToken attributeType : attributeTypes) {
         widgetXml += "<XWidget xwidgetType=\"XText\" displayName=\"" + attributeType.getUnqualifiedName() + "\"/>";
      }
      // Add groups to belong to
      try {
         groupArts = EmailUserGroups.getEmailGroupsAndUserGroups(UserManager.getUser());
         String groupStr = "";
         for (Artifact art : groupArts) {
            groupStr += art.getName() + ",";
         }
         groupStr = groupStr.replaceFirst(",$", "");
         widgetXml +=
            "<XWidget xwidgetType=\"XList(" + groupStr + ")\" displayName=\"Groups\" defaultValue=\"Everyone\"/>";
      } catch (Exception ex) {
         log(ex);
      }
      //
      widgetXml += "</xWidgets>";
      return widgetXml;
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }

   @Override
   public String getTarget() {
      return TARGET_ALL;
   }

}
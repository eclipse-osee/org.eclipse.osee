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

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.*;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailUserGroups;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XXListItem;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;

/**
 * @author Ryan D. Brooks
 */
@Component(service = AbstractBlam.class, immediate = true)
public class CreateNewUserBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Create New User";
   static final String NAME_LAST_FIRST = "Name (Last, First)";
   static final String USERID_UNIQUE = "UserId (unique)";
   static final String ACTIVE = "Active";
   static final String EMAIL = "Email";
   private final static List<AttributeTypeToken> attributeTypes = Arrays.asList(Company, CompanyTitle, Street, City,
      State, Zip, Phone, MobilePhone, FaxPhone, Country, Website, Notes);

   private Set<Artifact> groupArts;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public CreateNewUserBlam() {
      super(BLAM_NAME, CoreImage.USER_ADD);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andTextWidget(NAME_LAST_FIRST).andRequired();
      wb.andTextWidget(USERID_UNIQUE).andRequired();
      wb.andTextWidget(EMAIL).andRequired();
      wb.andCheckBoxWidget(ACTIVE).andRequired().andDefault(true);
      // Add all rest of attributes to fill
      for (AttributeTypeToken attributeType : attributeTypes) {
         wb.andTextWidget(attributeType.getUnqualifiedName());
      }
      // Add groups to belong to
      try {
         groupArts = EmailUserGroups.getEmailGroupsAndUserGroups();
         List<String> groupNames = org.eclipse.osee.framework.jdk.core.type.Named.getNames(groupArts);
         wb.andWidget("Groups", WidgetId.XXListWidget).andDefault("Everyone").andSelectable(groupNames);
      } catch (Exception ex) {
         log(ex);
      }
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask(BLAM_NAME, IProgressMonitor.UNKNOWN);

      Artifact user = ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, COMMON);

      String name = variableMap.getString(NAME_LAST_FIRST);
      if (name.equals("")) {
         AWorkbench.popup("ERROR", "Must Enter Name");
         monitor.done();
         return;
      }
      user.setName(name);

      String userId = variableMap.getString(USERID_UNIQUE);
      if (userId.equals("")) {
         AWorkbench.popup("ERROR", "Must Enter UserId");
         monitor.done();
         return;
      }
      try {
         UserToken existingUser = OseeApiService.userSvc().getUserByUserId(userId);
         if (existingUser != null) {
            log("User with userId \"" + userId + "\" already exists.");
            monitor.done();
            return;
         }
      } catch (UserNotInDatabase ex) {
         // good that is why we are creating it
      }
      user.setSoleAttributeValue(UserId, userId);

      boolean active = variableMap.getBoolean(ACTIVE);
      user.setSoleAttributeValue(CoreAttributeTypes.Active, active);

      String email = variableMap.getString(EMAIL);
      if (email.equals("")) {
         AWorkbench.popup("ERROR", "Must Enter Email");
         monitor.done();
         return;
      }
      user.setSoleAttributeValue(Email, email);

      // Process string attribute names
      for (AttributeTypeToken attributeType : attributeTypes) {
         String value = variableMap.getString(attributeType.getUnqualifiedName());
         if (Strings.isValid(value)) {
            user.setSoleAttributeValue(attributeType, value);
         }
      }
      // Add user to selected User Group and Universal Group
      for (XXListItem groupNameListItem : variableMap.getCollection(XXListItem.class, "Groups")) {
         for (Artifact groupArt : groupArts) {
            if (groupNameListItem.getName().equals(groupArt.getName())) {
               if (groupArt.isOfType(CoreArtifactTypes.UniversalGroup)) {
                  groupArt.addRelation(CoreRelationTypes.UniversalGrouping_Members, user);
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

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(CoreUserGroups.OseeAdmin);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.USER_MANAGEMENT_ADMIN);
   }

   @Override
   public String getDescriptionUsage() {
      return "Creates a new OSEE user account. Enter the user's name (Last, First), a unique user ID, and email. Optionally set additional contact details and assign the user to email or user groups.";
   }

}

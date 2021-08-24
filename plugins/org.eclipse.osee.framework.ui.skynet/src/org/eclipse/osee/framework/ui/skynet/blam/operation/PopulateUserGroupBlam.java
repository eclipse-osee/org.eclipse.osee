/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactList;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ryan D. Brooks
 */
public class PopulateUserGroupBlam extends AbstractBlam {
   HashMap<String, User> emailToUser = new HashMap<>();

   @Override
   public String getName() {
      return "Populate User Group";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) {
      String emailAddresses = variableMap.getString("Email Addresses");
      Collection<Artifact> groups = variableMap.getCollection(Artifact.class, "User Groups");

      emailToUser.clear();
      for (User user : UserManager.getUsers()) {
         emailToUser.put(user.getSoleAttributeValue(CoreAttributeTypes.Email, ""), user);
      }

      List<User> users = new ArrayList<>();
      int count = 0;
      for (String emailAddress : emailAddresses.split("[\n\r\t ,;]+")) {
         User user = emailToUser.get(emailAddress);
         count++;
         if (user == null) {
            logf("User does not exist for: " + emailAddress);
         } else {
            users.add(user);
         }
      }
      logf("addresses: " + count);

      SkynetTransaction transaction = TransactionManager.createTransaction(COMMON, getName());
      for (Artifact group : groups) {
         for (User user : users) {
            group.addRelation(CoreRelationTypes.Users_User, user);
         }
         group.persist(transaction);
      }
      transaction.execute();
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      super.widgetCreating(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals("User Groups")) {
         XArtifactList listViewer = (XArtifactList) xWidget;
         listViewer.setInputArtifacts(ArtifactQuery.getArtifactListFromTypeWithInheritence(CoreArtifactTypes.UserGroup,
            COMMON, DeletionFlag.EXCLUDE_DELETED));
      }
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XArtifactList\" displayName=\"User Groups\" multiSelect=\"true\" /><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Body is html\" /><XWidget xwidgetType=\"XText\" displayName=\"Email Addresses\" fill=\"Vertically\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "populate user group(s) based on list of emails";
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.USER_MANAGEMENT_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.USERS);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.USERS);
   }

}
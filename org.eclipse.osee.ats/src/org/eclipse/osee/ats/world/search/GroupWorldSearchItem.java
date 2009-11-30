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
package org.eclipse.osee.ats.world.search;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.GroupListDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class GroupWorldSearchItem extends WorldUISearchItem {

   private Artifact group;
   private Artifact selectedGroup;
   private String groupName;
   private final Branch branch;

   public GroupWorldSearchItem(String displayName, String groupName, Branch branch) {
      super(displayName, FrameworkImage.GROUP);
      this.groupName = groupName;
      this.branch = branch;
   }

   public GroupWorldSearchItem(Artifact group) {
      super("Group Search", FrameworkImage.GROUP);
      this.group = group;
      this.branch = group.getBranch();
   }

   public GroupWorldSearchItem(Branch branch) {
      this("Group Search", null, branch);
   }

   public GroupWorldSearchItem(GroupWorldSearchItem groupWorldSearchItem, int toDifferentiateFromBranch) {
      super(groupWorldSearchItem, FrameworkImage.GROUP);
      this.group = groupWorldSearchItem.group;
      this.groupName = groupWorldSearchItem.groupName;
      this.selectedGroup = groupWorldSearchItem.selectedGroup;
      this.branch = groupWorldSearchItem.branch;
   }

   public String getGroupSearchName() {
      if (group != null)
         return group.getName();
      else if (selectedGroup != null)
         return selectedGroup.getName();
      else if (groupName != null) return groupName;
      return "";
   }

   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return String.format("Group Search - %s", getGroupSearchName());
   }

   public void getProduct() throws OseeCoreException {
      if (groupName == null) return;
      if (group == null && branch != null) group = UniversalGroup.getGroups(groupName, branch).iterator().next();
      if (group == null) throw new IllegalArgumentException("Can't Find Universal Group for " + getName());
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      getProduct();
      if (getSearchGroup() == null) return EMPTY_SET;
      Collection<Artifact> arts =
            getSearchGroup().getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Members);
      if (cancelled) return EMPTY_SET;
      return arts;
   }

   private Artifact getSearchGroup() {
      if (group != null) return group;
      if (selectedGroup != null) return selectedGroup;
      return null;
   }

   @Override
   public void performUI(SearchType searchType) throws OseeCoreException {
      super.performUI(searchType);
      if (groupName != null) return;
      if (group != null) return;
      if (searchType == SearchType.ReSearch && selectedGroup != null) return;
      GroupListDialog gld = new GroupListDialog(Display.getCurrent().getActiveShell());
      int result = gld.open();
      if (result == 0) {
         selectedGroup = gld.getSelection();
         return;
      } else {
         selectedGroup = null;
         cancelled = true;
      }
   }

   /**
    * @param selectedGroup the selectedGroup to set
    */
   public void setSelectedGroup(Artifact selectedGroup) {
      this.selectedGroup = selectedGroup;
   }

   @Override
   public WorldUISearchItem copy() {
      return new GroupWorldSearchItem(this, 0);
   }

}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeArtifactDialog;

/**
 * @author Donald G. Dunne
 */
public class GroupWorldSearchItem extends WorldUISearchItem {

   private Collection<Artifact> groups;
   private Collection<Artifact> selectedGroups;
   private String groupName;
   private final BranchId branch;

   public GroupWorldSearchItem(String displayName, String groupName, BranchId branch) {
      super(displayName, FrameworkImage.GROUP);
      this.groupName = groupName;
      this.branch = branch;
   }

   public GroupWorldSearchItem(Artifact group) {
      super("Group Search", FrameworkImage.GROUP);
      this.groups = new ArrayList<>();
      this.groups.add(group);
      this.branch = group.getBranch();
   }

   public GroupWorldSearchItem(BranchId branch) {
      this("Group Search", null, branch);
   }

   public GroupWorldSearchItem(GroupWorldSearchItem groupWorldSearchItem) {
      super(groupWorldSearchItem, FrameworkImage.GROUP);
      this.groups = groupWorldSearchItem.groups;
      this.groupName = groupWorldSearchItem.groupName;
      this.selectedGroups = groupWorldSearchItem.selectedGroups;
      this.branch = groupWorldSearchItem.branch;
   }

   public String getGroupSearchName() {
      if (groups != null && groups.size() == 1) {
         return groups.iterator().next().getName();
      } else if (selectedGroups != null && selectedGroups.size() == 1) {
         return selectedGroups.iterator().next().getName();
      } else if (groupName != null) {
         return groupName;
      }
      return "";
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      return String.format("Group Search - %s", getGroupSearchName());
   }

   public void getProduct()  {
      if (groupName == null) {
         return;
      }
      if (groups == null && branch != null) {
         groups.add(UniversalGroup.getGroups(groupName, branch).iterator().next());
      }
      if (groups == null) {
         throw new OseeArgumentException("Can't Find Universal Group for [%s]", getName());
      }
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType)  {
      getProduct();
      if (getSearchGroups() == null) {
         return EMPTY_SET;
      }
      Set<Artifact> arts = new HashSet<>(100);
      for (Artifact group : getSearchGroups()) {
         arts.addAll(group.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Members));
      }
      if (cancelled) {
         return EMPTY_SET;
      }
      return arts;
   }

   private Collection<Artifact> getSearchGroups() {
      if (groups != null) {
         return groups;
      }
      if (selectedGroups != null) {
         return selectedGroups;
      }
      return null;
   }

   @Override
   public void performUI(SearchType searchType)  {
      super.performUI(searchType);
      if (groupName != null) {
         return;
      }
      if (groups != null) {
         return;
      }
      if (searchType == SearchType.ReSearch && selectedGroups != null) {
         return;
      }
      Collection<Artifact> allGroups = UniversalGroup.getGroupsNotRoot(AtsClientService.get().getAtsBranch());

      FilteredCheckboxTreeArtifactDialog gld =
         new FilteredCheckboxTreeArtifactDialog("Select Groups", "Select Groups", allGroups);
      int result = gld.open();
      if (result == 0) {
         selectedGroups = gld.getChecked();
         return;
      } else {
         selectedGroups = null;
         cancelled = true;
      }
   }

   /**
    * @param selectedGroup the selectedGroup to set
    */
   public void setSelectedGroup(Artifact selectedGroup) {
      this.selectedGroups = new ArrayList<>();
      this.selectedGroups.add(selectedGroup);
   }

   @Override
   public WorldUISearchItem copy() {
      return new GroupWorldSearchItem(this);
   }

}

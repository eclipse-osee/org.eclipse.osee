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
package org.eclipse.osee.framework.ui.skynet.group;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class GroupExplorerItem implements IAdaptable {

   private final Artifact artifact;
   private final TreeViewer treeViewer;
   private final GroupExplorerItem parentItem;
   private List<GroupExplorerItem> groupItems;
   private final GroupExplorer groupExplorer;

   public GroupExplorerItem(TreeViewer treeViewer, Artifact artifact, GroupExplorerItem parentItem, GroupExplorer groupExplorer) {
      this.treeViewer = treeViewer;
      this.artifact = artifact;
      this.parentItem = parentItem;
      this.groupExplorer = groupExplorer;
   }

   @Override
   public String toString() {
      return "\"" + artifact.getName() + "\" - " + groupItems.size();
   }

   public boolean contains(Artifact artifact) {
      for (GroupExplorerItem item : getGroupItemsCached()) {
         if (item.getArtifact() != null && item.getArtifact().equals(artifact)) {
            return true;
         }
      }
      return false;
   }

   private List<GroupExplorerItem> getGroupItemsCached() {
      return groupItems;
   }

   /**
    * @param artifact to match with
    * @return UGI that contains artifact
    */
   public GroupExplorerItem getItem(Artifact artifact) {
      if (this.artifact != null && this.artifact.equals(artifact)) {
         return this;
      }
      for (GroupExplorerItem item : getGroupItemsCached()) {
         GroupExplorerItem ugi = item.getItem(artifact);
         if (ugi != null) {
            return ugi;
         }
      }
      return null;
   }

   public void dispose() {
      if (groupItems != null) {
         for (GroupExplorerItem item : groupItems) {
            item.dispose();
         }
      }
   }

   public boolean isUniversalGroup() {
      if (artifact == null || artifact.isDeleted()) {
         return false;
      }
      return artifact.isOfType(CoreArtifactTypes.UniversalGroup);
   }

   public String getTableArtifactType() {
      return artifact.getArtifactTypeName();
   }

   public String getTableArtifactName() {
      return artifact.getName();
   }

   public String getTableArtifactDescription() {
      return null;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public List<GroupExplorerItem> getGroupItems() {
      // Light loading; load the first time getChildren is called
      if (groupItems == null) {
         groupItems = new ArrayList<>();
         //         populateUpdateCategory();
      }
      //      populateUpdateCategory();
      //      List<GroupExplorerItem> items = new ArrayList<>();
      //      if (groupItems != null) items.addAll(groupItems);
      //      return items;

      try {
         List<Artifact> related = artifact.getRelatedArtifacts(CoreRelationTypes.UniversalGrouping_Members);
         for (Artifact art : related) {
            addGroupItem(new GroupExplorerItem(treeViewer, art, this, groupExplorer));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      List<GroupExplorerItem> items = new ArrayList<>();
      if (groupItems != null) {
         items.addAll(groupItems);
      }
      return items;
   }

   //   /**
   //    * Populate/Update this category with it's necessary children items
   //    */
   //   public void populateUpdateCategory() {
   //      try {
   //         for (GroupExplorerItem item : getGroupItems()) {
   //            removeGroupItem(item);
   //         }
   //         for (Artifact art : artifact.getRelatedArtifacts(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS)) {
   //            addGroupItem(new GroupExplorerItem(treeViewer, art, this, groupExplorer));
   //         }
   //      } catch (OseeCoreException ex) {
   //         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
   //      }
   //   }

   public void addGroupItem(GroupExplorerItem item) {
      if (!groupItems.contains(item)) {
         groupItems.add(item);
      }
   }

   public void removeGroupItem(GroupExplorerItem item) {
      item.dispose();
      groupItems.remove(item);
   }

   public GroupExplorerItem getParentItem() {
      return parentItem;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof GroupExplorerItem) {
         if (!artifact.getGuid().equals(((GroupExplorerItem) obj).getArtifact().getGuid())) {
            return false;
         }
         if (((GroupExplorerItem) obj).getParentItem() == null && getParentItem() == null) {
            return true;
         }
         if (((GroupExplorerItem) obj).getParentItem() != null && getParentItem() != null) {
            return ((GroupExplorerItem) obj).getParentItem().equals(getParentItem());
         }
      }
      return false;
   }

   @Override
   public int hashCode() {
      return artifact.getGuid().hashCode() + (getParentItem() != null ? getParentItem().getArtifact().getGuid().hashCode() : 0);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T getAdapter(Class<T> type) {
      if (type != null && type.isAssignableFrom(Artifact.class)) {
         return (T) getArtifact();
      }

      Object obj = null;
      T object = (T) obj;
      return object;
   }
}

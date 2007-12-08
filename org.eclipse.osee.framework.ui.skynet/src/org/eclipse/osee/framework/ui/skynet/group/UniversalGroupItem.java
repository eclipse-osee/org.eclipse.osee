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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent.EventData;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Donald G. Dunne
 */
public class UniversalGroupItem implements IEventReceiver {

   private Artifact artifact;
   private final TreeViewer treeViewer;
   private SkynetEventManager eventManager;
   private UniversalGroupItem parentItem;
   private List<UniversalGroupItem> groupItems;
   private final GroupExplorer groupExplorer;

   public UniversalGroupItem(TreeViewer treeViewer, Artifact artifact, UniversalGroupItem parentItem, GroupExplorer groupExplorer) {
      this.treeViewer = treeViewer;
      this.artifact = artifact;
      this.parentItem = parentItem;
      this.groupExplorer = groupExplorer;
      eventManager = SkynetEventManager.getInstance();
      eventManager.register(RemoteTransactionEvent.class, this);
      eventManager.register(LocalTransactionEvent.class, this);
   }

   public boolean contains(Artifact artifact) {
      for (UniversalGroupItem item : getGroupItems()) {
         if (item.getArtifact() != null && item.getArtifact().equals(artifact)) return true;
      }
      return false;
   }

   /**
    * @param artifact to match with
    * @return UGI that contains artifact
    */
   public UniversalGroupItem getItem(Artifact artifact) {
      if (this.artifact != null && this.artifact.equals(artifact)) return this;
      for (UniversalGroupItem item : getGroupItems()) {
         UniversalGroupItem ugi = item.getItem(artifact);
         if (ugi != null) return ugi;
      }
      return null;
   }

   public void dispose() {
      eventManager.unRegisterAll(this);
      if (groupItems != null) for (UniversalGroupItem item : groupItems)
         item.dispose();
   }

   public boolean isUniversalGroup() {
      if (artifact == null || artifact.isDeleted()) return false;
      return artifact.getArtifactTypeName().equals("Universal Group");
   }

   public String getTableArtifactType() {
      return artifact.getArtifactTypeName();
   }

   public String getTableArtifactName() {
      return artifact.getDescriptiveName();
   }

   public String getTableArtifactDescription() {
      return null;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public List<UniversalGroupItem> getGroupItems() {
      // Light loading; load the first time getChildren is called
      if (groupItems == null) {
         groupItems = new ArrayList<UniversalGroupItem>();
         populateUpdateCategory();
      }
      List<UniversalGroupItem> items = new ArrayList<UniversalGroupItem>();
      if (groupItems != null) items.addAll(groupItems);
      return items;
   }

   /**
    * Populate/Update this category with it's necessary children items
    */
   public void populateUpdateCategory() {
      try {
         for (UniversalGroupItem item : getGroupItems()) {
            removeGroupItem(item);
         }
         for (Artifact art : artifact.getArtifacts(RelationSide.UNIVERSAL_GROUPING__MEMBERS)) {
            addGroupItem(new UniversalGroupItem(treeViewer, art, this, groupExplorer));
         }
      } catch (SQLException ex) {
         SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   public void addGroupItem(UniversalGroupItem item) {
      groupItems.add(item);
   }

   public void removeGroupItem(UniversalGroupItem item) {
      item.dispose();
      groupItems.remove(item);
   }

   public void onEvent(final Event event) {
      if (treeViewer == null || treeViewer.getTree().isDisposed() || (artifact != null && artifact.isDeleted())) {
         dispose();
         return;
      }
      final UniversalGroupItem tai = this;

      if (event instanceof TransactionEvent) {
         EventData ed = ((TransactionEvent) event).getEventData(artifact);
         if (ed.isRemoved()) {
            treeViewer.refresh();
            groupExplorer.restoreSelection();
         } else if (ed.getAvie() != null && ed.getAvie().getOldVersion().equals(artifact)) {
            if (artifact == ed.getAvie().getOldVersion()) {
               artifact = ed.getAvie().getNewVersion();
               treeViewer.refresh(tai);
               groupExplorer.restoreSelection();
            }
         } else if (ed.isModified()) {
            treeViewer.update(tai, null);
         } else if (ed.isRelChange()) {
            populateUpdateCategory();
            treeViewer.refresh(tai);
            groupExplorer.restoreSelection();
         }
      } else
         SkynetGuiPlugin.getLogger().log(Level.SEVERE, "Unexpected event => " + event);
   }

   public boolean runOnEventInDisplayThread() {
      return true;
   }

   public UniversalGroupItem getParentItem() {
      return parentItem;
   }

}

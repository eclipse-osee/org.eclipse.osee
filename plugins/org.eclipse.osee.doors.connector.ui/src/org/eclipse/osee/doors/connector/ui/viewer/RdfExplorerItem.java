/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.doors.connector.ui.viewer;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.doors.connector.core.DoorsArtifact;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class RdfExplorerItem implements IAdaptable {

   private final TreeViewer treeViewer;
   private final RdfExplorerItem parentItem;
   private List<RdfExplorerItem> groupItems;
   private final RdfExplorer rdfExplorer;
   private final String name;
   private final Long id;
   private final DoorsArtifact dwaItem;

   public RdfExplorerItem(String name, TreeViewer treeViewer, RdfExplorerItem parentItem, RdfExplorer rdfExplorer, DoorsArtifact dwaItem) {
      this.name = name;
      this.id = Lib.generateUuid();
      this.treeViewer = treeViewer;
      this.parentItem = parentItem;
      this.rdfExplorer = rdfExplorer;
      this.dwaItem = dwaItem;
   }

   public DoorsArtifact getDwaItem() {
      return dwaItem;
   }

   public TreeViewer getTreeViewer() {
      return treeViewer;
   }

   public RdfExplorer getRdfExplorer() {
      return rdfExplorer;
   }

   @Override
   public String toString() {
      return getName();
   }

   public void dispose() {
      if (groupItems != null) {
         for (RdfExplorerItem item : groupItems) {
            item.dispose();
         }
      }
   }

   public String getName() {
      return name;
   }

   public List<RdfExplorerItem> getChildrenItems() {
      if (groupItems == null) {
         groupItems = new LinkedList<>();
      }
      return groupItems;
   }

   public void addItem(RdfExplorerItem item) {
      List<RdfExplorerItem> children = getChildrenItems();
      if (!children.contains(item)) {
         children.add(item);
      }
   }

   public void removeGroupItem(RdfExplorerItem item) {
      item.dispose();
      groupItems.remove(item);
   }

   public RdfExplorerItem getParentItem() {
      return parentItem;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof RdfExplorerItem) {
         if (((RdfExplorerItem) obj).getId().equals(getId())) {
            return true;
         }
      }
      return false;
   }

   protected Long getId() {
      return id;
   }

   @Override
   public int hashCode() {
      return getId().hashCode();
   }

   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.REPOSITORY);
   }

   @Override
   public <T> T getAdapter(Class<T> adapter) {
      return null;
   }
}

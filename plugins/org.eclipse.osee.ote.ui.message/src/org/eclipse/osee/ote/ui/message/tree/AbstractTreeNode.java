/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.tree;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.graphics.Image;

public abstract class AbstractTreeNode {
   private String treeViewName = "treeViewName";
   private AbstractTreeNode parent;
   private Image image;
   private int level;
   private boolean enabled = true;
   private volatile boolean isSelected = false;
   private volatile boolean isDisposed = false;
   private boolean hasMappingToCurrentMemType = true;
   private String disabledReason = "";

   private boolean isChecked = false;

   public AbstractTreeNode(final String treeViewName) {
      this(treeViewName, null);
   }

   /**
    * constructs the node and adds this node to the parent
    */
   public AbstractTreeNode(final String treeViewName, final Image image) {
      this.treeViewName = treeViewName;
      this.image = image;
   }

   protected void setParent(AbstractTreeNode parent) {
      this.parent = parent;
   }

   protected void setImage(Image image) {
      this.image = image;
   }

   /**
    * sets the level or depth of this node. Usually the root is level 0. The level policy is up to the implementation
    */
   protected void setLevel(final int level) {
      this.level = level;
   }

   /**
    * @return Returns the isDisposed.
    */
   public boolean isDisposed() {
      return isDisposed;
   }

   /**
    * destroys the node. Must be called by the parent that wants to remove the child
    * 
    * @param isDisposed The isDisposed to set.
    */
   protected void dispose() {
      removeAll();
      this.isDisposed = true;
   }

   /**
    * gets the name of this node as it would appear in a tree view
    * 
    * @return Returns tree view name string.
    */
   public String getName() {
      return treeViewName;
   }

   abstract String getLabel();

   /**
    * Returns the parent name and this node's name seperated by a dot. If the parent of this node is null then it will
    * return exactly the same as {@link #treeViewName}.
    * 
    * @return Returns qualified name string. exactly the same as {@link #getName()}.
    */
   public String getQualifiedName() {
      if (parent != null) {
         return parent.treeViewName + '.' + treeViewName;
      } else {
         return treeViewName;
      }
   }

   /**
    * gets the level of this node. Level represents the number of descendants this node has. Root has a level of zero
    * 
    * @return Returns level value.
    */
   public int getLevel() {
      return level;
   }

   /**
    * Gets the parent node of this node
    * 
    * @return the parent node or null if no parent exist for this node
    */
   public AbstractTreeNode getParent() {
      return parent;
   }

   /**
    * returns an image that can be displayed
    * 
    * @return Returns image object reference.
    */
   public Image getImage(XViewerColumn columns) {
      return image;
   }

   abstract public void deleteChildren(Collection<AbstractTreeNode> children);

   abstract public void removeAll();

   abstract public boolean hasChildren();

   abstract public Collection<? extends AbstractTreeNode> getChildren();

   public void delete() {
      if (getParent() != null) {
         getParent().deleteChildren(Collections.singleton(this));
      }
   }

   public abstract boolean canSetValue();

   /**
    * Sets the flag determining whether the node can be edited or displays anything in the right-click pop-up menu. For
    * right now this will only apply to non-mapping elements which exist solely in pub/sub and not in other physical
    * types.
    * 
    * @param hasAMapping true if node should be able to be edited.
    */
   public void setHasMappingToCurrentMemType(boolean hasAMapping) {
      this.hasMappingToCurrentMemType = hasAMapping;
   }

   public boolean hasMappingToCurrentMemType() {
      return this.hasMappingToCurrentMemType;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public boolean isSelected() {
      return isSelected;
   }

   public void setSelected(final boolean selected) {
      isSelected = selected;
   }

   public abstract <T> T visit(INodeVisitor<T> visitor);

   // public List<Object> getElementPath() {
   // return elementPath;
   // }

   // public void addToPath(Object obj) {
   // elementPath.add(obj);
   // }

   public String getLabel(XViewerColumn columns) {
      return "";
   }

   public Image getImage() {
      return image;
   }

   public String getDisabledReason() {
      return disabledReason;
   }

   public void setDisabledReason(String disabledReason) {
      this.disabledReason = disabledReason;
   }

   public boolean isChecked() {
      return isChecked;
   }

   public void setChecked(boolean checked) {
      this.isChecked = checked;
   }

}

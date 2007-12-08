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
package org.eclipse.osee.framework.ui.admin;

/**
 * @author Jeff C. Phillips
 */
public class Group {

   private int groupId;
   private String groupName;
   private int childrenSize;
   private int numChildrenChecked;

   /**
    * 
    */
   public Group(String groupName, int groupId) {
      super();
      this.groupName = groupName;
      this.groupId = groupId;
   }

   /**
    * @return Returns the groupId.
    */
   public int getGroupId() {
      return groupId;
   }

   /**
    * @return Returns the groupName.
    */
   public String getGroupName() {
      return groupName;
   }

   /**
    * @return Returns the childrenSize.
    */
   public int getChildrenSize() {
      return childrenSize;
   }

   /**
    * @param childrenSize The childrenSize to set.
    */
   public void setChildrenSize(int childrenSize) {
      this.childrenSize = childrenSize;
   }

   /**
    * @return Returns the currentNumberChildrenChecked.
    */
   public int getNumChildrenChecked() {
      return numChildrenChecked;
   }

   /**
    * Increments the number of checked policies per user.<br>
    * <br>
    * Note: This is to maintain graying and checking functionality for each user. <br>
    * When the <code>numChildrenChecked</code> is equal to the <code>childrenSize</code> <br>
    * the parent will be checked. If the <code>numChildrenChecked</code> is equal to 0, <br>
    * then it will be unchecked. Else it will be grayed.
    */
   public void incrementChildChecked() {
      numChildrenChecked++;
   }

   /**
    * * decrements the number of checked policies per user.<br>
    * <br>
    * Note: This is to maintain graying and checking functionality for each user. <br>
    * When the <code>numChildrenChecked</code> is equal to the <code>childrenSize</code> <br>
    * the parent will be checked. If the <code>numChildrenChecked</code> is equal to 0, <br>
    * then it will be unchecked. Else it will be grayed.
    */
   public void minusChildChecked() {
      numChildrenChecked--;
   }

   /**
    * @return boolean, true if all the children are checked else false
    */
   public boolean isAllChecked() {
      if (numChildrenChecked == childrenSize) return true;
      return false;
   }

   /**
    * @return boolean, true if none of the children are checked else false
    */
   public boolean isAllEmpty() {
      if (numChildrenChecked == 0) return true;
      return false;
   }

   /**
    * reset the number of checked polcies
    */
   public void resetChildrenCheckedCount() {
      numChildrenChecked = 0;
   }

   /**
    * @param numChildrenChecked The numChildrenChecked to set.
    */
   public void setNumChildrenChecked(int numChildrenChecked) {
      this.numChildrenChecked = numChildrenChecked;
   }
}

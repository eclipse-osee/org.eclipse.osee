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
package org.eclipse.osee.ote.ui.define.viewers.data;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class BranchItem extends DataItem implements IXViewerItem {

   private Branch branch;
   private static boolean isScriptItemGrouped = true;

   public BranchItem(Branch branch, DataItem parentItem) {
      super(parentItem);
      this.branch = branch;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.viewers.data.DataItem#getData()
    */
   @Override
   public String getData() {
      return branch.getBranchName();
   }

   public Image getImage() {
      return OteUiDefinePlugin.getInstance().getImage("childBranch.gif");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.viewers.data.IXViewerItem#getLabel(int)
    */
   public String getLabel(int index) {
      return index == 0 ? getData() : "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.define.viewers.data.DataItem#getKey()
    */
   @Override
   public Object getKey() {
      return branch;
   }

   @Override
   public DataItem[] getChildren() {
      DataItem[] toReturn = super.getChildren();
      if (isGroupModeEnabled() != true) {
         List<DataItem> tempList = new ArrayList<DataItem>();
         for (DataItem item : toReturn) {
            if (item.hasChildren()) {
               for (DataItem itemX : item.getChildren()) {
                  tempList.add(itemX);
               }
            }
         }
         toReturn = tempList.toArray(new DataItem[tempList.size()]);
      }
      return toReturn;
   }

   public static void setGroupModeEnabled(boolean isEnabled) {
      BranchItem.isScriptItemGrouped = isEnabled;
   }

   public static boolean isGroupModeEnabled() {
      return BranchItem.isScriptItemGrouped;
   }
}

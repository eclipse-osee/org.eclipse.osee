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

package org.eclipse.osee.framework.ui.skynet.dialogs;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public final class SimpleBranchLabelProvider extends LabelProvider {
   private static final Image PARENT_BRANCH_IMAGE = SkynetGuiPlugin.getInstance().getImage("branch.gif");
   private static final Image CHILD_BRANCH_IMAGE = SkynetGuiPlugin.getInstance().getImage("change_managed_branch.gif");

   public Image getImage(Object arg0) {
      Image toReturn = null;
      if (arg0 instanceof Branch) {
         Branch branch = ((Branch) arg0);
         if (branch != null) {
            toReturn = branch.hasParentBranch() ? CHILD_BRANCH_IMAGE : PARENT_BRANCH_IMAGE;
         }
      }
      return toReturn;
   }

   public String getText(Object arg0) {
      String toReturn = "";
      if (arg0 instanceof Branch) {
         Branch branch = ((Branch) arg0);
         if (branch != null) {
            toReturn = branch.getBranchName();
         }
      }
      return toReturn;
   }
}

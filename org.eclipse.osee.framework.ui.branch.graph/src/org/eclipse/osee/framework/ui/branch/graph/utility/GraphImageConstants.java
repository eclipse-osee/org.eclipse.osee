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
package org.eclipse.osee.framework.ui.branch.graph.utility;

import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class GraphImageConstants {

   private GraphImageConstants() {
   }

   public static Image getImage(Branch branch) {
      Image image = null;
      BranchType branchType = branch.getBranchType();
      switch (branchType) {
         case SYSTEM_ROOT:
            image = ImageManager.getImage(FrameworkImage.BRANCH_SYSTEM_ROOT);
            break;
         case TOP_LEVEL:
            image = ImageManager.getImage(FrameworkImage.BRANCH_TOP);
            break;
         case BASELINE:
            image = ImageManager.getImage(FrameworkImage.BRANCH_BASELINE);
            break;
         case WORKING:
            image = ImageManager.getImage(FrameworkImage.BRANCH_WORKING);
            break;
         case MERGE:
            image = ImageManager.getImage(FrameworkImage.BRANCH_MERGE);
            break;
      }
      return image;
   }
}

/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.branch.graph.utility;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class GraphImageConstants {

   private GraphImageConstants() {
   }

   public static Image getImage(BranchId branch) {
      Image image = null;
      BranchType type = BranchManager.getType(branch);
      if (type.equals(BranchType.SYSTEM_ROOT)) {
         image = ImageManager.getImage(FrameworkImage.BRANCH_SYSTEM_ROOT);
      } else if (type.equals(BranchType.BASELINE)) {
         image = ImageManager.getImage(FrameworkImage.BRANCH_BASELINE);
      } else if (type.equals(BranchType.WORKING)) {
         image = ImageManager.getImage(FrameworkImage.BRANCH_WORKING);
      } else if (type.equals(BranchType.MERGE)) {
         image = ImageManager.getImage(FrameworkImage.BRANCH_MERGE);
      }
      return image;
   }
}
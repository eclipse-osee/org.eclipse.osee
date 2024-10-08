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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.branch.graph.model.BranchModel;
import org.eclipse.swt.graphics.Color;

/**
 * @author Roberto E. Escobar
 */
public class GraphColorConstants {

   public static final Color FONT_COLOR = new Color(null, 1, 70, 122);
   public static final Color BGCOLOR = new Color(null, 250, 250, 250);

   private final static Color CURRENT_BRANCH = ColorConstants.orange;
   private final static Color SYSTEM_ROOT_BRANCH = new Color(null, 255, 180, 220);
   private final static Color WORKING_BRANCH_COLOR = new Color(null, 244, 244, 244);

   private final static Color BASELINE_BRANCH = new Color(null, 120, 255, 120);
   private final static Color MERGE_BRANCH = new Color(null, 200, 200, 240);

   private GraphColorConstants() {
   }

   public static Color getBranchColor(BranchModel branchModel) {
      Color toReturn = ColorConstants.gray;
      if (branchModel.isDefaultBranch()) {
         toReturn = GraphColorConstants.CURRENT_BRANCH;
      } else {
         BranchType type = BranchManager.getType(branchModel.getBranch());
         if (type.equals(BranchType.BASELINE)) {
            toReturn = GraphColorConstants.BASELINE_BRANCH;
         } else if (type.equals(BranchType.MERGE)) {
            toReturn = GraphColorConstants.MERGE_BRANCH;
         } else if (type.equals(BranchType.SYSTEM_ROOT)) {
            toReturn = GraphColorConstants.SYSTEM_ROOT_BRANCH;
         } else if (type.equals(BranchType.WORKING)) {
            toReturn = GraphColorConstants.WORKING_BRANCH_COLOR;
         }
      }
      return toReturn;
   }
}
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

import org.eclipse.draw2d.ColorConstants;
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

   @SuppressWarnings("incomplete-switch")
   public static Color getBranchColor(BranchModel branchModel) {
      Color toReturn = ColorConstants.gray;
      if (branchModel.isDefaultBranch()) {
         toReturn = GraphColorConstants.CURRENT_BRANCH;
      } else {
         switch (BranchManager.getType(branchModel.getBranch())) {
            case BASELINE:
               toReturn = GraphColorConstants.BASELINE_BRANCH;
               break;
            case MERGE:
               toReturn = GraphColorConstants.MERGE_BRANCH;
               break;
            case SYSTEM_ROOT:
               toReturn = GraphColorConstants.SYSTEM_ROOT_BRANCH;
               break;
            case WORKING:
               toReturn = GraphColorConstants.WORKING_BRANCH_COLOR;
               break;
         }
      }
      return toReturn;
   }
}

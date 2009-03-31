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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.branch.graph.BranchGraphActivator;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class GraphImageConstants {

   public static final String IMG_FILTER_CONNECTIONS = "filter_connections.gif";
   public static final String IMG_FILTER_TXS = "DBiconBlue.GIF";

   private static final String IMG_SYSTEM_ROOT = "branchYellow.gif";
   private static final String IMG_TOP_LEVEL = "top.gif";
   private static final String IMG_BASELINE = "baseline.gif";
   private static final String IMG_WORKING = "working.gif";
   private static final String IMG_MERGE = "merge.gif";
   public static final String TX_IMAGE = "DBiconBlue.GIF";

   private GraphImageConstants() {
   }

   public static Image getImage(String imageName) {
      return BranchGraphActivator.getInstance().getImage(imageName);
   }

   public static ImageDescriptor getImageDescriptor(String imageName) {
      return BranchGraphActivator.getInstance().getImageDescriptor(imageName);
   }

   public static Image getImage(Branch branch) {
      Image image = null;
      BranchType branchType = branch.getBranchType();
      switch (branchType) {
         case SYSTEM_ROOT:
            image = getImage(GraphImageConstants.IMG_SYSTEM_ROOT);
            break;
         case TOP_LEVEL:
            image = getImage(GraphImageConstants.IMG_TOP_LEVEL);
            break;
         case BASELINE:
            image = getImage(GraphImageConstants.IMG_BASELINE);
            break;
         case WORKING:
            image = getImage(GraphImageConstants.IMG_WORKING);
            break;
         case MERGE:
            image = getImage(GraphImageConstants.IMG_MERGE);
            break;
      }
      return image;
   }
}

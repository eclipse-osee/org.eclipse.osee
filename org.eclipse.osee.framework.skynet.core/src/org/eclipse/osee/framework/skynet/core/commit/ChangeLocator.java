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
package org.eclipse.osee.framework.skynet.core.commit;

import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Roberto E. Escobar
 */
public class ChangeLocator {
   private final Branch sourceBranch;
   private final Branch destinationBranch;

   public ChangeLocator(Branch sourceBranch, Branch destinationBranch) {
      this.sourceBranch = sourceBranch;
      this.destinationBranch = destinationBranch;
   }

   public Branch getSourceBranch() {
      return sourceBranch;
   }

   public int getSourceBranchId() {
      return sourceBranch.getBranchId();
   }

   public Branch getDestinationBranch() {
      return destinationBranch;
   }

   public int getDestinationBranchId() {
      return destinationBranch.getBranchId();
   }
}

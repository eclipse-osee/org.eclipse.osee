/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author Megumi Telles
 */
public class ChangeBranchTypeRequest {
   private final int branchId;
   private final BranchType type;

   public ChangeBranchTypeRequest(int branchId, BranchType type) {
      super();
      this.branchId = branchId;
      this.type = type;
   }

   public int getBranchId() {
      return branchId;
   }

   public BranchType getType() {
      return type;
   }

}

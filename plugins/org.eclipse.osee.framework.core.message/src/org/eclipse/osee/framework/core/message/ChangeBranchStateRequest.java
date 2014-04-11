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
package org.eclipse.osee.framework.core.message;

import org.eclipse.osee.framework.core.enums.BranchState;

/**
 * @author Megumi Telles
 */
public class ChangeBranchStateRequest {
   private final long branchUuid;
   private final BranchState state;

   public ChangeBranchStateRequest(long branchUuid, BranchState state) {
      super();
      this.branchUuid = branchUuid;
      this.state = state;
   }

   public long getBranchId() {
      return branchUuid;
   }

   public BranchState getState() {
      return state;
   }

}

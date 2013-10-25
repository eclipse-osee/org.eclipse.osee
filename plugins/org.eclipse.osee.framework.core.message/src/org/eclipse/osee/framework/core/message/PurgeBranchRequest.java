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
package org.eclipse.osee.framework.core.message;

/**
 * @author Jeff C. Phillips
 * @author Megumi Telles
 */
public class PurgeBranchRequest {
   private final long branchId;
   private final boolean recursive;

   public PurgeBranchRequest(long branchId, boolean recursive) {
      this.branchId = branchId;
      this.recursive = recursive;
   }

   public long getBranchId() {
      return branchId;
   }

   public boolean isRecursive() {
      return recursive;
   }
}

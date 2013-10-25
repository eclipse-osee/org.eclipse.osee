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

/**
 * @author Roberto E. Escobar
 */
public class BranchCommitRequest {

   private final int userArtifact;
   private final long sourceBranch;
   private final long destinationBranch;
   private final boolean isArchiveAllowed;

   public BranchCommitRequest(int userArtifact, long sourceBranch, long destinationBranch, boolean isArchiveAllowed) {
      super();
      this.userArtifact = userArtifact;
      this.sourceBranch = sourceBranch;
      this.destinationBranch = destinationBranch;
      this.isArchiveAllowed = isArchiveAllowed;
   }

   public int getUserArtId() {
      return userArtifact;
   }

   public long getSourceBranchId() {
      return sourceBranch;
   }

   public long getDestinationBranchId() {
      return destinationBranch;
   }

   public boolean isArchiveAllowed() {
      return isArchiveAllowed;
   }
}

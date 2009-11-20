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

import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Roberto E. Escobar
 */
public class BranchCommitRequest {

   private final IBasicArtifact<?> userArtifact;
   private final Branch sourceBranch;
   private final Branch destinationBranch;
   private final boolean isArchiveAllowed;

   public BranchCommitRequest(IBasicArtifact<?> userArtifact, Branch sourceBranch, Branch destinationBranch, boolean isArchiveAllowed) {
      super();
      this.userArtifact = userArtifact;
      this.sourceBranch = sourceBranch;
      this.destinationBranch = destinationBranch;
      this.isArchiveAllowed = isArchiveAllowed;
   }

   public IBasicArtifact<?> getUser() {
      return userArtifact;
   }

   public Branch getSourceBranch() {
      return sourceBranch;
   }

   public Branch getDestinationBranch() {
      return destinationBranch;
   }

   public boolean isArchiveAllowed() {
      return isArchiveAllowed;
   }
}

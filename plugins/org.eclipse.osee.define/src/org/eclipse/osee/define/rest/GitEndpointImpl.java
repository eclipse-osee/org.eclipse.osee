/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.define.rest;

import java.util.List;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.api.DefineOperations;
import org.eclipse.osee.define.api.GitEndpoint;
import org.eclipse.osee.define.api.GitOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */
public class GitEndpointImpl implements GitEndpoint {
   private final GitOperations gitOps;

   public GitEndpointImpl(ActivityLog activityLog, OrcsApi orcsApi, DefineOperations defineOperations) {
      this.gitOps = defineOperations.gitOperations();
   }

   @Override
   public ArtifactId trackGitBranch(BranchId branch, String gitBranchName, boolean clone, String gitRepoUrl) {
      return gitOps.trackGitBranch(gitRepoUrl, branch, gitBranchName, clone, null);
   }

   @Override
   public ArtifactId updateGitTrackingBranch(BranchId branch, String repositoryName, boolean fetch, boolean shallowImport, String gitBranchName) {
      return gitOps.updateGitTrackingBranch(branch, gitOps.getRepoArtifact(branch, repositoryName), gitBranchName,
         fetch, null, false, shallowImport);
   }

   @Override
   public List<String> getChangeIdBetweenTags(BranchId branch, String repositoryName, String startTag, String endTag) {
      return gitOps.getChangeIdBetweenTags(branch, gitOps.getRepoArtifact(branch, repositoryName), startTag, endTag);
   }

   @Override
   public List<String> getRemoteBranches(BranchId branch, String repositoryName) {
      return gitOps.getRemoteBranches(branch, gitOps.getRepoArtifact(branch, repositoryName));

   }
}
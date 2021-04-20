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
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.api.GitEndpoint;
import org.eclipse.osee.define.api.GitOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */
public class GitEndpointImpl implements GitEndpoint {
   private final OrcsApi orcsApi;
   private final GitOperations gitOps;
   private final ActivityLog activityLog;

   public GitEndpointImpl(ActivityLog activityLog, OrcsApi orcsApi, DefineApi defineApi) {
      this.orcsApi = orcsApi;
      this.activityLog = activityLog;
      this.gitOps = defineApi.gitOperations();
   }

   @Override
   public ArtifactId trackGitBranch(BranchId branch, UserId account, String gitBranchName, boolean clone, String gitRepoUrl) {
      return gitOps.trackGitBranch(gitRepoUrl, branch, account, gitBranchName, clone, null);
   }

   @Override
   public ArtifactId updateGitTrackingBranch(BranchId branch, String repositoryName, UserId account, boolean fetch, boolean shallowImport, String gitBranchName) {
      return gitOps.updateGitTrackingBranch(branch, gitOps.getRepoArtifact(branch, repositoryName), account,
         gitBranchName, fetch, null, false, shallowImport);
   }

   @Override
   public List<String> getChangeIdBetweenTags(BranchId branch, String repositoryName, String startTag, String endTag) {
      return gitOps.getChangeIdBetweenTags(branch, gitOps.getRepoArtifact(branch, repositoryName), startTag, endTag);
   }
}
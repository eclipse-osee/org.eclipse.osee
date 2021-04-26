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

package org.eclipse.osee.define.api;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Ryan D. Brooks </br>
 * </br>
 * <a href="https://github.com/centic9/jgit-cookbook">JGit Cookbook</a>
 * <a href="https://www.codeaffine.com/2015/12/15/getting-started-with-jgit/">Getting Started with JGit</a>
 * <a href="https://www.codeaffine.com/2014/12/09/jgit-authentication/">JGit Authentication</a>
 * <a href="https://gist.github.com/rherrmann/433adb44b3d15ed0f0c7">JGit Examples</a>
 * <a href="https://download.eclipse.org/jgit/site/4.7.0.201704051617-r/apidocs/">JGit Java Docs</a>
 */
public interface GitOperations {

   ArtifactId trackGitBranch(String gitRepoUrl, BranchId branch, UserId account, String gitBranchName, boolean clone, String password);

   ArtifactId updateGitTrackingBranch(BranchId branch, ArtifactReadable repoArtifact, UserId account, String gitBranchName, boolean fetch, String password, boolean initialImport, boolean shallowImport);

   ArtifactReadable getRepoArtifact(BranchId branch, String repositoryName);

   void fetch(ArtifactReadable repoArtifact, String password);

   ArtifactToken getCommitArtifactId(BranchId branch, String changeId);

   List<String> getChangeIdBetweenTags(BranchId branch, ArtifactReadable repoArtifact, String startTag, String endTag);

   List<String> getRemoteBranches(BranchId branch, ArtifactReadable repoArtifact);
}
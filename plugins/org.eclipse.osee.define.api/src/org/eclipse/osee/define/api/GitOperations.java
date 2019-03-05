/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

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

   ArtifactId updateGitTrackingBranch(BranchId branch, ArtifactReadable repoArtifact, UserId account, String gitBranchName, boolean fetch, String password, boolean initialImport);

   ArtifactReadable getRepoArtifact(BranchId branch, String repositoryName);

   void fetch(ArtifactReadable repoArtifact, String password);

   ArtifactToken getCommitArtifactId(BranchId branch, String changeId);
}
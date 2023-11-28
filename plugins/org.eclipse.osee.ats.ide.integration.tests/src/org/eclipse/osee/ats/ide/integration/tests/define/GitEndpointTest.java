/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.define;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.define.rest.api.git.GitEndpoint;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Stephen Molaro
 * @author Kenn Luecke
 */
public class GitEndpointTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();
   @Rule
   public TemporaryFolder repoFolder = new TemporaryFolder();
   @Rule
   public TemporaryFolder remoteFolder = new TemporaryFolder();

   private static final Pattern changeIdPattern = Pattern.compile("\\s+Change-Id: (I\\w{40})");
   private final Matcher changeIdMatcher = changeIdPattern.matcher("");

   Repository jgitRepo;
   private Git git;

   private static final BranchToken branch = DemoBranches.CIS_Bld_1;
   private static final Artifact parent = ArtifactQuery.getArtifactFromId(CoreArtifactTokens.GitRepoFolder, branch);
   private Artifact repoArtifact;

   public static final ArtifactToken Git_Repo =
      ArtifactToken.valueOf(8974893274747923L, "Demo Repo", DemoBranches.CIS_Bld_1, CoreArtifactTypes.GitRepository);

   private GitEndpoint gitEp;

   @Before
   public void setup() {
      OseeClient oseeclient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);
      gitEp = oseeclient.getGitEndpoint();

      try {
         git = Git.init().setDirectory(repoFolder.getRoot()).call();

         CloneCommand jgitClone =
            Git.cloneRepository().setURI(repoFolder.getRoot().getAbsolutePath() + File.separator + ".git").setDirectory(
               remoteFolder.getRoot()).setNoCheckout(true);
         jgitClone.call();

         File gitDirPath = new File(remoteFolder.getRoot().getAbsolutePath() + File.separator + ".git");
         jgitRepo =
            new FileRepositoryBuilder().setGitDir(gitDirPath).readEnvironment().findGitDir().setMustExist(true).build();
         jgitRepo.getConfig().setString(ConfigConstants.CONFIG_REMOTE_SECTION, "origin", "url",
            repoFolder.getRoot().getAbsolutePath() + File.separator + ".git");
         jgitRepo.getConfig().save();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
      repoArtifact = TestUtil.createSimpleArtifact(CoreArtifactTypes.GitRepository, "Demo Repo", branch);
      repoArtifact.addAttribute(CoreAttributeTypes.FileSystemPath, remoteFolder.getRoot().getAbsolutePath());
      repoArtifact.persist(getClass().getSimpleName());
   }

   @After
   public void cleanup() {
      git.getRepository().close();
      git.close();
      repoFolder.delete();
      remoteFolder.delete();
      repoArtifact.purgeFromBranch();
   }

   @Test
   public void testGetBranches() {
      File fileToCommit;
      try {
         fileToCommit = repoFolder.newFile();

         git.add().addFilepattern(fileToCommit.getName()).call();
         RevCommit startCommit = git.commit().setMessage("getBranches commit one").call();

         git.branchCreate().setName("newBranch").setStartPoint(startCommit).call();
         git.commit().setMessage("creatingNewBranch").call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }

      List<String> branches = gitEp.getRemoteBranches(branch, Git_Repo.getName());
      Assert.assertTrue(branches.contains("newBranch"));
   }

   @Test
   public void testGitChangeIdBetweenTags() {

      File fileToCommit;
      String commitSHA;
      try {
         fileToCommit = repoFolder.newFile();

         git.add().addFilepattern(fileToCommit.getName()).call();
         git.commit().setMessage("Initial commit").call();
         git.tag().setName("tagA").call();

         fileToCommit = repoFolder.newFile();
         git.add().addFilepattern(fileToCommit.getName()).call();
         RevCommit taggedChangeIdCommit = git.commit().setMessage("Second commit").setInsertChangeId(true).call();
         changeIdMatcher.reset(taggedChangeIdCommit.getFullMessage()).find();

         fileToCommit = repoFolder.newFile();
         git.add().addFilepattern(fileToCommit.getName()).call();
         RevCommit taggedShaCommit = git.commit().setMessage("Second commit").call();

         git.tag().setName("tagB").call();
         commitSHA = taggedShaCommit.getId().name();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }

      List<String> changeIds = gitEp.getChangeIdBetweenTags(branch, Git_Repo.getName(), "tagA", "tagB");
      Assert.assertTrue(changeIds.contains(changeIdMatcher.group(1)));
      Assert.assertTrue(changeIds.contains(commitSHA));
   }
}
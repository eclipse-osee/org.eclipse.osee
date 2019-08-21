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
package org.eclipse.osee.define.rest;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GitCommit;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GitRepository;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.DefaultTrackingBranch;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.FileSystemPath;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitChangeId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitCommitAuthorDate;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.RepositoryUrl;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Git_Repository_Commit;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.NetRCCredentialsProvider;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.eclipse.osee.define.api.GitOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.SystemPreferences;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Ryan D. Brooks
 */
public final class GitOperationsImpl implements GitOperations {
   private final OrcsApi orcsApi;
   private final QueryFactory queryFactory;
   private final SystemPreferences systemPrefs;
   private final Map<String, ArtifactId> pathToCodeunitMap = new HashMap<>(10000);

   private static final Pattern changeIdPattern = Pattern.compile("\\s+Change-Id: (I\\w{40})");
   private final Matcher changeIdMatcher = changeIdPattern.matcher("");

   public GitOperationsImpl(OrcsApi orcsApi, SystemPreferences systemPrefs) {
      this.orcsApi = orcsApi;
      this.queryFactory = orcsApi.getQueryFactory();
      this.systemPrefs = systemPrefs;
   }

   @Override
   public ArtifactToken getCommitArtifactId(BranchId branch, String changeId) {
      List<ArtifactReadable> commits =
         queryFactory.fromBranch(branch).andAttributeIs(GitChangeId, changeId).andTypeEquals(
            GitCommit).getResults().getList();

      ArtifactToken latestCommit = ArtifactToken.SENTINEL;
      Date lastestAuthorDate = new Date(0);
      for (ArtifactReadable commit : commits) {
         Date authorDate = commit.getSoleAttributeValue(GitCommitAuthorDate);
         if (authorDate.after(lastestAuthorDate)) {
            lastestAuthorDate = authorDate;
            latestCommit = commit;
         }
      }
      return latestCommit;
   }

   @Override
   public ArtifactReadable getRepoArtifact(BranchId branch, String repositoryName) {
      return queryFactory.fromBranch(branch).andNameEquals(repositoryName).andTypeEquals(
         CoreArtifactTypes.GitRepository).getArtifact();
   }

   private Repository getLocalRepoReference(String repoPath) {
      File gitDirPath = new File(repoPath + File.separator + ".git");
      try {
         return new FileRepositoryBuilder().setGitDir(gitDirPath).readEnvironment().findGitDir().setMustExist(
            true).build();
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public void fetch(ArtifactReadable repoArtifact, String passphrase) {
      Repository jgitRepo = getLocalRepoReference(repoArtifact.getSoleAttributeValue(FileSystemPath));
      fetch(jgitRepo, passphrase);
   }

   private void fetch(Repository localRepo, String passphrase) {
      try (Git git = new Git(localRepo)) {

         FetchCommand fetchCommand = git.fetch().setCheckFetchedObjects(true).setTagOpt(TagOpt.NO_TAGS);

         configurateAuthentication(localRepo, fetchCommand, passphrase);

         FetchResult result = fetchCommand.call();
      } catch (GitAPIException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private void configurateAuthentication(Repository repo, TransportCommand<?, ?> transportCommand, String passphrase) {
      String gitRepoUrl = repo.getConfig().getString(ConfigConstants.CONFIG_REMOTE_SECTION, "origin", "url");
      configurateAuthentication(gitRepoUrl, transportCommand, passphrase);
   }

   private void configurateAuthentication(String gitRepoUrl, TransportCommand<?, ?> transportCommand, String passphrase) {
      if (gitRepoUrl.startsWith("ssh")) {
         configureSsh(transportCommand, passphrase);
      } else {
         transportCommand.setCredentialsProvider(new NetRCCredentialsProvider());
      }
   }

   private void configureSsh(TransportCommand<?, ?> transportCommand, String passphrase) {
      SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
         @Override
         protected void configure(Host host, Session session) {
            session.setPassword(passphrase);
         }

         @Override
         protected JSch createDefaultJSch(FS fs) throws JSchException {
            JSch defaultJSch = super.createDefaultJSch(fs);
            defaultJSch.addIdentity("~/.ssh/id_rsa", passphrase);
            return defaultJSch;
         }
      };

      transportCommand.setTransportConfigCallback(new TransportConfigCallback() {
         @Override
         public void configure(Transport transport) {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(sshSessionFactory);
         }
      });
   }

   @Override
   public ArtifactId trackGitBranch(String gitRepoUrl, BranchId branch, UserId account, String gitBranchName, boolean clone, String passphrase) {
      ArtifactReadable repoArtifact = clone(gitRepoUrl, branch, account, clone, passphrase);
      return updateGitTrackingBranch(branch, repoArtifact, account, gitBranchName, !clone, passphrase, true);
   }

   @Override
   public ArtifactId updateGitTrackingBranch(BranchId branch, ArtifactReadable repoArtifact, UserId account, String gitBranchName, boolean fetch, String passphrase, boolean initialImport) {
      Repository jgitRepo = getLocalRepoReference(repoArtifact.getSoleAttributeValue(FileSystemPath));
      if (fetch) {
         fetch(jgitRepo, passphrase);
      }
      if (gitBranchName == null) {
         gitBranchName = repoArtifact.getSoleAttributeValue(DefaultTrackingBranch);
      }

      try {
         String fromString = "remotes/origin/" + gitBranchName;
         ObjectId from = jgitRepo.resolve(fromString);
         if (from == null) {
            throw new OseeStateException("Failed to resolve commit [%s]", fromString);
         }

         ObjectId to = null;
         ArtifactReadable latestCommit =
            repoArtifact.getRelated(Git_Repository_Commit).getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
         if (latestCommit.isValid()) {
            String latestImportedSHA = latestCommit.getSoleAttributeValue(CoreAttributeTypes.GitCommitSha);
            to = ObjectId.fromString(latestImportedSHA);
            if (to == null) {
               throw new OseeStateException("Failed to resolve commit [%s]", latestImportedSHA);
            }
         }

         List<ArtifactToken> currentCommits =
            queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.CodeUnit).asArtifactTokens();
         for (ArtifactToken singleCommit : currentCommits) {
            pathToCodeunitMap.put(singleCommit.getName(), singleCommit);
         }

         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(repoArtifact.getBranch(), account,
            "updateGitTrackingBranch repo [" + repoArtifact + "]");
         HistoryImportStrategy importStrategy =
            new FastHistoryStrategy(repoArtifact, orcsApi, tx, initialImport, pathToCodeunitMap);
         walkTree(repoArtifact, jgitRepo, to, from, repoArtifact.getBranch(), account, importStrategy);
      } catch (RevisionSyntaxException | IOException ex) {
         throw OseeCoreException.wrap(ex);
      }

      return repoArtifact;
   }

   public ArtifactReadable clone(String gitRepoUrl, BranchId branch, UserId account, boolean clone, String passphrase) {
      String serverDataLocation = systemPrefs.getValue(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      String repoName = gitRepoUrl.substring(gitRepoUrl.lastIndexOf('/') + 1).replaceAll("\\.git$", "");
      File localPath = new File(serverDataLocation + File.separator + "git", repoName);

      if (clone) {
         CloneCommand jgitClone = Git.cloneRepository().setURI(gitRepoUrl).setDirectory(localPath).setNoCheckout(true);
         configurateAuthentication(gitRepoUrl, jgitClone, passphrase);
         try {
            jgitClone.call();
         } catch (GitAPIException ex) {
            throw OseeCoreException.wrap(ex);
         }
      }

      if (queryFactory.fromBranch(branch).andNameEquals(repoName).andTypeEquals(GitRepository).exists()) {
         throw new OseeStateException("A repository named %s already exists on branch %s", repoName, branch);
      }

      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "GitOperationsImpl.createGitRepository()");

      ArtifactId repoArtifact = tx.createArtifact(CoreArtifactTokens.GitRepoFolder, GitRepository, repoName);
      tx.setSoleAttributeValue(repoArtifact, RepositoryUrl, gitRepoUrl);
      try {
         tx.setSoleAttributeValue(repoArtifact, FileSystemPath, localPath.getCanonicalPath());
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
      tx.commit();

      return queryFactory.fromBranch(branch).andId(repoArtifact).getArtifact();
   }

   private TransactionToken walkTree(ArtifactReadable repoArtifact, Repository jgitRepo, ObjectId to, ObjectId from, BranchId branch, UserId account, HistoryImportStrategy importStrategy) {

      try (RevWalk revWalk = new RevWalk(jgitRepo)) {

         revWalk.markStart(revWalk.parseCommit(from)); //newest commit

         if (to != null) {
            RevCommit toRev = revWalk.parseCommit(to);
            revWalk.markUninteresting(toRev); // oldest commit - the last one we previously imported
         }
         revWalk.sort(RevSort.TOPO, true);
         revWalk.sort(RevSort.REVERSE, true);

         DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
         df.setRepository(jgitRepo);
         df.setDiffComparator(RawTextComparator.DEFAULT);
         df.setDetectRenames(true);

         setPathFilter(repoArtifact, df);

         ArtifactId lastValidCommit = ArtifactId.SENTINEL;
         ObjectReader objectReader = revWalk.getObjectReader();
         for (RevCommit revCommit : revWalk) {
            ArtifactId lastCommit =
               parseGitCommit(objectReader, df, repoArtifact, revCommit, branch, account, importStrategy);
            if (lastCommit.isValid()) {
               lastValidCommit = lastCommit;
            }
         }

         if (lastValidCommit.isValid()) {
            TransactionBuilder tx = importStrategy.getTransactionBuilder(orcsApi, branch, account);
            tx.unrelateFromAll(Git_Repository_Commit.getOpposite(), repoArtifact);
            tx.relate(repoArtifact, Git_Repository_Commit, lastValidCommit);
            importStrategy.finishGitCommit(tx);
         }
         return importStrategy.finishImport();
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private void setPathFilter(ArtifactReadable repoArtifact, DiffFormatter df) {
      List<String> paths = repoArtifact.getAttributeValues(CoreAttributeTypes.ExcludePath);
      if (!paths.isEmpty()) {
         List<TreeFilter> filters = new ArrayList<>();
         for (String path : paths) {
            filters.add(PathFilter.create(path));
         }
         if (filters.size() > 1) {
            df.setPathFilter(AndTreeFilter.create(filters).negate());
         } else {
            df.setPathFilter(filters.get(0).negate());
         }
      }
   }

   private ArtifactId parseGitCommit(ObjectReader objectReader, DiffFormatter df, ArtifactId repoArtifact, RevCommit revCommit, BranchId branch, UserId account, HistoryImportStrategy importStrategy) {
      try {
         TransactionBuilder tx = importStrategy.getTransactionBuilder(orcsApi, branch, account);

         String commitSHA = revCommit.getId().name();

         String commitId;
         if (changeIdMatcher.reset(revCommit.getFullMessage()).find()) {
            String changeId = changeIdMatcher.group(1);
            commitId = changeId;

            if (importStrategy.hasChangeIdAlredyImported(changeId)) {
               return ArtifactId.SENTINEL;
            }
         } else {
            commitId = commitSHA;
         }

         ArtifactId commitArtifact = tx.createArtifact(GitCommit, revCommit.getShortMessage());
         tx.setSoleAttributeValue(commitArtifact, CoreAttributeTypes.GitCommitSha, commitSHA);
         tx.setSoleAttributeValue(commitArtifact, CoreAttributeTypes.UserArtifactId, SystemUser.OseeSystem); //TODO: this must convert author to the corresponding user artifact
         tx.setSoleAttributeValue(commitArtifact, CoreAttributeTypes.GitCommitAuthorDate,
            revCommit.getAuthorIdent().getWhen());
         tx.setSoleAttributeValue(commitArtifact, CoreAttributeTypes.GitCommitMessage, revCommit.getFullMessage());

         tx.setSoleAttributeValue(commitArtifact, GitChangeId, commitId);

         importFileChanges(objectReader, df, repoArtifact, revCommit, commitSHA, commitArtifact, branch, tx,
            importStrategy);

         importStrategy.finishGitCommit(tx);
         return commitArtifact;
      } finally {
         revCommit.disposeBody();
      }
   }

   private void importFileChanges(ObjectReader objectReader, DiffFormatter df, ArtifactId repoArtifact, RevCommit revCommit, String commitSHA, ArtifactId commitArtifact, BranchId branch, TransactionBuilder tx, HistoryImportStrategy importStrategy) {
      if (revCommit.getParents().length > 1) {
         return;
      }

      RevTree parentTree = revCommit.getParentCount() > 0 ? revCommit.getParent(0).getTree() : null;
      List<DiffEntry> diffs = null;
      try {
         diffs = df.scan(parentTree, revCommit.getTree());

         for (DiffEntry entry : diffs) {
            ChangeType changeType = entry.getChangeType();
            String path = entry.getOldPath();
            String newPath = entry.getNewPath();

            ArtifactId codeUnit = importStrategy.getCodeUnit(branch, tx, commitSHA, changeType, path, newPath);
            if (codeUnit.isValid()) {
               importStrategy.handleCodeUnit(branch, codeUnit, tx, repoArtifact, commitArtifact, changeType);
            }
         }
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }
}
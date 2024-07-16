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

import static org.eclipse.osee.define.api.DefineTupleTypes.GitLatest;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.DefaultHierarchyRoot;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GitCommit;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.GitRepository;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.DefaultTrackingBranch;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.FileSystemPath;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitChangeId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitCommitAuthorDate;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.RepositoryUrl;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.GitRepositoryCommit_GitCommit;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
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
import org.eclipse.jgit.lib.Ref;
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
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.SystemProperties;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Ryan D. Brooks
 */
public final class GitOperationsImpl implements GitOperations {
   private final OrcsApi orcsApi;
   private final QueryFactory queryFactory;
   private final SystemProperties systemPrefs;
   private final Map<String, ArtifactId> pathToCodeunitReferenceMap = new HashMap<>();

   private static final Pattern changeIdPattern = Pattern.compile("\\s+Change-Id: (I\\w{40})");
   private final Matcher changeIdMatcher = changeIdPattern.matcher("");

   public GitOperationsImpl(OrcsApi orcsApi, SystemProperties systemPrefs) {
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
         Date authorDate;
         try {
            authorDate = new SimpleDateFormat().parse(commit.getSoleAttributeValue(GitCommitAuthorDate));
            if (authorDate.after(lastestAuthorDate)) {
               lastestAuthorDate = authorDate;
               latestCommit = commit;
            }
         } catch (ParseException ex) {
            ex.printStackTrace();
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

         FetchCommand fetchCommand = git.fetch().setCheckFetchedObjects(true).setTagOpt(TagOpt.FETCH_TAGS);

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
      ArtifactReadable repoArtifact = clone(gitRepoUrl, branch, account, gitBranchName, clone, passphrase);
      return updateGitTrackingBranch(branch, repoArtifact, account, gitBranchName, !clone, passphrase, true, false);
   }

   @Override
   public ArtifactId updateGitTrackingBranch(BranchId branch, ArtifactReadable repoArtifact, UserId account, String gitBranchName, boolean fetch, String passphrase, boolean initialImport, boolean shallowImport) {
      Repository jgitRepo = getLocalRepoReference(repoArtifact.getSoleAttributeValue(FileSystemPath));
      if (fetch) {
         fetch(jgitRepo, passphrase);
      }
      if (gitBranchName == null) {
         gitBranchName = repoArtifact.getSoleAttributeValue(DefaultTrackingBranch);
      }

      try {
         String fromString = gitBranchName;
         if (!gitBranchName.contains("/")) {
            fromString = "remotes/origin/" + gitBranchName;
         }
         ObjectId from = jgitRepo.resolve(fromString);
         if (from == null) {
            throw new OseeStateException("Failed to resolve commit [%s]", fromString);
         }

         ObjectId to = null;
         ArtifactReadable latestCommit =
            repoArtifact.getRelated(GitRepositoryCommit_GitCommit).getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
         if (latestCommit.isValid()) {
            String latestImportedSHA = latestCommit.getSoleAttributeValue(CoreAttributeTypes.GitCommitSha);
            to = ObjectId.fromString(latestImportedSHA);
            if (to == null) {
               throw new OseeStateException("Failed to resolve commit [%s]", latestImportedSHA);
            }
         }
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(repoArtifact.getBranch(), account,
            "updateGitTrackingBranch repo [" + repoArtifact + "]");
         List<ArtifactReadable> currentCommits =
            queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.CodeUnit).andRelatedRecursive(
               CoreRelationTypes.DefaultHierarchical_Child, repoArtifact).asArtifacts();
         for (ArtifactReadable singleCommit : currentCommits) {
            String fullPathName = "";
            try {
               fullPathName = singleCommit.getSoleAttributeAsString(CoreAttributeTypes.FileSystemPath);
            } catch (Exception e) {
               fullPathName = setFullPathName(branch, singleCommit, repoArtifact);
               tx.setSoleAttributeFromString(singleCommit, CoreAttributeTypes.FileSystemPath, fullPathName);
            }
            if (fullPathName.isEmpty()) {
               throw new OseeArgumentException(
                  "Attribute FileSystemPath on code unit %s - art id [%d] is missing and cannot be determined",
                  singleCommit.getName(), singleCommit.getId());
            }
            pathToCodeunitReferenceMap.put(fullPathName, singleCommit);
         }
         HistoryImportStrategy importStrategy = new FastHistoryStrategy(repoArtifact, orcsApi, tx, initialImport,
            shallowImport, pathToCodeunitReferenceMap);
         walkTree(repoArtifact, jgitRepo, to, from, branch, account, importStrategy, shallowImport);
      } catch (RevisionSyntaxException | IOException ex) {
         throw OseeCoreException.wrap(ex);
      }

      return repoArtifact;
   }

   @Override
   public List<String> getChangeIdBetweenTags(BranchId branch, ArtifactReadable repoArtifact, String startTag, String endTag) {

      Repository jgitRepo = getLocalRepoReference(repoArtifact.getSoleAttributeValue(FileSystemPath));
      /* fetch second arg (passPhrase) provide a key or password to enter repo. Here we have no pass phrase. */
      fetch(jgitRepo, "");
      try (Git git = new Git(jgitRepo)) {

         Ref tag1 = git.getRepository().exactRef("refs/tags/" + startTag);
         Ref tag2 = git.getRepository().exactRef("refs/tags/" + endTag);
         Iterable<RevCommit> commits = git.log().addRange(tag1.getPeeledObjectId(), tag2.getPeeledObjectId()).call();

         // parse through commits to get specific tags with specific commits and change ids
         List<String> changeIdList = new ArrayList<>();
         for (RevCommit revCommit : commits) {
            if (revCommit.getShortMessage() != "") {
               String commitSHA = revCommit.getId().name();

               if (changeIdMatcher.reset(revCommit.getFullMessage()).find()) {
                  String changeId = changeIdMatcher.group(1);
                  changeIdList.add(changeId);
               } else {
                  changeIdList.add(commitSHA);
               }

            }
         }

         return changeIdList;

      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public List<String> getRemoteBranches(BranchId branch, ArtifactReadable repoArtifact) {
      Repository jgitRepo = getLocalRepoReference(repoArtifact.getSoleAttributeValue(FileSystemPath));
      Collection<Ref> refs;
      List<String> allGitBranches = new ArrayList<>();
      fetch(jgitRepo, "");
      try {
         refs = new Git(jgitRepo).branchList().setListMode(ListMode.ALL).call();
         for (Ref ref : refs) {
            allGitBranches.add(ref.getName().substring(ref.getName().lastIndexOf("/") + 1, ref.getName().length()));
         }
      } catch (GitAPIException ex) {
         ex.printStackTrace();
      }
      return allGitBranches;
   }

   public ArtifactReadable clone(String gitRepoUrl, BranchId branch, UserId account, String gitBranchName, boolean clone, String passphrase) {
      String serverDataLocation = systemPrefs.getValue(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      String repoName = gitRepoUrl.substring(gitRepoUrl.lastIndexOf('/') + 1).replaceAll("\\.git$", "");
      File localPath = new File(serverDataLocation + File.separator + "git", repoName);
      String branchToClone = "refs/heads/" + gitBranchName;

      if (clone) {
         CloneCommand jgitClone = Git.cloneRepository().setURI(gitRepoUrl).setDirectory(localPath).setBranchesToClone(
            Arrays.asList(branchToClone)).setBranch(branchToClone).setNoCheckout(true);
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

      if (!queryFactory.fromBranch(branch).andNameEquals("Git Repositories").andTypeEquals(Folder).exists()) {
         tx.createArtifact(DefaultHierarchyRoot, CoreArtifactTokens.GitRepoFolder);
      }

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

   private TransactionToken walkTree(ArtifactReadable repoArtifact, Repository jgitRepo, ObjectId to, ObjectId from, BranchId branch, UserId account, HistoryImportStrategy importStrategy, boolean shallowImport) {

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
            ArtifactId lastCommit = parseGitCommit(objectReader, df, repoArtifact, revCommit, branch, account,
               importStrategy, shallowImport);
            if (lastCommit.isValid()) {
               lastValidCommit = lastCommit;
            }
         }

         if (lastValidCommit.isValid() && !shallowImport) {
            TransactionBuilder tx = importStrategy.getTransactionBuilder(orcsApi, branch, account);
            tx.unrelateFromAll(GitRepositoryCommit_GitCommit.getOpposite(), repoArtifact);
            tx.relate(repoArtifact, GitRepositoryCommit_GitCommit, lastValidCommit);
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

   private ArtifactId createCommitArtifact(RevCommit revCommit, TransactionBuilder tx, BranchId branch) {

      String commitSHA = revCommit.getId().name();

      String commitId;
      if (changeIdMatcher.reset(revCommit.getFullMessage()).find()) {
         String changeId = changeIdMatcher.group(1);
         commitId = changeId;

      } else {
         commitId = commitSHA;
      }

      try {
         return queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.GitCommit).andNameEquals(
            revCommit.getShortMessage()).asArtifact();
      } catch (Exception ex) {
         ArtifactId commitArtifact = tx.createArtifact(GitCommit, revCommit.getShortMessage());
         tx.setSoleAttributeValue(commitArtifact, CoreAttributeTypes.GitCommitSha, commitSHA);
         tx.setSoleAttributeValue(commitArtifact, CoreAttributeTypes.UserArtifactId, orcsApi.userService().getUser());
         tx.setSoleAttributeValue(commitArtifact, CoreAttributeTypes.GitCommitAuthorDate,
            revCommit.getAuthorIdent().getWhen());
         tx.setSoleAttributeValue(commitArtifact, CoreAttributeTypes.GitCommitMessage, revCommit.getFullMessage());

         tx.setSoleAttributeValue(commitArtifact, GitChangeId, commitId);
         return commitArtifact;

      }
   }

   private ArtifactId parseGitCommit(ObjectReader objectReader, DiffFormatter df, ArtifactReadable repoArtifact, RevCommit revCommit, BranchId branch, UserId account, HistoryImportStrategy importStrategy, boolean shallowImport) {
      try {
         TransactionBuilder tx = importStrategy.getTransactionBuilder(orcsApi, branch, account);
         ArtifactId commitArtifact = ArtifactId.SENTINEL;

         if (!shallowImport) {
            commitArtifact = createCommitArtifact(revCommit, tx, branch);
         }
         importFileChanges(objectReader, df, repoArtifact, revCommit, revCommit.getId().name(), commitArtifact, branch,
            tx, importStrategy);

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
            if (commitArtifact.isValid()) {
               if (codeUnit.isValid()) {
                  importStrategy.handleCodeUnit(branch, codeUnit, tx, repoArtifact, commitArtifact, changeType,
                     newPath);
               } else {
                  ArtifactId[] commitWraper = new ArtifactId[] {ArtifactId.SENTINEL};
                  codeUnit = importStrategy.findCodeUnit(repoArtifact, newPath);
                  orcsApi.getQueryFactory().tupleQuery().getTuple4E3E4FromE1E2(GitLatest, branch, repoArtifact,
                     codeUnit, (changeCommit, ignore) -> commitWraper[0] = changeCommit);
                  if (!commitWraper[0].isValid() && codeUnit.isValid()) {
                     importStrategy.handleCodeUnit(branch, codeUnit, tx, repoArtifact, commitArtifact, changeType,
                        newPath);
                  }

               }
            }
         }
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private String setFullPathName(BranchId branch, ArtifactReadable singleCommit, ArtifactReadable repoArtifact) {
      ArtifactReadable art = singleCommit;
      String wholePath = art.getName();
      while (!art.getParent().equals(repoArtifact)) {
         art =
            queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.Folder).andId(art.getParent()).asArtifact();
         wholePath = art.getName() + "/" + wholePath;
      }
      return wholePath;
   }
}
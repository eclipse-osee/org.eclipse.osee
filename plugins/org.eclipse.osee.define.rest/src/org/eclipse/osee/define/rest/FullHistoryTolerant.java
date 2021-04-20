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

import static org.eclipse.osee.define.api.DefineTupleTypes.GitCommitFile;
import static org.eclipse.osee.define.api.DefineTupleTypes.GitLatest;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitChangeId;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * This import strategy is tolerant because does not exception when unexpected state is identified. Once know cause for
 * such a case is a Git repository that has a single commit multiple times in its history causing a file to be added are
 * it already exists
 *
 * @author Ryan D. Brooks
 */
public class FullHistoryTolerant implements HistoryImportStrategy {
   protected final Map<String, ArtifactId> pathToCodeunitMap = new HashMap<>(10000);
   protected final Map<String, ArtifactId> pathToCodeunitReferenceMap;
   private final TupleQuery tupleQuery;
   protected final ArtifactReadable repoArtifact;
   protected final BranchId branch;
   protected final QueryFactory queryFactory;

   private final Map<String, ArtifactId> existingCodeUnitPath = new ConcurrentHashMap<>();

   public FullHistoryTolerant(ArtifactReadable repoArtifact, OrcsApi orcsApi, Map<String, ArtifactId> pathToCodeunitReferenceMap) {
      this.repoArtifact = repoArtifact;
      this.branch = repoArtifact.getBranch();
      queryFactory = orcsApi.getQueryFactory();
      this.tupleQuery = queryFactory.tupleQuery();
      this.pathToCodeunitReferenceMap = pathToCodeunitReferenceMap;
      initExistingCodeUnitPath();
   }

   @Override
   public ArtifactId getCodeUnit(BranchId branch, TransactionBuilder tx, String commitSHA, ChangeType changeType, String path, String newPath) {
      ArtifactId codeUnit = ArtifactId.SENTINEL;

      if (changeType == ChangeType.MODIFY) {
         // no change to the code unit (we add the GitCommitFile tuple entry elsewhere)
         codeUnit = findCodeUnit(repoArtifact, path);
      } else if (changeType == ChangeType.ADD || changeType == ChangeType.COPY) {
         codeUnit = findCodeUnit(repoArtifact, newPath);
         if (codeUnit.isValid()) {
            System.out.printf("commit [%s] adds code unit [%s] but found existing code unit [%s]\n", commitSHA, newPath,
               codeUnit);
            return ArtifactId.SENTINEL;
         } else {
            codeUnit = createCodeUnit(tx, newPath);
            pathToCodeunitMap.put(newPath, codeUnit);
         }
      } else if (changeType == ChangeType.DELETE) {
         codeUnit = findCodeUnit(repoArtifact, path);
         if (codeUnit.isValid()) {
            tx.deleteArtifact(codeUnit);
            pathToCodeunitReferenceMap.remove(path);
            pathToCodeunitMap.remove(path);
         } else {
            System.out.printf("didn't find %s for deletion in commit %s\n", path, commitSHA);
         }
      } else if (changeType == ChangeType.RENAME) {
         codeUnit = findCodeUnit(repoArtifact, path);
         if (codeUnit.isValid()) {
            if (Strings.isValid(newPath)) {
               tx.setName(codeUnit, getCodeUnitName(newPath));
               tx.setSoleAttributeValue(codeUnit, CoreAttributeTypes.FileSystemPath, newPath);
               pathToCodeunitReferenceMap.remove(path);
               pathToCodeunitMap.remove(path);
               pathToCodeunitMap.put(newPath, codeUnit);
            }
         } else {
            System.out.printf("didn't find in commit [%s] for rename from [%s] to [%s]\n", commitSHA, path, newPath);
            if (Strings.isValid(newPath)) {
               codeUnit = createCodeUnit(tx, newPath);
               pathToCodeunitMap.put(newPath, codeUnit);
            }
         }
      } else {
         System.out.printf("unexpected change type [%s] on path [%s]\n", changeType, path);
      }
      return codeUnit;
   }

   @Override
   public ArtifactId findCodeUnit(ArtifactId repository, String path) {
      ArtifactId codeUnit = ArtifactId.SENTINEL;
      if (pathToCodeunitMap.containsKey(path)) {
         codeUnit = pathToCodeunitMap.get(path);
      } else if (pathToCodeunitReferenceMap.containsKey(path)) {
         codeUnit = pathToCodeunitReferenceMap.get(path);
      }
      if (codeUnit == null) {
         return ArtifactId.SENTINEL;
      }
      return codeUnit;
   }

   private void initExistingCodeUnitPath() {
      List<ArtifactReadable> existingFolders =
         queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.Folder).andRelatedRecursive(
            CoreRelationTypes.DefaultHierarchical_Child, repoArtifact).asArtifacts();
      for (ArtifactReadable art : existingFolders) {
         String wholePath = art.getName();
         ArtifactReadable parentArt = art;
         while (!parentArt.getParent().equals(repoArtifact) && parentArt.isValid()) {
            parentArt = parentArt.getParent();
            wholePath = parentArt.getName() + "/" + wholePath;
         }
         if (!existingCodeUnitPath.containsKey(wholePath)) {
            existingCodeUnitPath.put(wholePath, art);
         }
      }
   }

   private ArtifactId createCodeUnit(TransactionBuilder tx, String newPath) {
      return editCodeUnitPath(tx, newPath, true);
   }

   private ArtifactId editCodeUnitPath(TransactionBuilder tx, String newPath, boolean newCodeUnit) {
      ArtifactId folder = repoArtifact;
      String codeUnitName = "";
      String wholePath = "";
      List<String> path = Lists.newArrayList(Splitter.on("/").split(newPath));
      if (!(path.size() > 0)) {
         return ArtifactId.SENTINEL;
      }
      codeUnitName = getCodeUnitName(newPath);
      path.remove(path.size() - 1);

      for (String newFolder : path) {
         wholePath += newFolder;
         if (!existingCodeUnitPath.containsKey(wholePath)) {
            folder = tx.createArtifact(folder, CoreArtifactTypes.Folder, newFolder);
            existingCodeUnitPath.put(wholePath, folder);
         } else {
            folder = existingCodeUnitPath.get(wholePath);
         }
         wholePath += "/";
      }
      if (newCodeUnit) {
         ArtifactId codeUnit = tx.createArtifact(folder, CoreArtifactTypes.CodeUnit, codeUnitName);
         tx.setSoleAttributeFromString(codeUnit, CoreAttributeTypes.FileSystemPath, newPath);
         return codeUnit;
      }
      return folder;
   }

   private boolean isPathRenamed(String path, String newPath) {
      if (path.substring(0, path.lastIndexOf("/")).equals(newPath.substring(0, newPath.lastIndexOf("/")))) {
         return true;
      }
      return false;
   }

   private String getCodeUnitName(String newPath) {
      String codeUnitName = "";
      List<String> path = Lists.newArrayList(Splitter.on("/").split(newPath));
      codeUnitName = path.get(path.size() - 1);
      return codeUnitName;
   }

   @Override
   public void handleCodeUnit(BranchId branch, ArtifactId codeUnit, TransactionBuilder tx, ArtifactId repository, ArtifactId commit, ChangeType changeType, String path) {
      tx.addTuple4(GitCommitFile, repository, codeUnit, commit, changeType);

      ArtifactId[] commitWraper = new ArtifactId[] {ArtifactId.SENTINEL};
      tupleQuery.getTuple4E3E4FromE1E2(GitLatest, branch, repository, codeUnit,
         (ignore, baselineCommit) -> commitWraper[0] = baselineCommit);

      tx.deleteTuple4ByE1E2(GitLatest, repository, codeUnit);

      tx.addTuple4(GitLatest, repository, codeUnit, commit, commitWraper[0]);
   }

   @Override
   public TransactionBuilder getTransactionBuilder(OrcsApi orcsApi, BranchId branch, UserId account) {
      return orcsApi.getTransactionFactory().createTransaction(branch, account,
         "TraceabilityOperationsImpl.parseGitHistory repo [" + repoArtifact.getIdString() + "]");
   }

   @Override
   public void finishGitCommit(TransactionBuilder tx) {
      tx.commit();
   }

   @Override
   public TransactionToken finishImport() {
      return TransactionToken.SENTINEL;
   }

   @Override
   public boolean hasChangeIdAlredyImported(String changeId) {
      return queryFactory.fromBranch(branch).and(GitChangeId, changeId).exists();
   }
}
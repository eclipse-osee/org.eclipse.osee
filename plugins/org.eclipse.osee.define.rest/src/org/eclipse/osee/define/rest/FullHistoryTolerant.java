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

import static org.eclipse.osee.define.api.DefineTupleTypes.GitCommitFile;
import static org.eclipse.osee.define.api.DefineTupleTypes.GitLatest;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitChangeId;
import java.util.Map;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
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
   protected final Map<String, ArtifactId> pathToCodeunitMap;
   private final TupleQuery tupleQuery;
   protected final ArtifactToken repoArtifact;
   protected final BranchId branch;
   protected final QueryFactory queryFactory;

   public FullHistoryTolerant(ArtifactToken repoArtifact, OrcsApi orcsApi, Map<String, ArtifactId> pathToCodeunitMap) {
      this.repoArtifact = repoArtifact;
      this.branch = repoArtifact.getBranch();
      queryFactory = orcsApi.getQueryFactory();
      this.tupleQuery = queryFactory.tupleQuery();
      this.pathToCodeunitMap = pathToCodeunitMap;
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
            codeUnit = tx.createArtifact(CoreArtifactTypes.CodeUnit, newPath);
            pathToCodeunitMap.put(newPath, codeUnit);
         }
      } else if (changeType == ChangeType.DELETE) {
         codeUnit = findCodeUnit(repoArtifact, path);
         if (codeUnit.isValid()) {
            tx.deleteArtifact(codeUnit);
            pathToCodeunitMap.remove(path);
         } else {
            System.out.printf("didn't find %s for deletion in commit %s\n", path, commitSHA);
         }
      } else if (changeType == ChangeType.RENAME) {
         codeUnit = findCodeUnit(repoArtifact, path);
         if (codeUnit.isValid()) {
            if (Strings.isValid(newPath)) {
               tx.setName(codeUnit, newPath);
               pathToCodeunitMap.remove(path);
               pathToCodeunitMap.put(newPath, codeUnit);
            }
         } else {
            System.out.printf("didn't find in commit [%s] for rename from [%s] to [%s]\n", commitSHA, path, newPath);
            if (Strings.isValid(newPath)) {
               codeUnit = tx.createArtifact(CoreArtifactTypes.CodeUnit, newPath);
               pathToCodeunitMap.put(newPath, codeUnit);
            }
         }
      } else {
         System.out.printf("unexpected change type [%s] on path [%s]\n", changeType, path);
      }
      return codeUnit;
   }

   private ArtifactId findCodeUnit(ArtifactId repository, String path) {
      ArtifactId codeUnit = pathToCodeunitMap.get(path);
      if (codeUnit != null) {
         return codeUnit;
      }
      return ArtifactId.SENTINEL;
   }

   @Override
   public void handleCodeUnit(BranchId branch, ArtifactId codeUnit, TransactionBuilder tx, ArtifactId repository, ArtifactId commit, ChangeType changeType) {
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
      return queryFactory.fromBranch(branch).andAttributeIs(GitChangeId, changeId).exists();
   }
}
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

import static org.eclipse.osee.define.rest.api.DefineTupleTypes.GitLatest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Ryan D. Brooks
 */
public class FastHistoryStrategy extends FullHistoryTolerant {
   private final Map<ArtifactId, ArtifactId> codeunitToCommitMap = new HashMap<>(10000);
   private final TransactionBuilder tx;
   private final HashSet<String> changeIds = new HashSet<>();
   private final boolean initialImport;
   private final boolean shallowImport;

   public FastHistoryStrategy(ArtifactReadable repository, OrcsApi orcsApi, TransactionBuilder tx, boolean initialImport, boolean shallowImport, Map<String, ArtifactId> pathToCodeunitReferenceMap) {
      super(repository, orcsApi, pathToCodeunitReferenceMap);
      this.tx = tx;
      this.initialImport = initialImport;
      this.shallowImport = shallowImport;
   }

   @Override
   public void handleCodeUnit(BranchId branch, ArtifactId codeUnit, TransactionBuilder tx, ArtifactId repository, ArtifactId commit, ChangeType changeType, String path) {
      codeunitToCommitMap.put(codeUnit, commit);
      if (!pathToCodeunitMap.containsValue(
         codeUnit) && (changeType == ChangeType.ADD || changeType == ChangeType.COPY || changeType == ChangeType.RENAME)) {
         pathToCodeunitMap.put(path, codeUnit);
      }
   }

   @Override
   public TransactionBuilder getTransactionBuilder(OrcsApi orcsApi, BranchId branch) {
      return tx;
   }

   @Override
   public TransactionToken finishImport() {
      if (shallowImport) {
         return tx.commit();
      } else if (codeunitToCommitMap.isEmpty()) {
         return TransactionToken.SENTINEL;
      }
      pathToCodeunitMap.forEach((path, codeUnit) -> tx.addTuple4(GitLatest, repoArtifact, codeUnit,
         codeunitToCommitMap.get(codeUnit), ArtifactId.SENTINEL));
      return tx.commit();
   }

   @Override
   public void finishGitCommit(TransactionBuilder tx) {
      // only commit transaction in finishImport()
   }

   @Override
   public boolean hasChangeIdAlredyImported(String changeId) {
      boolean imported = changeIds.contains(changeId) || (!initialImport && super.hasChangeIdAlredyImported(changeId));
      changeIds.add(changeId);
      return imported;
   }
}
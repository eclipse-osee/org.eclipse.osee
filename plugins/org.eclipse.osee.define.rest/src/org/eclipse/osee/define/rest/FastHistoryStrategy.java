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

import static org.eclipse.osee.define.api.DefineTupleTypes.GitLatest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
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

   public FastHistoryStrategy(ArtifactToken repository, OrcsApi orcsApi, TransactionBuilder tx, boolean initialImport) {
      super(repository, orcsApi);
      this.tx = tx;
      this.initialImport = initialImport;
   }

   @Override
   public void handleCodeUnit(BranchId branch, ArtifactId codeUnit, TransactionBuilder tx, ArtifactId repository, ArtifactId commit, ChangeType changeType) {
      codeunitToCommitMap.put(codeUnit, commit);
   }

   @Override
   public TransactionBuilder getTransactionBuilder(OrcsApi orcsApi, BranchId branch, UserId account) {
      return tx;
   }

   @Override
   public TransactionToken finishImport() {
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
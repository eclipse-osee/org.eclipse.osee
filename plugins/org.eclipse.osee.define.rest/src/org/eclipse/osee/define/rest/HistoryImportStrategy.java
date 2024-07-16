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

import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Ryan D. Brooks
 */
public interface HistoryImportStrategy {
   /**
    * Possible status letters are (see https://git-scm.com/docs/git-status):<br/>
    * A: addition of a file<br/>
    * C: copy of a file into a new one<br/>
    * D: deletion of a file<br/>
    * M: modification of the contents or mode of a file<br/>
    * R: renaming of a file<br/>
    * T: change in the type of the file<br/>
    */
   ArtifactId getCodeUnit(BranchId branch, TransactionBuilder tx, String commitSHA, ChangeType changeType, String path, String newPath);

   boolean hasChangeIdAlredyImported(String changeId);

   void handleCodeUnit(BranchId branch, ArtifactId codeUnit, TransactionBuilder tx, ArtifactId repository, ArtifactId commit, ChangeType changeType, String path);

   ArtifactId findCodeUnit(ArtifactId repository, String path);

   TransactionBuilder getTransactionBuilder(OrcsApi orcsApi, BranchId branch);

   default boolean matchesChangeType(String changeType, char typeToMatch) {
      return ((changeType.length() == 1 || changeType.length() == 4) && changeType.charAt(
         0) == typeToMatch) || (changeType.length() == 2 && changeType.charAt(1) == typeToMatch);
   }

   void finishGitCommit(TransactionBuilder tx);

   TransactionToken finishImport();
}
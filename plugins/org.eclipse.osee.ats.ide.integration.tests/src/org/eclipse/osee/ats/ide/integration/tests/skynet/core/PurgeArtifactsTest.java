/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link PurgeArtifacts}
 *
 * @author Donald G. Dunne
 */
public class PurgeArtifactsTest {

   @Test
   public void test() {
      Artifact userGroupsArt = ArtifactQuery.getArtifactFromToken(CoreArtifactTokens.UserGroups);

      SkynetTransaction transaction =
         TransactionManager.createTransaction(CoreBranches.COMMON, getClass().getSimpleName());
      Artifact childFolder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, CoreBranches.COMMON);
      transaction.addArtifact(childFolder);
      userGroupsArt.addChild(childFolder);
      transaction.addArtifact(userGroupsArt);
      TransactionId transactionId = transaction.execute();

      ArtifactCache.deCache(userGroupsArt);

      userGroupsArt = ArtifactQuery.getArtifactFromToken(CoreArtifactTokens.UserGroups);
      Assert.assertFalse(userGroupsArt.isDirty());
      Assert.assertEquals(userGroupsArt, childFolder.getParent());
      Assert.assertTrue(userGroupsArt.getChildren().contains(childFolder));

      Collection<Change> changes = new ArrayList<>();

      IOperation operation = null;
      operation =
         ChangeManager.comparedToPreviousTx(TransactionToken.valueOf(transactionId, CoreBranches.COMMON), changes);
      Operations.executeWorkAndCheckStatus(operation);
      Assert.assertEquals(4, changes.size());

      PurgeArtifacts purge = new PurgeArtifacts(Collections.singleton(childFolder));
      purge.run(null);

      childFolder = ArtifactQuery.getArtifactOrNull(childFolder, DeletionFlag.INCLUDE_DELETED);
      Assert.assertNull(childFolder);

      changes.clear();
      boolean transactionGone = false;
      try {
         operation =
            ChangeManager.comparedToPreviousTx(TransactionToken.valueOf(transactionId, CoreBranches.COMMON), changes);
         Operations.executeWorkAndCheckStatus(operation);
      } catch (OseeCoreException ex) {
         if (ex.getLocalizedMessage().contains("No item found")) {
            transactionGone = true;
         }
      }
      if (!transactionGone) {
         Assert.assertEquals("All changes in transactions should be removed and transaction gone", 0, changes.size());
      }

   }
}

/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
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
import org.junit.Rule;
import org.junit.Test;

/**
 * Test for {@link PurgeArtifacts}
 *
 * @author Donald G. Dunne
 */
public class PurgeArtifactsTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

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
      Assert.assertEquals(3, changes.size());

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

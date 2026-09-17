/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.publishing;

import java.util.List;
import org.eclipse.osee.ats.ide.integration.tests.synchronization.TestUserRules;
import org.eclipse.osee.client.test.framework.ExitDatabaseInitializationRule;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.DataRightsClassificationClientRefresher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

/**
 * Integration test for {@link DataRightsClassificationClientRefresher}.
 * <p>
 * Verifies, through the real client query and type-registry path, that a footer-defined
 * classification name written to the common branch {@code DataRightsFooters} artifact becomes a valid
 * enum value on the client {@code CoreAttributeTypes.DataRightsClassification} after
 * {@code ensureRefresh}. This is what allows footer-derived classifications to appear in the client
 * attribute editor and mass editor pick-lists, whose valid values come from the client type registry.
 * <p>
 * The test resets the once-per-session guard via {@link DataRightsClassificationClientRefresher#reset()}
 * (the same reset the client cache-clear path invokes) so the read is deterministic regardless of test
 * ordering. It restores the footers artifact's original values on completion so it does not pollute
 * other tests.
 *
 * @author David W. Miller
 */

public class DataRightsClassificationClientRefresherTest {

   //@formatter:off
   @Rule
   public TestRule ruleChain =
      RuleChain
         .outerRule( new NotProductionDataStoreRule() )
         .around( new ExitDatabaseInitializationRule() )
         .around( TestUserRules.createInPublishingGroupTestRule() );
   //@formatter:on

   private static final String NOVEL_CLASSIFICATION = "Client Refresh Test Classification";

   private static final String FOOTER_VALUE =
      DataRightsClassificationClientRefresherTest.NOVEL_CLASSIFICATION + "\n<ftr>client refresh test footer</ftr>";

   @Test
   public void testFooterClassificationBecomesValidAfterClientRefresh() {

      Artifact footersArtifact = ArtifactQuery.getArtifactFromToken(CoreArtifactTokens.DataRightsFooters);

      List<String> originalValues = footersArtifact.getAttributeValues(CoreAttributeTypes.GeneralStringData);

      Assert.assertFalse(
         "Precondition: the novel classification must not already be a valid enum before the refresh.",
         CoreAttributeTypes.DataRightsClassification.isValidEnum(
            DataRightsClassificationClientRefresherTest.NOVEL_CLASSIFICATION));

      try {

         SkynetTransaction addTx = TransactionManager.createTransaction(CoreBranches.COMMON,
            "DataRightsClassificationClientRefresherTest add footer");
         footersArtifact.addAttribute(CoreAttributeTypes.GeneralStringData,
            DataRightsClassificationClientRefresherTest.FOOTER_VALUE);
         footersArtifact.persist(addTx);
         addTx.execute();

         DataRightsClassificationClientRefresher.reset();
         DataRightsClassificationClientRefresher.ensureRefreshed();

         Assert.assertTrue(
            "The footer-defined classification must be a valid enum on the client DataRightsClassification"
               + " after the client refresh.",
            CoreAttributeTypes.DataRightsClassification.isValidEnum(
               DataRightsClassificationClientRefresherTest.NOVEL_CLASSIFICATION));

      } finally {

         SkynetTransaction restoreTx = TransactionManager.createTransaction(CoreBranches.COMMON,
            "DataRightsClassificationClientRefresherTest restore footers");
         footersArtifact.setAttributeFromValues(CoreAttributeTypes.GeneralStringData, originalValues);
         footersArtifact.persist(restoreTx);
         restoreTx.execute();
      }
   }

}

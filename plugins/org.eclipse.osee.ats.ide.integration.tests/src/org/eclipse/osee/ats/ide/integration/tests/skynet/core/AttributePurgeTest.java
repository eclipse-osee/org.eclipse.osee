/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.Asserts;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeAttributes;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

/**
 * This test is intended to be run against a demo database. It tests the purge logic by counting the rows of the version
 * and txs tables, creating artifacts, changing them and then purging them. If it works properly, all rows should be
 * equal.
 *
 * @author Jeff C. Phillips
 */
public class AttributePurgeTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public MethodRule oseeHousekeepingRule = new OseeHousekeepingRule();

   private static final String[] TABLES = new String[] {"osee_attribute", "osee_txs"};

   @Test
   public void testPurge() throws Exception {
      Collection<Artifact> softArts = TestUtil.createSimpleArtifacts(CoreArtifactTypes.SoftwareRequirementMsWord, 10,
         getClass().getSimpleName(), SAW_Bld_2);
      TransactionManager.persistInTransaction("Test purge artifacts", softArts);

      Map<String, Integer> initialRowCount = TestUtil.getTableRowCounts(TABLES);

      // make more changes to artifacts
      for (Artifact softArt : softArts) {
         softArt.addAttribute(CoreAttributeTypes.StaticId, getClass().getSimpleName());
         softArt.persist(getClass().getName());
      }
      // Count rows and check that increased
      Asserts.assertThatIncreased(initialRowCount, TestUtil.getTableRowCounts(TABLES));

      Set<Attribute<?>> attributesToPurge = new HashSet<>();
      for (Artifact softArt : softArts) {
         attributesToPurge.addAll(softArt.getAttributes(CoreAttributeTypes.StaticId));
      }

      Operations.executeWorkAndCheckStatus(new PurgeAttributes(attributesToPurge));

      // Count rows and check that same as when began
      Asserts.assertThatEquals(initialRowCount, TestUtil.getTableRowCounts(TABLES));
   }

}

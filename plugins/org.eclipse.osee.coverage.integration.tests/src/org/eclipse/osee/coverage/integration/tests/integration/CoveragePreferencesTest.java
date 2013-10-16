/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.integration.tests.integration;

import static org.eclipse.osee.coverage.demo.CoverageChoice.OSEE_COVERAGE_DEMO;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeHousekeepingRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.coverage.integration.tests.integration.util.CoverageTestUtil;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePreferences;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class CoveragePreferencesTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_COVERAGE_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public OseeHousekeepingRule hk = new OseeHousekeepingRule();

   @BeforeClass
   public static void setUp() throws OseeCoreException {
      SkynetTransaction transaction = TransactionManager.createTransaction(CoverageTestUtil.getTestBranch(), "delete");
      for (Artifact artifact : ArtifactQuery.getArtifactListFromTypeAndName(CoreArtifactTypes.GeneralData,
         "Coverage Preferences", CoverageTestUtil.getTestBranch())) {
         artifact.deleteAndPersist(transaction);
      }
      transaction.execute();
   }

   private static Artifact getCoveragePrefArt() throws OseeCoreException {
      try {
         IOseeBranch branch = CoverageTestUtil.getTestBranch();
         Assert.assertNotNull(branch);
         return ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.GeneralData, "Coverage Preferences", branch);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return null;
   }

   @Test
   public void testCoveragePreferences() throws OseeCoreException {
      Assert.assertNull(getCoveragePrefArt());
      CoveragePreferences prefs = new CoveragePreferences(CoverageTestUtil.getTestBranch());
      Assert.assertNotNull(prefs);
      Assert.assertNull(prefs.getCoverageOptions());
      prefs.setCoverageOptions(CoverageOptionManagerDefault.instance().toXml());
      Assert.assertNotNull(getCoveragePrefArt());
      Assert.assertFalse(getCoveragePrefArt().isDirty());
      Assert.assertEquals(CoverageOptionManagerDefault.instance().toXml(), prefs.getCoverageOptions());
   }

}

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

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
public class CrossBranchRelationLinkTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private Artifact left, right;

   @Before
   public void setUp() {
      left = TestUtil.createSimpleArtifact(CoreArtifactTypes.Requirement, "Left", SAW_Bld_1);
      right = TestUtil.createSimpleArtifact(CoreArtifactTypes.Requirement, "Right", SAW_Bld_2);
      left.persist(getClass().getSimpleName());
      right.persist(getClass().getSimpleName());
   }

   @After
   public void tearDown() {
      left.purgeFromBranch();
      right.purgeFromBranch();
   }

   @Test(expected = OseeArgumentException.class)
   public void attemptCrossBranchLinkCreationTest() {
      left.addRelation(CoreRelationTypes.DefaultHierarchical_Child, right);
   }

}

/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.test.relation;

import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
public class CrossBranchRelationLinkTest {
   Branch branch1;
   Branch branch2;
   Artifact left, right;
   RelationType type;

   @Before
   public void setUp() throws OseeCoreException {
      branch1 = BranchManager.getBranch("SAW_Bld_1");
      branch2 = BranchManager.getBranch("SAW_Bld_2");
      left = FrameworkTestUtil.createSimpleArtifact("Requirement", "Left", branch1);
      right = FrameworkTestUtil.createSimpleArtifact("Requirement", "Right", branch2);
      left.persist();
      right.persist();
   }

   @Test(expected = OseeArgumentException.class)
   public void attemptCrossBranchLinkCreationTest() throws OseeCoreException {
      left.addRelation(CoreRelationTypes.Default_Hierarchical__Child, right);
   }

   @After
   public void tearDown() throws OseeCoreException {
      left.purgeFromBranch();
      right.purgeFromBranch();
   }
}

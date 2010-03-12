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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.test.util.FrameworkTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
public class LoadDeletedRelationTest {
   Branch branch;
   Artifact left, right;
   RelationType type;

   @Before
   public void setUp() throws OseeCoreException {
      branch = BranchManager.getBranch("SAW_Bld_2");
      left = FrameworkTestUtil.createSimpleArtifact("Requirement", "Left", branch);
      right = FrameworkTestUtil.createSimpleArtifact("Requirement", "Right", branch);
      left.persist();
      right.persist();
      type = RelationTypeManager.getType("Requirement Trace");
   }

   @Ignore
   //not implemented  in the code
   @Test
   public void loadDeletedRelationTest() throws OseeCoreException {
      RelationManager.addRelation(type, left, right, "");
      left.persist();
      RelationLink loaded = RelationManager.getLoadedRelation(type, left.getArtId(), right.getArtId(), branch, branch);
      int oldGammaId = loaded.getGammaId();
      RelationManager.deleteRelation(type, left, right);
      left.persist();
      RelationManager.addRelation(type, left, right, "");
      left.persist();

      List<RelationLink> links = RelationManager.getRelationsAll(left.getArtId(), branch.getId(), true);
      int linkCount = 0;
      for (RelationLink link : links) {
         if (link.getRelationType().getName().equals("Requirement Trace")) {
            linkCount++;
         }
      }

      int newGammaId = loaded.getGammaId();
      assertEquals("Deleted relation was not re-used by addRelation; see L3778", 1, linkCount);
      assertFalse(loaded.isDeleted());
      assertEquals("Gamma ID was changed;", oldGammaId, newGammaId);
   }

   @After
   public void tearDown() throws OseeCoreException {
      left.purgeFromBranch();
      right.purgeFromBranch();
   }
}

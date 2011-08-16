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

package org.eclipse.osee.framework.ui.skynet.test.cases;

import java.util.Collections;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.blam.operation.ReplaceRelationsWithBaselineOperation;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.junit.Assert;

/**
 * @author Jeff C. Phillips
 */
public class ReplaceRelationWithBaselineTest {

   @org.junit.Test
   public void testReplaceAttributeVersion() throws Exception {
      Branch branch = BranchManager.getBranchByGuid(DemoSawBuilds.SAW_Bld_1.getGuid());
      Assert.assertNotNull(branch);

      Artifact artifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, branch, "Name1");
      Artifact artifact2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, branch, "Name2");

      artifact1.addChild(RelationOrderBaseTypes.USER_DEFINED, artifact2);

      artifact1.persist(getClass().getName());

      Branch childBranch = BranchManager.createWorkingBranch(branch, "Test Child branch", null);

      Artifact childArtifact1 = ArtifactQuery.getArtifactFromId(artifact1.getArtId(), childBranch);
      Artifact childArtifact2 = ArtifactQuery.getArtifactFromId(artifact2.getArtId(), childBranch);

      childArtifact1.deleteRelations(CoreRelationTypes.Default_Hierarchical__Child);

      childArtifact2.addChild(RelationOrderBaseTypes.USER_DEFINED, childArtifact1);

      childArtifact2.persist(getClass().getName());

      Assert.assertTrue(childArtifact1.getParent().equals(childArtifact2));

      ReplaceRelationsWithBaselineOperation relationsWithBaselineOperation =
         new ReplaceRelationsWithBaselineOperation(Collections.singleton(childArtifact1));
      relationsWithBaselineOperation.runDoWork();

      childArtifact1.reloadAttributesAndRelations();
      childArtifact2.reloadAttributesAndRelations();

      Assert.assertTrue(childArtifact2.getParent().equals(childArtifact1));
   }
}

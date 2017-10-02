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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.List;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ryan Schmitt
 */
@Ignore
public class LoadDeletedRelationTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private Artifact left, right;
   private IRelationType type;

   @Before
   public void setUp()  {
      left = TestUtil.createSimpleArtifact(CoreArtifactTypes.Requirement, "Left", SAW_Bld_2);
      right = TestUtil.createSimpleArtifact(CoreArtifactTypes.Requirement, "Right", SAW_Bld_2);
      left.persist(getClass().getSimpleName());
      right.persist(getClass().getSimpleName());
      type = CoreRelationTypes.Requirement_Trace__Higher_Level;
   }

   //not implemented  in the code
   @Ignore
   @Test
   public void loadDeletedRelationTest()  {
      RelationManager.addRelation(type, left, right, "");
      left.persist(getClass().getSimpleName());
      RelationLink loaded = RelationManager.getLoadedRelation(type, left, right, SAW_Bld_2);
      int oldGammaId = loaded.getGammaId();
      RelationManager.deleteRelation(type, left, right);
      left.persist(getClass().getSimpleName());
      RelationManager.addRelation(type, left, right, "");
      left.persist(getClass().getSimpleName());

      List<RelationLink> links = RelationManager.getRelationsAll(left, DeletionFlag.INCLUDE_DELETED);
      int linkCount = 0;
      for (RelationLink link : links) {
         if (link.getRelationType().equals(type)) {
            linkCount++;
         }
      }

      int newGammaId = loaded.getGammaId();
      assertEquals("Deleted relation was not re-used by addRelation; see L3778", 1, linkCount);
      assertFalse(loaded.isDeleted());
      assertEquals("Gamma ID was changed;", oldGammaId, newGammaId);
   }

   @After
   public void tearDown()  {
      left.purgeFromBranch();
      right.purgeFromBranch();
   }
}

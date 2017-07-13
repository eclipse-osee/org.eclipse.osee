/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Arrays;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Megumi Telles
 */
public class ArtifactRendererTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private static final String NAME1 = "Name with \"quote\"";
   private static final String NAME2 = "Name with 'quote'";
   private static final String EXPECTED_NAME = "Name with quote";
   private static Artifact artifact1;
   private static Artifact artifact2;
   private static TransactionToken startTx;
   private static TransactionRecord endTx1;
   private static TransactionRecord endTx2;

   @Before
   public void setUp() throws OseeCoreException {

      startTx = TransactionManager.getHeadTransaction(COMMON);
      artifact1 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Artifact, COMMON, NAME1);
      String comment1 = getClass().getSimpleName() + "_1";
      artifact1.persist(comment1);
      endTx1 = TransactionManager.getTransaction(comment1).iterator().next();
      endTx1.setCommit(artifact1.getArtId());

      artifact2 = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Artifact, COMMON, NAME2);
      String comment2 = getClass().getSimpleName() + "_2";
      artifact2.persist(comment2);
      endTx2 = TransactionManager.getTransaction(comment2).iterator().next();
      endTx2.setCommit(artifact2.getArtId());
   }

   @Test
   public void testAssociatedArtifact_notAllowedDoubleQuotes() throws Exception {

      TransactionDelta deltaTx = new TransactionDelta(startTx, endTx1);
      ArtifactDelta delta = new ArtifactDelta(null, artifact2, artifact1);
      Change change = new ArtifactChange(COMMON, GammaId.valueOf(artifact1.getGammaId()), artifact1, deltaTx,
         ModificationType.MODIFIED, "", "", false, artifact1, delta);

      String name = RenderingUtil.getAssociatedArtifactName(Arrays.asList(change));
      Assert.assertEquals(EXPECTED_NAME, name);
   }

   @Test
   public void testAssociatedArtifact_notAllowedSingleQuotes() throws Exception {
      TransactionDelta deltaTx = new TransactionDelta(startTx, endTx2);
      ArtifactDelta delta = new ArtifactDelta(null, artifact1, artifact2);
      Change change = new ArtifactChange(COMMON, GammaId.valueOf(artifact2.getGammaId()), artifact2, deltaTx,
         ModificationType.MODIFIED, "", "", false, artifact2, delta);

      String name = RenderingUtil.getAssociatedArtifactName(Arrays.asList(change));
      Assert.assertEquals(EXPECTED_NAME, name);
   }
}

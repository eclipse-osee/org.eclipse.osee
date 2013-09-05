/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.proxy.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * @author Megumi Telles
 */
public class ExternalArtifactManagerTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private OrcsSession session;
   
   @Mock private ArtifactReadOnlyImpl readable;
   @Mock private ArtifactReadOnlyImpl readable1;
   @Mock private ArtifactReadOnlyImpl readable2;
   @Mock private ArtifactReadOnlyImpl readable3;
   
   @Mock private ArtifactImpl artifact1;
   @Mock private ArtifactImpl artifact2;
   @Mock private ArtifactImpl artifact3;
   // @formatter:on

   private ExternalArtifactManager proxyManager;
   private List<? extends Artifact> artifacts;
   private List<? extends ArtifactReadable> readables;

   @Before
   public void setUp() throws Exception {
      initMocks(this);

      artifacts = Arrays.asList(artifact1, artifact2, artifact3);
      readables = Arrays.asList(readable1, readable2, readable3);

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);
      proxyManager = new ExternalArtifactManagerImpl();
      when(readable.getProxiedObject()).thenReturn(artifact1);
   }

   @Test
   public void testAsArtifacts() throws OseeCoreException {
      ResultSet<? extends RelationNode> arts1 = proxyManager.asInternalArtifacts(readables);
      assertFalse(arts1.isEmpty());
      assertEquals(3, arts1.size());
   }

   @Test
   public void testAsReadables() throws OseeCoreException {
      ResultSet<ArtifactReadable> asReadables = proxyManager.asExternalArtifacts(session, artifacts);
      assertFalse(asReadables.isEmpty());
      assertEquals(3, asReadables.size());
   }

   @Test
   public void testAsWriteable() throws OseeCoreException {
      ArtifactReadable readable = proxyManager.asExternalArtifact(session, artifact1);
      assertNotNull(readable);
      assertNotNull(proxyManager.asInternalArtifact(readable));
   }

}

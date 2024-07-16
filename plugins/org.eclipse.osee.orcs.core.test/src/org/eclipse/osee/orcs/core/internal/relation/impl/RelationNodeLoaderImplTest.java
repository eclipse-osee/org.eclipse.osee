/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.internal.relation.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilder;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilderFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * @author Megumi Telles
 */
public class RelationNodeLoaderImplTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private DataLoaderFactory dataLoaderFactory;
   @Mock private GraphBuilderFactory graphBuilderFactory;

   @Mock private OrcsSession session;
   @Mock private GraphData graph;
   @Mock private GraphBuilder builder;
   @Mock private DataLoader loader;

   @Mock private Artifact artifact;
   // @formatter:on

   private static final BranchToken BRANCH = CoreBranches.COMMON;
   private static final TransactionId TRANSACTION_ID = TransactionId.valueOf(231214214);
   private static final Collection<ArtifactId> ids =
      Arrays.asList(ArtifactId.valueOf(4), ArtifactId.valueOf(5), ArtifactId.valueOf(6), ArtifactId.valueOf(7));

   private RelationNodeLoaderImpl Artifact;

   @Before
   public void setUp() throws Exception {
      initMocks(this);

      Artifact = new RelationNodeLoaderImpl(dataLoaderFactory, graphBuilderFactory);

      when(graph.getBranch()).thenReturn(BRANCH);
      when(graph.getTransaction()).thenReturn(TRANSACTION_ID);
   }

   @Test
   public void testLoadNodes() {
      Iterable<Artifact> artifacts = Arrays.asList(artifact);

      when(dataLoaderFactory.newDataLoaderFromIds(session, BRANCH, ids)).thenReturn(loader);
      when(graphBuilderFactory.createBuilderForGraph(graph)).thenReturn(builder);
      when(builder.getArtifacts()).thenReturn(artifacts);

      Iterable<Artifact> actual = Artifact.loadNodes(session, graph, ids, LoadLevel.ALL);

      verify(dataLoaderFactory).newDataLoaderFromIds(session, BRANCH, ids);
      verify(graphBuilderFactory).createBuilderForGraph(graph);

      verify(loader).withLoadLevel(LoadLevel.ALL);
      verify(loader).fromTransaction(TRANSACTION_ID);
      verify(loader).load(null, builder);

      assertEquals(artifacts, actual);
   }
}

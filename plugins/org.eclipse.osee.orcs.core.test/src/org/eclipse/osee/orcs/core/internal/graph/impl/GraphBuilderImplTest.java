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
package org.eclipse.osee.orcs.core.internal.graph.impl;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.LoadDescription;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilder;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.graph.GraphProvider;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.core.internal.relation.impl.RelationNodeAdjacencies;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

/**
 * Test Case for @{link GraphBuilderImpl}
 *
 * @author Megumi Telles
 */
public class GraphBuilderImplTest {
   private static final ArtifactId artifactId60 = ArtifactId.valueOf(60);
   private static final RelationTypeToken TYPE_1 = TokenFactory.createRelationType(123456789L, "TYPE_1");
   private static final TransactionId TRANSACTION_ID = TransactionId.valueOf(231214214);

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   @Mock private ArtifactFactory artifactFactory;
   @Mock private AttributeFactory attributeFactory;
   @Mock private RelationFactory relationFactory;

   @Mock private OrcsSession session;
   @Mock private GraphProvider graphProvider;
   @Mock private GraphData graphData;

   @Mock private LoadDescription description;

   @Mock private ArtifactData artifactData;
   @Mock private AttributeData attributeData;
   @Mock private RelationData relationData;

   @Mock private Artifact artifact;
   @Mock private Artifact container;
   @Mock private Relation relation;

   // @formatter:on

   private GraphBuilder builder;

   private final RelationNodeAdjacencies adjacencies = new RelationNodeAdjacencies();

   @Before
   public void setUp() throws Exception {
      initMocks(this);

      builder = new GraphBuilderImpl(logger, artifactFactory, attributeFactory, relationFactory, graphProvider);

      when(description.getSession()).thenReturn(session);
      when(description.getBranch()).thenReturn(COMMON);
      when(description.getTransaction()).thenReturn(TRANSACTION_ID);
      when(graphProvider.getGraph(session, COMMON, TRANSACTION_ID)).thenReturn(graphData);

      when(relationFactory.createRelationContainer()).thenReturn(adjacencies);
      when(relationFactory.createRelation(relationData)).thenReturn(relation);
      when(relation.getRelationType()).thenReturn(TYPE_1);

      when(attributeData.getArtifactId()).thenReturn(artifactId60.getIdIntValue());
   }

   @Test
   public void testGraphNull() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("graph cannot be null");

      builder.onLoadStart();
      builder.onData(artifactData);
      builder.onData(attributeData);
      builder.onData(relationData);
      builder.onLoadEnd();
   }

   @Test
   public void testOnLoadDescription() {
      builder.onLoadStart();
      builder.onLoadDescription(description);
      builder.onLoadEnd();

      verify(description).getBranch();
      verify(description).getTransaction();
   }

   @Test
   public void testGetNodeOrAdjanciesNull() {
      when(graphData.getNode(artifactData)).thenReturn(null);
      when(artifactFactory.createArtifact(session, artifactData)).thenReturn(artifact);
      when(artifact.getOrcsData()).thenReturn(artifactData);
      when(artifactData.isExistingVersionUsed()).thenReturn(false);

      when(graphData.getNode(artifactId60)).thenReturn(null);
      when(graphData.getSession()).thenReturn(session);

      builder.onLoadStart();
      builder.onLoadDescription(description);

      builder.onData(artifactData);
      verify(graphData).addNode(artifact, false);
      verify(artifactFactory).createArtifact(session, artifactData);
      verify(relationFactory, times(1)).createRelationContainer();

      reset(relationFactory);
      when(relationFactory.createRelationContainer()).thenReturn(adjacencies);
      when(relationFactory.createRelation(relationData)).thenReturn(relation);

      builder.onData(attributeData);
      verify(logger).warn("Orphaned attribute detected - data[%s]", attributeData);

      builder.onData(relationData);
      verify(relationFactory, times(2)).createRelationContainer();

      builder.onLoadEnd();
      verify(relationFactory, times(1)).createRelation(relationData);
   }

   @Test
   public void testOnData() {
      when(graphData.getNode(artifactData)).thenReturn(artifact);
      when(graphData.getNode(artifactId60)).thenReturn(container);

      builder.onLoadStart();
      builder.onLoadDescription(description);
      builder.onData(artifactData);
      builder.onData(attributeData);
      builder.onData(relationData);
      builder.onLoadEnd();

      verify(attributeFactory).createAttribute(container, attributeData);
      verify(relationFactory).createRelation(relationData);

   }

}

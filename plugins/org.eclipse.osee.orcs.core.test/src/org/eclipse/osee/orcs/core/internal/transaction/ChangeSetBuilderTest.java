/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.transaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import com.google.common.collect.Iterables;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.relation.Relation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test Case for {@link ChangeSetBuilder}
 *
 * @author Roberto E. Escobar
 */
public class ChangeSetBuilderTest {

   // @formatter:off
   @Mock private Artifact artifact;
   @Mock private Attribute<?> attribute;
   @Mock private Relation relation;

   @Mock private ArtifactData artifactData;
   @Mock private AttributeData attributeData;
   @Mock private RelationData relationData;
   // @formatter:on

   private ChangeSetBuilder handler;

   @Before
   public void init() {
      initMocks(this);

      handler = new ChangeSetBuilder();

      when(artifact.getOrcsData()).thenReturn(artifactData);
      when(attribute.getOrcsData()).thenReturn(attributeData);
      when(relation.getOrcsData()).thenReturn(relationData);
   }

   @Test
   public void testDontCollectNoneDirtyData() {
      when(artifact.isDirty()).thenReturn(false);
      when(attribute.isDirty()).thenReturn(false);
      when(relation.isDirty()).thenReturn(false);

      handler.handleTuples(new TxData(null, null));

      handler.visit(artifact);
      handler.visit(attribute);
      handler.visit(relation);

      assertTrue(handler.getChangeSet().isEmpty());
   }

   @Test
   public void testVisitAndCollectDirtyData() {
      when(artifact.isDirty()).thenReturn(true);
      when(attribute.isDirty()).thenReturn(true);
      when(relation.isDirty()).thenReturn(true);

      handler.visit(artifact);
      handler.visit(attribute);
      handler.visit(relation);

      assertEquals(3, handler.getChangeSet().size());

      OrcsChangeSet txData = handler.getChangeSet();

      assertEquals(artifactData, Iterables.getOnlyElement(txData.getArtifactData()));
      assertEquals(attributeData, Iterables.getOnlyElement(txData.getAttributeData()));
      assertEquals(relationData, Iterables.getOnlyElement(txData.getRelationData()));
   }
}

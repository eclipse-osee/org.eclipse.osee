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
package org.eclipse.osee.orcs.core.internal.transaction.handler;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link CollectDirtyData}
 * 
 * @author Roberto E. Escobar
 */
public class CollectDirtyDataTest {

   // @formatter:off
   @Mock private Artifact artifact;
   @Mock private Attribute<?> attribute;

   @Mock private ArtifactData artifactData;
   @Mock private AttributeData attributeData;

   // @formatter:on

   private List<ArtifactTransactionData> data;
   private CollectDirtyData handler;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);

      data = new ArrayList<ArtifactTransactionData>();
      handler = new CollectDirtyData(data);

      when(artifact.getOrcsData()).thenReturn(artifactData);
      when(attribute.getOrcsData()).thenReturn(attributeData);
   }

   @Test
   public void testDontCollectNoneDirtyArtifact() {
      when(artifact.isDirty()).thenReturn(false);

      handler.visit(artifact);

      Assert.assertEquals(0, data.size());

      when(attribute.isDirty()).thenReturn(false);
      handler.visit(attribute);

      Assert.assertEquals(0, data.size());
   }

   @Test
   public void testVisitAndCollectDirtyArtifact() {
      when(artifact.isDirty()).thenReturn(true);
      when(attribute.isDirty()).thenReturn(true);

      handler.visit(artifact);
      handler.visit(attribute);

      Assert.assertEquals(1, data.size());

      ArtifactTransactionData txData = data.iterator().next();

      Assert.assertEquals(artifactData, txData.getArtifactData());
      Assert.assertEquals(attributeData, txData.getAttributeData().get(0));
   }
}

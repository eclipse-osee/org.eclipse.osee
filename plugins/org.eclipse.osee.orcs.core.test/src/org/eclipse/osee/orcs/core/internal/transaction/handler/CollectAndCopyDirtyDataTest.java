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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.core.internal.transaction.handler.CollectAndCopyDirtyData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link CollectAndCopyDirtyData}
 * 
 * @author Roberto E. Escobar
 */
public class CollectAndCopyDirtyDataTest {

   // @formatter:off
   @Mock DataFactory dataFactory;
   @Mock Artifact artifact;
   @Mock Attribute<?> attribute;
   
   @Mock ArtifactData artSourceData;
   @Mock AttributeData attrSourceData;
   @Mock ArtifactData artCopyData;
   @Mock AttributeData attrCopyData;
   // @formatter:on

   private List<ArtifactTransactionData> data;
   private CollectAndCopyDirtyData handler;

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);

      data = new ArrayList<ArtifactTransactionData>();
      handler = new CollectAndCopyDirtyData(dataFactory, data);

      when(artifact.getOrcsData()).thenReturn(artSourceData);
      when(attribute.getOrcsData()).thenReturn(attrSourceData);
   }

   @Test
   public void testDontCollectNoneDirtyArtifact() throws OseeCoreException {
      when(artifact.isDirty()).thenReturn(false);
      handler.visit(artifact);
      verify(dataFactory, times(0)).clone(artSourceData);

      Assert.assertEquals(0, data.size());

      when(attribute.isDirty()).thenReturn(false);
      handler.visit(attribute);
      verify(dataFactory, times(0)).clone(attrSourceData);

      Assert.assertEquals(0, data.size());
   }

   @Test
   public void testVisitAndCollectDirtyArtifact() throws OseeCoreException {
      when(artifact.isDirty()).thenReturn(true);
      when(attribute.isDirty()).thenReturn(true);
      when(dataFactory.clone(artSourceData)).thenReturn(artCopyData);
      when(dataFactory.clone(attrSourceData)).thenReturn(attrCopyData);

      handler.visit(artifact);
      handler.visit(attribute);

      verify(dataFactory).clone(artSourceData);
      verify(dataFactory).clone(attrSourceData);

      Assert.assertEquals(1, data.size());

      ArtifactTransactionData txData = data.iterator().next();

      Assert.assertEquals(artCopyData, txData.getArtifactData());
      Assert.assertEquals(attrCopyData, txData.getAttributeData().get(0));
   }
}

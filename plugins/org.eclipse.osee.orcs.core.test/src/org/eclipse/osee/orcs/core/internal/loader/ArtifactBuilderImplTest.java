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
package org.eclipse.osee.orcs.core.internal.loader;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilder;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.proxy.ArtifactProxyFactory;
import org.eclipse.osee.orcs.core.internal.relation.RelationContainer;
import org.eclipse.osee.orcs.core.internal.relation.RelationContainerImpl;
import org.eclipse.osee.orcs.data.RelationTypes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AritfactBuilderImpl}
 * 
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public class ArtifactBuilderImplTest {

   // @formatter:off
   @Mock private Log logger;
   @Mock private ArtifactProxyFactory proxyFactory;
   @Mock private ArtifactFactory artifactFactory;
   @Mock private AttributeFactory attributeFactory;
   
   @Mock private Artifact artifact;
   @Mock private ArtifactData artifactData;
   @Mock private AttributeData attributeData;
   // @formatter:on

   private ArtifactBuilder builder;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      builder = new ArtifactBuilderImpl(logger, proxyFactory, artifactFactory, attributeFactory);
   }

   @Test
   public void testAttributeDataValid() throws OseeCoreException {
      when(artifactFactory.createArtifact(artifactData)).thenReturn(artifact);
      when(artifact.getLocalId()).thenReturn(45);

      when(attributeData.getArtifactId()).thenReturn(45);

      builder.onData(artifactData);
      builder.onData(attributeData);

      verify(attributeFactory).createAttribute(artifact, attributeData);
      verify(logger, times(0)).warn("");
   }

   @Test
   public void testAttributeDataNotFound() throws OseeCoreException {
      when(artifactFactory.createArtifact(artifactData)).thenReturn(artifact);
      when(artifact.getLocalId()).thenReturn(45);

      when(attributeData.getArtifactId()).thenReturn(45);

      builder.onData(attributeData);

      verify(attributeFactory, times(0)).createAttribute(artifact, attributeData);
      verify(logger).warn("Orphaned attribute detected - [%s]", attributeData);
   }

   @Test
   public void testRelationCountMatches() throws OseeCoreException {
      RelationTypes cache = createAndPopulate();
      Map<Integer, RelationContainer> containers = getRelationProviderList(cache, 22);
      List<RelationData> datas = getTestData();
      doSetup(datas, containers);

      for (RelationData data : datas) {
         builder.onData(data);
      }

      checkRelationCount(containers.get(1), RelationSide.SIDE_B, 9);
      checkRelationCount(containers.get(1), RelationSide.SIDE_A, 0);
      checkRelationCount(containers.get(2), RelationSide.SIDE_B, 0);
      checkRelationCount(containers.get(3), RelationSide.SIDE_B, 6);
      checkRelationCount(containers.get(3), RelationSide.SIDE_A, 1);
      checkRelationCount(containers.get(4), RelationSide.SIDE_B, 7);
   }

   @Ignore
   @Test
   public void testRelatedArtifactsMatch() throws OseeCoreException {
      RelationTypes cache = createAndPopulate();
      Map<Integer, RelationContainer> containers = getRelationProviderList(cache, 22);
      List<RelationData> datas = getTestData();
      doSetup(datas, containers);

      checkRelatedArtifacts(containers.get(1), RelationSide.SIDE_B, 2, 3, 4, 5, 6, 7, 8, 9, 10);
      checkRelatedArtifacts(containers.get(1), RelationSide.SIDE_A);
      checkRelatedArtifacts(containers.get(2), RelationSide.SIDE_B);
      checkRelatedArtifacts(containers.get(3), RelationSide.SIDE_B, 11, 12, 13, 14, 15, 16);
      checkRelatedArtifacts(containers.get(3), RelationSide.SIDE_A, 1);
      checkRelatedArtifacts(containers.get(4), RelationSide.SIDE_B, 17, 18, 19, 20, 21, 22, 2);
   }

   private void doSetup(List<RelationData> datas, Map<Integer, RelationContainer> containers) throws OseeCoreException {
      for (RelationData data : datas) {
         Artifact artifact = Mockito.mock(Artifact.class);
         ArtifactData artData = Mockito.mock(ArtifactData.class);
         int id = data.getParentId();
         when(artifactFactory.createArtifact(artData)).thenReturn(artifact);
         when(artifact.getLocalId()).thenReturn(id);
         when(artData.getLocalId()).thenReturn(id);

         RelationContainer container = containers.get(id);
         when(artifact.getRelationContainer()).thenReturn(container);

         builder.onData(artData);
      }
   }

   private void checkRelatedArtifacts(RelationContainer relationContainer, RelationSide side, int... expected) {
      List<Integer> relatedArtifacts = new ArrayList<Integer>();
      relationContainer.getArtifactIds(relatedArtifacts, TokenFactory.createRelationTypeSide(side, 1, "blah"));
      Assert.assertTrue(String.format("Expected %d matches found %d", expected.length, relatedArtifacts.size()),
         expected.length == relatedArtifacts.size());
      for (int value : expected) {
         Assert.assertTrue(String.format("Expected relation to id[%d]", value), relatedArtifacts.contains(value));
      }
   }

   private Map<Integer, RelationContainer> getRelationProviderList(RelationTypes relationTypeCache, int size) {
      Map<Integer, RelationContainer> toReturn = new HashMap<Integer, RelationContainer>();
      for (int i = 1; i <= size; i++) {
         toReturn.put(i, new RelationContainerImpl(i, relationTypeCache));
      }
      return toReturn;
   }

   public RelationTypes createAndPopulate() throws OseeCoreException {
      IRelationType type = mock(IRelationType.class);
      RelationTypes cache = mock(RelationTypes.class);

      when(type.getGuid()).thenReturn(1L);
      when(type.getName()).thenReturn("test");

      when(cache.getByUuid(1L)).thenReturn(type);
      when(cache.getSideAName(type)).thenReturn("sideAName");
      when(cache.getSideBName(type)).thenReturn("sideBName");
      when(cache.getArtifactTypeSideA(type)).thenReturn(CoreArtifactTypes.Artifact);
      when(cache.getArtifactTypeSideB(type)).thenReturn(CoreArtifactTypes.Artifact);
      when(cache.getMultiplicity(type)).thenReturn(RelationTypeMultiplicity.MANY_TO_MANY);
      when(cache.getDefaultOrderTypeGuid(type)).thenReturn("");

      when(cache.getSideName(type, RelationSide.SIDE_A)).thenReturn("sideAName");
      when(cache.getSideName(type, RelationSide.SIDE_B)).thenReturn("sideBName");

      when(cache.getArtifactType(eq(type), any(RelationSide.class))).thenReturn(CoreArtifactTypes.Artifact);
      return cache;
   }

   private void checkRelationCount(RelationContainer relationContainer, RelationSide side, int size) {
      int count = relationContainer.getRelationCount(TokenFactory.createRelationTypeSide(side, 1, "blah"));
      Assert.assertEquals(
         String.format("We did not get the expected number of relations back [%d != %d]", size, count), size, count);
   }

   private List<RelationData> getTestData() {
      List<RelationData> datas = new ArrayList<RelationData>();
      RelationCsvReader csvReader = new RelationCsvReader(datas);
      //      #ArtIdA ArtIdB   BranchId GammaId  ModType  Rationale   RelationId  RelationTypeId 
      csvReader.onRow(1, 1, 2, 1, 1L, ModificationType.NEW, "yay", 1, 1L);
      csvReader.onRow(1, 1, 3, 1, 1L, ModificationType.NEW, "yay", 2, 1L);
      csvReader.onRow(1, 1, 4, 1, 1L, ModificationType.NEW, "yay", 3, 1L);
      csvReader.onRow(1, 1, 5, 1, 1L, ModificationType.NEW, "yay", 4, 1L);
      csvReader.onRow(1, 1, 6, 1, 1L, ModificationType.NEW, "yay", 5, 1L);
      csvReader.onRow(1, 1, 7, 1, 1L, ModificationType.NEW, "yay", 6, 1L);
      csvReader.onRow(1, 1, 8, 1, 1L, ModificationType.NEW, "yay", 7, 1L);
      csvReader.onRow(1, 1, 9, 1, 1L, ModificationType.NEW, "yay", 8, 1L);
      csvReader.onRow(1, 1, 10, 1, 1L, ModificationType.NEW, "yay", 9, 1L);
      csvReader.onRow(3, 3, 11, 1, 1L, ModificationType.NEW, "yay", 10, 1L);
      csvReader.onRow(3, 1, 3, 1, 1L, ModificationType.NEW, "yay", 10, 1L);
      csvReader.onRow(3, 3, 12, 1, 1L, ModificationType.NEW, "yay", 11, 1L);
      csvReader.onRow(3, 3, 13, 1, 1L, ModificationType.NEW, "yay", 12, 1L);
      csvReader.onRow(3, 3, 14, 1, 1L, ModificationType.NEW, "yay", 13, 1L);
      csvReader.onRow(3, 3, 15, 1, 1L, ModificationType.NEW, "yay", 14, 1L);
      csvReader.onRow(3, 3, 16, 1, 1L, ModificationType.NEW, "yay", 15, 1L);
      csvReader.onRow(4, 4, 17, 1, 1L, ModificationType.NEW, "yay", 16, 1L);
      csvReader.onRow(4, 4, 18, 1, 1L, ModificationType.NEW, "yay", 17, 1L);
      csvReader.onRow(4, 4, 19, 1, 1L, ModificationType.NEW, "yay", 18, 1L);
      csvReader.onRow(4, 4, 20, 1, 1L, ModificationType.NEW, "yay", 19, 1L);
      csvReader.onRow(4, 4, 21, 1, 1L, ModificationType.NEW, "yay", 20, 1L);
      csvReader.onRow(4, 4, 22, 1, 1L, ModificationType.NEW, "yay", 21, 1L);
      csvReader.onRow(4, 4, 2, 1, 1L, ModificationType.NEW, "yay", 21, 1L);
      return datas;
   }

   public static class RelationCsvReader {

      private final List<RelationData> data;

      public RelationCsvReader(List<RelationData> data) {
         this.data = data;
      }

      public void onRow(Object... row) {
         //ArtIdA,ArtIdB,BranchId,GammaId,ModType,Rationale,RelationId,RelationTypeId
         if (row.length != 9) {
            Assert.assertTrue("Data file is not formatted correctly", false);
         }

         VersionData version = mock(VersionData.class);
         RelationData relationRow = Mockito.mock(RelationData.class);

         when(version.getBranchId()).thenReturn((Integer) row[3]);
         when(version.getGammaId()).thenReturn((Long) row[4]);

         when(relationRow.getParentId()).thenReturn((Integer) row[0]);
         when(relationRow.getArtIdA()).thenReturn((Integer) row[1]);
         when(relationRow.getArtIdB()).thenReturn((Integer) row[2]);
         when(relationRow.getModType()).thenReturn((ModificationType) row[5]);
         when(relationRow.getRationale()).thenReturn((String) row[6]);
         when(relationRow.getLocalId()).thenReturn((Integer) row[7]);
         when(relationRow.getTypeUuid()).thenReturn((Long) row[8]);
         when(relationRow.getVersion()).thenReturn(version);

         data.add(relationRow);
      }
   }
}

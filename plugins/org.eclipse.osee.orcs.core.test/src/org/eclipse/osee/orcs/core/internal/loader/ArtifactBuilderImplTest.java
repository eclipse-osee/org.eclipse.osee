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
import java.io.IOException;
import java.net.URL;
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
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl;
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
   
   @Mock private ArtifactImpl artifact;
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

      builder.getArtifactDataHandler().onData(artifactData);
      builder.getAttributeDataHandler().onData(attributeData);

      verify(attributeFactory).createAttribute(artifact, attributeData);
      verify(logger, times(0)).warn("");
   }

   @Test
   public void testAttributeDataNotFound() throws OseeCoreException {
      when(artifactFactory.createArtifact(artifactData)).thenReturn(artifact);
      when(artifact.getLocalId()).thenReturn(45);

      when(attributeData.getArtifactId()).thenReturn(45);

      builder.getAttributeDataHandler().onData(attributeData);

      verify(attributeFactory, times(0)).createAttribute(artifact, attributeData);
      verify(logger).warn("Orphaned attribute detected - [%s]", attributeData);
   }

   @Test
   public void testRelationCountMatches() throws OseeCoreException, IOException {
      RelationTypes cache = createAndPopulate();
      Map<Integer, RelationContainer> containers = getRelationProviderList(cache, 22);
      List<RelationData> datas = getTestData("data.csv");
      doSetup(datas, containers);

      for (RelationData data : datas) {
         builder.getRelationDataHandler().onData(data);
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
   public void testRelatedArtifactsMatch() throws OseeCoreException, IOException {
      RelationTypes cache = createAndPopulate();
      Map<Integer, RelationContainer> containers = getRelationProviderList(cache, 22);
      List<RelationData> datas = getTestData("data.csv");
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
         ArtifactImpl artifact = Mockito.mock(ArtifactImpl.class);
         ArtifactData artData = Mockito.mock(ArtifactData.class);
         int id = data.getParentId();
         when(artifactFactory.createArtifact(artData)).thenReturn(artifact);
         when(artifact.getLocalId()).thenReturn(id);
         when(artData.getLocalId()).thenReturn(id);

         RelationContainer container = containers.get(id);
         when(artifact.getRelationContainer()).thenReturn(container);

         builder.getArtifactDataHandler().onData(artData);
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

   private List<RelationData> getTestData(String csvFile) throws IOException {
      URL url = getClass().getResource(csvFile);
      Assert.assertNotNull(url);

      List<RelationData> datas = new ArrayList<RelationData>();
      RelationCsvReader csvReader = new RelationCsvReader(datas);
      CsvReader reader = new CsvReader(url.openStream(), csvReader);
      reader.readFile();
      return datas;
   }

   public static class RelationCsvReader implements CsvRowHandler {

      private final List<RelationData> data;

      public RelationCsvReader(List<RelationData> data) {
         this.data = data;
      }

      @Override
      public void onRow(String... row) {
         //ArtIdA,ArtIdB,BranchId,GammaId,ModType,Rationale,RelationId,RelationTypeId
         if (row.length != 9) {
            Assert.assertTrue("Data file is not formatted correctly", false);
         }

         VersionData version = mock(VersionData.class);
         RelationData relationRow = Mockito.mock(RelationData.class);

         when(version.getBranchId()).thenReturn(Integer.parseInt(row[3]));
         when(version.getGammaId()).thenReturn(Long.parseLong(row[4]));

         when(relationRow.getParentId()).thenReturn(Integer.parseInt(row[0]));
         when(relationRow.getArtIdA()).thenReturn(Integer.parseInt(row[1]));
         when(relationRow.getArtIdB()).thenReturn(Integer.parseInt(row[2]));
         when(relationRow.getModType()).thenReturn(ModificationType.valueOf(row[5]));
         when(relationRow.getRationale()).thenReturn(row[6]);
         when(relationRow.getLocalId()).thenReturn(Integer.parseInt(row[7]));
         when(relationRow.getTypeUuid()).thenReturn(Long.parseLong(row[8]));
         when(relationRow.getVersion()).thenReturn(version);

         data.add(relationRow);
      }
   }
}

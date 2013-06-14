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
package org.eclipse.osee.orcs.core.internal.relation;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
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
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.data.RelationTypes;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationLoadingTest {

   @Test
   public void testRelationCountMatches() throws OseeCoreException, IOException {
      RelationTypes cache = createAndPopulate();
      Map<Integer, RelationContainer> providersThatWillBeLoaded = getRelationProviderList(cache, 22);
      RelationRowMapper relationRowMapper = new RelationRowMapper(providersThatWillBeLoaded);

      loadRowData("data.csv", relationRowMapper);

      checkRelationCount(providersThatWillBeLoaded.get(1), RelationSide.SIDE_B, 9);
      checkRelationCount(providersThatWillBeLoaded.get(1), RelationSide.SIDE_A, 0);
      checkRelationCount(providersThatWillBeLoaded.get(2), RelationSide.SIDE_B, 0);
      checkRelationCount(providersThatWillBeLoaded.get(3), RelationSide.SIDE_B, 6);
      checkRelationCount(providersThatWillBeLoaded.get(3), RelationSide.SIDE_A, 1);
      checkRelationCount(providersThatWillBeLoaded.get(4), RelationSide.SIDE_B, 7);
   }

   //@formatter:off
   @Ignore
   @Test
   public void testRelatedArtifactsMatch() throws OseeCoreException, IOException {
      RelationTypes cache = createAndPopulate();
      Map<Integer, RelationContainer> providersThatWillBeLoaded = getRelationProviderList(cache, 22);
      RelationRowMapper relationRowMapper = new RelationRowMapper(providersThatWillBeLoaded);

      loadRowData("data.csv", relationRowMapper);
      List<Integer> relatedArtifacts = new ArrayList<Integer>();
      checkRelatedArtifacts(relatedArtifacts, providersThatWillBeLoaded.get(1), RelationSide.SIDE_B, new int[]{2,3,4,5,6,7,8,9,10});
      checkRelatedArtifacts(relatedArtifacts, providersThatWillBeLoaded.get(1), RelationSide.SIDE_A, new int[]{});
      checkRelatedArtifacts(relatedArtifacts, providersThatWillBeLoaded.get(2), RelationSide.SIDE_B, new int[]{});
      checkRelatedArtifacts(relatedArtifacts, providersThatWillBeLoaded.get(3), RelationSide.SIDE_B, new int[]{11,12,13,14,15,16});
      checkRelatedArtifacts(relatedArtifacts, providersThatWillBeLoaded.get(3), RelationSide.SIDE_A, new int[]{1});
      checkRelatedArtifacts(relatedArtifacts, providersThatWillBeLoaded.get(4), RelationSide.SIDE_B, new int[]{17,18,19,20,21,22,2});
   }
   //@formatter:on

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

   private void checkRelatedArtifacts(List<Integer> relatedArtifacts, RelationContainer relationContainer, RelationSide side, int[] expected) {
      relatedArtifacts.clear();
      relationContainer.getArtifactIds(relatedArtifacts, TokenFactory.createRelationTypeSide(side, 1, "blah"));
      Assert.assertTrue(String.format("Expected %d matches found %d", expected.length, relatedArtifacts.size()),
         expected.length == relatedArtifacts.size());
      for (int value : expected) {
         Assert.assertTrue(String.format("Expected relation to id[%d]", value), relatedArtifacts.contains(value));
      }
   }

   private Map<Integer, RelationContainer> getRelationProviderList(RelationTypes relationTypeCache, int size) {
      Map<Integer, RelationContainer> providersThatWillBeLoaded = new HashMap<Integer, RelationContainer>();
      for (int i = 1; i <= size; i++) {
         providersThatWillBeLoaded.put(i, createRelationContainer(relationTypeCache, i));
      }
      return providersThatWillBeLoaded;
   }

   private RelationContainer createRelationContainer(RelationTypes relationTypeCache, final int parentId) {
      return new RelationContainerImpl(parentId, relationTypeCache);
   }

   private void loadRowData(String csvFile, RelationRowMapper relationRowMapper) throws IOException, OseeCoreException {
      URL url = RelationLoadingTest.class.getResource(csvFile);
      Assert.assertNotNull(url);

      List<RelationData> datas = new ArrayList<RelationData>();
      RelationCsvReader csvReader = new RelationCsvReader(datas);
      CsvReader reader = new CsvReader(url.openStream(), csvReader);
      reader.readFile();

      for (RelationData data : datas) {
         relationRowMapper.onData(data);
      }
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

         VersionData version = Mockito.mock(VersionData.class);
         version.setBranchId(Integer.parseInt(row[3]));
         version.setGammaId(Integer.parseInt(row[4]));

         RelationData relationRow = Mockito.mock(RelationData.class);

         Mockito.when(relationRow.getParentId()).thenReturn(Integer.parseInt(row[0]));
         Mockito.when(relationRow.getArtIdA()).thenReturn(Integer.parseInt(row[1]));
         Mockito.when(relationRow.getArtIdB()).thenReturn(Integer.parseInt(row[2]));
         Mockito.when(relationRow.getModType()).thenReturn(ModificationType.valueOf(row[5]));
         Mockito.when(relationRow.getRationale()).thenReturn(row[6]);
         Mockito.when(relationRow.getLocalId()).thenReturn(Integer.parseInt(row[7]));
         Mockito.when(relationRow.getTypeUuid()).thenReturn(Long.parseLong(row[8]));
         Mockito.when(relationRow.getVersion()).thenReturn(version);

         data.add(relationRow);
      }
   }
}

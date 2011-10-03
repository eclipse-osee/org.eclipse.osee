/*
 * Created on Sep 29, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.relation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.RelationRow;
import org.junit.Assert;
import org.junit.Test;

public class TestRelationLoading {

   @Test
   public void testRelationCountMatches() throws OseeCoreException, IOException {
      Log log = new MockLog();
      Map<Integer, RelationContainerImpl> providersThatWillBeLoaded = getRelationProviderList(22);
      RelationRowMapper relationRowMapper = new RelationRowMapper(log, providersThatWillBeLoaded);

      loadRowData("data.csv", relationRowMapper);

      checkRelationCount(providersThatWillBeLoaded.get(1), RelationSide.SIDE_B, 9);
      checkRelationCount(providersThatWillBeLoaded.get(1), RelationSide.SIDE_A, 0);
      checkRelationCount(providersThatWillBeLoaded.get(2), RelationSide.SIDE_B, 0);
      checkRelationCount(providersThatWillBeLoaded.get(3), RelationSide.SIDE_B, 6);
      checkRelationCount(providersThatWillBeLoaded.get(3), RelationSide.SIDE_A, 1);
      checkRelationCount(providersThatWillBeLoaded.get(4), RelationSide.SIDE_B, 7);
   }

   //@formatter:off
   @Test
   public void testRelatedArtifactsMatch() throws OseeCoreException, IOException {
      Log log = new MockLog();
      Map<Integer, RelationContainerImpl> providersThatWillBeLoaded = getRelationProviderList(22);
      RelationRowMapper relationRowMapper = new RelationRowMapper(log, providersThatWillBeLoaded);

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

   private void checkRelationCount(RelationContainerImpl relationContainer, RelationSide side, int size) {
      int count = relationContainer.getRelationCount(1, side);
      Assert.assertEquals(
         String.format("We did not get the expected number of relations back [%d != %d]", size, count), size, count);
   }

   private void checkRelatedArtifacts(List<Integer> relatedArtifacts, RelationContainerImpl relationContainer, RelationSide side, int[] expected) {
      relatedArtifacts.clear();
      relationContainer.getArtifactIds(relatedArtifacts, 1, side);
      Assert.assertTrue(String.format("Expected %d matches found %d", expected.length, relatedArtifacts.size()),
         expected.length == relatedArtifacts.size());
      for (int value : expected) {
         Assert.assertTrue(String.format("Expected relation to id[%d]", value), relatedArtifacts.contains(value));
      }
   }

   private Map<Integer, RelationContainerImpl> getRelationProviderList(int size) {
      Map<Integer, RelationContainerImpl> providersThatWillBeLoaded = new HashMap<Integer, RelationContainerImpl>();
      for (int i = 1; i <= size; i++) {
         providersThatWillBeLoaded.put(i, new RelationContainerImpl(i));
      }
      return providersThatWillBeLoaded;
   }

   private void loadRowData(String csvFile, RelationRowMapper relationRowMapper) throws IOException, OseeCoreException {
      URL url = TestRelationLoading.class.getResource(csvFile);
      Assert.assertNotNull(url);

      List<RelationRow> data = new ArrayList<RelationRow>();
      RelationCsvReader csvReader = new RelationCsvReader(data);
      CsvReader reader = new CsvReader(url.openStream(), csvReader);
      reader.readFile();

      for (RelationRow row : data) {
         relationRowMapper.onRow(row);
      }
   }

   public static class RelationCsvReader implements CsvRowHandler {

      private final List<RelationRow> data;

      public RelationCsvReader(List<RelationRow> data) {
         this.data = data;
      }

      @Override
      public void onRow(String... row) {
         //ArtIdA,ArtIdB,BranchId,GammaId,ModType,Rationale,RelationId,RelationTypeId
         if (row.length != 9) {
            Assert.assertTrue("Data file is not formatted correctly", false);
         }
         RelationRow relationRow = new RelationRow();
         relationRow.setParentId(Integer.parseInt(row[0]));
         relationRow.setArtIdA(Integer.parseInt(row[1]));
         relationRow.setArtIdB(Integer.parseInt(row[2]));
         relationRow.setBranchId(Integer.parseInt(row[3]));
         relationRow.setGammaId(Integer.parseInt(row[4]));
         relationRow.setModType(ModificationType.valueOf(row[5]));
         relationRow.setRationale(row[6]);
         relationRow.setRelationId(Integer.parseInt(row[7]));
         relationRow.setRelationTypeId(Integer.parseInt(row[8]));
         data.add(relationRow);
      }
   }
}

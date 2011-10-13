/*
 * Created on Oct 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal;

import java.util.List;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.mocks.MockOseeDataAccessor;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeClassResolver;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiUtil;
import org.junit.Rule;

public class MasterLoaderTest {

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   @org.junit.Test
   public void testMasterLoader() throws Exception {

      //get the needed services
      DataLoader dataLoader = OsgiUtil.getService(DataLoader.class);
      Log logger = OsgiUtil.getService(Log.class);
      IOseeDatabaseService oseeDbService = OsgiUtil.getService(IOseeDatabaseService.class);

      //create all necessary dependencies
      ArtifactTypeCache artifactTypeCache = new ArtifactTypeCache(new MockOseeDataAccessor<Long, ArtifactType>());
      AttributeTypeCache attributeTypeCache = new AttributeTypeCache(new MockOseeDataAccessor<Long, AttributeType>());
      BranchCache branchCache = new BranchCache(new MockOseeDataAccessor<String, Branch>());
      ArtifactFactory artifactFactory = new ArtifactFactory();
      AttributeFactory attributeFactory =
         new AttributeFactory(logger, new AttributeClassResolver(), attributeTypeCache);

      //create the loader
      MasterLoader masterLoader =
         new MasterLoader(dataLoader, logger, artifactTypeCache, branchCache, artifactFactory, attributeFactory);

      //do a db query     
      LoadOptions options = new LoadOptions();
      ArtifactJoinQuery artJoinQuery = JoinUtility.createArtifactJoinQuery();
      OseeConnection connection = oseeDbService.getConnection();
      artJoinQuery.add(7, 2, -1);
      artJoinQuery.add(8, 2, -1);
      artJoinQuery.add(9, 2, -1);
      artJoinQuery.store(connection);
      List<ReadableArtifact> arts = masterLoader.load(options, 10, artJoinQuery.getQueryId(), new MockSessionContext());
      artJoinQuery.delete();

   }
   private static class MockSessionContext implements SessionContext {

      @Override
      public <T extends ReadableArtifact> T getHistorical(int artId, int stripeId) {
         return null;
      }

      @Override
      public <T extends ReadableArtifact> T getActive(int artId, int branchId) {
         return null;
      }

      @Override
      public String getSessionId() {
         return "rockin";
      }

   }
}

/*
 * Created on Oct 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal;

import java.util.Hashtable;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.mocks.MockOseeDataAccessor;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.AttributeTypeFactory;
import org.eclipse.osee.framework.core.model.type.OseeEnumType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.DataStoreTypeCache;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeClassResolver;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.PrimitiveAttributeClassProvider;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiUtil;
import org.junit.Ignore;
import org.junit.Rule;

public class MasterLoaderTest {

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

   @Ignore
   @org.junit.Test
   public void testMasterLoader() throws Exception {

      //register required mock services
      DataStoreTypeCache dataStoreTypeCache = new DataStoreTypeCache() {

         ArtifactTypeCache artifactTypeCache = new ArtifactTypeCache(new MockOseeDataAccessor<Long, ArtifactType>());
         AttributeTypeCache attributeTypeCache =
            new AttributeTypeCache(new MockOseeDataAccessor<Long, AttributeType>());
         RelationTypeCache relationTypeCache = new RelationTypeCache(new MockOseeDataAccessor<Long, RelationType>());

         @Override
         public RelationTypeCache getRelationTypeCache() {
            return relationTypeCache;
         }

         @Override
         public AttributeTypeCache getAttributeTypeCache() {
            return attributeTypeCache;
         }

         @Override
         public ArtifactTypeCache getArtifactTypeCache() {
            return artifactTypeCache;
         }
      };

      OsgiUtil.registerService(DataStoreTypeCache.class, dataStoreTypeCache, new Hashtable<String, Object>());
      //      OsgiUtil.registerService(Log.class, new MockLog(), new Hashtable<String, Object>());

      OsgiUtil.getService(IOseeDatabaseService.class);
      OsgiUtil.getService(IdentityService.class);

      //get the needed services
      Log logger = OsgiUtil.getService(Log.class);
      DataLoader dataLoader = OsgiUtil.getService(DataLoader.class);
      IOseeDatabaseService oseeDbService = OsgiUtil.getService(IOseeDatabaseService.class);

      //create all necessary dependencies
      BranchCache branchCache = new BranchCache(new MockOseeDataAccessor<String, Branch>());
      ArtifactFactory artifactFactory = new ArtifactFactory();
      AttributeClassResolver resolver = new AttributeClassResolver();
      resolver.addProvider(new PrimitiveAttributeClassProvider());
      AttributeFactory attributeFactory =
         new AttributeFactory(logger, resolver, dataStoreTypeCache.getAttributeTypeCache());

      //create some attribute types
      setupAttributeTypeCache(dataStoreTypeCache.getAttributeTypeCache());

      //create the loader
      MasterLoader masterLoader =
         new MasterLoader(dataLoader, logger, dataStoreTypeCache.getArtifactTypeCache(), branchCache, artifactFactory,
            attributeFactory);

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

   private void setupAttributeTypeCache(AttributeTypeCache cache) throws OseeCoreException {
      AttributeTypeFactory typeFactory = new AttributeTypeFactory();
      addType(cache, typeFactory, CoreAttributeTypes.Name, "StringAttribute", "DefaultAttributeDataProvider");
      addType(cache, typeFactory, CoreAttributeTypes.UriGeneralStringData, "StringAttribute",
         "DefaultAttributeDataProvider");
      addType(cache, typeFactory, CoreAttributeTypes.Active, "StringAttribute", "DefaultAttributeDataProvider");
      addType(cache, typeFactory, CoreAttributeTypes.DefaultGroup, "StringAttribute", "DefaultAttributeDataProvider");

   }

   private void addType(AttributeTypeCache cache, AttributeTypeFactory typeFactory, IAttributeType type, String attributeType, String providerType) throws OseeCoreException {
      typeFactory.createOrUpdate(cache, type.getGuid(), type.getName(), attributeType, providerType, "name", "name",
         new OseeEnumType(type.getGuid(), type.getName()), 1, 1, "", "");

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

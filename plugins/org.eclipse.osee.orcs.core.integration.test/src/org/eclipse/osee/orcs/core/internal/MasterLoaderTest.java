/*
 * Created on Oct 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.Identity;
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
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.DataStoreTypeCache;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeClassResolver;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.primitives.PrimitiveAttributeClassProvider;
import org.eclipse.osee.orcs.core.internal.search.ResultSetImpl;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.db.mock.OseeDatabase;
import org.eclipse.osee.orcs.db.mock.OsgiUtil;
import org.eclipse.osee.orcs.search.ResultSet;
import org.junit.Rule;

public class MasterLoaderTest {

   @Rule
   public OseeDatabase db = new OseeDatabase("osee.demo.h2");

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

      QueryEngine queryEngine = OsgiUtil.getService(QueryEngine.class);
      CriteriaSet set = new CriteriaSet(new IOseeBranch() {

         @Override
         public String getName() {
            return "branch";
         }

         @Override
         public boolean matches(Identity<?>... identities) {
            return false;
         }

         @Override
         public String getGuid() {
            return "2";
         }
      });
      List<Integer> ids = new ArrayList<Integer>();
      ids.add(7);
      ids.add(8);
      ids.add(9);
      set.add(new CriteriaArtifactIds(ids));
      QueryOptions queryOptions = new QueryOptions();
      Object obj = queryEngine.create("session1", set, queryOptions);
      List<ReadableArtifact> arts = masterLoader.load(obj, options, new MockSessionContext());

      Assert.assertEquals(arts.size(), 3);
      ResultSet resultSet =
         new ResultSetImpl(masterLoader, queryEngine, set, queryOptions, new MockSessionContext(), options);
      List<ReadableArtifact> moreArts = resultSet.getList();
      Assert.assertEquals(3, moreArts.size());
      Assert.assertEquals(3, resultSet.getCount());

      //      Map<Integer, ReadableArtifact> lookup = creatLookup(arts);
      //art 7 has no relations
      //art 8 has 4 
      //      REL_LINK_ID    REL_LINK_TYPE_ID     A_ART_ID    B_ART_ID    RATIONALE   GAMMA_ID    TX_CURRENT     MOD_TYPE    BRANCH_ID   TRANSACTION_ID    GAMMA_ID  
      //      2  397   1  8     36 1  1  2  6  36
      //      3  397   8  16    37 1  1  2  6  37
      //      1  397   8  9     41 1  1  2  6  41
      //      173   397   8  121      699   1  1  2  16
      //art9 has 
      //      REL_LINK_ID    REL_LINK_TYPE_ID     A_ART_ID    B_ART_ID    RATIONALE   GAMMA_ID    TX_CURRENT     MOD_TYPE    BRANCH_ID   TRANSACTION_ID    GAMMA_ID  
      //      1  397   8  9     41 1  1  2  6  41
      //      21 382   9  34    326   1  1  2  14 326
      //      20 382   9  33    327   1  1  2  14 327
      //      23 382   9  36    328   1  1  2  14 328
      //      22 382   9  35    329   1  1  2  14 329
      //      28 382   9  41    334   1  1  2  14 334
      //      29 382   9  42    335   1  1  2  14 335
      //      30 382   9  43    336   1  1  2  14 336
      //      31 382   9  44    337   1  1  2  14 337
      //      24 382   9  37    338   1  1  2  14 338
      //      25 382   9  38    339   1  1  2  14 339
      //      26 382   9  39    340   1  1  2  14 340
      //      27 382   9  40    341   1  1  2  14 341
      //      36 382   9  49    342   1  1  2  14 342
      //      37 382   9  50    343   1  1  2  14 343
      //      38 382   9  51    344   1  1  2  14 344
      //      32 382   9  45    346   1  1  2  14 346
      //      33 382   9  46    347   1  1  2  14 347
      //      34 382   9  47    348   1  1  2  14 348
      //      35 382   9  48    349   1  1  2  14 349
      //      218   382   9  166      898   1  1  2  21 898

      //      Iterable<ReadableArtifact> it =
      //         masterLoader.getIterable(options, 1, artJoinQuery.getQueryId(), new MockSessionContext());
      //
      //      int count = 0;
      //      for (ReadableArtifact readableArtifact : it) {
      //         count++;
      //      }
      //      Assert.assertEquals(count, 3);
      //      artJoinQuery.delete();

   }

   Map<Integer, ReadableArtifact> creatLookup(List<ReadableArtifact> arts) {
      Map<Integer, ReadableArtifact> lookup = new HashMap<Integer, ReadableArtifact>();
      for (ReadableArtifact artifact : arts) {
         lookup.put(artifact.getId(), artifact);
      }
      return lookup;
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

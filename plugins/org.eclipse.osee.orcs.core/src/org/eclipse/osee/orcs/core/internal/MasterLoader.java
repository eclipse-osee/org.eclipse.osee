/*
 * Created on Oct 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactRowHandler;
import org.eclipse.osee.orcs.core.ds.AttributeContainer;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.RelationContainer;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactReciever;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactRowMapper;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeRowMapper;
import org.eclipse.osee.orcs.core.internal.relation.RelationRowMapper;
import org.eclipse.osee.orcs.data.ReadableArtifact;

public class MasterLoader {

   private final DataLoader dataLoader;
   private final Log logger;
   private final ArtifactTypeCache artifactTypeCache;
   private final BranchCache branchCache;
   private final ArtifactFactory artifactFactory;
   private final AttributeFactory attributeFactory;

   MasterLoader(DataLoader dataLoader, Log logger, ArtifactTypeCache artifactTypeCache, BranchCache branchCache, ArtifactFactory artifactFactory, AttributeFactory attributeFactory) {
      this.dataLoader = dataLoader;
      this.artifactTypeCache = artifactTypeCache;
      this.logger = logger;
      this.branchCache = branchCache;
      this.artifactFactory = artifactFactory;
      this.attributeFactory = attributeFactory;
   }

   List<ReadableArtifact> load(LoadOptions options, int fetchSize, int queryId, SessionContext sessionContext) throws OseeCoreException {

      ArtifactRecieveHandler artifactHandler =
         new ArtifactRecieveHandler(options, fetchSize, queryId, dataLoader, logger, attributeFactory);

      ArtifactRowHandler artifactRowHandler =
         new ArtifactRowMapper(logger, sessionContext, branchCache, artifactTypeCache, artifactFactory, artifactHandler);

      dataLoader.loadArtifacts(artifactRowHandler, options, fetchSize, queryId);

      artifactHandler.loadRemaining();
      return artifactHandler.get();

   }

   private static class ArtifactRecieveHandler implements ArtifactReciever {

      private final List<ReadableArtifact> arts = new ArrayList<ReadableArtifact>();
      private final int fetchSize;
      private final LoadOptions options;
      private final int queryId;
      private final DataLoader dataLoader;
      private final Log logger;
      private final AttributeFactory attributeFactory;

      public ArtifactRecieveHandler(LoadOptions options, int fetchSize, int queryId, DataLoader dataLoader, Log logger, AttributeFactory attributeFactory) {
         this.fetchSize = fetchSize;
         this.options = options;
         this.queryId = queryId;
         this.dataLoader = dataLoader;
         this.logger = logger;
         this.attributeFactory = attributeFactory;
      }

      public void loadRemaining() throws OseeCoreException {
         Map<Integer, RelationContainer> relationContainers = new HashMap<Integer, RelationContainer>();
         Map<Integer, AttributeContainer> attributeContainers = new HashMap<Integer, AttributeContainer>();

         for (ReadableArtifact artifact : arts) {
            relationContainers.put(artifact.getId(), ((Artifact) artifact).getRelationContainer());
            attributeContainers.put(artifact.getId(), ((Artifact) artifact).getAttributeContainer());
         }

         AttributeRowMapper attributeHandler = new AttributeRowMapper(logger, attributeFactory, attributeContainers);
         RelationRowMapper relationHandler = new RelationRowMapper(logger, relationContainers);

         dataLoader.loadAttributes(attributeHandler, options, fetchSize, queryId);
         dataLoader.loadRelations(relationHandler, options, fetchSize, queryId);
      }

      @Override
      public void onArtifact(ReadableArtifact artifact, boolean isArtifactAlreadyLoaded) throws OseeCoreException {
         arts.add(artifact);
         if (arts.size() >= fetchSize) {
            loadRemaining();
         }
      }

      List<ReadableArtifact> get() {
         return arts;
      }

   }

}

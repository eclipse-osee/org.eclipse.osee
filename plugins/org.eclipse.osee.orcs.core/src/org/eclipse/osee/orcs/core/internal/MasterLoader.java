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
import org.eclipse.osee.orcs.core.ds.AttributeRowHandler;
import org.eclipse.osee.orcs.core.ds.AttributeRowHandlerFactory;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.RelationContainer;
import org.eclipse.osee.orcs.core.ds.RelationRowHandler;
import org.eclipse.osee.orcs.core.ds.RelationRowHandlerFactory;
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

   public List<ReadableArtifact> load(Object dataStoreContext, LoadOptions loadOptions, SessionContext sessionContext) throws OseeCoreException {
      ArtifactRecieveHandler artifactHandler =
         new ArtifactRecieveHandler(loadOptions, dataLoader, logger, attributeFactory);

      ArtifactRowHandler artifactRowHandler =
         new ArtifactRowMapper(logger, sessionContext, branchCache, artifactTypeCache, artifactFactory, artifactHandler);

      dataLoader.loadArtifacts(artifactRowHandler, dataStoreContext, loadOptions, artifactHandler, artifactHandler);

      return artifactHandler.get();

   }

   private static class ArtifactRecieveHandler implements ArtifactReciever, RelationRowHandlerFactory, AttributeRowHandlerFactory {

      private final List<ReadableArtifact> arts = new ArrayList<ReadableArtifact>();
      private final Log logger;
      private final AttributeFactory attributeFactory;

      public ArtifactRecieveHandler(LoadOptions options, DataLoader dataLoader, Log logger, AttributeFactory attributeFactory) {
         this.logger = logger;
         this.attributeFactory = attributeFactory;
      }

      private RelationRowHandler getRelationRowHandler() {
         Map<Integer, RelationContainer> relationContainers = new HashMap<Integer, RelationContainer>();
         for (ReadableArtifact artifact : arts) {
            relationContainers.put(artifact.getId(), ((Artifact) artifact).getRelationContainer());
         }
         return new RelationRowMapper(logger, relationContainers);
      }

      private AttributeRowHandler getAttributeRowHandler() {
         Map<Integer, AttributeContainer> attributeContainers = new HashMap<Integer, AttributeContainer>();
         for (ReadableArtifact artifact : arts) {
            attributeContainers.put(artifact.getId(), ((Artifact) artifact).getAttributeContainer());
         }
         return new AttributeRowMapper(logger, attributeFactory, attributeContainers);
      }

      @Override
      public AttributeRowHandler createAttributeRowHandler() {
         return getAttributeRowHandler();
      }

      @Override
      public RelationRowHandler createRelationRowHandler() {
         return getRelationRowHandler();
      }

      @Override
      public void onArtifact(ReadableArtifact artifact, boolean isArtifactAlreadyLoaded) throws OseeCoreException {
         arts.add(artifact);
      }

      List<ReadableArtifact> get() {
         return arts;
      }

   }

}

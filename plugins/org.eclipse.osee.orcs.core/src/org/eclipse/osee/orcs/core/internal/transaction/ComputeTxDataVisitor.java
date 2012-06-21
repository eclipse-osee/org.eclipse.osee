/*
 * Created on Jun 22, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.core.internal.transaction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.data.AttributeReadable;

public class ComputeTxDataVisitor implements TxVisitor {

   private final List<ArtifactTransactionData> data = new LinkedList<ArtifactTransactionData>();
   private final ArtifactFactory artifactFactory;
   private final AttributeFactory attributeFactory;

   public ComputeTxDataVisitor(ArtifactFactory artifactFactory, AttributeFactory attributeFactory) {
      super();
      this.artifactFactory = artifactFactory;
      this.attributeFactory = attributeFactory;
   }

   @Override
   public void visit(ArtifactImpl modified) throws OseeCoreException {
      ArtifactTxDataImpl toAdd = getTransferObject(modified);
      if (toAdd != null) {
         data.add(toAdd);
      }
   }

   public void reset() {
      data.clear();
   }

   public List<ArtifactTransactionData> getData() {
      return data;
   }

   private ArtifactTxDataImpl getTransferObject(ArtifactImpl artifact) throws OseeCoreException {
      ArtifactTxDataImpl toReturn = null;

      ArtifactData data = artifact.getOrcsData();
      if (artifact.isDirty() || data.getModType() == ModificationType.REPLACED_WITH_VERSION) {
         boolean persist = true;

         if (artifact.isDeleted()) {
            // always clear dirtys if deleted
            artifact.setAttributesToNotDirty();
            if (!data.getVersion().isInStorage()) {
               //nothing to persist if deleted and not in storage
               persist = false;
            }
         }

         if (persist) {
            toReturn = new ArtifactTxDataImpl(artifactFactory.clone(data));
            for (AttributeReadable<?> attribute : artifact.getAttributesDirty()) {
               Attribute<?> attributeImpl = attributeFactory.asAttributeImpl(attribute);
               toReturn.addAttributeData(attributeFactory.clone(attributeImpl.getOrcsData()));
            }
         }
      }
      //TX_TODO: need to add relations
      return toReturn;
   }

   private static class ArtifactTxDataImpl implements ArtifactTransactionData {

      private final List<AttributeData> attributeData = new LinkedList<AttributeData>();
      private final List<RelationData> relationData = new LinkedList<RelationData>();
      private final ArtifactData artifactData;

      public ArtifactTxDataImpl(ArtifactData artifactData) {
         super();
         this.artifactData = artifactData;
      }

      @Override
      public ArtifactData getArtifactData() {
         return artifactData;
      }

      public void addAttributeData(AttributeData data) {
         attributeData.add(data);
      }

      @Override
      public Collection<AttributeData> getAttributeData() {
         return attributeData;
      }

      @Override
      public Collection<RelationData> getRelationData() {
         return relationData;
      }

      public void addRelationData(RelationData data) {
         //TX_TODO clone the relationData?
         relationData.add(data);
      }
   }

}

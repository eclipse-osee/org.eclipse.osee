package org.eclipse.osee.framework.skynet.core.artifact.cache;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class HistoricalArtifactCache extends AbstractArtifactCache {

   public HistoricalArtifactCache(int initialCapacity) {
      super(initialCapacity);
   }

   @Override
   protected Integer getKey2(Artifact artifact) {
      return artifact.getTransactionNumber();
   }

   public Artifact getById(Integer artId, Integer transactionNumber) {
      return asArtifact(getObjectById(artId, transactionNumber));
   }

   public Artifact getByGuid(String artGuid, Integer transactionNumber) {
      return asArtifact(getObjectByGuid(artGuid, transactionNumber));
   }
}
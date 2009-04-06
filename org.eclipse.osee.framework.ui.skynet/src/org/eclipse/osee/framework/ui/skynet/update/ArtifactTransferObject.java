/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.update;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactTransferObject {
   private Artifact artifact;
   private TransferMessage message;

   public ArtifactTransferObject(Artifact artifact, TransferMessage message) {
      super();
      this.artifact = artifact;
      this.message = message;
   }

   /**
    * @return the artifact
    */
   public Artifact getArtifact() {
      return artifact;
   }

   /**
    * @return the message
    */
   public TransferMessage getMessage() {
      return message;
   }
}

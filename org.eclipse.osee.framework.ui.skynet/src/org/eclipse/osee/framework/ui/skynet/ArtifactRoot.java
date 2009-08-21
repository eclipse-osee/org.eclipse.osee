/*
 * Created on Aug 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author b1528444
 *
 */
public class ArtifactRoot {

   private Artifact artifact;
   
   ArtifactRoot(Artifact artifact){
      this.artifact = artifact;
   }
   
   Artifact getArtifact(){
      return artifact;
   }
}

/*
 * Created on Mar 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.artifact;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class TaskManager {

   public static TaskArtifact cast(Artifact artifact) {
      if (artifact instanceof TaskArtifact) {
         return (TaskArtifact) artifact;
      }
      return null;
   }

}

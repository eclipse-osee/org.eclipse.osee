/*
 * Created on Oct 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.actions;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class SelectedAtsArtifactsAdapter implements ISelectedAtsArtifacts {

   public SelectedAtsArtifactsAdapter() {
   }

   @SuppressWarnings("unused")
   @Override
   public Set<? extends Artifact> getSelectedSMAArtifacts() throws OseeCoreException {
      return Collections.emptySet();
   }

   @SuppressWarnings("unused")
   @Override
   public List<Artifact> getSelectedAtsArtifacts() throws OseeCoreException {
      return Collections.emptyList();
   }

   @SuppressWarnings("unused")
   @Override
   public List<TaskArtifact> getSelectedTaskArtifacts() throws OseeCoreException {
      return Collections.emptyList();
   }

}

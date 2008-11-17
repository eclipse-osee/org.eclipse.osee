/*
 * Created on Jun 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.hyper;

import java.util.Collection;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class TasksActionHyperItem extends ActionHyperItem {

   private final Collection<TaskArtifact> taskArtifacts;

   /**
    * @param hyperartifact
    */
   public TasksActionHyperItem(Collection<TaskArtifact> taskArtifacts) {
      super(new TasksHyperViewArtifact(taskArtifacts));
      this.taskArtifacts = taskArtifacts;
      setRelationToolTip("Task");
   }

   @Override
   public void handleDoubleClick(HyperViewItem hyperViewItem) throws OseeCoreException {
      super.handleDoubleClick(hyperViewItem);
      WorldEditor.open(new WorldEditorSimpleProvider("Tasks", taskArtifacts));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.ActionHyperItem#calculateCurrent(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public void calculateCurrent(Artifact currentArtifact) {
      setCurrent(taskArtifacts.contains(currentArtifact));
   }

}

/*
 * Created on Jan 19, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.task;

import java.util.Collection;
import org.eclipse.osee.ats.artifact.TaskArtifact;

/**
 * @author Megumi Telles
 */
public interface ITaskAction {

   public abstract boolean isValid(Collection<TaskArtifact> tasks);

   public abstract void setXViewer(TaskXViewer viewer);

}

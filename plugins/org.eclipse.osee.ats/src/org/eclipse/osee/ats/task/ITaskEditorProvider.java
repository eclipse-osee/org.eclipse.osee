/*
 * Created on Mar 29, 2016
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.task;

import java.util.Collection;
import org.eclipse.osee.ats.world.IWorldEditorProvider;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public interface ITaskEditorProvider extends IWorldEditorProvider {

   Collection<? extends Artifact> getTaskEditorTaskArtifacts() throws OseeCoreException;

   String getTaskEditorLabel(SearchType searchType);

}

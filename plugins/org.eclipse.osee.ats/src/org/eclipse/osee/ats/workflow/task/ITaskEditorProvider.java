/*
 * Created on Mar 29, 2016
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.task;

import org.eclipse.osee.ats.world.IWorldEditorProvider;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;

public interface ITaskEditorProvider extends IWorldEditorProvider {

   String getTaskEditorLabel(SearchType searchType);

}

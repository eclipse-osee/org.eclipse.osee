/*
 * Created on Nov 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor;

import java.util.Collection;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class TaskEditorSearchItemProvider implements ITaskEditorProvider {

   private final WorldSearchItem worldSearchItem;
   private final TableLoadOption[] tableLoadOptions;

   public TaskEditorSearchItemProvider(WorldSearchItem worldSearchItem) {
      this(worldSearchItem, TableLoadOption.None);
   }

   public TaskEditorSearchItemProvider(WorldSearchItem worldSearchItem, TableLoadOption... tableLoadOptions) {
      this.worldSearchItem = worldSearchItem;
      this.tableLoadOptions = tableLoadOptions;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTableLoadOptions()
    */
   @Override
   public Collection<TableLoadOption> getTableLoadOptions() throws OseeCoreException {
      return Collections.getAggregate(tableLoadOptions);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorLabel()
    */
   @Override
   public String getTaskEditorLabel(SearchType searchType) throws OseeCoreException {
      return worldSearchItem.getSelectedName(searchType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorTaskArtifacts()
    */
   @Override
   public Collection<? extends Artifact> getTaskEditorTaskArtifacts() throws OseeCoreException {
      return worldSearchItem.performSearchGetResults(false, SearchType.ReSearch);
   }

}

/*
 * Created on Nov 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.task;

import java.util.Collection;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class TaskEditorParameterSearchItemProvider implements ITaskEditorProvider {

   private final TaskEditorParameterSearchItem worldParameterSearchItem;
   private final TableLoadOption[] tableLoadOptions;

   public TaskEditorParameterSearchItemProvider(TaskEditorParameterSearchItem worldParameterSearchItem) {
      this(worldParameterSearchItem, TableLoadOption.None);
   }

   public TaskEditorParameterSearchItemProvider(TaskEditorParameterSearchItem worldParameterSearchItem, TableLoadOption... tableLoadOptions) {
      this.worldParameterSearchItem = worldParameterSearchItem;
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
      return worldParameterSearchItem.getTaskEditorLabel(searchType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorTaskArtifacts()
    */
   @Override
   public Collection<? extends Artifact> getTaskEditorTaskArtifacts() throws OseeCoreException {
      return worldParameterSearchItem.getTaskEditorTaskArtifacts();
   }

   public boolean isFirstTime() {
      return worldParameterSearchItem.isFirstTime();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.ITaskEditorProvider#getName()
    */
   @Override
   public String getName() throws OseeCoreException {
      return worldParameterSearchItem.getName();
   }

   /**
    * @return the worldSearchItem
    */
   public TaskEditorParameterSearchItem getWorldSearchItem() {
      return worldParameterSearchItem;
   }

}

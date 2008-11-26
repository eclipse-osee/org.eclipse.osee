/*
 * Created on Nov 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.task;

import java.util.Collection;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;

/**
 * @author Donald G. Dunne
 */
public class TaskEditorParameterSearchItemProvider extends TaskEditorProvider {

   private final TaskEditorParameterSearchItem taskParameterSearchItem;

   public TaskEditorParameterSearchItemProvider(TaskEditorParameterSearchItem worldParameterSearchItem) {
      this(worldParameterSearchItem, null, TableLoadOption.None);
   }

   public TaskEditorParameterSearchItemProvider(TaskEditorParameterSearchItem taskParameterSearchItem, CustomizeData customizeData, TableLoadOption... tableLoadOptions) {
      super(customizeData, tableLoadOptions);
      this.taskParameterSearchItem = taskParameterSearchItem;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorLabel()
    */
   @Override
   public String getTaskEditorLabel(SearchType searchType) throws OseeCoreException {
      return taskParameterSearchItem.getTaskEditorLabel(searchType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorTaskArtifacts()
    */
   @Override
   public Collection<? extends Artifact> getTaskEditorTaskArtifacts() throws OseeCoreException {
      return taskParameterSearchItem.getTaskEditorTaskArtifacts();
   }

   public boolean isFirstTime() {
      return taskParameterSearchItem.isFirstTime();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.ITaskEditorProvider#getName()
    */
   @Override
   public String getName() throws OseeCoreException {
      return taskParameterSearchItem.getName();
   }

   /**
    * @return the worldSearchItem
    */
   public TaskEditorParameterSearchItem getWorldSearchItem() {
      return taskParameterSearchItem;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.ITaskEditorProvider#copyProvider()
    */
   @Override
   public ITaskEditorProvider copyProvider() {
      return new TaskEditorParameterSearchItemProvider((TaskEditorParameterSearchItem) taskParameterSearchItem.copy(),
            customizeData, tableLoadOptions);
   }

}

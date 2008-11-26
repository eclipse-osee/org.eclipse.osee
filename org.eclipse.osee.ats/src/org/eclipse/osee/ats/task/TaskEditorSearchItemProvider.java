/*
 * Created on Nov 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.task;

import java.util.Collection;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldUISearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;

/**
 * @author Donald G. Dunne
 */
public class TaskEditorSearchItemProvider extends TaskEditorProvider {

   private final WorldSearchItem worldSearchItem;

   public TaskEditorSearchItemProvider(WorldSearchItem worldSearchItem) {
      this(worldSearchItem, null, TableLoadOption.None);
   }

   public TaskEditorSearchItemProvider(WorldSearchItem worldSearchItem, CustomizeData customizeData, TableLoadOption... tableLoadOptions) {
      super(customizeData, tableLoadOptions);
      this.worldSearchItem = worldSearchItem;
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
      if (worldSearchItem instanceof WorldUISearchItem) {
         return ((WorldUISearchItem) worldSearchItem).performSearchGetResults(false, SearchType.ReSearch);
      } else
         throw new OseeStateException("Unsupported WorldSearchItem");
   }

   /**
    * @return the worldSearchItem
    */
   public WorldSearchItem getWorldSearchItem() {
      return worldSearchItem;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.ITaskEditorProvider#getName()
    */
   @Override
   public String getName() throws OseeCoreException {
      return worldSearchItem.getName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.ITaskEditorProvider#copyProvider()
    */
   @Override
   public ITaskEditorProvider copyProvider() {
      return new TaskEditorSearchItemProvider(worldSearchItem.copy(), customizeData, tableLoadOptions);
   }

}

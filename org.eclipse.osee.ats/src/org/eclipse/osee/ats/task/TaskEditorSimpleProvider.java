/*
 * Created on Nov 7, 2008
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
public class TaskEditorSimpleProvider extends TaskEditorProvider {

   private final String name;
   private final Collection<? extends Artifact> artifacts;

   public TaskEditorSimpleProvider(String name, Collection<? extends Artifact> artifacts) {
      this(name, artifacts, null, TableLoadOption.None);
   }

   public TaskEditorSimpleProvider(String name, Collection<? extends Artifact> artifacts, CustomizeData customizeData, TableLoadOption... tableLoadOption) {
      super(customizeData, tableLoadOption);
      this.name = name;
      this.artifacts = artifacts;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorLabel(org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType)
    */
   @Override
   public String getTaskEditorLabel(SearchType searchType) throws OseeCoreException {
      return name;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorTaskArtifacts()
    */
   @Override
   public Collection<? extends Artifact> getTaskEditorTaskArtifacts() throws OseeCoreException {
      return artifacts;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.ITaskEditorProvider#getName()
    */
   @Override
   public String getName() throws OseeCoreException {
      return name;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.ITaskEditorProvider#copyProvider()
    */
   @Override
   public ITaskEditorProvider copyProvider() {
      return new TaskEditorSimpleProvider(name, artifacts, customizeData, tableLoadOptions);
   }

}

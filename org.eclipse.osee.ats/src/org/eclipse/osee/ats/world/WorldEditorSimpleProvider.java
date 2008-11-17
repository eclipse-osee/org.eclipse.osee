/*
 * Created on Nov 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.Collection;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorSimpleProvider implements IWorldEditorProvider {

   private final String name;
   private final Collection<? extends Artifact> artifacts;
   private final TableLoadOption[] tableLoadOption;
   private final CustomizeData customizeData;

   public WorldEditorSimpleProvider(String name, Collection<? extends Artifact> artifacts) {
      this(name, artifacts, null, TableLoadOption.None);
   }

   public WorldEditorSimpleProvider(String name, Collection<? extends Artifact> artifacts, CustomizeData customizeData, TableLoadOption... tableLoadOption) {
      this.name = name;
      this.artifacts = artifacts;
      this.customizeData = customizeData;
      this.tableLoadOption = tableLoadOption;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTableLoadOptions()
    */
   @Override
   public Collection<TableLoadOption> getTableLoadOptions() throws OseeCoreException {
      return Collections.getAggregate(tableLoadOption);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorLabel(org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType)
    */
   @Override
   public String getWorldEditorLabel(SearchType searchType) throws OseeCoreException {
      return name;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.ITaskEditorProvider#getTaskEditorTaskArtifacts()
    */
   @Override
   public Collection<? extends Artifact> getWorldEditorArtifacts(SearchType searchType) throws OseeCoreException {
      return artifacts;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#getCustomizeData()
    */
   @Override
   public CustomizeData getCustomizeData() throws OseeCoreException {
      return customizeData;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#getParameterXWidgetXml()
    */
   @Override
   public String getParameterXWidgetXml() throws OseeCoreException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#isCancelled()
    */
   @Override
   public boolean isCancelled() throws OseeCoreException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#performUI(org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType)
    */
   @Override
   public void performUI(SearchType searchType) throws OseeCoreException {
   }

}

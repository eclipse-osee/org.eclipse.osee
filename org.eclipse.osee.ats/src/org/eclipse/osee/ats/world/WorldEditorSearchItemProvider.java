/*
 * Created on Nov 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.Collection;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorSearchItemProvider implements IWorldEditorProvider {

   private final WorldSearchItem worldSearchItem;
   private final TableLoadOption[] tableLoadOptions;
   private final CustomizeData customizeData;

   public WorldEditorSearchItemProvider(WorldSearchItem worldSearchItem) {
      this(worldSearchItem, null, TableLoadOption.None);
   }

   public WorldEditorSearchItemProvider(WorldSearchItem worldSearchItem, CustomizeData customizeData, TableLoadOption... tableLoadOptions) {
      this.worldSearchItem = worldSearchItem;
      this.customizeData = customizeData;
      this.tableLoadOptions = tableLoadOptions;
   }

   /**
    * @return the worldSearchItem
    */
   public WorldSearchItem getWorldSearchItem() {
      return worldSearchItem;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IWorldEditorProvider#getTableLoadOptions()
    */
   @Override
   public Collection<TableLoadOption> getTableLoadOptions() throws OseeCoreException {
      return Collections.getAggregate(tableLoadOptions);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IWorldEditorProvider#getTaskEditorLabel()
    */
   @Override
   public String getWorldEditorLabel(SearchType searchType) throws OseeCoreException {
      return worldSearchItem.getSelectedName(searchType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.editor.IWorldEditorProvider#getWorldEditorArtifacts()
    */
   @Override
   public Collection<? extends Artifact> getWorldEditorArtifacts(SearchType searchType) throws OseeCoreException {
      return worldSearchItem.performSearchGetResults(false, SearchType.ReSearch);
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
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#performUI()
    */
   @Override
   public void performUI(SearchType searchType) throws OseeCoreException {
      worldSearchItem.performUI(searchType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#isCancelled()
    */
   @Override
   public boolean isCancelled() throws OseeCoreException {
      return worldSearchItem.isCancelled();
   }

}

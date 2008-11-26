/*
 * Created on Nov 22, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldEditorProvider implements IWorldEditorProvider {

   protected TableLoadOption[] tableLoadOptions;
   protected CustomizeData customizeData;

   public WorldEditorProvider(CustomizeData customizeData, TableLoadOption[] tableLoadOptions) {
      this.customizeData = customizeData;
      this.tableLoadOptions = tableLoadOptions;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#getSelectedName(org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType)
    */
   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return getName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#getTargetedVersionArtifact()
    */
   @Override
   public VersionArtifact getTargetedVersionArtifact() throws OseeCoreException {
      return null;
   }

   /**
    * @return the tableLoadOptions
    */
   public TableLoadOption[] getTableLoadOptions() {
      return tableLoadOptions;
   }

   /**
    * @return the customizeData
    */
   public CustomizeData getCustomizeData() {
      return customizeData;
   }

   /**
    * @param tableLoadOptions the tableLoadOptions to set
    */
   public void setTableLoadOptions(TableLoadOption... tableLoadOptions) {
      this.tableLoadOptions = tableLoadOptions;
   }

   /**
    * @param customizeData the customizeData to set
    */
   public void setCustomizeData(CustomizeData customizeData) {
      this.customizeData = customizeData;
   }

}

/*
 * Created on Nov 6, 2008
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
public interface IWorldEditorProvider {

   public void run(WorldEditor worldEditor, SearchType searchType, boolean forcePend) throws OseeCoreException;

   public String getSelectedName(SearchType searchType) throws OseeCoreException;

   public String getName() throws OseeCoreException;

   public VersionArtifact getTargetedVersionArtifact() throws OseeCoreException;

   public IWorldEditorProvider copyProvider();

   public void setCustomizeData(CustomizeData customizeData);

   public void setTableLoadOptions(TableLoadOption... tableLoadOptions);
}

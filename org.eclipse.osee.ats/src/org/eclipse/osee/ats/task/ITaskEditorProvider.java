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
public interface ITaskEditorProvider {

   public Collection<? extends Artifact> getTaskEditorTaskArtifacts() throws OseeCoreException;

   public String getTaskEditorLabel(SearchType searchType) throws OseeCoreException;

   public String getName() throws OseeCoreException;

   public Collection<TableLoadOption> getTableLoadOptions() throws OseeCoreException;

   public void setCustomizeData(CustomizeData customizeData);

   public void setTableLoadOptions(TableLoadOption... tableLoadOptions);

   public ITaskEditorProvider copyProvider();
}

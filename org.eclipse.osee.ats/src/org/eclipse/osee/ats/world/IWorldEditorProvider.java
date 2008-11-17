/*
 * Created on Nov 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.Collection;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;

/**
 * @author Donald G. Dunne
 */
public interface IWorldEditorProvider {

   public Collection<? extends Artifact> getWorldEditorArtifacts(SearchType searchType) throws OseeCoreException;

   public String getWorldEditorLabel(SearchType searchType) throws OseeCoreException;

   public Collection<TableLoadOption> getTableLoadOptions() throws OseeCoreException;

   public CustomizeData getCustomizeData() throws OseeCoreException;

   public String getParameterXWidgetXml() throws OseeCoreException;

   public void performUI(SearchType searchType) throws OseeCoreException;

   public boolean isCancelled() throws OseeCoreException;
}

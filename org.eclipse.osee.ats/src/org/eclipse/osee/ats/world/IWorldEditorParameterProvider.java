/*
 * Created on Nov 18, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.Collection;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;

/**
 * @author Donald G. Dunne
 */
public interface IWorldEditorParameterProvider extends IWorldEditorProvider {

   public String getParameterXWidgetXml() throws OseeCoreException;

   public Collection<? extends Artifact> performSearchGetResults(SearchType searchType) throws OseeCoreException;

   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener();

   public String[] getWidgetOptions(DynamicXWidgetLayoutData xWidgetData);
}

/*
 * Created on Dec 8, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorldEditorItem {

   public List<XViewerColumn> getXViewerColumns() throws OseeCoreException;

}

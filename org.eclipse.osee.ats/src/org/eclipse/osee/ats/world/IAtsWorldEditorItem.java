/*
 * Created on Dec 8, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorldEditorItem {

   public List<XViewerColumn> getXViewerColumns() throws OseeCoreException;

   public boolean isXColumnProvider(XViewerColumn xCol) throws OseeCoreException;

   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException;

   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException;

   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws OseeCoreException;

   public List<? extends Action> getWorldMenuActions(WorldComposite worldComposite) throws OseeCoreException;

}

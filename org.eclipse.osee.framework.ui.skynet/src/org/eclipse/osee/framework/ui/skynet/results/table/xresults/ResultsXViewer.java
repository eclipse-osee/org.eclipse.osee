/*
 * Created on Jun 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results.table.xresults;

import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class ResultsXViewer extends XViewer {

   /**
    * @param parent
    * @param style
    * @param namespace
    * @param viewerFactory
    */
   public ResultsXViewer(Composite parent, int style, List<XViewerColumn> xColumns) {
      super(parent, style, new ResultsXViewerFactory(xColumns));
   }

}

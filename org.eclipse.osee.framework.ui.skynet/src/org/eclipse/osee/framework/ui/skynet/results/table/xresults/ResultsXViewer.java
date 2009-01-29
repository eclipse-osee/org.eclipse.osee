/*
 * Created on Jun 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results.table.xresults;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

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

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.XViewer#handleDoubleClick()
    */
   @Override
   public void handleDoubleClick() {
      if (getSelectedRows().size() > 0) {
         Artifact art = getSelectedRows().iterator().next().getDoubleClickOpenArtifact();
         if (art != null) {
            RendererManager.openInJob(art, PresentationType.GENERALIZED_EDIT);
         }
      }
   }

   public ArrayList<ResultsXViewerRow> getSelectedRows() {
      ArrayList<ResultsXViewerRow> arts = new ArrayList<ResultsXViewerRow>();
      TreeItem items[] = getTree().getSelection();
      for (TreeItem item : items)
         arts.add((ResultsXViewerRow) item.getData());
      return arts;
   }
}

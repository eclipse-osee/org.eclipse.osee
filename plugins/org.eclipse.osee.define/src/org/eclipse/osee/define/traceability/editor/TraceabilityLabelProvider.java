/*
 * Created on Jan 31, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.traceability.editor;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewer;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TraceabilityLabelProvider extends ResultsXViewerLabelProvider {

   public TraceabilityLabelProvider(ResultsXViewer resultsXViewer) {
      super(resultsXViewer);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) throws Exception {
      if (col.getName().equals("Requirement")) {
         if (element instanceof ResultsXViewerRow) {
            Object data = ((ResultsXViewerRow) element).getData();
            if (data instanceof Artifact) {
               return ArtifactImageManager.getImage(((Artifact) data).getArtifactTypeToken());
            }
         }
      }
      return null;
   }

}

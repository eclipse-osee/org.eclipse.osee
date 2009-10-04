/*
 * Created on Oct 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor.xmerge;

import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class CoverageMergeXViewer extends CoverageXViewer {

   public CoverageMergeXViewer(Composite parent, int style, XCoverageMergeViewer xCoverageMergeViewer) {
      this(parent, style, new CoverageMergeXViewerFactory(), xCoverageMergeViewer);
   }

   public CoverageMergeXViewer(Composite parent, int style, IXViewerFactory xViewerFactory, XCoverageMergeViewer xCoverageMergeViewer) {
      super(parent, style, xViewerFactory, xCoverageMergeViewer);
   }

}

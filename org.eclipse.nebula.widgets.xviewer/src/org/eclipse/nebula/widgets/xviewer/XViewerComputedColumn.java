/*
 * Created on Aug 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer;

/**
 * @author Donald G. Dunne
 */
public abstract class XViewerComputedColumn extends XViewerValueColumn {

   protected XViewerColumn sourceXViewerColumn;
   protected XViewer xViewer;

   public XViewerComputedColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   public XViewerComputedColumn(XViewer viewer, String xml) {
      super(viewer, xml);
   }

   public XViewerColumn getSourceXViewerColumn() {
      return sourceXViewerColumn;
   }

   public void setSourceXViewerColumn(XViewerColumn sourceXViewerColumn) {
      this.sourceXViewerColumn = sourceXViewerColumn;
   }

   public abstract boolean isApplicableFor(XViewerColumn xViewerColumn);

   public XViewer getXViewer() {
      return xViewer;
   }

   @Override
   public void setXViewer(XViewer xViewer) {
      this.xViewer = xViewer;
   }

   @Override
   public abstract XViewerComputedColumn copy();

}

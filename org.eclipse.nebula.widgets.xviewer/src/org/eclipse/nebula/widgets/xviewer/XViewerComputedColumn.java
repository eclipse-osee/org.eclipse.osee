/*
 * Created on Aug 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer;

import java.util.Collection;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;

/**
 * @author Donald G. Dunne
 */
public abstract class XViewerComputedColumn extends XViewerValueColumn {

   protected XViewerColumn sourceXViewerColumn;
   protected XViewer xViewer;
   private final Pattern idPattern = Pattern.compile("^.*\\((.*?)\\)$");

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
      id = getId();
      name = getName();
   }

   public String getSourceColumnId() {
      if (sourceXViewerColumn != null) {
         return sourceXViewerColumn.getId();
      }
      Matcher matcher = idPattern.matcher(id);
      if (matcher.find()) {
         return matcher.group(1);
      }
      return null;
   }

   public void setSourceXViewerColumnFromColumns(Collection<XViewerColumn> xViewerColumns) {
      String sourceColumnId = getSourceColumnId();
      if (sourceColumnId == null) {
         XViewerLog.log(Activator.class, Level.SEVERE, "Invalid null sourceColumnId");
         return;
      }
      for (XViewerColumn xCol : xViewerColumns) {
         if (xCol.getId().equals(sourceColumnId)) {
            setSourceXViewerColumn(xCol);
            return;
         }
      }
      XViewerLog.log(Activator.class, Level.SEVERE, String.format(
            "Can't resolve sourceColumn for XViewerComputedColumn [%s]", this));

   }

   public abstract boolean isApplicableFor(XViewerColumn xViewerColumn);

   public abstract boolean isApplicableFor(String storedId);

   public abstract XViewerComputedColumn createFromStored(XViewerColumn storedColumn);

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

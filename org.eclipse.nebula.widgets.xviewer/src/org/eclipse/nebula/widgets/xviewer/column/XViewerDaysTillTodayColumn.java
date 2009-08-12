/*
 * Created on Aug 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.column;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerComputedColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerDaysTillTodayColumn extends XViewerComputedColumn {

   private final static SimpleDateFormat format10 = new SimpleDateFormat("MM/dd/yyyy");

   public XViewerDaysTillTodayColumn() {
      super("ats.computed.daysTillToday", "Days Till Today", 30, SWT.LEFT, false, SortDataType.Integer, false,
            "Shows number of days till today for selected column");
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      if (sourceXViewerColumn == null) {
         return "Source column not set";
      }
      try {
         String dateStr =
               ((XViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(element, sourceXViewerColumn,
                     columnIndex);
         if (dateStr == null || dateStr.equals("")) {
            return "";
         }
         DateFormat format;
         if (dateStr.length() == 10) {
            format = format10;
         } else {
            format = SimpleDateFormat.getInstance();
         }
         Date date1Date = null;
         try {
            date1Date = format.parse(dateStr);
         } catch (ParseException ex) {
            try {
               date1Date = SimpleDateFormat.getInstance().parse(dateStr);
            } catch (ParseException ex2) {
               XViewerLog.log(Activator.class, Level.SEVERE, ex2);
            }
         }
         if (date1Date == null) {
            return "Can't parse date";
         }
         return String.valueOf(XViewerLib.daysTillToday(date1Date));
      } catch (Exception ex) {
         return ex.getLocalizedMessage();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.XViewerComputedColumn#isApplicableFor(org.eclipse.nebula.widgets.xviewer.XViewerColumn)
    */
   @Override
   public boolean isApplicableFor(XViewerColumn xViewerColumn) {
      return xViewerColumn.getSortDataType() == SortDataType.Date;
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.XViewerColumn#getName()
    */
   @Override
   public String getName() {
      return super.getName() + " from " + sourceXViewerColumn.getName() + "";
   }

   @Override
   public XViewerDaysTillTodayColumn copy() {
      XViewerDaysTillTodayColumn col = new XViewerDaysTillTodayColumn();
      col.setXViewer(getXViewer());
      col.setSourceXViewerColumn(getSourceXViewerColumn());
      return col;
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.XViewerColumn#getId()
    */
   @Override
   public String getId() {
      if (sourceXViewerColumn == null) {
         return super.getId();
      }
      return super.getId() + "(" + sourceXViewerColumn.getId() + ")";
   }

}

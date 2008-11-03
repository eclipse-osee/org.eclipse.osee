/*
 * Created on Nov 2, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xbargraph;

import java.util.Map;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Donald G. Dunne
 */
public class XBarGraphTable extends XWidget {

   private final String itemHeader;
   private final String percentHeader;
   private final Map<String, Integer> itemToValueMap;
   private Table table;
   private final String valuePostFix;

   public XBarGraphTable(String label, String itemHeader, String percentHeader, Map<String, Integer> itemToValueMap) {
      this(label, itemHeader, percentHeader, itemToValueMap, "%");
   }

   public XBarGraphTable(String label, String itemHeader, String percentHeader, Map<String, Integer> itemToValueMap, String valuePostFix) {
      super(label);
      this.itemHeader = itemHeader;
      this.percentHeader = percentHeader;
      this.itemToValueMap = itemToValueMap;
      this.valuePostFix = valuePostFix;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#createWidgets(org.eclipse.swt.widgets.Composite, int)
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {
      labelWidget = new Label(parent, SWT.NONE);
      labelWidget.setText(label + ": ");

      table = new Table(parent, SWT.BORDER);
      table.setHeaderVisible(true);
      table.setLinesVisible(true);
      if (isFillHorizontally()) {
         table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      }
      TableColumn column1 = new TableColumn(table, SWT.NONE);
      column1.setText(itemHeader);
      column1.setWidth(300);
      final TableColumn column2 = new TableColumn(table, SWT.NONE);
      column2.setText(percentHeader);
      column2.setWidth(500);
      for (String itemName : itemToValueMap.keySet()) {
         TableItem item = new TableItem(table, SWT.NONE);
         item.setText(itemName);
      }

      /*
       * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
       * Therefore, it is critical for performance that these methods be
       * as efficient as possible.
       */
      table.addListener(SWT.PaintItem, new Listener() {
         Integer[] percents = itemToValueMap.values().toArray(new Integer[itemToValueMap.size()]);

         public void handleEvent(Event event) {
            if (event.index == 1) {
               GC gc = event.gc;
               TableItem item = (TableItem) event.item;
               int index = table.indexOf(item);
               int percent = percents[index];
               Color foreground = gc.getForeground();
               Color background = gc.getBackground();
               if (valuePostFix.equals("%") && percent == 100) {
                  gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
                  gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
               } else {
                  gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
                  gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
               }
               int width = (column2.getWidth() - 1) * percent / 100;
               gc.fillGradientRectangle(event.x, event.y, width, event.height, true);
               Rectangle rect2 = new Rectangle(event.x, event.y, width - 1, event.height - 1);
               gc.drawRectangle(rect2);
               gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
               String text = percent + valuePostFix;
               Point size = event.gc.textExtent(text);
               int offset = Math.max(0, (event.height - size.y) / 2);
               gc.drawText(text, event.x + 2, event.y + offset, true);
               gc.setForeground(background);
               gc.setBackground(foreground);
            }
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#dispose()
    */
   @Override
   public void dispose() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return table;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getReportData()
    */
   @Override
   public String getReportData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getXmlData()
    */
   @Override
   public String getXmlData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#isValid()
    */
   @Override
   public Result isValid() {
      return Result.TrueResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#refresh()
    */
   @Override
   public void refresh() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#setFocus()
    */
   @Override
   public void setFocus() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#setXmlData(java.lang.String)
    */
   @Override
   public void setXmlData(String str) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#toHTML(java.lang.String)
    */
   @Override
   public String toHTML(String labelFont) {
      return null;
   }

}

/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xbargraph;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Donald G. Dunne
 */
public class XBarGraphTable extends XWidget {

   private final String itemHeader;
   private final String percentHeader;
   private Table table;
   private final List<XBarGraphLine> lines;
   private boolean isHeaderVisible = true;
   private boolean isLinesVisible = true;

   public XBarGraphTable(String label, String itemHeader, String percentHeader, List<XBarGraphLine> lines) {
      super(label);
      this.itemHeader = itemHeader;
      this.percentHeader = percentHeader;
      this.lines = lines;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#createControls(org.eclipse.swt.widgets.Composite, int)
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      labelWidget = new Label(parent, SWT.NONE);
      labelWidget.setText(getLabel() + ": ");

      table = new Table(parent, SWT.BORDER);
      table.setHeaderVisible(isHeaderVisible);
      table.setLinesVisible(isLinesVisible);
      if (isFillHorizontally()) {
         table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      }
      TableColumn column1 = new TableColumn(table, SWT.NONE);
      column1.setText(itemHeader);
      column1.setWidth(300);
      final TableColumn column2 = new TableColumn(table, SWT.NONE);
      column2.setText(percentHeader);
      column2.setWidth(500);
      for (XBarGraphLine line : lines) {
         TableItem item = new TableItem(table, SWT.NONE);
         item.setText(line.name);
      }

      /*
       * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
       * Therefore, it is critical for performance that these methods be
       * as efficient as possible.
       */
      table.addListener(SWT.PaintItem, new Listener() {
         public void handleEvent(Event event) {
            if (event.index == 1) {
               GC gc = event.gc;
               TableItem item = (TableItem) event.item;
               int index = table.indexOf(item);
               XBarGraphLine line = lines.get(index);
               int cummulativeWidth = 0;
               for (XBarGraphLineSegment seg : line.segments) {
                  Color foreground = gc.getForeground();
                  Color background = gc.getBackground();
                  gc.setForeground(Display.getCurrent().getSystemColor(seg.foreground));
                  gc.setBackground(Display.getCurrent().getSystemColor(seg.background));
                  int width = (column2.getWidth()) * (int) seg.value / 100;
                  gc.fillGradientRectangle(event.x + cummulativeWidth, event.y, width, event.height, true);
                  Rectangle rect2 = new Rectangle(event.x + cummulativeWidth, event.y, width - 1, event.height - 1);
                  gc.drawRectangle(rect2);
                  if (seg.name != null && !seg.name.equals("")) {
                     gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
                     Point size = event.gc.textExtent(seg.name);
                     int offset = Math.max(0, (event.height - size.y) / 2);
                     gc.drawText(seg.name, event.x + cummulativeWidth + 5, event.y + offset, true);
                     gc.setForeground(background);
                     gc.setBackground(foreground);
                  }
                  cummulativeWidth += width;
               }
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
   public IStatus isValid() {
      return Status.OK_STATUS;
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

   public static void main(String[] args) {
      final Display display = new Display();
      Shell shell = new Shell(display);
      shell.setLayout(new GridLayout(1, false));
      shell.setText("Show results as a bar chart in Table");

      List<XBarGraphLine> lines = new ArrayList<XBarGraphLine>();
      lines.add(XBarGraphLine.getPercentLine("Fix", 34));
      lines.add(XBarGraphLine.getPercentLine("Improvement", 100));
      lines.add(new XBarGraphLine("Support", SWT.COLOR_GREEN, SWT.COLOR_YELLOW, SWT.COLOR_RED, SWT.COLOR_YELLOW, 33,
            "33%", "67%"));
      List<XBarGraphLineSegment> segments = new ArrayList<XBarGraphLineSegment>();
      segments.add(XBarGraphLineSegment.getPercentSegment("23%", SWT.COLOR_GREEN, 23));
      segments.add(XBarGraphLineSegment.getPercentSegment("45%", SWT.COLOR_BLUE, 45));
      segments.add(XBarGraphLineSegment.getPercentSegment("20%", SWT.COLOR_YELLOW, 20));
      segments.add(XBarGraphLineSegment.getPercentSegment("12%", SWT.COLOR_MAGENTA, 12));
      lines.add(new XBarGraphLine("Other", segments));
      XBarGraphTable table = new XBarGraphTable("By Improvement", "", "Percent", lines);
      table.createWidgets(shell, 1);

      shell.pack();
      shell.open();
      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) display.sleep();
      }
      display.dispose();
   }

   /**
    * @return the isHeaderVisible
    */
   public boolean isHeaderVisible() {
      return isHeaderVisible;
   }

   /**
    * @param isHeaderVisible the isHeaderVisible to set
    */
   public void setHeaderVisible(boolean isHeaderVisible) {
      this.isHeaderVisible = isHeaderVisible;
   }

   /**
    * @return the isLinesVisible
    */
   public boolean isLinesVisible() {
      return isLinesVisible;
   }

   /**
    * @param isLinesVisible the isLinesVisible to set
    */
   public void setLinesVisible(boolean isLinesVisible) {
      this.isLinesVisible = isLinesVisible;
   }
}

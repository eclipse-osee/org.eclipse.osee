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
package org.eclipse.osee.framework.ui.skynet.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osee.framework.core.client.ServiceHealthManager;
import org.eclipse.osee.framework.core.client.ServiceStatus;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Roberto E. Escobar
 */
public class ConfigurationDetails extends PreferencePage implements IWorkbenchPreferencePage {
   private static final Image STATUS_OK = SkynetGuiPlugin.getInstance().getImage("green_light.gif");
   private static final Image STATUS_ERROR = SkynetGuiPlugin.getInstance().getImage("red_light.gif");
   private static final int TEXT_MARGIN = 10;
   private Table table;

   public ConfigurationDetails() {
      super();
      this.table = null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
    */
   @Override
   protected Control createContents(Composite parent) {

      Composite content = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      content.setLayout(layout);
      content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Group composite = new Group(content, SWT.NONE);
      GridLayout layout1 = new GridLayout();
      layout1.marginHeight = 0;
      layout1.marginWidth = 0;
      composite.setLayout(layout1);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setText("Connections");

      TableViewer tableViewer = new TableViewer(composite, SWT.READ_ONLY);
      tableViewer.setContentProvider(new ArrayContentProvider());
      table = tableViewer.getTable();
      table.setLayout(new GridLayout());
      table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      table.setHeaderVisible(true);
      table.setLinesVisible(true);

      TableViewerColumn tc1 = new TableViewerColumn(tableViewer, SWT.LEFT);
      TableViewerColumn tc2 = new TableViewerColumn(tableViewer, SWT.LEFT);
      TableViewerColumn tc3 = new TableViewerColumn(tableViewer, SWT.CENTER);

      tc1.getColumn().setText("Type");
      tc2.getColumn().setText("Info");
      tc3.getColumn().setText("Status");

      tc1.getColumn().setWidth(125);
      tableViewer.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(ViewerCell cell) {
            DataRecord record = (DataRecord) cell.getElement();
            switch (cell.getColumnIndex()) {
               case 0:
                  String text = record.getLabel() + ":";
                  cell.setText(text);
                  cell.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE));
                  break;
               case 1:
                  cell.setText(record.getData());
                  cell.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
                  break;
               case 2:
                  cell.setImage(record.getStatus() ? STATUS_OK : STATUS_ERROR);
                  break;
               default:
                  break;
            }
         }
      });

      tableViewer.setInput(getConfigurationDetails());

      Listener paintListener = new PaintListener();
      table.addListener(SWT.MeasureItem, paintListener);
      table.addListener(SWT.EraseItem, paintListener);
      table.addListener(SWT.PaintItem, paintListener);

      pack();
      return content;
   }

   private List<DataRecord> getConfigurationDetails() {
      List<DataRecord> configurationDetails = new ArrayList<DataRecord>();

      Collection<ServiceStatus> serviceInfos = ServiceHealthManager.getServiceStatus();
      for (ServiceStatus serviceInfo : serviceInfos) {
         DataRecord record = new DataRecord(serviceInfo.getName(), serviceInfo.getDetails());
         record.setStatus(serviceInfo.isHealthOk());
         configurationDetails.add(record);
      }
      return configurationDetails;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
    */
   public void init(IWorkbench workbench) {
      setPreferenceStore(SkynetActivator.getInstance().getPreferenceStore());
      setDescription("See below for OSEE configuration details.");
   }

   private void pack() {
      for (int i = 0; i < 3; i++) {
         table.getColumn(i).pack();
      }
      table.pack();
   }

   private class DataRecord {
      private String label;
      private String data;
      private boolean status;

      DataRecord(String label, String data) {
         this.label = label;
         this.data = data != null ? data : "";
         this.status = false;
      }

      public String getLabel() {
         return label;
      }

      public String getData() {
         return data;
      }

      public boolean getStatus() {
         return status;
      }

      public void setStatus(boolean status) {
         this.status = status;
      }
   }

   private final class PaintListener implements Listener {
      public void handleEvent(Event event) {

         switch (event.type) {
            case SWT.MeasureItem: {
               TableItem item = (TableItem) event.item;

               Image image = item.getImage(event.index);
               if (image != null) {
                  Rectangle rect = image.getBounds();
                  event.width += rect.width;
                  event.height = Math.max(event.height, rect.height + 2);
               }

               String text = getText(event.gc, item, event.index);
               if (text != null) {
                  Point size = event.gc.textExtent(text);
                  event.width = size.x;
                  event.height = Math.max(event.height, size.y + 2);
               }
               break;
            }
            case SWT.PaintItem: {
               TableItem item = (TableItem) event.item;
               String text = getText(event.gc, item, event.index);
               if (text != null) {

                  Point size = event.gc.textExtent(text);
                  int offset2 = event.index == 0 ? Math.min(0, (event.height - size.y) / 2) : 0;
                  event.gc.drawText(text, event.x + TEXT_MARGIN, event.y + offset2, true);
               }
               Image image = item.getImage(event.index);
               if (image != null) {
                  int x = event.x + event.width;
                  Rectangle rect = image.getBounds();
                  int offset = Math.max(0, (event.height - rect.height) / 2);
                  event.gc.drawImage(image, x, event.y + offset);
               }
               break;
            }
            case SWT.EraseItem: {
               event.detail &= ~SWT.FOREGROUND;
               break;
            }
         }
      }

      String getText(GC gc, TableItem item, int columnIndex) {
         String text = item.getText(columnIndex);
         if (text != null && columnIndex < 2) {
            int pixelWidth = item.getTextBounds(columnIndex + 1).x - item.getTextBounds(columnIndex).x;
            Point size1 = gc.textExtent(text);

            double charactersToPixels = (double) text.length() / (double) size1.x;
            int charactersAllowed = (int) (pixelWidth * charactersToPixels) - TEXT_MARGIN;
            if (charactersAllowed > 0) {
               StringBuilder builder = new StringBuilder();
               int count = 0;
               for (int index = 0; index < text.length(); index++) {
                  builder.append(text.charAt(index));
                  if (count == charactersAllowed) {
                     count = 0;
                     builder.append("\n");
                  }
                  count++;
               }
               text = builder.toString().trim();
            }
         }
         return text;
      }
   }

}

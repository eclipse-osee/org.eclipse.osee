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
package org.eclipse.osee.framework.ui.service.control.renderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.entry.Comment;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;
import org.apache.commons.lang.StringUtils;
import org.eclipse.osee.framework.jini.service.core.FormmatedEntry;
import org.eclipse.osee.framework.jini.service.core.GroupEntry;
import org.eclipse.osee.framework.jini.service.core.OwnerEntry;
import org.eclipse.osee.framework.jini.service.core.PropertyEntry;
import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Roberto E. Escobar
 */
public class ServiceItemHandler implements IRenderer {

   List<ItemRecord> serviceRecords;

   public ServiceItemHandler(ServiceItem serviceItem) {
      serviceRecords = new ArrayList<ItemRecord>();
      parseServiceItem(serviceItem);
   }

   private class ItemEntry {
      String value;
      private final int style;
      private final int color;

      public ItemEntry(String value, int style, int color) {
         this.value = value;
         this.style = style;
         this.color = color;
      }

      public int getColor() {
         return color;
      }

      public int getStyle() {
         return style;
      }

      public String getValue() {
         return value;
      }
   }

   private class ItemRecord {
      private final ItemEntry label;
      private final ItemEntry data;

      public ItemRecord(String label, String data) {
         this(label, SWT.BOLD, SWT.COLOR_DARK_BLUE, data, SWT.NORMAL, SWT.COLOR_BLACK);
      }

      public ItemRecord(String label, int labelStyle, int labelColor, String data) {
         this(label, labelStyle, labelColor, data, SWT.NORMAL, SWT.COLOR_BLACK);
      }

      public ItemRecord(String label, String data, int dataStyle, int dataColor) {
         this(label, SWT.BOLD, SWT.COLOR_DARK_BLUE, data, dataStyle, dataColor);
      }

      public ItemRecord(String label, int labelStyle, int labelColor, String data, int dataStyle, int dataColor) {
         this.label = new ItemEntry(label, labelStyle, labelColor);
         this.data = new ItemEntry(data, dataStyle, dataColor);
      }

      public String getData() {
         return data.getValue();
      }

      public String getLabel() {
         return label.getValue();
      }

      public int getDataColor() {
         return data.getColor();
      }

      public int getDataStyle() {
         return data.getStyle();
      }

      public int getLabelColor() {
         return label.getColor();
      }

      public int getLabelStyle() {
         return label.getStyle();
      }
   }

   public void parseServiceItem(ServiceItem serviceItem) {
      serviceRecords.clear();
      if (serviceItem != null) {
         String additionalInfo = "";
         Entry[] entries = serviceItem.attributeSets;
         for (int i = 0; i < entries.length; i++) {
            if (entries[i] instanceof Name) {
               serviceRecords.add(new ItemRecord("Name", ((Name) entries[i]).name));
            } else if (entries[i] instanceof Comment) {
               serviceRecords.add(new ItemRecord("Comment", ((Comment) entries[i]).comment));
            } else if (entries[i] instanceof OwnerEntry) {
               serviceRecords.add(new ItemRecord("Owner", ((OwnerEntry) entries[i]).getOwner()));
            } else if (entries[i] instanceof GroupEntry) {
               String[] groups = ((GroupEntry) entries[i]).group;
               serviceRecords.add(new ItemRecord("Group",
                     "{ " + (groups == null ? "" : StringUtils.join(groups, ",")) + " }", SWT.BOLD,
                     SWT.COLOR_DARK_GREEN));
            } else if (entries[i] instanceof FormmatedEntry) {
               additionalInfo += ((FormmatedEntry) entries[i]).getFormmatedString();
            } else if (entries[i] instanceof ServiceInfo) {
               ServiceInfo info = (ServiceInfo) entries[i];
               serviceRecords.add(new ItemRecord("Info Name", info.name));
               serviceRecords.add(new ItemRecord("Manufacturer", info.manufacturer));
               serviceRecords.add(new ItemRecord("Model", info.model));
               serviceRecords.add(new ItemRecord("Version", info.version));
               serviceRecords.add(new ItemRecord("SerialNumber", info.serialNumber));
            } else if (entries[i] instanceof PropertyEntry) {
               PropertyEntry info = (PropertyEntry) entries[i];
               String[] keys = info.map.keySet().toArray(new String[info.map.keySet().size()]);
               Arrays.sort(keys);
               for (String key : keys) {
                  serviceRecords.add(new ItemRecord(key, info.map.get(key).toString()));
               }
            }
         }

         if (entries.length == 0) {
            String label = "";
            try {
               label = serviceItem.service.getClass().getName();
            } catch (Exception ex) {
               label = "nullpointerexception";
            }
            serviceRecords.add(new ItemRecord("Name", label));
         }
         if (!additionalInfo.equals("")) {
            serviceRecords.add(new ItemRecord("Additional Info", additionalInfo));
         }
         serviceRecords.add(new ItemRecord("ID", serviceItem.serviceID.toString()));
      }
   }

   public List<ItemRecord> getData() {
      return this.serviceRecords;
   }

   public Control renderInComposite(Composite parent) {
      if (parent instanceof FormattedText) {
         FormattedText textArea = (FormattedText) parent;
         textArea.clearTextArea();

         for (ItemRecord record : serviceRecords) {
            String label = record.getLabel();
            String data = record.getData();
            if (label.equals("Additional Info")) {
               String[] temp = data.split("\n");

               textArea.addText("\t" + label + ": \n", record.getLabelStyle(), record.getLabelColor());

               for (String innerRecord : temp) {
                  String[] array = innerRecord.split(":", 2);
                  for (int i = 0; i < array.length; i++) {
                     textArea.addText("\t\t\t" + array[i] + ": ", SWT.BOLD, SWT.COLOR_DARK_BLUE);
                     if (i + 1 < array.length) {
                        textArea.addText(" " + array[++i] + "\n", SWT.NORMAL, SWT.COLOR_BLACK);
                     }
                  }
               }
            } else {
               textArea.addText("\t" + label + ": ", record.getLabelStyle(), record.getLabelColor());
               textArea.addText(data + "\n", record.getDataStyle(), record.getDataColor());
            }
         }
      }
      return parent;
   }

   @Override
   public String toString() {
      String toReturn = "";
      for (ItemRecord record : serviceRecords) {
         String label = record.getLabel();
         String data = record.getData();
         if (label.equals("Additional Info")) {
            String[] temp = data.split("\n");
            toReturn += label + ": \n";
            for (String innerRecord : temp) {
               toReturn += "\t" + innerRecord + "\n";
            }
         } else {
            toReturn += label + ": " + data + "\n";
         }
      }
      return toReturn;
   }
}

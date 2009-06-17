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
package org.eclipse.osee.ote.ui.test.manager.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


/**
 * @author Roberto E. Escobar
 */
public class StatusWindowWidget {
   private Map<String, Map<EntryAttribute, Object>> labelValueMap;
   private List<String> keys;
   private FormattedText statusTextArea;

   private enum EntryAttribute {
      LABEL, VALUE, STYLE, COLOR;
   }

   public StatusWindowWidget(Composite parent) {
      keys = new ArrayList<String>();
      labelValueMap = new HashMap<String, Map<EntryAttribute, Object>>();
      statusTextArea = new FormattedText(parent, SWT.BORDER, SWT.DEFAULT, SWT.DEFAULT, false);
      statusTextArea.setTextAreaBackground(SWT.COLOR_WHITE);
   }

   public void setLabelAndValue(String key, String label, String value, int style, int color) {
      if (!keys.contains(key)) {
         keys.add(key);
      }
      Map<EntryAttribute, Object> entry = new HashMap<EntryAttribute, Object>();
      entry.put(EntryAttribute.LABEL, label);
      entry.put(EntryAttribute.VALUE, value);
      entry.put(EntryAttribute.STYLE, new Integer(style));
      entry.put(EntryAttribute.COLOR, new Integer(color));

      labelValueMap.put(key, entry);
   }

   public void setLabelAndValue(String key, String label, String value) {
      setLabelAndValue(key, label, value, SWT.NORMAL, SWT.COLOR_BLACK);
   }

   public String getValue(String key) {
      Map<EntryAttribute, Object> entry = labelValueMap.get(key);
      EntryAttribute attribute = EntryAttribute.VALUE;
      return ((entry != null && entry.get(attribute) != null) ? (String) entry.get(attribute) : "");
   }

   public String getLabel(String key) {
      Map<EntryAttribute, Object> entry = labelValueMap.get(key);
      EntryAttribute attribute = EntryAttribute.LABEL;
      return ((entry != null && entry.get(attribute) != null) ? (String) entry.get(attribute) : "");
   }

   public void setLabel(String key, String label) {
      Map<EntryAttribute, Object> entry = labelValueMap.get(key);
      if (entry != null) {
         entry.put(EntryAttribute.LABEL, label);
      }
      else {
         setLabelAndValue(key, label, "");
      }
   }

   public void setValueStyle(String key, int style) {
      Map<EntryAttribute, Object> entry = labelValueMap.get(key);
      if (entry != null) {
         entry.put(EntryAttribute.STYLE, new Integer(style));
      }
   }

   public void setValueColor(String key, int color) {
      Map<EntryAttribute, Object> entry = labelValueMap.get(key);
      if (entry != null) {
         entry.put(EntryAttribute.STYLE, new Integer(color));
      }
   }

   public void setValue(String key, String value, int style, int color) {
      Map<EntryAttribute, Object> entry = labelValueMap.get(key);
      if (entry != null) {
         entry.put(EntryAttribute.VALUE, value);
         entry.put(EntryAttribute.STYLE, new Integer(style));
         entry.put(EntryAttribute.COLOR, new Integer(color));
      }
      else {
         setLabelAndValue(key, "", value, style, color);
      }
   }

   public void setValue(String key, String value) {
      Map<EntryAttribute, Object> entry = labelValueMap.get(key);
      if (entry != null) {
         entry.put(EntryAttribute.VALUE, value);
      }
      else {
         setLabelAndValue(key, "", value);
      }
   }

   public void refresh() {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            statusTextArea.clearTextArea();
            for (String key : keys) {
               Map<EntryAttribute, Object> entry = labelValueMap.get(key);
               if (entry != null) {
                  String label = (String) entry.get(EntryAttribute.LABEL);
                  String value = (String) entry.get(EntryAttribute.VALUE);
                  Integer style = (Integer) entry.get(EntryAttribute.STYLE);
                  Integer color = (Integer) entry.get(EntryAttribute.COLOR);
                  statusTextArea.addText("\t" + label + ": ", SWT.BOLD, SWT.COLOR_DARK_BLUE);
                  statusTextArea.addText(value + "\n", style, color);
               }
            }
         }
      });
   }
}

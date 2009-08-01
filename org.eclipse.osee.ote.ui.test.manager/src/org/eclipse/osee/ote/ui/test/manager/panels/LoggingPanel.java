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
package org.eclipse.osee.ote.ui.test.manager.panels;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Roberto E. Escobar
 */
public class LoggingPanel extends Composite {

   private enum LoggingLevel {
      Minimal(Level.WARNING), Detailed(Level.INFO), All(Level.ALL);

      private Level level;

      LoggingLevel(Level level) {
         this.level = level;
      }

      public Level getLevel() {
         return level;
      }

      public static LoggingLevel fromLevel(Level value) {
         LoggingLevel toReturn = LoggingLevel.Detailed;
         if (value != null) {
            for (LoggingLevel formatType : LoggingLevel.values()) {
               if (formatType.getLevel().equals(value)) {
                  toReturn = formatType;
                  break;
               }
            }
         }
         return toReturn;
      }

      public static LoggingLevel fromString(String value) {
         LoggingLevel toReturn = LoggingLevel.Detailed;
         if (Strings.isValid(value) != false) {
            for (LoggingLevel formatType : LoggingLevel.values()) {
               if (formatType.name().equalsIgnoreCase(value)) {
                  toReturn = formatType;
                  break;
               }
            }
         }
         return toReturn;
      }
   }

   private Map<LoggingLevel, Button> buttonMap;
   private LoggingLevel lastSelected;

   public LoggingPanel(Composite parent, int style) {
      super(parent, style);
      GridLayout gl = new GridLayout();
      gl.marginHeight = 0;
      gl.marginWidth = 0;
      this.setLayout(gl);
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      createControl(this);
   }

   private void createControl(Composite parent) {
      LoggingLevel[] levels = LoggingLevel.values();
      this.buttonMap = new HashMap<LoggingLevel, Button>();
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout gl = new GridLayout();
      gl.marginHeight = 0;
      gl.marginWidth = 0;
      composite.setLayout(gl);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

      for (int index = 0; index < levels.length; index++) {
         LoggingLevel level = levels[index];

         Button button = new Button(composite, SWT.RADIO);
         button.setData(level);
         button.setText(level.name());
         button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               Object object = e.getSource();
               if (object instanceof Button) {
                  setSelected((Button) object);
               }
            }

         });
         boolean isDefault = index == 1;
         button.setSelection(isDefault);
         if (isDefault != false) {
            lastSelected = level;
         }
         buttonMap.put(level, button);
      }
   }

   private void setSelected(Button button) {
      if (button.getSelection() != false) {
         lastSelected = (LoggingLevel) button.getData();
      }
   }

   public String getSelected() {
      return lastSelected.getLevel().toString();
   }

   public void setSelected(String value) {
      Level level = LoggingLevel.Detailed.getLevel();
      try {
         level = Level.parse(value);
      } catch (Exception ex) {
         OseeLog.log(TestManagerPlugin.class, Level.WARNING, String.format("Error parsing log level [%s] using default [%s]", value, level));
      }

      LoggingLevel loggingLevel = LoggingLevel.fromLevel(level);
      this.lastSelected = loggingLevel;
      for (LoggingLevel keys : buttonMap.keySet()) {
         Button button = buttonMap.get(keys);
         button.setSelection(keys.equals(loggingLevel));
      }
   }

}
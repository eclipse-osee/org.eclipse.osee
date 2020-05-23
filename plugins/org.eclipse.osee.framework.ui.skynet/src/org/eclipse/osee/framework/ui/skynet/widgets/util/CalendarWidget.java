/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Roberto E. Escobar
 */
public class CalendarWidget extends Composite {

   public static interface CalendarListener {
      void dateChanged(Calendar calendar);
   }

   private final List<CalendarListener> listeners = new CopyOnWriteArrayList<>();
   private Text text;
   private Calendar calendar;

   public CalendarWidget(Composite parent, int style) {
      super(parent, SWT.NONE);
      GridLayout gridLayout = ALayout.getZeroMarginLayout();
      gridLayout.horizontalSpacing = 0;
      this.setLayout(gridLayout);
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      calendar = Calendar.getInstance();
      calendar.clear();
      createControl(this);
   }

   private void createControl(final Composite parent) {
      GridLayout layout = ALayout.getZeroMarginLayout(3, false);
      layout.horizontalSpacing = 0;

      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      text = new Text(composite, SWT.SINGLE | SWT.BORDER);
      text.setEditable(false);
      createButtons(composite);
      setCalendar(getCalendar());
   }

   private void createButtons(final Composite parent) {
      final ToolBar toolBar = new ToolBar(parent, SWT.FLAT);

      ToolItem dropDown = new ToolItem(toolBar, SWT.DROP_DOWN);
      dropDown.setImage(ImageManager.getImage(FrameworkImage.CALENDAR));
      dropDown.setToolTipText("click to select date");
      dropDown.addSelectionListener(new SelectionAdapter() {
         DateTimePanel panel = null;

         @Override
         public void widgetSelected(SelectionEvent event) {
            if (panel == null) {
               try {
                  panel = new DateTimePanel(parent);
                  panel.setVisible(false);
                  panel.pack();
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
            final ToolItem toolItem = (ToolItem) event.widget;
            Rectangle rect = toolItem.getBounds();
            Point pt = new Point(rect.x - 1, rect.y + rect.height - 1);
            pt = text.toDisplay(pt);

            panel.setLocation(pt.x, pt.y);
            panel.setVisible(true);
            panel.forceFocus();
         }
      });
   }

   public Calendar getCalendar() {
      return calendar;
   }

   public Date getDate() {
      return getCalendar().getTime();
   }

   public boolean isValid() {
      return calendar.isSet(Calendar.DATE);
   }

   public void setCalendar(Calendar calendar) {
      if (calendar != null) {
         this.calendar = calendar;
         String current = text.getText();
         if (current == null) {
            current = "";
         }
         String newText = getDateAsString();
         if (!current.equals(newText)) {
            setText(newText);
            notifyListeners();
         }
      } else {
         clearText();
      }
   }

   public void setDate(Date date) {
      if (date != null) {
         Calendar calendar = getCalendar();
         calendar.setTime(date);
         setCalendar(calendar);
      } else {
         clearText();
      }
   }

   protected void setText(String string) {
      if (Widgets.isAccessible(text)) {
         text.setText(string);
         text.selectAll();
      }
   }

   protected void clearText() {
      Calendar calendar = getCalendar();
      calendar.clear();
      setText("");
      notifyListeners();
   }

   public String getDateAsString() {
      String toReturn = "";
      if (isValid()) {
         Calendar calendar = getCalendar();
         SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
         try {
            toReturn = sdf.format(calendar.getTime());
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return toReturn;
   }

   private void notifyListeners() {
      final Calendar calendar = isValid() ? (Calendar) getCalendar().clone() : null;
      for (CalendarListener listener : listeners) {
         try {
            listener.dateChanged(calendar);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

   }

   public void addCalendarListener(CalendarListener listener) {
      if (listener != null) {
         if (!listeners.contains(listener)) {
            listeners.add(listener);
         }
      }
   }

   public void removeCalendarListener(CalendarListener listener) {
      if (listener != null) {
         listeners.remove(listener);
      }
   }

   public void addModifyListener(ModifyListener listener) {
      if (listener != null && text != null) {
         text.addModifyListener(listener);
      }
   }

   public void removeModifyListener(ModifyListener listener) {
      if (listener != null && text != null) {
         text.removeModifyListener(listener);
      }
   }

   private final class DateTimePanel {

      private final Shell shell;
      private DateTime dateTime;

      public DateTimePanel(Composite parent) {
         shell = new Shell(parent.getShell(), SWT.MENU | SWT.BORDER | SWT.NO_TRIM | SWT.SHEET);
         GridLayout layout = ALayout.getZeroMarginLayout();

         shell.setLayout(layout);
         shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
         shell.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));
         createControl(shell);

         parent.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
               if (isShellValid()) {
                  shell.dispose();
               }
            }
         });
      }

      private boolean isShellValid() {
         return Widgets.isAccessible(shell);
      }

      public void forceFocus() {
         if (isShellValid()) {
            shell.forceFocus();
         }
      }

      public void setLocation(int x, int y) {
         if (isShellValid()) {
            shell.setLocation(x, y);
         }
      }

      public void pack() {
         if (isShellValid()) {
            shell.pack();
         }
      }

      public void setVisible(boolean visible) {
         if (visible && Widgets.isAccessible(dateTime)) {
            if (isValid()) {
               Calendar calendar = getCalendar();
               dateTime.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                  calendar.get(Calendar.DAY_OF_MONTH));
            }
            dateTime.setFocus();
         }
         if (isShellValid()) {
            shell.setVisible(visible);
         }
      }

      private void createControl(Composite parent) {
         dateTime = new DateTime(parent, SWT.CALENDAR);

         Composite buttons = new Composite(parent, SWT.NONE);
         GridLayout layout = ALayout.getZeroMarginLayout(3, false);
         layout.horizontalSpacing = 0;
         buttons.setLayout(layout);
         buttons.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));
         buttons.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));

         Button clear = new Button(buttons, SWT.PUSH);
         clear.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
         clear.setImage(ImageManager.getImage(FrameworkImage.ERASE));
         clear.setToolTipText("clear date field");
         clear.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               clearText();
               if (Widgets.isAccessible(text)) {
                  text.setFocus();
               }
               setVisible(false);
            }
         });

         Button ok = new Button(buttons, SWT.PUSH);
         ok.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
         ok.setText("Ok");
         ok.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               Calendar calendar = Calendar.getInstance();
               calendar.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
               setCalendar(calendar);
               if (Widgets.isAccessible(text)) {
                  text.setFocus();
               }
               setVisible(false);
            }
         });
         Button cancel = new Button(buttons, SWT.PUSH);
         cancel.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
         cancel.setText("Cancel");
         cancel.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               if (Widgets.isAccessible(text)) {
                  text.setFocus();
               }
               setVisible(false);
            }
         });
      }
   }

}

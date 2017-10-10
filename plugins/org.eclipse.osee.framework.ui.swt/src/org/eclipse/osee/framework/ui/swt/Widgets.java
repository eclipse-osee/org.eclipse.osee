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
package org.eclipse.osee.framework.ui.swt;

import java.util.regex.Pattern;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * Utility class that provides helper functions for SWT widgets
 *
 * @author Ken J. Aguilar
 */
public final class Widgets {
   private static Composite targetContainer;

   public static abstract class IntegerTextEntryHandler implements FocusListener, VerifyListener, TraverseListener {
      private static final Pattern NEGATIVE_PATTERN = Pattern.compile("\\-?|\\-?\\d+");
      private static final Pattern NON_NEGATIVE_PATTERN = Pattern.compile("\\d+");

      private String currentTxt;
      private final Pattern pattern;
      private final Text txt;
      private final StringBuilder str;

      public IntegerTextEntryHandler(Text txt, boolean allowNegatives, int limit) {
         this.txt = txt;
         txt.addFocusListener(this);
         txt.addVerifyListener(this);
         txt.addTraverseListener(this);
         str = new StringBuilder(limit + 2);
         if (allowNegatives) {
            pattern = NEGATIVE_PATTERN;
         } else {
            pattern = NON_NEGATIVE_PATTERN;
         }
      }

      @Override
      public void verifyText(VerifyEvent e) {
         String text = txt.getText();
         str.replace(0, text.length(), text);
         str.setLength(text.length());
         if (e.start == e.end) {
            str.insert(e.start, e.text);
         } else {
            str.replace(e.start, e.end, e.text);
         }
         final String result = str.toString();
         if (result.equals("")) {
            e.doit = true;
         } else {
            e.doit = verifyValue(result);
         }
      }

      @Override
      public void focusGained(FocusEvent e) {
         currentTxt = txt.getText();
         txt.selectAll();
      }

      @Override
      public void focusLost(FocusEvent e) {
         try {
            long value = Long.parseLong(txt.getText());
            applyValue(value);
         } catch (NumberFormatException ex) {
            txt.setText(currentTxt);
         }
      }

      public abstract void applyValue(long value);

      public Long getPreviousValue() {
         if (!currentTxt.equals("")) {
            return Long.parseLong(currentTxt);
         } else {
            return null;
         }
      }

      @Override
      public void keyTraversed(TraverseEvent e) {
         switch (e.detail) {
            case SWT.TRAVERSE_ESCAPE:
               txt.setText(currentTxt);
               break;
            case SWT.TRAVERSE_RETURN:
               try {
                  Long value = Long.parseLong(txt.getText());
                  applyValue(value);
                  currentTxt = txt.getText();
               } catch (NumberFormatException ex) {
                  txt.setText(currentTxt);
               }
               break;
         }
      }

      private boolean verifyValue(String value) {
         return pattern.matcher(value).matches();
      }

   }

   public static abstract class RealNumberTextEntryHandler implements FocusListener, VerifyListener, TraverseListener {
      private static final Pattern NON_NEGATIVE_PATTERN = Pattern.compile("\\d*\\.?\\d*");
      private static final Pattern NEGATIVE_PATTERN = Pattern.compile("[\\-.]?|\\-?\\d*\\.?\\d*");
      private String currentTxt;
      private final Pattern pattern;
      private final Text txt;
      private final StringBuilder str;

      public RealNumberTextEntryHandler(Text txt, boolean allowNegatives, int limit) {
         this.txt = txt;
         txt.addFocusListener(this);
         txt.addVerifyListener(this);
         txt.addTraverseListener(this);
         str = new StringBuilder(limit + 2);
         if (allowNegatives) {
            pattern = NEGATIVE_PATTERN;
         } else {
            pattern = NON_NEGATIVE_PATTERN;
         }
      }

      @Override
      public void verifyText(VerifyEvent e) {
         String text = txt.getText();
         str.replace(0, text.length(), text);
         str.setLength(text.length());
         if (e.start == e.end) {
            str.insert(e.start, e.text);
         } else {
            str.replace(e.start, e.end, e.text);
         }
         final String result = str.toString();
         if (result.equals("")) {
            e.doit = true;
         } else {
            e.doit = verifyValue(result);
         }
      }

      @Override
      public void focusGained(FocusEvent e) {
         currentTxt = txt.getText();
         txt.selectAll();
      }

      @Override
      public void focusLost(FocusEvent e) {
         try {
            double value = Double.parseDouble(txt.getText());
            applyValue(value);
         } catch (NumberFormatException ex) {
            txt.setText(currentTxt);
         }
      }

      public Double getPreviousValue() {
         if (!currentTxt.equals("")) {
            return Double.parseDouble(currentTxt);
         } else {
            return null;
         }
      }

      public abstract void applyValue(double value);

      @Override
      public void keyTraversed(TraverseEvent e) {
         switch (e.detail) {
            case SWT.TRAVERSE_ESCAPE:
               txt.setText(currentTxt);
               break;
            case SWT.TRAVERSE_RETURN:
               try {
                  double value = Double.parseDouble(txt.getText());
                  applyValue(value);
                  currentTxt = txt.getText();
               } catch (NumberFormatException ex) {
                  txt.setText(currentTxt);
               }
               break;
         }
      }

      private boolean verifyValue(String value) {
         return pattern.matcher(value).matches();
      }

   }

   public static void setTargetContainer(Composite target) {
      targetContainer = target;
   }

   /**
    * Positions the specified control within a grid layout
    *
    * @return Return grid data reference
    */
   public static GridData positionGridItem(Control control, boolean grabHExcess, boolean grabVExcess, int halign, int valign, int hspan) {
      final GridData gd = new GridData();
      gd.grabExcessHorizontalSpace = grabHExcess;
      gd.grabExcessVerticalSpace = grabVExcess;
      gd.horizontalAlignment = halign;
      gd.horizontalSpan = hspan;
      gd.verticalAlignment = valign;
      control.setLayoutData(gd);
      return gd;
   }

   /**
    * Positions the specified control within a grid layout
    *
    * @return Return grid data reference
    */
   public static GridData positionGridItem(final Control control, final boolean grabHExcess, final boolean grabVExcess, final int halign, final int valign, final int hspan, final int vspan) {

      final GridData gd = new GridData();
      gd.grabExcessHorizontalSpace = grabHExcess;
      gd.grabExcessVerticalSpace = grabVExcess;
      gd.horizontalAlignment = halign;
      gd.horizontalSpan = hspan;
      gd.verticalAlignment = valign;
      gd.verticalSpan = vspan;
      control.setLayoutData(gd);
      return gd;
   }

   /**
    * Positions the specified control within a grid layout
    *
    * @return return grid data reference
    */
   public static GridData positionGridItem(Control control, boolean grabHExcess, int halign, int valign, int hspan) {
      final GridData gd = new GridData();
      gd.grabExcessHorizontalSpace = grabHExcess;
      gd.horizontalAlignment = halign;
      gd.horizontalSpan = hspan;
      gd.verticalAlignment = valign;
      control.setLayoutData(gd);
      return gd;
   }

   /**
    * Positions the specified control within a grid layout
    *
    * @return return grid data reference
    */
   public static GridData positionGridItem(final Control control, final boolean grabHExcess, final int halign, final int valign) {
      final GridData gd = new GridData();
      gd.grabExcessHorizontalSpace = grabHExcess;
      gd.horizontalAlignment = halign;
      gd.verticalAlignment = valign;
      control.setLayoutData(gd);
      return gd;
   }

   /**
    * Positions the specified control within a grid layout
    *
    * @return return grid data reference
    */
   public static GridData positionGridItem(Control control, boolean grabHExcess, int halign, int valign, int hspan, int vspan) {
      final GridData gd = new GridData();
      gd.grabExcessHorizontalSpace = grabHExcess;
      gd.horizontalAlignment = halign;
      gd.horizontalSpan = hspan;
      gd.verticalAlignment = valign;
      gd.verticalSpan = vspan;
      control.setLayoutData(gd);
      return gd;
   }

   /**
    * Positions the specified control within a grid layout
    *
    * @return return grid data reference
    */
   public static GridData positionGridItem(Control control, boolean grabHExcess, int halign, int valign, int hspan, int vspan, int width) {
      final GridData gd = new GridData();
      gd.grabExcessHorizontalSpace = grabHExcess;
      gd.horizontalAlignment = halign;
      gd.horizontalSpan = hspan;
      gd.verticalAlignment = valign;
      gd.verticalSpan = vspan;
      gd.widthHint = width;
      control.setLayoutData(gd);
      return gd;
   }

   /**
    * Positions the specified control within a grid layout
    *
    * @return return grid data reference
    */
   public static GridData positionGridItem(Control control, boolean grabHExcess, int halign, int valign, int hspan, int vspan, int width, int height) {
      final GridData gd = new GridData();
      gd.grabExcessHorizontalSpace = grabHExcess;
      gd.horizontalAlignment = halign;
      gd.horizontalSpan = hspan;
      gd.verticalAlignment = valign;
      gd.verticalSpan = vspan;
      gd.heightHint = height;
      gd.widthHint = width;
      control.setLayoutData(gd);
      return gd;
   }

   public static GridData positionGridItem(Control control, boolean grabHExcess, boolean grabVExcess, int halign, int valign, int hspan, int vspan, int width, int height) {
      final GridData gd = new GridData();
      gd.grabExcessHorizontalSpace = grabHExcess;
      gd.grabExcessVerticalSpace = grabVExcess;
      gd.horizontalAlignment = halign;
      gd.horizontalSpan = hspan;
      gd.verticalAlignment = valign;
      gd.verticalSpan = vspan;
      gd.heightHint = height;
      gd.widthHint = width;
      control.setLayoutData(gd);
      return gd;
   }

   public static Text createTxt(String defaultTxt, int limit) {
      final Text txt = new Text(targetContainer, SWT.BORDER);
      txt.setTextLimit(limit);
      txt.setText(defaultTxt);
      return txt;
   }

   /**
    * @return Return composite text
    */
   public static Text createTxt(Composite comp, String defaultTxt, int limit) {
      final Text txt = new Text(comp, SWT.BORDER);
      txt.setTextLimit(limit);
      txt.setText(defaultTxt);
      return txt;
   }

   public static Text createTxt(Composite comp, final int style, String defaultTxt, int limit) {
      final Text txt = new Text(comp, style);
      txt.setTextLimit(limit);
      txt.setText(defaultTxt);
      return txt;
   }

   public static Text createTxt(Composite comp, final int style, String defaultTxt) {
      final Text txt = new Text(comp, style);
      txt.setText(defaultTxt);
      return txt;
   }

   /**
    * Creates a text box that only accepts numbers
    *
    * @return Return numeric text
    */
   public static Text createNumericTxt(Composite comp, String defaultTxt, int limit) {
      return createNumericTxt(comp, defaultTxt, limit, SWT.BORDER);
   }

   public static Text createNumericTxt(Composite comp, String defaultTxt, int limit, int style) {
      final Text txtBox = new Text(comp, style);
      txtBox.setTextLimit(limit);
      txtBox.setText(defaultTxt);
      txtBox.addVerifyListener(new VerifyListener() {
         @Override
         public void verifyText(final VerifyEvent e) {
            final StringBuilder str = new StringBuilder(txtBox.getText());
            if (e.start == e.end) {
               // user is typing a character
               str.insert(e.start, e.text);
            } else {
               // user is replacing a range of values
               str.replace(e.start, e.end, e.text);
            }
            final String txt = str.toString();
            e.doit = txt.matches("\\d*");
         }
      });
      return txtBox;
   }

   public static Text createDecimalText(Composite comp, String defaultTxt, int min, int max, int limit, int style) {
      final Text txtBox = new Text(comp, style);
      txtBox.setTextLimit(limit);
      txtBox.setText(defaultTxt);
      txtBox.addVerifyListener(new VerifyListener() {
         @Override
         public void verifyText(final VerifyEvent e) {
            final StringBuilder str = new StringBuilder(txtBox.getText());
            if (e.start == e.end) {
               // user is typing a character
               str.insert(e.start, e.text);
            } else {
               // user is replacing a range of values
               str.replace(e.start, e.end, e.text);
            }
            final String txt = str.toString();
            e.doit = txt.matches("\\d*");
         }
      });
      return txtBox;
   }

   public static Label createLbl(String txt, int align) {
      Label lbl = new Label(targetContainer, align);
      lbl.setText(txt);
      return lbl;
   }

   public static Label createLbl(Composite item, String txt, int align) {
      final Label lbl = new Label(item, align);
      lbl.setText(txt);
      return lbl;
   }

   public static Group createGrp(Composite item, String txt) {
      Group grp = new Group(item, SWT.NONE);
      grp.setText(txt);
      return grp;

   }

   public static Button createBtn(int style, String txt) {
      final Button btn = new Button(targetContainer, style);
      btn.setText(txt);
      return btn;
   }

   public static Button createBtn(Composite comp, int style, String txt) {
      final Button btn = new Button(comp, style);
      btn.setText(txt);
      return btn;
   }

   public static Button createBtn(Composite comp, int style, String txt, SelectionListener listener) {
      final Button btn = new Button(comp, style);
      btn.setText(txt);
      btn.addSelectionListener(listener);
      return btn;
   }

   public static Group createGrp(String txt) {
      final Group grp = new Group(targetContainer, SWT.NONE);
      grp.setText(txt);
      return grp;
   }

   public static GridLayout setGridLayout(final Composite item, int numColumns, int columnSpacing) {
      final GridLayout layout = new GridLayout();
      layout.numColumns = numColumns;
      layout.horizontalSpacing = columnSpacing;
      item.setLayout(layout);
      return layout;
   }

   public static RowLayout setRowLayout(final Composite item, final int style, final boolean wrap, final boolean pack) {
      final RowLayout layout = new RowLayout(style);
      layout.wrap = wrap;
      layout.pack = pack;
      item.setLayout(layout);
      return layout;
   }

   public static RowLayout setRowLayout(final Composite item, final int style, final boolean wrap, final boolean pack, boolean fill) {
      final RowLayout layout = setRowLayout(item, style, wrap, pack);
      layout.fill = fill;
      layout.center = true;
      return layout;
   }

   public static FillLayout setFillLayout(Composite item, int style) {
      FillLayout layout = new FillLayout(style);
      item.setLayout(layout);
      return layout;
   }

   public static void setGridLayout(Composite item, int numColumns, int columnSpacing, boolean equal, int hpad) {
      final GridLayout layout = new GridLayout();
      layout.numColumns = numColumns;
      layout.horizontalSpacing = columnSpacing;
      layout.makeColumnsEqualWidth = equal;
      layout.marginLeft = hpad;
      layout.marginRight = hpad;
      item.setLayout(layout);
   }

   public static GridLayout setGridLayout(Composite item, int numColumns, int columnSpacing, int verticalSpacing, boolean equal, int hpad) {
      final GridLayout layout = new GridLayout();
      layout.numColumns = numColumns;
      layout.horizontalSpacing = columnSpacing;
      layout.makeColumnsEqualWidth = equal;
      layout.marginLeft = hpad;
      layout.marginRight = hpad;
      layout.verticalSpacing = verticalSpacing;
      layout.marginHeight = 0;
      item.setLayout(layout);
      return layout;
   }

   public static GridLayout setGridLayout(Composite item, int numColumns, int columnSpacing, int verticalSpacing, boolean equal, int hpad, int vpad) {
      final GridLayout layout = new GridLayout();
      layout.numColumns = numColumns;
      layout.horizontalSpacing = columnSpacing;
      layout.makeColumnsEqualWidth = equal;
      layout.marginLeft = hpad;
      layout.marginRight = hpad;
      layout.marginTop = vpad;
      layout.marginBottom = vpad;
      layout.verticalSpacing = verticalSpacing;
      layout.marginHeight = 0;
      item.setLayout(layout);
      return layout;
   }

   public static Combo createCombo(int style, Object[] list) {
      final Combo cbx = new Combo(targetContainer, style);
      for (Object element : list) {
         cbx.add(element.toString());
      }
      cbx.select(0);

      return cbx;
   }

   public static Combo createCombo(Composite item, int style, Object[] list) {
      final Combo cbx = new Combo(item, style);
      for (Object element : list) {
         cbx.add(element.toString());
      }
      cbx.select(0);

      return cbx;
   }

   public static Group createRadioGrp(final String txt, final int columns, final Object[] items) {
      final Group grp = new Group(targetContainer, SWT.NONE);
      grp.setText(txt);
      setGridLayout(grp, columns, 10);
      int count = 0;
      for (Object obj : items) {

         final Button rdo = new Button(grp, SWT.RADIO);
         rdo.setText(obj.toString());
         rdo.setData(obj);
         rdo.setSelection(count == 0 ? true : false);
         positionGridItem(rdo, false, SWT.BEGINNING, SWT.CENTER);
         rdo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               if (rdo.getSelection()) {
                  grp.setData(rdo.getData());
               }
            }

         });
         count++;
      }
      return grp;
   }

   public static Group createRadioGrp(String txt, int columns, Object[] items, Button[] btns) {
      final Group grp = new Group(targetContainer, SWT.NONE);
      grp.setText(txt);
      setGridLayout(grp, columns, 10);
      int count = 0;
      for (Object obj : items) {

         final Button rdo = new Button(grp, SWT.RADIO);
         btns[count] = rdo;
         rdo.setText(obj.toString());
         rdo.setData(obj);
         rdo.setSelection(count == 0 ? true : false);
         positionGridItem(rdo, false, SWT.BEGINNING, SWT.CENTER);
         rdo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               if (rdo.getSelection()) {
                  grp.setData(rdo.getData());
                  System.out.println(grp.getData().toString());
               }
            }

         });
         count++;
      }
      return grp;
   }

   public static Group createRadioGrp(Composite comp, String txt, int columns, Object[] items) {
      final Group grp = new Group(comp, SWT.NONE);
      grp.setText(txt);
      setGridLayout(grp, columns, 10);
      int count = 0;
      for (final Object item : items) {
         final Button rdo = new Button(grp, SWT.RADIO);
         rdo.setText(item.toString());
         rdo.setSelection(count == 0 ? true : false);
         positionGridItem(rdo, false, SWT.BEGINNING, SWT.CENTER);
         rdo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               if (rdo.getSelection()) {
                  grp.setData(item);
               }
            }
         });
         count++;
      }
      return grp;
   }

   public static Group createRadioGrp(Composite comp, String txt, int columns, Object[] items, Button[] btns) {
      final Group grp = new Group(comp, SWT.NONE);
      grp.setText(txt);
      setGridLayout(grp, columns, 5);
      int count = 0;
      for (final Object obj : items) {

         final Button rdo = new Button(grp, SWT.RADIO);
         btns[count] = rdo;
         rdo.setText(obj.toString());
         rdo.setSelection(count == 0 ? true : false);
         positionGridItem(rdo, false, SWT.CENTER, SWT.CENTER);
         rdo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               if (rdo.getSelection()) {
                  grp.setData(obj);
                  System.out.println(grp.getData().toString());
               }
            }
         });
         count++;
      }
      return grp;
   }

   /**
    * Used in form layouts. Attaches the specified control to the specified edge of the item's parent
    */
   public static void attachToParent(final Control control, final int edge, final int percent, final int offset) {
      attachToParent(control, edge, percent, offset, SWT.DEFAULT, SWT.DEFAULT);
   }

   public static void attachToParent(final Control control, final int edge, final int percent, final int offset, int width, int height) {
      final Object ld = control.getLayoutData();
      final FormData fd = ld != null ? (FormData) ld : new FormData();
      switch (edge) {
         case SWT.LEFT:
            fd.left = new FormAttachment(percent, offset);
            break;
         case SWT.RIGHT:
            fd.right = new FormAttachment(percent, offset);
            break;
         case SWT.TOP:
            fd.top = new FormAttachment(percent, offset);
            break;
         case SWT.BOTTOM:
            fd.bottom = new FormAttachment(percent, offset);
            break;
         default:
            throw new IllegalArgumentException("invalid edge specified");
      }
      fd.width = width;
      fd.height = height;
      control.setLayoutData(fd);
   }

   /**
    * Used in form layouts. Attaches the specified control to the specified edge of another control
    *
    * @param control the parimary control that will be attached to another control
    * @param itemToAttachTo the secondary control that the primary control will attach to
    * @param edge the primary control's edge to attach
    * @param itemToAttachToEdge the secondary control's edge
    * @param offset the number of pixels between the two controls' edges
    */
   public static void attachToControl(final Control control, final Control itemToAttachTo, final int edge, final int itemToAttachToEdge, final int offset) {
      attachToControl(control, itemToAttachTo, edge, itemToAttachToEdge, offset, SWT.DEFAULT, SWT.DEFAULT);
   }

   public static void attachToControl(final Control control, final Control itemToAttachTo, final int edge, final int itemToAttachToEdge, final int offset, final int width, final int height) {
      final Object ld = control.getLayoutData();
      final FormData fd = ld != null ? (FormData) ld : new FormData();
      switch (edge) {
         case SWT.LEFT:
            fd.left = new FormAttachment(itemToAttachTo, offset, itemToAttachToEdge);
            break;
         case SWT.RIGHT:
            fd.right = new FormAttachment(itemToAttachTo, offset, itemToAttachToEdge);
            break;
         case SWT.TOP:
            fd.top = new FormAttachment(itemToAttachTo, offset, itemToAttachToEdge);
            break;
         case SWT.BOTTOM:
            fd.bottom = new FormAttachment(itemToAttachTo, offset, itemToAttachToEdge);
            break;
         default:
            throw new IllegalArgumentException("invalid edge specified");
      }
      fd.width = width;
      fd.height = height;
      control.setLayoutData(fd);
   }

   public static void setFormLayout(final Composite composite) {
      composite.setLayout(new FormLayout());
   }

   /**
    * sets the the layout of the specified composite to a form layout.
    */
   public static void setFormLayout(final Composite composite, final int marginWidth, final int marginHeight) {
      final FormLayout fl = new FormLayout();
      fl.marginWidth = marginWidth;
      fl.marginHeight = marginHeight;
      composite.setLayout(fl);
   }

   /**
    * disposes all the supplied widgets if they are not null and not already disposed
    */
   @SuppressWarnings("unchecked")
   public static <T extends Widget> void disposeWidgets(final T... widgets) {
      if (widgets == null) {
         return;
      }
      for (T widget : widgets) {
         if (widget != null && !widget.isDisposed()) {
            widget.dispose();
         }
      }
   }

   /**
    * disposes all the controls including child controls if they are not null and not already disposed
    *
    * @param Control control to dispose
    */
   public static void disposeWidget(final Widget widget) {
      if (isAccessible(widget)) {
         if (widget instanceof Composite) {
            for (Control child : ((Composite) widget).getChildren()) {
               disposeWidget(child);
            }
         }
         widget.dispose();
      }
   }

   /**
    * Checks if widget is accessible
    *
    * @return <b>true</b> if widget is not null and is not disposed
    */
   public static boolean isAccessible(Widget widget) {
      return widget != null && !widget.isDisposed();
   }
}

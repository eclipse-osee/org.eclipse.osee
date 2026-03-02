/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.text.NumberFormat;
import java.text.ParseException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.OseeDictionary;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.osgi.service.component.annotations.Component;

/**
 * Generic label and text field object for use by single entry artifact attributes
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XTextWidget extends XWidget {

   public static final WidgetId ID = WidgetId.XTextWidget;

   protected StyledText sText; // Contains visible representation of text
   private Composite parent;
   protected String text = ""; // Where actual text with xml tags is stored
   private int maxTextChars = 0;
   private boolean spellCheck = true;
   private int width = 0;
   private int height = 0;
   private XTextSpellCheckListener spellPaintListener;
   private XTextUrlListener urlListener;
   private static XTextSpellModifyDictionary modDict;
   private Font font;
   Composite composite = null;
   private boolean dynamicallyCreated = false;

   public XTextWidget() {
      this(ID, "XText");
   }

   public XTextWidget(String displayLabel) {
      this(ID, displayLabel);
   }

   public XTextWidget(WidgetId widgetId, String displayLabel) {
      super(widgetId, displayLabel);
   }

   public void updateUiEnablementFromValue(String value) {
      if (value.length() > 1000) {
         spellCheck = false;

      }
   }

   public void setEnabled(boolean enabled) {
      sText.setEnabled(enabled);
   }

   public void setDynamicallyCreated(boolean value) {
      dynamicallyCreated = value;
   }

   public void setSize(int width, int height) {
      this.width = width;
      this.height = height;
      if (sText != null && !sText.isDisposed()) {
         sText.setSize(width, height);
      }
   }

   public void setHeight(int height) {
      this.height = height;
      if (sText != null && !sText.isDisposed()) {
         sText.setSize(sText.getSize().x, height);
      }
   }

   @Override
   public String toString() {
      return String.format("%s: *%s*", getLabel(), text);
   }

   @Override
   public Control getControl() {
      if (Widgets.isAccessible(labelWidget)) {
         return labelWidget;
      }
      return sText;
   }

   /**
    * Create Text Widgets. Widgets Created: Label: "text entry" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      createControls(parent, horizontalSpan, true);
   }

   protected void createControls(Composite parent, int horizontalSpan, boolean fillText) {
      setNotificationsAllowed(false);
      try {
         if (!isVerticalLabel() && horizontalSpan < 2) {
            horizontalSpan = 2;
         }

         this.parent = parent;
         if (isFillVertically()) {
            composite = new Composite(parent, SWT.NONE);
            GridLayout layout = ALayout.getZeroMarginLayout(1, false);
            layout.verticalSpacing = 4;
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
         } else {
            composite = new Composite(parent, SWT.NONE);
            GridLayout layout = ALayout.getZeroMarginLayout(horizontalSpan, false);
            layout.verticalSpacing = 4;
            composite.setLayout(layout);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = horizontalSpan;
            composite.setLayoutData(gd);
         }

         // Create Text Widgets
         if (isDisplayLabel() && !getLabel().equals("")) {
            labelWidget = new Label(composite, SWT.NONE);
            labelWidget.setText(getLabel() + ":");
            if (getToolTip() != null) {
               labelWidget.setToolTipText(getToolTip());
            }
         }

         sText = new StyledText(composite, getTextStyle());

         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         if (isVerticalLabel()) {
            gd.horizontalSpan = horizontalSpan;
         } else {
            gd.horizontalSpan = horizontalSpan - 1;
         }
         gd.grabExcessHorizontalSpace = true;
         gd.horizontalAlignment = GridData.FILL;
         if (isFillVertically()) {
            gd.grabExcessVerticalSpace = true;
            gd.verticalAlignment = GridData.FILL;
            if (height > 0) {
               gd.heightHint = height;
            }
            if (dynamicallyCreated) {
               if (height > 0) {
                  gd.minimumHeight = height;
               } else {
                  gd.minimumHeight = 60;
               }
            }
         }
         if (isFillHorizontally() && dynamicallyCreated) {
            gd.grabExcessHorizontalSpace = true;
            gd.minimumWidth = 60;
         }

         sText.setLayoutData(gd);
         sText.setMenu(getDefaultMenu());
         sText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
               if (Widgets.isAccessible(sText)) {
                  text = sText.getText();
                  notifyXModifiedListeners();
               }
            }
         });
         sText.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(org.eclipse.swt.events.FocusEvent e) {
               if (Widgets.isAccessible(sText)) {
                  text = sText.getText();
                  validate();
               }
            }
         });
         if (text != null) {
            sText.setText(text);
         }
         if (Strings.isValid(widData.getDefaultValueStr())) {
            text = widData.getDefaultValueStr();
            sText.setText(text);
         }
         if (spellCheck) {
            spellPaintListener = new XTextSpellCheckListener(sText, OseeDictionary.getInstance());
            sText.addModifyListener(spellPaintListener);
            if (modDict == null) {
               modDict = new SkynetSpellModifyDictionary();
               spellPaintListener.addXTextSpellModifyDictionary(modDict);
            }
         }
         urlListener = new XTextUrlListener(sText);
         sText.addModifyListener(urlListener);

         if (width != 0 && height != 0) {
            sText.setSize(width, height);
         }

         if (maxTextChars > 0) {
            sText.setTextLimit(maxTextChars);
         }
         if (fillText) {
            updateTextWidget();
         }
         validate();
         sText.setEditable(isEditable());
         if (font != null) {
            sText.setFont(font);
         }
         parent.layout();
      } finally {
         setNotificationsAllowed(true);
      }
   }

   protected int getTextStyle() {
      int styleBase = SWT.BORDER;
      if (isEditable()) {
         styleBase |= SWT.READ_ONLY;
      }
      return styleBase | (isFillVertically() ? SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL : SWT.SINGLE);
   }

   public void addXTextSpellModifyDictionary(XTextSpellModifyDictionary modDict) {
      XTextWidget.modDict = modDict;
      if (spellPaintListener != null) {
         spellPaintListener.addXTextSpellModifyDictionary(modDict);
      }
   }

   /**
    * @return text including xml tags replaced for references
    */
   public String getText() {
      String text = sText.getText();
      return text;
   }

   public void setText(String text) {
      this.text = text;
      if (sText != null) {
         sText.setText(text);
      }
   }

   public Menu getDefaultMenu() {
      Menu menu = new Menu(sText.getShell());
      MenuItem cut = new MenuItem(menu, SWT.NONE);
      cut.setText("Cut");
      cut.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            sText.cut();
            sText.redraw();
         }
      });
      MenuItem copy = new MenuItem(menu, SWT.NONE);
      copy.setText("Copy");
      copy.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            sText.copy();
         }
      });
      MenuItem paste = new MenuItem(menu, SWT.NONE);
      paste.setText("Paste");
      paste.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            sText.paste();
            sText.redraw();
         }
      });
      return menu;
   }

   @Override
   public void dispose() {
      if (labelWidget != null) {
         labelWidget.dispose();
      }
      if (sText != null) {
         if (spellPaintListener != null && !sText.isDisposed()) {
            sText.removeModifyListener(spellPaintListener);
         }
         if (urlListener != null && !sText.isDisposed()) {
            sText.removeModifyListener(urlListener);
         }
         sText.dispose();
      }
      if (Widgets.isAccessible(composite)) {
         composite.dispose();
      }
      if (parent != null && !parent.isDisposed()) {
         parent.layout();
      }
   }

   @Override
   public void setFocus() {
      if (sText != null) {
         sText.setFocus();
      }
   }

   public void setSpellChecking(boolean spellCheck) {
      if (sText != null) {
         if (spellCheck) {
            sText.addModifyListener(spellPaintListener);
         } else if (spellPaintListener != null) {
            sText.removeModifyListener(spellPaintListener);
         }
      }
      this.spellCheck = spellCheck;
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (Widgets.isAccessible(sText)) {
         sText.setEditable(editable);
      }
   }

   /**
    * Set max character limit on text field
    *
    * @param limit - if 0, then limit is 999, else sets to limit
    */
   public void setMaxTextLimit(int limit) {
      this.maxTextChars = limit;
      if (sText != null) {
         if (limit == 0) {
            sText.setTextLimit(999);
         } else {
            sText.setTextLimit(limit);
         }
      }
   }

   public void forceFocus() {
      if (sText != null) {
         sText.forceFocus();
      }
   }

   @Override
   public void setFillVertically(boolean fillVertically) {
      super.setFillVertically(fillVertically);
   }

   public boolean isInteger() {
      try {
         Integer.parseInt(text);
      } catch (NumberFormatException e) {
         return false;
      }
      return true;
   }

   public boolean isLong() {
      try {
         Long.parseLong(text);
      } catch (NumberFormatException e) {
         return false;
      }
      return true;
   }

   public boolean isFloat() {
      try {
         NumberFormat.getInstance().parse(text);
      } catch (ParseException e) {
         return false;
      }
      return true;
   }

   public int getInteger() {
      Integer num;
      try {
         num = Integer.valueOf(text);
      } catch (NumberFormatException e) {
         return 0;
      }
      return num.intValue();
   }

   public long getLong() {
      Long num;
      try {
         num = Long.valueOf(text);
      } catch (NumberFormatException e) {
         return 0;
      }
      return num;
   }

   public double getFloat() {
      Number num;
      try {
         num = NumberFormat.getInstance().parse(text);
      } catch (ParseException e) {
         return 0.0;
      }
      return num.doubleValue();
   }

   @Override
   public void setRequiredEntry(boolean requiredEntry) {
      super.setRequiredEntry(requiredEntry);
      validate();
   }

   public void addModifyListener(ModifyListener modifyListener) {
      if (sText != null) {
         sText.addModifyListener(modifyListener);
      }
   }

   public void addFocusListener(FocusListener focusListener) {
      if (sText != null) {
         sText.addFocusListener(focusListener);
      }
   }

   public String get() {
      return text;
   }

   public int getInt() {
      Integer percent;
      try {
         percent = Integer.valueOf(text);
      } catch (NumberFormatException e) {
         percent = 0;
      }
      return percent.intValue();
   }

   protected void updateTextWidget() {
      if (Widgets.isAccessible(sText)) {
         if (!text.equals(sText.getText())) {
            // Disable Listeners so not to fill Undo List
            sText.setText(text);
            // Re-enable Listeners
            validate();
         }
      }
   }

   public void set(String text) {
      if (text == null) {
         this.text = "";
      } else {
         this.text = text;
      }
      updateTextWidget();
   }

   public void set(XTextWidget text) {
      set(text.get());
   }

   public void append(String text) {
      this.text += text;
      updateTextWidget();
   }

   @Override
   public void refresh() {
      updateTextWidget();
   }

   @Override
   public String getReportData() {
      String s = "";
      String textStr = new String(text);
      if (isFillVertically()) {
         s = s + "\n";
         textStr = textStr.replaceAll("\n", "\n" + "      ");
         textStr = "      " + textStr;
      }
      s = s + textStr;
      s = s.replaceAll("\n$", "");
      return s;
   }

   public String toHTML(String labelFont, boolean newLineText) {
      String result = AHTML.getLabelStr(labelFont, getLabel() + ": ");
      if (newLineText) {
         result = "<dl><dt>" + result + "<dd>";
      }
      result += AHTML.textToHtml(text);
      if (newLineText) {
         result += "</dl>";
      }
      return result;
   }

   @Override
   public String toHTML(String labelFont) {
      return toHTML(labelFont, false);
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (!status.isOK()) {
         return status;
      }
      if (sText.isEnabled() && isRequiredEntry() && isEmpty()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format("Must enter \"%s\"", getLabel()));
      }
      return Status.OK_STATUS;
   }

   @Override
   public boolean isEmpty() {
      return !Strings.isValid(get());
   }

   @Override
   public Object getData() {
      return sText.getText();
   }

   public StyledText getStyledText() {
      return sText;
   }

   public Font getFont() {
      return font;
   }

   public void setFont(Font font) {
      this.font = font;
      if (sText != null) {
         sText.setFont(font);
      }
   }

   public void selectAll() {
      sText.selectAll();
   }

   public Color getBackground() {
      return sText.getBackground();
   }

   public void setBackground(Color color) {
      sText.setBackground(color);
   }

}

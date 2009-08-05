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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OseeDictionary;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Generic label and text field object for use by single entry artifact attributes
 * 
 * @author Donald G. Dunne
 */
public class XText extends XWidget {

   protected StyledText sText; // Contains visable representation of text
   private Composite parent;
   protected String text = ""; // Where actual text with xml tags is stored
   private int maxTextChars = 0;

   private boolean dragableArtifact = false;
   private boolean spellCheck = true;
   private final boolean debug = false;
   private int width = 0;
   private int height = 0;
   private XTextSpellCheckPaintListener spellPaintListener;
   private XTextSpellModifyDictionary modDict;
   private Font font;

   public XText() {
      super("AText", "text");
   }

   public XText(String displayLabel) {
      this(displayLabel, "text");
   }

   public XText(String displayLabel, String xmlRoot) {
      this(displayLabel, xmlRoot, "");
   }

   public XText(String displayLabel, String xmlRoot, String xmlSubRoot) {
      super(displayLabel, xmlRoot, xmlSubRoot);
   }

   public void setEnabled(boolean enabled) {
      sText.setEnabled(enabled);
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
      return getLabel() + ": *" + text + "*";
   }

   @Override
   public Control getControl() {
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
         if (!verticalLabel && horizontalSpan < 2) {
            horizontalSpan = 2;
         }

         this.parent = parent;
         Composite composite = null;

         ModifyListener textListener = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
               if (sText != null) {
                  debug("modifyText");
                  text = sText.getText();
                  validate();
                  notifyXModifiedListeners();
               }
            }
         };

         if (fillVertically) {
            composite = new Composite(parent, SWT.NONE);
            GridLayout layout = ALayout.getZeroMarginLayout(1, false);
            layout.verticalSpacing = 4;
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
         } else {
            composite = new Composite(parent, SWT.NONE);
            GridLayout layout = ALayout.getZeroMarginLayout(2, false);
            layout.verticalSpacing = 4;
            composite.setLayout(layout);
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = horizontalSpan;
            composite.setLayoutData(gd);
         }
         // composite = parent;

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
         if (verticalLabel) {
            gd.horizontalSpan = horizontalSpan;
         } else {
            gd.horizontalSpan = horizontalSpan - 1;
         }
         gd.grabExcessHorizontalSpace = true;
         gd.horizontalAlignment = GridData.FILL;
         if (fillVertically) {
            gd.grabExcessVerticalSpace = true;
            gd.verticalAlignment = GridData.FILL;
         }
         if (fillVertically) {
            if (height > 0) {
               gd.heightHint = height;
            }
         }
         //      gd.widthHint = 200;
         sText.setLayoutData(gd);
         sText.setMenu(getDefaultMenu());
         sText.addModifyListener(textListener);
         if (text != null) {
            sText.setText(text);
         }
         if (spellCheck) {
            spellPaintListener = new XTextSpellCheckPaintListener(this, OseeDictionary.getInstance());
            sText.addPaintListener(spellPaintListener);
            if (modDict != null) {
               spellPaintListener.addXTextSpellModifyDictionary(modDict);
            }
         }
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
      return styleBase | (fillVertically ? SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL : SWT.SINGLE);
   }

   public void addXTextSpellModifyDictionary(XTextSpellModifyDictionary modDict) {
      this.modDict = modDict;
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

   /**
    * @param text
    */
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
            sText.removePaintListener(spellPaintListener);
         }
         sText.dispose();
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
            sText.addPaintListener(spellPaintListener);
         } else if (spellPaintListener != null) {
            sText.removePaintListener(spellPaintListener);
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
         new Integer(text);
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
         num = new Integer(text);
      } catch (NumberFormatException e) {
         return 0;
      }
      return num.intValue();
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

   public String get() {
      if (debug) {
         System.err.println("text set *" + text + "*");
      }
      return text;
   }

   @Override
   public String getXmlData() {
      if (sText == null || sText.isDisposed()) {
         return AXml.textToXml(text);
      } else {
         try {
            return AXml.textToXml(sText.getText());
         } catch (SWTException e) {
            return AXml.textToXml(text);
         }
      }
   }

   @Override
   protected String toXml() {
      if (getXmlSubRoot().equals("")) {
         return toXml(getXmlRoot());
      } else {
         return toXml(getXmlRoot(), getXmlSubRoot());
      }
   }

   @Override
   public String toXml(String xmlRoot) {
      String s = "<" + xmlRoot + ">" + getXmlData() + "</" + xmlRoot + ">\n";
      return s;
   }

   @Override
   public String toXml(String xmlRoot, String xmlSubRoot) {
      String s =
            "<" + xmlRoot + ">" + "<" + xmlSubRoot + ">" + getXmlData() + "</" + xmlSubRoot + ">" + "</" + xmlRoot + ">\n";
      return s;
   }

   @Override
   public void setXmlData(String str) {
      set(str);
      if (debug) {
         System.err.println("setFromXml *" + str + "*");
      }
   }

   @Override
   public void setFromXml(String xml) {
      Matcher m;
      m =
            Pattern.compile("<" + getXmlRoot() + ">(.*?)</" + getXmlRoot() + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(
                  xml);

      if (m.find()) {
         String xmlStr = m.group(1);
         if (debug) {
            System.err.println("xmlStr *" + xmlStr + "*");
         }
         String str = AXml.xmlToText(xmlStr);
         if (debug) {
            System.err.println("str *" + str + "*");
         }
         setXmlData(str);
      }
   }

   public int getInt() {
      Integer percent = new Integer(0);
      try {
         percent = new Integer(text);
      } catch (NumberFormatException e) {
      }
      return percent.intValue();
   }

   protected void updateTextWidget() {
      if (Widgets.isAccessible(sText)) {
         if (!text.equals(sText.getText())) {
            // Disable Listeners so not to fill Undo List
            sText.setText(text);
            // Reenable Listeners
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
      if (debug) {
         System.err.println("set *" + text + "*");
      }
      updateTextWidget();
   }

   public void set(XText text) {
      set(text.get());
   }

   public void append(String text) {
      this.text = this.text + text;
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
      if (fillVertically) {
         s = s + "\n";
         textStr = textStr.replaceAll("\n", "\n" + "      ");
         textStr = "      " + textStr;
      }
      s = s + textStr;
      s = s.replaceAll("\n$", "");
      return s;
   }

   public String toHTML(String labelFont, boolean newLineText) {
      String s = AHTML.getLabelStr(labelFont, getLabel() + ": ");
      if (newLineText) {
         s = "<dl><dt>" + s + "<dd>";
      }
      s += text;
      if (newLineText) {
         s += "</dl>";
      }
      return s;
   }

   @Override
   public String toHTML(String labelFont) {
      return toHTML(labelFont, false);
   }

   /**
    * @return Returns the dragableArtifact.
    */
   public boolean isDragableArtifact() {
      return dragableArtifact;
   }

   /**
    * @param dragableArtifact The dragableArtifact to set.
    */
   public void setDragableArtifact(boolean dragableArtifact) {
      this.dragableArtifact = dragableArtifact;
   }

   public void debug(String str) {
      if (debug) {
         System.err.println("AText :" + str);
      }
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() && !Strings.isValid(get())) {
         return new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, String.format("Must enter \"%s\"", getLabel()));
      }
      return Status.OK_STATUS;
   }

   @Override
   public Object getData() {
      return sText.getText();
   }

   /**
    * @return the sText
    */
   public StyledText getStyledText() {
      return sText;
   }

   /**
    * @return the font
    */
   public Font getFont() {
      return font;
   }

   /**
    * @param font the font to set
    */
   public void setFont(Font font) {
      this.font = font;
      if (sText != null) {
         sText.setFont(font);
      }
   }

}
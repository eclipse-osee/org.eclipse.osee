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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OseeDictionary;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
   private boolean debug = false;
   private int width = 0;
   private int height = 0;
   private XTextSpellCheckPaintListener spellPaintListener;
   private XTextSpellModifyDictionary modDict;

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
      if (sText != null && !sText.isDisposed()) sText.setSize(width, height);
   }

   public void setHeight(int height) {
      this.height = height;
      if (sText != null && !sText.isDisposed()) sText.setSize(sText.getSize().x, height);
   }

   public String toString() {
      return label + ": *" + text + "*";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return sText;
   }

   /**
    * Create Text Widgets. Widgets Created: Label: "text entry" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2
    */
   public void createWidgets(Composite parent, int horizontalSpan) {
      createWidgets(parent, horizontalSpan, true);
   }

   public void createWidgets(Composite parent, int horizontalSpan, boolean fillText) {

      if (!verticalLabel && (horizontalSpan < 2)) {
         horizontalSpan = 2;
      }

      this.parent = parent;
      Composite composite = null;

      ModifyListener textListener = new ModifyListener() {

         public void modifyText(ModifyEvent e) {
            if (sText != null) {
               debug("modifyText");
               text = sText.getText();
               setLabelError();
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
      if (displayLabel && !label.equals("")) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(label + ":");
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }
      if (fillVertically) {
         sText = new StyledText(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL);
      } else {
         sText = new StyledText(composite, SWT.BORDER | SWT.SINGLE);
      }
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      if (verticalLabel)
         gd.horizontalSpan = horizontalSpan;
      else
         gd.horizontalSpan = horizontalSpan - 1;
      gd.grabExcessHorizontalSpace = true;
      gd.horizontalAlignment = GridData.FILL;
      if (fillVertically) {
         gd.grabExcessVerticalSpace = true;
         gd.verticalAlignment = GridData.FILL;
      }
      if (fillVertically) {
         if (height > 0) gd.heightHint = height;
      }

      sText.setLayoutData(gd);
      sText.setMenu(getDefaultMenu());
      sText.addModifyListener(textListener);
      spellPaintListener = new XTextSpellCheckPaintListener(this, OseeDictionary.getInstance());
      if (spellCheck) {
         sText.addPaintListener(spellPaintListener);
         if (modDict != null) spellPaintListener.addXTextSpellModifyDictionary(modDict);
      }
      if (width != 0 && height != 0) sText.setSize(width, height);

      if (maxTextChars > 0) {
         sText.setTextLimit(maxTextChars);
      }
      if (fillText) updateTextWidget();
      setLabelError();
      sText.setEditable(editable);
      parent.layout();
   }

   public void addXTextSpellModifyDictionary(XTextSpellModifyDictionary modDict) {
      this.modDict = modDict;
      if (spellPaintListener != null) spellPaintListener.addXTextSpellModifyDictionary(modDict);
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
      sText.setText(text);
   }

   public Menu getDefaultMenu() {
      Menu menu = new Menu(sText.getShell());
      MenuItem cut = new MenuItem(menu, SWT.NONE);
      cut.setText("Cut");
      cut.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            sText.cut();
            sText.redraw();
         }
      });
      MenuItem copy = new MenuItem(menu, SWT.NONE);
      copy.setText("Copy");
      copy.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            sText.copy();
         }
      });
      MenuItem paste = new MenuItem(menu, SWT.NONE);
      paste.setText("Paste");
      paste.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            sText.paste();
            sText.redraw();
         }
      });
      return menu;
   }

   @Override
   public void dispose() {
      labelWidget.dispose();
      if (sText != null) {
         if (spellPaintListener != null && !sText.isDisposed()) sText.removePaintListener(spellPaintListener);
         sText.dispose();
         sText = null;
      }
      if (parent != null && !parent.isDisposed()) parent.layout();
   }

   public void setFocus() {
      if (sText != null) sText.setFocus();
   }

   public void setSpellChecking(boolean spellCheck) {
      if (sText != null) {
         if (spellCheck)
            sText.addPaintListener(spellPaintListener);
         else if (spellPaintListener != null) sText.removePaintListener(spellPaintListener);
      }
      this.spellCheck = spellCheck;
   }

   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (sText != null && !sText.isDisposed()) {
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
         new Float(text);
      } catch (NumberFormatException e) {
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
      Double num;
      try {
         num = new Double(text);
      } catch (NumberFormatException e) {
         return 0;
      }
      return num.doubleValue();
   }

   public void setRequiredEntry(boolean requiredEntry) {
      super.setRequiredEntry(requiredEntry);
      setLabelError();
   }

   public boolean requiredEntry() {
      return requiredEntry;
   }

   public void addModifyListener(ModifyListener modifyListener) {
      if (sText != null) sText.addModifyListener(modifyListener);
   }

   public String get() {
      if (debug) System.err.println("text set *" + text + "*");
      return text;
   }

   public String getXmlData() {
      if (sText == null || sText.isDisposed())
         return AXml.textToXml(text);
      else
         try {
            return AXml.textToXml(sText.getText());
         } catch (SWTException e) {
            return AXml.textToXml(text);
         }
   }

   public String toXml() {
      if (xmlSubRoot.equals("")) {
         return toXml(xmlRoot);
      } else {
         return toXml(xmlRoot, xmlSubRoot);
      }
   }

   public String toXml(String xmlRoot) {
      String s = "<" + xmlRoot + ">" + getXmlData() + "</" + xmlRoot + ">\n";
      return s;
   }

   public String toXml(String xmlRoot, String xmlSubRoot) {
      String s =
            "<" + xmlRoot + ">" + "<" + xmlSubRoot + ">" + getXmlData() + "</" + xmlSubRoot + ">" + "</" + xmlRoot + ">\n";
      return s;
   }

   public void setXmlData(String str) {
      set(str);
      if (debug) System.err.println("setFromXml *" + str + "*");
   }

   public void setFromXml(String xml) {
      Matcher m;
      m = Pattern.compile("<" + xmlRoot + ">(.*?)</" + xmlRoot + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(xml);

      if (m.find()) {
         String xmlStr = m.group(1);
         if (debug) System.err.println("xmlStr *" + xmlStr + "*");
         String str = AXml.xmlToText(xmlStr);
         if (debug) System.err.println("str *" + str + "*");
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
      if (sText != null) {
         if (text.equals(sText.getText())) return;
         // Disable Listeners so not to fill Undo List
         sText.setText(text);
         // Reenable Listeners
         setLabelError();
      }
   }

   public void set(String text) {
      if (text == null)
         this.text = "";
      else
         this.text = text;
      if (debug) System.err.println("set *" + text + "*");
      updateTextWidget();
   }

   public void set(XText text) {
      set(text.get());
   }

   public void append(String text) {
      this.text = this.text + text;
      updateTextWidget();
   }

   public void refresh() {
      updateTextWidget();
   }

   public boolean isValid() {
      if (requiredEntry && (text.compareTo("") == 0)) {
         return false;
      }
      return true;
   }

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
      String s = AHTML.getLabelStr(labelFont, label + ": ");
      if (newLineText) s = "<dl><dt>" + s + "<dd>";
      s += text;
      if (newLineText) s += "</dl>";
      return s;
   }

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
      if (debug) System.err.println("AText :" + str);
   }

   public Result isValidResult() {
      if (isRequiredEntry() && get().equals("")) {
         return new Result(String.format("Must enter \"%s\"", label));
      }
      return Result.TrueResult;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getData()
    */
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

}
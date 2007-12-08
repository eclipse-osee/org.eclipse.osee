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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
public class FormattedText extends Composite {

   private List<StyleRange> styleArray;
   private StyledText textArea;
   private int height;
   private int width;
   private boolean editable;

   public FormattedText(Composite parent, int style) {
      this(parent, style, 300, 300, false);
   }

   public FormattedText(Composite parent, int style, boolean editable) {
      this(parent, style, 300, 300, editable);
   }

   public FormattedText(Composite parent, int style, int height, int width) {
      this(parent, style, height, width, false);
   }

   public FormattedText(Composite parent, int style, int height, int width, boolean editable) {
      super(parent, style);
      this.editable = editable;
      this.height = height;
      this.width = width;
      this.styleArray = new ArrayList<StyleRange>();
      createTextArea();
   }

   private void createTextArea() {
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      textArea = new StyledText(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = height;
      gd.widthHint = width;
      textArea.setLayoutData(gd);
      textArea.setEditable(editable);
      textArea.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
      textArea.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
      textArea.setText("");
   }

   public String[] getCmdList() {
      return textArea.getText().split("\n");
   }

   public void setTextAreaBackground(final int swtColor) {
      textArea.setBackground(Display.getDefault().getSystemColor(swtColor));
   }

   public StyledText getStyledText() {
      return textArea;
   }

   public void clearTextArea() {
      textArea.setText("");
      styleArray.clear();
   }

   public void addText(String textToAdd) {
      addText(textToAdd, SWT.NORMAL, SWT.COLOR_BLACK);
   }

   public void addText(String textToAdd, int swtFontStyle, int swtColor) {
      addText(textToAdd, swtFontStyle, swtColor, false);
   }

   public void addText(String textToAdd, int swtFontStyle, int swtColor, boolean underline) {
      if (textToAdd != null) {
         String temp = textArea.getText();
         int startIndex = temp.length();
         temp += textToAdd;
         textArea.setText(temp);
         StyleRange tempStyle = new StyleRange();
         styleArray.add(tempStyle);
         tempStyle.fontStyle = swtFontStyle;
         tempStyle.start = startIndex;
         tempStyle.length = textToAdd.length();
         tempStyle.underline = underline;
         tempStyle.foreground = Display.getDefault().getSystemColor(swtColor);
         textArea.setStyleRanges(styleArray.toArray(new StyleRange[styleArray.size()]));
         textArea.redraw();
      }
   }

   public void dispose() {
      super.dispose();
      textArea.dispose();
   }
}

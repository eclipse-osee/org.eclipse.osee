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
package org.eclipse.osee.ote.ui.test.manager.preferences.environment;

import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

/**
 * @author Roberto E. Escobar
 */
public class EnvironmentPageDataViewer {

   private Group environmentVariable;
   private EnvironmentPreferenceNode nodeToDisplay;
   private ArrayList<StyleRange> styleArray;
   private StyledText textArea;

   public EnvironmentPageDataViewer(Composite parent) {
      createArea(parent);
      styleArray = new ArrayList<StyleRange>();
   }

   public void clearTextArea() {
      textArea.setText("");
      styleArray.clear();
      textArea.redraw();
   }

   public void setNodeToDisplay(EnvironmentPreferenceNode nodeToDisplay) {
      this.nodeToDisplay = nodeToDisplay;
      update();
   }

   public void setTitleName(String name) {
      environmentVariable.setText("Preview Environment Variable: " + name);
      environmentVariable.redraw();
   }

   public void update() {
      clearTextArea();
      if (nodeToDisplay != null) {
         setTitleName(nodeToDisplay.getEnvName());
         addEntry(nodeToDisplay.getEnvName(), nodeToDisplay.getValue());
      }
      else {
         setTitleName("NONE SELECTED");
      }
   }

   private void addEntry(String name, String value) {
      addEntryName(name);
      addEntryValue(value);
   }

   private void addEntryName(String name) {
      if (name != null) {
         String temp = textArea.getText();
         temp += "\n\t";
         int startIndex = temp.length();
         temp += name;
         textArea.setText(temp);
         StyleRange tempStyle = new StyleRange();
         styleArray.add(tempStyle);
         tempStyle.fontStyle = SWT.BOLD;
         tempStyle.start = startIndex;
         tempStyle.length = name.length();
         tempStyle.underline = true;
         tempStyle.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);
         textArea.setStyleRanges(styleArray.toArray(new StyleRange[styleArray.size()]));
         textArea.redraw();
      }
   }

   private void addEntryValue(String value) {
      if (value != null) {
         String temp = textArea.getText();
         temp += " = ";
         int startIndex = temp.length();
         String toAdd = value.replaceAll(":", ":\n\t\t");
         temp += toAdd;
         textArea.setText(temp + "\n");
         StyleRange tempStyle = new StyleRange();
         styleArray.add(tempStyle);
         tempStyle.fontStyle = SWT.ITALIC;
         tempStyle.start = startIndex;
         tempStyle.length = toAdd.length();
         tempStyle.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
         textArea.setStyleRanges(styleArray.toArray(new StyleRange[styleArray.size()]));
         textArea.redraw();
      }
   }

   private Control createArea(Composite parent) {
      GridData d = new GridData(GridData.FILL_BOTH);

      environmentVariable = new Group(parent, SWT.NONE);
      environmentVariable.setText("Preview Environment Variable:");
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      environmentVariable.setLayout(gridLayout);
      environmentVariable.setLayoutData(d);

      Composite topLevelComposite = new Composite(environmentVariable, SWT.NONE);
      gridLayout = new GridLayout();
      gridLayout.numColumns = 1;
      topLevelComposite.setLayout(gridLayout);
      topLevelComposite.setLayoutData(d);
      topLevelComposite.setToolTipText("Select a Value From the Tree to Display");

      textArea = new StyledText(topLevelComposite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
      textArea.setEditable(false);
      GridLayout gL = new GridLayout();
      gL.numColumns = 1;
      textArea.setLayout(gL);
      textArea.setLayoutData(d);
      textArea.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
      textArea.setToolTipText("Select a Value From the Tree to Display");

      return parent;
   }
}

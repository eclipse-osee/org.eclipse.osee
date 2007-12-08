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
package org.eclipse.osee.framework.ui.swt.styledText;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class ToolTip {

   Shell tipShell = null;
   Label tipLabelText = null;
   Shell parent = null;
   String text = "ToolTip Text Not Set";

   public ToolTip() {
   }

   public boolean isVisable() {
      return (tipShell != null) && (tipShell.isVisible());
   }

   public void popUp(Display display) {
      popUp(display.getCursorLocation());
   }

   public void popUp(Point location) {
      //        System.out.println("popupToolTip");
      if (tipShell == null) {
         create(parent);
         open(getText(), location);
         close();
      }

      if (isVisable()) {
         return;
      }

      open(getText(), location);
   }

   public void popDown() {
      close();
   }

   private void create(Composite parent) {

      tipShell = new Shell(parent.getShell(), SWT.NONE);
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      gridLayout.marginWidth = 2;
      gridLayout.marginHeight = 2;

      tipShell.setLayout(gridLayout);
      tipShell.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

      tipLabelText = new Label(tipShell, SWT.NONE);

      tipLabelText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
      tipLabelText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      tipLabelText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));

   }

   private void open(String text, Point loc) {

      tipLabelText.setText(text);

      // Size it
      Rectangle displayBounds = tipShell.getDisplay().getBounds();
      Rectangle shellBounds = tipShell.getBounds();
      shellBounds.x = Math.max(Math.min(loc.x + 16, displayBounds.width - shellBounds.width), 0);
      shellBounds.y = Math.max(Math.min(loc.y, displayBounds.height - shellBounds.height), 0);
      tipShell.setBounds(shellBounds);

      tipShell.pack();
      tipShell.open();
      tipShell.setVisible(true);

   }

   private void close() {
      if (tipShell != null) tipShell.setVisible(false);
   }

   /**
    * @return Returns the Text.
    */
   public String getText() {
      return text;
   }

   /**
    * @param text - The Text to set.
    */
   public void setText(String text) {
      this.text = text;
   }

   /**
    * @return Returns the parent.
    */
   public Shell getParent() {
      return parent;
   }

   /**
    * @param parent The parent to set.
    */
   public void setParent(Shell parent) {
      this.parent = parent;
   }
}
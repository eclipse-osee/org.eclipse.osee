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

import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.CursorManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class XButton extends XButtonCommon {

   protected Label button;
   protected Composite parent;
   protected Composite comp;
   private boolean labelAfter = true;
   protected int numColumns = 2;
   private static Cursor cursorHand;

   public XButton(String displayLabel) {
      super(displayLabel);
   }

   public XButton(String displayLabel, Image image) {
      super(displayLabel, image);
   }

   @Override
   public Control getControl() {
      return comp;
   }

   /**
    * Create Check Widgets. Widgets Created: Label: "text entry" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      if (horizontalSpan < 2) {
         horizontalSpan = 2;
      }
      this.parent = parent;

      comp = new Composite(parent, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout(numColumns, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      if (toolkit != null) {
         toolkit.adapt(comp);
      }

      // Create Text Widgets
      if (!labelAfter) {
         labelWidget = new Label(comp, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
      }

      if (toolkit != null) {
         button = toolkit.createLabel(comp, "");
      } else {
         button = new Label(comp, SWT.PUSH);
      }
      GridData gd2 = new GridData(GridData.BEGINNING);
      button.setLayoutData(gd2);
      button.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event event) {
            validate();
            if (event.button == 1) {
               notifyXModifiedListeners();
            } else if (event.button == 3) {
               notifyRightClickListeners();
            }
         }
      });
      GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      gd.horizontalSpan = horizontalSpan - 1;

      if (labelAfter) {
         labelWidget = new Label(comp, SWT.NONE);
         labelWidget.setText(getLabel());
      }
      // Nice to allow user to select label or icon to kick-off action
      if (labelWidget != null) {
         labelWidget.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               if (event.button == 1) {
                  notifyXModifiedListeners();
               } else if (event.button == 3) {
                  notifyRightClickListeners();
               }
            }
         });
         labelWidget.setCursor(getCursorHand());
      }
      if (getToolTip() != null) {
         button.setToolTipText(getToolTip());
         if (labelWidget != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      button.setLayoutData(gd);
      validate();
      button.setEnabled(isEditable());
      if (image != null) {
         button.setImage(image);
      }
      button.setCursor(getCursorHand());

   }

   private Cursor getCursorHand() {
      if (cursorHand == null) {
         cursorHand = CursorManager.getCursor(SWT.CURSOR_HAND);
      }
      return cursorHand;
   }

   @Override
   public void dispose() {
      labelWidget.dispose();
      button.dispose();
      comp.dispose();
      if (parent != null && !parent.isDisposed()) {
         parent.layout();
      }
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelStr(labelFont, getLabel() + ": ") + selected;
   }

   /**
    * If set, label will be displayed after the button NOTE: Has to be set before call to createWidgets
    */
   public void setLabelAfter(boolean labelAfter) {
      this.labelAfter = labelAfter;
   }

   public Label getbutton() {
      return button;
   }

   @Override
   public Object getData() {
      return Boolean.valueOf(isSelected());
   }

}

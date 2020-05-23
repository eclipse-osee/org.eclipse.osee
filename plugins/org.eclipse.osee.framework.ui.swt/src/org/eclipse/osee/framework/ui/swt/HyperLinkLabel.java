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

package org.eclipse.osee.framework.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Creates a hyperlink label that when enabled, shows as blue and changes cursor when hover over and when disabled,
 * shows in black with no cursor change. To add listener, use: link.addListener(SWT.MouseUp, new Listener() { public
 * void handleEvent(org.eclipse.swt.widgets.Event event) { System.out.println("Link Selected"); } });
 * 
 * @author Donald G. Dunne
 */

public class HyperLinkLabel {

   private boolean hyperEnabled = true;
   private Label label = null;
   /**
    * Amount of the margin width around the hyperlink (default is 1).
    */
   protected int marginWidth = 1;

   /**
    * Amount of the margin height around the hyperlink (default is 1).
    */
   protected int marginHeight = 1;

   public HyperLinkLabel(Composite parent, int style) {
      this(null, parent, style, null);
   }

   public HyperLinkLabel(FormToolkit toolkit, Composite parent, int style) {
      this(toolkit, parent, style, null);
   }

   public HyperLinkLabel(FormToolkit toolkit, Composite parent, int style, String text) {
      label = new Label(parent, style);
      if (text != null) {
         label.setText(text);
      }
      if (toolkit != null) {
         toolkit.adapt(label, true, true);
      }
      refresh();
   }

   public static void adapt(final Label label) {
      label.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
      label.addMouseTrackListener(new MouseTrackAdapter() {

         @Override
         public void mouseEnter(MouseEvent e) {
            label.setCursor(CursorManager.getCursor(SWT.CURSOR_HAND));
         }

         @Override
         public void mouseExit(MouseEvent e) {
            label.setCursor(null);
         };

      });
   }

   MouseTrackListener listener = new MouseTrackAdapter() {

      @Override
      public void mouseEnter(MouseEvent e) {
         label.setCursor(CursorManager.getCursor(SWT.CURSOR_HAND));
      }

      @Override
      public void mouseExit(MouseEvent e) {
         label.setCursor(null);
      };

   };

   public void refresh() {
      if (hyperEnabled) {
         label.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
         label.removeMouseTrackListener(listener);
         label.addMouseTrackListener(listener);
      } else {
         label.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
         label.removeMouseTrackListener(listener);
      }
   }

   public boolean isHyperEnabled() {
      return hyperEnabled;
   }

   public void setHyperEnabled(boolean hyperEnabled) {
      this.hyperEnabled = hyperEnabled;
      refresh();
   }

   public void setText(String text) {
      label.setText(text);
   }

   public void addListener(int mouseup, Listener listener) {
      label.addListener(mouseup, listener);
   }

}

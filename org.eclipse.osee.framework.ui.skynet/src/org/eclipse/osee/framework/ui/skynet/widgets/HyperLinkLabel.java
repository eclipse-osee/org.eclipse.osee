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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Creates a hyperlink label that when enabled, shows as blue and changes cursor when hover over and when disabled,
 * shows in black with no cursor change. To add listener, use: link.addListener(SWT.MouseUp, new Listener() { public
 * void handleEvent(org.eclipse.swt.widgets.Event event) { System.out.println("Link Selected"); } });
 * 
 * @author Donald G. Dunne
 */

public class HyperLinkLabel extends Label {

   private boolean hyperEnabled = true;
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
      super(parent, style);
      if (text != null) setText(text);
      if (toolkit != null) toolkit.adapt(this, true, true);
      refresh();
   }

   public static void adapt(final Label label) {
      label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
      label.addMouseTrackListener(new MouseTrackListener() {

         public void mouseEnter(MouseEvent e) {
            label.setCursor(new Cursor(null, SWT.CURSOR_HAND));
         }

         public void mouseExit(MouseEvent e) {
            label.setCursor(null);
         };

         public void mouseHover(MouseEvent e) {
         }
      });
   }

   MouseTrackListener listener = new MouseTrackListener() {

      public void mouseEnter(MouseEvent e) {
         setCursor(new Cursor(null, SWT.CURSOR_HAND));
      }

      public void mouseExit(MouseEvent e) {
         setCursor(null);
      };

      public void mouseHover(MouseEvent e) {
      }
   };

   public void refresh() {
      if (hyperEnabled) {
         setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
         addMouseTrackListener(listener);
      } else {
         setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
         removeMouseTrackListener(listener);
      }
   }

   public boolean isHyperEnabled() {
      return hyperEnabled;
   }

   public void setHyperEnabled(boolean hyperEnabled) {
      this.hyperEnabled = hyperEnabled;
      refresh();
   }

   @Override
   protected void checkSubclass() {
   }

}

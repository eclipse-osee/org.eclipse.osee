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

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.eclipse.osee.framework.ui.swt.styledText.ASpellWord;
import org.eclipse.osee.framework.ui.swt.styledText.IDictionary;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * PaintListener that will underline all misspelled words in a StyledText widget. This class must be extended to provide
 * for getting the dictionary and activating/deactivating certain functionality.
 * 
 * @author Donald G. Dunne
 */
public class XTextSpellCheckPaintListener implements PaintListener {

   private final IDictionary dict;
   private final XText xText;
   private Set<ASpellWord> errors = new HashSet<ASpellWord>();
   private XTextSpellModifyDictionary modDict;

   public XTextSpellCheckPaintListener(final XText xText, IDictionary dict) {
      this.xText = xText;
      this.dict = dict;
      if (modDict != null) {
         addXTextSpellModifyDictionary(modDict);
      }
   }

   public void addXTextSpellModifyDictionary(XTextSpellModifyDictionary modDict) {
      this.modDict = modDict;
      xText.getStyledText().addMouseListener(mouseListener);
      xText.getStyledText().addDisposeListener(new DisposeListener() {
         public void widgetDisposed(DisposeEvent e) {
            xText.getStyledText().removeMouseListener(mouseListener);
         }
      });
   }

   public void paintControl(PaintEvent e) {
      GC gc = e.gc;
      gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
      if (xText != null) {
         String text = xText.getStyledText().getText();

         // Get spelling errors
         getErrors(text);
         for (ASpellWord sw : errors)
            drawError(sw.start, sw.word.length(), xText.getStyledText(), gc);
      }
   }

   private MouseListener mouseListener = new MouseListener() {
      public void mouseUp(org.eclipse.swt.events.MouseEvent e) {
         StyledText styledText = (StyledText) e.widget;
         int offset = 0;
         try {
            offset = styledText.getOffsetAtLocation(new Point(e.x, e.y));
         } catch (IllegalArgumentException ex) {
            // Illegal arguement exception happens when selected point is outside
            // the range of the rectangle.  Since it does it's own calculation, just
            // throw this exception away.
            return;
         }
         for (ASpellWord sw : errors) {
            if (sw.start < offset && (sw.start + sw.word.length()) > offset) {
               // System.out.println("Found error word " + sw.word);
               handleErrorSelected(sw);
               break;
            }
         }
      };

      public void mouseDoubleClick(MouseEvent e) {
      }

      public void mouseDown(MouseEvent e) {
      }
   };

   private void handleErrorSelected(final ASpellWord sw) {
      Menu menu = xText.getStyledText().getMenu();

      new MenuItem(menu, SWT.SEPARATOR);

      MenuItem addLocal = new MenuItem(menu, SWT.NONE);
      addLocal.setText("Add \"" + sw.word + "\" to Personal dictionary.");
      addLocal.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            if (modDict.addToLocalDictionary(sw.word)) {
               xText.getStyledText().redraw();
            }
         }
      });

      MenuItem addGlobal = new MenuItem(menu, SWT.NONE);
      addGlobal.setText("Add \"" + sw.word + "\" to Global dictionary.");
      addGlobal.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            if (modDict.addToGlobalDictionary(sw.word)) {
               xText.getStyledText().redraw();
            }
         }
      });
      menu.addMenuListener(new MenuListener() {
         public void menuHidden(MenuEvent e) {
            xText.getStyledText().setMenu(xText.getDefaultMenu());
         }

         public void menuShown(MenuEvent e) {
         };
      });
   }

   /**
    * Draws a single spelling error squiggly line
    * 
    * @param offset - offset of bad word
    * @param len - length of bad word
    */
   private void drawError(int offset, int len, StyledText sText, GC gc) {
      if (sText.isDisposed()) return;
      // Convert to coordinates
      try {
         Point off1 = sText.getLocationAtOffset(offset);
         off1.y--;
         Point off2 = sText.getLocationAtOffset(offset + len);
         off2.y--;
         int h = sText.getLineHeight();
         int[] polyline = computePolyline(off1, off2, h);
         gc.drawPolyline(polyline);
      } catch (RuntimeException e) {
      }
   }

   /**
    */
   private void getErrors(String str) {
      errors.clear();
      StringTokenizer st = new StringTokenizer(str, "[\t\r\n ]", true);
      int loc = 0;
      while (st.hasMoreTokens()) {
         String string = st.nextToken();
         // if not a whitespace character
         if (!string.matches("^\\s*$")) {
            // System.out.println("isWord: orig *" + string + "* => *" + word + "*");
            if (!dict.isWord(string)) {
               ASpellWord sw = new ASpellWord(string, loc);
               // System.out.println("word " + word + " is error");
               errors.add(sw);
            }
         }
         loc += string.length();
      }
   }

   /**
    * Computes the squiggly line.
    * 
    * @param left the left end point
    * @param right the right end point
    * @param height the height of the squiggly line
    * @return the polyline array
    */
   private int[] computePolyline(Point left, Point right, int height) {

      final int WIDTH = 3;
      final int HEIGHT = 0;

      int w2 = 2 * WIDTH;
      int peeks = (right.x - left.x) / w2;

      int leftX = left.x;

      // compute (number of points) * 2
      int length = 4 * peeks + 2;
      if (length <= 0) return new int[0];

      int[] coordinates = new int[length];

      // compute top and bottom of peeks
      int bottom = left.y + height;
      int top = bottom - HEIGHT;

      // populate array with peek coordinates
      int index = 0;
      for (int i = 0; i < peeks; i++) {
         coordinates[index++] = leftX + (w2 * i);
         coordinates[index++] = bottom;
         coordinates[index++] = coordinates[index - 3] + WIDTH;
         coordinates[index++] = top;
      }
      // add the last down flank
      coordinates[length - 2] = left.x + (w2 * peeks);
      coordinates[length - 1] = bottom;
      return coordinates;
   }

}
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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.eclipse.osee.framework.ui.skynet.util.OseeDictionary;
import org.eclipse.osee.framework.ui.swt.styledText.ASpellWord;
import org.eclipse.osee.framework.ui.swt.styledText.IDictionary;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
public class XTextSpellCheckListener implements ModifyListener {

   private final IDictionary dict;
   private final XText xText;
   private final Set<ASpellWord> errors = new LinkedHashSet<>();
   private XTextSpellModifyDictionary modDict = null;
   private Integer maxLength = 50000;

   public XTextSpellCheckListener(final XText xText, IDictionary dict) {
      this.xText = xText;
      this.dict = dict;
      if (modDict != null) {
         addXTextSpellModifyDictionary(modDict);
      }
      refreshStyleRanges();
   }

   public void addXTextSpellModifyDictionary(XTextSpellModifyDictionary modDict) {
      if (xText == null || xText.getStyledText() == null || xText.getStyledText().isDisposed()) {
         return;
      }
      this.modDict = modDict;
      xText.getStyledText().addMouseListener(mouseListener);
      xText.getStyledText().addDisposeListener(new DisposeListener() {
         @Override
         public void widgetDisposed(DisposeEvent e) {
            if (xText.getStyledText() == null || xText.getStyledText().isDisposed()) {
               return;
            }
            xText.getStyledText().removeMouseListener(mouseListener);
         }
      });
   }

   private final MouseListener mouseListener = new MouseListener() {
      @Override
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
            if (sw.start < offset && sw.start + sw.word.length() > offset) {
               // System.out.println("Found error word " + sw.word);
               handleErrorSelected(sw);
               break;
            }
         }
      };

      @Override
      public void mouseDoubleClick(MouseEvent e) {
         // do nothing
      }

      @Override
      public void mouseDown(MouseEvent e) {
         // do nothing
      }
   };

   private void handleErrorSelected(final ASpellWord sw) {
      Menu menu = xText.getStyledText().getMenu();

      new MenuItem(menu, SWT.SEPARATOR);

      MenuItem addLocal = new MenuItem(menu, SWT.NONE);
      addLocal.setText("Add \"" + sw.word + "\" to Personal dictionary.");
      addLocal.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (modDict.addToLocalDictionary(sw.word)) {
               xText.getStyledText().redraw();
            }
         }
      });

      MenuItem addGlobal = new MenuItem(menu, SWT.NONE);
      addGlobal.setText("Add \"" + sw.word + "\" to Global dictionary.");
      addGlobal.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (modDict.addToGlobalDictionary(sw.word)) {
               xText.getStyledText().redraw();
            }
         }
      });
      menu.addMenuListener(new MenuListener() {
         @Override
         public void menuHidden(MenuEvent e) {
            xText.getStyledText().setMenu(xText.getDefaultMenu());
         }

         @Override
         public void menuShown(MenuEvent e) {
            // do nothing
         };
      });
   }

   private void refreshStyleRanges() {
      String text = xText.getStyledText().getText();
      // Only handle urls if widget is under maxLength size
      if (text.length() > maxLength) {
         xText.getStyledText().setStyleRanges(new StyleRange[] {});
         return;
      }

      // Get spelling errors
      getErrors(text);

      StyleRange[] styles = new StyleRange[errors.size()];
      int index = 0;
      for (ASpellWord sw : errors) {
         StyleRange styleRange = new StyleRange();
         styleRange.underline = true;
         styleRange.data = sw.word;
         styleRange.start = sw.start;
         styleRange.length = sw.word.length();
         styleRange.underlineColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
         styles[index++] = styleRange;
      }
      xText.getStyledText().setStyleRanges(styles);
   }

   private void getErrors(String str) {
      errors.clear();
      StringTokenizer st = new StringTokenizer(str, "[\t\r\n ]", true);
      int loc = 0;
      while (st.hasMoreTokens()) {
         String string = st.nextToken();
         // if not a whitespace character
         if (!string.matches("^\\s*$")) {
            //            System.out.println("isWord: orig *" + string + "* => *" + string + "*");
            if (!dict.isWord(string)) {
               //               System.out.println("word " + string + " is error");
               String cleanError = OseeDictionary.getInstance().getCleanWord(string);
               ASpellWord sw = new ASpellWord(cleanError, loc);
               errors.add(sw);
            }
         }
         loc += string.length();
      }
   }

   @Override
   public void modifyText(ModifyEvent e) {
      if (xText == null || xText.getStyledText() == null || xText.getStyledText().isDisposed()) {
         return;
      }
      if (xText != null) {
         refreshStyleRanges();
      }

   }

   /**
    * @return the maxLength
    */
   public Integer getMaxLength() {
      return maxLength;
   }

   /**
    * @param maxLength the maxLength to set
    */
   public void setMaxLength(Integer maxLength) {
      this.maxLength = maxLength;
   }

}
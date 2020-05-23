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

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

/**
 * PaintListener that will turn any http://[^ ] into selectable hyperlink.
 *
 * @author Donald G. Dunne
 */
public class XTextUrlListener implements ModifyListener {

   private final XText xText;
   private final Set<UrlWord> urls = new HashSet<>();
   private Integer maxLength = 50000;

   public class UrlWord {
      public String word;
      public int start;

      public UrlWord(String word, int start) {
         this.word = word;
         this.start = start;
      }
   }

   public XTextUrlListener(final XText xText) {
      this.xText = xText;
      refreshStyleRanges();
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

   private void getErrors(String str) {
      urls.clear();
      StringTokenizer st = new StringTokenizer(str, "[\t\r\n ]", true);
      int loc = 0;
      while (st.hasMoreTokens()) {
         String string = st.nextToken();
         if (string.matches("\\(?\\b(http[s]{0,1}://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]")) {
            //            System.out.println(String.format("url found [%s]", string));
            UrlWord sw = new UrlWord(string, loc);
            urls.add(sw);
         }
         loc += string.length();
      }
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
      for (UrlWord sw : urls) {
         StyleRange styleRange = new StyleRange();
         styleRange.underlineStyle = SWT.UNDERLINE_LINK;
         styleRange.underline = true;
         styleRange.data = sw.word;
         styleRange.start = sw.start;
         styleRange.length = sw.word.length();
         styleRange.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
         xText.getStyledText().setStyleRange(styleRange);
      }
   }

   private final MouseListener mouseListener = new MouseListener() {
      @SuppressWarnings("deprecation")
      @Override
      public void mouseUp(org.eclipse.swt.events.MouseEvent e) {

         StyledText styledText = xText.getStyledText();
         int offset = 0;
         try {
            offset = styledText.getOffsetAtLocation(new Point(e.x, e.y));
         } catch (IllegalArgumentException ex) {
            // Illegal arguement exception happens when selected point is outside
            // the range of the rectangle.  Since it does it's own calculation, just
            // throw this exception away.
            return;
         }
         for (UrlWord sw : urls) {
            if (sw.start < offset && sw.start + sw.word.length() > offset) {
               // System.out.println("Found error word " + sw.word);
               handleSelected(sw);
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

   private void handleSelected(final UrlWord sw) {
      Job job = new Job(String.format("Opening browser for [%s].", sw.word)) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            Program.launch(sw.word);
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job, true);
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

   public Integer getMaxLength() {
      return maxLength;
   }

   public void setMaxLength(Integer maxLength) {
      this.maxLength = maxLength;
   }
}
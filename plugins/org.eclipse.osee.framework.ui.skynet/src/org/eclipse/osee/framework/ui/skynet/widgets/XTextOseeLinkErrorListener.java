/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Display;

/**
 * PaintListener that will turn any oseeimagelink:... into selectable hyperlink.
 *
 * @author Jaden W. Puckett
 */
public class XTextOseeLinkErrorListener implements ModifyListener {

   private final XText xText;
   private final Set<OseeLinkErrorWord> linkErrors = new HashSet<>();
   private Integer maxLength = 50000;
   public static Pattern oseeLinkErrorPattern =
      Pattern.compile("Linked artifact \\[(.*?)\\] has not been found\\. Remove this text\\.");
   private final BranchToken branchToken;

   public class OseeLinkErrorWord {
      public String word;
      public int start;
      public long id;

      public OseeLinkErrorWord(String word, int start, long id) {
         this.word = word;
         this.start = start;
         this.id = id;
      }
   }

   public XTextOseeLinkErrorListener(final XText xText, BranchToken branchToken) {
      this.xText = xText;
      this.branchToken = branchToken;
      refreshStyleRanges();
      xText.getStyledText().addDisposeListener(new DisposeListener() {
         @Override
         public void widgetDisposed(DisposeEvent e) {
            if (xText.getStyledText() == null || xText.getStyledText().isDisposed()) {
               return;
            }
         }
      });
   }

   private void getLinks(String str) {
      linkErrors.clear();
      Matcher m = oseeLinkErrorPattern.matcher(str);
      while (m.find()) {
         String string = m.group();
         long id = Long.valueOf(m.group(1));
         OseeLinkErrorWord sw = new OseeLinkErrorWord(string, m.start(), id);
         linkErrors.add(sw);
      }
   }

   private void refreshStyleRanges() {
      String text = xText.getStyledText().getText();
      getLinks(text);
      for (OseeLinkErrorWord linkError : linkErrors) {
         StyleRange styleRange = new StyleRange();
         styleRange.data = linkError.word;
         styleRange.start = linkError.start;
         styleRange.length = linkError.word.length();
         styleRange.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
         xText.getStyledText().setStyleRange(styleRange);
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

   public Integer getMaxLength() {
      return maxLength;
   }

   public void setMaxLength(Integer maxLength) {
      this.maxLength = maxLength;
   }
}
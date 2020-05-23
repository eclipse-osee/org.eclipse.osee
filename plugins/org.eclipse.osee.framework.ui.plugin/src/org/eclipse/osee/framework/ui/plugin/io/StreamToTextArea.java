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

package org.eclipse.osee.framework.ui.plugin.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;

/**
 * @author Roberto E. Escobar
 */
public class StreamToTextArea extends Thread {
   private final InputStream is;
   private final String type;
   private final FormattedText textArea;
   private final StyledText styledText;
   private int swtColor;
   private boolean isStopped;

   public StreamToTextArea(InputStream is, String type, FormattedText textArea) {
      this.is = is;
      this.type = type;
      this.textArea = textArea;
      this.styledText = textArea.getStyledText();
      this.isStopped = false;
   }

   public void typeColor(int swtColor) {
      this.swtColor = swtColor;
   }

   public void setStopped(boolean value) {
      this.isStopped = value;
   }

   @Override
   public void run() {
      try {
         BufferedReader br = new BufferedReader(new InputStreamReader(is));
         String line = null;
         while (true != isStopped && null != textArea && true != textArea.getStyledText().isDisposed() && null != (line =
            br.readLine())) {
            final String toDisplay = line;
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  if (textArea != null && !textArea.getStyledText().isDisposed()) {
                     textArea.addText("\t" + type + "> ", SWT.NORMAL, swtColor);
                     textArea.addText(toDisplay + "\n");
                     styledText.setSelection(styledText.getCharCount());
                  }
               }
            });
         }
      } catch (IOException ioe) {
         ioe.printStackTrace();
      }
   }
}

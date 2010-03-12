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

package org.eclipse.osee.framework.ui.plugin.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
public class StreamToTextArea extends Thread {
   private InputStream is;
   private String type;
   private FormattedText textArea;
   private StyledText styledText;
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

   public void run() {
      try {
         BufferedReader br = new BufferedReader(new InputStreamReader(is));
         String line = null;
         while (true != isStopped && null != textArea && true != textArea.getStyledText().isDisposed() && null != (line =
               br.readLine())) {
            final String toDisplay = line;
            Display.getDefault().asyncExec(new Runnable() {
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

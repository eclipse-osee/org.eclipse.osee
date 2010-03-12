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
package org.eclipse.osee.framework.ui.service.control.jobs;

import org.eclipse.osee.framework.ui.plugin.io.StreamToTextArea;
import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class TextDisplayHelper {

   private FormattedText formattedText;
   private StreamToTextArea errorGobbler;
   private StreamToTextArea outputGobbler;

   public TextDisplayHelper(FormattedText formattedText) {
      this.formattedText = formattedText;
      this.errorGobbler = null;
      this.outputGobbler = null;
   }

   public void updateScrollBar() {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
         public void run() {
            formattedText.getStyledText().setSelection(formattedText.getStyledText().getCharCount());
         }
      });
   }

   public void addText(final String toDisplay, final int format, final int color, final boolean underline) {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
         public void run() {
            formattedText.addText(toDisplay, format, color, underline);
         }
      });
   }

   public void clear() {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
         public void run() {
            formattedText.clearTextArea();
         }
      });
   }

   public FormattedText getFormattedText() {
      return formattedText;
   }

   public void startProcessHandling(Process process) {
      disposeProcessHandling();
      errorGobbler = new StreamToTextArea(process.getErrorStream(), "\t\terr", getFormattedText());
      errorGobbler.setName("ServiceErrorHandler");
      errorGobbler.typeColor(SWT.COLOR_RED);
      outputGobbler = new StreamToTextArea(process.getInputStream(), "\t\tout", getFormattedText());
      outputGobbler.setName("ServiceOutputHandler");
      outputGobbler.typeColor(SWT.COLOR_DARK_BLUE);

      errorGobbler.start();
      outputGobbler.start();
   }

   public void disposeProcessHandling() {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            if (errorGobbler != null) {
               errorGobbler.setStopped(true);
            }
            if (outputGobbler != null) {
               outputGobbler.setStopped(true);
            }
         }
      });
   }
}

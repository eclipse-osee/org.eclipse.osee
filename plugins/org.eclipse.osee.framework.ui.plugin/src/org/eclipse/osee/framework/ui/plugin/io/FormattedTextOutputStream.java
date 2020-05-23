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

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import org.eclipse.osee.framework.ui.swt.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;

/**
 * @author Roberto E. Escobar
 */
public class FormattedTextOutputStream extends OutputStream {

   private final FormattedText textArea;
   private String charset;
   private final StyledText styledText;
   private final String type;
   private int swtColor;

   public FormattedTextOutputStream(FormattedText textArea, String type) {
      this.textArea = textArea;
      this.styledText = textArea.getStyledText();
      this.charset = null;
      this.type = type;
   }

   public FormattedTextOutputStream(FormattedText ta, String charset, String type) throws UnsupportedCharsetException {
      this(ta, type);

      Charset.forName(charset);
      this.charset = charset;
   }

   @Override
   public void write(byte[] b) {
      write(b, 0, b.length);
   }

   private boolean isTextAreaAvailable() {
      return textArea != null && !textArea.getStyledText().isDisposed();
   }

   public void typeColor(int swtColor) {
      this.swtColor = swtColor;
   }

   @Override
   public void write(byte[] b, int off, int len) {
      String s;
      try {
         if (charset == null) {
            s = new String(b, off, len);
         } else {
            s = new String(b, off, len, charset);
         }
      } catch (UnsupportedEncodingException ex) {
         throw new Error("encoding support was already verified", ex);
      }
      synchronized (textArea) {
         if (isTextAreaAvailable()) {
            textArea.addText("\t" + type + "> ", SWT.BOLD, swtColor);
            textArea.addText(s + "\n");
            styledText.setSelection(styledText.getCharCount());
         }
      }
   }

   @Override
   public void write(int b) {
      byte[] tmp = {(byte) b};
      write(tmp);
   }
}

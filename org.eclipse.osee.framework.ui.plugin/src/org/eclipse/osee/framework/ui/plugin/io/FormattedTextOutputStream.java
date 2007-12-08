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

   private FormattedText textArea;
   private String charset;
   private StyledText styledText;
   private String type;
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

   public void write(byte[] b) {
      write(b, 0, b.length);
   }

   private boolean isTextAreaAvailable() {
      return (textArea != null && !textArea.getStyledText().isDisposed());
   }

   public void typeColor(int swtColor) {
      this.swtColor = swtColor;
   }

   public void write(byte[] b, int off, int len) {
      String s;
      try {
         if (charset == null)
            s = new String(b, off, len);
         else
            s = new String(b, off, len, charset);
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

   public void write(int b) {
      byte[] tmp = {(byte) b};
      write(tmp);
   }
}

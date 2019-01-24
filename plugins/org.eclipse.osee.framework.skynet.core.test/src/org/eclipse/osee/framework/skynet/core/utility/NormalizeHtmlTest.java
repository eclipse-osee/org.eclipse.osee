/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.utility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Marc A. Potter
 */
public class NormalizeHtmlTest {

   private static final String SUPPORT_PATH = "support";
   private static final String CONVERTED_HTML = "NormalizeHtml_converted.htm";
   private static final String TEST_DOC = "NormalizeHtml_test_doc.htm";

   @Test
   public void test() throws Exception {
      String rawInput = getResource(TEST_DOC);
      String expected = getResource(CONVERTED_HTML);
      String input = NormalizeHtml.convertToNormalizedHTML(rawInput);
      input = bodyOnly(input);
      expected = bodyOnly(expected);
      expected = expected.replaceAll("\r", "");
      input = input.replaceAll("\r", "");
      Assert.assertEquals("Converted HTML does not equal expected", expected, input);

      input = rawInput.replaceAll("BODY", "body");
      input = bodyOnly(input);
      input = NormalizeHtml.wrapAndNormalizeHTML(input, false, false, false);
      input = bodyOnly(input);
      input = input.replaceAll("\r", "");
      Assert.assertEquals("Converted body only HTML does not equal expected", expected, input);
   }

   private String getResource(String resource) throws IOException {
      String resourcePath = String.format("%s/%s", SUPPORT_PATH, resource);

      URL input = getClass().getResource(resourcePath);

      String output = null;
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(input.openStream());
         output = Lib.inputStreamToString(inputStream);
      } finally {
         Lib.close(inputStream);
      }
      return output;
   }

   private static String bodyOnly(String input) {
      int iBodyStart = input.indexOf("<body");
      int iBodyEnd = input.indexOf("</body");
      if (iBodyStart == -1 || iBodyEnd == -1) {
         return input;
      }
      iBodyStart = iBodyStart + 1 + input.substring(iBodyStart).indexOf('>');
      return input.substring(iBodyStart, iBodyEnd - 1);
   }

}

/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - Initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import javax.ws.rs.core.StreamingOutput;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link StreamingOutput} implementations.
 *
 * @author Loren K. Ashley
 */

public class StreamingOutputTestSuite {

   //@formatter:off
   private static String testString =
      "Three Blinde Mice,\n" +
      "Three Blinde Mice,\n" +
      "Dame Iulian,\n" +
      "Dame Iulian,\n" +
      "the Miller and his merry olde Wife,\n" +
      "shee scrapte her tripe licke thou the knife.";
   //@formatter:on

   @Test
   public void testCharSequenceStreamingOutput() {

      var charSequenceStreamingOutput = new CharSequenceStreamingOutput(StreamingOutputTestSuite.testString);

      var outputStream = new ByteArrayOutputStream(StreamingOutputTestSuite.testString.length() * 2) {
         byte[] getBytes() {
            return this.buf;
         }
      };

      try {
         charSequenceStreamingOutput.write(outputStream);
      } catch (Exception e) {
         Assert.assertFalse(e.getMessage(), false);
      }

      var startInclusive = 0;
      var endExclusive = StreamingOutputTestSuite.testString.length();

      //@formatter:off
      Assert.assertEquals
         (
            Arrays.compare
               (
                 StreamingOutputTestSuite.testString.getBytes(),
                 startInclusive,
                 endExclusive,
                 outputStream.getBytes(),
                 startInclusive,
                endExclusive
              ),
            0
         );
      //@formatter:on
   }

   @Test
   public void testInputStreamStreamingOutput() {

      var inputStream = new ByteArrayInputStream(StreamingOutputTestSuite.testString.getBytes());

      var inputStreamStreamingOutput = new InputStreamStreamingOutput(inputStream);

      var outputStream = new ByteArrayOutputStream(StreamingOutputTestSuite.testString.length() * 2) {
         byte[] getBytes() {
            return this.buf;
         }
      };

      try {
         inputStreamStreamingOutput.write(outputStream);
      } catch (Exception e) {
         Assert.assertFalse(e.getMessage(), false);
      }

      var startInclusive = 0;
      var endExclusive = StreamingOutputTestSuite.testString.length();

      //@formatter:off
      Assert.assertEquals
         (
            Arrays.compare
               (
                 StreamingOutputTestSuite.testString.getBytes(),
                 startInclusive,
                 endExclusive,
                 outputStream.getBytes(),
                 startInclusive,
                endExclusive
              ),
            0
         );
      //@formatter:on
   }

}

/* EOF */

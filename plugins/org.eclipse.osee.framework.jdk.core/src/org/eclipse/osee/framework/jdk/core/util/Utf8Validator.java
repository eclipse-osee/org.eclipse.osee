/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

public class Utf8Validator {

   /**
    * Checks if the given String contains only valid UTF-8 characters by encoding it to UTF-8 bytes and then attempting
    * to decode those bytes strictly.
    *
    * @param inputText The String to validate.
    * @return true if the String represents valid UTF-8, false otherwise.
    */
   public static boolean isValidUtf8(String inputText) {
      if (inputText == null) {
         // Or handle nulls as needed, perhaps return true or throw exception
         return true;
      }

      // Get the standard UTF-8 Charset
      Charset utf8Charset = StandardCharsets.UTF_8;

      // 1. Encode the String to UTF-8 bytes
      byte[] utf8Bytes = inputText.getBytes(utf8Charset);

      // 2. Attempt to decode the bytes back using a strict UTF-8 decoder
      CharsetDecoder decoder = utf8Charset.newDecoder();

      // Configure the decoder to report errors rather than replacing or ignoring them
      decoder.onMalformedInput(CodingErrorAction.REPORT);
      decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

      try {
         // Wrap the byte array in a ByteBuffer
         ByteBuffer byteBuffer = ByteBuffer.wrap(utf8Bytes);
         // Attempt to decode. We don't need the result, just whether it throws.
         CharBuffer decoded = decoder.decode(byteBuffer);
         // If decode succeeds without exception, it's valid UTF-8
         return true;
      } catch (CharacterCodingException e) {
         // An exception occurred, meaning the byte sequence was not valid UTF-8
         // This can happen if the original string contained characters that
         // couldn't be represented correctly or if invalid sequences were formed.
         return false;
      }
   }

   /**
    * More direct check if you happen to have the input as raw bytes before it's converted to a Java String. This is
    * often more robust if you can intercept the data at that stage.
    *
    * @param inputBytes The byte array to validate.
    * @return true if the bytes represent valid UTF-8, false otherwise.
    */
   public static boolean isValidUtf8(byte[] inputBytes) {
      if (inputBytes == null) {
         return true; // Or handle as needed
      }

      CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
      decoder.onMalformedInput(CodingErrorAction.REPORT);
      decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

      try {
         decoder.decode(ByteBuffer.wrap(inputBytes));
         return true;
      } catch (CharacterCodingException e) {
         return false;
      }
   }

   // Example Usage
   public static void main(String[] args) {
      String validText = "This is standard text with 😊 emoji.";
      String textWithInvalidChars = "Text with maybe a lone surrogate \ud800 here."; // Example of an unpaired surrogate
      String commonMultiByteChars = "\uD83D\uDE80 ";

      // Simulate receiving potentially problematic bytes directly
      // (e.g., Windows-1252 'é' represented as a single byte)
      byte[] invalidSequenceBytes = {(byte) 0x48, (byte) 0x65, (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0xC3}; // Incomplete 'é' sequence

      System.out.println("'" + validText + "': Is valid UTF-8? " + isValidUtf8(validText));
      System.out.println("'" + textWithInvalidChars + "': Is valid UTF-8? " + isValidUtf8(textWithInvalidChars));
      System.out.println("Byte sequence: Is valid UTF-8? " + isValidUtf8(invalidSequenceBytes));
      System.out.println("Byte sequence: Is valid UTF-8? " + isValidUtf8(commonMultiByteChars) + commonMultiByteChars);

      // Example with potential copy-paste issue (often involves control chars or weird encodings)
      // Let's assume a character from CP-1252 got pasted somehow
      // The byte for 'é' in CP-1252 is 0xE9. If this byte stream was misinterpreted as UTF-8
      // it would be invalid.
      byte[] cp1252Bytes = {(byte) 0x54, (byte) 0x65, (byte) 0x73, (byte) 0x74, (byte) 0xE9, (byte) 0xF1}; // "Test" + CP-1252 'é'
      System.out.println("CP-1252 byte sequence: Is valid UTF-8? " + isValidUtf8(cp1252Bytes));

      // How Java might represent it if it came via a mis-configured process
      // (This string creation might replace the invalid char depending on default charset)
      String potentiallyMangled = new String(cp1252Bytes, StandardCharsets.ISO_8859_1); // Read as Latin-1
      System.out.println("String from CP-1252 bytes ('" + potentiallyMangled + "'): Is valid UTF-8? " + isValidUtf8(
         potentiallyMangled));

   }
}
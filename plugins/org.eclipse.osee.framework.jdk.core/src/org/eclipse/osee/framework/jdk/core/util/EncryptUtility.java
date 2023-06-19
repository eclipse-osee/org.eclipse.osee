/*********************************************************************
 * Copyright (c) 2014 Boeing
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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author David W. Miller
 */
public final class EncryptUtility {
   private final static byte[] linebreak = {}; // Remove Base64 encoder default line break
   private final static String TRANSFORMATION = "AES/ECB/PKCS5Padding";
   private final static String ALGORITHM = "AES";

   public static String encryptWithExceptions(String plainText, String secret, String transformation, String algorithm)
      throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException,
      NoSuchPaddingException, UnsupportedEncodingException {
      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret.getBytes(), algorithm));

      byte[] encrypted = cipher.doFinal(plainText.getBytes());

      Base64 encoder = new Base64(32, linebreak, true);
      return new String(encoder.encode(encrypted), "UTF-8");
   }

   public static String decryptWithExceptions(String codedText, String secret, String transformation, String algorithm)
      throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException,
      NoSuchPaddingException, UnsupportedEncodingException {
      Base64 encoder = new Base64(32, linebreak, true);
      byte[] encypted = encoder.decode(codedText.getBytes());

      Cipher cipher = Cipher.getInstance(transformation);
      cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secret.getBytes(), algorithm));

      byte[] decrypted = cipher.doFinal(encypted);

      return new String(decrypted, "UTF-8");
   }

   public static String encrypt(String plainText, String secret) {
      Conditions.checkNotNull(plainText, "Provided text");
      Conditions.checkNotNull(secret, "Secret key");

      String toReturn = "";
      try {
         toReturn = encryptWithExceptions(plainText, secret, TRANSFORMATION, ALGORITHM);
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
      return toReturn;
   }

   public static String decrypt(String codedText, String secret) {
      Conditions.checkNotNull(codedText, "Coded text");
      Conditions.checkNotNull(secret, "Secret key");

      String toReturn = "";
      try {
         toReturn = decryptWithExceptions(codedText, secret, TRANSFORMATION, ALGORITHM);
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
      return toReturn;
   }
}

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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author David W. Miller
 */
public final class EncryptUtility {
   private final static byte[] linebreak = {}; // Remove Base64 encoder default line break
   private final static String TRANSFORMATION = "AES/CBC/PKCS5Padding";
   private final static String ALGORITHM = "AES";
   private final static int IV_LENGTH = 16;

   public static String encryptWithExceptions(String plainText, String secret, String transformation, String algorithm)
      throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException,
      NoSuchPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
      SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), algorithm);
      Cipher cipher = Cipher.getInstance(transformation);

      byte[] iv = new byte[IV_LENGTH];
      new SecureRandom().nextBytes(iv);
      IvParameterSpec ivSpec = new IvParameterSpec(iv);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
      byte[] cipherText = cipher.doFinal(plainText.getBytes());
      // Prepend IV to ciphertext for use during decryption
      byte[] encrypted = new byte[iv.length + cipherText.length];
      System.arraycopy(iv, 0, encrypted, 0, iv.length);
      System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);

      Base64 encoder = new Base64(32, linebreak, true);
      return new String(encoder.encode(encrypted), "UTF-8");
   }

   public static String decryptWithExceptions(String codedText, String secret, String transformation, String algorithm)
      throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException,
      NoSuchPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
      Base64 encoder = new Base64(32, linebreak, true);
      byte[] decoded = encoder.decode(codedText.getBytes());

      SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), algorithm);
      Cipher cipher = Cipher.getInstance(transformation);

      // Extract IV from the beginning of the decoded data
      byte[] iv = new byte[IV_LENGTH];
      System.arraycopy(decoded, 0, iv, 0, iv.length);
      IvParameterSpec ivSpec = new IvParameterSpec(iv);
      cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
      byte[] decrypted = cipher.doFinal(decoded, iv.length, decoded.length - iv.length);

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

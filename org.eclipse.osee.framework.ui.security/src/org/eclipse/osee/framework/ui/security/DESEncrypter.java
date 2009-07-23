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
package org.eclipse.osee.framework.ui.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.security.internal.Activator;

/**
 * Encrypts and Decrypts inputStreams using DES and MD5 hashing.
 * 
 * @author Jeff C. Phillips
 */

public class DESEncrypter {
   private static final String KEY_FORMAT = "PBEWithMD5AndDES";
   private static int iterationCount = 15;
   private Cipher encryptCipher;
   private Cipher decryptCipher;
   private SecretKey key;

   private static byte[] salt =
         {(byte) 0x8E, (byte) 0x45, (byte) 0x77, (byte) 0x94, (byte) 0x21, (byte) 0x32, (byte) 0x90, (byte) 0x22};

   /**
    * This constructor will auto generate a secret key. Therefore, the getKey() method should be used to save off the
    * key to decrypt.
    * 
    * @throws InvalidKeySpecException
    * @throws NoSuchAlgorithmException
    */
   public DESEncrypter() throws InvalidKeySpecException, NoSuchAlgorithmException {
      this(GUID.create());
   }

   /**
    * @param passPhrase
    * @throws NoSuchAlgorithmException
    * @throws InvalidKeySpecException
    */
   public DESEncrypter(String passPhrase) throws InvalidKeySpecException, NoSuchAlgorithmException {
      this(SecretKeyFactory.getInstance(KEY_FORMAT).generateSecret(
            new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount)));
   }

   /**
    * @param key
    */
   public DESEncrypter(SecretKey key) {
      this.key = key;

      configureCipher(key);
   }

   public SecretKey getKey() {
      return key;
   }

   private void configureCipher(SecretKey key) {
      try {
         encryptCipher = Cipher.getInstance(key.getAlgorithm());
         decryptCipher = Cipher.getInstance(key.getAlgorithm());

         AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

         encryptCipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
         decryptCipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "", ex);
      }
   }

   /**
    * Encrypts inputStream and write data out to outputStream
    * 
    * @param in in , InputStream
    * @param out out, OutputStream
    */
   public void encrypt(InputStream in, OutputStream out) {
      byte[] buffer = new byte[1024];
      int index = 0;

      try {
         out = new CipherOutputStream(out, encryptCipher);

         while ((index = in.read(buffer)) >= 0) {
            out.write(buffer, 0, index);
         }
         in.close();
         out.close();
      } catch (java.io.IOException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "", ex);
      }
   }

   /**
    * decrypts inputStream
    * 
    * @param in , InputStream
    * @return decrypted inputStream
    */
   public InputStream decrypt(InputStream in) {
      in = new CipherInputStream(in, decryptCipher);
      return in;
   }

   /**
    * @param in - inputStream
    * @param out - outputStream
    */
   public void decrypt(InputStream in, OutputStream out) {
      byte[] buffer = new byte[1024];
      int index = 0;

      try {
         in = new CipherInputStream(in, decryptCipher);

         while ((index = in.read(buffer)) >= 0) {
            out.write(buffer, 0, index);
         }
         out.close();
      } catch (java.io.IOException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "", ex);
      }
   }
}

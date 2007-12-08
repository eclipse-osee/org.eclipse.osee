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
import java.security.spec.KeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * Encrypts and Decrypts inputStreams using DES and MD5 hashing.
 * 
 * @author Jeff C. Phillips
 */

public class DESEncrypter {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(DESEncrypter.class);
   private static final String KEY_FORMAT = "PBEWithMD5AndDES";
   private Cipher encryptCipher;
   private Cipher decryptCipher;

   /**
    * @param passPhrase
    */
   public DESEncrypter(String passPhrase) {
      try {
         KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray());
         SecretKey key = SecretKeyFactory.getInstance(KEY_FORMAT).generateSecret(keySpec);
         configureCipher(key);
      } catch (Exception ex) {
         logger.log(Level.SEVERE, "", ex);
      }
   }

   /**
    * @param key Key example: SecretKey key = KeyGenerator.getInstance("DES").generateKey();
    */
   public DESEncrypter(SecretKey key) {
      configureCipher(key);
   }

   private void configureCipher(SecretKey key) {
      try {
         encryptCipher = Cipher.getInstance("DES");
         decryptCipher = Cipher.getInstance("DES");

         encryptCipher.init(Cipher.ENCRYPT_MODE, key);
         decryptCipher.init(Cipher.DECRYPT_MODE, key);
      } catch (Exception ex) {
         logger.log(Level.SEVERE, "", ex);
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
         logger.log(Level.SEVERE, "", ex);
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
         logger.log(Level.SEVERE, "", ex);
      }
   }
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author David W. Miller
 */
public class EncryptUtilityTest {

   private final static String SECRET = "lRL2uka3CwLL88Q1";

   @Rule
   public ExpectedException exception = ExpectedException.none();

   @Test
   public void testSameEncryption() throws Exception {
      String firstEncrypt = "123456:654321";
      String secondEncrypt = "123456:654322";

      String firstEncrypted = EncryptUtility.encrypt(firstEncrypt, SECRET);
      String secondEncrypted = EncryptUtility.encrypt(secondEncrypt, SECRET);
      assertTrue(!firstEncrypted.equals(secondEncrypted));
   }

   @Test
   public void testDecrypt() throws Exception {
      String toEncrypt = "123456:654321";
      checkEncryption(toEncrypt);
   }

   @Test
   public void testCharacters() throws Exception {
      String toEncrypt = "[\t\r]{}0QWer123.,$#!%&*^";
      checkEncryption(toEncrypt);
   }

   @Test
   public void testEmptyString() throws Exception {
      String toEncrypt = "";
      checkEncryption(toEncrypt);
   }

   @Test
   public void testOneCharacter() throws Exception {
      String toEncrypt = "a";
      checkEncryption(toEncrypt);
   }

   @Test
   public void testNullSecretEncrypt() throws Exception {
      String toEncrypt = "a";
      exception.expect(OseeArgumentException.class);
      EncryptUtility.encrypt(toEncrypt, null);
   }

   @Test
   public void testNullSecretDecrypt() throws Exception {
      String encrypted = "a";
      exception.expect(OseeArgumentException.class);
      EncryptUtility.decrypt(encrypted, null);
   }

   @Test
   public void testNullProvidedEncrypt() throws Exception {
      exception.expect(OseeArgumentException.class);
      EncryptUtility.encrypt(null, SECRET);
   }

   @Test
   public void testNullProvidedDecrypt() throws Exception {
      exception.expect(OseeArgumentException.class);
      EncryptUtility.decrypt(null, SECRET);
   }

   private void checkEncryption(String toEncrypt) throws Exception {
      String encrypted = EncryptUtility.encrypt(toEncrypt, SECRET);
      String decrypted = EncryptUtility.decrypt(encrypted, SECRET);
      assertEquals(toEncrypt, decrypted);
   }
}

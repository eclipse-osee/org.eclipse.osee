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
package org.eclipse.osee.jaxrs.server.internal;

import static org.junit.Assert.fail;
import com.google.common.io.InputSupplier;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.server.internal.ext.RestResourceConcatenator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit Test for {@link RestResourceConcatenator}
 * 
 * @author David W. Miller
 */
public class RestResourceConcatenatorTest {

   private static final String VALID_RESOURCE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
      + "<resourceDoc>" //
      + "<classDocs><classDoc><className>org.eclipse.osee.something</className>" //
      + "<commentText><![CDATA[]]></commentText>" //
      + "<methodDocs><methodDoc><methodName>handle</methodName>" //
      + "<commentText><![CDATA[]]></commentText>" //
      + "<responseDoc/></methodDoc></methodDocs></classDoc>" //
      + "</resourceDoc>";

   private static final String INVALID_RESOURCE = //
      "<some stuff = this is not a well formated \n" //
         + "> . other characters that do not matter\n" //
         + "</stuff>\n\n\n";

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   @Test
   public void testNullCreation() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("xmlTag cannot be null");

      String regex = null;
      RestResourceConcatenator rc = new RestResourceConcatenator();
      rc.initialize(regex);

      fail("A null string should not produce " + rc.toString());
   }

   @Test
   public void testEmptyCreation() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("xmlTag cannot be empty");
      String regex = "";
      RestResourceConcatenator rc = new RestResourceConcatenator();
      rc.initialize(regex);
      fail("An empty string should not produce " + rc.toString());
   }

   @Test
   public void testIncorrectRegexSpecialCharsCreation() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid resource document tag");
      String regex = "*&^$";
      RestResourceConcatenator rc = new RestResourceConcatenator();
      rc.initialize(regex);
      fail(regex + " should not produce " + rc.toString());
   }

   @Test
   public void testIncorrectRegexNumsCreation() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid resource document tag");
      String regex = "123";
      RestResourceConcatenator rc = new RestResourceConcatenator();
      rc.initialize(regex);
      fail(regex + " should not produce " + rc.toString());
   }

   @Test
   public void testIncorrectRegexStringCreation() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Invalid resource document tag");
      String regex = "randomWord";
      RestResourceConcatenator rc = new RestResourceConcatenator();
      rc.initialize(regex);
      fail(regex + " should not produce " + rc.toString());
   }

   @Test
   public void testAlreadyInitialized() {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Already initialized");
      String resourceDoc = "resourceDoc";
      RestResourceConcatenator rc1 = new RestResourceConcatenator();
      rc1.initialize(resourceDoc);
      rc1.initialize(resourceDoc);
      fail("initialize shouldn't work twice");
   }

   @Test
   public void testCorrectResourceDocCreation() {
      String resourceDoc = "resourceDoc";

      RestResourceConcatenator rc = new RestResourceConcatenator();
      rc.initialize(resourceDoc);
      Assert.assertNotNull("application Docs resource not created", rc);
   }

   @Test
   public void testCorrectGrammarsCreation() {
      String grammars = "grammars";

      RestResourceConcatenator rc = new RestResourceConcatenator();
      rc.initialize(grammars);
      Assert.assertNotNull("grammars resource not created", rc);

   }

   @Test
   public void testCorrectAppDocCreation() {
      String appDoc = "applicationDocs";

      RestResourceConcatenator rc = new RestResourceConcatenator();
      rc.initialize(appDoc);
      Assert.assertNotNull("application Docs resource not created", rc);
   }

   @Test
   public void testAddEmptyResource() throws IOException {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("bundle resource cannot be empty");

      String regex = "resourceDoc";
      RestResourceConcatenator rc = new RestResourceConcatenator();
      rc.initialize(regex);
      rc.addResource(testSupplier(""));
   }

   @Test
   public void testAddMultipleResources() throws IOException {
      String regex = "resourceDoc";
      RestResourceConcatenator rc = new RestResourceConcatenator();
      rc.initialize(regex);
      rc.addResource(testSupplier(VALID_RESOURCE));
      rc.addResource(testSupplier(VALID_RESOURCE));
      rc.addResource(testSupplier(VALID_RESOURCE));
      Assert.assertTrue(rc.getResources().compareTo(VALID_RESOURCE) == 0);
   }

   @Test
   public void testResourcesWithReturns() throws IOException {
      String value = buildValidResourceDocResourceWithReturns(1);
      String regex = "resourceDoc";
      RestResourceConcatenator rc = new RestResourceConcatenator();
      rc.initialize(regex);
      rc.addResource(testSupplier(value));
      Assert.assertTrue(rc.getResources().compareTo(value) == 0);
   }

   @Test
   public void testInvalidResource() throws IOException {

      String regex = "resourceDoc";
      RestResourceConcatenator rc = new RestResourceConcatenator();
      rc.initialize(regex);
      rc.addResource(testSupplier(INVALID_RESOURCE));
      Assert.assertTrue(rc.getResources().compareTo("nullnull") == 0);
   }

   @Test(expected = OseeCoreException.class)
   public void testGetAsInputStreamNotReady() {
      String regex = "resourceDoc";
      RestResourceConcatenator rc = new RestResourceConcatenator();
      rc.initialize(regex);

      try {
         rc.getAsInputStream();
      } catch (UnsupportedEncodingException ex) {
         fail("not expected to get here" + ex.toString());
      }

   }

   private static InputSupplier<InputStream> testSupplier(final String input) {
      return new InputSupplier<InputStream>() {
         @Override
         public InputStream getInput() throws IOException {
            return Lib.stringToInputStream(input);
         }
      };
   }

   private String buildValidResourceDocResourceWithReturns(int count) {
      StringBuilder toReturn = new StringBuilder();
      toReturn.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      toReturn.append("<resourceDoc>\n");
      for (int i = 0; i < count; ++i) {
         toReturn.append(
            "<classDocs><classDoc><className>org.eclipse.osee.something</className><commentText><![CDATA[]]></commentText>\n");
         toReturn.append(
            "<methodDocs><methodDoc><methodName>handle</methodName><commentText><![CDATA[]]></commentText><responseDoc/></methodDoc></methodDocs></classDoc>\n");
      }
      toReturn.append("</resourceDoc>");
      return toReturn.toString();
   }
}

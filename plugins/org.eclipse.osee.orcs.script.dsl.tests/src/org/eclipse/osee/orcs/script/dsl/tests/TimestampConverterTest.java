/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.script.dsl.tests;

import static org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil.parseDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import org.eclipse.osee.orcs.script.dsl.OrcsScriptUtil;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslFactory;
import org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsStringLiteral;
import org.eclipse.osee.orcs.script.dsl.typesystem.TimestampConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.nodemodel.INode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test Case for {@link TimestampConverter}
 *
 * @author Roberto E. Escobar
 */
public class TimestampConverterTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   private final INode node = null;
   private TimestampConverter converter;

   private final OrcsScriptDslFactory factory = OrcsScriptDslFactory.eINSTANCE;

   @Before
   public void setup() {
      converter = new TimestampConverter();
   }

   @Test
   public void testType() {
      boolean actual = converter.isTimestampType(Date.class);
      assertEquals(true, actual);

      actual = converter.isTimestampType(Object.class);
      assertEquals(false, actual);
   }

   @Test
   public void testTypeObject() {
      OsStringLiteral literal = newLiteral("hello");
      boolean actual = converter.isTimestampType(literal);
      assertEquals(false, actual);

      literal = newLiteral("09/10/2014 11:00:12 AM");
      actual = converter.isTimestampType(literal);
      assertEquals(true, actual);
   }

   private OsStringLiteral newLiteral(String value) {
      OsStringLiteral literal = factory.createOsStringLiteral();
      literal.setValue(value);
      return literal;
   }

   @Test
   public void testNullToValue() {
      Date actual = converter.toValue(null, node);
      assertNull(actual);
   }

   @Test
   public void testToValue() throws ParseException {
      String value = "09/10/2014 11:00:12 AM";
      Date expected = parseDate(value);

      Date actual = converter.toValue(value, node);
      assertEquals(expected, actual);
   }

   @Test
   public void testToValueUsingLocaleSpecificFormat() throws ParseException {
      // Use locale specific timestamp format - system should convert correctly
      // For example: MMM d, yyyy h:mm:ss a
      Date expected = OrcsScriptUtil.parseDate("09/10/2014 11:00:12 AM");
      String localeFormattedDate = DateFormat.getDateTimeInstance().format(expected);

      Date actual = converter.toValue(localeFormattedDate, node);
      assertEquals(expected, actual);
   }

   @Test
   public void testToValueException() {
      thrown.expect(ValueConverterException.class);
      thrown.expectMessage("Invalid timestamp format - format should be [MM/dd/yyyy hh:mm:ss a] or [");
      converter.toValue("asdadsa", node);
   }
}

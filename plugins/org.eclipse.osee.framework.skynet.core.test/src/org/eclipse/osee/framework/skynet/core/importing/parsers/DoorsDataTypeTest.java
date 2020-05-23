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

package org.eclipse.osee.framework.skynet.core.importing.parsers;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link DoorsDataType}
 * 
 * @author David Miller
 */
@RunWith(Parameterized.class)
public final class DoorsDataTypeTest {

   private final String given;
   private final DoorsDataType expected;

   public DoorsDataTypeTest(DoorsDataType expected, String given) {
      this.given = given;
      this.expected = expected;
   }

   @Parameters
   public static Collection<Object[]> getData() {
      return Arrays.asList(new Object[][] {
         {DoorsDataType.HEADING, "Heading"},
         {DoorsDataType.HEADER, "Header"},
         {DoorsDataType.INFORMATION, "Information"},
         {DoorsDataType.REQUIREMENT, "Requirement"},
         {DoorsDataType.DESIGN_REQ, "Design Req"},
         {DoorsDataType.FUNCTIONAL_REQ, "Functional Req"},
         {DoorsDataType.PERFORMANCE_REQ, "Performance Req"},
         {DoorsDataType.PHYSICAL_REQ, "Physical Req"},
         {DoorsDataType.SAFETY_REQ, "Safety Req"},
         {DoorsDataType.ENVIRONMENT_REQ, "Environment Req"},
         {DoorsDataType.INTERFACE_REQ, "Interface Req"},
         {DoorsDataType.TABLE, "Table"},
         {DoorsDataType.FIGURE, "Figure"},
         {DoorsDataType.LIST, "List"},
         {DoorsDataType.NOT_DEFINED, "Not Defined"},
         {DoorsDataType.OTHER, ""},
         {DoorsDataType.OTHER, "random stuff"},
         {DoorsDataType.OTHER, "\t"},
         {DoorsDataType.OTHER, "\n"}});
   }

   @Test
   public void testEnumContents() {
      DoorsDataType actual = DoorsDataType.fromString(given);
      assertEquals(expected, actual);
   }
}

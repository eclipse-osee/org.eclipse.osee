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
 * Test Case for {@link DoorsColumnType}
 * 
 * @author David Miller
 */

@RunWith(Parameterized.class)
public final class DoorsColumnTypeTest {
   private final String given;
   private final DoorsColumnType expected;

   public DoorsColumnTypeTest(DoorsColumnType expected, String given) {
      this.given = given;
      this.expected = expected;
   }

   @Parameters
   public static Collection<Object[]> getData() {
      return Arrays.asList(new Object[][] {
         {DoorsColumnType.ID, "ID"},
         {DoorsColumnType.REQUIREMENTS, "Requirements"},
         {DoorsColumnType.OBJECT_NUMBER, "Object Number"},
         {DoorsColumnType.IS_REQ, "Req?"},
         {DoorsColumnType.PARENT_ID, "Parent ID"},
         {DoorsColumnType.EFFECTIVITY, "Effectivity"},
         {DoorsColumnType.PARAGRAPH_HEADING, "Paragraph Heading"},
         {DoorsColumnType.DOCUMENT_APPLICABILITY, "Document Applicability"},
         {DoorsColumnType.VERIFICATION_CRITERIA, "Verification Criteria"},
         {DoorsColumnType.VERIFICATION_CRITERIA, "Verification Criteria (V-PIDS_Verification)"},
         {DoorsColumnType.CHANGE_STATUS, "Change Status"},
         {DoorsColumnType.OBJECT_HEADING, "Proposed Object Heading"},
         {DoorsColumnType.OBJECT_TEXT, "Proposed Object Text"},
         {DoorsColumnType.CHANGE_RATIONALE, "Change Rationale"},
         {DoorsColumnType.LINKS, "Links"},
         {DoorsColumnType.GUID, "OSEE GUID"},
         {DoorsColumnType.SUBSYSTEM, "Subsystem"},
         {DoorsColumnType.DATA_TYPE, "Data Type"},
         {DoorsColumnType.OTHER, ""},
         {DoorsColumnType.OTHER, "random stuff"},
         {DoorsColumnType.OTHER, "\t"},
         {DoorsColumnType.OTHER, "\n"}});
   }

   @Test
   public void testEnumContents() {
      DoorsColumnType actual = DoorsColumnType.fromString(given);
      assertEquals(expected, actual);
   }
}

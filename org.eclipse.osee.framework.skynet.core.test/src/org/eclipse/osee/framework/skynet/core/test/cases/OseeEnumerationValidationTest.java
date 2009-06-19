/*******************************************************************************
 * Copyright (c) 2004, 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.skynet.core.test.cases;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.junit.After;
import org.junit.Before;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumerationValidationTest {

   private Artifact mockArtifact;
   private AttributeType enumeratedAttributeType;

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   @Before
   public void setUp() throws Exception {
      Branch branch = BranchManager.getCommonBranch();
      // Create an artifact having an enumerated attribute

      enumeratedAttributeType = AttributeTypeManager.getType("GFE / CFE");
      mockArtifact = ArtifactTypeManager.addArtifact("Component", branch);
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#tearDown()
    */
   @After
   public void tearDown() throws Exception {
      mockArtifact.deleteAndPersist();
      mockArtifact = null;
      enumeratedAttributeType = null;
   }

   public List<TestData> getEnumerationCases() {
      List<TestData> data = new ArrayList<TestData>();

      data.add(new TestData("Test 1: Null", null, getErrorStatus("No enum const [GFE / CFE].[null]")));
      data.add(new TestData("Test 2: Empty String", "", getErrorStatus("No enum const [GFE / CFE].[]")));
      data.add(new TestData("Test 3: Invalid", "asbasdfasdfa",
            getErrorStatus("No enum const [GFE / CFE].[asbasdfasdfa]")));
      data.add(new TestData("Test 4: Valid", "CFE", Status.OK_STATUS));
      data.add(new TestData("Test 5: Valid", "GFE", Status.OK_STATUS));
      data.add(new TestData("Test 5: Valid", "Unspecified", Status.OK_STATUS));
      data.add(new TestData("Test 6: Valid", "cfe", getErrorStatus("No enum const [GFE / CFE].[cfe]")));
      data.add(new TestData("Test 7: Invalid Class", 0,
            getErrorStatus("java.lang.Integer cannot be cast to java.lang.String")));
      return data;
   }

   private IStatus getErrorStatus(String message) {
      return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
   }

   @org.junit.Test
   public void testEnumerationData() throws OseeCoreException {
      for (TestData data : getEnumerationCases()) {
         IStatus actual =
               OseeValidator.getInstance().validate(IOseeValidator.SHORT, mockArtifact, enumeratedAttributeType,
                     data.getValue());
         checkStatus(data.getMessage(), data.getExpected(), actual);
      }
   }

   private void checkStatus(String message, IStatus expected, IStatus actual) {
      assertEquals(message, expected.getSeverity(), actual.getSeverity());
      assertEquals(message, expected.getMessage(), actual.getMessage());
   }

   public static class TestData {
      private final String message;
      private final IStatus expected;
      private final Object value;

      public TestData(String message, Object value, IStatus expected) {
         this.message = message;
         this.value = value;
         this.expected = expected;
      }

      public String getMessage() {
         return message;
      }

      public Object getValue() {
         return value;
      }

      public IStatus getExpected() {
         return expected;
      }
   }

}

/*
 * Created on Jun 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test2.cases;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumerationValidationTest extends TestCase {

   private Artifact mockArtifact;
   private AttributeType enumeratedAttributeType;

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception {
      super.setUp();
      Branch branch = BranchManager.getCommonBranch();
      // Create an artifact having an enumerated attribute

      enumeratedAttributeType = AttributeTypeManager.getType("GFE / CFE");
      mockArtifact = ArtifactTypeManager.addArtifact("Component", branch);
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#tearDown()
    */
   @Override
   protected void tearDown() throws Exception {
      super.tearDown();
      mockArtifact.delete();
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
      return new Status(IStatus.ERROR, SkynetActivator.PLUGIN_ID, message);
   }

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
      private String message;
      private IStatus expected;
      private Object value;

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

/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class OseeEnumerationValidationTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private Artifact mockArtifact;
   private final String message;
   private final IStatus expected;
   private final Object value;

   public OseeEnumerationValidationTest(String message, Object value, IStatus expected) {
      this.message = message;
      this.value = value;
      this.expected = expected;
   }

   @Before
   public void setUp() throws Exception {
      mockArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Component, CoreBranches.COMMON);
   }

   @After
   public void tearDown() throws Exception {
      mockArtifact.deleteAndPersist();
      mockArtifact = null;
   }

   @Test
   public void testEnumerationData() {
      IStatus actual =
         OseeValidator.getInstance().validate(IOseeValidator.SHORT, mockArtifact, CoreAttributeTypes.GfeCfe, value);
      assertEquals(message, expected.getSeverity(), actual.getSeverity());
      assertEquals(message, expected.getMessage(), actual.getMessage());
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<>();

      addTest(data, "Test 1: Null", null, errorStatus("No enum const [enum.req.gfe.cfe].[null]"));
      addTest(data, "Test 2: Empty String", "", errorStatus("No enum const [enum.req.gfe.cfe].[]"));
      addTest(data, "Test 3: Invalid", "asbasdfasdfa", errorStatus("No enum const [enum.req.gfe.cfe].[asbasdfasdfa]"));
      addTest(data, "Test 4: Valid", "CFE", Status.OK_STATUS);
      addTest(data, "Test 5: Valid", "GFE", Status.OK_STATUS);
      addTest(data, "Test 5: Valid", "Unspecified", Status.OK_STATUS);
      addTest(data, "Test 6: Valid", "cfe", errorStatus("No enum const [enum.req.gfe.cfe].[cfe]"));
      addTest(data, "Test 7: Invalid Class", 0, errorStatus("java.lang.Integer cannot be cast to java.lang.String"));

      return data;
   }

   private static void addTest(Collection<Object[]> data, String message, Object value, IStatus expected) {
      data.add(new Object[] {message, value, expected});
   }

   private static IStatus errorStatus(String message) {
      return new Status(IStatus.ERROR, OseeEnumerationValidationTest.class.getSimpleName(), message);
   }

}

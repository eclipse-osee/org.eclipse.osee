/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import static java.util.Arrays.asList;
import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.client.test.framework.TestInfo;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class ArtifactSetAttributeValuesTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   @Rule
   public TestInfo method = new TestInfo();

   private static final List<String> START_VALUE = asList("First", "Second", "Third");
   private static final List<String> ADD_ONE = asList("First", "Second", "Third", "Fourth");
   private static final List<String> ADD_ONE_REMOVE_ONE = asList("Second", "Third", "Fourth", "Fifth");
   private static final List<String> ADD_DUPLICATES = asList("Second", "Second", "Third", "Fourth", "Fifth", "Fourth");
   private static final List<String> ADD_DUPLICATES_EXPECTED = asList("Second", "Third", "Fifth", "Fourth");

   private Artifact artifact;

   @Before
   public void setup() throws Exception {
      artifact =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralDocument, SAW_Bld_1, method.getQualifiedTestName());
      artifact.setAttributeValues(CoreAttributeTypes.StaticId, START_VALUE);
   }

   @After
   public void tearDown() throws Exception {
      if (artifact != null) {
         artifact.purgeFromBranch();
      }
   }

   @Test
   public void testSetAttributeValues() throws Exception {
      List<String> actual = artifact.getAttributesToStringList(CoreAttributeTypes.StaticId);

      assertTrue(Collections.isEqual(START_VALUE, actual));
   }

   @Test
   public void testSetAttributeValuesAddOne() throws Exception {
      artifact.setAttributeValues(CoreAttributeTypes.StaticId, ADD_ONE);

      List<String> actual = artifact.getAttributesToStringList(CoreAttributeTypes.StaticId);

      assertTrue(Collections.isEqual(ADD_ONE, actual));
   }

   @Test
   public void testSetAttributeValuesAddOneRemoveOne() throws Exception {
      artifact.setAttributeValues(CoreAttributeTypes.StaticId, ADD_ONE_REMOVE_ONE);

      List<String> actual = artifact.getAttributesToStringList(CoreAttributeTypes.StaticId);

      assertTrue(Collections.isEqual(ADD_ONE_REMOVE_ONE, actual));
   }

   @Test
   public void testSetAttributeValuesRemoveAll() throws Exception {
      artifact.setAttributeValues(CoreAttributeTypes.StaticId, java.util.Collections.<String> emptyList());

      List<String> actual = artifact.getAttributesToStringList(CoreAttributeTypes.StaticId);

      assertTrue(actual.isEmpty());
   }

   @Test
   public void testSetAttributeValuesWithDuplicates() throws Exception {
      artifact.setAttributeValues(CoreAttributeTypes.StaticId, ADD_DUPLICATES);

      List<String> actual = artifact.getAttributesToStringList(CoreAttributeTypes.StaticId);

      assertTrue(Collections.isEqual(ADD_DUPLICATES_EXPECTED, actual));
   }

}

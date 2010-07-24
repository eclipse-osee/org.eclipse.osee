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
package org.eclipse.osee.framework.core.server.test.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.internal.BuildInfo;
import org.eclipse.osee.framework.core.server.internal.BuildTypeIdentifier;
import org.eclipse.osee.framework.core.server.test.mocks.MockBuildTypeDataProvider;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link BuildTypeIdentifier}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class BuildTypeIdentifierTest {
   private static final String NULL_XML = null;
   private static final String INVALID_XML = "Invalid<><>";
   private static final Collection<BuildInfo> EMPTY_INFOS = Collections.emptyList();
   private static final String XML_DATA1 =
      "<builds><build type=\"BUILD1\"><matches>version1</matches></build></builds>";
   private static final Collection<BuildInfo> BUILD_DATA1 = Collections.singleton(new BuildInfo("BUILD1", "version1"));

   private static final String XML_DATA2 =
      "<builds><build type=\"BUILD1\"><matches>version1.*</matches></build></builds>";
   private static final Collection<BuildInfo> BUILD_DATA2 =
      Collections.singleton(new BuildInfo("BUILD1", "version1.*"));

   private static final String XML_DATA3 =
      "<builds><build type=\"BUILD1\"><matches>ver[a|B].*?456</matches></build></builds>";
   private static final Collection<BuildInfo> BUILD_DATA3 = Collections.singleton(new BuildInfo("BUILD1",
      "ver[a|B].*?456"));

   private static final String XML_DATA4 =
      "<builds><build type=\"BUILD1\"><matches>0.9.1.*</matches></build><build type=\"BUILD2\"><matches>0.9.2.*</matches><matches>0.9.3.*</matches></build></builds>";
   private static final Collection<BuildInfo> BUILD_DATA4 = Arrays.asList(new BuildInfo("BUILD1", "0.9.1.*"),
      new BuildInfo("BUILD2", "0.9.2.*", "0.9.3.*"));

   private static final String XML_DATA5 =
      "<builds><build type=\"Release Candidate\"><matches>0.9.2.*</matches></build><build type=\"Release\"><matches>0.9.*</matches></build><build type=\"Hacking\"><matches>.*</matches></build></builds>";
   private static final Collection<BuildInfo> BUILD_DATA5 = Arrays.asList(
      new BuildInfo("Release Candidate", "0.9.2.*"), new BuildInfo("Release", "0.9.*"), new BuildInfo("Hacking", ".*"));

   private final BuildTypeIdentifier buildIdentifier;
   private final String clientVersion;
   private final String expectedMatch;
   private final String description;
   private final boolean isError;
   private final String xmlData;
   private final Collection<BuildInfo> expectedInfos;

   public BuildTypeIdentifierTest(String description, String clientVersion, String expectedMatch, String xmlData, boolean isError, Collection<BuildInfo> expectedInfos) {
      this.isError = isError;
      this.xmlData = xmlData;
      this.buildIdentifier = new BuildTypeIdentifier(new MockBuildTypeDataProvider(xmlData, isError));
      this.description = description;
      this.clientVersion = clientVersion;
      this.expectedMatch = expectedMatch;
      this.expectedInfos = expectedInfos;
   }

   @Test
   public void testBuildType() {
      Collection<BuildInfo> actualInfos = null;
      try {
         actualInfos = buildIdentifier.getBuildTypeEntries();
         if (isExceptionExpected()) {
            Assert.fail("This line should not executed during an xml data access exception");
         }
      } catch (OseeCoreException ex1) {
         Assert.assertTrue("Threw exception when not expected. \n" + Lib.exceptionToString(ex1), isExceptionExpected());
      }
      Assert.assertFalse(Compare.isDifferent(expectedInfos, actualInfos));
   }

   @Test
   public void testBuildDesignation() {
      String actualMatch = buildIdentifier.getBuildDesignation(clientVersion);
      Assert.assertEquals(description, expectedMatch, actualMatch);
   }

   private boolean isExceptionExpected() {
      return isError || INVALID_XML.equals(xmlData);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();

      data.add(new Object[] {"null version && null xml data", null, "N/A", NULL_XML, false, EMPTY_INFOS});
      data.add(new Object[] {"valid versions && xml DataAccessException", "version1", "N/A", NULL_XML, true, null});
      data.add(new Object[] {"valid version && invalid xml data", "version1", "N/A", INVALID_XML, false, null});
      data.add(new Object[] {"exact match", "version1", "BUILD1", XML_DATA1, false, BUILD_DATA1});
      data.add(new Object[] {"regex match", "version12 !@#$da", "BUILD1", XML_DATA2, false, BUILD_DATA2});
      data.add(new Object[] {"complex regex match1", "verB!@#$_456", "BUILD1", XML_DATA3, false, BUILD_DATA3});
      data.add(new Object[] {"complex regex match2", "0.9.3.123132131", "BUILD2", XML_DATA4, false, BUILD_DATA4});
      data.add(new Object[] {"complex regex match3", "Development", "Hacking", XML_DATA5, false, BUILD_DATA5});
      return data;
   }

}

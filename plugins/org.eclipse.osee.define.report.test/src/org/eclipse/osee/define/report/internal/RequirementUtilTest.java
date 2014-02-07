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
package org.eclipse.osee.define.report.internal;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.define.report.internal.util.RequirementUtil;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author Megumi Telles
 */
public class RequirementUtilTest {

   // @formatter:off
   @Mock ArtifactReadable readable;
   @Mock ArtifactReadable srs;
   // @formatter:on

   private final List<String> qualMethods = Arrays.asList("Demonstration", "Inspection");

   @Before
   public void initialize() throws Exception {
      MockitoAnnotations.initMocks(this);

      when(readable.isOfType(CoreArtifactTypes.AbstractSoftwareRequirement)).thenReturn(true);
      when(readable.getAttributeValues(CoreAttributeTypes.QualificationMethod)).thenAnswer(answer(qualMethods));
   }

   @Test(expected = OseeCoreException.class)
   public void testNullGetHigherLevelTrace() {
      RequirementUtil.getHigherLevelTrace(null);
   }

   @Test(expected = OseeCoreException.class)
   public void testNullGetSupportingInfo() {
      RequirementUtil.getSupportingInfo(null);
   }

   @Test
   public void testExtractAbbrevSubsystem() {
      String heading1 = "ThisIs.MySubsystem:MyHeading";
      String actualHeading = RequirementUtil.extractAbbrevSubsystem(heading1);
      Assert.assertEquals("MySubsystem", actualHeading);

      String heading2 = "ThisIs.ASubsystem";
      actualHeading = RequirementUtil.extractAbbrevSubsystem(heading2);
      Assert.assertEquals("", actualHeading);

      String heading3 = "";
      actualHeading = RequirementUtil.extractAbbrevSubsystem(heading3);
      Assert.assertEquals("", actualHeading);

   }

   @Test(expected = OseeCoreException.class)
   public void testNullExtractAbbrevSubsystem() {
      RequirementUtil.extractAbbrevSubsystem(null);
   }

   @Test
   public void testGetAbbrevQualificationMethod() {
      Iterable<ArtifactReadable> readables = Arrays.asList(readable);
      String qualificationMethod = RequirementUtil.getAbbrevQualificationMethod(ResultSets.newResultSet(readables));
      Assert.assertEquals("/D/I", qualificationMethod);
   }

   @Test
   public void testEmptyGetAbbrevQualificationMethod() {
      Iterable<ArtifactReadable> readables = new ArrayList<ArtifactReadable>();
      String qualificationMethod = RequirementUtil.getAbbrevQualificationMethod(ResultSets.newResultSet(readables));
      Assert.assertEquals("", qualificationMethod);
   }

   @Test(expected = OseeCoreException.class)
   public void testGetNullAbbrevQualificationMethod() {
      RequirementUtil.getAbbrevQualificationMethod(null);
   }

   private static <T> Answer<T> answer(final T value) {
      return new Answer<T>() {

         @Override
         public T answer(InvocationOnMock invocation) throws Throwable {
            return value;
         }
      };
   }

}

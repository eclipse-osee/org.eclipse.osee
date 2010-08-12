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
package org.eclipse.osee.framework.core.dsl.integration.test.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.dsl.integration.util.OseeDslSegmentParser;
import org.eclipse.osee.framework.core.dsl.integration.util.OseeDslSegmentParser.OseeDslSegment;
import org.eclipse.osee.framework.core.dsl.integration.util.OseeDslSegmentParser.TagLocation;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.test.mocks.MockArtifact;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link OseeDslSegmentParser}
 * 
 * @author Roberto E. Escobar
 */
public class OseeDslSegmentParserTest {

   private static OseeDslSegmentParser parser;

   @BeforeClass
   public static void setUp() {
      parser = new OseeDslSegmentParser();
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetStartTagNullCheck1() throws OseeCoreException {
      parser.getStartTag(null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetStartTagNullCheck2() throws OseeCoreException {
      IBasicArtifact<?> artifact = new DefaultBasicArtifact(45, "abc", "name");
      parser.getStartTag(artifact);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetEndTagNullCheck1() throws OseeCoreException {
      parser.getEndTag(null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetEndTagNullCheck2() throws OseeCoreException {
      IBasicArtifact<?> artifact = new DefaultBasicArtifact(45, "abc", "name");
      parser.getEndTag(artifact);
   }

   @Test
   public void testGetStartEndTag() throws OseeCoreException {
      IOseeBranch branch = CoreBranches.COMMON;
      final String artifactGuid = GUID.create();
      final String artifactName = "artifactTest";
      final String branchName = branch.getName();
      final String branchGuid = branch.getGuid();

      IBasicArtifact<?> artifact = new MockArtifact(artifactGuid, artifactName, branch, CoreArtifactTypes.Artifact, 45);

      String actual = parser.getStartTag(artifact);
      String expected =
         String.format("//@start_artifact branch/%s/artifact/%s/ (%s:%s)", branchGuid, artifactGuid, branchName,
            artifactName);
      Assert.assertEquals(expected, actual);

      actual = parser.getEndTag(artifact);
      expected =
         String.format("//@end_artifact branch/%s/artifact/%s/ (%s:%s)", branchGuid, artifactGuid, branchName,
            artifactName);
      Assert.assertEquals(expected, actual);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetTagLocationsNull() throws OseeCoreException {
      parser.getTagLocations(null);
   }

   @Test
   public void testGetLocations() throws OseeCoreException {
      final String artifactGuid = GUID.create();
      final String branchGuid = CoreBranches.COMMON.getGuid();
      String data = null;

      data = String.format("//@start_artifact branch/%s/artifact/%s/ ()", branchGuid, artifactGuid);
      checkGetTagLocations(data, new TagLocation(true, branchGuid, artifactGuid, 0, data.length()));

      data = String.format("//@end_artifact branch/%s/artifact/%s/ ()", branchGuid, artifactGuid);
      checkGetTagLocations(data, new TagLocation(false, branchGuid, artifactGuid, 0, data.length()));

      data = String.format("//@end_artifact branch/%s/artifact/%s/", branchGuid, artifactGuid);
      checkGetTagLocations(data);

      data = "//@end_artifact branch//artifact/";
      checkGetTagLocations(data);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetSegmentsNull() throws OseeCoreException {
      parser.getSegments((String) null);
   }

   @Test(expected = OseeArgumentException.class)
   public void testGetSegmentsNull2() throws OseeCoreException {
      parser.getSegments((Collection<TagLocation>) null);
   }

   @Test(expected = OseeStateException.class)
   public void testGetSegmentsUnMatched() throws OseeCoreException {
      TagLocation start = new TagLocation(true, "1", "2", 0, 2);
      TagLocation end = new TagLocation(false, "1", "2", 0, 2);

      Collection<TagLocation> locations = Arrays.asList(start, start, end);
      parser.getSegments(locations);
   }

   @Test
   public void testOrderedSegments() throws OseeCoreException {
      TagLocation start1 = new TagLocation(true, "branch_1", "art_1", 0, 6);
      TagLocation end1 = new TagLocation(false, "branch_1", "art_1", 10, 12);

      TagLocation start2 = new TagLocation(true, "branch_2", "art_2", 13, 56);
      TagLocation end2 = new TagLocation(false, "branch_2", "art_2", 79, 80);

      // Segments should capture in-between data

      OseeDslSegment segment1 = new OseeDslSegment("branch_1", "art_1", 6, 10);
      OseeDslSegment segment2 = new OseeDslSegment("branch_2", "art_2", 56, 79);

      Collection<TagLocation> locations = Arrays.asList(start1, end1, start2, end2);
      Collection<OseeDslSegment> segments = parser.getSegments(locations);
      assertSegments(segments, segment1, segment2);
   }

   // No Support for nesting?
   //   @Test
   //   public void testNestedSegments() throws OseeCoreException {
   //      TagLocation start1 = new TagLocation(true, "branch_1", "art_1", 0, 6);
   //      TagLocation end1 = new TagLocation(false, "branch_1", "art_1", 10, 12);
   //
   //      TagLocation start2 = new TagLocation(true, "branch_2", "art_2", 13, 56);
   //      TagLocation end2 = new TagLocation(false, "branch_2", "art_2", 79, 80);
   //
   //      // Segments should capture in-between data
   //      OseeDslSegment segment1 = new OseeDslSegment("branch_1", "art_1", 6, 10);
   //      OseeDslSegment segment2 = new OseeDslSegment("branch_2", "art_2", 56, 79);
   //
   //      Collection<TagLocation> locations = Arrays.asList(start1, start2, end2, end1);
   //      Collection<OseeDslSegment> segments = parser.getSegments(locations);
   //      assertSegments(segments, segment2, segment1);
   //   }

   private void assertSegments(Collection<OseeDslSegment> segments, OseeDslSegment... expectedSegments) {
      Assert.assertEquals("number of expected segments don't match", expectedSegments.length, segments.size());

      Iterator<OseeDslSegment> iterator = segments.iterator();
      for (int index = 0; index < expectedSegments.length; index++) {
         OseeDslSegment actual = iterator.next();
         OseeDslSegment expected = expectedSegments[index];
         assertEquals(index, expected, actual);
      }
   }

   private void checkGetTagLocations(String source, TagLocation... expectedLocations) throws OseeCoreException {
      Collection<TagLocation> segments = parser.getTagLocations(source);
      Assert.assertEquals("number of expected tagLocations don't match", expectedLocations.length, segments.size());

      Iterator<TagLocation> iterator = segments.iterator();
      for (int index = 0; index < expectedLocations.length; index++) {
         TagLocation actual = iterator.next();
         TagLocation expected = expectedLocations[index];
         assertEquals(index, expected, actual);
      }
   }

   private static final void assertEquals(int index, TagLocation expected, TagLocation actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals("isStartTag - " + index, expected.isStartTag(), actual.isStartTag());
         Assert.assertEquals("branchguid  - " + index, expected.getBranchGuid(), actual.getBranchGuid());
         Assert.assertEquals("artguid - " + index, expected.getArtifactGuid(), actual.getArtifactGuid());
         Assert.assertEquals("start - " + index, expected.start(), actual.start());
         Assert.assertEquals("end - " + index, expected.end(), actual.end());
      }
   }

   private static final void assertEquals(int index, OseeDslSegment expected, OseeDslSegment actual) {
      if (expected == null) {
         Assert.assertNull(actual);
      } else {
         Assert.assertEquals("branchguid  - " + index, expected.getBranchGuid(), actual.getBranchGuid());
         Assert.assertEquals("artguid - " + index, expected.getArtifactGuid(), actual.getArtifactGuid());
         Assert.assertEquals("start - " + index, expected.start(), actual.start());
         Assert.assertEquals("end - " + index, expected.end(), actual.end());
      }
   }
}

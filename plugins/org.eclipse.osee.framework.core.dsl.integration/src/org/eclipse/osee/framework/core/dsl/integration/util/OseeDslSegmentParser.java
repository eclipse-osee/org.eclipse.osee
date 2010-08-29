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
package org.eclipse.osee.framework.core.dsl.integration.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class OseeDslSegmentParser {

   public String getStartTag(IBasicArtifact<?> artifact) throws OseeCoreException {
      return getSegmentTag("start", artifact);
   }

   public String getEndTag(IBasicArtifact<?> artifact) throws OseeCoreException {
      return getSegmentTag("end", artifact);
   }

   private static String getSegmentTag(String tagPrefix, IBasicArtifact<?> artifact) throws OseeCoreException {
      Conditions.checkNotNull(artifact, "artifact");
      IOseeBranch branch = artifact.getBranch();
      Conditions.checkNotNull(branch, "branch");
      return String.format("//@%s_artifact branch/%s/artifact/%s/ (%s:%s)", tagPrefix, branch.getGuid(),
         artifact.getGuid(), branch.getName(), artifact.getName());
   }

   public Collection<OseeDslSegment> getSegments(String source) throws OseeCoreException {
      return getSegments(getTagLocations(source));
   }

   public Collection<OseeDslSegment> getSegments(Collection<TagLocation> tagLocations) throws OseeCoreException {
      Conditions.checkNotNull(tagLocations, "tagLocations");
      Collection<OseeDslSegment> segments = new ArrayList<OseeDslSegment>();
      Stack<TagLocation> segmentStack = new Stack<TagLocation>();
      for (TagLocation segment : tagLocations) {
         if (segment.isStartTag()) {
            segmentStack.push(segment);
         } else {
            if (matches(segmentStack.peek(), segment)) {
               TagLocation startSeg = segmentStack.pop();
               processData(segments, startSeg, segment);
            }
         }
      }
      if (!segmentStack.isEmpty()) {
         throw new OseeStateException("Found unmatched segments");
      }
      return segments;
   }

   private void processData(Collection<OseeDslSegment> segments, TagLocation startSeg, TagLocation stopSeg) {
      String branchGuid = startSeg.getBranchGuid();
      String artifactGuid = startSeg.getArtifactGuid();
      int startAt = startSeg.end();
      int endAt = stopSeg.start();
      segments.add(new OseeDslSegment(branchGuid, artifactGuid, startAt, endAt));
   }

   private boolean matches(TagLocation seg1, TagLocation seg2) {
      return seg1.getBranchGuid().equals(seg2.getBranchGuid()) && seg1.getArtifactGuid().equals(seg2.getArtifactGuid());
   }

   public Collection<TagLocation> getTagLocations(String source) throws OseeCoreException {
      Conditions.checkNotNull(source, "artifact source data");
      Collection<TagLocation> segments = new ArrayList<TagLocation>();
      Pattern pattern = Pattern.compile("\\s?//@(.*?)_artifact\\s+branch/(.*?)/artifact/(.*?)/\\s+\\(.*?\\)");
      Matcher matcher = pattern.matcher(source);

      String branchGuid = null;
      String artifactGuid = null;
      String tag = null;
      int tagStart = -1;
      int tagEnd = -1;
      while (matcher.find()) {
         tagStart = matcher.start();
         tagEnd = matcher.end();

         tag = matcher.group(1);
         branchGuid = matcher.group(2);
         artifactGuid = matcher.group(3);
         if (Strings.isValid(tag) && Strings.isValid(branchGuid) && Strings.isValid(artifactGuid)) {
            boolean isStartTag = tag.equalsIgnoreCase("start");
            segments.add(new TagLocation(isStartTag, branchGuid, artifactGuid, tagStart, tagEnd));
         }
      }
      return segments;
   }

   public static final class TagLocation {

      private final boolean isStartTag;
      private final String branchGuid;
      private final String artifactGuid;
      private final int start;
      private final int end;

      public TagLocation(boolean isStartTag, String branchGuid, String artifactGuid, int start, int end) {
         super();
         this.isStartTag = isStartTag;
         this.branchGuid = branchGuid;
         this.artifactGuid = artifactGuid;
         this.start = start;
         this.end = end;
      }

      public boolean isStartTag() {
         return isStartTag;
      }

      public String getBranchGuid() {
         return branchGuid;
      }

      public String getArtifactGuid() {
         return artifactGuid;
      }

      public int start() {
         return start;
      }

      public int end() {
         return end;
      }

      @Override
      public String toString() {
         return "OseeDslSegment [isStartTag=" + isStartTag + ", branchGuid=" + branchGuid + ", artifactGuid=" + artifactGuid + ", start=" + start + ", end=" + end + "]";
      }

   }

   public static final class OseeDslSegment {

      private final String branchGuid;
      private final String artifactGuid;
      private final int start;
      private final int end;

      public OseeDslSegment(String branchGuid, String artifactGuid, int start, int end) {
         super();
         this.branchGuid = branchGuid;
         this.artifactGuid = artifactGuid;
         this.start = start;
         this.end = end;
      }

      public String getBranchGuid() {
         return branchGuid;
      }

      public String getArtifactGuid() {
         return artifactGuid;
      }

      public int start() {
         return start;
      }

      public int end() {
         return end;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + (artifactGuid == null ? 0 : artifactGuid.hashCode());
         result = prime * result + (branchGuid == null ? 0 : branchGuid.hashCode());
         result = prime * result + end;
         result = prime * result + start;
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         OseeDslSegment other = (OseeDslSegment) obj;
         if (artifactGuid == null) {
            if (other.artifactGuid != null) {
               return false;
            }
         } else if (!artifactGuid.equals(other.artifactGuid)) {
            return false;
         }
         if (branchGuid == null) {
            if (other.branchGuid != null) {
               return false;
            }
         } else if (!branchGuid.equals(other.branchGuid)) {
            return false;
         }
         if (end != other.end) {
            return false;
         }
         if (start != other.start) {
            return false;
         }
         return true;
      }

      @Override
      public String toString() {
         return "OseeDslSegment [branchGuid=" + branchGuid + ", artifactGuid=" + artifactGuid + ", start=" + start + ", end=" + end + "]";
      }

   }
}

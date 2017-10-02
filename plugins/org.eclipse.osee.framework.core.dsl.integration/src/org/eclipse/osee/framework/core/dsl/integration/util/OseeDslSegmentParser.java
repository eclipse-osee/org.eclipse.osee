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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class OseeDslSegmentParser {

   public String getStartTag(ArtifactToken artifact, IOseeBranch branch) {
      return getSegmentTag("start", artifact, branch);
   }

   public String getEndTag(ArtifactToken artifact, IOseeBranch branch) {
      return getSegmentTag("end", artifact, branch);
   }

   private static String getSegmentTag(String tagPrefix, ArtifactToken artifact, IOseeBranch branch) {
      Conditions.checkNotNull(artifact, "artifact");
      Conditions.checkNotNull(branch, "branch");
      return String.format("//@%s_artifact branch/%s/artifact/%s/ (%s:%s)", tagPrefix, branch.getId(),
         artifact.getGuid(), branch.getName(), artifact.getName());
   }

   public Collection<OseeDslSegment> getSegments(String source) {
      return getSegments(getTagLocations(source));
   }

   public Collection<OseeDslSegment> getSegments(Collection<TagLocation> tagLocations) {
      Conditions.checkNotNull(tagLocations, "tagLocations");
      Collection<OseeDslSegment> segments = new ArrayList<>();
      Stack<TagLocation> segmentStack = new Stack<>();
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
      Long branchUuid = startSeg.getBranchUuid();
      String artifactGuid = startSeg.getArtifactGuid();
      int startAt = startSeg.end();
      int endAt = stopSeg.start();
      segments.add(new OseeDslSegment(branchUuid, artifactGuid, startAt, endAt));
   }

   private boolean matches(TagLocation seg1, TagLocation seg2) {
      return seg1.getBranchUuid() == seg2.getBranchUuid() && seg1.getArtifactGuid().equals(seg2.getArtifactGuid());
   }

   public Collection<TagLocation> getTagLocations(String source) {
      Conditions.checkNotNull(source, "artifact source data");
      Collection<TagLocation> segments = new ArrayList<>();
      Pattern pattern = Pattern.compile("\\s?//@(.*?)_artifact\\s+branch/(.*?)/artifact/(.*?)/\\s+\\(.*?\\)");
      Matcher matcher = pattern.matcher(source);

      Long branchUuid = null;
      String artifactGuid = null;
      String tag = null;
      int tagStart = -1;
      int tagEnd = -1;
      while (matcher.find()) {
         tagStart = matcher.start();
         tagEnd = matcher.end();

         tag = matcher.group(1);
         branchUuid = Long.valueOf(matcher.group(2));
         artifactGuid = matcher.group(3);
         if (Strings.isValid(tag) && branchUuid > 0 && Strings.isValid(artifactGuid)) {
            boolean isStartTag = tag.equalsIgnoreCase("start");
            segments.add(new TagLocation(isStartTag, branchUuid, artifactGuid, tagStart, tagEnd));
         }
      }
      return segments;
   }

   public static final class TagLocation {

      private final boolean isStartTag;
      private final Long branchUuid;
      private final String artifactGuid;
      private final int start;
      private final int end;

      public TagLocation(boolean isStartTag, Long branchUuid, String artifactGuid, int start, int end) {
         super();
         this.isStartTag = isStartTag;
         this.branchUuid = branchUuid;
         this.artifactGuid = artifactGuid;
         this.start = start;
         this.end = end;
      }

      public boolean isStartTag() {
         return isStartTag;
      }

      public long getBranchUuid() {
         return branchUuid;
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
         return "OseeDslSegment [isStartTag=" + isStartTag + ", branchUuid=" + branchUuid + ", artifactGuid=" + artifactGuid + ", start=" + start + ", end=" + end + "]";
      }

   }

   public static final class OseeDslSegment {

      private final Long branchId;
      private final String artifactGuid;
      private final int start;
      private final int end;

      public OseeDslSegment(Long branchId, String artifactGuid, int start, int end) {
         super();
         this.branchId = branchId;
         this.artifactGuid = artifactGuid;
         this.start = start;
         this.end = end;
      }

      public Long getBranchId() {
         return branchId;
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
         result = prime * result + (branchId == null ? 0 : branchId.hashCode());
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
         if (branchId == null) {
            if (other.branchId != null) {
               return false;
            }
         } else if (!branchId.equals(other.branchId)) {
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
         return "OseeDslSegment [branchId=" + branchId + ", artifactId=" + artifactGuid + ", start=" + start + ", end=" + end + "]";
      }

   }
}

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

package org.eclipse.osee.framework.skynet.core.relation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Roberto E. Escobar
 */
public class RelationFilterUtil {

   public static interface RelationMatcher {
      public boolean matches(RelationLink relationLink);

      public boolean isFindNextAllowed();
   }

   public static final RelationMatcher EXCLUDE_DELETED = new ExcludeDeletedRelationFilter();
   public static final RelationMatcher INCLUDE_ALL = new DefaultRelationFilter();

   public static RelationMatcher createFindFirstRelatedArtIdMatcher(ArtifactId aArtifactId, RelationSide side) {
      return new FirstSideRelatedArtIdMatcher(aArtifactId, side.isSideA());
   }

   public static RelationMatcher createFindFirstRelationLinkIdMatcher(RelationId relLinkId) {
      return new FirstRelationLinkIdMatcher(relLinkId);
   }

   public static RelationMatcher createMatcher(DeletionFlag deletionFlag, RelationMatcher... matchers) {
      RelationMatcher toReturn;
      if (matchers.length > 0) {
         CompositeMatcher compositeMatcher = new CompositeMatcher();
         if (!deletionFlag.areDeletedAllowed()) {
            compositeMatcher.add(EXCLUDE_DELETED);
         }
         compositeMatcher.addAll(matchers);

         toReturn = compositeMatcher;
      } else if (!deletionFlag.areDeletedAllowed()) {
         toReturn = EXCLUDE_DELETED;
      } else {
         toReturn = INCLUDE_ALL;
      }
      return toReturn;
   }

   public static void filter(Collection<RelationLink> source, Collection<RelationLink> destination, RelationMatcher matcher) {
      if (source != null) {
         if (matcher != null) {
            for (RelationLink link : source) {
               if (matcher.matches(link)) {
                  destination.add(link);
                  if (!matcher.isFindNextAllowed()) {
                     break;
                  }
               }
            }
         } else {
            destination.addAll(source);
         }
      }
   }
   private static final class FirstRelationLinkIdMatcher implements RelationMatcher {

      private final RelationId relLinkId;

      public FirstRelationLinkIdMatcher(RelationId relLinkId) {
         this.relLinkId = relLinkId;
      }

      @Override
      public boolean matches(RelationLink relationLink) {
         return relLinkId.equals(relationLink.getId());
      }

      @Override
      public boolean isFindNextAllowed() {
         return false;
      }
   };

   private static final class FirstSideRelatedArtIdMatcher implements RelationMatcher {

      private final ArtifactId artifactId;
      private final boolean sideA;

      public FirstSideRelatedArtIdMatcher(ArtifactId artifactId, boolean sideA) {
         this.artifactId = artifactId;
         this.sideA = sideA;
      }

      @Override
      public boolean matches(RelationLink relationLink) {
         return artifactId.equals(sideA ? relationLink.getArtifactIdA() : relationLink.getArtifactIdB());
      }

      @Override
      public boolean isFindNextAllowed() {
         return false;
      }
   };

   private static final class CompositeMatcher implements RelationMatcher {

      private final List<RelationMatcher> matchers = new ArrayList<>();

      public void add(RelationMatcher matcher) {
         matchers.add(matcher);
      }

      public void addAll(RelationMatcher[] matchers2) {
         for (RelationMatcher matcher : matchers2) {
            add(matcher);
         }
      }

      @Override
      public boolean matches(RelationLink relationLink) {
         boolean result = true;
         for (RelationMatcher matcher : matchers) {
            result = result && matcher.matches(relationLink);
         }
         return result;
      }

      @Override
      public boolean isFindNextAllowed() {
         boolean result = true;
         for (RelationMatcher matcher : matchers) {
            result = result && matcher.isFindNextAllowed();
         }
         return result;
      }
   }

   private static final class ExcludeDeletedRelationFilter implements RelationMatcher {
      @Override
      public boolean matches(RelationLink relationLink) {
         return !relationLink.isDeleted();
      }

      @Override
      public boolean isFindNextAllowed() {
         return true;
      }
   }

   private static final class DefaultRelationFilter implements RelationMatcher {
      @Override
      public boolean matches(RelationLink relationLink) {
         return true;
      }

      @Override
      public boolean isFindNextAllowed() {
         return true;
      }
   }
}

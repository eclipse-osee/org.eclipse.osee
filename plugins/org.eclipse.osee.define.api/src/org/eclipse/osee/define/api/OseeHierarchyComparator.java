/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 * @author Branden W. Phillips
 */
public final class OseeHierarchyComparator implements Comparator<ArtifactReadable> {

   private final Map<Long, List<ArtifactReadable>> parentToChildrenCache;
   private final ActivityLog activityLog;

   public Map<ArtifactReadable, String> errors;

   public OseeHierarchyComparator(ActivityLog activityLog) {
      this.activityLog = activityLog;
      this.parentToChildrenCache = new HashMap<>();
      this.errors = new HashMap<>();
   }

   @Override
   public int compare(ArtifactReadable art1, ArtifactReadable art2) {
      try {
         int toReturn = 0;
         String paragraph1 = getHierarchyPosition(art1);
         String paragraph2 = getHierarchyPosition(art2);
         int[] set1 = getParagraphIndices(paragraph1);
         int[] set2 = getParagraphIndices(paragraph2);
         int length1 = set1.length;
         int length2 = set2.length;

         int size = length1 < length2 ? length1 : length2;
         if (size == 0 && length1 != length2) {
            toReturn = length1 < length2 ? -1 : 1;
         } else {
            for (int index = 0; index < size; index++) {
               toReturn = Integer.compare(set1[index], set2[index]);
               if (toReturn != 0) {
                  break;
               }
            }
            if (toReturn == 0) {
               toReturn = length1 < length2 ? -1 : 1;
            }
         }
         return toReturn;
      } catch (Exception ex) {
         activityLog.createThrowableEntry(CoreActivityTypes.OSEE_ERROR, ex);
      }
      return 1;
   }

   private String getHierarchyPosition(ArtifactReadable art1) {
      ArtifactReadable artifactCursor = art1;
      StringBuilder builder = new StringBuilder(20);

      while (artifactCursor.notEqual(CoreArtifactTokens.DefaultHierarchyRoot)) {
         ArtifactReadable parent = null;
         try {
            parent = artifactCursor.getParent();
         } catch (OseeCoreException ex) {
            errors.put(art1, "Hierarchy Index Unavailable");
            break;
         }
         if (parent == null) {
            errors.put(art1, "Not Connected to Root");
            break;
         }
         builder.insert(0, getPosition(artifactCursor) + ".");
         artifactCursor = parent;
      }

      return builder.substring(0, builder.length() - 1);
   }

   private int getPosition(ArtifactReadable artifact) {
      ArtifactReadable parent = artifact.getParent();
      List<ArtifactReadable> children = parentToChildrenCache.get(parent.getId());
      if (children == null) {
         children = parent.getChildren();
         parentToChildrenCache.put(parent.getId(), children);
      }
      int index = 1 + children.indexOf(artifact);
      if (index > 0) {
         return index;
      }
      throw new OseeStateException("[%s] is expected to be a child of [%s]", artifact, parent);
   }

   private int[] getParagraphIndices(String paragraph) {
      int[] paragraphs;
      if (Strings.isValid(paragraph)) {
         String[] values = paragraph.split("\\.");
         paragraphs = new int[values.length];
         for (int index = 0; index < values.length; index++) {
            paragraphs[index] = Integer.parseInt(values[index].replace("-", ""));
         }
      } else {
         paragraphs = new int[0];
      }
      return paragraphs;
   }
}
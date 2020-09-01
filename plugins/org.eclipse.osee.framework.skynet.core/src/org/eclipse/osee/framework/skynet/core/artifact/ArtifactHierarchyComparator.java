/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 * @author Branden W. Phillips
 */
public final class ArtifactHierarchyComparator implements Comparator<Artifact> {

   private final Map<Long, List<Artifact>> parentToChildrenCache;

   public ArtifactHierarchyComparator() {
      this.parentToChildrenCache = new HashMap<>();
   }

   @Override
   public int compare(Artifact art1, Artifact art2) {
      try {
         int toReturn = 0;
         String paragraph1 = art1.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
         if (paragraph1.isEmpty()) {
            paragraph1 = getHierarchyPosition(art1);
         }
         String paragraph2 = art2.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
         if (paragraph2.isEmpty()) {
            paragraph2 = getHierarchyPosition(art2);
         }
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
         OseeLog.log(this.getClass(), Level.SEVERE, ex);
      }
      return 1;
   }

   public String getHierarchyPosition(Artifact art1) {
      Artifact artifactCursor = art1;
      StringBuilder builder = new StringBuilder(20);

      while (artifactCursor.notEqual(CoreArtifactTokens.DefaultHierarchyRoot)) {
         Artifact parent = null;
         try {
            parent = artifactCursor.getParent();
         } catch (OseeCoreException ex) {
            return "0";
         }
         if (parent == null) {
            return "0";
         }
         builder.insert(0, getPosition(artifactCursor) + ".");
         artifactCursor = parent;
      }

      return builder.substring(0, builder.length() - 1);
   }

   private int getPosition(Artifact artifact) {
      Artifact parent = artifact.getParent();
      List<Artifact> children = parentToChildrenCache.get(parent.getId());
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
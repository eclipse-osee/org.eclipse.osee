/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class HierarchyIndexColumn extends XViewerColumn implements IXViewerPreComputedColumn {
   private final Map<Long, List<Artifact>> parentToChildrenCache = new HashMap<>();
   public static HierarchyIndexColumn instance = new HierarchyIndexColumn();

   public static HierarchyIndexColumn getInstance() {
      return instance;
   }

   private HierarchyIndexColumn() {
      super("framework.hierarchy.index", "Hierarchy Index", 50, XViewerAlign.Left, false, SortDataType.Paragraph_Number,
         false, "Hierarchy Index");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public HierarchyIndexColumn copy() {
      HierarchyIndexColumn newXCol = new HierarchyIndexColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public Long getKey(Object obj) {
      Long id = -1L;
      Artifact art = getArtifactFromElement(obj);
      if (art != null) {
         id = art.getId();
      }
      return id;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      for (Object element : objects) {
         Artifact artifactCursor = getArtifactFromElement(element);
         if (artifactCursor != null) {
            Long artId = artifactCursor.getId();
            if (!preComputedValueMap.containsKey(artId)) {
               if (artifactCursor.isDeleted()) {
                  preComputedValueMap.put(artId, "deleted");
               } else {
                  StringBuilder builder = new StringBuilder(20);
                  String error = Strings.emptyString();

                  while (artifactCursor.notEqual(CoreArtifactTokens.DefaultHierarchyRoot)) {
                     Artifact parent = null;
                     try {
                        parent = artifactCursor.getParent();
                     } catch (OseeCoreException ex) {
                        error = "Hierarchy Index unavailable: " + ex.getLocalizedMessage();
                        break;
                     }
                     if (parent == null) {
                        error = "not connected to root";
                        break;
                     }
                     builder.insert(0, getPosition(artifactCursor) + ".");
                     artifactCursor = parent;
                  }
                  if (error.isEmpty()) {
                     if (builder.length() > 0) {
                        preComputedValueMap.put(artId, builder.substring(0, builder.length() - 1));
                     } else {
                        preComputedValueMap.put(artId, "no parent");
                     }
                  } else {
                     preComputedValueMap.put(artId, error);
                  }
               }
            }
         }
      }
      parentToChildrenCache.clear();
   }

   private Artifact getArtifactFromElement(Object element) {
      Artifact toReturn = null;
      if (element instanceof Artifact) {
         toReturn = (Artifact) element;
      } else if (element instanceof Change) {
         toReturn = ((Change) element).getChangeArtifact();
      }
      return toReturn;
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      return cachedValue;
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
}
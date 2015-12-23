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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.swt.SWT;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class HierarchyIndexColumn extends XViewerValueColumn {
   private final Set<Artifact> strongArtifactRefs = new HashSet<>();

   public static HierarchyIndexColumn instance = new HierarchyIndexColumn();

   public static HierarchyIndexColumn getInstance() {
      return instance;
   }

   private HierarchyIndexColumn() {
      super("framework.hierarchy.index", "Hierarchy Index", 50, SWT.LEFT, false, SortDataType.Paragraph_Number, false,
         "Hierarchy Index");
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
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      Artifact artifact = null;
      if (element instanceof Artifact) {
         artifact = (Artifact) element;
      } else if (element instanceof Change) {
         artifact = ((Change) element).getChangeArtifact();
      } else {
         return "unexpected type: " + element;
      }

      try {
         if (artifact.isDeleted()) {
            return "deleted";
         }
         return computeHierarchyIndex(artifact);
      } catch (OseeCoreException ex) {
         return ex.toString();
      }
   }

   private final Cache<Artifact, String> artToIndexStr =
      CacheBuilder.newBuilder().expireAfterAccess(2, TimeUnit.MINUTES).build();

   private String computeHierarchyIndex(final Artifact artifact) throws OseeCoreException {
      String indexStr = "";
      try {
         indexStr = artToIndexStr.get(artifact, new Callable<String>() {

            @Override
            public String call() throws Exception {
               StringBuilder builder = new StringBuilder(20);
               Artifact artifactCursor = artifact;
               Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(artifact.getBranch());

               while (!artifactCursor.equals(root)) {
                  Artifact parent = null;
                  try {
                     parent = artifactCursor.getParent();
                  } catch (OseeCoreException ex) {
                     return "Hierarchy Index unavailable: " + ex.getLocalizedMessage();
                  }
                  if (parent == null) {
                     return "not connected to root";
                  }
                  builder.insert(0, getPosition(artifactCursor) + ".");
                  artifactCursor = parent;
               }
               return builder.substring(0, builder.length() - 1);
            }
         });
      } catch (ExecutionException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return indexStr;
   }

   private int getPosition(Artifact artifact) throws OseeCoreException {
      Artifact parent = artifact.getParent();
      List<Artifact> children = parent.getChildren();
      strongArtifactRefs.addAll(children);

      for (int index = 0; index < children.size(); index++) {
         Artifact child = children.get(index);
         if (artifact.equals(child)) {
            return index + 1;
         }
      }
      throw new OseeStateException("[%s] is expected to be a child of [%s]", artifact, parent);
   }
}
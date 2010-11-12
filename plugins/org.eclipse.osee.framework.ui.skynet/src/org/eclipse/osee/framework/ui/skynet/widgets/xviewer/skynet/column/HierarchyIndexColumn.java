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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.swt.SWT;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class HierarchyIndexColumn extends XViewerValueColumn {
   private final Set<Artifact> strongArtifactRefs = new HashSet<Artifact>();

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
      copy(this, newXCol);
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

   private String computeHierarchyIndex(Artifact artifact) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      Artifact artifactCursor = artifact;
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(artifact.getBranch());

      while (!artifactCursor.equals(root)) {
         Artifact parent = artifactCursor.getParent();
         if (parent == null) {
            return "not connected to root";
         }
         builder.insert(0, getPosition(artifactCursor) + ".");
         artifactCursor = parent;
      }
      return builder.substring(0, builder.length() - 1);
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
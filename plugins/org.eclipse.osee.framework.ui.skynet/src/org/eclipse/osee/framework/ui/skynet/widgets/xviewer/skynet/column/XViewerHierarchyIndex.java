/*
 * Created on Sep 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.swt.SWT;

public class XViewerHierarchyIndex extends XViewerValueColumn {
   private final Set<Artifact> strongArtifactRefs = new HashSet<Artifact>();

   public XViewerHierarchyIndex(boolean show) {
      this("framework.hierarchy.index", "Hierarchy Index", 50, SWT.LEFT, show, SortDataType.Paragraph_Number, false,
         "Hierarchy Index");
   }

   public XViewerHierarchyIndex(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public XViewerHierarchyIndex copy() {
      return new XViewerHierarchyIndex(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      Artifact artifact = null;
      if (element instanceof Artifact) {
         artifact = (Artifact) element;
      } else if (element instanceof Change) {
         artifact = ((Change) element).getChangeArtifact();
      }

      try {
         return computeHierarchyIndex(artifact);
      } catch (OseeCoreException ex) {
         return "-1";
      }
   }

   private String computeHierarchyIndex(Artifact artifact) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      Artifact artifactCursor = artifact;
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(artifact.getBranch());

      while (!artifactCursor.equals(root)) {
         builder.insert(0, getPosition(artifactCursor) + ".");
         artifactCursor = artifactCursor.getParent();
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
      return 0;
   }
}
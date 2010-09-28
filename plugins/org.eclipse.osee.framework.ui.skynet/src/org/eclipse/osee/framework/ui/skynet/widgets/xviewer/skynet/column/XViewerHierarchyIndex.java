/*
 * Created on Sep 27, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.swt.SWT;

public class XViewerHierarchyIndex extends XViewerValueColumn {

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
      String toReturn;
      try {
         toReturn = computeHierarchyIndex(artifact);
      } catch (OseeCoreException ex) {
         toReturn = "-1";
      }
      return toReturn;
   }

   private String computeHierarchyIndex(Artifact artifact) throws OseeCoreException {
      int position = 0;
      StringBuilder builder = new StringBuilder();
      int depth = getHierarchyDepth(artifact);
      if (depth != -1) {
         position = getPosition(artifact);
      }
      builder.append(depth);
      if (position > 0) {
         builder.append(".");
         builder.append(position);
      }
      return builder.toString();
   }

   private int getHierarchyDepth(Artifact artifact) throws OseeCoreException {
      int depth = -1;
      if (artifact != null) {
         Artifact artifactPtr = artifact;
         while (artifactPtr != null) {
            artifactPtr = artifactPtr.getParent();
            depth++;
         }
      }
      return depth;
   }

   private int getPosition(Artifact artifact) throws OseeCoreException {
      if (artifact != null) {
         Artifact parent = artifact.getParent();
         if (parent != null) {
            List<Artifact> artifacts = parent.getChildren();
            for (int index = 0; index < artifacts.size(); index++) {
               Artifact child = artifacts.get(index);
               if (artifact.equals(child)) {
                  return index + 1;
               }
            }
         }
      }
      return 0;
   }
}
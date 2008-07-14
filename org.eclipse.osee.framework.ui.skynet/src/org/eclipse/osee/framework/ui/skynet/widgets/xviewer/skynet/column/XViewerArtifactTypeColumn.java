/*
 * Created on Jul 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerArtifactTypeColumn extends XViewerValueColumn {

   public XViewerArtifactTypeColumn(String name, XViewer viewer, int columnNum) {
      super(viewer, name == null ? "Artifact Type" : name, "", 75, 75, SWT.LEFT);
      setOrderNum(columnNum);
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column) {
      if (element instanceof Artifact) {
         return ((Artifact) element).getArtifactTypeName();
      }
      return "";
   }

}

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
package org.eclipse.osee.ats.util.widgets.task;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.world.AtsXColumn;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.ats.world.WorldXViewerSorter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;

/**
 * @author Donald G. Dunne
 */
public class TaskXViewerSorter extends WorldXViewerSorter {

   /**
    * @param xViewer
    */
   public TaskXViewerSorter(XViewer xViewer) {
      super(xViewer);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter#compare(org.eclipse.jface.viewers.Viewer,
    *      java.lang.Object, java.lang.Object, int)
    */
   @SuppressWarnings("unchecked")
   @Override
   public int compare(Viewer viewer, Object o1, Object o2, int sortXColIndex) {
      if (xViewer == null || !xViewer.getCustomize().getCurrentCustData().getSortingData().isSorting()) return 0;
      XViewerColumn sortXCol =
            xViewer.getCustomize().getCurrentCustData().getSortingData().getSortXCols().get(sortXColIndex);
      AtsXColumn aCol = AtsXColumn.getAtsXColumn(sortXCol);
      IWorldViewArtifact m1 = (IWorldViewArtifact) ((Artifact) o1);
      IWorldViewArtifact m2 = (IWorldViewArtifact) ((Artifact) o2);

      if (aCol == AtsXColumn.Assignees_Col) {
         int compareInt =
               getComparator().compare(
                     (new SMAManager((StateMachineArtifact) m1)).getAssigneesWasIsStr().replaceFirst("\\(", ""),
                     (new SMAManager((StateMachineArtifact) m2)).getAssigneesWasIsStr().replaceFirst("\\(", ""));
         return getCompareBasedOnDirection(sortXCol, compareInt, viewer, o1, o2, sortXColIndex);
      }

      return super.compare(viewer, o1, o2, sortXColIndex);
   }

}

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
package org.eclipse.osee.ats.world;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.ats.ActionDebug;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class WorldXViewerSorter extends XViewerSorter {

   ActionDebug debug = new ActionDebug(true, "WorldXViewerSorter");
   protected final XViewer xViewer;

   /**
    * @param xViewer
    */
   public WorldXViewerSorter(XViewer xViewer) {
      super(xViewer);
      this.xViewer = xViewer;
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
      try {
         if (xViewer == null || !xViewer.getCustomizeMgr().isSorting()) return 0;
         XViewerColumn sortXCol = xViewer.getCustomizeMgr().getSortXCols().get(sortXColIndex);
         IWorldViewArtifact m1 = (IWorldViewArtifact) ((Artifact) o1);
         IWorldViewArtifact m2 = (IWorldViewArtifact) ((Artifact) o2);

         if (sortXCol.equals(WorldXViewerFactory.Assignees_Col)) {
            int compareInt =
                  getComparator().compare(m1.getWorldViewActivePoc().replaceFirst("\\(", ""),
                        m2.getWorldViewActivePoc().replaceFirst("\\(", ""));
            return getCompareBasedOnDirection(sortXCol, compareInt, viewer, o1, o2, sortXColIndex);
         } else if (sortXCol.equals(WorldXViewerFactory.Change_Type_Col)) {
            int compareInt =
                  getComparator().compare(m1.getWorldViewChangeType().ordinal() + "",
                        m2.getWorldViewChangeType().ordinal() + "");
            return getCompareBasedOnDirection(sortXCol, compareInt, viewer, o1, o2, sortXColIndex);
         }

         return super.compare(viewer, o1, o2, sortXColIndex);
      } catch (Exception ex) {
         // do nothing
      }
      return 1;
   }

}

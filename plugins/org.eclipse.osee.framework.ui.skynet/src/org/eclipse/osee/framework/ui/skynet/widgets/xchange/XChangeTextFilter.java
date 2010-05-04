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
package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Donald G. Dunne
 */
public class XChangeTextFilter extends XViewerTextFilter {

   private final List<ArtifactChange> orderedChanges;
   private boolean showDocumentOrderFilter;
   private int lastSize;

   public XChangeTextFilter(ChangeXViewer changeXViewer) {
      super(changeXViewer);
      this.orderedChanges = new ArrayList<ArtifactChange>();
      lastSize = -1;
   }

   public boolean isShowDocumentOrderFilter() {
      return showDocumentOrderFilter;
   }

   public void setShowDocumentOrderFilter(boolean showDocumentOrderFilter) {
      this.showDocumentOrderFilter = showDocumentOrderFilter;
      lastSize = -1;
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      boolean accept = false;
      if (isShowDocumentOrderFilter()) {
         if (parentElement instanceof Collection<?>) {
            Collection<?> data = (Collection<?>) parentElement;
            if (data.size() == 1 && data.iterator().next() instanceof String) {
               accept = super.select(viewer, parentElement, element);
            } else {
               if (lastSize != data.size()) {
                  try {
                     computeDocOrder(data);
                     lastSize = data.size();
                  } catch (OseeCoreException ex) {
                     OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
                  }
               }
               accept = orderedChanges.contains(element);
            }
         }
      } else {
         accept = super.select(viewer, parentElement, element);
      }
      return accept;
   }

   private boolean selectInDocOrder(Viewer viewer, Object parentElement, Object element) {
      return false;
   }

   private void computeDocOrder(Collection<?> data) throws OseeCoreException {
      Map<Artifact, ArtifactChange> artChangeMap = new HashMap<Artifact, ArtifactChange>();
      for (Object object : data) {
         if (object instanceof ArtifactChange) {
            ArtifactChange artifactChanged = (ArtifactChange) object;
            Artifact artifact = artifactChanged.getChangeArtifact();
            if (artifact != null) {
               artChangeMap.put(artifact, artifactChanged);
            }
         }
      }
      orderedChanges.clear();
      DefaultHierarchySorter sorter = new DefaultHierarchySorter();
      List<Artifact> sortedArtifacts = sorter.sort(artChangeMap.keySet());
      for (Artifact artifact : sortedArtifacts) {
         orderedChanges.add(artChangeMap.get(artifact));
      }
   }
}

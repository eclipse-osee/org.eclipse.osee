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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Donald G. Dunne
 */
public class XChangeTextFilter extends XViewerTextFilter {

   private Map<Artifact, ArtifactChange> artifactToChangeMap;
   private ArrayList<ArtifactChange> docOrderedChanges;
   private boolean showDocumentOrderFilter = false;

   public XChangeTextFilter(ChangeXViewer changeXViewer) {
      super(changeXViewer);
      this.artifactToChangeMap = new HashMap<Artifact, ArtifactChange>();
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (isShowDocumentOrderFilter()) {
         try {
            computeDocOrderedChanges((Collection<Change>) parentElement);
            if (docOrderedChanges != null) {
               return docOrderedChanges.contains(element);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      return super.select(viewer, parentElement, element);
   }

   private void computeDocOrderedChanges(Collection<Change> changes) throws OseeCoreException {
      if (docOrderedChanges != null) return;
      Set<Artifact> artifacts = new HashSet<Artifact>();
      if (this.docOrderedChanges == null) {
         this.docOrderedChanges = new ArrayList<ArtifactChange>();
      }
      for (Object object : changes) {
         if (object instanceof ArtifactChange) {
            ArtifactChange artifactChanged = (ArtifactChange) object;
            Artifact artifact = artifactChanged.getToArtifact();
            if (artifact != null) {
               artifacts.add(artifact);
               artifactToChangeMap.put(artifact, artifactChanged);
            }
         }
      }

      DefaultHierSorter sorter = new DefaultHierSorter();

      for (Artifact artifact : sorter.sort(artifacts)) {
         docOrderedChanges.add(artifactToChangeMap.get(artifact));
      }
   }

   public boolean isShowDocumentOrderFilter() {
      return showDocumentOrderFilter;
   }

   public void setShowDocumentOrderFilter(boolean showDocumentOrderFilter) {
      this.showDocumentOrderFilter = showDocumentOrderFilter;
      // Reset cache so it re-computes
      docOrderedChanges = null;
   }

}

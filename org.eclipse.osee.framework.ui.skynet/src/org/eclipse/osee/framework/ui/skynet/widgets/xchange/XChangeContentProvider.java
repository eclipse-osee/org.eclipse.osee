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
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChanged;
import org.eclipse.osee.framework.skynet.core.change.Change;

public class XChangeContentProvider implements ITreeContentProvider {
   private final ChangeXViewer changeXViewer;
   private static Object[] EMPTY_ARRAY = new Object[0];
   private Map<Artifact, ArtifactChanged> artifactToChangeMap;
   private ArrayList<ArtifactChanged> docOrderedChnages;
   private boolean showDocOrder;

   public XChangeContentProvider(ChangeXViewer commitXViewer) {
      super();
      this.changeXViewer = commitXViewer;
      this.showDocOrder = true;
      this.artifactToChangeMap = new HashMap<Artifact, ArtifactChanged>();
      this.docOrderedChnages = new ArrayList<ArtifactChanged>();
   }

   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection) {
         if(showDocOrder){
            try {
               return getDocOrderedChanges((Collection<Change>) parentElement);
            } catch (OseeCoreException ex) {
               ex.printStackTrace();
            }
         }
         return ((Collection<?>) parentElement).toArray();
      }
      return EMPTY_ARRAY;
   }

   private Object[] getDocOrderedChanges(Collection<Change> changes) throws OseeCoreException {
      Set<Artifact> artifacts = new HashSet<Artifact>();

      if (docOrderedChnages.size() < 1) {
         for (Object object : changes) {
            if (object instanceof ArtifactChanged) {
               ArtifactChanged artifactChanged = (ArtifactChanged)object;
               Artifact artifact = artifactChanged.getArtifact();
               if (artifact != null) {
                  artifacts.add(artifact);
                  artifactToChangeMap.put(artifact, artifactChanged);
               }
            }
         }
         
         DefaultHierSorter sorter = new DefaultHierSorter();
         
         for(Artifact artifact : sorter.sort(artifacts)){
            docOrderedChnages.add(artifactToChangeMap.get(artifact));
         }
      }

      return docOrderedChnages.toArray();
   }
   
   public void refeshDocOrder(){
      if(docOrderedChnages.size() > 1){
         docOrderedChnages.clear();
      }
   }

   public Object getParent(Object element) {
      return null;
   }

   public boolean hasChildren(Object element) {
      if (element instanceof Collection) return true;
      return false;
   }

   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

   /**
    * @param showDocOrder the showDocOrder to set
    */
   public void setShowDocOrder(boolean showDocOrder) {
      this.showDocOrder = showDocOrder;
   }
   
   /**
    * @return the changeXViewer
    */
   public ChangeXViewer getChangeXViewer() {
      return changeXViewer;
   }

}

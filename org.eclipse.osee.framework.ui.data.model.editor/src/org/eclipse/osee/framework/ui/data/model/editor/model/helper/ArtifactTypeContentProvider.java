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
package org.eclipse.osee.framework.ui.data.model.editor.model.helper;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeCache;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTypeContentProvider implements ITreeContentProvider {

   @Override
   public Object[] getChildren(Object element) {
      if (element instanceof DataTypeCache) {
         List<DataTypeSource> sources = new ArrayList<DataTypeSource>();
         DataTypeCache cache = (DataTypeCache) element;
         for (String key : cache.getDataTypeSourceIds()) {
            sources.add(cache.getDataTypeSourceById(key));
         }
         return sources.toArray();
      }
      if (element instanceof DataTypeSource) {
         List<PackageModel> data = new ArrayList<PackageModel>();

         List<ArtifactDataType> artifacts = ((DataTypeSource) element).getArtifactTypeManager().getAll();
         List<ArtifactDataType> topLevel = new ArrayList<ArtifactDataType>();
         for (ArtifactDataType artifact : artifacts) {
            if (artifact.getSuperType() == null) {
               topLevel.add(artifact);
            }
         }
         data.add(new PackageModel(artifacts.get(0).getNamespace(), topLevel));
         return data.toArray();
      }
      if (element instanceof PackageModel) {
         return ((PackageModel) element).getArtifacts().toArray();
      }
      if (element instanceof ArtifactDataType) {
         return ((ArtifactDataType) element).getSubTypes().toArray();
      }
      return null;
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof DataTypeCache) {
         return !((DataTypeCache) element).getDataTypeSourceIds().isEmpty();
      }
      if (element instanceof DataTypeSource) {
         return ((DataTypeSource) element).getArtifactTypeManager().size() > 0;
      }
      if (element instanceof PackageModel) {
         return !((PackageModel) element).hasArtifactTypes();
      }
      if (element instanceof ArtifactDataType) {
         return !((ArtifactDataType) element).getSubTypes().isEmpty();
      }
      return false;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   @Override
   public void dispose() {
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

}

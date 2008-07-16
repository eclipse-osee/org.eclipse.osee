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
package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import java.util.Collection;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class MassArtifactEditorInput implements IEditorInput {

   private final Collection<? extends Artifact> artifacts;

   private final String name;

   private final List<XViewerColumn> columns;

   /**
    * @param artifact
    */
   public MassArtifactEditorInput(String name, Collection<? extends Artifact> artifacts, List<XViewerColumn> columns) {
      this.name = name;
      this.artifacts = artifacts;
      this.columns = columns;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      return false;
   }

   /**
    * @return the taskArts
    */
   public Collection<? extends Artifact> getArtifacts() {
      return artifacts;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#exists()
    */
   public boolean exists() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
    */
   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getPersistable()
    */
   public IPersistableElement getPersistable() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IEditorInput#getToolTipText()
    */
   public String getToolTipText() {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      return null;
   }

   /**
    * @return the columns
    */
   public List<XViewerColumn> getColumns() {
      return columns;
   }

}

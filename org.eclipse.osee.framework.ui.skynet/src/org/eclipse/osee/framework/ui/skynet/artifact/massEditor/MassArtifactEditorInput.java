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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class MassArtifactEditorInput implements IEditorInput {

   private final Collection<? extends Artifact> artifacts;
   private final String name;
   private final SkynetXViewerFactory skynetXViewerFactory;

   public SkynetXViewerFactory getXViewerFactory() {
      return skynetXViewerFactory;
   }

   public MassArtifactEditorInput(String name, Collection<? extends Artifact> artifacts, SkynetXViewerFactory skynetXViewerFactory) {
      this.name = name;
      this.artifacts = artifacts;
      this.skynetXViewerFactory = skynetXViewerFactory;
   }

   public Collection<? extends Artifact> getArtifacts() {
      return artifacts;
   }

   public String getName() {
      return name;
   }

   public boolean exists() {
      return false;
   }

   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   public IPersistableElement getPersistable() {
      return null;
   }

   public String getToolTipText() {
      return "";
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      return null;
   }

}

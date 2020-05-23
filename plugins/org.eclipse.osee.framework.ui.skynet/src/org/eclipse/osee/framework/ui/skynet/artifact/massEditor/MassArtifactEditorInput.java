/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import java.util.Collection;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.Adaptable;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class MassArtifactEditorInput implements IEditorInput, Adaptable {

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

   @Override
   public String getName() {
      return name;
   }

   @Override
   public boolean exists() {
      return false;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return null;
   }

   @Override
   public IPersistableElement getPersistable() {
      return null;
   }

   @Override
   public String getToolTipText() {
      return "";
   }
}
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
package org.eclipse.osee.coverage.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Donald G. Dunne
 */
public class CoverageEditorInput implements IEditorInput {

   private final CoveragePackageBase coveragePackageBase;
   private final Artifact coveragePackageArtifact;

   public CoverageEditorInput(Artifact coveragePackageArtifact, CoveragePackageBase coveragePackageBase) {
      this.coveragePackageArtifact = coveragePackageArtifact;
      this.coveragePackageBase = coveragePackageBase;
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
   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

   @Override
   public String getName() {
      return coveragePackageBase.getName();
   }

   public CoveragePackageBase getCoveragePackageBase() {
      return coveragePackageBase;
   }

   public Artifact getCoveragePackageArtifact() {
      return coveragePackageArtifact;
   }

}

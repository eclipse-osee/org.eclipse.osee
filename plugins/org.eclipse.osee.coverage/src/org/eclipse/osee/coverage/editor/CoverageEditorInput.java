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

   private CoveragePackageBase coveragePackageBase;
   private Artifact coveragePackageArtifact;
   private final String preLoadName;
   private final boolean isInTest;

   public CoverageEditorInput(String preLoadName, Artifact coveragePackageArtifact, CoveragePackageBase coveragePackageBase, boolean isInTest) {
      this.preLoadName = preLoadName;
      this.coveragePackageArtifact = coveragePackageArtifact;
      this.coveragePackageBase = coveragePackageBase;
      this.isInTest = isInTest;
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

   @SuppressWarnings("rawtypes")
   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

   @Override
   public String getName() {
      if (coveragePackageBase == null) {
         return getPreLoadName();
      }
      return coveragePackageBase.getName();
   }

   public CoveragePackageBase getCoveragePackageBase() {
      return coveragePackageBase;
   }

   public Artifact getCoveragePackageArtifact() {
      return coveragePackageArtifact;
   }

   public String getPreLoadName() {
      return preLoadName;
   }

   public void setCoveragePackageBase(CoveragePackageBase coveragePackageBase) {
      this.coveragePackageBase = coveragePackageBase;
   }

   public boolean isInTest() {
      return isInTest;
   }

   public void setCoveragePackageArtifact(Artifact coveragePackageArtifact) {
      this.coveragePackageArtifact = coveragePackageArtifact;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((coveragePackageArtifact == null) ? 0 : coveragePackageArtifact.hashCode());
      result = prime * result + ((coveragePackageBase == null) ? 0 : coveragePackageBase.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      CoverageEditorInput other = (CoverageEditorInput) obj;
      if (coveragePackageArtifact == null) {
         if (other.coveragePackageArtifact != null) {
            return false;
         }
      } else if (!coveragePackageArtifact.equals(other.coveragePackageArtifact)) {
         return false;
      }
      if (coveragePackageBase == null) {
         if (other.coveragePackageBase != null) {
            return false;
         }
      } else if (!coveragePackageBase.equals(other.coveragePackageBase)) {
         return false;
      }
      return true;
   }

}

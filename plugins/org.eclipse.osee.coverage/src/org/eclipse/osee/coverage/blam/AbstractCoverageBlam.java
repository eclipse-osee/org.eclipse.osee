/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.blam;

import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractCoverageBlam extends AbstractBlam {

   CoverageImport coverageImport;
   CoverageEditor coverageEditor;

   public CoverageImport getCoverageImport() {
      return coverageImport;
   }

   public void setCoverageImport(CoverageImport coverageImport) {
      this.coverageImport = coverageImport;
      if (coverageEditor != null) {
         coverageEditor.getCoverageEditorImportTab().setCoverageImportResults(getName(), coverageImport);
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((coverageImport.getName() == null) ? 0 : coverageImport.getName().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      AbstractCoverageBlam other = (AbstractCoverageBlam) obj;
      if (coverageImport == null) {
         if (other.coverageImport != null) return false;
      } else if (!coverageImport.getName().equals(other.coverageImport.getName())) return false;
      return true;
   }

   public CoverageEditor getCoverageEditor() {
      return coverageEditor;
   }

   public void setCoverageEditor(CoverageEditor coverageEditor) {
      this.coverageEditor = coverageEditor;
   }

}

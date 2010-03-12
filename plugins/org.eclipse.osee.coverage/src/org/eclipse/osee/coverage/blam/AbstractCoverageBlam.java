/*
 * Created on Sep 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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

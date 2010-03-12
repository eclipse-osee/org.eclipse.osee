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
package org.eclipse.osee.coverage.editor.params;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.coverage.merge.MessageMergeItem;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.model.MessageCoverageItem;

/**
 * @author Donald G. Dunne
 */
public class CoverageParametersTextFilter extends ViewerFilter {

   private Set<ICoverage> shownCoverages;
   private Set<ICoverage> parentCoverages = new HashSet<ICoverage>(1000);
   private boolean showAll = true;

   public CoverageParametersTextFilter(XViewer xViewer) {
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (isShowAll()) return true;
      if (shownCoverages.contains(element)) return true;
      if (parentCoverages.contains(element)) return true;
      if (element instanceof MessageCoverageItem || element instanceof MessageMergeItem) return true;
      return false;
   }

   public void setShownCoverages(Set<ICoverage> shownCoverages) {
      this.shownCoverages = shownCoverages;
      computeParentCoverages();
   }

   public boolean isShowAll() {
      return showAll;
   }

   public void setShowAll(boolean showAll) {
      this.showAll = showAll;
   }

   private void computeParentCoverages() {
      parentCoverages.clear();
      for (ICoverage coverage : shownCoverages) {
         computeParentCoverages(coverage.getParent());
      }
   }

   private void computeParentCoverages(ICoverage coverage) {
      if (coverage == null) return;
      parentCoverages.add(coverage);
      if (coverage.getParent() != null) {
         computeParentCoverages(coverage.getParent());
      }
   }
}

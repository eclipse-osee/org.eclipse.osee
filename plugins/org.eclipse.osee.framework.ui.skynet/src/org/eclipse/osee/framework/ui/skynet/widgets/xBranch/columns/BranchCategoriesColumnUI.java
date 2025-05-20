/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch.columns;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Donald G. Dunne
 */
public class BranchCategoriesColumnUI extends XViewerColumn implements IXViewerPreComputedColumn {

   public static BranchCategoriesColumnUI instance = new BranchCategoriesColumnUI();
   public Map<BranchId, String> branchToCats = new HashMap<>();

   public static BranchCategoriesColumnUI getInstance() {
      return instance;
   }

   private BranchCategoriesColumnUI() {
      super("framework.branch.categories", "Categories", 100, XViewerAlign.Left, false, SortDataType.String, false,
         null);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public BranchCategoriesColumnUI copy() {
      BranchCategoriesColumnUI newXCol = new BranchCategoriesColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      for (Object obj : objects) {
         if (obj instanceof BranchId) {
            Branch branch = BranchManager.getBranch((BranchId) obj);
            // For performance, just show Baseline branch categories for now; Remove when categories loaded for all BranchTokens
            if (branch.getBranchType().isBaselineBranch()) {
               preComputedValueMap.put(((BranchId) obj).getId(),
                  Collections.toString(",", BranchManager.getBranchCategories((BranchId) obj)));
            } else {
               preComputedValueMap.put(((BranchId) obj).getId(), "skipping...");
            }
         }
      }
   }

   @Override
   public Long getKey(Object obj) {
      if (obj instanceof BranchId) {
         return ((BranchId) obj).getId();
      }
      return 0L;
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      return cachedValue;
   }

}

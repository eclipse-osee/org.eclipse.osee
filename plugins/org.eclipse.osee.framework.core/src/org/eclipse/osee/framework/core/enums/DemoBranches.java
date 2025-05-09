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

package org.eclipse.osee.framework.core.enums;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchToken;

/**
 * @author Donald G. Dunne
 */
public final class DemoBranches {

   public static final BranchToken Default_Reqts = BranchToken.create(143, "Default Reqts",
      Arrays.asList(CoreBranchCategoryTokens.ATS, CoreBranchCategoryTokens.PLE));
   public static final BranchToken SAW_Bld_1 =
      BranchToken.create(3, "SAW_Bld_1", Arrays.asList(CoreBranchCategoryTokens.PLE));
   public static final BranchToken SAW_Bld_2 =
      BranchToken.create(5, "SAW_Bld_2", Arrays.asList(CoreBranchCategoryTokens.ATS, CoreBranchCategoryTokens.PLE));
   public static final BranchToken SAW_Bld_3 =
      BranchToken.create(6, "SAW_Bld_3", Arrays.asList(CoreBranchCategoryTokens.ATS, CoreBranchCategoryTokens.PLE));

   public static final BranchToken SAW_PL_Access_Baseline_Test = BranchToken.create(5432,
      "SAW Access Control Baseline Test", Arrays.asList(CoreBranchCategoryTokens.ATS, CoreBranchCategoryTokens.PLE));

   public static final BranchToken SAW_PL = BranchToken.create(8, "SAW Product Line",
      Arrays.asList(CoreBranchCategoryTokens.ATS, CoreBranchCategoryTokens.PLE));
   public static final BranchToken SAW_PL_Hardening_Branch = BranchToken.create(9, "SAW PL Hardening Branch",
      Arrays.asList(CoreBranchCategoryTokens.ATS, CoreBranchCategoryTokens.PLE));
   public static final BranchToken SAW_PL_Working_Branch = BranchToken.create(10, "SAW PL Working Branch",
      Arrays.asList(CoreBranchCategoryTokens.ATS, CoreBranchCategoryTokens.PLE));

   public static final BranchToken Dispo_Parent = BranchToken.create(5781701693103907161L, "Dispo Parent",
      Arrays.asList(CoreBranchCategoryTokens.ATS, CoreBranchCategoryTokens.PLE));
   public static final BranchToken Dispo_Demo = BranchToken.create(5781701693103907162L, "Dispo Demo",
      Arrays.asList(CoreBranchCategoryTokens.ATS, CoreBranchCategoryTokens.PLE));

   /**
    * Used when creating a working branch in the {@link DemoDbPopulateSuite}. Not initialized in the {@link DbInitTest}.
    * Initialized in {@link ImportAndSetupMarkdownReqs} within the {@link DemoDbPopulateSuite}.
    */
   public static final BranchToken SAW_PL_Working_Branch_Markdown = BranchToken.create(3932812339895768577L,
      "SAW Markdown Requirements Updates", Arrays.asList(CoreBranchCategoryTokens.ATS, CoreBranchCategoryTokens.PLE));

   public static final BranchToken CIS_Bld_1 =
      BranchToken.create(4, "CIS_Bld_1", Arrays.asList(CoreBranchCategoryTokens.ATS, CoreBranchCategoryTokens.PLE));

   public static List<BranchToken> DEMO_BRANCHES =
      Arrays.asList(SAW_Bld_1, SAW_Bld_2, SAW_Bld_3, SAW_PL, SAW_PL_Hardening_Branch, CIS_Bld_1);

   public static final BranchToken Processes = BranchToken.create(3705076646356466171L, "Processes",
      Arrays.asList(CoreBranchCategoryTokens.ATS, CoreBranchCategoryTokens.PLE));

   private DemoBranches() {
      // Constants
   }
}
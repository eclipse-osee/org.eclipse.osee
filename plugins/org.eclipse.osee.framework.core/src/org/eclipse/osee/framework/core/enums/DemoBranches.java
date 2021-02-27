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
import org.eclipse.osee.framework.core.data.IOseeBranch;

/**
 * @author Donald G. Dunne
 */
public final class DemoBranches {

   public static final IOseeBranch SAW_Bld_1 = IOseeBranch.create(3, "SAW_Bld_1");
   public static final IOseeBranch SAW_Bld_2 = IOseeBranch.create(5, "SAW_Bld_2");
   public static final IOseeBranch SAW_Bld_3 = IOseeBranch.create(6, "SAW_Bld_3");

   public static final IOseeBranch SAW_PL = IOseeBranch.create(8, "SAW Product Line");
   public static final IOseeBranch SAW_PL_Hardening_Branch = IOseeBranch.create(9, "SAW PL Hardening Branch");
   public static final IOseeBranch SAW_PL_Working_Branch = IOseeBranch.create(10, "SAW PL Working Branch");

   public static final IOseeBranch CIS_Bld_1 = IOseeBranch.create(4, "CIS_Bld_1");

   public static List<IOseeBranch> DEMO_BRANCHES =
      Arrays.asList(SAW_Bld_1, SAW_Bld_2, SAW_Bld_3, SAW_PL, SAW_PL_Hardening_Branch, CIS_Bld_1);

   public static final IOseeBranch Processes = IOseeBranch.create(3705076646356466171L, "Processes");

   private DemoBranches() {
      // Constants
   }
}
/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.integration.util;

import org.eclipse.osee.disposition.rest.DispoConstants;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Angel Avila
 */
public class DispositionTestUtil {
   // @formatter:off
   public static final IArtifactToken DISPO_SET_DEMO = TokenFactory.createArtifactToken("BJAg0ZVHHA7oDqY_LcgA", "Dispo Set Demo", DispoConstants.DispoSet);
   public static final IArtifactToken DISPO_SET_DEV = TokenFactory.createArtifactToken("BJCzD1ZijxOCjL+n7pAA", "Dispo Set Dev", DispoConstants.DispoSet);
   public static final IArtifactToken DISPO_ITEM_DEV_ONE = TokenFactory.createArtifactToken("BJEiM7sbfDZjtkJ9JSAA", "Dispo Item Dev One", DispoConstants.DispoItem);
   public static final IArtifactToken DISPO_ITEM_DEMO_ONE = TokenFactory.createArtifactToken("BJEiNETyAGTDpW4ZnrAA", "Dispo Item Demo One", DispoConstants.DispoItem);
   
   public static final IOseeBranch SAW_Bld_1_FOR_DISPO =
      TokenFactory.createBranch("AyH_f2sSKy3l07fIvAVV", 2323, "SAW_Bld_1 - FOR_DISPOSITION");
   public static final IOseeBranch SAW_Bld_1 = TokenFactory.createBranch("AyH_f2sSKy3l07fIvAAA", 2000, "SAW_Bld_1");
   // @formatter:on

}

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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Angel Avila
 */
public class DispositionTestUtil {
   // @formatter:off
   public static final ArtifactToken DISPO_SET_DEMO = TokenFactory.createArtifactToken(663199, "BJAg0ZVHHA7oDqY_LcgA", "Dispo Set Demo", DispoConstants.DispoSet);
   public static final ArtifactToken DISPO_SET_DEV = TokenFactory.createArtifactToken(433652, "BJCzD1ZijxOCjL+n7pAA", "Dispo Set Dev", DispoConstants.DispoSet);
   public static final ArtifactToken DISPO_ITEM_DEV_ONE = TokenFactory.createArtifactToken(8702337, "BJEiM7sbfDZjtkJ9JSAA", "Dispo Item Dev One", DispoConstants.DispoItem);
   public static final ArtifactToken DISPO_ITEM_DEMO_ONE = TokenFactory.createArtifactToken(4132534, "BJEiNETyAGTDpW4ZnrAA", "Dispo Item Demo One", DispoConstants.DispoItem);

   public static final IOseeBranch SAW_Bld_1_FOR_DISPO = IOseeBranch.create("SAW_Bld_1 - FOR_DISPOSITION");
   // @formatter:on
}
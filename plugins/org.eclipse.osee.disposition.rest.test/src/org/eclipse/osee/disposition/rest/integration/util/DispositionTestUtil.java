/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.disposition.rest.integration.util;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Angel Avila
 */
public class DispositionTestUtil {
   // @formatter:off
   public static final ArtifactToken DISPO_SET_DEMO = ArtifactToken.valueOf(663199L, "Dispo Set Demo", CoreArtifactTypes.DispositionSet);
   public static final ArtifactToken DISPO_SET_DEV = ArtifactToken.valueOf(433652L, "Dispo Set Dev", CoreArtifactTypes.DispositionSet);
   public static final ArtifactToken DISPO_ITEM_DEV_ONE = ArtifactToken.valueOf(8702337L, "Dispo Item Dev One", CoreArtifactTypes.DispositionableItem);
   public static final ArtifactToken DISPO_ITEM_DEMO_ONE = ArtifactToken.valueOf(4132534L, "Dispo Item Demo One", CoreArtifactTypes.DispositionableItem);
   public static final BranchToken SAW_Bld_1_FOR_DISPO = BranchToken.create("SAW_Bld_1 - FOR_DISPOSITION");
   // @formatter:on
}
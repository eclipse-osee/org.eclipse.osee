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

import org.eclipse.osee.disposition.rest.DispoOseeTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;

/**
 * @author Angel Avila
 */
public class DispositionTestUtil {
   // @formatter:off
   public static final ArtifactToken DISPO_SET_DEMO = ArtifactToken.valueOf(663199L, "Dispo Set Demo", DispoOseeTypes.DispositionSet);
   public static final ArtifactToken DISPO_SET_DEV = ArtifactToken.valueOf(433652L, "Dispo Set Dev", DispoOseeTypes.DispositionSet);
   public static final ArtifactToken DISPO_ITEM_DEV_ONE = ArtifactToken.valueOf(8702337L, "Dispo Item Dev One", DispoOseeTypes.DispositionableItem);
   public static final ArtifactToken DISPO_ITEM_DEMO_ONE = ArtifactToken.valueOf(4132534L, "Dispo Item Demo One", DispoOseeTypes.DispositionableItem);
   public static final IOseeBranch SAW_Bld_1_FOR_DISPO = IOseeBranch.create("SAW_Bld_1 - FOR_DISPOSITION");
   // @formatter:on
}
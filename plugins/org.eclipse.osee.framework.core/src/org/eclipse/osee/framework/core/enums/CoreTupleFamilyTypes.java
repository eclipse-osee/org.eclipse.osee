/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import org.eclipse.osee.framework.core.data.TupleFamilyId;

/**
 * @author Ryan D. Brooks
 */
public final class CoreTupleFamilyTypes {

   public static final TupleFamilyId DefaultFamily = TupleFamilyId.valueOf(1L);
   public static final TupleFamilyId ArtifatFamily = TupleFamilyId.valueOf(2L);
   public static final TupleFamilyId AttribueFamily = TupleFamilyId.valueOf(3L);
   public static final TupleFamilyId RelationFamily = TupleFamilyId.valueOf(4L);
   public static final TupleFamilyId ProductLineFamily = TupleFamilyId.valueOf(5L);
   public static final TupleFamilyId GitFamily = TupleFamilyId.valueOf(6L);
   public static final TupleFamilyId JoinFamily = TupleFamilyId.valueOf(7L);

   private CoreTupleFamilyTypes() {
      // Constants
   }
}
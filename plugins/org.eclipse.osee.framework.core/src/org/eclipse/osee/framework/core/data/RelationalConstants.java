/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Roberto E. Escobar
 */
public final class RelationalConstants {

   private RelationalConstants() {
      // Constants class
   }

   public static final boolean IS_HISTORICAL_DEFAULT = false;
   public static final String DEFAULT_RATIONALE = "";
   public static final String DEFAULT_COMMENT = "";
   public static final ModificationType DEFAULT_MODIFICATION_TYPE = ModificationType.NEW;
   public static final int MIN_FETCH_SIZE = 10;
}
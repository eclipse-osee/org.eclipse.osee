/*******************************************************************************
 * Copyright (c) 20012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * @author Roberto E. Escobar
 */
public final class RelationalConstants {

   private RelationalConstants() {
      // Constants class
   }

   public static final int JOIN_QUERY_ID_SENTINEL = -1;
   public static final boolean IS_HISTORICAL_DEFAULT = false;
   public static final int ART_ID_SENTINEL = -1;
   public static final String DEFAULT_RATIONALE = "";
   public static final String DEFAULT_NAME = "";
   public static final String DEFAULT_COMMENT = "";

   public static final Integer DEFAULT_ITEM_ID = -1;
   public static final long DEFAULT_TYPE_UUID = -1L;

   public static final ModificationType DEFAULT_MODIFICATION_TYPE = ModificationType.NEW;

   public static final String DEFAULT_GUID = null;
   public static final Long DEFAULT_UUID = -1L;

   public static final int MIN_FETCH_SIZE = 10;

}

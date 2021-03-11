/*********************************************************************
 * Copyright (c) 2019 Boeing
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

import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Ryan D. Brooks
 */
public class Multiplicity extends BaseId {
   public static final Multiplicity SENTINEL = Multiplicity.valueOf(Id.SENTINEL);
   public static final Multiplicity ANY = Multiplicity.valueOf(1);
   public static final Multiplicity EXACTLY_ONE = Multiplicity.valueOf(2);
   public static final Multiplicity ZERO_OR_ONE = Multiplicity.valueOf(3);
   public static final Multiplicity AT_LEAST_ONE = Multiplicity.valueOf(4);

   public Multiplicity(Long id) {
      super(id);
   }

   public static Multiplicity valueOf(long id) {
      return new Multiplicity(id);
   }
}
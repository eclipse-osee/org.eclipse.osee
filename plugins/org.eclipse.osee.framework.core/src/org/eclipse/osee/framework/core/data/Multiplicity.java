/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.BaseId;

/**
 * @author Ryan D. Brooks
 */
public class Multiplicity extends BaseId {
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
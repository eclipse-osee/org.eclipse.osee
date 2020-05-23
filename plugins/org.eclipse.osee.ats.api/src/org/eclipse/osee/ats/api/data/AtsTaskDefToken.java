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

package org.eclipse.osee.ats.api.data;

import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskDefToken extends NamedIdBase {

   public static String ID = AtsTaskDefToken.class.getSimpleName();

   public AtsTaskDefToken() {
      // for jax-rs
   }

   private AtsTaskDefToken(Long id, String name) {
      super(id, name);
   }

   public static AtsTaskDefToken valueOf(Long id, String name) {
      return new AtsTaskDefToken(id, name);
   }

}

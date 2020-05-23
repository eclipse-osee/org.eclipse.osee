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

package org.eclipse.osee.ats.api.workdef;

import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionToken extends NamedIdBase {

   public static final AtsWorkDefinitionToken SENTINEL = new AtsWorkDefinitionToken(-1L, "");

   public AtsWorkDefinitionToken() {
      super(Id.SENTINEL, "");

   }

   public AtsWorkDefinitionToken(Long id, String name) {
      super(id, name);
   }

   public static AtsWorkDefinitionToken valueOf(Long id, String name) {
      return new AtsWorkDefinitionToken(id, name);
   }

}

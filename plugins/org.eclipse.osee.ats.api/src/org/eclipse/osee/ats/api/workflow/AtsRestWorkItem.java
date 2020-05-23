/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.api.workflow;

import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class AtsRestWorkItem extends NamedIdBase {

   private final String atsId;

   public AtsRestWorkItem(String name, long id, String atsId) {
      super(id, name);
      this.atsId = atsId;
   }

   public String getAtsId() {
      return atsId;
   }
}

/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

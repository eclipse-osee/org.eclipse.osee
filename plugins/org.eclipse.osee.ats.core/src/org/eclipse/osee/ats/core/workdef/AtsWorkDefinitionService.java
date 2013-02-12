/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionService {

   private static AtsWorkDefinitionService instance;
   private IAtsWorkDefinitionService service;

   public static IAtsWorkDefinitionService getService() {
      if (instance == null) {
         throw new IllegalStateException("ATS Work Definition Service has not been activated");
      }
      return instance.service;
   }

   public void setWorkDefinitionService(IAtsWorkDefinitionService service) {
      this.service = service;
   }

   public void start() {
      instance = this;
   }
}

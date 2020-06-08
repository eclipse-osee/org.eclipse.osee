/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task.create;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;

/**
 * Methods to control how/when TaskSetDefinitions should be used and what should be done.
 *
 * @author Donald G. Dunne
 */
public interface IAtsTaskSetDefinitionHelper {

   default public boolean isApplicable(IAtsWorkItem workItem, AtsApi atsApi) {
      return true;
   }

   public static IAtsTaskSetDefinitionHelper emptyHelper() {
      return new IAtsTaskSetDefinitionHelper() {
         // do nothing
      };
   }

}

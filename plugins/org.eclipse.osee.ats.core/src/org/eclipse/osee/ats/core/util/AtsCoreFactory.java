/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.column.IAtsColumnService;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnService;
import org.eclipse.osee.ats.core.internal.log.AtsLogFactory;
import org.eclipse.osee.ats.core.internal.state.AtsStateFactory;
import org.eclipse.osee.ats.core.internal.state.AtsWorkStateFactory;

/**
 * @author Donald G. Dunne
 */
public final class AtsCoreFactory {

   private AtsCoreFactory() {
      //
   }

   public static IAtsStateFactory newStateFactory(AtsApi atsApi, IAtsLogFactory logFactory) {
      return new AtsStateFactory(atsApi, new AtsWorkStateFactory(atsApi.getUserService()), logFactory);
   }

   public static IAtsLogFactory newLogFactory() {
      return new AtsLogFactory();
   }

   public static IAtsColumnService getColumnService(AtsApi atsApi) {
      return new AtsColumnService(atsApi);
   }

   public static IAtsWorkStateFactory getWorkStateFactory(IAtsUserService userService) {
      return new AtsWorkStateFactory(userService);
   }

   public static IAtsLogFactory getLogFactory() {
      return new AtsLogFactory();
   }

}

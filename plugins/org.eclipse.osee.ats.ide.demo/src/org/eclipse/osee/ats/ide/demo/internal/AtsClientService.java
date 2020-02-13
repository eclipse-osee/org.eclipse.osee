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
package org.eclipse.osee.ats.ide.demo.internal;

import org.eclipse.osee.ats.ide.util.IAtsClient;

/**
 * @author Donald G. Dunne
 */
public class AtsClientService {

   private static IAtsClient atsClient;

   public void setAtsClient(IAtsClient atsClient) {
      AtsClientService.atsClient = atsClient;
   }

   public static IAtsClient get() {
      return atsClient;
   }

}

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
package org.eclipse.osee.ats.core.internal;

import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsEarnedValueService {

   private static IAtsEarnedValueServiceProvider earnedValueServiceProvider;

   public void setEarnedValueServiceProvider(IAtsEarnedValueServiceProvider earnedValueProvider) {
      AtsEarnedValueService.earnedValueServiceProvider = earnedValueProvider;
   }

   public static IAtsEarnedValueServiceProvider getEarnedValueServiceProvider() {
      return earnedValueServiceProvider;
   }

}

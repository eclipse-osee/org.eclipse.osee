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

import org.eclipse.osee.ats.core.config.IAtsConfigProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigUtility {

   private static IAtsConfigProvider atsConfigProvider;

   public void setAtsConfigProvider(IAtsConfigProvider atsConfigProvider) {
      AtsConfigUtility.atsConfigProvider = atsConfigProvider;
   }

   public static IAtsConfigProvider getAtsConfigProvider() {
      return atsConfigProvider;
   }

}

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
package org.eclipse.osee.ats.rest.internal.config;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.config.IAtsConfigurationViewsProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigurationViewsService {

   private static List<IAtsConfigurationViewsProvider> viewsProviders = new LinkedList<>();

   public void addConfigurationViewsProvider(IAtsConfigurationViewsProvider configViewProvider) {
      AtsConfigurationViewsService.viewsProviders.add(configViewProvider);
   }

   public static List<IAtsConfigurationViewsProvider> getViewsProviders() {
      return viewsProviders;
   }

}

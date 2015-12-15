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
package org.eclipse.osee.ats.rest.internal.util;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.core.util.CacheProvider;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionCache;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;

/**
 * Thread safe loading of ATS Config Objects
 * 
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionCacheProvider extends LazyObject<AtsWorkDefinitionCache> implements CacheProvider<AtsWorkDefinitionCache> {

   private final IAtsWorkDefinitionService workDefinitionService;

   public AtsWorkDefinitionCacheProvider(IAtsWorkDefinitionService workDefinitionService) {
      super();
      this.workDefinitionService = workDefinitionService;
   }

   @Override
   protected FutureTask<AtsWorkDefinitionCache> createLoaderTask() {
      Callable<AtsWorkDefinitionCache> newCallable = new LoadAtsWorkDefinitionCacheCallable(workDefinitionService);
      FutureTask<AtsWorkDefinitionCache> newTask = new FutureTask<>(newCallable);
      return newTask;
   }

}

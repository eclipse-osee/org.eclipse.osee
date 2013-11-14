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
package org.eclipse.osee.ats.core.client.internal.config;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactStore;
import org.eclipse.osee.ats.core.util.CacheProvider;
import org.eclipse.osee.framework.core.data.LazyObject;

/**
 * Thread safe loading of ATS Config Objects
 * 
 * @author Donald G. Dunne
 */
public class AtsConfigCacheProvider extends LazyObject<AtsArtifactConfigCache> implements CacheProvider<AtsArtifactConfigCache> {

   private final IAtsArtifactStore artifactStore;

   public AtsConfigCacheProvider(IAtsArtifactStore artifactStore) {
      this.artifactStore = artifactStore;
   }

   @Override
   protected FutureTask<AtsArtifactConfigCache> createLoaderTask() {
      Callable<AtsArtifactConfigCache> newCallable = new LoadAtsConfigCacheCallable(artifactStore);
      FutureTask<AtsArtifactConfigCache> newTask = new FutureTask<AtsArtifactConfigCache>(newCallable);
      return newTask;
   }

}

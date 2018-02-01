/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public interface IAtsCache {

   public <T extends IAtsObject> T getAtsObject(Long id);

   public <T extends IAtsObject> T getAtsObject(ArtifactId artifact);

   public void cacheAtsObject(IAtsObject atsObject);

   public void cacheArtifact(ArtifactId artifact);

   public void invalidate();

   public void deCacheAtsObject(IAtsObject atsObject);
}
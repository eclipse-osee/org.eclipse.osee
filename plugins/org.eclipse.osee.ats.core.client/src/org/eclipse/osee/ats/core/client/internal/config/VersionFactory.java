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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IVersionFactory;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.config.Version;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Donald G. Dunne
 */
public class VersionFactory implements IVersionFactory {

   @Override
   public IAtsVersion createVersion(String title, IAtsChangeSet changes, AtsApi atsApi) {
      return createVersion(title, AtsUtilClient.createConfigObjectId(), changes, atsApi);
   }

   @Override
   public IAtsVersion createVersion(String name, long id, IAtsChangeSet changes, AtsApi atsApi) {
      ArtifactToken artifact = changes.createArtifact(AtsArtifactTypes.Version, name, GUID.create(), id);
      return new Version(atsApi.getLogger(), atsApi, artifact);
   }

}

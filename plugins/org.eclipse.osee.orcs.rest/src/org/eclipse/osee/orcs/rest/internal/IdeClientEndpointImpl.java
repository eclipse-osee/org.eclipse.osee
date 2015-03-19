/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.orcs.rest.model.IdeClientEndpoint;
import org.eclipse.osee.orcs.rest.model.IdeVersion;

/**
 * @author Roberto E. Escobar
 */
public class IdeClientEndpointImpl implements IdeClientEndpoint {

   @Override
   public IdeVersion getSupportedVersions() {
      IdeVersion versions = new IdeVersion();
      versions.addVersion(OseeCodeVersion.getVersion());
      return versions;
   }

}

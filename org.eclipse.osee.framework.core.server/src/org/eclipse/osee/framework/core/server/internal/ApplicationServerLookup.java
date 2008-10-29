/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.server.internal;

import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerLookup implements IApplicationServerLookup {

   public OseeServerInfo searchBy(String version) {
      // TODO :: ADD SEARCH AND SELECTION LOGIC    
      // ApplicationServerDataStore.getApplicationServerInfos(options);

      return null;
   }
}

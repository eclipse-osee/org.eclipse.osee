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
package org.eclipse.osee.ats.core.config;

import org.eclipse.osee.ats.api.version.IAtsVersion;

public interface IVersionFactory {

   IAtsVersion createVersion(String title, String create, String generate);

   IAtsVersion getOrCreate(String guid, String name);

   IAtsVersion createVersion(String name);

}

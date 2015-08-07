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
package org.eclipse.osee.ats.core.client.internal;

import org.eclipse.osee.ats.api.util.IArtifactResolver;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

public interface IAtsWorkItemArtifactServiceProvider {

   IArtifactResolver getArtifactResolver() throws OseeStateException;
}

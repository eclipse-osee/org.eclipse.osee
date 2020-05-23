/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.importing;

import java.net.URI;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IOseeTypesHandler {

   boolean isApplicable(String resource);

   void execute(IProgressMonitor monitor, URI uri);

}

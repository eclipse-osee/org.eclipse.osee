/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.server;

import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;

public interface TestEnvironmentFactory {
   MessageSystemTestEnvironment createEnvironment(IRuntimeLibraryManager runtimeLibraryManager) throws Throwable;
}

/*
 * Created on Mar 17, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.server;

import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;

public interface TestEnvironmentFactory {
   MessageSystemTestEnvironment createEnvironment(IRuntimeLibraryManager runtimeLibraryManager) throws Throwable;
}

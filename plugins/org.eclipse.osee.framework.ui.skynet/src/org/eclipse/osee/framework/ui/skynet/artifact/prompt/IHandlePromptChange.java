/*
 * Created on Jul 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.artifact.prompt;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface IHandlePromptChange {
   boolean promptOk() throws OseeCoreException;

   boolean store() throws OseeCoreException;

}

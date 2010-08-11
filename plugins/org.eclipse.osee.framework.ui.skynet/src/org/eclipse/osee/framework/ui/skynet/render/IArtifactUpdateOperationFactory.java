/*
 * Created on Aug 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.File;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;

public interface IArtifactUpdateOperationFactory {

   IOperation createUpdateOp(File file) throws OseeCoreException;
}

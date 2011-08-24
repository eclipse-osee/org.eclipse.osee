/*
 * Created on Aug 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.validate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public abstract class AbstractValidationRule {
   /**
    * @param artToValidate The Artifact to evaluate and validate against the criteria provided separately.
    * @param operation An AbstractOperation to be used simply to update the operation monitor during validation.
    * @param rd The XResultData object to write (or render) validation results to.
    */
   protected abstract ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor) throws OseeCoreException;
}

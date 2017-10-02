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
package org.eclipse.osee.ats.util.validate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public abstract class AbstractValidationRule {
   /**
    * @param artToValidate The Artifact to evaluate and validate against the criteria provided separately.
    * @param operation An AbstractOperation to be used simply to update the operation monitor during validation.
    * @param rd The XResultData object to write (or render) validation results to.
    */
   protected abstract ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor) ;

   public abstract String getRuleDescription();

   public abstract String getRuleTitle();
}

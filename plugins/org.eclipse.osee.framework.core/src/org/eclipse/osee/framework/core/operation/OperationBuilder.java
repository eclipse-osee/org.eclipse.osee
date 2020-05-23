/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.core.operation;

import java.util.List;
import org.eclipse.osee.framework.core.enums.OperationBehavior;

/**
 * @author Roberto E. Escobar
 */
public interface OperationBuilder {

   String getName();

   OperationBehavior getExecutionBehavior();

   OperationLogger getLogger();

   OperationBuilder executionBehavior(OperationBehavior behavior);

   OperationBuilder logger(OperationLogger logger);

   OperationBuilder addOp(IOperation op);

   OperationBuilder addOp(double weight, IOperation op);

   OperationBuilder addAll(List<? extends IOperation> operations);

   /**
    * Builds a composite operation. Once build is called, all builder settings are set back to defaults.
    * 
    * @return IOperation collecting all added operations
    */
   IOperation build();

}
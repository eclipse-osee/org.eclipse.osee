/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.traceability;

import org.eclipse.osee.framework.core.operation.AbstractOperation;

/**
 * @author John R. Misinco
 */
public abstract class TraceabilityProviderOperation extends AbstractOperation implements ITraceabilityProvider {

   public TraceabilityProviderOperation(String operationName, String pluginId) {
      super(operationName, pluginId);
   }

}

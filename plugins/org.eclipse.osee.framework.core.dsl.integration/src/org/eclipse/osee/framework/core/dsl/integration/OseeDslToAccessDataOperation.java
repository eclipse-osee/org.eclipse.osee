/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.integration;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.dsl.integration.internal.Activator;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class OseeDslToAccessDataOperation extends AbstractOperation {

	private final AccessModelInterpreter interpreter;
	private final AccessData accessData;
	private final AccessContextId contextId;
	private final Collection<AccessContext> accessContexts;
	private final Collection<?> objectsToCheck;

	public OseeDslToAccessDataOperation(AccessModelInterpreter interpreter, AccessData accessData, AccessContextId contextId, Collection<AccessContext> accessContexts, Collection<?> objectsToCheck) {
		super("Access Dsl To AccessData", Activator.PLUGIN_ID);
		this.accessData = accessData;
		this.contextId = contextId;
		this.accessContexts = accessContexts;
		this.objectsToCheck = objectsToCheck;
		this.interpreter = interpreter;
	}

	@Override
	protected void doWork(IProgressMonitor monitor) throws Exception {
		AccessContext context = interpreter.getContext(accessContexts, contextId);
		Conditions.checkNotNull(context, "context", "Unable to find accessContext for [%s]", contextId);

		if (objectsToCheck.isEmpty()) {
			monitor.worked(getTotalWorkUnits());
		} else {
			double stepAmount = 1.0 / objectsToCheck.size();
			int step = calculateWork(stepAmount);
			for (Object objectToCheck : objectsToCheck) {
				checkForCancelledStatus(monitor);
				Collection<AccessDetail<?>> accessDetail = new HashSet<AccessDetail<?>>();
				interpreter.computeAccessDetails(context, objectToCheck, accessDetail);
				accessData.addAll(objectToCheck, accessDetail);
				monitor.worked(step);
			}
		}
	}
}

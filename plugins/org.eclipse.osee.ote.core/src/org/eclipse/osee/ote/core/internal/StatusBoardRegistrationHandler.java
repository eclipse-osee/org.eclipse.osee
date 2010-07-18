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
package org.eclipse.osee.ote.core.internal;

import java.util.Map;

import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.core.environment.status.OTEStatusBoard;
import org.eclipse.osee.ote.core.environment.status.StatusBoard;
import org.osgi.framework.BundleContext;

public class StatusBoardRegistrationHandler extends AbstractTrackingHandler {

	private final static Class<?>[] SERVICE_DEPENDENCIES = new Class<?>[] {OTEStatusBoard.class, TestEnvironmentInterface.class};
	private TestEnvironmentInterface testEnv;
	private StatusBoard statusBoard;
	
	@Override
	public Class<?>[] getDependencies() {
		return SERVICE_DEPENDENCIES;
	}

	@Override
	public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
		
		testEnv = getService(TestEnvironmentInterface.class, services);
		statusBoard = (StatusBoard)getService(OTEStatusBoard.class, services);
		testEnv.addEnvironmentListener(statusBoard);
	}

	@Override
	public void onDeActivate() {
	}

}

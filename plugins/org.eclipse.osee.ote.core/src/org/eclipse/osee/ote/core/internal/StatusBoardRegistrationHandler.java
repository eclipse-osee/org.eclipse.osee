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

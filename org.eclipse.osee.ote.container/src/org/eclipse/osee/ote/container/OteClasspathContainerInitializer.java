/*
 * Created on Aug 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.container;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IJavaProject;

public class OteClasspathContainerInitializer extends
		ClasspathContainerInitializer {

	public OteClasspathContainerInitializer() {
	}

	@Override
	public void initialize(IPath containerPath, IJavaProject project)
			throws CoreException {
	}

}

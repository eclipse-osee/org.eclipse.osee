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
package org.eclipse.osee.ote.ui;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class ProjectNatureTest implements IProjectNature {

	public static final String TEST_NATURE = "org.eclipse.osee.ote.ui.testnature";
	
	private static List<IProject> testProjects = new ArrayList<IProject>();
	
	private IProject project;
	
	@Override
	public void configure() throws CoreException {
		testProjects.add(project);
	}

	@Override
	public void deconfigure() throws CoreException {
		testProjects.remove(project);
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

	
	public static List<IProject> getTestProjects(){
		return testProjects;
	}
}

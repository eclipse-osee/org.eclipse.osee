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
package org.eclipse.osee.framework.ui.workspacebundleloader;

import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.BundleException;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public interface WorkspaceLoader {
	public void loadBundles() throws CoreException, BundleException;
	public void unloadBundles();
}

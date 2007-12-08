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
package org.eclipse.osee.framework.ui.skynet.render;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Ryan D. Brooks
 */
public interface ITemplateRenderer extends IRenderer {
   public abstract void renderInComposite(Composite composite, BlamVariableMap variableMap, boolean readOnly, IProgressMonitor monitor) throws Exception;

   public abstract void renderToFolder(IFolder folder, BlamVariableMap variableMap, boolean readOnly, IProgressMonitor monitor) throws Exception;
}

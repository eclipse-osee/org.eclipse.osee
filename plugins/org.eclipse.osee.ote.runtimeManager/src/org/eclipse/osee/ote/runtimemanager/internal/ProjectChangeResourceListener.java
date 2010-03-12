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
package org.eclipse.osee.ote.runtimemanager.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

/** 
 * @author Robert A. Fisher
 *
 */
public class ProjectChangeResourceListener implements IResourceChangeListener {

   @Override
   public void resourceChanged(IResourceChangeEvent event) {
   }

   /**
    * @param project
    */
   public void addProject(IProject project) {
   }

}

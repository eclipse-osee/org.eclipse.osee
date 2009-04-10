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
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.core.resources.IFile;

/**
 * @author Michael S. Rodgers
 */
public class WorkspaceURL {
   public static String getURL(IFile file) {
      // Add only 1 "/" due to the path for the file having a preceding "/"
      return "ws:/" + file.getFullPath().toString();
   }
}

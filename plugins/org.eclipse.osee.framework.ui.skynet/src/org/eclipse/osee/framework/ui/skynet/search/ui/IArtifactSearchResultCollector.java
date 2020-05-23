/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.search.ui;

import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Michael S. Rodgers
 */
public interface IArtifactSearchResultCollector {
   /**
    * Returns the progress monitor used to setup and report progress.
    * 
    * @return The progress monitor
    */
   public IProgressMonitor getProgressMonitor();

   /**
    * Called before the actual search starts.
    * 
    * @throws CoreException Throws when starting failed.
    */
   public void aboutToStart() throws CoreException;

   /**
    * Accepts the given search result.
    * 
    * @param proxy proxy the resource proxy in which the match has been found.
    * @param start position from the beginning of the file. Start position is zero based.
    * @param length the length of the match.
    * @throws CoreException Processing failed
    */
   public void accept(IResourceProxy proxy, int start, int length) throws CoreException;

   /**
    * Called when the search has ended.
    * 
    * @throws CoreException Throws when finish failed.
    */
   public void done() throws CoreException;
}

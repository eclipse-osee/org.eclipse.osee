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

package org.eclipse.osee.framework.jdk.core.util.io;

import java.util.Collection;

/**
 * @author Ken J. Aguilar
 */
public interface IFileWatcherListener {

   public void filesModified(Collection<FileChangeEvent> fileChangeEvents);

   public void handleException(Exception ex);
}

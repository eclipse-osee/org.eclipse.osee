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

package org.eclipse.osee.define.ide.utility;

import java.nio.CharBuffer;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.osee.define.ide.traceability.ResourceIdentifier;

/**
 * @author Roberto E. Escobar
 */
public interface IResourceLocator {

   public boolean isValidDirectory(IFileStore fileStore);

   public boolean isValidFile(IFileStore fileStore);

   public boolean hasValidContent(CharBuffer fileBuffer);

   public ResourceIdentifier getIdentifier(IFileStore fileStore, CharBuffer fileBuffer) throws Exception;
}

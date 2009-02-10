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

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Theron Virgin
 */
public interface IVbaDiffGenerator {
   public boolean initialize(boolean visible, boolean detectFormatChanges);

   public boolean addComparison(IFile baseFile, IFile newerFile, String diffPath, boolean merge);

   public void finish(String vbaScriptPath, boolean show) throws OseeCoreException;

   public File getFile(String path) throws OseeCoreException;
}

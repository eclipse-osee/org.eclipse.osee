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
package org.eclipse.osee.framework.services;

import java.io.OutputStream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeImportModelRequest;
import org.eclipse.osee.framework.core.data.OseeImportModelResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeModelingService {

   void importOseeTypes(IProgressMonitor monitor, boolean isInitializing, OseeImportModelRequest request, OseeImportModelResponse response) throws OseeCoreException;

   void exportOseeTypes(IProgressMonitor monitor, OutputStream outputStream) throws OseeCoreException;

}

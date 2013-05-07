/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs;

import java.io.OutputStream;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.OseeImportModelRequest;
import org.eclipse.osee.framework.core.model.OseeImportModelResponse;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsTypes {

   void importOseeTypes(boolean isInitializing, OseeImportModelRequest request, OseeImportModelResponse response) throws OseeCoreException;

   void exportOseeTypes(OutputStream outputStream) throws OseeCoreException;

}

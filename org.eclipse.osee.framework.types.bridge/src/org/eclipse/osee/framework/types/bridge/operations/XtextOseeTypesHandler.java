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
package org.eclipse.osee.framework.types.bridge.operations;

import java.net.URI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.importing.IOseeTypesHandler;

/**
 * @author Roberto E. Escobar
 */
public class XtextOseeTypesHandler implements IOseeTypesHandler {

   @Override
   public void execute(IProgressMonitor monitor, URI uri) throws OseeCoreException {
      try {
         IOperation operation = new OseeTypesImportOperation(uri, false, false, true);
         Operations.executeWorkAndCheckStatus(operation, monitor, -1);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public boolean isApplicable(String resource) {
      return Strings.isValid(resource) && resource.endsWith(".osee");
   }

}

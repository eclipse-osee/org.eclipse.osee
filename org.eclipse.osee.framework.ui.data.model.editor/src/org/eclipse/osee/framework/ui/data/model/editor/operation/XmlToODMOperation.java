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
package org.eclipse.osee.framework.ui.data.model.editor.operation;

import java.io.File;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;

/**
 * @author Roberto E. Escobar
 */
public class XmlToODMOperation {

   private final File[] resources;

   public XmlToODMOperation(File[] resources) {
      this.resources = resources;
   }

   public void execute(IProgressMonitor listener) throws OseeCoreException {
      try {

      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }
}

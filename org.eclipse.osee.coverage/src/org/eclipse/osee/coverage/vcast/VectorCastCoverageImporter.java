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
package org.eclipse.osee.coverage.vcast;

import java.io.File;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.coverage.ICoverageImporter;
import org.eclipse.osee.coverage.internal.CoveragePlugin;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class VectorCastCoverageImporter extends AbstractOperation implements ICoverageImporter {

   private final String vcastDirectory;
   private CoverageImport coverageImport;

   public VectorCastCoverageImporter(String vcastDirectory) {
      super("VectorCast Coverage Importer", CoveragePlugin.PLUGIN_ID);
      this.vcastDirectory = vcastDirectory;
   }

   @Override
   public CoverageImport run() {
      VectorCastCoverageImporter operation = new VectorCastCoverageImporter(vcastDirectory);
      Operations.executeAsJob(operation, true);
      IStatus status = operation.getStatus();
      if (!status.isOK()) {
         return coverageImport;
      }
      return null;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!Strings.isValid(vcastDirectory)) {
         throw new IllegalArgumentException("VectorCast directory must be specified");
      }
      File file = new File(vcastDirectory);
      if (!file.exists()) {
         throw new IllegalArgumentException(String.format("VectorCast directory doesn't exist [%s]", vcastDirectory));
      }
   }

}

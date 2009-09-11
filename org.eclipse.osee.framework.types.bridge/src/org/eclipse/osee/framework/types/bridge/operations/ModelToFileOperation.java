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

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.xtext.ui.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ModelToFileOperation extends AbstractOperation {
   private final Map<String, OseeTypeModel> models;
   private final File folder;

   public ModelToFileOperation(File folder, Map<String, OseeTypeModel> models) {
      super("Write Model", Activator.PLUGIN_ID);
      this.folder = folder;
      this.models = models;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!models.isEmpty()) {
         double workPercentage = 1.0 / models.size();
         for (Entry<String, OseeTypeModel> entry : models.entrySet()) {
            File file = new File(folder, entry.getKey() + ".osee");
            OseeTypeModelUtil.saveModel(file.toURI(), entry.getValue());
            monitor.worked(calculateWork(workPercentage));
         }
      }
   }
}

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
package org.eclipse.osee.coverage.editor;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.coverage.action.ConfigureCoverageMethodsAction;
import org.eclipse.osee.coverage.event.CoverageEventManager;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class LoadCoverageEditorOperation extends AbstractOperation {

   private final CoverageEditor editor;

   public LoadCoverageEditorOperation(CoverageEditor editor, String operationName) {
      super(operationName, Activator.PLUGIN_ID);
      this.editor = editor;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      CoverageEditorInput editorInput = editor.getCoverageEditorInput();
      Artifact coveragePackageArtifact = editorInput.getCoveragePackageArtifact();
      monitor.worked(calculateWork(0.10));

      @SuppressWarnings("unused")
      Collection<Artifact> artifactLoadCache = null;
      if (coveragePackageArtifact != null) {
         checkForCancelledStatus(monitor);
         artifactLoadCache = ConfigureCoverageMethodsAction.bulkLoadCoveragePackage(coveragePackageArtifact);
         // TODO Need to bulk load binary attributes also; Some Coverage Items are binary attributes
         // that are not bulk loaded with attributes.  This was mitigated by moving test units to separate table
         // and only referencing their ids in Coverage Items.
      }
      monitor.worked(calculateWork(0.50));

      checkForCancelledStatus(monitor);
      if (coveragePackageArtifact != null) {
         CoveragePackage coveragePackage = OseeCoveragePackageStore.get(coveragePackageArtifact);
         editorInput.setCoveragePackageBase(coveragePackage);

         checkForCancelledStatus(monitor);
         CoverageEventManager.instance.register(editor);
      }
      monitor.worked(calculateWork(0.20));

      editor.onLoadComplete();
      monitor.worked(calculateWork(0.20));
   }
};
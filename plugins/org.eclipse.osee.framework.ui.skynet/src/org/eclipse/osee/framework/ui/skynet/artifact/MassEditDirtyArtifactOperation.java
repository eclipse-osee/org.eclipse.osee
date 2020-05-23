/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IOperationFactory;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class MassEditDirtyArtifactOperation extends AbstractOperation implements IOperationFactory {

   public static final String NAME = "Mass Edit Dirty Artifacts";

   public MassEditDirtyArtifactOperation() {
      super(NAME, Activator.PLUGIN_ID);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Collection<Artifact> artifacts = ArtifactCache.getDirtyArtifacts();
      if (artifacts.isEmpty()) {
         AWorkbench.popup("No Dirty Artifacts Found");
         return;
      }
      HashCollection<BranchId, Artifact> branchMap = Artifacts.getBranchArtifactMap(artifacts);
      for (BranchId branch : branchMap.keySet()) {
         MassArtifactEditor.editArtifacts(String.format("Dirty Artifacts for Branch [%s]", branch),
            branchMap.getValues(branch));
      }
   }

   @Override
   public IOperation createOperation() {
      return this;
   }

}

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
package org.eclipse.osee.framework.skynet.core.importing.operations;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * Usually used as part of the import process to filter items found by the {@link RoughArtifactCollector}
 * {@link #collector}. Result fills the {@link #selectedArtifactTypes}.
 *
 * @author Roberto E. Escobar
 */
public class FilterArtifactTypesByAttributeTypes extends AbstractOperation {

   private final BranchId branch;
   private final Collection<ArtifactTypeToken> selectedArtifactTypes;
   private final RoughArtifactCollector collector;

   /**
    * @param branch
    * @param collector
    * @param selectedArtifactTypes ----> <b>MUTABLE</b> list of items {{@link #doWork(IProgressMonitor)} will operate
    * on.
    */
   public FilterArtifactTypesByAttributeTypes(BranchId branch, RoughArtifactCollector collector, Collection<ArtifactTypeToken> selectedArtifactTypes) {
      super("Filter Artifact Types", Activator.PLUGIN_ID);
      this.branch = branch;
      this.selectedArtifactTypes = selectedArtifactTypes;
      this.collector = collector;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Set<String> names = new HashSet<>();
      for (RoughArtifact artifact : collector.getRoughArtifacts()) {
         names.addAll(artifact.getAttributeTypeNames());
      }
      selectedArtifactTypes.clear();
      Set<AttributeTypeId> requiredTypes = new HashSet<>();
      for (String name : names) {
         requiredTypes.add(AttributeTypeManager.getType(name));
      }

      Branch fullBranch = BranchManager.getBranch(branch);
      for (ArtifactTypeToken artifactType : ArtifactTypeManager.getConcreteArtifactTypes(branch)) {
         Collection<? extends AttributeTypeId> attributeTypes =
            ArtifactTypeManager.getFullType(artifactType).getAttributeTypes(fullBranch);
         if (Collections.setComplement(requiredTypes, attributeTypes).isEmpty()) {
            selectedArtifactTypes.add(artifactType);
         }
      }
   }
}
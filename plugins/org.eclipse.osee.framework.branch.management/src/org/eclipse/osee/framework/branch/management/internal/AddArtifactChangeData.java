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

package org.eclipse.osee.framework.branch.management.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactChangeItem;
import org.eclipse.osee.framework.core.data.AttributeChangeItem;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.ChangeItemUtil;

public class AddArtifactChangeData extends AbstractOperation {
   private final List<ChangeItem> changeItems;

   public AddArtifactChangeData(List<ChangeItem> changeItems) {
      super("Add Extra Artifact Change Data", Activator.PLUGIN_ID);
      this.changeItems = changeItems;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Map<Integer, ArtifactChangeItem> artifactChanges = new HashMap<Integer, ArtifactChangeItem>();
      for (ChangeItem item : changeItems) {
         if (item instanceof ArtifactChangeItem) {
            ArtifactChangeItem artItem = (ArtifactChangeItem) item;
            artifactChanges.put(artItem.getArtId(), artItem);
         }
      }

      List<AttributeChangeItem> attrItems = new ArrayList<AttributeChangeItem>();
      Map<Integer, ArtifactChangeItem> syntheticArtifactChanges = new HashMap<Integer, ArtifactChangeItem>();
      for (ChangeItem item : changeItems) {
         if (item instanceof AttributeChangeItem) {
            AttributeChangeItem attributeChange = (AttributeChangeItem) item;
            Integer artIdToCheck = attributeChange.getArtId();

            ArtifactChangeItem artifactChange = artifactChanges.get(artIdToCheck);
            if (artifactChange == null) {
               artifactChange = syntheticArtifactChanges.get(artIdToCheck);
               if (artifactChange == null) {
                  artifactChange = new ArtifactChangeItem(artIdToCheck, -1, -1, null);
                  syntheticArtifactChanges.put(artIdToCheck, artifactChange);
               }
               attrItems.add(attributeChange);
               updateArtifactChangeItem(artifactChange, attributeChange);
            }
         }
      }
      changeItems.addAll(syntheticArtifactChanges.values());
   }

   private void updateArtifactChangeItem(ArtifactChangeItem artifact, AttributeChangeItem attribute) {
      try {
         if (!artifact.getBaselineVersion().isValid() && attribute.getBaselineVersion().isValid()) {
            ChangeItemUtil.copy(attribute.getBaselineVersion(), artifact.getBaselineVersion());
         }

         if (!artifact.getCurrentVersion().isValid() && attribute.getCurrentVersion().isValid()) {
            ChangeItemUtil.copy(attribute.getCurrentVersion(), artifact.getCurrentVersion());
         }

         if (!artifact.getDestinationVersion().isValid() && attribute.getDestinationVersion().isValid()) {
            ChangeItemUtil.copy(attribute.getDestinationVersion(), artifact.getDestinationVersion());
         }

         if (!artifact.getNetChange().isValid() && attribute.getNetChange().isValid()) {
            ChangeItemUtil.copy(attribute.getNetChange(), artifact.getNetChange());
            artifact.getNetChange().setModType(ModificationType.MODIFIED);
         }

      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }
}

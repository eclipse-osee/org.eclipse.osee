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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactChangeItem;
import org.eclipse.osee.framework.core.data.AttributeChangeItem;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;

public class AddArtifactChangeData extends AbstractOperation {
   private final List<ChangeItem> changeItems;

   public AddArtifactChangeData(List<ChangeItem> changeItems) {
      super("Add Extra Artifact Change Data", Activator.PLUGIN_ID);
      this.changeItems = changeItems;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      List<ChangeItem> newItems = new ArrayList<ChangeItem>();
      CompositeKeyHashMap<Integer, Integer, ArtifactChangeItem> artEntries =
            new CompositeKeyHashMap<Integer, Integer, ArtifactChangeItem>();
      for (ChangeItem item : changeItems) {
         if (item instanceof ArtifactChangeItem) {
            ArtifactChangeItem artItem = (ArtifactChangeItem) item;
            artEntries.put(artItem.getArtId(), artItem.getNetChange().getTransactionNumber(), artItem);
         }
      }
      for (ChangeItem item : changeItems) {
         if (item instanceof AttributeChangeItem) {
            AttributeChangeItem attrItem = (AttributeChangeItem) item;
            ChangeVersion attrItemNet = attrItem.getNetChange();
            ArtifactChangeItem artItem = artEntries.get(attrItem.getArtId(), attrItemNet.getTransactionNumber());
            if (artItem == null) {
               try {
                  artItem =
                        new ArtifactChangeItem(attrItemNet.getGammaId(), attrItemNet.getModType(),
                              attrItemNet.getTransactionNumber(), attrItem.getArtId());
                  ChangeVersion netVersion = artItem.getNetChange();
                  netVersion.setGammaId(attrItemNet.getGammaId());
                  netVersion.setModType(ModificationType.MODIFIED);
                  netVersion.setTransactionNumber(attrItemNet.getTransactionNumber());
                  netVersion.setValue("");
                  artEntries.put(artItem.getArtId(), attrItemNet.getTransactionNumber(), artItem);
                  newItems.add(artItem);
               } catch (Exception ex) {
                  ex.printStackTrace();
               }
            }
         }
      }
      changeItems.addAll(newItems);
   }
}

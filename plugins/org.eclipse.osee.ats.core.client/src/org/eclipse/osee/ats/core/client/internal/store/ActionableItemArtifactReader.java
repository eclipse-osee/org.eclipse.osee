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
package org.eclipse.osee.ats.core.client.internal.store;

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.IAtsCache;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactReader;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemArtifactReader implements IAtsArtifactReader<IAtsActionableItem> {

   private final IActionableItemFactory actionableItemFactory;

   public ActionableItemArtifactReader(IActionableItemFactory actionableItemFactory) {
      this.actionableItemFactory = actionableItemFactory;
   }

   @Override
   public IAtsActionableItem load(IAtsCache cache, Artifact aiArt) throws OseeCoreException {
      IAtsActionableItem aia =
         actionableItemFactory.createActionableItem(aiArt.getGuid(), aiArt.getName(), aiArt.getUuid());
      aia.setStoreObject(aiArt);
      cache.cacheAtsObject(aia);

      aia.setName(aiArt.getName());
      aia.setActive(aiArt.getSoleAttributeValue(AtsAttributeTypes.Active, false));
      aia.setAllowUserActionCreation(aiArt.getSoleAttributeValue(AtsAttributeTypes.AllowUserActionCreation, true));
      aia.setActionable(aiArt.getSoleAttributeValue(AtsAttributeTypes.Actionable, false));
      aia.setDescription(aiArt.getSoleAttributeValue(AtsAttributeTypes.Description, ""));
      return aia;
   }
}

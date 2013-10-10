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

import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactReader;
import org.eclipse.osee.ats.core.client.internal.config.AtsArtifactConfigCache;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.ats.core.config.IVersionFactory;
import org.eclipse.osee.framework.core.data.FullyNamedIdentity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsArtifactReader<T extends IAtsConfigObject> implements IAtsArtifactReader<T> {

   private final IActionableItemFactory actionableItemFactory;
   private final ITeamDefinitionFactory teamDefFactory;
   private final IVersionFactory versionFactory;

   protected AbstractAtsArtifactReader(IActionableItemFactory actionableItemFactory, ITeamDefinitionFactory teamDefFactory, IVersionFactory versionFactory) {
      super();
      this.actionableItemFactory = actionableItemFactory;
      this.teamDefFactory = teamDefFactory;
      this.versionFactory = versionFactory;
   }

   protected IAtsActionableItem getOrCreateActionableItem(AtsArtifactConfigCache cache, FullyNamedIdentity<String> artifact) throws OseeCoreException {
      Conditions.checkNotNull(artifact, "artifact");
      String guid = artifact.getGuid();
      IAtsActionableItem item = cache.getSoleByGuid(guid, IAtsActionableItem.class);
      if (item == null) {
         item = actionableItemFactory.createActionableItem(guid, artifact.getName());
         cache.cache(item);
      }
      return item;
   }

   protected IAtsTeamDefinition getOrCreateTeamDefinition(AtsArtifactConfigCache cache, FullyNamedIdentity<String> artifact) throws OseeCoreException {
      Conditions.checkNotNull(artifact, "artifact");
      String guid = artifact.getGuid();
      IAtsTeamDefinition item = cache.getSoleByGuid(guid, IAtsTeamDefinition.class);
      if (item == null) {
         item = teamDefFactory.createTeamDefinition(guid, artifact.getName());
         cache.cache(item);
      }
      return item;
   }

   protected IAtsVersion getOrCreateVersion(AtsArtifactConfigCache cache, Artifact artifact) throws OseeCoreException {
      Conditions.checkNotNull(artifact, "artifact");
      String guid = artifact.getGuid();
      IAtsVersion item = cache.getSoleByGuid(guid, IAtsVersion.class);
      if (item == null) {
         item = versionFactory.createVersion(artifact.getName(), guid);
         cache.cache(item);
      }
      return item;
   }

}

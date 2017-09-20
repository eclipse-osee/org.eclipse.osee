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
package org.eclipse.osee.ats.rest;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemService;
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.api.review.IAtsReviewServiceProvider;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactoryProvider;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.api.workflow.IAtsBranchServiceProvider;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.config.IAtsCacheProvider;
import org.eclipse.osee.ats.rest.util.IArtifactProvider;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G Dunne
 */
public interface IAtsServer extends IAtsServices, IAtsNotifier, IAtsConfigItemFactoryProvider, IAtsCacheProvider, IAtsReviewServiceProvider, IAtsBranchServiceProvider, IArtifactProvider {

   OrcsApi getOrcsApi() throws OseeCoreException;

   @Override
   ArtifactReadable getArtifactByGuid(String guid);

   Iterable<IAtsDatabaseConversion> getDatabaseConversions();

   @Override
   ArtifactReadable getArtifactByAtsId(String id);

   QueryBuilder getQuery();

   boolean isProduction();

   IAtsServices getServices();

   void setEmailEnabled(boolean emailEnabled);

   @Override
   ArtifactReadable getArtifact(Long uuid);

   @Override
   Collection<ArtifactToken> getArtifacts(Collection<Long> uuids);

   void addAtsDatabaseConversion(IAtsDatabaseConversion conversion);

   CustomizeData getCustomizationByGuid(String customize_guid);

   Collection<CustomizeData> getCustomizations(String namespace);

   Collection<CustomizeData> getCustomizationsGlobal(String namespace);

   AtsConfigEndpointApi getConfigurationEndpoint();

   @Override
   IAtsChangeSet createChangeSet(String string, IAtsUser systemUser);

   @Override
   IAtsTeamWorkflow getTeamWf(Long id);

   @Override
   IAtsActionableItemService getActionableItemService();

}
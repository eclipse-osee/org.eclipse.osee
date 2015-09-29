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
package org.eclipse.osee.ats.impl;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.review.IAtsReviewServiceProvider;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactoryProvider;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.util.IAtsUtilService;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionServiceProvider;
import org.eclipse.osee.ats.api.workflow.IAtsBranchServiceProvider;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemServiceProvider;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.core.config.IAtsConfigProvider;
import org.eclipse.osee.ats.core.util.IAtsActionFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G Dunne
 */
public interface IAtsServer extends IAtsServices, IAtsNotifier, IAtsConfigItemFactoryProvider, IAtsConfigProvider, IAtsReviewServiceProvider, IAtsBranchServiceProvider, IAtsWorkItemServiceProvider, IAtsWorkDefinitionServiceProvider, IArtifactProvider {

   OrcsApi getOrcsApi() throws OseeCoreException;

   @Override
   IAtsWorkItemFactory getWorkItemFactory() throws OseeCoreException;

   IAtsWorkDefinitionAdmin getWorkDefAdmin();

   ArtifactReadable getArtifactByGuid(String guid);

   @Override
   IAtsStoreService getStoreService();

   @Override
   IAtsConfigItemFactory getConfigItemFactory();

   IAtsStateFactory getStateFactory();

   IAtsLogFactory getLogFactory();

   Iterable<IAtsDatabaseConversion> getDatabaseConversions();

   IAtsUtilService getUtilService();

   ISequenceProvider getSequenceProvider();

   IAtsActionFactory getActionFactory();

   ArtifactReadable getArtifactByAtsId(String id);

   @Override
   ArtifactId getArtifactById(String id);

   QueryBuilder getQuery();

   boolean isProduction();

   String getConfigValue(String key);

   IAtsServices getServices();

   List<ArtifactReadable> getArtifactListByIds(String id);

   List<IAtsWorkItem> getWorkItemListByIds(String id);

   void setEmailEnabled(boolean emailEnabled);

   IAtsProgramService getProgramService();

   IAtsTeamDefinitionService getTeamDefinitionService();

   ArtifactReadable getArtifactByUuid(long uuid);

   IAgileService getAgileService();

   IAtsQueryService getQueryService();

   Collection<ArtifactReadable> getArtifacts(List<Long> uuids);

   void addAtsDatabaseConversion(IAtsDatabaseConversion conversion);

   @Override
   ArtifactReadable getArtifact(IArtifactToken token);

}
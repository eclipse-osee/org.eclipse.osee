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

import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.api.review.IAtsReviewServiceProvider;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactoryProvider;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.api.util.IAtsStoreFactory;
import org.eclipse.osee.ats.api.util.IAtsUtilService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionServiceProvider;
import org.eclipse.osee.ats.api.workflow.IAtsBranchServiceProvider;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemServiceProvider;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.core.config.IAtsConfigProvider;
import org.eclipse.osee.ats.core.util.AtsSequenceProvider;
import org.eclipse.osee.ats.core.util.IAtsActionFactory;
import org.eclipse.osee.ats.impl.internal.workitem.IArtifactProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Donald G Dunne
 */
public interface IAtsServer extends IAtsServices, IAtsNotifier, IAtsConfigItemFactoryProvider, IAtsConfigProvider, IAtsReviewServiceProvider, IAtsBranchServiceProvider, IAtsWorkItemServiceProvider, IAtsWorkDefinitionServiceProvider, IArtifactProvider {

   OrcsApi getOrcsApi() throws OseeCoreException;

   IAtsWorkItemFactory getWorkItemFactory() throws OseeCoreException;

   @Override
   ArtifactReadable getArtifact(IAtsObject atsObject) throws OseeCoreException;

   IAtsWorkDefinitionAdmin getWorkDefAdmin();

   ArtifactReadable getArtifactByGuid(String guid);

   IAtsStoreFactory getStoreFactory();

   @Override
   IAtsConfigItemFactory getConfigItemFactory();

   IAtsStateFactory getStateFactory();

   IAtsLogFactory getLogFactory();

   List<IAtsDatabaseConversion> getDatabaseConversions();

   IAtsUtilService getUtilService();

   AtsSequenceProvider getSequenceProvider();

   IAtsActionFactory getActionFactory();

   ArtifactReadable getArtifactByAtsId(String id);

   ArtifactReadable getArtifactById(String id);

   QueryBuilder getQuery();

   boolean isProduction();

   String getConfigValue(String key);

   IAtsServices getServices();

   List<ArtifactReadable> getArtifactListByIds(String id);

   List<IAtsWorkItem> getWorkItemListByIds(String id);

   String getAtsId(Object obj);

}
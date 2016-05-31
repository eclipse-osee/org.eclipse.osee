/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse  License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api;

import java.util.Collection;
import org.eclipse.osee.ats.api.column.IAtsColumnService;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.task.IAtsTaskService;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IArtifactResolver;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.util.IAtsUtilService;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsActionFactory;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsImplementerService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsServices {

   IRelationResolver getRelationResolver();

   IAttributeResolver getAttributeResolver();

   IAtsUserService getUserService();

   IAtsWorkItemService getWorkItemService();

   IAtsReviewService getReviewService();

   IAtsBranchService getBranchService();

   IAtsWorkDefinitionService getWorkDefService();

   IAtsVersionService getVersionService();

   ArtifactId getArtifact(Long uuid);

   ArtifactId getArtifact(ArtifactId artifact) throws OseeCoreException;

   ArtifactId getArtifact(IAtsObject atsObject) throws OseeCoreException;

   void setChangeType(IAtsObject atsObject, ChangeType changeType, IAtsChangeSet changes);

   ChangeType getChangeType(IAtsAction fromAction);

   String getAtsId(ArtifactId artifact);

   String getAtsId(IAtsObject atsObject);

   Collection<IArtifactType> getArtifactTypes();

   IAtsWorkItemFactory getWorkItemFactory();

   ArtifactId getArtifactById(String id);

   IAtsConfigItemFactory getConfigItemFactory();

   ArtifactId getArtifact(IArtifactToken token);

   IAtsStoreService getStoreService();

   <A extends IAtsConfigObject> A getSoleByUuid(long uuid, Class<A> clazz) throws OseeCoreException;

   Collection<ITransitionListener> getTransitionListeners();

   Collection<WidgetResult> validateWidgetTransition(IAtsWorkItem workItem, IAtsStateDefinition toStateDef);

   void clearImplementersCache(IAtsWorkItem workItem);

   IArtifactResolver getArtifactResolver();

   IAtsTaskService getTaskService();

   ArtifactId getArtifactByName(IArtifactType artifactType, String name);

   ArtifactId getArtifactByGuid(String workPackageGuid);

   IAtsProgramService getProgramService();

   IAtsQueryService getQueryService();

   IAtsEarnedValueService getEarnedValueService();

   AtsConfigurations getConfigurations();

   IAtsEarnedValueServiceProvider getEarnedValueServiceProvider();

   IAtsImplementerService getImplementerService();

   IAtsColumnService getColumnService();

   IAtsUtilService getUtilService();

   ISequenceProvider getSequenceProvider();

   IAtsActionFactory getActionFactory();

   String getConfigValue(String key);

}

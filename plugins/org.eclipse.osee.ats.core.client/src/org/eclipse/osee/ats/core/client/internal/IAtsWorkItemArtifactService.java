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
package org.eclipse.osee.ats.core.client.internal;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItemArtifactService {

   public abstract Artifact get(IAtsObject atsObject) throws OseeCoreException;

   public abstract <A extends Artifact> A get(IAtsWorkItem workItem, Class<?> clazz) throws OseeCoreException;

   public abstract <A extends Artifact> List<A> get(Collection<? extends IAtsWorkItem> workItems, Class<?> clazz) throws OseeCoreException;

   public abstract Collection<? extends IAtsWorkItem> getWorkItems(Collection<Artifact> arts);

   public abstract IAtsTeamWorkflow getParentTeamWorkflow(IAtsWorkItem workItem) throws OseeCoreException;

   public abstract IAtsTeamDefinition getTeamDefinition(IAtsWorkItem workItem) throws OseeCoreException;

}
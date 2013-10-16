/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.version;

import java.util.Collection;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsVersionService {

   boolean hasTargetedVersion(Object object) throws OseeCoreException;

   IAtsVersion getTargetedVersion(Object object) throws OseeCoreException;

   Collection<IAtsTeamWorkflow> getTargetedForTeamWorkflows(IAtsVersion version) throws OseeCoreException;

   IAtsVersion setTargetedVersion(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException;

   IAtsVersion setTargetedVersionAndStore(IAtsTeamWorkflow teamWf, IAtsVersion build) throws OseeCoreException;

   IAtsTeamDefinition getTeamDefinition(IAtsVersion version) throws OseeCoreException;

   void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef) throws OseeCoreException;

   boolean isReleased(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   boolean isVersionLocked(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   void removeTargetedVersion(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   IAtsVersion getById(Identity<String> id) throws OseeCoreException;

}

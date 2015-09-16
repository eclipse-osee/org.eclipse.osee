/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.config;

import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsClientVersionService extends IAtsVersionService {

   void removeTargetedVersion(IAtsTeamWorkflow teamWf, boolean store) throws OseeCoreException;

   IAtsVersion setTargetedVersionAndStore(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException;

   IAtsVersion setTargetedVersion(IAtsTeamWorkflow teamWf, IAtsVersion build) throws OseeCoreException;

   void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef) throws OseeCoreException;

   Branch getBranch(IAtsVersion version);

   IAtsVersion createVersion(String name, String guid, long uuid);

   IAtsVersion createVersion(String name);

   void invalidateVersionCache();

   void invalidateVersionCache(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   IAtsVersion store(IAtsVersion version, IAtsTeamDefinition teamDef);

}

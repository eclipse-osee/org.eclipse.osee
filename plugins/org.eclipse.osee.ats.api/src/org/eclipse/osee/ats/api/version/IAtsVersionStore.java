/*******************************************************************************
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
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsVersionStore {

   public IAtsTeamWorkflow removeTargetedVersionLink(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   public IAtsTeamWorkflow setTargetedVersionLink(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException;

   public IAtsVersion getTargetedVersion(Object object) throws OseeCoreException;

   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) throws OseeCoreException;

   public IAtsTeamDefinition getTeamDefinition(IAtsVersion version) throws OseeCoreException;

   public void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef) throws OseeCoreException;

   public IAtsVersion getById(Identity<String> id) throws OseeCoreException;

}

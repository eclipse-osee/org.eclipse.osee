/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/eplv10.html
 *
 * Contributors:
 *     Boeing  initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.config;

import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IAtsProgramClient extends IAtsProgram {

   public Artifact getArtifact() throws OseeCoreException;

   public void setArtifact(Artifact artifact) throws OseeCoreException;

   public String getProgramName() throws OseeCoreException;

   public IAtsTeamDefinition getTeamDefHoldingVersions() throws OseeCoreException;

   public String getOseeProgramName();
}

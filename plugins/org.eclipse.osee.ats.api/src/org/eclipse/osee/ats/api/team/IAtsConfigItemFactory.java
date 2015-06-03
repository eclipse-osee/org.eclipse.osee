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
package org.eclipse.osee.ats.api.team;

import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionFeature;
import org.eclipse.osee.ats.api.insertion.JaxNewInsertion;
import org.eclipse.osee.ats.api.insertion.JaxNewInsertionFeature;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfigItemFactory {

   IAtsTeamDefinition getTeamDef(Object artifact) throws OseeCoreException;

   IAtsActionableItem getActionableItem(Object aiArt) throws OseeCoreException;

   IAtsConfigObject getConfigObject(Object artifact) throws OseeCoreException;

   IAtsVersion getVersion(Object artifact) throws OseeCoreException;

   IAtsProgram getProgram(Object artifact);

   IAgileTeam getAgileTeam(Object artifact);

   IAgileFeatureGroup getAgileFeatureGroup(Object artifact);

   IAtsInsertion getInsertion(ArtifactId artifact);

   IAtsInsertion createInsertion(ArtifactId programArtifact, JaxNewInsertion newInsertion);

   IAtsInsertion updateInsertion(JaxNewInsertion newInsertion);

   void deleteInsertion(ArtifactId artifact);

   IAtsInsertionFeature getInsertionFeature(ArtifactId artifact);

   IAtsInsertionFeature createInsertionFeature(ArtifactId insertion, JaxNewInsertionFeature newFeature);

   IAtsInsertionFeature updateInsertionFeature(JaxNewInsertionFeature newFeature);

   void deleteInsertionFeature(ArtifactId artifact);

}

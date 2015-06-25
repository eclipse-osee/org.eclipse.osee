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

import java.util.List;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.insertion.JaxNewInsertion;
import org.eclipse.osee.ats.api.insertion.JaxNewInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfigItemFactory {

   IAtsTeamDefinition getTeamDef(ArtifactId artifact) throws OseeCoreException;

   IAtsActionableItem getActionableItem(ArtifactId artifact) throws OseeCoreException;

   IAtsConfigObject getConfigObject(ArtifactId artifact) throws OseeCoreException;

   IAtsVersion getVersion(ArtifactId artifact) throws OseeCoreException;

   IAtsProgram getProgram(ArtifactId artifact);

   IAgileTeam getAgileTeam(ArtifactId artifact);

   IAgileFeatureGroup getAgileFeatureGroup(ArtifactId artifact);

   IAtsInsertion getInsertion(ArtifactId artifact);

   IAtsInsertion createInsertion(ArtifactId programArtifact, JaxNewInsertion newInsertion);

   IAtsInsertion updateInsertion(JaxNewInsertion newInsertion);

   void deleteInsertion(ArtifactId artifact);

   IAtsInsertionActivity getInsertionActivity(ArtifactId artifact);

   IAtsInsertionActivity createInsertionActivity(ArtifactId insertion, JaxNewInsertionActivity newActivity);

   IAtsInsertionActivity updateInsertionActivity(JaxNewInsertionActivity newFeature);

   void deleteInsertionActivity(ArtifactId artifact);

   boolean isAtsConfigArtifact(ArtifactId artifact);

   List<IArtifactType> getAtsConfigArtifactTypes();

}

/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config.tx;

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.Csci;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfigTxActionableItem {

   IAtsConfigTxActionableItem createChildActionableItem(IAtsActionableItemArtifactToken teamDef);

   IAtsConfigTxActionableItem and(AttributeTypeToken attrType, Object value);

   IAtsConfigTxActionableItem createChildActionableItem(String name);

   IAtsConfigTxActionableItem andActionable(boolean actionable);

   IAtsConfigTxActionableItem andTeamDef(IAtsTeamDefinitionArtifactToken teamDef);

   IAtsConfigTxActionableItem andTeamDef(String name);

   IAtsConfigTxActionableItem andActive(boolean active);

   IAtsConfigTxActionableItem andAccessContextId(String contextId);

   IAtsActionableItem getAi();

   IAtsConfigTxActionableItem andChildAis(String... aiNames);

   IAtsConfigTxActionableItem andChildAis(IAtsActionableItemArtifactToken... ais);

   IAtsConfigTxActionableItem andWorkType(WorkType workType);

   IAtsConfigTxActionableItem andProgram(IAtsProgramArtifactToken program);

   IAtsConfigTxActionableItem andCsci(Csci... cscis);

}

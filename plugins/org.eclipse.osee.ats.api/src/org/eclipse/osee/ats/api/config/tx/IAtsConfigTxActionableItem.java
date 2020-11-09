/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.config.tx;

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.Csci;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAccessContextId;

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

   IAtsActionableItem getAi();

   IAtsConfigTxActionableItem andChildAis(String... aiNames);

   IAtsConfigTxActionableItem andChildAis(IAtsActionableItemArtifactToken... ais);

   IAtsConfigTxActionableItem andWorkType(WorkType workType);

   IAtsConfigTxActionableItem andProgram(IAtsProgramArtifactToken program);

   IAtsConfigTxActionableItem andCsci(Csci... cscis);

   IAtsConfigTxActionableItem andAccessContexts(IAccessContextId... accessContexts);

}

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
package org.eclipse.osee.ats.api.workdef;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkDefinitionDslService {

   String getStorageString(IAtsWorkDefinition workDef, XResultData resultData);

   IAtsWorkDefinition getWorkDefinition(Long id, String workDefinitionDsl);

   IAtsWorkDefinition copyWorkDefinition(String newName, IAtsWorkDefinition workDef, XResultData resultData);

   List<IAtsRuleDefinition> getRuleDefinitions(String ruleDefintionsDslStr);

}

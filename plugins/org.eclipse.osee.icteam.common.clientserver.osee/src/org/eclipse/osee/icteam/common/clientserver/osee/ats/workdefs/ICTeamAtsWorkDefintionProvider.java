/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.common.clientserver.osee.ats.workdefs;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProvider;
/**
 * The class provides Work definitions for ICTeam 
 * 
 * @author Ajay Chandrahasan
 */
public class ICTeamAtsWorkDefintionProvider implements IAtsWorkDefinitionProvider {

	@Override
	public Collection<IAtsWorkDefinition> getWorkDefinitions() {
		
		return Arrays.asList(new WorkDefiCTeam().build());
	}

}

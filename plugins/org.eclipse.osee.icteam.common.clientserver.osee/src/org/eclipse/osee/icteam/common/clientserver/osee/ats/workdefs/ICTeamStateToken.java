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

import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * The class creates StateTokens or objects which hold the states for ICTeam processing
 * 
 * @author Ajay Chandrahasan
 */
public class ICTeamStateToken extends NamedIdBase {

   public static StateToken New = StateToken.valueOf(593820494L, "New");
   public static StateToken InProgress = StateToken.valueOf(593820495L, "In Progress");
}

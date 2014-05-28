/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.util;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;

/**
 * @author Donald G. Dunne
 */
public interface IAtsUtilService {

   void setAtsId(ISequenceProvider sequenceProvider, IAtsObject newObject, IAtsTeamDefinition teamDef);

   String getNextAtsId(ISequenceProvider sequenceProvider, IAtsObject newObject, IAtsTeamDefinition teamDef);

}

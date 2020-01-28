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

import org.eclipse.osee.ats.api.config.Csci;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfigTxProgram {

   IAtsConfigTxProgram andActive(boolean active);

   IAtsConfigTxProgram and(AttributeTypeToken attrType, Object value);

   IAtsConfigTxProgram andTeamDef(IAtsTeamDefinitionArtifactToken teamDef);

   IAtsConfigTxProgram andCsci(Csci... cscis);

   IAtsProgram getProgram();
}

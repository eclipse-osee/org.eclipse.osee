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

import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfigTxVersion {

   IAtsVersion getVersion();

   IAtsConfigTxVersion andAllowCreate();

   IAtsConfigTxVersion andAllowCommit();

   IAtsConfigTxVersion andRelation(RelationTypeSide relAtype, ArtifactToken artifact);

   IAtsConfigTxVersion andProgram(ArtifactId programId);

}

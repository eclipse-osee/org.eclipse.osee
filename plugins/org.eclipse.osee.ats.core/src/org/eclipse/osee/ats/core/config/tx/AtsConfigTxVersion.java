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

package org.eclipse.osee.ats.core.config.tx;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTx;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTxVersion;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigTxVersion extends AbstractAtsConfigTxObject<IAtsConfigTxVersion> implements IAtsConfigTxVersion {

   public IAtsVersion version;

   public AtsConfigTxVersion(IAtsObject atsObject, AtsApi atsApi, IAtsChangeSet changes, IAtsConfigTx cfgTx) {
      super(atsObject, atsApi, changes, cfgTx);
      Conditions.assertTrue(atsObject instanceof IAtsVersion, "AtsObject must be of type IAtsVersion");
      version = (IAtsVersion) atsObject;
   }

   @Override
   public IAtsVersion getVersion() {
      return version;
   }

   @Override
   public IAtsConfigTxVersion andAllowCreate() {
      changes.setSoleAttributeValue(version, AtsAttributeTypes.AllowCreateBranch, true);
      return this;
   }

   @Override
   public IAtsConfigTxVersion andAllowCommit() {
      changes.setSoleAttributeValue(version, AtsAttributeTypes.AllowCommitBranch, true);
      return this;
   }

   @Override
   public IAtsConfigTxVersion andProgram(ArtifactId programId) {
      changes.setSoleAttributeValue(version, AtsAttributeTypes.ProgramId, ArtifactId.valueOf(programId));
      return this;
   }

}

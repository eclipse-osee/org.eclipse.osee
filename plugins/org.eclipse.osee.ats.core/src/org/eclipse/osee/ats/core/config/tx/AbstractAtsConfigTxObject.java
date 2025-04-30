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
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author Donald G. Dunne
 */
public class AbstractAtsConfigTxObject<T> {

   protected final IAtsObject atsObject;
   protected final AtsApi atsApi;
   protected final IAtsChangeSet changes;
   protected final IAtsConfigTx cfgTx;

   public AbstractAtsConfigTxObject(IAtsObject atsObject, AtsApi atsApi, IAtsChangeSet changes, IAtsConfigTx cfgTx) {
      this.atsObject = atsObject;
      this.atsApi = atsApi;
      this.changes = changes;
      this.cfgTx = cfgTx;
   }

   @SuppressWarnings("unchecked")
   public T and(AttributeTypeToken attrType, Object value) {
      changes.setSoleAttributeValue(atsObject, attrType, value);
      return (T) this;
   }

   @SuppressWarnings("unchecked")
   public T andActive(boolean active) {
      changes.setSoleAttributeValue(atsObject, AtsAttributeTypes.Active, String.valueOf(active));
      return (T) this;
   }

   @SuppressWarnings("unchecked")
   public T andRelation(RelationTypeSide relAtype, ArtifactToken artifact) {
      changes.relate(atsObject, relAtype, artifact);
      return (T) this;
   }

}

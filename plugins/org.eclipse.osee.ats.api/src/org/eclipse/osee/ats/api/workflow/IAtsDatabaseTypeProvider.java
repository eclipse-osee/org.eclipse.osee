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

package org.eclipse.osee.ats.api.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.config.AtsAttrValCol;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;

/**
 * @author Jeremy A. Midvidy
 * @author Donald G. Dunne
 */
public interface IAtsDatabaseTypeProvider {

   public boolean useFactory();

   public AttributeTypeEnum<?> getPrioirtyAttrType();

   public Collection<ChangeType> getChangeTypeValues();

   AtsAttrValCol getPrioirtyColumnToken();

   default public String[] getChangeTypeArray() {
      List<String> values = new ArrayList<>();
      for (ChangeType type : getChangeTypeValues()) {
         values.add(type.name());
      }
      return values.toArray(new String[values.size()]);
   }

}

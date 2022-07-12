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

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.config.AtsAttrValCol;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;

/**
 * @author Jeremy A. Midvidy
 * @author Donald G. Dunne
 */
public interface IAtsDatabaseTypeProvider {

   public boolean useFactory();

   public AttributeTypeEnum<?> getPriorityAttrType();

   AtsAttrValCol getPriorityColumnToken();

   /**
    * @return Default ChangeType values for whole database
    */
   default public List<ChangeTypes> getChangeTypeValues() {
      return Collections.emptyList();
   }

}

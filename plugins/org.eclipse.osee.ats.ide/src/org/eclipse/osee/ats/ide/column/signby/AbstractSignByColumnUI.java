/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.ats.ide.column.signby;

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * Handles the Sign-by side of a signby/signbydate pair
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractSignByColumnUI extends AbstractSignByAndDateColumnUI {

   public AbstractSignByColumnUI(AttributeTypeToken byAttrType, AttributeTypeToken dateAttrType) {
      super(byAttrType, dateAttrType);
      Conditions.assertTrue(byAttrType.isLong(), "Attribute type must be User Art Id Long for %s",
         byAttrType.toStringWithId());
   }

}

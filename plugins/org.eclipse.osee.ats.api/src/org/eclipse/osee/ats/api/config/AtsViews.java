/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.api.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class AtsViews {
   private final List<AtsAttributeValueColumn> attrColumns = new ArrayList<>();

   public List<AtsAttributeValueColumn> getAttrColumns() {
      return attrColumns;
   }

}

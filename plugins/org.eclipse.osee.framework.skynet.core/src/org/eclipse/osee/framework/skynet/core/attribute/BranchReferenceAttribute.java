/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Ryan D. Brooks
 */
public class BranchReferenceAttribute extends IdentityReferenceAttribute {
   @Override
   public BranchId convertStringToValue(String value) {
      return BranchId.valueOf(value);
   }
}

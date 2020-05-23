/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.model.mocks;

import org.eclipse.osee.framework.core.model.AbstractOseeType;

/**
 * @author Roberto E. Escobar
 */
public class MockAbstractOseeType extends AbstractOseeType {

   public MockAbstractOseeType(Long guid, String name) {
      super(guid, name);
   }
}

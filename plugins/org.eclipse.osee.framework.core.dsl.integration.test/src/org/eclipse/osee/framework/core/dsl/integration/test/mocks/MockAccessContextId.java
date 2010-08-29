/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.integration.test.mocks;

import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author Roberto E. Escobar
 */
public final class MockAccessContextId extends NamedIdentity implements AccessContextId {
   public MockAccessContextId(String guid, String name) {
      super(guid, name);
   }
}
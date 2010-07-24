/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.test.internal;

import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.lifecycle.IAccessCheckProvider;

public class MockAccessCheckProvider implements IAccessCheckProvider {

   @Override
   public boolean canEdit(IBasicArtifact<?> user, IBasicArtifact<?> artTcheck) {
      return false;
   }

}

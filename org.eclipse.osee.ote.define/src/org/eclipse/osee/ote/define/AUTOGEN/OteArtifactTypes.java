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
package org.eclipse.osee.ote.define.AUTOGEN;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;

public class OteArtifactTypes extends NamedIdentity implements IArtifactType {
   public static final OteArtifactTypes TEST_RUN = new OteArtifactTypes("AAMFDjqDHWo+orlSpaQA", Requirements.TEST_RUN);
   public static final OteArtifactTypes TEST_SCRIPT =
         new OteArtifactTypes("AAMFDikEi0TGK27TKPgA", Requirements.TEST_CASE);

   private OteArtifactTypes(String guid, String name) {
      super(guid, name);
   }
}
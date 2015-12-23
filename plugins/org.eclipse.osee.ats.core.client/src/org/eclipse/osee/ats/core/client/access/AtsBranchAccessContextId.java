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
package org.eclipse.osee.ats.core.client.access;

import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Donald G. Dunne
 */
public final class AtsBranchAccessContextId {

   public static final IAccessContextId DENY_CONTEXT =
      TokenFactory.createAccessContextId("ABcgU0QxFG_cQU4Ph1wA", "ats.branchobject.deny");

   private AtsBranchAccessContextId() {
      // Branch Object Contexts;
   }
}

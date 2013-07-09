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
package org.eclipse.osee.orcs.core.internal.session;

import org.eclipse.osee.framework.core.data.AbstractIdentity;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public class OrcsSessionImpl extends AbstractIdentity<String> implements OrcsSession {

   private final String sessionId;

   public OrcsSessionImpl(String sessionId) {
      super();
      this.sessionId = sessionId;
   }

   @Override
   public String getGuid() {
      return sessionId;
   }

}

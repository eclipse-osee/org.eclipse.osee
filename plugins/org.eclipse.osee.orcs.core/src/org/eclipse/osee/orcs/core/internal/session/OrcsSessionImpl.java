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

package org.eclipse.osee.orcs.core.internal.session;

import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.orcs.OrcsSession;

/**
 * @author Roberto E. Escobar
 */
public class OrcsSessionImpl extends BaseIdentity<String> implements OrcsSession {
   public OrcsSessionImpl(String sessionId) {
      super(sessionId);
   }
}
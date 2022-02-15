/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.server.ide.api.client.model;

import java.util.LinkedList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.data.IdeClientSession;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class Sessions {

   public List<IdeClientSession> sessions = new LinkedList<>();

   public void add(IdeClientSession clientSession) {
      sessions.add(clientSession);
   }

   public List<IdeClientSession> get() {
      return sessions;
   }

   @Override
   public String toString() {
      return "Sessions [sessions=" + sessions + "]";
   }

}

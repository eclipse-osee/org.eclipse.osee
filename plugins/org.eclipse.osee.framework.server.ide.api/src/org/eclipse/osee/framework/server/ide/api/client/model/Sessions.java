/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.server.ide.api.client.model;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
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

}

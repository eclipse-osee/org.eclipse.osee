/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.world;

import org.eclipse.osee.ats.rest.IAtsServer;

/**
 * @author Donald G. Dunne
 */
public class WorldView {

   private final String name;
   private final IAtsServer atsServer;

   public WorldView(String name, IAtsServer atsServer) {
      this.name = name;
      this.atsServer = atsServer;
   }
}

/*******************************************************************************
* Copyright (c) 2019 Boeing.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Boeing - initial API and implementation
*******************************************************************************/
package org.eclipse.osee.doors.connector.ui.handler;

import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProviderBase;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;

/**
 * @author Ryan D. Brooks
 */
public final class DoorsTypeTokenProvider extends OrcsTypeTokenProviderBase {
   private static final NamespaceToken DOORS =
      NamespaceToken.valueOf(7, "doors", "Namespace for doors system and content management types");

   public static final OrcsTypeTokens doors = new OrcsTypeTokens(DOORS);

   public DoorsTypeTokenProvider() {
      super(doors);
      loadClasses(DoorsOseeTypes.DoorReqId);
   }
}
/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access;

import org.eclipse.osee.framework.core.access.context.AccessType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class AccessTypeResult {

   AccessType accessType = null;
   AccessTypeMatch match = AccessTypeMatch.NotComputed;

   public AccessTypeResult() {
   }

   public void logToResults(XResultData results) {
      results.logf("%s - %s\n", match.name(), accessType.toString());
   }

   public AccessType getAccessType() {
      return accessType;
   }

   public void setAccessType(AccessType accessType) {
      this.accessType = accessType;
   }

   public AccessTypeMatch getMatch() {
      return match;
   }

   public void setMatch(AccessTypeMatch match) {
      this.match = match;
   }

}

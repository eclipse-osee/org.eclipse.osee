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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.access.context.AccessContext;
import org.eclipse.osee.framework.core.access.context.AccessType;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class AccessContextResult {

   AccessContextToken contextId = null;
   AccessContext context = null;
   List<AccessType> accessTypes = new ArrayList<>();
   Map<AccessType, AccessTypeMatch> accessTypeToMatch = new HashMap<>();

   public AccessContextResult() {
   }

   public AccessContextToken getContextId() {
      return contextId;
   }

   public void setContextId(AccessContextToken contextId) {
      this.contextId = contextId;
   }

   public AccessContext getContext() {
      return context;
   }

   public void setContext(AccessContext context) {
      this.context = context;
   }

   public void logToResults(XResultData results) {
      results.logf("%s\n", contextId.toStringWithId());
      for (AccessType accessType : accessTypes) {
         results.logf("%s - %s\n", Lib.padTrailing(getAccessTypeMatchOrNotComputed(accessType).getName(), ' ', 7),
            accessType.toString());
      }
   }

   private AccessTypeMatch getAccessTypeMatch(AccessType accessType) {
      return accessTypeToMatch.get(accessType);
   }

   private AccessTypeMatch getAccessTypeMatchOrNotComputed(AccessType accessType) {
      AccessTypeMatch match = getAccessTypeMatch(accessType);
      if (match == null) {
         match = AccessTypeMatch.NotComputed;
      }
      return match;
   }

   public List<AccessType> getAccessTypes() {
      return accessTypes;
   }

   public void setAccessTypes(List<AccessType> accessTypes) {
      this.accessTypes = accessTypes;
   }

   public void setAccessTypeMatch(AccessType accessType, AccessTypeMatch match) {
      this.accessTypeToMatch.put(accessType, match);
   }
}

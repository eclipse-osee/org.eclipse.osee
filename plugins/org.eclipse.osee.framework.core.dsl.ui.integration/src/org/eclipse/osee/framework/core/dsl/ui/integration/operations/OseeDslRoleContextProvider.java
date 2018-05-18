/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.ui.integration.operations;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.dsl.integration.RoleContextProvider;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.Role;
import org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author John R. Misinco
 */
public class OseeDslRoleContextProvider implements RoleContextProvider {

   private final OseeDslProvider dslProvider;

   public OseeDslRoleContextProvider(OseeDslProvider dslProvider) {
      this.dslProvider = dslProvider;
   }

   @Override
   public Collection<? extends IAccessContextId> getContextId(ArtifactToken user) {
      OseeDsl dsl = null;
      try {
         dsl = dslProvider.getDsl();
      } catch (OseeCoreException ex) {
         OseeLog.log(OseeDslRoleContextProvider.class, Level.WARNING, Lib.exceptionToString(ex));
         return Collections.emptyList();
      }

      Collection<IAccessContextId> toReturn = Collections.emptyList();
      EList<Role> roleDeclarations = dsl.getRoleDeclarations();

      if (!roleDeclarations.isEmpty()) {

         //find which roles have the relevant guids
         List<Long> applicableIds;
         try {
            applicableIds = getApplicableIds(user);
         } catch (OseeCoreException ex) {
            OseeLog.log(OseeDslRoleContextProvider.class, Level.SEVERE, Lib.exceptionToString(ex));
            return Collections.emptyList();
         }

         //now go through roles and roll up the inherited roles
         Set<Role> applicableRoles = getApplicableRoles(dsl.getRoleDeclarations(), applicableIds);

         if (!applicableRoles.isEmpty()) {
            Map<String, Long> accessContextMap = new HashMap<>();
            for (AccessContext ac : dsl.getAccessDeclarations()) {
               Long id = null;
               if (Strings.isNumeric(ac.getId())) {
                  id = Long.valueOf(ac.getId());
               } else if (GUID.isValid(ac.getId())) {
                  id = dslProvider.getContextGuidToIdMap().get(ac.getId());
                  if (id == null) {
                     throw new OseeArgumentException("Can't find AccessContextId for id [%s] in AccessIdMap artifact.",
                        ac.getId());
                  }
               } else {
                  throw new OseeArgumentException("Invalid AccessContext id [%s]");
               }
               accessContextMap.put(ac.getName(), Long.valueOf(ac.getId()));
            }

            //now get the context id's
            toReturn = new HashSet<>();
            for (Role role : applicableRoles) {
               for (ReferencedContext ref : role.getReferencedContexts()) {
                  String contextName = ref.getAccessContextRef();
                  toReturn.add(IAccessContextId.valueOf(accessContextMap.get(contextName), contextName));
               }
            }
         }
      }
      return toReturn;
   }

   private Set<Role> getApplicableRoles(List<Role> roles, List<Long> applicableIds) {
      Queue<Role> applicableRoles = new LinkedList<>();
      for (Role role : roles) {
         for (UsersAndGroups uag : role.getUsersAndGroups()) {
            String userOrGroupIdStr = uag.getUserOrGroupId();
            Long userOrGroupId = null;
            if (Strings.isNumeric(userOrGroupIdStr)) {
               userOrGroupId = Long.valueOf(userOrGroupIdStr);
            } else if (GUID.isValid(userOrGroupIdStr)) {
               userOrGroupId = dslProvider.getContextGuidToIdMap().get(userOrGroupIdStr);
               if (userOrGroupId == null) {
                  throw new OseeArgumentException("Can't find UserOrGroupId for guid [%s] in AccessIdMap artifact.",
                     userOrGroupIdStr);
               }
            } else {
               throw new OseeArgumentException("Invalid UserOrGroupId id [%s]");
            }
            if (applicableIds.contains(userOrGroupId) && !applicableRoles.contains(role)) {
               applicableRoles.add(role);
               break;
            }
         }
      }

      Set<Role> includesInherited = new HashSet<>();

      getSuperRoles(includesInherited, applicableRoles);
      return includesInherited;
   }

   private List<Long> getApplicableIds(ArtifactToken user) {
      List<Long> applicableIds = new LinkedList<>();
      List<Artifact> groups = Collections.emptyList();

      try {
         Artifact artifact;
         if (user instanceof Artifact) {
            artifact = (Artifact) user;
         } else {
            artifact = ArtifactQuery.getArtifactFromToken(user);
         }
         groups = artifact.getRelatedArtifacts(CoreRelationTypes.Users_Artifact);
      } catch (OseeCoreException ex) {
         OseeLog.log(OseeDslRoleContextProvider.class, Level.SEVERE, Lib.exceptionToString(ex));
      }

      Long userId = user.getId();
      applicableIds.add(userId);
      for (Artifact group : groups) {
         applicableIds.add(group.getId());
      }
      return applicableIds;
   }

   private void getSuperRoles(Set<Role> visited, Queue<Role> toVisit) {
      if (!toVisit.isEmpty()) {
         Role role = toVisit.remove();
         visited.add(role);
         toVisit.addAll(role.getSuperRoles());
         getSuperRoles(visited, toVisit);
      }
   }

   @Override
   public Map<String, Long> getContextGuidToIdMap() {
      return dslProvider.getContextGuidToIdMap();
   }
}

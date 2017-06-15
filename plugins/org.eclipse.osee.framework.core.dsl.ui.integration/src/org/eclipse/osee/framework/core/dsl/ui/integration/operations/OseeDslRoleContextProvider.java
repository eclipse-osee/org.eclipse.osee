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
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.dsl.integration.RoleContextProvider;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.Role;
import org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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
         List<String> applicableGuids;
         try {
            applicableGuids = getApplicableGuids(user);
         } catch (OseeCoreException ex) {
            OseeLog.log(OseeDslRoleContextProvider.class, Level.SEVERE, Lib.exceptionToString(ex));
            return Collections.emptyList();
         }

         //now go through roles and roll up the inherited roles
         Set<Role> applicableRoles = getApplicableRoles(dsl.getRoleDeclarations(), applicableGuids);

         if (!applicableRoles.isEmpty()) {
            Map<String, String> accessContextMap = new HashMap<>();
            for (AccessContext ac : dsl.getAccessDeclarations()) {
               accessContextMap.put(ac.getName(), ac.getGuid());
            }

            //now get the context id's
            toReturn = new HashSet<>();
            for (Role role : applicableRoles) {
               for (ReferencedContext ref : role.getReferencedContexts()) {
                  String contextName = ref.getAccessContextRef();
                  toReturn.add(TokenFactory.createAccessContextId(accessContextMap.get(contextName), contextName));
               }
            }
         }
      }
      return toReturn;
   }

   private Set<Role> getApplicableRoles(List<Role> roles, List<String> applicableGuids) {
      Queue<Role> applicableRoles = new LinkedList<>();
      for (Role role : roles) {
         for (UsersAndGroups uag : role.getUsersAndGroups()) {
            if (applicableGuids.contains(uag.getUserOrGroupGuid()) && !applicableRoles.contains(role)) {
               applicableRoles.add(role);
               break;
            }
         }
      }

      Set<Role> includesInherited = new HashSet<>();
      getSuperRoles(includesInherited, applicableRoles);
      return includesInherited;
   }

   private List<String> getApplicableGuids(ArtifactToken user) {
      List<String> applicableGuids = new LinkedList<>();
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

      String userGuid = user.getGuid();
      applicableGuids.add(userGuid);
      for (Artifact group : groups) {
         applicableGuids.add(group.getGuid());
      }
      return applicableGuids;
   }

   private void getSuperRoles(Set<Role> visited, Queue<Role> toVisit) {
      if (!toVisit.isEmpty()) {
         Role role = toVisit.remove();
         visited.add(role);
         toVisit.addAll(role.getSuperRoles());
         getSuperRoles(visited, toVisit);
      }
   }
}

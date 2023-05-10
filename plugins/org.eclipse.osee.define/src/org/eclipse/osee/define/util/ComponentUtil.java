/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.define.util;

import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.Allocation_Component;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */
public class ComponentUtil {
   private final BranchId branchId;
   private final OrcsApi orcsApi;
   private ArtifactReadable mpCsci;
   private Collection<ArtifactReadable> mpComponents;
   private final AtomicBoolean wasLoaded;

   public ComponentUtil(BranchId branchId, OrcsApi providedOrcs) {
      super();
      this.branchId = branchId;
      this.mpComponents = null;
      this.wasLoaded = new AtomicBoolean(false);
      this.mpCsci = null;
      this.orcsApi = providedOrcs;
   }

   private void ensureLoaded() {

      if (wasLoaded.get()) {
         return;
      }

      synchronized (this) {

         if (wasLoaded.get()) {
            return;
         }

         wasLoaded.set(true);

         mpCsci =
            orcsApi.getQueryFactory().fromBranch(branchId).andTypeEquals(CoreArtifactTypes.Component).andNameEquals(
               "MP CSCI").getResults().getExactlyOne();

         mpComponents = mpCsci.getDescendants();
      }
   }

   public String getQualifiedComponentName(ArtifactReadable component) {
      ensureLoaded();
      if (component.getParent().equals(mpCsci)) {
         return component.getName();
      }
      return component.getParent().getName() + "." + component.getName();
   }

   public String getQualifiedComponentNames(ArtifactReadable requirement) {
      ensureLoaded();
      ResultSet<ArtifactReadable> components = requirement.getRelated(Allocation_Component);

      StringBuilder strB = new StringBuilder(20);

      for (ArtifactReadable component : components) {
         if (mpComponents.contains(component)) {
            strB.append(getQualifiedComponentName(component));
            strB.append(", ");
         }
      }
      return strB.length() == 0 ? null : strB.substring(0, strB.length() - 2);
   }

   public Collection<ArtifactReadable> getComponents() {
      ensureLoaded();
      return mpComponents;
   }
}

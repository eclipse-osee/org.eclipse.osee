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

package org.eclipse.osee.ote.ui.define.dialogs;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.ote.define.artifacts.ArtifactTestRunOperator;
import org.eclipse.osee.ote.ui.define.panels.IOverrideHandler;

/**
 * @author Roberto E. Escobar
 */
public class OverrideInvalidScriptRevisions implements IOverrideHandler {

   @Override
   public String getText() {
      return "Allow Invalid Script Revisions.";
   }

   @Override
   public String getToolTipText() {
      return "Allows invalid script revisions to be committed.\nWARNING: Duplicate commits are not overridable.";
   }

   @Override
   public Set<Object> getOverridableFromUnselectable(Set<Object> unselectable) {
      Set<Object> toReturn = new HashSet<>();
      for (Object object : unselectable) {
         if (object instanceof Artifact) {
            ArtifactTestRunOperator operator = new ArtifactTestRunOperator((Artifact) object);
            if (operator.hasNotBeenCommitted() != false) {
               toReturn.add(object);
            }
         }
      }
      return toReturn;
   }
}

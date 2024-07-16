/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;

/**
 * @author Donald G. Dunne
 */
public final class PromptChangeUtil {

   private PromptChangeUtil() {
      // Utility class
   }

   public static boolean promptChangeAttributeWI(final Collection<? extends IAtsWorkItem> workItems, AttributeTypeToken attributeType, boolean persist) {
      List<AbstractWorkflowArtifact> awas = new LinkedList<>();
      for (IAtsWorkItem workItem : workItems) {
         awas.add((AbstractWorkflowArtifact) workItem.getStoreObject());
      }
      return ArtifactPromptChange.promptChangeAttribute(attributeType, awas, persist);
   }

   public static boolean promptChangeAttribute(final Collection<? extends AbstractWorkflowArtifact> awas, AttributeTypeToken attributeType, boolean persist) {
      return ArtifactPromptChange.promptChangeAttribute(attributeType, awas, persist);
   }

   public static boolean promptChangeAttribute(AbstractWorkflowArtifact sma, AttributeTypeToken attributeType, final boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, Arrays.asList(sma), persist);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType, boolean persist) {
      if (workItem.getStoreObject() instanceof AbstractWorkflowArtifact) {
         return promptChangeAttribute((AbstractWorkflowArtifact) workItem.getStoreObject(), attributeType, persist);
      }
      return false;

   }

}
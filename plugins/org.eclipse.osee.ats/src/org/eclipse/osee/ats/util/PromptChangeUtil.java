/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
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

   public static boolean promptChangeAttributeWI(final Collection<? extends IAtsWorkItem> workItems, AttributeTypeToken attributeType, boolean persist, boolean multiLine) {
      List<AbstractWorkflowArtifact> awas = new LinkedList<>();
      for (IAtsWorkItem workItem : workItems) {
         awas.add((AbstractWorkflowArtifact) workItem.getStoreObject());
      }
      return ArtifactPromptChange.promptChangeAttribute(attributeType, awas, persist, multiLine);
   }

   public static boolean promptChangeAttribute(final Collection<? extends AbstractWorkflowArtifact> awas, AttributeTypeToken attributeType, boolean persist, boolean multiLine) {
      return ArtifactPromptChange.promptChangeAttribute(attributeType, awas, persist, multiLine);
   }

   public static boolean promptChangeAttribute(AbstractWorkflowArtifact sma, AttributeTypeToken attributeType, final boolean persist, boolean multiLine) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, Arrays.asList(sma), persist, multiLine);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

}
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
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;

/**
 * @author Donald G. Dunne
 */
public final class PromptChangeUtil {

   private PromptChangeUtil() {
      // Utility class
   }

   public static boolean promptChangePercentAttribute(AbstractWorkflowArtifact sma, IAttributeType attributeType, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, Arrays.asList(new Artifact[] {sma}), persist);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeAttribute(final Collection<? extends AbstractWorkflowArtifact> smas, IAttributeType attributeType, boolean persist, boolean multiLine) {
      return ArtifactPromptChange.promptChangeAttribute(attributeType, smas, persist, multiLine);
   }

   public static boolean promptChangeAttribute(final Artifact sma, IAttributeType attributeType, boolean persist, boolean multiLine) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, Arrays.asList(new Artifact[] {sma}), persist,
            multiLine);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeAttribute(AbstractWorkflowArtifact sma, IAttributeType attributeType, final boolean persist, boolean multiLine) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, Arrays.asList(sma), persist, multiLine);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeDate(AbstractWorkflowArtifact sma, IAttributeType attributeType, boolean persist) {
      try {
         return ArtifactPromptChange.promptChangeAttribute(attributeType, java.util.Collections.singleton(sma), persist);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
            "Can't save " + attributeType.getUnqualifiedName() + " date to artifact " + sma.getHumanReadableId(), ex);
      }
      return false;
   }

}
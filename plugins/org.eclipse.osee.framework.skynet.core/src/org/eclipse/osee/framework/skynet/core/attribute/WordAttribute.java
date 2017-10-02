/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.InputStream;
import java.util.HashSet;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.XmlTextInputStream;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Jeff C. Phillips
 */
public class WordAttribute extends StringAttribute {
   private static final IStatus promptStatus = new Status(IStatus.WARNING, Activator.PLUGIN_ID, 256, "", null);

   @Override
   protected boolean subClassSetValue(String value) {
      value = checkForTrackedChanges(value);
      value = WordUtil.removeWordMarkupSmartTags(value);
      return super.subClassSetValue(value);
   }

   private String checkForTrackedChanges(String value)  {
      String returnValue = value;

      Artifact art = getArtifact();
      BranchId branch = art.getBranch();

      if (WordCoreUtil.containsWordAnnotations(value) && !BranchManager.getType(branch).isMergeBranch()) {
         try {
            String message =
               "This document contains track changes and cannot be saved with them. Do you want OSEE to remove them?" + "\n\nNote:You will need to reopen this artifact in OSEE to see the final result.";
            IStatusHandler handler = DebugPlugin.getDefault().getStatusHandler(promptStatus);
            @SuppressWarnings("unchecked")
            Pair<MutableBoolean, Integer> answer =
               (Pair<MutableBoolean, Integer>) handler.handleStatus(promptStatus, message);
            MutableBoolean first = answer.getFirst();
            boolean isOkToRemove = first.getValue();
            if (isOkToRemove) {
               returnValue = WordCoreUtil.removeAnnotations(value);
            } else {
               throw new OseeCoreException(
                  "Artifact %s [%s], Branch[%s] contains track changes. Please remove them and save again.",
                  art.getName(), art.getArtId(), branch.getId());
            }
         } catch (CoreException ex) {
            OseeCoreException.wrapAndThrow(ex);
         } catch (ClassCastException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      return returnValue;
   }

   public boolean containsWordAnnotations()  {
      String temp = getValue();
      return WordCoreUtil.containsWordAnnotations(temp);
   }

   public boolean areApplicabilityTagsInvalid(BranchId branch, HashCollection<String, String> validFeatureValues, HashSet<String> validConfigurations) {
      return WordCoreUtil.areApplicabilityTagsInvalid(getValue(), branch, validFeatureValues, validConfigurations);
   }

   @Override
   public String getDisplayableString()  {
      String toReturn = null;
      InputStream inputStream = null;
      try {
         inputStream = new XmlTextInputStream(getValue());
         toReturn = Lib.inputStreamToString(inputStream);
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         Lib.close(inputStream);
      }
      return toReturn;
   }
}
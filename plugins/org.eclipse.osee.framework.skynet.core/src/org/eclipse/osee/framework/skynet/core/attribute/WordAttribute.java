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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.XmlTextInputStream;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Jeff C. Phillips
 */
public class WordAttribute extends StringAttribute {
   private static final IStatus promptStatus = new Status(IStatus.WARNING, Activator.PLUGIN_ID, 256, "", null);

   @Override
   public boolean subClassSetValue(String value) throws OseeCoreException {
      value = checkForTrackedChanges(value);
      value = WordUtil.removeWordMarkupSmartTags(value);
      return super.subClassSetValue(value);
   }

   private String checkForTrackedChanges(String value) throws OseeCoreException {
      String returnValue = value;
      if (WordAnnotationHandler.containsWordAnnotations(value) && getArtifact().getBranch().getBranchType() != BranchType.MERGE) {
         Artifact art = getArtifact();
         Branch branch = art.getBranch();

         try {
            if ((Boolean) DebugPlugin.getDefault().getStatusHandler(promptStatus).handleStatus(
               promptStatus,
               "This document contains track changes and cannot be saved with them. Do you want OSEE to remove them?" + "\n\nNote:You will need to reopen this artifact in OSEE to see the final result.")) {
               returnValue = WordAnnotationHandler.removeAnnotations(value);
            } else {
               throw new OseeCoreException(
                  "Artifact %s (%s), Branch %s (%s) contains track changes. Please remove them and save again.",
                  art.getName(), art.getArtId(), branch.getName(), branch.getId());
            }
         } catch (CoreException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      return returnValue;
   }

   public boolean containsWordAnnotations() throws OseeCoreException {
      String temp = getValue();
      return WordAnnotationHandler.containsWordAnnotations(temp);
   }

   @Override
   public String getDisplayableString() throws OseeCoreException {
      String toReturn = null;
      InputStream inputStream = null;
      try {
         inputStream = new XmlTextInputStream(getValue());
         toReturn = Lib.inputStreamToString(inputStream);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         Lib.close(inputStream);
      }
      return toReturn;
   }
}
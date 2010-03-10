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
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.xml.XmlTextInputStream;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Jeff C. Phillips
 */
public class WordAttribute extends StringAttribute {
   @Override
   public boolean subClassSetValue(String value) throws OseeCoreException {
      checkForTrackedChanges(value);
      value = WordUtil.removeWordMarkupSmartTags(value);
      return super.subClassSetValue(value);
   }

   private void checkForTrackedChanges(String value) throws OseeCoreException {
      if (WordAnnotationHandler.containsWordAnnotations(value) && getArtifact().getBranch().getBranchType() != BranchType.MERGE) {
         Artifact art = getArtifact();
         Branch branch = art.getBranch();
         throw new OseeTrackedChangesException(String.format("Artifact %s (%s), Branch %s (%s)", art.getName(),
               art.getArtId(), branch.getName(), branch.getId()));
      }
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
/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.widget;

import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboDam;

/**
 * @author Donald G. Dunne
 */
public class XColorTeamDam extends XComboDam {

   public XColorTeamDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public void saveToArtifact() {
      try {
         if (!Strings.isValid(data)) {
            artifact.deleteSoleAttribute(attributeType);
         } else {
            String enteredValue = get();
            artifact.setSoleAttributeValue(attributeType, enteredValue);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      if (isEditable()) {
         try {
            String enteredValue = get();
            String storedValue = artifact.getSoleAttributeValue(attributeType);
            if (!enteredValue.equals(storedValue)) {
               return new Result(true, attributeType + " is dirty");
            }
         } catch (AttributeDoesNotExist ex) {
            if (!get().equals("")) {
               return new Result(true, attributeType + " is dirty");
            }
         }
      }
      return Result.FalseResult;
   }

}

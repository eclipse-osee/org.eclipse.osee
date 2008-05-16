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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.util.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleAttributesExist;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

public class XComboBooleanDam extends XCombo implements IArtifactWidget {

   private Artifact artifact;
   private String attributeTypeName;

   public XComboBooleanDam(String displayLabel) {
      super(displayLabel);
   }

   public void setArtifact(Artifact artifact, String attrName) throws IllegalStateException, SQLException, MultipleAttributesExist, AttributeDoesNotExist {
      this.artifact = artifact;
      this.attributeTypeName = attrName;
      Boolean result = artifact.getSoleAttributeValue(attrName, null);
      if (result == null)
         super.set("");
      else
         super.set(result ? "yes" : "no");
   }

   @Override
   public void saveToArtifact() throws Exception {
      try {
         if (data == null || data.equals("")) {
            artifact.deleteSoleAttribute(attributeTypeName);
         } else {
            String enteredValue = get();
            artifact.setSoleXAttributeValue(attributeTypeName, enteredValue);
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#isDirty()
    */
   @Override
   public Result isDirty() throws Exception {
      try {
         String enteredValue = get();
         boolean storedValue = artifact.getSoleAttributeValue(attributeTypeName);
         if (enteredValue.equals("yes") != storedValue) {
            return new Result(true, attributeTypeName + " is dirty");
         }
      } catch (AttributeDoesNotExist ex) {
         if (!get().equals("")) return new Result(true, attributeTypeName + " is dirty");
      }
      return Result.FalseResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#revert()
    */
   @Override
   public void revert() throws Exception {
      setArtifact(artifact, attributeTypeName);
   }
}

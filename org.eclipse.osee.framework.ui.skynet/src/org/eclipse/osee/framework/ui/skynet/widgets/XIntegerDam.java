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
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

public class XIntegerDam extends XInteger implements IArtifactWidget {

   private Artifact artifact;
   private String attributeTypeName;

   public XIntegerDam(String displayLabel) {
      super(displayLabel);
   }

   public void setArtifact(Artifact artifact, String attrName) throws OseeCoreException, SQLException {
      this.artifact = artifact;
      this.attributeTypeName = attrName;
      try {
         Integer value = artifact.getSoleAttributeValue(attributeTypeName);
         super.set(value.toString());
      } catch (AttributeDoesNotExist ex) {
         super.set("");
      }
   }

   @Override
   public void saveToArtifact() throws OseeCoreException, SQLException {
      try {
         if (text == null || text.equals("")) {
            artifact.deleteSoleAttribute(attributeTypeName);
         } else {
            Integer enteredValue = getInteger();
            artifact.setSoleAttributeValue(attributeTypeName, enteredValue);
         }
      } catch (NumberFormatException ex) {
         // do nothing
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#isDirty()
    */
   @Override
   public Result isDirty() throws OseeCoreException, SQLException {
      try {
         Integer enteredValue = getInteger();
         Integer storedValue = artifact.getSoleAttributeValue(attributeTypeName);
         if (enteredValue.doubleValue() != storedValue.doubleValue()) {
            return new Result(true, attributeTypeName + " is dirty");
         }
      } catch (AttributeDoesNotExist ex) {
         if (!get().equals("")) return new Result(true, attributeTypeName + " is dirty");
      } catch (NumberFormatException ex) {
         // do nothing
      }
      return Result.FalseResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#revert()
    */
   @Override
   public void revert() throws OseeCoreException, SQLException {
      setArtifact(artifact, attributeTypeName);
   }

}

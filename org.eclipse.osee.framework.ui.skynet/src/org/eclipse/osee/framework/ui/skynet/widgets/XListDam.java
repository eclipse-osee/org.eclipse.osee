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
import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class XListDam extends XList implements IArtifactWidget {

   private Artifact artifact;
   private String attributeTypeName;

   /**
    * @param displayLabel
    */
   public XListDam(String displayLabel) {
      super(displayLabel);
   }

   public void setArtifact(Artifact artifact, String attrName) throws OseeCoreException, SQLException {
      this.artifact = artifact;
      this.attributeTypeName = attrName;
      super.setSelected(getStoredStrs());
   }

   @Override
   public void saveToArtifact() throws OseeCoreException, SQLException {
      artifact.setAttributeValues(attributeTypeName, getSelectedStrs());
   }

   public Collection<String> getStoredStrs() throws SQLException {
      return artifact.getAttributesToStringList(attributeTypeName);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#isDirty()
    */
   @Override
   public Result isDirty() throws OseeCoreException, SQLException {
      try {
         Collection<String> enteredValues = getSelectedStrs();
         Collection<String> storedValues = getStoredStrs();
         if (!Collections.isEqual(enteredValues, storedValues)) {
            return new Result(true, attributeTypeName + " is dirty");
         }
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

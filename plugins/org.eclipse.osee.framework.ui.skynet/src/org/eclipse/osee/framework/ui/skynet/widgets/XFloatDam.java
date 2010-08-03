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

import java.text.NumberFormat;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Donald G. Dunne
 */
public class XFloatDam extends XFloat implements IAttributeWidget {

   private Artifact artifact;
   private IAttributeType attributeType;

   public XFloatDam(String displayLabel) {
      super(displayLabel);
   }

   public XFloatDam(String displayLabel, String xmlRoot) {
      super(displayLabel, xmlRoot);
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public IAttributeType getAttributeType() {
      return attributeType;
   }

   @Override
   public void setAttributeType(Artifact artifact, IAttributeType attrName) throws OseeCoreException {
      this.artifact = artifact;
      this.attributeType = attrName;
      Double value = artifact.getSoleAttributeValue(getAttributeType(), null);
      super.set(value == null ? "" : NumberFormat.getInstance().format(value));
   }

   @Override
   public void saveToArtifact() {
      try {
         if (text == null || text.equals("")) {
            getArtifact().deleteSoleAttribute(getAttributeType());
         } else {
            Double enteredValue = getFloat();
            getArtifact().setSoleAttributeValue(getAttributeType(), enteredValue);
         }
      } catch (NumberFormatException ex) {
         // do nothing
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      Double enteredValue = getFloat();
      Double storedValue = getArtifact().getSoleAttributeValue(getAttributeType(), 0.0);
      if (enteredValue.doubleValue() != storedValue.doubleValue()) {
         return new Result(true, getAttributeType() + " is dirty");
      }
      return Result.FalseResult;
   }

   @Override
   public void revert() throws OseeCoreException {
      setAttributeType(getArtifact(), getAttributeType());
   }
}

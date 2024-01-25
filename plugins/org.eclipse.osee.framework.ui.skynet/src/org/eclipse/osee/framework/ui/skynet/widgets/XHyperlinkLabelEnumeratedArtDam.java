/*******************************************************************************
 * Copyright (c) 2023 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * @author Vaibhav Patel
 */
public class XHyperlinkLabelEnumeratedArtDam extends XHyperlinkLabelEnumeratedArt implements AttributeWidget {

   public static final String WIDGET_ID = XHyperlinkLabelEnumeratedArtDam.class.getSimpleName();

   protected AttributeTypeToken attributeType;
   protected Artifact artifact;

   public XHyperlinkLabelEnumeratedArtDam() {
      super("");
   }

   public XHyperlinkLabelEnumeratedArtDam(String label) {
      super(label);
   }

   @Override
   public void setAttributeType(AttributeTypeToken attributeType) {
      this.attributeType = attributeType;
   }

   @Override
   public boolean handleSelection() {
      try {
         if (super.handleSelection()) {
            artifact.setAttributeValues(attributeType, checked);
            artifact.persistInThread("Set Value(s)");
            return true;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   @Override
   public String getCurrentValue() {
      String value = Widgets.NOT_SET;
      List<String> values = artifact.getAttributesToStringList(attributeType);
      if (values.size() > 0) {
         value = org.eclipse.osee.framework.jdk.core.util.Collections.toString(", ", values);
      }
      return value;
   }

   @Override
   public List<String> getCurrentSelected() {
      return artifact.getAttributesToStringList(attributeType);
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;
      if (Strings.isInValid(getLabel())) {
         setLabel(attributeType.getUnqualifiedName());
      }
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

}

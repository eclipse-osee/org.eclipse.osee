package org.eclipse.osee.framework.ui.skynet.widgets;
/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Stephen J Molaro
 */
public class XComputedCharacteristicWidget extends XText implements ArtifactWidget {

   public static final String WIDGET_ID = XComputedCharacteristicWidget.class.getSimpleName();

   private final ComputedCharacteristicToken<?> computedCharacteristic;
   private Artifact artifact;

   public XComputedCharacteristicWidget(ComputedCharacteristicToken<?> computedCharacteristic) {
      super(computedCharacteristic.getName());
      this.computedCharacteristic = computedCharacteristic;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      refresh();
   }

   @Override
   public void refresh() {
      if (!getAttributeValues().isEmpty()) {
         setText(artifact.getComputedCharacteristicValue(computedCharacteristic).toString());
      } else {
         setText("");
      }
      updateTextWidget();
   }

   private <T> List<T> getAttributeValues() {
      List<T> attributeValues = new ArrayList<>();
      for (AttributeTypeToken attributeType : computedCharacteristic.getAttributeTypesToCompute()) {
         for (Attribute<?> attr : getArtifact().getAttributes(attributeType)) {
            attributeValues.add((T) attr.getValue());
         }
      }
      return attributeValues;
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public void saveToArtifact() {
      // Do Nothing
   }

   @Override
   public void revert() {
      // Do Nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      this.artifact = artifact;
   }
}
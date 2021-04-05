/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.client.demo;

import static org.eclipse.osee.client.demo.ClientDemoTypeTokenProvider.clientDemo;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Partition;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.SafetySeverity;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.SoftwareControlCategory;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.SoftwareCriticalityIndex;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeDouble;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeLong;
import org.eclipse.osee.framework.core.data.computed.ComputedCharacteristicAverage;
import org.eclipse.osee.framework.core.data.computed.ComputedCharacteristicDelta;
import org.eclipse.osee.framework.core.data.computed.ComputedCharacteristicProduct;
import org.eclipse.osee.framework.core.data.computed.ComputedCharacteristicQuotient;
import org.eclipse.osee.framework.core.data.computed.ComputedCharacteristicSum;

/**
 * @author Roberto E. Escobar
 */
public interface DemoOseeTypes {

   // @formatter:off
   AttributeTypeInteger MeasurementOne = clientDemo.createInteger(8927304985723049571L, "Measurement One", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger MeasurementTwo = clientDemo.createInteger(8927304985723049572L, "Measurement Two", MediaType.TEXT_PLAIN, "");
   AttributeTypeDouble MeasurementThree = clientDemo.createDouble(8927304985723049573L, "Measurement Three", MediaType.TEXT_PLAIN, "");
   AttributeTypeDouble MeasurementFour = clientDemo.createDouble(8927304985723049574L, "Measurement Four", MediaType.TEXT_PLAIN, "");
   AttributeTypeLong MeasurementFive = clientDemo.createLong(8927304985723049575L, "Measurement Five", MediaType.TEXT_PLAIN, "");
   AttributeTypeLong MeasurementSix = clientDemo.createLong(8927304985723049576L, "Measurement Six", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger MeasurementSeven = clientDemo.createInteger(8927304985723049577L, "Measurement Seven", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger MeasurementEight = clientDemo.createInteger(8927304985723049578L, "Measurement Eight", MediaType.TEXT_PLAIN, "");

   ComputedCharacteristicSum ComputationSum = clientDemo.createComp(ComputedCharacteristicSum::new, 8927304985723049579L, "Sum Computation", "", MeasurementOne, MeasurementTwo);
   ComputedCharacteristicProduct ComputationProduct = clientDemo.createComp(ComputedCharacteristicProduct::new, 8927304985723049580L, "Product Computation", "", MeasurementThree, MeasurementFour);
   ComputedCharacteristicAverage ComputationAverage = clientDemo.createComp(ComputedCharacteristicAverage::new, 8927304985723049581L, "Average Computation", "", MeasurementFive, MeasurementSix);
   ComputedCharacteristicQuotient ComputationQuotient = clientDemo.createComp(ComputedCharacteristicQuotient::new, 8927304985723049582L, "Quotient Computation", "", MeasurementOne, MeasurementTwo);
   ComputedCharacteristicDelta ComputationDelta = clientDemo.createComp(ComputedCharacteristicDelta::new, 8927304985723049583L, "Delta Computation", "", MeasurementThree, MeasurementFour);
   ComputedCharacteristicDelta ComputationFailure = clientDemo.createComp(ComputedCharacteristicDelta::new, 8927304985723049584L, "Failed Computation", "This computation has too many inputs", MeasurementOne, MeasurementTwo, MeasurementSeven);
   ComputedCharacteristicDelta ComputationInvalid = clientDemo.createComp(ComputedCharacteristicDelta::new, 8927304985723049585L, "Invalid Computation", "This computation should not be valid for any artifact type", MeasurementOne, MeasurementTwo);
   ComputedCharacteristicQuotient ComputationDivideByZero = clientDemo.createComp(ComputedCharacteristicQuotient::new, 8927304985723049586L, "Dividing by Zero", "", MeasurementOne, MeasurementEight);

   ArtifactTypeToken DemoArtifactWithSelectivePartition = clientDemo.add(clientDemo.artifactType(86L, "Demo Artifact With Selective Partition", false, Artifact)
      .atLeastOne(Partition, Partition.Unspecified));
   ArtifactTypeToken DemoArtifactWithComputedCharacteristics = clientDemo.add(clientDemo.artifactType(836365L, "Demo Artifact With Computed Characteristics", false, Artifact)
      .exactlyOne(MeasurementOne, 100)
      .exactlyOne(MeasurementTwo, 25)
      .exactlyOne(MeasurementThree, 3.6)
      .exactlyOne(MeasurementFour, 1.5)
      .exactlyOne(MeasurementFive, 80L)
      .exactlyOne(MeasurementSix, 354L)
      .any(MeasurementSeven, 48)
      .exactlyOne(MeasurementEight, 0)
      .computed(ComputationSum)
      .computed(ComputationProduct)
      .computed(ComputationAverage)
      .computed(ComputationQuotient)
      .computed(ComputationDelta)
      .computed(ComputationFailure)
      .computed(ComputationDivideByZero)
      .exactlyOne(SoftwareControlCategory, SoftwareControlCategory.Unspecified)
      .exactlyOne(SafetySeverity, SafetySeverity.Unspecified)
      .computed(SoftwareCriticalityIndex));
   // @formatter:on

}
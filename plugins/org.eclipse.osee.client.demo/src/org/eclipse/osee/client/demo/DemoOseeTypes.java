/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.demo;

import static org.eclipse.osee.client.demo.ClientDemoTypeTokenProvider.clientDemo;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Partition;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeString;

/**
 * @author Roberto E. Escobar
 */
public interface DemoOseeTypes {

   // @formatter:off
   AttributeTypeString DemoDslAttribute = clientDemo.createStringNoTag(1153126013769613777L, "Demo DSL Attribute", MediaType.TEXT_PLAIN, "");

   ArtifactTypeToken DemoArtifactWithSelectivePartition = clientDemo.add(clientDemo.artifactType(86L, "Demo Artifact With Selective Partition", false, Artifact)
      .atLeastOne(Partition, " ", 3458764513820541309L));
   ArtifactTypeToken DemoDslArtifact = clientDemo.add(clientDemo.artifactType(204526342635554L, "Demo DSL Artifact", false, Artifact)
      .any(DemoDslAttribute, ""));
   // @formatter:on

}
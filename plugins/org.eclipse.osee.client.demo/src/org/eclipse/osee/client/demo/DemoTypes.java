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

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Roberto E. Escobar
 */
public final class DemoTypes {

   // @formatter:off
   public static final IArtifactType DemoTestRequirement = TokenFactory.createArtifactType(0x0000000000000056L, "Demo Artifact With Selective Partition");
   
   public static final IArtifactType DemoDslArtifact = TokenFactory.createArtifactType(0x0000BA0400000022L, "Demo DSL Artifact");
   public static final IAttributeType DemoDslAttribute = TokenFactory.createAttributeType(0x1000BA00000001CFL, "AxRbLlj+c2ZHjBeumfgA");
   // @formatter:on

   private DemoTypes() {
      // Constants
   }
}
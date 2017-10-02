/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.ui.handler;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * Class to create Doors Artifact type
 * 
 * @author Chandan Bandemutt
 */
public class DoorsArtifactType {

   /**
    * Doors Artifact type
    */
   public static final IArtifactType Doors_Artifact =
      TokenFactory.createArtifactType(5764607523034243073L, "DoorsArtifact");

}

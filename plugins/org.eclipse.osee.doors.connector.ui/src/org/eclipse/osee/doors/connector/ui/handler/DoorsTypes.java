/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.ui.handler;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * Class to create Doors Artifact type
 *
 * @author Chandan Bandemutt
 */
public class DoorsTypes {

   /**
    * Doors Artifact type
    */
   public static final ArtifactTypeToken DoorsRequirement =
      ArtifactTypeToken.valueOf(5764607523034243073L, "Doors Requirement");

   public static final AttributeTypeToken DoorReqName =
      AttributeTypeToken.valueOf(5764607523034243075L, "Door Req Name");
   public static final AttributeTypeToken DoorReqUrl = AttributeTypeToken.valueOf(8198L, "Door Req URL");

}

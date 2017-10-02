/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Model Class to store the requirements from the DWA responses
 * 
 * @author Chandan Bandemutt
 */
public class QueryCapabilities extends DoorsArtifact {

   List<Requirement> requirements;

   /**
    * Constructor to instantiate requirements list
    */
   public QueryCapabilities() {
      this.requirements = new ArrayList<>();
   }

   /**
    * Method to add Requirement objects
    * 
    * @param requirement Requirment Object from DWA
    */
   public void addrequirements(final Requirement requirement) {
      this.requirements.add(requirement);
   }

   /**
    * @return Requirment Object from DWA
    */
   public List<Requirement> getRequirements() {
      return this.requirements;
   }

}

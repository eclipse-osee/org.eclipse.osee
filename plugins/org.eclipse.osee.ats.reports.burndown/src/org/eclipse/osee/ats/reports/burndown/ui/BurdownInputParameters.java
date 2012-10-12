/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.ats.reports.burndown.ui;

import java.util.Date;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Praveen Joseph
 */
public class BurdownInputParameters {

   private final Artifact version;
   private final Date startDate;
   private final Date endDate;

   public BurdownInputParameters(Artifact version, Date startDate, Date endDate) {
      super();
      this.version = version;
      this.startDate = startDate;
      this.endDate = endDate;
   }

   public Artifact getVersion() {
      return version;
   }

   public Date getStartDate() {
      return startDate;
   }

   public Date getEndDate() {
      return endDate;
   }

}
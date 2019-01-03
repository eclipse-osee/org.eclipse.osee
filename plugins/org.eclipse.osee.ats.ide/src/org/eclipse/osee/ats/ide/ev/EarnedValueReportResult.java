/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.ev;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.util.IColumn;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class EarnedValueReportResult {

   private final Map<IColumn, String> values = new HashMap<>(9);
   private Artifact artifact;
   private final IAtsWorkPackage workPackage;

   public EarnedValueReportResult(IAtsWorkPackage workPackage, Artifact artifact) {
      this.workPackage = workPackage;
      this.artifact = artifact;
   }

   public void setValue(IColumn column, String value) {
      values.put(column, value);
   }

   public String getValue(IColumn column) {
      return values.get(column);
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public void setArtifact(Artifact artifact) {
      this.artifact = artifact;
   }

   public IAtsWorkPackage getWorkPackage() {
      return workPackage;
   }

}

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
package org.eclipse.osee.ote.define.artifacts;

import java.util.Date;

/**
 * @author John R. Misinco
 */
public interface TestRunOperator {

   public abstract String getScriptRevision();

   public abstract Date getLastDateUploaded();

   public abstract String getChecksum();

   public abstract String getOutfileUrl();

   public abstract String getPartition();

   public abstract String getSubsystem();

   public abstract int getTestPointsPassed();

   public abstract int getTestPointsFailed();

   public abstract int getTotalTestPoints();

   public abstract Date getEndDate();

   public abstract Date getLastModifiedDate();

   public abstract Date getTestStartDate();

   public abstract String getTestResultStatus();

   public abstract boolean isBatchModeAllowed();

   public abstract String getOseeVersion();

   public abstract String getOseeServerTitle();

   public abstract String getOseeServerVersion();

   public abstract String getProcessorId();

   public abstract String getRunDuration();

   public abstract String getQualificationLevel();

   public abstract String getBuildId();

   public abstract String getRanOnOperatingSystem();

   public abstract String getLastAuthor();

   public abstract String getScriptSimpleName();

   public abstract boolean wasAborted();

}
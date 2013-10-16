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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author John R. Misinco
 */
public interface TestRunOperator {

   public abstract String getScriptRevision() throws OseeCoreException;

   public abstract Date getLastDateUploaded() throws OseeCoreException;

   public abstract String getChecksum() throws OseeCoreException;

   public abstract String getOutfileUrl() throws OseeCoreException;

   public abstract String getPartition();

   public abstract String getSubsystem();

   public abstract int getTestPointsPassed() throws OseeCoreException;

   public abstract int getTestPointsFailed() throws OseeCoreException;

   public abstract int getTotalTestPoints() throws OseeCoreException;

   public abstract Date getEndDate() throws OseeCoreException;

   public abstract Date getLastModifiedDate() throws OseeCoreException;

   public abstract Date getTestStartDate() throws OseeCoreException;

   public abstract String getTestResultStatus() throws OseeCoreException;

   public abstract boolean isBatchModeAllowed();

   public abstract String getOseeVersion() throws OseeCoreException;

   public abstract String getOseeServerTitle() throws OseeCoreException;

   public abstract String getOseeServerVersion() throws OseeCoreException;

   public abstract String getProcessorId() throws OseeCoreException;

   public abstract String getRunDuration() throws OseeCoreException;

   public abstract String getQualificationLevel() throws OseeCoreException;

   public abstract String getBuildId() throws OseeCoreException;

   public abstract String getRanOnOperatingSystem() throws OseeCoreException;

   public abstract String getLastAuthor() throws OseeCoreException;

   public abstract String getScriptSimpleName();

   public abstract boolean wasAborted();

}
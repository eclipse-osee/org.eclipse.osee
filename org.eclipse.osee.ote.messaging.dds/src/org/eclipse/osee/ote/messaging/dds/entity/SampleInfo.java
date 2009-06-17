/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.messaging.dds.entity;

import org.eclipse.osee.ote.messaging.dds.InstanceHandle;
import org.eclipse.osee.ote.messaging.dds.InstanceStateKind;
import org.eclipse.osee.ote.messaging.dds.NotImplementedException;
import org.eclipse.osee.ote.messaging.dds.SampleStateKind;
import org.eclipse.osee.ote.messaging.dds.Time;
import org.eclipse.osee.ote.messaging.dds.ViewStateKind;

/*
 * NOTE: This class is in the same package as DataReader so that the setter methods
 *       on this class can have 'default'/'package' visibility, allowing only the
 *       DDS system classes modify the SampleInfo fields, and thusly causing the
 *       application using the DDS system to only be able to read the fields.
 */

/**
 * Provides information about a sample of data. This class is not fully implemented, but is passed
 * through the system with default values in the current implementation.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class SampleInfo {

   public static final SampleInfo DEFAULT_SAMPLE_INFO = new SampleInfo(null, null, null, 0, 0, 0, 0, 0, null, null);

   private SampleStateKind sampleStateKind;
   private ViewStateKind viewStateKind;
   private InstanceStateKind instanceStateKind;
   private long disposeGenerationCount;
   private long noWritersGenerationCount;
   private long sampleRank;
   private long generationRank;
   private long absoluteGenerationRank;
   private Time sourceTimestamp;
   private InstanceHandle instanceHandle;

   /**
    * Create a <code>SampleInfo</code> with particular values for all of the state date.
    * 
    */
   public SampleInfo(SampleStateKind sampleStateKind, ViewStateKind viewStateKind, InstanceStateKind instanceStateKind, long disposeGenerationCount,
         long noWritersGenerationCount, long sampleRank, long generationRank, long absoluteGenerationRank, Time sourceTimestamp, InstanceHandle instanceHandle) {
      super();
      this.sampleStateKind = sampleStateKind;
      this.viewStateKind = viewStateKind;
      this.instanceStateKind = instanceStateKind;
      this.disposeGenerationCount = disposeGenerationCount;
      this.noWritersGenerationCount = noWritersGenerationCount;
      this.sampleRank = sampleRank;
      this.generationRank = generationRank;
      this.absoluteGenerationRank = absoluteGenerationRank;
      this.sourceTimestamp = sourceTimestamp;
      this.instanceHandle = instanceHandle;
   }

   /**
    * Create a <code>SampleInfo</code> with default values for all of the state data.
    *
    */
   public SampleInfo() {
      this(SampleStateKind.NOT_READ, ViewStateKind.NEW, InstanceStateKind.ALIVE, 0, 0, 0, 0, 0, new Time(), new InstanceHandle());
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    * 
    * @return Returns the absoluteGenerationRank.
    */
   public long getAbsoluteGenerationRank() {
      //DONT_NEED
      if (true) throw new NotImplementedException();
      return absoluteGenerationRank;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    * 
    * @return Returns the disposeGenerationCount.
    */
   public long getDisposeGenerationCount() {
      //DONT_NEED
      if (true) throw new NotImplementedException();
      return disposeGenerationCount;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    * 
    * @return Returns the generationRank.
    */
   public long getGenerationRank() {
      //DONT_NEED
      if (true) throw new NotImplementedException();
      return generationRank;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    * 
    * @return Returns the instanceHandle.
    */
   public InstanceHandle getInstanceHandle() {
      //DONT_NEED
      if (true) throw new NotImplementedException();
      return instanceHandle;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    * 
    * @return Returns the instanceStateKind.
    */
   public InstanceStateKind getInstanceStateKind() {
      //DONT_NEED
      if (true) throw new NotImplementedException();
      return instanceStateKind;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    * 
    * @return Returns the noWritersGenerationCount.
    */
   public long getNoWritersGenerationCount() {
      //DONT_NEED
      if (true) throw new NotImplementedException();
      return noWritersGenerationCount;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    * 
    * @return Returns the sampleRank.
    */
   public long getSampleRank() {
      //DONT_NEED
      if (true) throw new NotImplementedException();
      return sampleRank;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    * 
    * @return Returns the sampleStateKind.
    */
   public SampleStateKind getSampleStateKind() {
      //DONT_NEED
      if (true) throw new NotImplementedException();
      return sampleStateKind;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    * 
    * @return Returns the sourceTimestamp.
    */
   public Time getSourceTimestamp() {
      //DONT_NEED
      if (true) throw new NotImplementedException();
      return sourceTimestamp;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    * 
    * @return Returns the viewStateKind.
    */
   public ViewStateKind getViewStateKind() {
      //DONT_NEED
      if (true) throw new NotImplementedException();
      return viewStateKind;
   }

   public void copyFrom(SampleInfo sampleInfo) {
      this.sampleStateKind = sampleInfo.sampleStateKind;
      this.viewStateKind = sampleInfo.viewStateKind;
      this.instanceStateKind = sampleInfo.instanceStateKind;
      this.disposeGenerationCount = sampleInfo.disposeGenerationCount;
      this.noWritersGenerationCount = sampleInfo.noWritersGenerationCount;
      this.sampleRank = sampleInfo.sampleRank;
      this.generationRank = sampleInfo.generationRank;
      this.absoluteGenerationRank = sampleInfo.absoluteGenerationRank;
      this.sourceTimestamp.copyFrom(sampleInfo.sourceTimestamp);
      this.instanceHandle.copyFrom(sampleInfo.instanceHandle);
   }

   /**
    * @param absoluteGenerationRank The absoluteGenerationRank to set.
    */
   void setAbsoluteGenerationRank(long absoluteGenerationRank) {
      this.absoluteGenerationRank = absoluteGenerationRank;
   }

   /**
    * @param disposeGenerationCount The disposeGenerationCount to set.
    */
   void setDisposeGenerationCount(long disposeGenerationCount) {
      this.disposeGenerationCount = disposeGenerationCount;
   }

   /**
    * @param generationRank The generationRank to set.
    */
   void setGenerationRank(long generationRank) {
      this.generationRank = generationRank;
   }

   /**
    * @param instanceHandle The instanceHandle to set.
    */
   void setInstanceHandle(InstanceHandle instanceHandle) {
      this.instanceHandle = instanceHandle;
   }

   /**
    * @param instanceStateKind The instanceStateKind to set.
    */
   void setInstanceStateKind(InstanceStateKind instanceStateKind) {
      this.instanceStateKind = instanceStateKind;
   }

   /**
    * @param noWritersGenerationCount The noWritersGenerationCount to set.
    */
   void setNoWritersGenerationCount(long noWritersGenerationCount) {
      this.noWritersGenerationCount = noWritersGenerationCount;
   }

   /**
    * @param sampleRank The sampleRank to set.
    */
   void setSampleRank(long sampleRank) {
      this.sampleRank = sampleRank;
   }

   /**
    * @param sampleStateKind The sampleStateKind to set.
    */
   void setSampleStateKind(SampleStateKind sampleStateKind) {
      this.sampleStateKind = sampleStateKind;
   }

   /**
    * @param sourceTimestamp The sourceTimestamp to set.
    */
   void setSourceTimestamp(Time sourceTimestamp) {
      this.sourceTimestamp = sourceTimestamp;
   }

   /**
    * @param viewStateKind The viewStateKind to set.
    */
   void setViewStateKind(ViewStateKind viewStateKind) {
      this.viewStateKind = viewStateKind;
   }
}

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
package org.eclipse.osee.ote.messaging.dds;

import java.lang.ref.WeakReference;

import org.eclipse.osee.ote.messaging.dds.entity.SampleInfo;

/**
 * Provides a coupling mechanism for <code>SampleInfo</code> with <code>Data</code> instances.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class DataSample {
   private WeakReference<Data> data;
   private SampleInfo sampleInfo;

   /**
    * Create a data sample that is used for propogating data through
    * the system.
    * 
    * @param data
    * @param sampleInfo
    */
   public DataSample(Data data, SampleInfo sampleInfo) {
      this.data = new WeakReference<Data>(data);
      this.sampleInfo = sampleInfo;
   }

   /**
    * Create a data sample that can be used for receiving propogated data.
    * 
    * @param data
    */
   public DataSample(Data data) {
      super();
      this.data = new WeakReference<Data>(data);
      this.sampleInfo = new SampleInfo();
   }
   /**
    * @return Returns the data.
    */
   public Data getData() {
      return data.get();
   }

   /**
    * @return Returns the sampleInfo.
    */
   public SampleInfo getSampleInfo() {
      return sampleInfo;
   }

   /**
    * @param sourceSampleInfo The sampleInfo to set.
    */
   public void setSampleInfo(SampleInfo sourceSampleInfo) {
      sampleInfo.copyFrom(sourceSampleInfo);
   }
}

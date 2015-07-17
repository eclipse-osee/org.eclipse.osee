/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.core;

/**
 * Model Class to store the Doors Artifact
 *
 * @author Chandan Bandemutt
 */
public class DoorsModel {

   /**
   *
   */

   public static String dialogUrl;

   /**
    *
    */
   public static DoorsArtifact doorsArtifact;
   private static DoorsModel instance;

   private static String id;

   /**
    * @return the DoorsArtifact object
    */
   public static DoorsArtifact getDoorsArtifact() {
      return doorsArtifact;
   }

   /**
    * @param doorsArtifact : sets the DoorsArtifact object
    */
   public static void setDoorsArtifact(final DoorsArtifact doorsArtifact) {
      DoorsModel.doorsArtifact = doorsArtifact;
   }

   /**
    * @return the only instance of this class.
    */
   public static synchronized DoorsModel getInstance() {
      if (null == instance) {
         instance = new DoorsModel();
      }
      return instance;
   }

   /**
    * @return the delegated UI url
    */
   public static String getDialogUrl() {
      return dialogUrl;
   }

   /**
    * @param dialogUrl : Url of the delegated UI
    */
   public static void setDialogUrl(final String dialogUrl) {
      DoorsModel.dialogUrl = dialogUrl;
   }

   /**
    * @return the session ID
    */
   public static String getJSessionID() {
      return id;

   }

   /**
    * @param id : Session ID
    */
   public static void setJSessionID(final String id) {
      DoorsModel.id = id;

   }

}

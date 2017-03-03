/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.core;

/**
 * Interface to parse the doors reponse and to replace the url
 *
 * @author Chandan Bandemutt
 */
public interface IDoorsArtifactParser {

   /**
    * @param provider : Pass in the artifact to parse, for recursive parsing
    * @return : the doors artifact
    * @throws Exception : XML exception
    */
   DoorsArtifact parse(DoorsArtifact provider) throws Exception;

   /**
    * @param url : response url to change to lower case
    * @return the url changed to lower case
    */
   String replace(final String url);

}

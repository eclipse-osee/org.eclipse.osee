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
package org.eclipse.osee.framework.core.dsl.integration.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.ComparisonSnapshot;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;

/**
 * @author Roberto E. Escobar
 */
public final class ModelUtil {

   private ModelUtil() {
      // Utility Class
   }

   private static void storeModel(Resource resource, OutputStream outputStream, EObject object, String uri, Map<String, Boolean> options) throws OseeCoreException {
      try {
         resource.setURI(URI.createURI(uri));
         resource.getContents().add(object);
         resource.save(outputStream, options);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public static String modelToStringXML(EObject object, String uri, Map<String, Boolean> options) throws OseeCoreException {
      return modelToString(new XMLResourceImpl(), object, uri, options);
   }

   private static String modelToString(Resource resource, EObject object, String uri, Map<String, Boolean> options) throws OseeCoreException {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      storeModel(resource, outputStream, object, uri, options);
      try {
         return outputStream.toString("UTF-8");
      } catch (UnsupportedEncodingException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   public static ComparisonSnapshot loadComparisonSnapshot(String compareName, String compareData) throws OseeCoreException {
      ComparisonSnapshot snapshot = null;
      try {
         ResourceSet resourceSet = new ResourceSetImpl();
         Resource resource = resourceSet.createResource(URI.createURI(compareName));
         resource.load(new ByteArrayInputStream(compareData.getBytes("UTF-8")), resourceSet.getLoadOptions());
         snapshot = (ComparisonSnapshot) resource.getContents().get(0);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return snapshot;
   }

}

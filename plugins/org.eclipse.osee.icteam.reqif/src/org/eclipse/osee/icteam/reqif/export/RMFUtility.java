/*********************************************************************
 * Copyright (c) 2021 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 *     Boeing - update for RMF 0.13
 **********************************************************************/
package org.eclipse.osee.icteam.reqif.export;

import java.io.File;
import java.io.IOException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.rmf.reqif10.ReqIF;
import org.eclipse.rmf.reqif10.ReqIF10Package;
import org.eclipse.rmf.reqif10.datatypes.DatatypesPackage;
import org.eclipse.rmf.reqif10.serialization.ReqIF10ResourceFactoryImpl;
import org.eclipse.rmf.reqif10.xhtml.XhtmlPackage;

/**
 * Load and Save ReqIf content
 *
 * @author Manjunath Sangappa
 */
public class RMFUtility {

   /**
    * Load supplied reqif file and return the reqif contents
    *
    * @param file
    * @return
    * @throws Exception
    */
   public static Object LoadReqif(final File file) throws Exception {

      ResourceSetImpl resourceSet = getResourceSet();
      URI uri = file.isFile() ? URI.createFileURI(file.getAbsolutePath()) : URI.createURI(file.getName());
      XMLResource resource = (XMLResource) resourceSet.createResource(uri);

      resource.load(null);

      EList<EObject> rootObjects = resource.getContents();
      if (rootObjects.isEmpty()) {
         return null;
      }
      return rootObjects.get(0);
   }

   /**
    * Save ReqIf contents to a file.
    *
    * @param reqif
    * @param fileName
    * @throws IOException
    */
   protected static void saveReqIFFile(final ReqIF reqif, final String fileName) throws IOException {
      ResourceSetImpl resourceSet = getResourceSet();
      File file = new File(fileName);
      file.createNewFile();
      URI uri = file.isFile() ? URI.createFileURI(file.getAbsolutePath()) : URI.createURI(fileName);
      Resource resource = resourceSet.createResource(uri);
      resource.getContents().add(reqif);
      resource.save(null);
   }

   /**
    * getResourceSet
    *
    * @return {@link ResourceSet}
    */
   private static ResourceSetImpl getResourceSet() {
      ResourceSetImpl resourceSet = new ResourceSetImpl();

      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("reqif",
         new ReqIF10ResourceFactoryImpl());
      return resourceSet;
   }

   /**
    * Setup the Registry for loading the file
    *
    * @throws Exception @{@link Exception}
    */
   public static void setup() throws Exception {

      EPackage.Registry.INSTANCE.clear();
      EPackage.Registry.INSTANCE.put(ReqIF10Package.eNS_URI, ReqIF10Package.eINSTANCE);
      EPackage.Registry.INSTANCE.put(XhtmlPackage.eNS_URI, XhtmlPackage.eINSTANCE);
      EPackage.Registry.INSTANCE.put(DatatypesPackage.eNS_URI, DatatypesPackage.eINSTANCE);
      EPackage.Registry.INSTANCE.put(XMLNamespacePackage.eNS_URI, XMLNamespacePackage.eINSTANCE);
      EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
      EPackage.Registry.INSTANCE.put(XMLTypePackage.eNS_URI, XMLTypePackage.eINSTANCE);

   }

}
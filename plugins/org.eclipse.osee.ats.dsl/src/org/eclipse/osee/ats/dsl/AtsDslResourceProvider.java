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
package org.eclipse.osee.ats.dsl;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import com.google.inject.Injector;

/**
 * @author Donald G. Dunne
 */
public class AtsDslResourceProvider implements IResourceProvider {

   private Resource resource;

   public AtsDslResourceProvider() {
   }

   @Override
   public Collection<String> getErrors() {
      List<String> errors = new LinkedList<String>();
      for (org.eclipse.emf.ecore.resource.Resource.Diagnostic diagnostic : resource.getErrors()) {
         throw new IllegalStateException(diagnostic.toString());
      }
      return errors;
   }

   @Override
   public AtsDsl getContents(String uri, String xTextData) throws Exception {
      AtsDslStandaloneSetup setup = new AtsDslStandaloneSetup();
      Injector injector = setup.createInjectorAndDoEMFRegistration();
      XtextResourceSet set = injector.getInstance(XtextResourceSet.class);

      //         set.setClasspathURIContext(ModelUtil.class);
      set.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
      resource = set.createResource(URI.createURI(uri));
      resource.load(new ByteArrayInputStream(xTextData.getBytes("UTF-8")), set.getLoadOptions());
      return (AtsDsl) resource.getContents().get(0);
   }

}

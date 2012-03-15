/*
 * Created on Mar 26, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.osee.ats.dsl.AtsDslStandaloneSetup;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import com.google.inject.Injector;

public class AtsDslResourceProvider implements IResourceProvider {

   private Resource resource;

   public AtsDslResourceProvider() {
   }

   @Override
   public Collection<String> getErrors() throws OseeStateException {
      List<String> errors = new LinkedList<String>();
      for (Diagnostic diagnostic : resource.getErrors()) {
         throw new OseeStateException(diagnostic.toString());
      }
      return errors;
   }

   @Override
   public AtsDsl getContents(String uri, String xTextData) throws OseeCoreException {
      AtsDslStandaloneSetup setup = new AtsDslStandaloneSetup();
      Injector injector = setup.createInjectorAndDoEMFRegistration();
      XtextResourceSet set = injector.getInstance(XtextResourceSet.class);

      //         set.setClasspathURIContext(ModelUtil.class);
      set.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
      resource = set.createResource(URI.createURI(uri));
      try {
         resource.load(new ByteArrayInputStream(xTextData.getBytes("UTF-8")), set.getLoadOptions());
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
      return (AtsDsl) resource.getContents().get(0);
   }

}

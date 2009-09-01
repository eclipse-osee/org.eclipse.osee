package org.eclipse.osee.framework.types.bridge.operations;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osee.framework.OseeTypesStandaloneSetup;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.osgi.framework.Bundle;
import com.google.inject.Injector;

public final class OseeTypeModelUtil {

   private OseeTypeModelUtil() {
   }

   public static OseeTypeModel loadModel(Object context, java.net.URI target) throws OseeCoreException, MalformedURLException, IOException, URISyntaxException {
      String uri = target.toASCIIString();
      Injector injector = new OseeTypesStandaloneSetup().createInjectorAndDoEMFRegistration();
      XtextResourceSet set = injector.getInstance(XtextResourceSet.class);

      Object projectContext = context;
      if (context instanceof Bundle) {
         Bundle bundle = (Bundle) context;
         projectContext = bundle.getClass().getClassLoader();
         projectContext = ExportClassLoader.getSystemClassLoader();
      }

      set.setClasspathURIContext(projectContext);
      set.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
      Resource resource = set.getResource(URI.createURI(uri), true);
      OseeTypeModel model = (OseeTypeModel) resource.getContents().get(0);
      for (Diagnostic diagnostic : resource.getErrors()) {
         System.err.println(diagnostic.toString());
      }
      return model;
   }

   //
   //      
   //      //      URL fileUrl = FileLocator.toFileURL(target.toURL());
   //      //      String path = fileUrl.toURI().toASCIIString();
   //      //      int indexOf = path.indexOf("/org");
   //      //      String data = path.substring(indexOf + 1, path.length());
   //      URI uri = URI.createURI(target.toASCIIString(), true);
   //
   //      Injector injector = new OseeTypesStandaloneSetup().createInjectorAndDoEMFRegistration();
   //      XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
   //      resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
   //
   //      //      for (IConfigurationElement el : ExtensionPoints.getExtensionElements(
   //      //            "org.eclipse.osee.framework.skynet.core.OseeTypes", "OseeTypes")) {
   //      //         if (el.getName().equals("OseeTypes")) {
   //      //            String resource = el.getAttribute("resource");
   //      //            Bundle bundle = Platform.getBundle(el.getContributor().getName());
   //      //            URL url = bundle.getEntry(resource);
   //      //         }
   //      //      }
   //
   //      resourceSet.setClasspathUriResolver(new JdtClasspathUriResolver());
   //      URL fileURL = FileLocator.toFileURL(target.toURL());
   //      resourceSet.setClasspathURIContext(fileURL);
   //
   //      Resource resource = resourceSet.createResource(uri);
   //      resource.load(null);
   //
   //      //      List<Resource> resources = new ArrayList<Resource>();
   //      //      resources.add(resource);
   //      //      for (int i = 0; i < resources.size(); i++) {
   //      //         Resource r = resources.get(i);
   //      //         for (Iterator<EObject> j = r.getAllContents(); j.hasNext();) {
   //      //            for (Object object : j.next().eCrossReferences()) {
   //      //               EObject eObject = (EObject) object;
   //      //               Resource otherResource = eObject.eResource();
   //      //               if (otherResource != null && !resources.contains(otherResource)) {
   //      //                  resources.add(otherResource);
   //      //               }
   //      //            }
   //      //         }
   //      //      }
   //      if (resource instanceof LazyLinkingResource) {
   //         ((LazyLinkingResource) resource).setEagerLinking(true);
   //      }
   //
   //      //      XtextResource xtextResource = (XtextResource) resource;
   //      //      xtextResource.
   //      OseeTypeModel model = (OseeTypeModel) resource.getContents().get(0);
   //      for (Diagnostic diagnostic : resource.getErrors()) {
   //         System.err.println(diagnostic.toString());
   //      }
   //      return model;
   //   }

   public static void saveModel(java.net.URI target, OseeTypeModel model) throws IOException {
      OseeTypesStandaloneSetup.doSetup();

      URI uri = URI.createURI(target.toASCIIString());
      ResourceSet resourceSet = new ResourceSetImpl();
      Resource resource = resourceSet.createResource(uri);

      resource.getContents().add(model);

      Map<String, Boolean> options = new HashMap<String, Boolean>();
      options.put(XtextResource.OPTION_FORMAT, Boolean.FALSE); // Inverted in the code
      resource.save(options);
   }
}

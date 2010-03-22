/*
 * Created on Mar 19, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.sos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.Bundle;

public class SosCommand implements CommandProvider {
   private Collection<String> requiredBundles;

   public SosCommand() {
      requiredBundles = new HashSet<String>();
   }

   private void specifyRequiredBundles() {
      requiredBundles = new HashSet<String>();
      requiredBundles.add("com.google.collect");
      requiredBundles.add("com.google.guice");
      requiredBundles.add("com.ibm.icu");
      requiredBundles.add("com.lowagie.text");
      requiredBundles.add("javax.servlet");
      requiredBundles.add("javax.xml");
      //      will be needed after the new event system is implemented
      //      requiredBundles.add("jms.libraries");
      requiredBundles.add("org.antlr.runtime");
      requiredBundles.add("org.apache.commons.codec");
      requiredBundles.add("org.apache.commons.httpclient");
      requiredBundles.add("org.apache.commons.logging");
      requiredBundles.add("org.apache.log4j");
      requiredBundles.add("org.apache.xerces");
      requiredBundles.add("org.apache.xml.resolver");
      requiredBundles.add("org.apache.xml.serializer");
      requiredBundles.add("org.eclipse.core.contenttype");
      requiredBundles.add("org.eclipse.core.expressions");
      requiredBundles.add("org.eclipse.core.filesystem");
      requiredBundles.add("org.eclipse.core.filesystem.win32.x86");
      requiredBundles.add("org.eclipse.core.jobs");
      requiredBundles.add("org.eclipse.core.resources");
      requiredBundles.add("org.eclipse.core.resources.compatibility");
      requiredBundles.add("org.eclipse.core.resources.win32.x86");
      requiredBundles.add("org.eclipse.core.runtime");
      requiredBundles.add("org.eclipse.core.runtime.compatibility.registry");
      requiredBundles.add("org.eclipse.emf.common");
      requiredBundles.add("org.eclipse.emf.compare");
      requiredBundles.add("org.eclipse.emf.compare.diff");
      requiredBundles.add("org.eclipse.emf.compare.match");
      requiredBundles.add("org.eclipse.emf.ecore");
      requiredBundles.add("org.eclipse.emf.ecore.xmi");
      requiredBundles.add("org.eclipse.emf.edit");
      requiredBundles.add("org.eclipse.equinox.app");
      requiredBundles.add("org.eclipse.equinox.common");
      requiredBundles.add("org.eclipse.equinox.ds");
      requiredBundles.add("org.eclipse.equinox.http.jetty");
      requiredBundles.add("org.eclipse.equinox.http.registry");
      requiredBundles.add("org.eclipse.equinox.http.servlet");
      requiredBundles.add("org.eclipse.equinox.preferences");
      requiredBundles.add("org.eclipse.equinox.registry");
      requiredBundles.add("org.eclipse.equinox.util");

      requiredBundles.add("org.eclipse.osee.framework.branch.management");
      requiredBundles.add("org.eclipse.osee.framework.core");
      requiredBundles.add("org.eclipse.osee.framework.core.server");
      requiredBundles.add("org.eclipse.osee.framework.database");
      requiredBundles.add("org.eclipse.osee.framework.jdbcodbc");
      requiredBundles.add("org.eclipse.osee.framework.jdk.core");
      requiredBundles.add("org.eclipse.osee.framework.logging");
      requiredBundles.add("org.eclipse.osee.framework.manager.servlet");
      requiredBundles.add("org.eclipse.osee.framework.postgresql");
      requiredBundles.add("org.eclipse.osee.framework.resource.locator.attribute");
      requiredBundles.add("org.eclipse.osee.framework.resource.management");
      requiredBundles.add("org.eclipse.osee.framework.search.engine");
      requiredBundles.add("org.eclipse.osee.framework.types");

      requiredBundles.add("org.eclipse.osgi");
      requiredBundles.add("org.eclipse.osgi.services");
      requiredBundles.add("org.eclipse.team.core");
      requiredBundles.add("org.eclipse.xtext");
      requiredBundles.add("org.eclipse.xtext.logging");
      requiredBundles.add("org.eclipse.xtext.util");
      requiredBundles.add("org.hamcrest.core");
      requiredBundles.add("org.junit4");
      requiredBundles.add("org.mortbay.jetty.server");
      requiredBundles.add("org.mortbay.jetty.util");
   }

   public void _sos(CommandInterpreter ci) {
      specifyRequiredBundles();

      System.out.println("Inactive OSEE bundles:");
      printBundles(resolvedOseeBundles());

      System.out.println("Suspicious bundles:");
      Collection<Bundle> ext = nonOseeBundles();
      removeByName(ext, requiredBundles);
      printBundles(ext);

      Collection<String> present = toNames(allBundles());
      requiredBundles.removeAll(present);
      System.out.println("Missing bundles:");
      for (String p : requiredBundles) {
         System.out.println("\t" + p);
      }
   }

   private void removeByName(Collection<Bundle> bundles, Collection<String> names) {
      for (Object bundle : bundles.toArray()) {
         String name = ((Bundle) bundle).getSymbolicName();
         if (names.contains(name)) {
            bundles.remove(bundle);
         }
      }
   }

   private Collection<String> toNames(Collection<Bundle> bundles) {
      Collection<String> ret = new HashSet<String>();
      for (Bundle bundle : bundles) {
         ret.add(bundle.getSymbolicName());
      }
      return ret;
   }

   private List<Bundle> resolvedOseeBundles() {
      Bundle[] bundles = Activator.getInstance().getContext().getBundles();
      List<Bundle> toReturn = new ArrayList<Bundle>();
      for (Bundle bundle : bundles) {
         String bundleName = bundle.getSymbolicName();
         int bundleState = bundle.getState();
         boolean isOsee = isOseeBundle(bundle);
         boolean isNotActive = bundleState != Bundle.ACTIVE;
         boolean isOseeNonTest = isOsee & !bundleName.contains(".test");
         if (isOseeNonTest && isNotActive) {
            toReturn.add(bundle);
         }
      }
      return toReturn;
   }

   private boolean isOseeBundle(Bundle bundle) {
      String bundleName = bundle.getSymbolicName();
      return bundleName.matches(".*(osee|lba|postgres).*");
   }

   private Set<Bundle> allBundles() {
      Bundle[] bundles = Activator.getInstance().getContext().getBundles();
      Set<Bundle> toReturn = new HashSet<Bundle>();
      for (Bundle bundle : bundles) {
         toReturn.add(bundle);
      }
      return toReturn;
   }

   private Set<Bundle> nonOseeBundles() {
      Set<Bundle> toReturn = new HashSet<Bundle>();
      for (Bundle bundle : allBundles()) {
         boolean isNonOsee = !isOseeBundle(bundle);
         if (isNonOsee) {
            toReturn.add(bundle);
         }
      }
      return toReturn;
   }

   private void printBundles(Collection<Bundle> bundles) {
      ArrayList<Bundle> sorted = new ArrayList<Bundle>();
      for (Bundle bundle : bundles) {
         sorted.add(bundle);
      }

      Collections.sort(sorted, new Comparator<Bundle>() {
         @Override
         public int compare(Bundle o1, Bundle o2) {
            return o1.getSymbolicName().compareTo(o2.getSymbolicName());
         }
      });

      for (Bundle bundle : sorted) {
         printBundle(bundle);
      }
   }

   private void printBundle(Bundle bundle) {
      System.out.println(String.format("\t%s\t%s", bundle.getBundleId(), bundle.getSymbolicName()));
   }

   @Override
   public String getHelp() {
      StringBuilder help = new StringBuilder();
      help.append("---OSEE-OSGi Debugger---\n");
      help.append("        sos - Diagnose launch configuration issues\n");
      return help.toString();
   }
}

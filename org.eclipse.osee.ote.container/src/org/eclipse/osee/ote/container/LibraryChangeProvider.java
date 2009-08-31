/*
 * Created on Aug 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.container;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.framework.ui.workspacebundleloader.JarCollectionNature;

public class LibraryChangeProvider<T extends JarCollectionNature> {
   
   private List<IUserLibListener> listeners;
   
   /**
    * 
    */
   public LibraryChangeProvider() {
      listeners = new ArrayList<IUserLibListener>();
   }
   
   public void addListener(IUserLibListener listener) {
      if( !listeners.contains(listener))
         listeners.add(listener);
   }

   public void handleBundleAdded(URL url) {
      resetClasspath();
   }

   /**
    * 
    */
   @SuppressWarnings("restriction")
   private void resetClasspath() {
      for( IUserLibListener listener : listeners )
         listener.libraryChanged();
//      try {
//         this.oteClasspathContainer.javaProject.getPerProjectInfo().resetResolvedClasspath();
//      }
//      catch (JavaModelException ex) {
//         ex.printStackTrace();
//      }
   }

   public void handleBundleChanged(URL url) {
   }

   public void handleBundleRemoved(URL url) {
      resetClasspath();
   }

   public void handleNatureClosed(T nature) {
      resetClasspath();
   }

   public void handlePostChange() {
   }
   
}
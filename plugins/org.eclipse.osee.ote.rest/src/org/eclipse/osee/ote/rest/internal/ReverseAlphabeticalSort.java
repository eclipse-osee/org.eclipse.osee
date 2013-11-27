package org.eclipse.osee.ote.rest.internal;

import java.io.File;
import java.util.Comparator;

public class ReverseAlphabeticalSort implements Comparator<File> {

   @Override
   public int compare(File arg0, File arg1) {
      return arg1.getName().compareToIgnoreCase(arg0.getName());
   }

}

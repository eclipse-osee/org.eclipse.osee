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
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.styledText.IDictionary;

/**
 * Dictionary provided by OSEE that includes all dictionarys through the OseeDictionary extension point.
 * 
 * @author Donald G. Dunne
 */
public class OseeDictionary implements IDictionary {

   private static final OseeDictionary instance = new OseeDictionary();

   // Remove any junky characters and check for acronyms and other known
   // non-word type stuff. Return valid word to check in dictionary OR
   // "" if there is no word in this string
   // eg now) = now
   // a..b = ""
   // SQA = ""
   // NEon = ""
   private static final Pattern pattern = Pattern.compile("^[a-zA-Z]{1}[a-z]+$");

   private final Set<IOseeDictionary> dictionaries = new HashSet<IOseeDictionary>();

   private volatile boolean isLoadInProgress = false;
   private volatile boolean wasLoaded = false;

   public static OseeDictionary getInstance() {
      return instance;
   }

   public synchronized static void load() {
      // only load once
      getInstance().ensureLoaded();
   }

   @Override
   public boolean isWord(String word) {
      ensureLoaded();
      // Just return true till dictionary loaded
      if (!wasLoaded || isLoadInProgress) {
         return true;
      }

      //       System.out.println("Lookup => \""+word+"\"");
      String cleanWord = getCleanWord(word);
      if (cleanWord.equals("") || cleanWord.length() == 1) {
         return true;
      }

      for (IOseeDictionary dict : dictionaries) {
         if (dict.isWord(cleanWord)) {
            return true;
         }
      }

      return false;
   }

   private synchronized void ensureLoaded() {
      if (!wasLoaded && !isLoadInProgress) {
         if (!Platform.isRunning()) {
            return;
         }
         IOperation op = new LoadDictionaryOperation(dictionaries);
         Operations.executeAsJob(op, false, Job.LONG, new JobChangeAdapter() {

            @Override
            public void done(IJobChangeEvent event) {
               super.done(event);
               wasLoaded = true;
               isLoadInProgress = false;
            }
         });
         isLoadInProgress = true;
      }
   }

   public String getCleanWord(String word) {
      String cleanWord = word;
      // Single character is a valid word
      if (cleanWord.length() == 1) {
         return cleanWord;
      }

      // First, remove any non-word characters before and after string
      // eg. end. (now) it!
      cleanWord = cleanWord.replaceAll("^\\W+", "");
      cleanWord = cleanWord.replaceAll("\\W+$", "");
      cleanWord = cleanWord.replaceAll("'s$", ""); // Get rid of 's at end of word

      // If any non-alphabetic characters still in string, not a word
      // If string not either all lowercase or first letter capitalized, not a
      // word
      Matcher matcher = pattern.matcher(cleanWord);
      if (!matcher.find()) {
         return "";
      }
      return cleanWord.toLowerCase();
   }

   private static class LoadDictionaryOperation extends AbstractOperation {
      private final Collection<IOseeDictionary> toLoad;

      public LoadDictionaryOperation(Collection<IOseeDictionary> toLoad) {
         super("Loading Osee Dictionary", Activator.PLUGIN_ID);
         this.toLoad = toLoad;
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         OseeLog.log(Activator.class, Level.INFO, "Loading Osee Dictionary");
         ExtensionDefinedObjects<IOseeDictionary> contributions =
            new ExtensionDefinedObjects<IOseeDictionary>("org.eclipse.osee.framework.ui.skynet.OseeDictionary",
               "OseeDictionary", "classname");
         List<IOseeDictionary> dictionaries = contributions.getObjects();
         toLoad.addAll(dictionaries);
         OseeLog.log(Activator.class, Level.INFO, "Loaded Osee Dictionary");
      }
   }

}

/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.model.change;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * @author Roberto E. Escobar
 * @author Loren K. Ashley
 */
public class CompareData implements ToMessage {

   private final Map<String, String> dataToCompare;

   /**
    * The full path with filename to write the Visual Basic generation script to.
    */

   private final String generatorScriptPath;

   private final List<String> mergeList;

   /**
    * The full path with filename to write the result file to.
    */

   private final String outputPath;

   /**
    * The relative path below the workspace presentation to write intermediate file to.
    */

   private final String pathPrefix;

   /**
    * The type of presentation being rendered. This is used to locate the workspace presentation folder.
    */

   private final PresentationType presentationType;

   /**
    * Creates a new {@link CompareData} object initialized with the rendering parameters.
    *
    * @param presentationType the {@link PresentationType} being rendered.
    * @param pathPrefix a relative path below the the workspace presentation folder. This parameter must be
    * non-<code>null</code> but may be an empty {@link CharSequence}.
    * @param outputPath the absolute path to write the rendered file to.
    * @param generatorScriptPath the absolute path to write the Visual Basic generator script to.
    * @throws NUllPointerException when any of the parameters are <code>null</code>.
    */

   public CompareData(PresentationType presentationType, CharSequence pathPrefix, CharSequence outputPath, CharSequence generatorScriptPath) {
      this.presentationType = Objects.requireNonNull(presentationType,
         "CompareData::new, The parameter \"presentationType\" cannot be null.");
      this.pathPrefix = Objects.requireNonNull(pathPrefix,
         "CompareData::new, The parameter \"pathPrefix\" cannot be null.").toString();
      this.outputPath = Objects.requireNonNull(outputPath,
         "COmpareData::new, The parameter \"outputPath\" cannot be null.").toString();
      this.generatorScriptPath = Objects.requireNonNull(generatorScriptPath,
         "CompareData::new, The parameter \"generatorScriptPath\" cannot be null.").toString();
      this.dataToCompare = new LinkedHashMap<>();
      this.mergeList = new ArrayList<>();
   }

   public void add(String file1Location, String file2Location) {
      this.dataToCompare.put(file1Location, file2Location);
   }

   public void addMerge(String fileLocation) {
      if (fileLocation != null && fileLocation.length() > 0) {
         this.mergeList.add(fileLocation);
      }
   }

   public void clear() {
      this.dataToCompare.clear();
   }

   public Set<Entry<String, String>> entrySet() {
      return this.dataToCompare.entrySet();
   }

   /**
    * Gets the path for the Visual Basic generator script.
    *
    * @return the absolute file path as a {@link String}.
    */

   public String getGeneratorScriptPath() {
      return this.generatorScriptPath;
   }

   public String getOutputPath() {
      return this.outputPath;
   }

   /**
    * Gets the relative path under the workspace presentation folder to create intermediate file.
    *
    * @return a relative path as a {@link String}.
    */

   public String getPathPrefix() {
      return this.pathPrefix;
   }

   /**
    * Gets the {@link PresentationType} being rendered.
    *
    * @return the {@link PresentationType}.
    */

   public PresentationType getPresentationType() {
      return this.presentationType;
   }

   public boolean isEmpty() {
      return this.dataToCompare.isEmpty();
   }

   public boolean isMerge(String fileLocation) {
      return this.mergeList.contains(fileLocation);
   }

   public int size() {
      return this.dataToCompare.size();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "CompareData" )
         .indentInc()
         .segment           ( "Presentation Type",     this.presentationType    )
         .segment           ( "Path Prefix",           this.pathPrefix          )
         .segment           ( "Output Path",           this.outputPath          )
         .segment           ( "Generator Script Path", this.generatorScriptPath )
         .segmentIndexedList( "Merge List",            this.mergeList           )
         .segmentMap        ( "Data To Compare",       this.dataToCompare       )
         .indentDec()
         ;
      //@formatter:on
      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }
}

/* EOF */

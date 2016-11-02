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

package org.eclipse.osee.framework.database.init.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.database.init.DefaultDbInitTasks;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

public class SimpleTemplateProviderTask implements IDbInitializationTask {
   private static String EDIT_RENDERER_OPTIONS =
      "{\"ElementType\" : \"Artifact\", \"OutliningOptions\" : [ {\"Outlining\" : true, \"RecurseChildren\" : false, \"HeadingAttributeType\" : \"Name\", \"ArtifactName\" : \"Default\", \"OutlineNumber\" : \"\" }], \"AttributeOptions\" : [{\"AttrType\" : \"Word Template Content\",  \"Label\" : \"\", \"FormatPre\" : \"\", \"FormatPost\" : \"\"}]}";
   private static String RECURSIVE_RENDERER_OPTIONS =
      "{\"ElementType\" : \"Artifact\", \"OutliningOptions\" : [ {\"Outlining\" : true, \"RecurseChildren\" : true, \"HeadingAttributeType\" : \"Name\", \"ArtifactName\" : \"Default\", \"OutlineNumber\" : \"\" }], \"AttributeOptions\" : [{\"AttrType\" : \"*\",  \"Label\" : \"\", \"FormatPre\" : \"\", \"FormatPost\" : \"\"}]}";
   private static String MASTER_RENDERER_OPTIONS =
      "{\"ElementType \" :  \"NestedTemplate \",  \"NestedTemplates \" : [{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.1 \",  \"SubDocName \" :  \"Communication Subsystem Crew Interface \",  \"Key \" :  \"Name \",  \"Value \" :  \"Communication Subsystem Crew Interface \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.2 \",  \"SubDocName \" :  \"Navigation Subsystem Crew Interface \",  \"Key \" :  \"Name \",  \"Value \" :  \"Navigation Subsystem Crew Interface \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.3 \",  \"SubDocName \" :  \"Aircraft Systems Management Subsystem Crew Interface \",  \"Key \" :  \"Name \",  \"Value \" :  \"Aircraft Systems Management Subsystem Crew Interface \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.4 \",  \"SubDocName \" :  \"Controls and Display Subsystem Crew Interface \",  \"Key \" :  \"Name \",  \"Value \" :  \"Controls and Display Subsystem Crew Interface \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.5 \",  \"SubDocName \" :  \"Sight Subsystem Crew Interface \",  \"Key \" :  \"Name \",  \"Value \" :  \"Sight Subsystem Crew Interface \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.6 \",  \"SubDocName \" :  \"Armament Subsystem Crew Interface \",  \"Key \" :  \"Name \",  \"Value \" :  \"Armament Subsystem Crew Interface \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.7 \",  \"SubDocName \" :  \"Data Management Subsystem Crew Interface \",  \"Key \" :  \"Name \",  \"Value \" :  \"Data Management Subsystem Crew Interface \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.8 \",  \"SubDocName \" :  \"Aircraft Survivability Subsystem Crew Interface \",  \"Key \" :  \"Name \",  \"Value \" :  \"Aircraft Survivability Subsystem Crew Interface \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.9 \",  \"SubDocName \" :  \"Flight Controls Subsystem Crew Interface \",  \"Key \" :  \"Name \",  \"Value \" :  \"Flight Controls Subsystem Crew Interface \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.10 \",  \"SubDocName \" :  \"Unmanned Systems Management Subsystem Crew Interface \",  \"Key \" :  \"Name \",  \"Value \" :  \"Unmanned Systems Management Subsystem Crew Interface \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.11 \",  \"SubDocName \" :  \"Mission System Management Subsystem Functions \",  \"Key \" :  \"Name \",  \"Value \" :  \"Mission System Management Subsystem Functions \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.12 \",  \"SubDocName \" :  \"Communications Subsystem Functions \",  \"Key \" :  \"Name \",  \"Value \" :  \"Communications Subsystem Functions \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.13 \",  \"SubDocName \" :  \"Navigation Subsystem Functions \",  \"Key \" :  \"Name \",  \"Value \" :  \"Navigation Subsystem Functions \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.14 \",  \"SubDocName \" :  \"Aircraft Systems Subsystem Management Functions \",  \"Key \" :  \"Name \",  \"Value \" :  \"Aircraft Systems Subsystem Management Functions \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.15 \",  \"SubDocName \" :  \"Controls and Displays Subsystem Functions \",  \"Key \" :  \"Name \",  \"Value \" :  \"Controls and Displays Subsystem Functions \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.16 \",  \"SubDocName \" :  \"Sights Subsystem Functions \",  \"Key \" :  \"Name \",  \"Value \" :  \"Sights Subsystem Functions \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.17 \",  \"SubDocName \" :  \"Armament Subsystem Functions \",  \"Key \" :  \"Name \",  \"Value \" :  \"Armament Subsystem Functions \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.18 \",  \"SubDocName \" :  \"Data Management Subsystem Functions \",  \"Key \" :  \"Name \",  \"Value \" :  \"Data Management Subsystem Functions \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.19 \",  \"SubDocName \" :  \"Flight Control Subsystem Functions \",  \"Key \" :  \"Name \",  \"Value \" :  \"Flight Control Subsystem Functions \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.20 \",  \"SubDocName \" :  \"Network Centric Operations Subsystem Functions \",  \"Key \" :  \"Name \",  \"Value \" :  \"Network Centric Operations Subsystem Functions \"},{ \"OutlineType \" :  \" \",  \"SectionNumber \" :  \"3.2.21 \",  \"SubDocName \" :  \"Unmanned Systems Management Subsystem Functions \",  \"Key \" :  \"Name \",  \"Value \" :  \"Unmanned Systems Management Subsystem Functions \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"A.0 \",  \"SubDocName \" :  \"Appendix A - Acronyms and Abbreviations \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix A - Acronyms and Abbreviations \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"B.0 \",  \"SubDocName \" :  \"Appendix B - Laser Range Validator Requirements \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix B - Laser Range Validator Requirements \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"C.0 \",  \"SubDocName \" :  \"Appendix C - Display Character Fonts on the Longbow Apache \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix C - Display Character Fonts on the Longbow Apache \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"D.0 \",  \"SubDocName \" :  \"Appendix D - Display Symbol Set on the Longbow Apache \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix D - Display Symbol Set on the Longbow Apache \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"E.0 \",  \"SubDocName \" :  \"Appendix E - Warnings, Cautions, Advisories on the Longbow Apache \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix E - Warnings, Cautions, Advisories on the Longbow Apache \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"F.0 \",  \"SubDocName \" :  \"Appendix F - INU Lever Arm Compensation \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix F - INU Lever Arm Compensation \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"G.0 \",  \"SubDocName \" :  \"Appendix G - Checklists on the Longbow Apache \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix G - Checklists on the Longbow Apache \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"H.0 \",  \"SubDocName \" :  \"Appendix H - Map Algorithms on the Longbow Apache \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix H - Map Algorithms on the Longbow Apache \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"I.0 \",  \"SubDocName \" :  \"Appendix I - SA Symbol Mapping on the Longbow Apache \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix I - SA Symbol Mapping on the Longbow Apache \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"J.0 \",  \"SubDocName \" :  \"Appendix J - 2525b Symbology \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix J - 2525b Symbology \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"N.0 \",  \"SubDocName \" :  \"Appendix N - Common Navigation Related Computations \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix N - Common Navigation Related Computations \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"O.0 \",  \"SubDocName \" :  \"Appendix O -  7 State Kalman Filter for Target Tracking \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix O -  7 State Kalman Filter for Target Tracking \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"P.0 \",  \"SubDocName \" :  \"Appendix P -  Gun Fire Control Equations \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix P -  Gun Fire Control Equations \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"Q.0 \",  \"SubDocName \" :  \"Appendix Q -  Rocket Fire Control Equations \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix Q -  Rocket Fire Control Equations \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"R.0 \",  \"SubDocName \" :  \"Appendix R -  Fire Control Equation Coefficients \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix R -  Fire Control Equation Coefficients \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"S.0 \",  \"SubDocName \" :  \"Appendix S -  Body Bending Equations \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix S -  Body Bending Equations \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"W.0 \",  \"SubDocName \" :  \"Appendix W -  FMS DMS Fault Codes \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix W -  FMS DMS Fault Codes \"},{ \"OutlineType \" :  \"APPENDIX \",  \"SectionNumber \" :  \"X.0 \",  \"SubDocName \" :  \"Appendix X - Messenger Table \",  \"Key \" :  \"Name \",  \"Value \" :  \"Appendix X - Messenger Table \"}]}";

   @Override
   public void run() {
      try {
         processTemplatesForDBInit();
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private void processTemplatesForDBInit() throws IOException {

      Artifact templateFolder = getTemplateFolder();
      IExtensionPoint ep = Platform.getExtensionRegistry().getExtensionPoint(
         "org.eclipse.osee.framework.ui.skynet.SimpleTemplateProviderTemplate");
      for (IExtension extension : ep.getExtensions()) {
         for (IConfigurationElement el : extension.getConfigurationElements()) {
            Artifact templateArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplate, COMMON);
            String filePath = el.getAttribute("File");
            String name = filePath.substring(filePath.lastIndexOf('/') + 1);
            name = name.substring(0, name.lastIndexOf('.'));
            URL url = Platform.getBundle(el.getContributor().getName()).getEntry(filePath);

            if (url != null) {
               templateArtifact.setName(name);
               if (name.equals("Word Edit Template")) {
                  templateArtifact.setSoleAttributeFromString(CoreAttributeTypes.RendererOptions,
                     EDIT_RENDERER_OPTIONS);
               } else if (name.equals(DefaultDbInitTasks.PREVIEW_ALL_RECURSE)) {
                  templateArtifact.setSoleAttributeFromString(CoreAttributeTypes.RendererOptions,
                     RECURSIVE_RENDERER_OPTIONS);
               } else if (name.equals("srsMasterTemplate")) {
                  templateArtifact.setSoleAttributeFromString(CoreAttributeTypes.RendererOptions,
                     MASTER_RENDERER_OPTIONS);
               }
               templateArtifact.setSoleAttributeFromStream(CoreAttributeTypes.WholeWordContent, url.openStream());
               for (IConfigurationElement matchCriteriaEl : el.getChildren()) {
                  String match = matchCriteriaEl.getAttribute("match");
                  templateArtifact.addAttribute(CoreAttributeTypes.TemplateMatchCriteria, match);
               }
               templateArtifact.persist(getClass().getSimpleName());
               templateFolder.addChild(templateArtifact);
            } else {
               OseeLog.logf(SimpleTemplateProviderTask.class, Level.SEVERE, "Problem loading file %s", filePath);
            }
         }
      }
      templateFolder.persist(getClass().getSimpleName());
   }

   private Artifact getTemplateFolder() {
      Artifact templateFolder =
         ArtifactQuery.checkArtifactFromTypeAndName(CoreArtifactTypes.HeadingMSWord, "Document Templates", COMMON);
      if (templateFolder == null) {
         Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(COMMON);

         templateFolder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, COMMON, "Document Templates");
         rootArt.addChild(templateFolder);
         templateFolder.persist(getClass().getSimpleName());
      }
      return templateFolder;
   }
}

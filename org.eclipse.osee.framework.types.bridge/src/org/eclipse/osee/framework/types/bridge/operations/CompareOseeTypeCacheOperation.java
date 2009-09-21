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
package org.eclipse.osee.framework.types.bridge.operations;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSnapshot;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.compare.ui.editor.ModelCompareEditorInput;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.types.ArtifactTypeCache;
import org.eclipse.osee.framework.skynet.core.types.AttributeTypeCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeEnumTypeCache;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.RelationTypeCache;
import org.eclipse.osee.framework.skynet.core.types.impl.DatabaseArtifactTypeAccessor;
import org.eclipse.osee.framework.skynet.core.types.impl.DatabaseAttributeTypeAccessor;
import org.eclipse.osee.framework.skynet.core.types.impl.DatabaseOseeEnumTypeAccessor;
import org.eclipse.osee.framework.skynet.core.types.impl.DatabaseRelationTypeAccessor;
import org.eclipse.osee.framework.types.bridge.internal.Activator;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Roberto E. Escobar
 */
public class CompareOseeTypeCacheOperation extends AbstractOperation {

   private final OseeTypeCache modifiedCache;

   /**
    * @param operationName
    * @param pluginId
    */
   public CompareOseeTypeCacheOperation(OseeTypeCache modifiedCache) {
      super("Compare Osee Type Changes", Activator.PLUGIN_ID);
      this.modifiedCache = modifiedCache;
   }

   private OseeTypeCache createEmptyCache() {
      IOseeTypeFactory factory = new OseeTypeFactory();
      OseeEnumTypeCache enumCache = new OseeEnumTypeCache(factory, new DatabaseOseeEnumTypeAccessor());
      AttributeTypeCache attrCache = new AttributeTypeCache(factory, new DatabaseAttributeTypeAccessor(enumCache));

      ArtifactTypeCache artCache = new ArtifactTypeCache(factory, new DatabaseArtifactTypeAccessor(attrCache));
      RelationTypeCache relCache = new RelationTypeCache(factory, new DatabaseRelationTypeAccessor(artCache));

      OseeTypeCache storeCache = new OseeTypeCache(factory, artCache, attrCache, relCache, enumCache);
      return storeCache;
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      Map<String, OseeTypeModel> changedModels = new HashMap<String, OseeTypeModel>();
      doSubWork(new OseeToXtextOperation(modifiedCache, changedModels), monitor, 0.20);

      OseeTypeCache storeCache = createEmptyCache();
      storeCache.ensurePopulated();
      Map<String, OseeTypeModel> baseModels = new HashMap<String, OseeTypeModel>();
      doSubWork(new OseeToXtextOperation(storeCache, baseModels), monitor, 0.20);

      OseeTypeModel changedModel = null;
      OseeTypeModel baseModel = null;
      for (String key : changedModels.keySet()) {
         changedModel = changedModels.get(key);
         baseModel = baseModels.get(key);
      }

      monitor.setTaskName("Matching models");

      //      final File before = OseeData.getFile("before.osee");
      //      final File after = OseeData.getFile("after.osee");
      //      OseeTypeModelUtil.saveModel(before.toURI(), baseModel);
      //      OseeTypeModelUtil.saveModel(after.toURI(), changedModel);

      URI uri = URI.createURI("http://org.eclipse/osee/types/oseetypecache");
      final ResourceSet resourceSet1 = new ResourceSetImpl();
      Resource resource1 = resourceSet1.createResource(uri);
      resource1.getContents().add(baseModel);

      final ResourceSet resourceSet2 = new ResourceSetImpl();
      Resource resource2 = resourceSet2.createResource(uri);
      resource2.getContents().add(changedModel);

      //      final EObject model1 = ModelUtils.load(before, resourceSet1);
      //      final EObject model2 = ModelUtils.load(after, resourceSet2);
      openCompare(monitor, baseModel, changedModel);

   }

   private void openCompare(IProgressMonitor monitor, final EObject model1, final EObject model2) throws Exception {

      monitor.setTaskName("Comparing models");
      final MatchModel match = MatchService.doMatch(model1, model2, model1, null);
      final DiffModel diff = DiffService.doDiff(match, true);

      //      monitor.setTaskName("Merging difference");
      //      final List<DiffElement> differences = new ArrayList<DiffElement>(diff.getOwnedElements());
      //      MergeService.merge(differences, true);

      monitor.setTaskName("Opening Editor");

      final ComparisonResourceSnapshot snapshot = DiffFactory.eINSTANCE.createComparisonResourceSnapshot();
      snapshot.setDate(Calendar.getInstance().getTime());
      snapshot.setMatch(match);
      snapshot.setDiff(diff);

      Job job = new UIJob("Open Compare") {

         @Override
         public IStatus runInUIThread(IProgressMonitor monitor) {
            IStatus status;

            try {
               CompareEditorInput input = new ModelCompareEditorInput(snapshot);
               IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
               page.openEditor(input, "org.eclipse.compare.CompareEditor", true);
               status = Status.OK_STATUS;
            } catch (Exception ex) {
               status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error opening compare editor", ex);
            }
            return status;
         }
      };
      Jobs.startJob(job);
   }
}

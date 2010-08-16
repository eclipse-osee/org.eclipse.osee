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

package org.eclipse.osee.framework.svn;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.svn.entry.IRepositoryEntry;
import org.eclipse.osee.framework.svn.entry.NullRepositoryEntry;
import org.eclipse.osee.framework.svn.entry.RepositoryEntry;
import org.eclipse.osee.framework.svn.enums.RepositoryEnums.ControlledType;
import org.eclipse.osee.framework.svn.enums.RepositoryEnums.EntryFields;
import org.eclipse.osee.framework.svn.internal.Activator;
import org.eclipse.team.svn.core.connector.ISVNConnector.Depth;
import org.eclipse.team.svn.core.connector.SVNEntryInfo;
import org.eclipse.team.svn.core.operation.remote.CheckoutOperation;
import org.eclipse.team.svn.core.resource.ILocalResource;
import org.eclipse.team.svn.core.resource.IRepositoryLocation;
import org.eclipse.team.svn.core.resource.IRepositoryResource;
import org.eclipse.team.svn.core.resource.IRepositoryRoot;
import org.eclipse.team.svn.core.svnstorage.SVNRemoteStorage;
import org.eclipse.team.svn.core.utility.SVNUtility;
import org.eclipse.team.svn.ui.action.remote.CheckoutAction;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class SvnAPI {
   private static SvnAPI instance = null;
   private static final String SVN_REPOSITORY_TYPE = "SVN";

   private SvnAPI() {
      super();
   }

   public static SvnAPI getInstance() {
      if (instance == null) {
         instance = new SvnAPI();
      }
      return instance;
   }

   public boolean isSVNRepositoryType(String repositoryType) {
      return SVN_REPOSITORY_TYPE.equalsIgnoreCase(repositoryType);
   }

   public boolean isValidUrl(String url) {
      return SVNUtility.isValidSVNURL(url);
   }

   protected boolean isSvn(File file) {
      File svn = new File(file.getParentFile(), SVNUtility.getSVNFolderName());
      return svn.exists();
   }

   protected IRepositoryEntry getSVNInfo(File file) {
      IRepositoryEntry toReturn = new NullRepositoryEntry();
      SVNEntryInfo info = SVNUtility.getSVNInfo(file);
      if (info != null) {
         toReturn = toRepositoryEntry(file, info);
      }
      return toReturn;
   }

   private RepositoryEntry toRepositoryEntry(File file, SVNEntryInfo info) {
      String entryType = "undefined";
      if (info.kind >= 0 && info.kind < NodeKind.NAMES.length) {
         entryType = NodeKind.NAMES[info.kind];
      }
      DateFormat dateFormat =
         DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
      final RepositoryEntry entry = new RepositoryEntry(entryType, getVersionControlSystem());
      entry.addField(EntryFields.checksum, info.checksum);
      entry.addField(EntryFields.committedRev, Long.toString(info.lastChangedRevision));
      entry.addField(EntryFields.fileName, info.path);
      entry.addField(EntryFields.committeDate, dateFormat.format(new Date(info.lastChangedDate)));
      entry.addField(EntryFields.url, info.url);
      entry.addField(EntryFields.lastAuthor, info.lastChangedAuthor);
      entry.addField(EntryFields.kind,
         info.kind == NodeKind.dir ? ControlledType.dir.name() : ControlledType.file.name());
      entry.addField(EntryFields.uuid, info.reposUUID);
      entry.addField(EntryFields.repository, info.reposRootUrl);
      entry.addField(EntryFields.currentRevision, Long.toString(info.revision));
      entry.addField(EntryFields.textTime, dateFormat.format(new Date(info.textTime)));
      entry.addField(EntryFields.dateCommitted, dateFormat.format(new Date(info.lastChangedDate)));
      entry.addField(EntryFields.properTime, dateFormat.format(new Date(info.propTime)));

      try {
         IFile iFile = AIFile.constructIFile(file);
         ILocalResource local = SVNRemoteStorage.instance().asLocalResource(iFile);
         entry.setModifiedFlag(SVNUtility.getStatusText(local.getStatus()));
      } catch (OseeArgumentException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return entry;
   }

   protected String getVersionControlSystem() {
      return "svn";
   }

   public void addRepositoryLocation(String reference) {
      SVNRemoteStorage remoteStorage = SVNRemoteStorage.instance();
      IRepositoryLocation newRepository = remoteStorage.newRepositoryLocation(reference);
      remoteStorage.addRepositoryLocation(newRepository);
   }

   public void checkOut(String[] filesToCheckout, final IProgressMonitor monitor) {
      checkOut(filesToCheckout, null, monitor);
   }

   public IStatus checkOut(String[] filesToCheckout, String wsLocation, final IProgressMonitor monitor) {
      IStatus status = Status.OK_STATUS;
      HashMap<String, IRepositoryResource> checkoutMap = toMap(getRepositoryResources(filesToCheckout));
      if (checkoutMap.size() > 0) {
         CheckoutOperationBuilder operationBuilder = new CheckoutOperationBuilder(checkoutMap, wsLocation);
         CheckoutOperation op = operationBuilder.getOperation();
         op.run(monitor);
         status = op.getStatus();
      }
      return status;
   }

   public IRepositoryRoot getRepositoryRoot(String url) {
      IRepositoryRoot repository = null;
      IRepositoryLocation[] locations = SVNRemoteStorage.instance().getRepositoryLocations();
      for (IRepositoryLocation location : locations) {
         IRepositoryRoot root = location.getRoot();
         String repositoryUrl = root.getUrl();
         if (url.startsWith(repositoryUrl)) {
            repository = root;
         }
      }
      return repository;
   }

   private IRepositoryResource[] getRepositoryResources(String[] fileToCheckout) {
      // TODO: Create Repository Locations if they are not available.
      List<IRepositoryResource> toReturn = new ArrayList<IRepositoryResource>();
      IRepositoryLocation[] locations = SVNRemoteStorage.instance().getRepositoryLocations();
      for (IRepositoryLocation location : locations) {
         IRepositoryRoot root = location.getRoot();
         String repository = root.getUrl();
         for (String target : fileToCheckout) {
            if (target.startsWith(repository)) {
               IRepositoryResource repositoryResource = root.asRepositoryFile(target, false);
               if (repositoryResource != null) {
                  toReturn.add(repositoryResource);
               }
            }
         }
      }
      return toReturn.toArray(new IRepositoryResource[toReturn.size()]);
   }

   private HashMap<String, IRepositoryResource> toMap(IRepositoryResource[] resources) {
      HashMap<String, IRepositoryResource> checkoutMap = new HashMap<String, IRepositoryResource>();
      for (IRepositoryResource repositoryResource : resources) {
         checkoutMap.put(repositoryResource.getName(), repositoryResource);
      }
      return checkoutMap;
   }

   public URI getLocalFileMatchingRepositoryUrl(String url, String revision) {
      return null;
   }

   private final class CheckoutOperationBuilder {
      private static final int WAIT_TIME = 30000;
      private CheckoutOperation checkoutOperation;
      private final HashMap<String, IRepositoryResource> checkoutMap;
      private boolean callbackReceived;
      private final String location;

      public CheckoutOperationBuilder(HashMap<String, IRepositoryResource> checkoutMap, String location) {
         this.checkoutMap = checkoutMap;
         this.callbackReceived = false;
         this.location = location;
      }

      public void callbackReceived() {
         synchronized (this) {
            this.notify();
            callbackReceived = true;
         }
      }

      protected boolean wasCallbackReceived() {
         return callbackReceived;
      }

      public CheckoutOperation getOperation() {
         PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
               checkoutOperation = getCheckoutOperation(checkoutMap);
               callbackReceived();
            }
         });
         synchronized (this) {
            if (!wasCallbackReceived()) {
               try {
                  this.wait(WAIT_TIME);
               } catch (InterruptedException ex) {
               }
            }
         }
         return checkoutOperation;
      }

      @SuppressWarnings("unchecked")
      private CheckoutOperation getCheckoutOperation(HashMap<String, IRepositoryResource> checkoutMap) {
         CheckoutOperation toReturn = null;
         if (checkoutMap != null) {
            HashMap resources2names = CheckoutAction.getResources2Names(checkoutMap);
            ArrayList operateResources =
               CheckoutAction.getOperateResources(checkoutMap, resources2names,
                  PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                  ResourcesPlugin.getWorkspace().getRoot().getLocation().toString(), true);

            if (operateResources.size() > 0) {
               IRepositoryResource[] checkoutSet =
                  (IRepositoryResource[]) operateResources.toArray(new IRepositoryResource[operateResources.size()]);
               HashMap operateMap = new HashMap();
               for (int i = 0; i < checkoutSet.length; i++) {
                  operateMap.put(resources2names.get(checkoutSet[i]), checkoutSet[i]);
               }
               try {
                  Class<?> clazz =
                     Platform.getBundle("org.eclipse.team.svn.core").loadClass(
                        "org.eclipse.team.svn.core.operation.remote.CheckoutOperation");
                  if (EclipseVersion.isVersion("3.3")) {
                     Constructor<?> constructor =
                        clazz.getConstructor(Map.class, boolean.class, String.class, boolean.class);
                     toReturn = (CheckoutOperation) constructor.newInstance(operateMap, false, location, true);
                  } else if (EclipseVersion.isVersion("3.4")) {
                     Constructor<?> constructor =
                        clazz.getConstructor(Map.class, boolean.class, String.class, int.class);
                     toReturn =
                        (CheckoutOperation) constructor.newInstance(operateMap, true, location, Depth.INFINITY, true);
                  }
               } catch (Exception ex) {
                  throw new UnsupportedOperationException();
               }
            }
         }
         return toReturn;
      }
   }
}

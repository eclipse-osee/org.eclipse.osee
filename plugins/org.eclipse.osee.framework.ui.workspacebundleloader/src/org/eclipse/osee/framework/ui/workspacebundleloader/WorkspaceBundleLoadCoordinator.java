	package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.FrameworkWiring;

public class WorkspaceBundleLoadCoordinator {

	private static final String TAG_VIEW = "view";
	private static final String TAG_PERSPECTIVE = "perspective";
	private static final String TAG_OTE_PRECOMPILED = "OTEPrecompiled";
	private static final String OTE_MEMENTO = "OTEMemento";
	private File temporaryBundleLocationFolder;
	private Set<String> bundlesToCheck;
	private BundleCollection managedArea = new BundleCollection();
	private FrameworkWiring wiring;
	
	public WorkspaceBundleLoadCoordinator(File temporaryBundleLocationFolder) {
		bundlesToCheck = new HashSet<String>();
		this.temporaryBundleLocationFolder = temporaryBundleLocationFolder;
		if(!temporaryBundleLocationFolder.exists()){
			if(!temporaryBundleLocationFolder.mkdirs()){
				this.temporaryBundleLocationFolder = makeTempFolder();
			}
		} else if(temporaryBundleLocationFolder.exists() && !temporaryBundleLocationFolder.isDirectory()){
			this.temporaryBundleLocationFolder = makeTempFolder();
		} else if(temporaryBundleLocationFolder.exists()){
			cleanOutDirectory();
		}
		
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		for(Bundle findit:bundle.getBundleContext().getBundles()){
			wiring = findit.adapt(FrameworkWiring.class);
			if(wiring != null){
				break;
			}
		}
	}
	
	/**
	 * should be a flat list of folders with the symbolic name of the bundle and then a version for each jar underneath each folder.
	 */
	private void cleanOutDirectory() {
		File[] symbolicNameFolders = this.temporaryBundleLocationFolder.listFiles();
		for(File folder:symbolicNameFolders){
			if(folder.isDirectory()){
				for(File file :folder.listFiles()){
					file.delete();
				}
				folder.delete();
			}
		}
	}

	private File makeTempFolder(){
		File folder = new File(System.getProperty("java.io.tmpdir"));
		File oteFolder = new File(folder, "otebundleload");
		if(!oteFolder.exists()){
			oteFolder.mkdirs();
		}
		return oteFolder;
	}
	
	File getFolder(){
		return temporaryBundleLocationFolder;
	}
	
	
	
	public synchronized void uninstallBundles(){
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable(){
			@Override
			public void run() {
				saveAndCloseManagedViews();
				for(BundleInfoLite info:managedArea.getInstalledBundles()){
					try {
						info.uninstall();
					} catch (BundleException e) {
						OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
					}
				}
			}
		});
		
		if(wiring != null){
			wiring.refreshBundles(null);
		}
	}

	private void saveAndCloseManagedViews() {
		Set<String> managedViewIds = determineManagedViews();
		
		IWorkbench workbench = PlatformUI.getWorkbench();     
		if (managedArea.getInstalledBundles().size() > 0 && workbench != null && workbench.getActiveWorkbenchWindow() != null){
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IPerspectiveDescriptor originalPerspective = page.getPerspective();
			XMLMemento memento = XMLMemento.createWriteRoot(TAG_OTE_PRECOMPILED);
			//find the view in other perspectives
			IPerspectiveDescriptor[] pd = page.getOpenPerspectives();
			for (int i = 0; i < pd.length; i++) {
				try {
					page.setPerspective(pd[i]);
				} catch (Exception ex) {
					// Ignore, this can get an NPE in Eclipse, see bug 4454
				} 
				IMemento perspectiveMemento = null;
				try{
					perspectiveMemento = memento.createChild(TAG_PERSPECTIVE);
					perspectiveMemento.putString("id", pd[i].getId());
				} catch (Exception ex){
					//Ignore, the perspective id is invalid xml
				}
				IViewReference[] activeReferences = page.getViewReferences();
				for (IViewReference viewReference : activeReferences) {
					if (managedViewIds.contains(viewReference.getId())){
						if(perspectiveMemento != null){
							try{
								IMemento viewMemento = perspectiveMemento.createChild(TAG_VIEW);
								viewMemento.putString("id", viewReference.getId());
								String secondaryId = viewReference.getSecondaryId();
								if(secondaryId != null){
									viewMemento.putString("secondId", secondaryId);
								}
								IWorkbenchPart part = viewReference.getPart(false);
								if(part instanceof IViewPart){
									IViewPart viewPart = (IViewPart)part;
									viewPart.saveState(viewMemento);
								}
							} catch (Exception ex){
								//Ignore, we failed during view save
							}
						}
						page.hideView(viewReference);
					}
				}
			}
			
			saveMementoToFile(memento);
			page.setPerspective(originalPerspective);
		}
	}

	private Set<String> determineManagedViews() {
		Set<String> managedViewIds = new HashSet<String>();
		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint("org.eclipse.ui.views");
		IExtension[] extensions = extensionPoint.getExtensions();
		for(IExtension ex:extensions){
			String name = ex.getContributor().getName();
			if(managedArea.getByBundleName(name) != null){
				IConfigurationElement[] elements = ex.getConfigurationElements();
				for(IConfigurationElement el:elements){
					if(el.getName().equals(TAG_VIEW)){
						String id = el.getAttribute("id");
						if(id != null){
							managedViewIds.add(id);
						}
					}
				}
			}
		}
		return managedViewIds;
	}
	
	private boolean saveMementoToFile(XMLMemento memento) {
		File stateFile = OseeData.getFile(OTE_MEMENTO);
		if (stateFile == null) {
			return false;
		}
		try {
			FileOutputStream stream = new FileOutputStream(stateFile);
			OutputStreamWriter writer = new OutputStreamWriter(stream, "utf-8"); //$NON-NLS-1$
			memento.save(writer);
			writer.close();
		} catch (IOException e) {
			stateFile.delete();
			return false;
		}
		return true;
	}
	
	 public static void copyFile(File source, File destination) throws IOException {
	      final FileChannel in = new FileInputStream(source).getChannel();
	      try {
	         final FileChannel out;
	         if (destination.isDirectory()) {
	            out = new FileOutputStream(new File(destination, source.getName())).getChannel();
	         } else {
	            if (destination.exists()) {
	               destination.delete(); // to work around some file permission
	            }
	            // problems
	            out = new FileOutputStream(destination).getChannel();
	         }
	         try {
	            long position = 0;
	            long size = in.size();
	            while (position < size) {
	               position += in.transferTo(position, size, out);
	            }
	         } finally {
	            Lib.close(out);
	         }
	      } finally {
	         Lib.close(in);
	      }
	   }
	
	private List<BundleInfoLite> copyDeltasToManagedFolder(List<BundleInfoLite> copies) {
		List<BundleInfoLite> lockedFiles = new ArrayList<BundleInfoLite>();
		for(BundleInfoLite info: copies){
			File folder = new File(temporaryBundleLocationFolder, info.getSymbolicName());
			folder.mkdirs();
			File newFile = new File(folder, info.getVersion() + ".jar");
			if(newFile.exists()){
				newFile.delete();
			}
			FileChannel out = null;
			FileChannel in = null;
			try {
				out = new FileOutputStream(newFile).getChannel();
				String path = info.getSystemLocation().toURI().getPath();
				in = new FileInputStream(new File(path)).getChannel();

				long position = 0;
				long size = in.size();
				while (position < size) {
					position += in.transferTo(position, size, out);
				}
				BundleInfoLite newBundle = new BundleInfoLite(newFile.toURI().toURL());
				managedArea.add(newBundle);
			} catch (IOException e) {
				OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
			} catch (URISyntaxException e) {
				OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
			} finally {
				try {
					if(in != null){
						in.close();
					}
				} catch (IOException e) {
				}
				try {
					if(out != null){
						out.close();
					}
				} catch (IOException e) {
				}
			}
		}
		return lockedFiles;
	}
	
	private List<BundleInfoLite> determineDeltasBetweenBundlesToLoad() {
		List<BundleInfoLite> bundlesToOperateOn = new ArrayList<BundleInfoLite>();
		for(String urlString:bundlesToCheck){
			try {
				URL newURL;
				try{
					newURL = new URL(urlString);
				} catch(MalformedURLException ex){
					newURL = new File(urlString).toURI().toURL();	
				}
				
				BundleInfoLite bundleInfo = new BundleInfoLite(newURL);
				List<BundleInfoLite> bundleList = managedArea.getByBundleName(bundleInfo.getSymbolicName());
				if(bundleList == null){
					bundlesToOperateOn.add(bundleInfo);
				} else {
					boolean newBundle=true;
					if(bundleList != null && bundleList.size() > 0){
						byte[] digest1 = bundleInfo.getMd5Digest();
						for(BundleInfoLite bundle:bundleList){
							byte[] digest2 = bundle.getMd5Digest();
							if (Arrays.equals(digest1, digest2)) {
								newBundle = false;
								new File(bundle.getSystemLocation().getFile()).setLastModified(System.currentTimeMillis());
							}
						}
					} 
					if(newBundle){
						bundlesToOperateOn.add(bundleInfo);
					}
				}
			} catch (MalformedURLException e) {
				OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
			} catch (IOException e) {
				OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
			}
		}
		bundlesToCheck.clear();
		return bundlesToOperateOn;
	}

	public synchronized void addBundleToCheck(String urlString){
		this.bundlesToCheck.add(urlString);
	}
	
	public synchronized void updateBundles(SubMonitor subMonitor){
		final SubMonitor master = SubMonitor.convert(subMonitor, 100);
		List<BundleInfoLite> deltas = determineDeltasBetweenBundlesToLoad();
		master.worked(30);
		List<BundleInfoLite> lockedFiles = copyDeltasToManagedFolder(deltas);
		master.worked(65);
		for(BundleInfoLite info: lockedFiles){
			addBundleToCheck(info.getSystemLocation().toString());
			OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, String.format("Unable to copy and load locked file: [%s]", info.getSystemLocation().toString()));
		}
		master.worked(5);
	}
	
	public synchronized void installLatestBundles(SubMonitor subMonitor){
		final SubMonitor master = SubMonitor.convert(subMonitor, 100);
		final List<BundleInfoLite> bundles = managedArea.getLatestBundles();
		Collection<Bundle> bundlesToRefresh = new ArrayList<Bundle>();
		for(BundleInfoLite info:bundles){
			if(!info.isInstalled()){
				try {
					List<BundleInfoLite> uninstallList = managedArea.getByBundleName(info.getSymbolicName());
					if(uninstallList.size() > 1){
						for(BundleInfoLite toUninstall:uninstallList){
							if(toUninstall.isInstalled()){
								Bundle bundle = toUninstall.uninstall();
								bundlesToRefresh.add(bundle);
							}
						}
					}

				} catch (BundleException e) {
					OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
				} 
			}
		}
		
		if(wiring != null && bundlesToRefresh.size() > 0){
			wiring.refreshBundles(bundlesToRefresh, new FrameworkListener(){
				@Override
				public void frameworkEvent(FrameworkEvent event) {
					if(FrameworkEvent.PACKAGES_REFRESHED == event.getType()){
						startBundles(bundles, master.newChild(80));
						waitForViewsToBeRegistered(master.newChild(15));
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
							@Override
							public void run() {
								restoreStateFromMemento(master.newChild(5));
							}
						});
					}
				}
			});
		} else {
			startBundles(bundles, master.newChild(80));
			waitForViewsToBeRegistered(master.newChild(15));
			final SubMonitor restore = master.newChild(5);
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable(){
				@Override
				public void run() {
					restoreStateFromMemento(restore);
				}
			});
		}
	}
	
	private boolean waitForViewsToBeRegistered(SubMonitor subMonitor){
		SubMonitor monitor = SubMonitor.convert(subMonitor, 10);
		monitor.setTaskName("Waiting for views to register.");
		for(int i = 0; i < 10; i++){
			monitor.worked(1);
			CheckViewsRegistered check = new CheckViewsRegistered();
			PlatformUI.getWorkbench().getDisplay().syncExec(check);
			if(check.isLoaded()){
				return true;
			} else {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	private class CheckViewsRegistered implements Runnable {

		private volatile boolean isLoaded = false;
		
		@Override
		public void run() {
			IWorkbench workbench = PlatformUI.getWorkbench();     
			if (managedArea.getInstalledBundles().size() > 0 && workbench != null && workbench.getActiveWorkbenchWindow() != null){
				IViewRegistry registry = workbench.getViewRegistry();
				Set<String> managedViews = determineManagedViews();
				for(String viewId:managedViews){
					try{
						IViewDescriptor desc = registry.find(viewId);
						if(desc == null){
							return;
						}
					} catch (Exception ex){
						return;
					}
				}
				isLoaded = true;
			}
			
		}
		
		public boolean isLoaded(){
			return isLoaded;
		}
	}


	
	private void restoreStateFromMemento(SubMonitor restore) {
		File mementoFile = OseeData.getFile(OTE_MEMENTO);
		if(mementoFile.exists()){
			try {
				IWorkbench workbench = PlatformUI.getWorkbench();     
				if (managedArea.getInstalledBundles().size() > 0 && workbench != null && workbench.getActiveWorkbenchWindow() != null){
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IPerspectiveDescriptor originalPerspective = page.getPerspective();
					IPerspectiveDescriptor[] pds = page.getOpenPerspectives();

					XMLMemento memento = XMLMemento.createReadRoot(new FileReader(mementoFile));
					IMemento[] perspectives = memento.getChildren(TAG_PERSPECTIVE);
					if(perspectives != null){
						for(IMemento perspective:perspectives){
							IMemento[] views = perspective.getChildren(TAG_VIEW);
							if(views != null && views.length > 0){
								String perspectiveId = perspective.getString("id");
								for(IPerspectiveDescriptor pd:pds){
									if(pd.getId().equals(perspectiveId)){
										page.setPerspective(pd);
										for(IMemento view:views){
											String viewId = view.getString("id");
											String secondId = view.getString("secondId");
											if(viewId != null){
												//show view
												try {
													page.showView(viewId, secondId, IWorkbenchPage.VIEW_ACTIVATE);
												} catch (PartInitException ex) {
													System.err.println("COULD NOT FIND " + viewId + ", with ID # = " + secondId);
													ex.printStackTrace();
												}
											}
										}
										break;
									}
								}
							}
						}
					}

					page.setPerspective(originalPerspective);
				}

			} catch (WorkbenchException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void startBundles(Collection<BundleInfoLite> bundles, SubMonitor subMonitor){
		final SubMonitor master = SubMonitor.convert(subMonitor, bundles.size() * 3);
		BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		master.setTaskName("Installing Bundles");
		for(BundleInfoLite info:bundles){
			if(!info.isInstalled()){
				try {
					info.install(context);					
					master.worked(2);
				} catch (BundleException e) {
				} catch (IOException e) {
				} 
			}
		}
		for(BundleInfoLite info:bundles){
			if(!info.isStarted()){
				try {
					info.start(context);
					master.worked(1);
				} catch (BundleException e) {
				} 
			}
		}
	}
}

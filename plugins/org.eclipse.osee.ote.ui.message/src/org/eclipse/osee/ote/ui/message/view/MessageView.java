/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.ui.plugin.OseeUiActions;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.osee.ote.service.IMessageDictionary;
import org.eclipse.osee.ote.service.IMessageDictionaryListener;
import org.eclipse.osee.ote.ui.message.OteMessageImage;
import org.eclipse.osee.ote.ui.message.internal.Activator;
import org.eclipse.osee.ote.ui.message.tree.AbstractTreeNode;
import org.eclipse.osee.ote.ui.message.tree.ElementNode;
import org.eclipse.osee.ote.ui.message.tree.MessageContentProvider;
import org.eclipse.osee.ote.ui.message.tree.MessageNode;
import org.eclipse.osee.ote.ui.message.tree.MessageTreeBuilder;
import org.eclipse.osee.ote.ui.message.tree.MessageTreeSorter;
import org.eclipse.osee.ote.ui.message.tree.MessageViewLabelProvider;
import org.eclipse.osee.ote.ui.message.tree.RootNode;
import org.eclipse.osee.ote.ui.message.watch.AddWatchParameter;
import org.eclipse.osee.ote.ui.message.watch.ElementPath;
import org.eclipse.osee.ote.ui.message.watch.WatchView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class MessageView extends ViewPart implements IActionable, IMessageDictionaryListener {
	protected TreeViewer treeViewer;
	protected Text searchText;
	protected MessageViewLabelProvider labelProvider;

	protected Action expandAction, collapseAction, refreshAction, filterByName, bugAction;
	protected ViewerSorter nameSorter;
	public static final String VIEW_ID = "org.eclipse.osee.ote.ui.message.view.MessageView";
	private Label startLabel;
	private Label versionLbl;
	private Composite parentComposite;
	private Button searchButton;
	private int numMessages = 0;
	private int numElements = 0;
	private MessageTreeBuilder treeBuilder;

	public MessageView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);
		parentComposite = new Composite(parent, SWT.NONE);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		parentComposite.setLayoutData(layoutData);
		Widgets.setFormLayout(parentComposite, 5, 5);

		startLabel = new Label(parentComposite, SWT.LEFT);
		Widgets.attachToParent(startLabel, SWT.TOP, 0, 0);
		Widgets.attachToParent(startLabel, SWT.LEFT, 0, 0);
		Widgets.attachToParent(startLabel, SWT.RIGHT, 50, 0);

		versionLbl = new Label(parentComposite, SWT.CENTER);
		Widgets.attachToParent(versionLbl, SWT.TOP, 0, 0);
		Widgets.attachToParent(versionLbl, SWT.LEFT, 50, 5);
		Widgets.attachToParent(versionLbl, SWT.RIGHT, 100, 0);

		// Create the tree treeViewer as a child of the composite parent
		treeViewer = new TreeViewer(parentComposite);
		treeViewer.setContentProvider(new MessageContentProvider());
		labelProvider = new MessageViewLabelProvider();
		treeViewer.setLabelProvider(labelProvider);
		final Tree tree = treeViewer.getTree();
		Widgets.attachToControl(tree, startLabel, SWT.TOP, SWT.BOTTOM, 5);
		Widgets.attachToParent(tree, SWT.BOTTOM, 100, -50);
		Widgets.attachToParent(tree, SWT.LEFT, 0, 0);
		Widgets.attachToParent(tree, SWT.RIGHT, 100, 0);
		treeViewer.setUseHashlookup(true);
		// tree.setMenu(getPopupMenu(parent));
		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 3) {
					Point p = new Point(e.x, e.y);
					final Menu contextMenu = getPopupMenu(tree.getParent());
					if (contextMenu != null) {
						p = tree.toDisplay(p);
						contextMenu.setLocation(p);
						contextMenu.setVisible(true);

						Activator.getDefault().setHelp(contextMenu, "messageViewWatch", "org.eclipse.osee.framework.help.ui");
					}
				}
			}

		});
		treeBuilder = new MessageTreeBuilder();
		/*
		 * Create a text field to be used for filtering the elements displayed by the tree treeViewer
		 */
		Group grp = new Group(parentComposite, SWT.NONE);
		Widgets.attachToControl(grp, tree, SWT.TOP, SWT.BOTTOM, 5);
		Widgets.attachToParent(grp, SWT.LEFT, 0, 0);
		Widgets.attachToParent(grp, SWT.RIGHT, 100, 0);
		layout = new GridLayout();
		layout.numColumns = 3;
		grp.setLayout(layout);
		Label l = new Label(grp, SWT.NULL);
		l.setText("Search:");
		l.setToolTipText("Enter a regular expression filter.\nEnter space to see all.");

		searchText = new Text(grp, SWT.SINGLE | SWT.BORDER);
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchText.addTraverseListener(new TraverseListener() {

			public void keyTraversed(TraverseEvent event) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					search(searchText.getText());
				}
			}

		});

		searchText.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				char chr = e.character;
				if (chr >= 'a' && chr <= 'z') {
					final char[] chars = new char[] {chr};
					e.text = new String(chars).toUpperCase();
				}
			}
		});

		searchButton = new Button(grp, SWT.PUSH);
		searchButton.setText("Go");
		searchButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				search(searchText.getText());
			}
		});

		// Create menu, toolbars, filters, sorters.
		createFiltersAndSorters();
		createActions();
		createMenus();
		createToolbar();

		treeViewer.setSorter(nameSorter);
		treeViewer.expandToLevel(0);

		RootNode root = new RootNode("empty");
		/*
		 * instantiate dummy nodes since the first time these classes are instantiated must be from a UI thread.
		 */
		MessageNode msgNode = new MessageNode("osee.test.msg.pubsub.DUMMY_MSG", null);
		ElementPath obj = new ElementPath();
		obj.add("DUMMY");
		ElementNode node = new ElementNode(obj);
		msgNode.addChild(node);
		treeViewer.setInput(root);
		setHelpContexts();
		setLibraryUnloadedState();
		Activator.getDefault().getOteClientService().addDictionaryListener(this);
	}

	private void setHelpContexts() {
		Activator.getDefault().setHelp(parentComposite.getParent(), "messageView", "org.eclipse.osee.framework.help.ui");
		Activator.getDefault().setHelp(searchButton, "messageViewSearch", "org.eclipse.osee.framework.help.ui");
		Activator.getDefault().setHelp(searchText, "messageViewSearch", "org.eclipse.osee.framework.help.ui");
	}

	/**
	 * sets the filter for searches
	 */
	private void search(final String searchPattern) {
		final Color bgColor = treeViewer.getTree().getBackground();
		treeViewer.getTree().setBackground(Displays.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		searchText.setEnabled(false);
		searchButton.setEnabled(false);
		final Job searchJob = new Job("Searching Message.jar") {

			@Override
			public IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("searching jar", numElements);
				try {
					String searchTxt = searchPattern;
					if (searchPattern.equals("")) {
						searchTxt = ".*";
					}
					try {
						final Pattern pattern = Pattern.compile(searchTxt);
						final Collection<Map.Entry<String, ArrayList<String>>> map = treeBuilder.getMessages();
						if (map.size() > 0) {
							final RootNode root = new RootNode("root");
							for (Map.Entry<String, ArrayList<String>> entry : map) {
								final String name = entry.getKey().substring(entry.getKey().lastIndexOf('.') + 1);
								final Matcher matcher = pattern.matcher(name);

								if (matcher.matches()) {
									MessageNode msgNode = new MessageNode(entry.getKey());
									root.addChild(msgNode);
									for (String elementName : entry.getValue()) {
										ElementPath obj = new ElementPath(msgNode.getMessageClassName(), elementName);
										ElementNode node = new ElementNode(obj);
										msgNode.addChild(node);
									}
								} else {
									LinkedList<String> matches = new LinkedList<String>();
									for (String elementName : entry.getValue()) {
										if (pattern.matcher(elementName).matches()) {
											matches.add(elementName);
										}
									}
									if (!matches.isEmpty()) {
										MessageNode msgNode = new MessageNode(entry.getKey());
										root.addChild(msgNode);
										for (String elementName : matches) {
											ElementPath obj = new ElementPath(msgNode.getMessageClassName(), elementName);
											ElementNode node = new ElementNode(obj);
											msgNode.addChild(node);
										}
									}
								}
								monitor.worked(entry.getValue().size());
							}

							Displays.ensureInDisplayThread(new Runnable() {
								@Override
								public void run() {

									treeViewer.setInput(root);
									if (!root.hasChildren()) {
										MessageDialog.openError(Displays.getActiveShell(), "Search",
													"No matches found for pattern " + searchPattern);
									}
								}

							});
						} else {
							Displays.ensureInDisplayThread(new Runnable() {
								@Override
								public void run() {
									MessageDialog.openError(Displays.getActiveShell(), "Search",
												"There are no messages available for searching");
								}
							});
						}
					} catch (final PatternSyntaxException e) {
						Displays.ensureInDisplayThread(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openError(Displays.getActiveShell(), "Search Error", e.getMessage());

							}
						});
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR, "", null);
					}
					return new Status(IStatus.OK, Activator.PLUGIN_ID, IStatus.OK, "", null);
				} catch (Throwable t) {
					OseeLog.log(Activator.class, Level.SEVERE, "exception during search operation", t);
					return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, IStatus.CANCEL, "", t);
				} finally {
					monitor.done();
					Displays.pendInDisplayThread(new Runnable() {
						@Override
						public void run() {
							treeViewer.getTree().setBackground(bgColor);
							searchText.setEnabled(true);
							searchButton.setEnabled(true);
						}
					});
				}
			}

		};
		searchJob.setUser(true);
		searchJob.schedule();
	}

	private Menu getPopupMenu(final Composite composite) {
		final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		final AbstractTreeNode node = (AbstractTreeNode) selection.getFirstElement();
		final Menu previewMenu = new Menu(composite);

		// if there's nothing selected in the tree, no need to display a menu.
		if (node == null) {
			return null;
		}

		MenuItem item = new MenuItem(previewMenu, SWT.CASCADE);
		item.setText("Watch");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					WatchView watchView = (WatchView) page.findView(WatchView.VIEW_ID);
					if (watchView == null) {
						watchView = launchMessageWatch(page);
					}
					if (watchView != null) {
						final Iterator<?> iter = selection.iterator();
						AddWatchParameter addWatchParam = new AddWatchParameter();
						while (iter.hasNext()) {
							final AbstractTreeNode node = (AbstractTreeNode) iter.next();
							if (node instanceof MessageNode) {
								addWatchParam.addMessage(((MessageNode) node).getMessageClassName());
							} else if (node instanceof ElementNode) {
								addWatchParam.addMessage(((ElementNode) node).getElementPath().getMessageName(),
											((ElementNode) node).getElementPath());
							}
						}
						watchView.addWatchMessage(addWatchParam);
						watchView.refresh();
					}
				} catch (RuntimeException ex) {
					OseeLog.log(Activator.class, Level.SEVERE, "exception during attempt to watch", ex);
				}
			}
		});

		if (node instanceof MessageNode) {
			item = new MenuItem(previewMenu, SWT.CASCADE);
			item.setText("Watch All");
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						WatchView watchView = (WatchView) page.findView(WatchView.VIEW_ID);
						if (watchView == null) {
							watchView = launchMessageWatch(page);
						}
						if (watchView != null) {
							final Iterator<?> iter = selection.iterator();
							final RootNode root = (RootNode) treeViewer.getInput();
							AddWatchParameter parameter = new AddWatchParameter();
							while (iter.hasNext()) {
								final AbstractTreeNode node = (AbstractTreeNode) iter.next();
								if (node.getParent() == root) {
									if (node instanceof MessageNode) {
										parameter.addMessage(((MessageNode) node).getMessageClassName());
										for (AbstractTreeNode child : node.getChildren()) {
											ElementPath obj =
														new ElementPath(((MessageNode) node).getMessageClassName(), child.getName());
											parameter.addMessage(((MessageNode) node).getMessageClassName(), obj);
										}
									}
								} else {
									MessageNode parent = (MessageNode) node.getParent();
									if (!parameter.containsMessage(parent.getName())) {
										for (int index = 0; index < parent.getChildren().size(); index++) {
											ElementPath obj = new ElementPath(parent.getMessageClassName(), node.getName());
											parameter.addMessage(parent.getMessageClassName(), obj);
										}
									}
								}
							}

							watchView.addWatchMessage(parameter);
							watchView.refresh();
						}
					} catch (RuntimeException ex) {
						OseeLog.log(Activator.class, Level.SEVERE, "Exception during attempt to watch all", ex);
					}
				}
			});
		}

		return previewMenu;
	}

	private WatchView launchMessageWatch(IWorkbenchPage page) {
		WatchView watchView = null;
		try {
			watchView = (WatchView) page.showView(WatchView.VIEW_ID);
		} catch (PartInitException e1) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
						"Couldn't Launch MsgWatch " + e1.getMessage());
			return null;
		}
		return watchView;
	}

	protected void createFiltersAndSorters() {
		nameSorter = new MessageTreeSorter();
	}

	protected void createActions() {
		final TreeViewer ftv = treeViewer;
		expandAction = new Action("Expand All") {

			@Override
			public void run() {
				treeViewer.getTree().setRedraw(false);
				ftv.expandAll();
				treeViewer.getTree().setRedraw(true);
			}
		};
		expandAction.setImageDescriptor(ImageManager.getImageDescriptor(OteMessageImage.EXPAND_STATE));
		expandAction.setToolTipText("Expand All");

		collapseAction = new Action("Collapse All") {

			@Override
			public void run() {
				treeViewer.getTree().setRedraw(false);
				ftv.collapseAll();
				treeViewer.getTree().setRedraw(true);
			}
		};
		collapseAction.setImageDescriptor(ImageManager.getImageDescriptor(OteMessageImage.COLLAPSE_STATE));
		collapseAction.setToolTipText("Collapse All");

		refreshAction = new Action("Reload Messages from JAR") {

			@Override
			public void run() {
				search(".*");
			}
		};
		refreshAction.setToolTipText("Reload Messages from JAR");
		refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(OteMessageImage.REFRESH));

		OseeUiActions.addBugToViewToolbar(this, this, Activator.getDefault(), VIEW_ID, "Message View");
	}

	protected void createMenus() {
		IMenuManager rootMenuManager = getViewSite().getActionBars().getMenuManager();
		rootMenuManager.setRemoveAllWhenShown(true);
		rootMenuManager.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager mgr) {
				fillMenu(mgr);
			}
		});
		fillMenu(rootMenuManager);
	}

	protected void fillMenu(IMenuManager rootMenuManager) {
		rootMenuManager.add(refreshAction);
		rootMenuManager.add(expandAction);
		rootMenuManager.add(collapseAction);
	}

	protected void createToolbar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		toolbarManager.add(refreshAction);
		toolbarManager.add(expandAction);
		toolbarManager.add(collapseAction);
		// toolbarManager.add(bugAction);
	}

	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		parentComposite.setFocus();
	}

	public String getActionDescription() {
		return "";
	}

	@Override
	public void onDictionaryLoaded(final IMessageDictionary dictionary) {
		Displays.pendInDisplayThread(new Runnable() {
			@Override
			public void run() {
				try {
					startLabel.setText("processing library...");
					treeViewer.getTree().setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));
					treeBuilder.clear();
					dictionary.generateMessageIndex(treeBuilder);
					numMessages = treeBuilder.getNumMessages();
					numElements = treeBuilder.getNumElements();
					versionLbl.setText(dictionary.getMessageLibraryVersion());
					versionLbl.setToolTipText(String.format("#Messages: %d, #Elements: %d", numMessages, numElements));
					searchText.setEnabled(true);
					startLabel.setText("Ready for query");
				} catch (Exception e) {
					OseeLog.log(Activator.class, Level.SEVERE, "Problem during message jar processing", e);
				}
			}
		});
	}

	@Override
	public void onDictionaryUnloaded(IMessageDictionary dictionary) {
		Displays.pendInDisplayThread(new Runnable() {
			@Override
			public void run() {
				setLibraryUnloadedState();
			}

		});

	}

	private void setLibraryUnloadedState() {
		if (treeViewer.getTree().isDisposed() || startLabel.isDisposed() || versionLbl.isDisposed() || searchText.isDisposed()) {
			return;
		}
		treeViewer.getTree().setBackground(Displays.getSystemColor(SWT.COLOR_GRAY));
		startLabel.setText("message library not detected");
		treeViewer.setInput(null);
		versionLbl.setText("");
		versionLbl.setToolTipText("");
		searchText.setEnabled(false);
	}

	@Override
	public void dispose() {
		super.dispose();
		Activator.getDefault().getOteClientService().removeDictionaryListener(this);
	}

}
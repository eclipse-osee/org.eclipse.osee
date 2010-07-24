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
package org.eclipse.osee.ote.ui.message.watch.recording;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.commands.RecordCommand.MessageRecordDetails;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.ui.message.tree.ElementNode;
import org.eclipse.osee.ote.ui.message.tree.MessageNode;
import org.eclipse.osee.ote.ui.message.tree.WatchList;
import org.eclipse.osee.ote.ui.message.tree.WatchedMessageNode;
import org.eclipse.osee.ote.ui.message.watch.ElementPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Andrew M. Finkbeiner
 */
public class RecordingFilter extends WizardPage {

   private CheckboxTreeViewer viewer;
   private final WatchList watchList;

   /**
    * @param rootNode
    */
   public RecordingFilter(WatchList watchList) {
      super("Filter Recording");
      setTitle("Recording File");
      setDescription("Check all of the elements you want to record.");
      this.watchList = watchList;
   }

   @Override
   public void createControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.numColumns = 6;

      composite.setLayout(layout);

      GridData data = new GridData(GridData.FILL_BOTH);
      data.grabExcessHorizontalSpace = true;
      data.grabExcessVerticalSpace = true;
      data.horizontalSpan = 6;

      viewer = new CheckboxTreeViewer(composite);
      viewer.setUseHashlookup(true);
      viewer.getTree().setLayoutData(data);
      viewer.setLabelProvider(new MessageRecordDetailLabelProvider());
      viewer.setContentProvider(new MessageRecordDetailContentProvider());
      viewer.setInput(watchList);

      restoreCheckBoxSelectionState();

      viewer.addCheckStateListener(new ICheckStateListener() {
         @Override
         public void checkStateChanged(CheckStateChangedEvent event) {
            viewer.expandToLevel(event.getElement(), 1);
            viewer.setSubtreeChecked(event.getElement(), event.getChecked());

            setPageComplete(isPageComplete());
         }
      });

      Button selectAll = new Button(composite, SWT.PUSH);
      data = new GridData();
      selectAll.setText("Select All");
      selectAll.setLayoutData(data);
      selectAll.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            viewer.expandAll();
            Object[] objects = viewer.getExpandedElements();
            for (Object element : objects) {
               viewer.setSubtreeChecked(element, true);
            }
            setPageComplete(isPageComplete());
         }

      });

      Button selectBodyElements = new Button(composite, SWT.PUSH);
      selectBodyElements.setText("Select Body Elements");
      data = new GridData();
      selectBodyElements.setLayoutData(data);
      selectBodyElements.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            for (Object node : ((MessageRecordDetailContentProvider) viewer.getContentProvider()).getChildren(watchList)) {
               if (node instanceof MessageNode) {
                  for (Object messageChild : ((MessageRecordDetailContentProvider) viewer.getContentProvider()).getChildren(node)) {
                     if (messageChild instanceof BodyElements) {
                        viewer.setSubtreeChecked(messageChild, true);
                     }
                  }
               }
            }
            setPageComplete(isPageComplete());
         }

      });

      Button deselectAll = new Button(composite, SWT.PUSH);
      deselectAll.setText("Deselect All");
      data = new GridData();
      deselectAll.setLayoutData(data);
      deselectAll.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            for (Object obj : viewer.getCheckedElements()) {
               viewer.setSubtreeChecked(obj, false);
            }
            setPageComplete(isPageComplete());
         }

      });
      setControl(composite);
   }

   /**
    * This method must be called after the input has been set so that the content provider lookups can find the tree
    * items to restore.
    */
   private void restoreCheckBoxSelectionState() {
      for (Object node : ((MessageRecordDetailContentProvider) viewer.getContentProvider()).getChildren(watchList)) {
         if (node instanceof MessageNode) {
            WatchedMessageNode msgNode = (WatchedMessageNode) node;
            Object[] headerElements = null;
            for (Object messageChild : ((MessageRecordDetailContentProvider) viewer.getContentProvider()).getChildren(node)) {
               if (msgNode.getRecordingState().getHeaderDump() && messageChild instanceof HeaderDump) {
                  viewer.setSubtreeChecked(messageChild, true);
               }
               if (msgNode.getRecordingState().getBodyDump() && messageChild instanceof BodyDump) {
                  viewer.setSubtreeChecked(messageChild, true);
               }

               if (messageChild instanceof HeaderElements) {
                  headerElements =
                     ((MessageRecordDetailContentProvider) viewer.getContentProvider()).getChildren(messageChild);
               }

            }

            for (ElementPath path : msgNode.getRecordingState().getBodyElements()) {
               ElementNode child = msgNode.findChildElement(path);
               viewer.setSubtreeChecked(child, true);
            }

            if (headerElements != null) {
               for (ElementPath child : msgNode.getRecordingState().getHeaderElements()) {
                  for (Object obj : headerElements) {
                     if (obj instanceof ElementNode) {
                        if (((ElementNode) obj).getElementPath().equals(child)) {
                           viewer.setSubtreeChecked(obj, true);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public List<MessageRecordDetails> getData() {
      Object[] objs = viewer.getCheckedElements();
      MessageDetailsTransform transform = new MessageDetailsTransform();
      for (Object obj : objs) {
         transform.put(obj);
      }
      List<MessageRecordDetails> details = transform.getData();
      for (MessageRecordDetails detail : details) {
         WatchedMessageNode msgNode = watchList.getMessageNode(detail.getName());
         if (msgNode != null) {
            msgNode.getRecordingState().reset();
            msgNode.getRecordingState().setBodyDump(detail.getBodyDump());
            msgNode.getRecordingState().setHeaderDump(detail.getHeaderDump());
            for (List<Object> path : detail.getHeaderElementNames()) {
               msgNode.getRecordingState().addHeader(new ElementPath(path));
            }
            for (List<Object> path : detail.getBodyElementNames()) {
               msgNode.getRecordingState().addBody(new ElementPath(path));
            }
         }
      }
      return transform.getData();
   }

   private class MessageDetailsTransform {
      private final Map<String, MessageRecordDetailsChecked> details =
         new LinkedHashMap<String, MessageRecordDetailsChecked>();

      public void put(Object checkedWrapper) {
         if (checkedWrapper instanceof IElementPath) {
            ElementPath path = ((IElementPath) checkedWrapper).getElementPath();
            if (path == null) {
               System.out.println(checkedWrapper.getClass().getName());
               System.out.println(checkedWrapper);
            } else {
               String messageName = path.getMessageName();
               if (messageName.equals("UNKNOWN")) {
                  System.out.println("hello");
               }
               MessageRecordDetailsChecked checked = details.get(messageName);
               if (checked == null) {
                  checked = new MessageRecordDetailsChecked();
                  details.put(messageName, checked);
               }
               checked.put(checkedWrapper);
            }
         } else {
            checkedWrapper.getClass().toString();
         }
      }

      public List<MessageRecordDetails> getData() {
         List<MessageRecordDetails> data = new ArrayList<MessageRecordDetails>();
         for (String msg : details.keySet()) {
            data.add(details.get(msg).create(msg, watchList.getMessageNode(msg).getMemType()));
         }
         return data;
      }
   }

   private class MessageRecordDetailsChecked {
      private final List<List<Object>> headers = new ArrayList<List<Object>>();
      private final List<List<Object>> elements = new ArrayList<List<Object>>();
      private boolean bodyDump;
      private boolean headerDump;

      public void put(Object checkedWrapper) {
         if (checkedWrapper instanceof BodyDump) {
            bodyDump = true;
         } else if (checkedWrapper instanceof HeaderDump) {
            headerDump = true;
         } else if (checkedWrapper instanceof IElementPath) {
            ElementPath path = ((IElementPath) checkedWrapper).getElementPath();
            if (path.isHeader()) {
               headers.add(path.getElementPath());
            } else {

               if (getMessageElement(new ElementPath(path.getElementPath())) != null) {
                  elements.add(path.getElementPath());
               } else {
                  System.out.println(String.format("unable to record [%s]",
                     new ElementPath(path.getElementPath()).asString()));
               }
            }
         }
      }

      public MessageRecordDetails create(String msg, DataType currentMemType) {
         return new MessageRecordDetails(msg, currentMemType, headerDump, headers, bodyDump, elements);
      }
   }

   public Element getMessageElement(ElementPath elementPath) {
      Element el = null;
      WatchedMessageNode node = watchList.getMessageNode((String) elementPath.get(0));
      Message<?, ?, ?> msg = node.getSubscription().getMessage();
      el = msg.getElement(elementPath.getElementPath());
      return el;
   }

   @Override
   public boolean isPageComplete() {
      return viewer.getCheckedElements().length > 0;
   }
}

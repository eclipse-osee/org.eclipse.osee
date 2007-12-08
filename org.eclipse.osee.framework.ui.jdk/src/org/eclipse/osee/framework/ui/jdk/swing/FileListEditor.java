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
package org.eclipse.osee.framework.ui.jdk.swing;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Ryan D. Brooks
 */
public class FileListEditor extends JPanel implements ActionListener {

   private static final long serialVersionUID = -4397118110586026746L;
   private DefaultListModel listModel;
   private JList list;
   private JFileChooser chooser;
   private JButton addButton;
   private JButton removeButton;
   private String relativeToPath;

   public FileListEditor() {
      this(null, null, JFileChooser.FILES_AND_DIRECTORIES, null);
   }

   public FileListEditor(List<File> items, File currentDir, int mode, File relativeToFile) {
      super(new GridBagLayout());

      if (relativeToFile != null) {
         try {
            this.relativeToPath = relativeToFile.getCanonicalFile().getPath(); //ensure this has a standard name
         } catch (IOException ex) {
            // this.relativeToFile is already null so do nothing
         }
      }

      listModel = new DefaultListModel();
      list = new JList(listModel);
      chooser = new JFileChooser(currentDir);

      chooser.setFileSelectionMode(mode);
      chooser.setMultiSelectionEnabled(true);

      if (items != null) {
         addFiles(items);
      }

      JScrollPane scrollList = new JScrollPane(list);
      add(scrollList, EasyGridConstraint.setConstraints(0, 0, 1, 1, 2, 1, 'B', ""));

      addButton = new JButton("Add");
      addButton.addActionListener(this);
      add(addButton, EasyGridConstraint.setConstraints(0, 1, 1, 0, 1, 1, 'N', "W"));

      removeButton = new JButton("Remove");
      removeButton.addActionListener(this);
      add(removeButton, EasyGridConstraint.setConstraints(1, 1, 1, 0, 1, 1, 'N', "E"));

   }

   public void setChooserTitle(String title) {
      chooser.setDialogTitle(title);
   }

   public void actionPerformed(ActionEvent ev) {
      Component component = (Component) ev.getSource();
      if (component == addButton) {
         if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            for (int i = 0; i < files.length; i++) {
               if (relativeToPath == null) {
                  listModel.addElement(files[i].getPath());
               } else {
                  try {
                     String path = files[i].getCanonicalFile().getPath();
                     if (path.startsWith(relativeToPath)) {
                        listModel.addElement(path.substring(relativeToPath.length() + 1));
                     } else {
                        System.out.println("Path is not relative to " + relativeToPath);
                     }
                  } catch (IOException ex) {
                     System.out.println(ex);
                  }
               }
            }
         }
      } else if (component == removeButton) {
         int[] indices = list.getSelectedIndices();
         for (int i = indices.length - 1; i > -1; i--) {
            listModel.remove(indices[i]);
         }
      }
   }

   public void addFiles(List<File> items) {
      for (Iterator<File> i = items.iterator(); i.hasNext();) {
         File file = i.next();
         if (relativeToPath == null) {
            listModel.addElement(file.getPath());
         } else {
            try {
               String path = file.getCanonicalFile().getPath();
               if (path.startsWith(relativeToPath)) {
                  listModel.addElement(path.substring(relativeToPath.length() + 1));
               } else {
                  System.out.println("Path is not relative to " + relativeToPath);
               }
            } catch (IOException ex) {
               System.out.println(ex);
            }
         }
      }
   }

   public void addFiles(String[] files) {
      for (int i = 0; i < files.length; i++) {
         listModel.addElement(files[i]);
      }
   }

   public void addFile(String file) {
      listModel.addElement(file);
   }

   public void writeListTo(File file) throws IOException {
      BufferedWriter out = new BufferedWriter(new FileWriter(file));
      for (Enumeration<?> e = listModel.elements(); e.hasMoreElements();) {
         out.write((String) e.nextElement());
         out.newLine();
      }
      out.close();
   }

   public File[] getFiles() {
      File[] files = new File[listModel.size()];
      Enumeration<?> e = listModel.elements();
      for (int i = 0; i < files.length; i++) {
         files[i] = new File((String) e.nextElement());
      }
      return files;
   }
}

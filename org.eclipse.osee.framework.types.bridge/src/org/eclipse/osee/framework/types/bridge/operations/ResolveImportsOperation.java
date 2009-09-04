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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.types.bridge.wizards.LinkMessage;
import org.eclipse.osee.framework.types.bridge.wizards.LinkNode;
import org.eclipse.xtext.resource.IClasspathUriResolver;
import org.eclipse.xtext.ui.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ResolveImportsOperation extends AbstractOperation {
   private final Pattern pattern = Pattern.compile("\\s*import\\s+\"(.*?)\"");
   private final List<IFile> selectedItems;
   private final Matcher matcher;
   private final IClasspathUriResolver resolver;
   private final List<LinkNode> dependencyData;

   public ResolveImportsOperation(IClasspathUriResolver resolver, List<IFile> selectedItems, List<LinkNode> dependencyData) {
      super("Extract imports", Activator.PLUGIN_ID);
      this.matcher = pattern.matcher("");
      this.selectedItems = selectedItems;
      this.resolver = resolver;
      this.dependencyData = dependencyData;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!selectedItems.isEmpty()) {
         double workPercentage = 1.0 / selectedItems.size();
         for (IFile selectedFile : selectedItems) {
            URI uri = URI.createURI(selectedFile.getLocationURI().toASCIIString());
            LinkNode node = new LinkNode(uri);
            dependencyData.add(node);
            resolveImports(node);
         }
         monitor.worked(calculateWork(workPercentage));
      }
   }

   private void resolveImports(LinkNode node) throws IOException {
      if (node.getUri() != null) {
         Set<String> requiredImports = null;
         try {
            requiredImports = getImports(node.getUri());
         } catch (IOException ex) {
            node.setIsResolved(false);
            throw ex;
         }
         for (String importEntry : requiredImports) {
            URI resolved = resolver.resolve((Object) null, URI.createURI(importEntry));
            LinkMessage message = new LinkMessage(resolved, importEntry);
            node.addChild(message);
            resolveImports(message);
         }
      }
   }

   private Set<String> getImports(URI uri) throws IOException {
      Set<String> imports = new HashSet<String>();
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(new URL(uri.toString()).openStream());
         String inputString = Lib.inputStreamToString(inputStream);
         matcher.reset(inputString);
         while (matcher.find()) {
            imports.add(matcher.group(1));
         }
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
      return imports;
   }
}

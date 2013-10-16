/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.branch;

import java.io.InputStream;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractBranchCallable<REQ_TYPE, RESP_TYPE> extends CancellableCallable<RESP_TYPE> {

   private final ApplicationContext context;
   private final HttpServletRequest req;
   private final HttpServletResponse resp;
   private final IDataTranslationService translationService;
   private final OrcsApi orcsApi;
   private final String mediaType;
   private final CoreTranslatorId reqType;
   private final CoreTranslatorId respType;
   private Callable<?> innerWorker;
   private final OrcsBranch branchOps;

   public AbstractBranchCallable(ApplicationContext context, HttpServletRequest req, HttpServletResponse resp, IDataTranslationService translationService, OrcsApi orcsApi, String mediaType, CoreTranslatorId reqType, CoreTranslatorId respType) {
      super();
      this.context = context;
      this.req = req;
      this.resp = resp;
      this.translationService = translationService;
      this.orcsApi = orcsApi;
      this.mediaType = mediaType;
      this.reqType = reqType;
      this.respType = respType;
      this.branchOps = getOrcsApi().getBranchOps(context);
   }

   @Override
   public final RESP_TYPE call() throws Exception {
      REQ_TYPE request = translationService.convert(req.getInputStream(), reqType);
      RESP_TYPE response = executeCall(request);

      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      resp.setContentType(mediaType);
      resp.setCharacterEncoding("UTF-8");

      if (response != null && respType != null) {
         InputStream inputStream = translationService.convertToStream(response, respType);
         Lib.inputStreamToOutputStream(inputStream, resp.getOutputStream());
      } else {
         resp.getWriter().flush();
      }
      return response;
   }

   protected abstract RESP_TYPE executeCall(REQ_TYPE request) throws Exception;

   protected <K> K callAndCheckForCancel(Callable<K> callable) throws Exception {
      checkForCancelled();
      setInnerWorker(callable);
      K result = callable.call();
      setInnerWorker(null);
      return result;
   }

   private synchronized void setInnerWorker(Callable<?> callable) {
      innerWorker = callable;
   }

   @Override
   public void setCancel(boolean isCancelled) {
      super.setCancel(isCancelled);
      final Callable<?> inner = innerWorker;
      if (inner != null) {
         synchronized (inner) {
            if (inner instanceof CancellableCallable) {
               ((CancellableCallable<?>) inner).setCancel(isCancelled);
            }
         }
      }
   }

   private OrcsApi getOrcsApi() {
      return orcsApi;
   }

   protected OrcsBranch getBranchOps() {
      return branchOps;
   }

   protected ApplicationContext getContext() {
      return context;
   }

   protected ArtifactReadable getArtifactById(int id) throws OseeCoreException {
      ArtifactReadable artifact = null;
      if (id > 0) {
         QueryFactory factory = getOrcsApi().getQueryFactory(getContext());
         artifact = factory.fromBranch(CoreBranches.COMMON).andLocalId(id).getResults().getExactlyOne();
      }
      return artifact;
   }

}
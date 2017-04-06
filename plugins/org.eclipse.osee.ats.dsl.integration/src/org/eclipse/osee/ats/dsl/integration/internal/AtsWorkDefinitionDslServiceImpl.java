/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.dsl.integration.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAtsRuleDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionDslService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.dsl.ModelUtil;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.StringOutputStream;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.logger.Log;

/**
 * Provides new and stored Work Definitions
 *
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionDslServiceImpl implements IAtsWorkDefinitionDslService {

   private IAttributeResolver attrResolver;
   private IAtsUserService userService;
   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setAttributeResolver(IAttributeResolver attrResolver) {
      this.attrResolver = attrResolver;
   }

   public void setAtsUserService(IAtsUserService userService) {
      this.userService = userService;
   }

   public void start() throws OseeCoreException {
      logger.info("AtsWorkDefinitionServiceImpl started");
   }

   @Override
   public IAtsWorkDefinition copyWorkDefinition(String newName, IAtsWorkDefinition workDef, XResultData resultData) {
      ConvertWorkDefinitionToAtsDsl converter = new ConvertWorkDefinitionToAtsDsl(resultData);
      AtsDsl atsDsl = converter.convert(newName, workDef);

      // Convert back to WorkDefinition
      ConvertAtsDslToWorkDefinition converter2 = new ConvertAtsDslToWorkDefinition(Lib.generateArtifactIdAsInt(),
         newName, atsDsl, resultData, attrResolver, userService);
      IAtsWorkDefinition newWorkDef = converter2.convert().iterator().next();
      return newWorkDef;
   }

   @Override
   public String getStorageString(IAtsWorkDefinition workDef, XResultData resultData) {
      ConvertWorkDefinitionToAtsDsl converter = new ConvertWorkDefinitionToAtsDsl(resultData);
      AtsDsl atsDsl = converter.convert(workDef.getName(), workDef);
      StringOutputStream writer = new StringOutputStream();
      try {
         ModelUtil.saveModel(atsDsl, "ats:/mock" + Lib.getDateTimeString() + ".ats", writer);
      } catch (Exception ex) {
         resultData.errorf("Error converting workDef [%s][%s] to storange string", workDef.getId(), workDef.getName());
      }
      return writer.toString();
   }

   @Override
   public List<IAtsRuleDefinition> getRuleDefinitions(String ruleDefintionsDslStr) {
      List<IAtsRuleDefinition> ruleDefs = new ArrayList<>();
      if (Strings.isValid(ruleDefintionsDslStr)) {
         AtsDsl atsDsl;
         try {
            atsDsl = ModelUtil.loadModel("Rule Definitions" + ".ats", ruleDefintionsDslStr);
            ConvertAtsDslToRuleDefinition convert = new ConvertAtsDslToRuleDefinition(atsDsl, ruleDefs, userService);
            ruleDefs = convert.convert();
         } catch (Exception ex) {
            OseeLog.log(AtsWorkDefinitionDslServiceImpl.class, Level.SEVERE, ex);
         }
      }
      return ruleDefs;
   }

   @Override
   public IAtsWorkDefinition getWorkDefinition(Long id, String workDefinitionDsl) {
      try {
         AtsDsl atsDsl = ModelUtil.loadModel("model.ats", workDefinitionDsl);
         if (atsDsl != null && atsDsl.getWorkDef() != null && !atsDsl.getWorkDef().isEmpty()) {
            XResultData result = new XResultData(false);
            ConvertAtsDslToWorkDefinition convert =
               new ConvertAtsDslToWorkDefinition(id, Strings.unquote(atsDsl.getWorkDef().iterator().next().getName()),
                  atsDsl, result, attrResolver, userService);
            if (!result.isEmpty()) {
               throw new IllegalStateException(result.toString());
            }
            return convert.convert().iterator().next();
         }
         return null;
      } catch (Exception ex) {
         throw new OseeWrappedException(ex, "Error converting workDefStr [%s] to Work Definition", id);
      }
   }

}

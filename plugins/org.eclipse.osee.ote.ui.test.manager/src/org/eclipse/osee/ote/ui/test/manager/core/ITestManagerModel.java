package org.eclipse.osee.ote.ui.test.manager.core;

import org.eclipse.osee.framework.jdk.core.type.Pair;

public interface ITestManagerModel {

   public abstract boolean hasParseExceptions();

   public abstract Pair<Integer, Integer> getParseErrorRange();

   public abstract String getParseError();

   public abstract boolean setFromXml(String xmlText);

   public abstract String getRawXml();

}
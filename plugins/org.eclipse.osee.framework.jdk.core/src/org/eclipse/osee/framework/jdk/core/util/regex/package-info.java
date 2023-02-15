/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

/**
 * A collection of classes for matching character sequences with regular expressions based upon the classes in the
 * package {@link java.util.regex}.
 * <dl>
 * <dt>{@link TokenPattern} and {@link TokenMatcher}</dt>
 * <dd>These classes are patterned after {@link java.util.regex.Pattern} and {@link java.util.regex.Matcher}. The
 * classes allow a complex poor performing regular expression to be split into a prefix, core, and suffix pattern for
 * better performance. The simpler core pattern is matched, then an anchored test of the suffix pattern is performed,
 * followed by a backwards search for the prefix pattern.</dd>
 * </dl>
 *
 * @author Loren K. Ashley
 */

package org.eclipse.osee.framework.jdk.core.util.regex;

/* EOF */
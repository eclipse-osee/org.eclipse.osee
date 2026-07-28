/*********************************************************************
 * Copyright (c) 2026 Boeing
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
 * Multiplicity IDs as returned by the OSEE server.
 * Used to determine how many instances of an attribute type are allowed.
 */
export const MULTIPLICITY_ID = {
	/** Zero or more instances allowed. */
	ANY: '1',
	/** Exactly one instance required. */
	EXACTLY_ONE: '2',
	/** Zero or one instance allowed. */
	ZERO_OR_ONE: '3',
	/** One or more instances required. */
	AT_LEAST_ONE: '4',
} as const;

export type MultiplicityId =
	(typeof MULTIPLICITY_ID)[keyof typeof MULTIPLICITY_ID];

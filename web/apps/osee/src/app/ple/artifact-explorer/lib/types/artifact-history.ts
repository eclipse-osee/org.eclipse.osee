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
 * Represents a change version (before or after state) from the backend ChangeItem.
 */
export type changeVersion = {
	transactionToken: { id: string };
	gammaId: string;
	modType: string;
	value: string;
	applicabilityToken?: { id: string; name: string };
};

/**
 * Represents a single change item from the artifact's transaction history.
 * Maps to the Java ChangeItem class serialized via Jackson.
 */
export type artifactHistoryEntry = {
	changeType: string;
	itemId: string;
	itemTypeId: string;
	artId: string;
	baselineVersion: changeVersion;
	currentVersion: changeVersion;
	destinationVersion?: changeVersion;
	netChange?: changeVersion;
	synthetic: boolean;
};

/**
 * Lightweight transaction metadata returned alongside change items.
 */
export type transactionInfo = {
	id: string;
	comment: string;
	timestamp: number;
};

/**
 * Response from the paginated artifact history endpoint.
 */
export type artifactHistoryResult = {
	changes: artifactHistoryEntry[];
	transactions: Record<string, transactionInfo>;
};

/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { transactionToken } from '@osee/transactions/types';
export type XResultData = {
	empty: boolean;
	errorCount: number;
	errors: boolean;
	failed: boolean;
	ids: string[];
	infoCount: number;
	numErrors: number;
	numErrorsViaSearch: number;
	numWarnings: number;
	numWarningsViaSearch: number;
	results: string[];
	success: boolean;
	tables: [];
	title: string | null;
	txId: string;
	warningCount: number;
};

export type commitResponse = {
	tx: transactionToken;
	results: XResultData;
	success: boolean;
	failed: boolean;
};

/**
 * A single validation/transition result line for one work item, carrying a human-readable reason a
 * transition was blocked (e.g. "Working Branch exists. Please commit or delete...").
 */
export type transitionResult = {
	details: string;
	exception?: string | null;
};

/**
 * Per-work-item transition outcome. Blocking reasons live in {@link results} here — NOT in the
 * top-level {@link transitionResponse.results}, which stays empty for validation failures.
 */
export type transitionWorkItem = {
	workItemId?: { id: string; name?: string };
	atsId?: string;
	results: transitionResult[];
};

export type transitionResponse = {
	cancelled: boolean;
	/**
	 * Work items that were transitioned. Serialized from the server's {@code Set<ArtifactToken>},
	 * so each element is a token object with an {@code id} — NOT a bare id string.
	 */
	workItemIds: { id: string; name?: string }[];
	results: string[];
	transitionWorkItems: transitionWorkItem[];
	transaction: transactionToken;
	empty: boolean;
};

/**
 * Collects all blocking reasons from a transition/validate response, checking both the top-level
 * `results` and each work item's `results[].details`. Returns an empty array when the transition is
 * clear to proceed.
 */
export function getTransitionFailureReasons(
	response: transitionResponse
): string[] {
	const reasons: string[] = [...(response.results ?? [])];
	for (const workItem of response.transitionWorkItems ?? []) {
		for (const result of workItem?.results ?? []) {
			if (result?.details) {
				reasons.push(result.details);
			}
		}
	}
	return reasons;
}

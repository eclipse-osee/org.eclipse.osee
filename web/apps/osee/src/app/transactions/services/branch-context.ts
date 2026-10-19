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
import { InjectionToken, Injectable, Signal, signal } from '@angular/core';

/**
 * A per-subtree branch/view override for transactions (and any read that chooses
 * to honor it). When this token is provided in a component's injector,
 * {@link CurrentTransactionService} targets this branch instead of the global
 * `UiService` current branch for every transaction created under that subtree.
 *
 * This exists for surfaces that legitimately edit something OTHER than "the
 * current branch selector" value -- most importantly the artifact explorer's
 * editor tabs, which pin their own branch and must keep writing to it even after
 * the global branch selector changes. Absent (the default everywhere else) =
 * the app's single current branch.
 */
export type BranchContext = {
	branchId: Signal<string>;
	viewId: Signal<string>;
};

export const BRANCH_CONTEXT = new InjectionToken<BranchContext>(
	'BRANCH_CONTEXT'
);

/**
 * Mutable, signal-backed {@link BranchContext}. Provided at a subtree root so a
 * host component can set the pinned branch/view from its own inputs (which are
 * not available at provider-construction time), while descendants read it
 * reactively through {@link BRANCH_CONTEXT}.
 */
@Injectable()
export class BranchContextSource implements BranchContext {
	private readonly _branchId = signal('');
	private readonly _viewId = signal('');

	readonly branchId: Signal<string> = this._branchId.asReadonly();
	readonly viewId: Signal<string> = this._viewId.asReadonly();

	set(branchId: string, viewId: string): void {
		this._branchId.set(branchId);
		this._viewId.set(viewId);
	}
}

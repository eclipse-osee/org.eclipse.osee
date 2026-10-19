/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import { Injectable, Provider, inject } from '@angular/core';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import { UiService } from '@osee/shared/services';
import { applic } from '@osee/applicability/types';
import { ARTIFACTTYPEID } from '@osee/shared/types/constants';
import {
	createArtifact,
	modifyArtifact,
	deleteArtifact,
	deleteRelation,
	deleteRelations,
	addRelation,
	addRelations,
} from '@osee/transactions/operators';
import { createArtifact as _createArtifact } from '@osee/transactions/functions';
import {
	attrConfig,
	relation,
	modifyRelation,
	transaction,
	transactionResult,
} from '@osee/transactions/types';
import { pipe, Observable, switchMap, tap, filter, of } from 'rxjs';
import { TransactionService } from './transaction.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { branchSentinel } from '@osee/shared/types';
import { BRANCH_CONTEXT, BranchContextSource } from './branch-context';

/**
 * Whether a branch id names a real, usable branch rather than a "no branch"
 * placeholder. Centralizes the rule so the branch-context tier and the global
 * current-branch read agree on what counts as valid. The empty string is the
 * `toSignal` initial value; `'0'` is the unset route default; `branchSentinel.id`
 * (`'-1'`) is the explicit sentinel.
 */
function isRealBranchId(id: string | undefined): id is string {
	return (
		id !== undefined && id !== '' && id !== '0' && id !== branchSentinel.id
	);
}

@Injectable({
	providedIn: 'root',
})
export class CurrentTransactionService {
	private _uiService = inject(UiService);
	private _txService = inject(TransactionService);

	/**
	 * Optional per-subtree branch pin (see {@link provideBranchContext}). When a
	 * surface edits something other than the global current branch -- e.g. an
	 * artifact explorer tab pinned to its own branch -- it provides this so writes
	 * created under that subtree target the pinned branch, not whatever the global
	 * branch selector currently holds. Null everywhere else = use the global branch.
	 */
	private _branchContext = inject(BRANCH_CONTEXT, { optional: true });

	//note: anything using _id should be assumed to be non-reactive(i.e. called once)
	private _id = toSignal(this._uiService.id.pipe(filter(isRealBranchId)), {
		initialValue: '',
	});

	/**
	 * Resolves the branch a transaction targets, in precedence order:
	 * 1. an explicit `branchIdOverride` argument (when valid),
	 * 2. the subtree's pinned {@link BRANCH_CONTEXT} branch (when provided + valid),
	 * 3. the global current branch (`UiService.id`).
	 *
	 * The context tier is what makes branch targeting correct-by-construction inside
	 * a pinned subtree: callers there do not need to pass an override, so a forgotten
	 * argument can no longer silently write to the wrong branch.
	 */
	private resolveBranch(branchIdOverride?: string): string {
		if (
			branchIdOverride !== undefined &&
			branchIdOverride !== branchSentinel.id
		) {
			return branchIdOverride;
		}
		const context = this._branchContext?.branchId();
		if (isRealBranchId(context)) {
			return context;
		}
		return this._id();
	}

	private emptyTransaction(
		branch: string,
		comment: string
	): Required<transaction> {
		return {
			branch,
			txComment: comment,
			createArtifacts: [],
			modifyArtifacts: [],
			deleteArtifacts: [],
			deleteRelations: [],
			addRelations: [],
		};
	}

	createTransaction(comment: string): Required<transaction> {
		return this.emptyTransaction(this.resolveBranch(), comment);
	}

	createTx(
		comment: string,
		branchIdOverride?: string
	): Observable<Required<transaction>> {
		return of(
			this.emptyTransaction(this.resolveBranch(branchIdOverride), comment)
		);
	}

	createArtifact(
		comment: string,
		artTypeId: ARTIFACTTYPEID,
		applicability: applic,
		relations: relation[],
		...attributes: attribute<
			string | number | boolean | unknown[] | unknown,
			ATTRIBUTETYPEID
		>[]
	) {
		const tx = this.createTransaction(comment);
		return _createArtifact(
			tx,
			artTypeId,
			applicability,
			relations,
			undefined,
			...attributes
		);
	}

	createArt = (
		comment: string,
		artTypeId: ARTIFACTTYPEID,
		applicability: applic,
		relations: relation[],
		...attributes: attribute<
			string | number | boolean | unknown[] | unknown,
			ATTRIBUTETYPEID
		>[]
	) => {
		return this.createTx(comment).pipe(
			createArtifact(artTypeId, applicability, relations, ...attributes)
		);
	};

	createArtifactAndMutate = (
		comment: string,
		artTypeId: ARTIFACTTYPEID,
		applicability: applic,
		relations: relation[],
		...attributes: attribute<
			string | number | boolean | unknown[] | unknown,
			ATTRIBUTETYPEID
		>[]
	) => {
		return this.createArt(
			comment,
			artTypeId,
			applicability,
			relations,
			...attributes
		).pipe(this.performMutation());
	};

	modifyArt = (
		comment: string,
		artId: `${number}`,
		applicability: applic,
		attrConfig: attrConfig,
		branchIdOverride?: string
	) => {
		return this.createTx(comment, branchIdOverride).pipe(
			modifyArtifact(artId, applicability, attrConfig)
		);
	};

	modifyArtifactAndMutate = (
		comment: string,
		artId: `${number}`,
		applicability: applic,
		attrConfig: attrConfig,
		branchIdOverride?: string
	) => {
		return this.modifyArt(
			comment,
			artId,
			applicability,
			attrConfig,
			branchIdOverride
		).pipe(this.performMutation());
	};

	deleteArt = (comment: string, artId: `${number}`) => {
		return this.createTx(comment).pipe(deleteArtifact(artId));
	};

	deleteRelation = (comment: string, relation: modifyRelation) => {
		return this.createTx(comment).pipe(deleteRelation(relation));
	};
	deleteRelations = (comment: string, relations: modifyRelation[]) => {
		return this.createTx(comment).pipe(deleteRelations(relations));
	};

	deleteRelationAndMutate = (comment: string, relation: modifyRelation) => {
		return this.deleteRelation(comment, relation).pipe(
			this.performMutation()
		);
	};

	deleteRelationsAndMutate = (
		comment: string,
		relations: modifyRelation[]
	) => {
		return this.deleteRelations(comment, relations).pipe(
			this.performMutation()
		);
	};

	deleteArtifactAndMutate = (comment: string, artId: `${number}`) => {
		return this.deleteArt(comment, artId).pipe(this.performMutation());
	};

	addRelation = (comment: string, relation: modifyRelation) => {
		return this.createTx(comment).pipe(addRelation(relation));
	};

	addRelationAndMutate = (comment: string, relation: modifyRelation) => {
		return this.addRelation(comment, relation).pipe(this.performMutation());
	};

	addRelations = (comment: string, relations: modifyRelation[]) => {
		return this.createTx(comment).pipe(addRelations(relations));
	};

	addRelationsAndMutate = (comment: string, relations: modifyRelation[]) => {
		return this.addRelations(comment, relations).pipe(
			this.performMutation()
		);
	};

	performMutation() {
		return pipe<
			Observable<Required<transaction>>,
			Observable<Required<transactionResult>>
		>(
			switchMap((tx) =>
				this._txService.performMutation(tx).pipe(
					tap(() => {
						// Backward compat: keep global update for non-migrated parts of the app
						this._uiService.updated = true;
					})
				)
			)
		);
	}
}

/**
 * Pins a component subtree to a branch/view for all transactions created within
 * it. Add to a component's `providers`, then set the branch via the injected
 * {@link BranchContextSource} (e.g. from an `effect` reading the host's inputs).
 *
 * Provides a SCOPED {@link CurrentTransactionService} so the branch-aware instance
 * is the one descendants inject -- the root singleton cannot see a token provided
 * lower in the tree. Any transaction built by a descendant (including the
 * no-override create/relation helpers) then targets the pinned branch.
 */
export function provideBranchContext(): Provider[] {
	return [
		BranchContextSource,
		{ provide: BRANCH_CONTEXT, useExisting: BranchContextSource },
		CurrentTransactionService,
	];
}

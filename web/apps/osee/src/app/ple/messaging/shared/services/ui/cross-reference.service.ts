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
import { Injectable, inject } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import {
	ConnectionService,
	CrossReferenceHttpService,
	MimRouteService,
	PreferencesUIService,
} from '@osee/messaging/shared/services';
import type { CrossReference, connection } from '@osee/messaging/shared/types';
import { TransactionBuilderService } from '@osee/shared/transactions-legacy';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { TransactionService } from '@osee/transactions/services';
import type {
	legacyRelation,
	legacyTransaction,
	transaction,
} from '@osee/transactions/types';
import {
	BehaviorSubject,
	combineLatest,
	debounceTime,
	filter,
	map,
	of,
	repeat,
	shareReplay,
	switchMap,
	take,
	tap,
} from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class CrossReferenceService {
	private crossRefHttpService = inject(CrossReferenceHttpService);
	private ui = inject(MimRouteService);
	private connectionService = inject(ConnectionService);
	private txBuilder = inject(TransactionBuilderService);
	private preferencesService = inject(PreferencesUIService);

	private txService = inject(TransactionService);

	selectedConnectionId = toObservable(this.ui.connectionId);

	set SelectedConnectionId(connection: `${number}`) {
		this.ui.connectionIdString = connection;
	}
	private _currentPage$ = new BehaviorSubject<number>(0);
	private _currentPageSize$ = new BehaviorSubject<number>(10);

	private _filterValue = new BehaviorSubject<string>('');

	private _crossReferences = combineLatest([
		this.branchId,
		this.selectedConnectionId,
		this.filterValue,
		this.ui.viewId,
	]).pipe(
		filter(
			([branchId, connectionId, _filterValue, _viewId]) =>
				branchId !== '' && branchId !== '0' && connectionId !== '-1'
		),
		debounceTime(500),
		switchMap(([branchId, connection, filterValue, viewId]) =>
			this.crossRefHttpService
				.getAll(branchId, connection, filterValue, viewId)
				.pipe(repeat({ delay: () => this.ui.updated }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _connections = this.branchId.pipe(
		filter((v) => v !== ''),
		switchMap((branchId) => this.connectionService.getConnections(branchId))
	);

	addCrossRefToTransaction(
		crossRef: CrossReference,
		tx: Required<transaction>,
		key?: string
	) {
		//TODO: UPDATE CROSS REFS TO USE GAMMA IDS
		//NOTE: this is a manual tx due to crossRefs not supporting gamma ids yet(see above)
		const { id, applicability, connections, ..._remainingAttributes } =
			crossRef;
		// const attributeKeys = Object.keys(
		// 	remainingAttributes
		// ) as (keyof typeof remainingAttributes)[];
		// const attributes = attributeKeys.map((k) => remainingAttributes[k]);
		tx.createArtifacts.push({
			typeId: ARTIFACTTYPEIDENUM.CROSSREFERENCE,
			name: crossRef.name,
			key: key ?? crypto.randomUUID(),
			attributes: [
				{
					typeId: ATTRIBUTETYPEIDENUM.CROSSREFVALUE,
					value: crossRef.crossReferenceValue,
				},
				{
					typeId: ATTRIBUTETYPEIDENUM.CROSSREFARRAYVALUES,
					value: crossRef.crossReferenceArrayValues,
				},
				{
					typeId: ATTRIBUTETYPEIDENUM.CROSSREFADDITIONALCONTENT,
					value: crossRef.crossReferenceAdditionalContent,
				},
			],
		});
		return tx;
	}
	createCrossReferenceTx(
		crossRef: CrossReference,
		branchId: string,
		transaction: legacyTransaction | undefined,
		key?: string
	) {
		return of(
			this.txBuilder.createArtifact(
				crossRef,
				ARTIFACTTYPEIDENUM.CROSSREFERENCE,
				[],
				transaction,
				branchId,
				'Create Cross Reference',
				key
			)
		);
	}

	createCrossReference(crossRef: CrossReference) {
		return combineLatest([this.branchId, this.selectedConnectionId]).pipe(
			take(1),
			filter(
				([branchId, connectionId]) =>
					branchId !== '' && branchId !== '0' && connectionId !== '-1'
			),
			switchMap(([branchId, connection]) =>
				this.createCrossReferenceTx(
					crossRef,
					branchId,
					undefined,
					'CROSS_REF'
				).pipe(
					switchMap((tx) =>
						this.createConnectionRelation(
							connection,
							'CROSS_REF'
						).pipe(
							switchMap((rel) =>
								this.addRelation(branchId, rel, tx)
							)
						)
					)
				)
			),
			switchMap((tx) =>
				this.txService
					.performMutation(tx)
					.pipe(tap((_) => (this.ui.update = true)))
			)
		);
	}

	deleteCrossReference(crossRef: CrossReference) {
		return this.branchId.pipe(
			take(1),
			filter(
				(branchId) =>
					branchId !== '' &&
					branchId !== '0' &&
					crossRef.id !== undefined
			),
			switchMap((branchId) =>
				of(
					this.txBuilder.deleteArtifact(
						crossRef.id!,
						undefined,
						branchId,
						'Delete Cross Reference'
					)
				).pipe(switchMap((tx) => this.txService.performMutation(tx)))
			)
		);
	}

	updateCrossReferenceAttribute<U extends keyof CrossReference>(
		currentCrossRef: CrossReference,
		attribute: U,
		value: CrossReference[U]
	) {
		if (currentCrossRef[attribute] !== value) {
			const updatedCrossRef = {
				id: currentCrossRef.id,
				[attribute]: value,
			};
			const update = this.branchId.pipe(
				filter((v) => v !== ''),
				switchMap((branchId) =>
					of(
						this.txBuilder.modifyArtifact(
							updatedCrossRef,
							undefined,
							branchId,
							'Update Cross Reference Attribute'
						)
					).pipe(
						switchMap((tx) =>
							this.txService.performMutation(tx).pipe(
								tap((txRes) => {
									if (txRes.results.success) {
										currentCrossRef[attribute] = value;
									}
								})
							)
						)
					)
				)
			);
			update.subscribe();
		}
	}

	updateCrossReference(crossRef: CrossReference) {
		return this.branchId.pipe(
			filter((v) => v !== ''),
			switchMap((branchId) =>
				of(
					this.txBuilder.modifyArtifact(
						crossRef,
						undefined,
						branchId,
						'Update Cross Reference'
					)
				).pipe(switchMap((tx) => this.txService.performMutation(tx)))
			)
		);
	}

	createConnectionRelation(connectionId: string, crossReferenceId: string) {
		const relation: legacyRelation = {
			typeName: 'Interface Connection Cross Reference',
			sideA: connectionId,
			sideB: crossReferenceId,
		};
		return of(relation);
	}

	addRelation(
		branchId: string,
		relation: legacyRelation,
		tx: legacyTransaction
	) {
		return of(
			this.txBuilder.addRelation(
				relation.typeName,
				undefined,
				relation.sideA as string,
				relation.sideB as string,
				relation.afterArtifact,
				undefined,
				tx,
				branchId,
				'Relating Cross Reference'
			)
		);
	}

	private _selectedConnection = combineLatest([
		this.selectedConnectionId,
		this.connections,
	]).pipe(
		switchMap(([connectionId, connections]) => {
			let connection: connection | undefined;
			if (connectionId !== '-1') {
				connections.forEach((c) => {
					if (c.id === connectionId) {
						connection = c;
						return;
					}
				});
			}
			return of(connection);
		})
	);

	getFilteredCount(filter: string) {
		return combineLatest([
			this.branchId,
			this.selectedConnectionId,
			this.ui.viewId,
		]).pipe(
			take(1),
			switchMap(([id, connectionId, viewId]) =>
				this.crossRefHttpService.getCount(
					id,
					connectionId,
					filter,
					viewId
				)
			)
		);
	}
	getFilteredPaginatedCrossRefs(pageNum: string | number, filter: string) {
		return combineLatest([
			this.branchId,
			this.selectedConnectionId,
			this.ui.viewId,
			this.currentPageSize,
		]).pipe(
			take(1),
			switchMap(([id, connectionId, viewId, pageSize]) =>
				this.crossRefHttpService.getAll(
					id,
					connectionId,
					filter,
					viewId,
					pageNum,
					pageSize,
					true
				)
			)
		);
	}

	private _inEditMode = this.preferences.pipe(
		map((prefs) => prefs.inEditMode),
		shareReplay(1)
	);

	get preferences() {
		return this.preferencesService.preferences;
	}

	get inEditMode() {
		return this._inEditMode;
	}

	get filterValue() {
		return this._filterValue;
	}

	set FilterValue(value: string) {
		this._filterValue.next(value);
	}

	get branchId() {
		return this.ui.id;
	}

	set BranchId(id: string) {
		this.ui.idValue = id;
	}

	get selectedConnection() {
		return this._selectedConnection;
	}

	get crossReferences() {
		return this._crossReferences;
	}

	get connections() {
		return this._connections;
	}

	get currentPageSize() {
		return this._currentPageSize$;
	}

	get currentPage() {
		return this._currentPage$;
	}

	set page(pg: number) {
		this._currentPage$.next(pg);
	}

	set pageSize(pg: number) {
		this._currentPageSize$.next(pg);
	}
}

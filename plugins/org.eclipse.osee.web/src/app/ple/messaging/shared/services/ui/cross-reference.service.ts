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
import { Injectable } from '@angular/core';
import {
	connection,
	ConnectionService,
	CrossReference,
	CrossReferenceHttpService,
	PreferencesUIService,
} from '@osee/messaging/shared';
import {
	TransactionBuilderService,
	TransactionService,
} from '@osee/shared/transactions';
import { relation, transaction } from '@osee/shared/types';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
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
import { UiService } from 'src/app/ple-services/ui/ui.service';

@Injectable({
	providedIn: 'root',
})
export class CrossReferenceService {
	constructor(
		private crossRefHttpService: CrossReferenceHttpService,
		private ui: UiService,
		private connectionService: ConnectionService,
		private txBuilder: TransactionBuilderService,
		private txService: TransactionService,
		private preferencesService: PreferencesUIService
	) {}

	private _selectedConnectionId = new BehaviorSubject<string>('');
	private _filterValue = new BehaviorSubject<string>('');

	private _crossReferences = combineLatest([
		this.branchId,
		this._selectedConnectionId,
		this.filterValue,
	]).pipe(
		filter(
			([branchId, connectionId, filterValue]) =>
				branchId !== '' && branchId !== '0' && connectionId !== ''
		),
		debounceTime(500),
		switchMap(([branchId, connection, filterValue]) =>
			this.crossRefHttpService
				.getAll(branchId, connection, filterValue)
				.pipe(repeat({ delay: () => this.ui.update }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _connections = this.branchId.pipe(
		filter((v) => v !== ''),
		switchMap((branchId) => this.connectionService.getConnections(branchId))
	);

	createCrossReferenceTx(
		crossRef: CrossReference,
		branchId: string,
		transaction: transaction | undefined,
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
					branchId !== '' && branchId !== '0' && connectionId !== ''
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
					.pipe(tap((_) => (this.ui.updated = true)))
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

	updateCrossReferenceAttribute(
		currentCrossRef: CrossReference,
		attribute: keyof CrossReference,
		value: string
	) {
		if (currentCrossRef[attribute] !== value) {
			let updatedCrossRef = { ...currentCrossRef } as CrossReference;
			updatedCrossRef[attribute] = value;
			const update = this.branchId.pipe(
				switchMap((branchId) =>
					of(
						this.txBuilder.modifyArtifact(
							updatedCrossRef,
							undefined,
							branchId,
							'Update Cross Reference'
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
		let relation: relation = {
			typeName: 'Interface Connection Cross Reference',
			sideA: connectionId,
			sideB: crossReferenceId,
		};
		return of(relation);
	}

	addRelation(branchId: string, relation: relation, tx: transaction) {
		return of(
			this.txBuilder.addRelation(
				relation.typeName,
				undefined,
				relation.sideA as string,
				relation.sideB as string,
				undefined,
				tx,
				branchId,
				'Relating Cross Reference'
			)
		);
	}

	private _selectedConnection = combineLatest([
		this._selectedConnectionId,
		this.connections,
	]).pipe(
		switchMap(([connectionId, connections]) => {
			let connection: connection | undefined;
			if (connectionId !== '') {
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

	get selectedConnectionId() {
		return this._selectedConnectionId;
	}

	set SelectedConnectionId(connection: string) {
		this._selectedConnectionId.next(connection);
	}
}

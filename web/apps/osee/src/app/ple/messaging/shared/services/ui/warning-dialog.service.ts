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
import { inject, Injectable } from '@angular/core';
import {
	MatDialog,
	MatDialogConfig,
	MatDialogRef,
} from '@angular/material/dialog';
import { AffectedArtifactDialogComponent } from '@osee/messaging/shared/dialogs/warnings';
import type {
	affectedArtifact,
	affectedArtifactWarning,
	element,
	message,
	PlatformType,
	structure,
	subMessage,
} from '@osee/messaging/shared/types';
import {
	legacyCreateArtifact,
	legacyModifyArtifact,
	legacyModifyRelation,
} from '@osee/transactions/types';
import { from, of } from 'rxjs';
import {
	concatMap,
	distinct,
	filter,
	last,
	map,
	scan,
	startWith,
	switchMap,
	take,
} from 'rxjs/operators';
import { BranchedAffectedArtifactService } from '../ui/branched-affected-artifact.service';

@Injectable({
	providedIn: 'any',
})
export class WarningDialogService {
	private dialog = inject(MatDialog);
	private affectedArtifacts = inject(BranchedAffectedArtifactService);

	private _openDialog<T>(
		config: MatDialogConfig<T>
	): MatDialogRef<AffectedArtifactDialogComponent<T>, T> {
		return this.dialog.open(AffectedArtifactDialogComponent<T>, config);
	}
	private _listenToDialogEmission<T>(config: MatDialogConfig<T>) {
		return this._openDialog(config).afterClosed().pipe(take(1));
	}
	openMessageDialog(body: Partial<message>) {
		return of(body.id).pipe(
			take(1),
			filter((id: string | undefined): id is string => id !== undefined),
			switchMap((id) =>
				this._getAffectedConnectionsFromAffectedArtifacts([id])
			),
			switchMap((artifacts) =>
				artifacts.length > 1
					? this._listenToDialogEmission({
							data: {
								affectedArtifacts: artifacts,
								body: body,
								modifiedObjectType: 'Message',
								affectedObjectType: 'Connection',
							},
						}).pipe(
							filter(
								(
									value
								): value is affectedArtifactWarning<message> =>
									value !== undefined
							),
							map((value) => value.body)
						)
					: of(body)
			)
		);
	}

	openMessageDialogForValidation(body: `${number}`) {
		return of(body).pipe(
			take(1),
			filter((id: string | undefined): id is string => id !== undefined),
			switchMap((id) =>
				this._getAffectedConnectionsFromAffectedArtifacts([id])
			),
			switchMap((artifacts) =>
				artifacts.length > 1
					? this._listenToDialogEmission({
							data: {
								affectedArtifacts: artifacts,
								body: body,
								modifiedObjectType: 'Message',
								affectedObjectType: 'Connection',
							},
						}).pipe(map((v) => (v !== undefined ? true : false)))
					: of(true)
			)
		);
	}
	openSubMessageDialog(body: Partial<subMessage>) {
		return of(body.id).pipe(
			take(1),
			filter((id: string | undefined): id is string => id !== undefined),
			switchMap((id) =>
				this.affectedArtifacts.getMessagesBySubMessage(id)
			),
			switchMap((artifacts) =>
				artifacts.length > 1
					? this._listenToDialogEmission({
							data: {
								affectedArtifacts: artifacts,
								body: body,
								modifiedObjectType: 'SubMessage',
								affectedObjectType: 'Message',
							},
						}).pipe(
							filter(
								(
									value
								): value is affectedArtifactWarning<subMessage> =>
									value !== undefined
							),
							map((value) => value.body)
						)
					: of(body)
			)
		);
	}

	openSubMessageDialogForValidation(body: `${number}`) {
		return of(body).pipe(
			take(1),
			filter((id: string | undefined): id is string => id !== undefined),
			switchMap((id) =>
				this.affectedArtifacts.getMessagesBySubMessage(id)
			),
			switchMap((artifacts) =>
				artifacts.length > 1
					? this._listenToDialogEmission({
							data: {
								affectedArtifacts: artifacts,
								body: body,
								modifiedObjectType: 'SubMessage',
								affectedObjectType: 'Message',
							},
						}).pipe(map((v) => (v !== undefined ? true : false)))
					: of(true)
			)
		);
	}

	openStructureDialog(body: Partial<structure>) {
		return of(body.id).pipe(
			take(1),
			filter((id: string | undefined): id is string => id !== undefined),
			switchMap((id) =>
				this.affectedArtifacts.getSubMessagesByStructure(id)
			),
			switchMap((artifacts) =>
				artifacts.length > 1
					? this._listenToDialogEmission({
							data: {
								affectedArtifacts: artifacts,
								body: body,
								modifiedObjectType: 'Structure',
								affectedObjectType: 'Submessage',
							},
						}).pipe(
							filter(
								(
									value
								): value is affectedArtifactWarning<structure> =>
									value !== undefined
							),
							map((value) => value.body)
						)
					: of(body)
			)
		);
	}

	openStructureDialogForValidation(body: `${number}`) {
		return of(body).pipe(
			take(1),
			filter((id: string | undefined): id is string => id !== undefined),
			switchMap((id) =>
				this.affectedArtifacts.getSubMessagesByStructure(id)
			),
			switchMap((artifacts) =>
				artifacts.length > 1
					? this._listenToDialogEmission({
							data: {
								affectedArtifacts: artifacts,
								body: body,
								modifiedObjectType: 'Structure',
								affectedObjectType: 'Submessage',
							},
						}).pipe(map((v) => (v !== undefined ? true : false)))
					: of(true)
			)
		);
	}

	openElementDialogForValidation(body: `${number}`) {
		return of(body).pipe(
			take(1),
			filter((id: string | undefined): id is string => id !== undefined),
			switchMap((id) =>
				this.affectedArtifacts.getStructuresByElement(id)
			),
			switchMap((artifacts) =>
				artifacts.length > 1
					? this._listenToDialogEmission({
							data: {
								affectedArtifacts: artifacts,
								body: body,
								modifiedObjectType: 'Element',
								affectedObjectType: 'Structure',
							},
						}).pipe(map((v) => (v !== undefined ? true : false)))
					: of(true)
			)
		);
	}

	openElementDialog(body: Partial<element>) {
		return of(body.id).pipe(
			take(1),
			filter((id: string | undefined): id is string => id !== undefined),
			switchMap((id) =>
				this.affectedArtifacts.getStructuresByElement(id)
			),
			switchMap((artifacts) =>
				artifacts.length > 1
					? this._listenToDialogEmission({
							data: {
								affectedArtifacts: artifacts,
								body: body,
								modifiedObjectType: 'Element',
								affectedObjectType: 'Structure',
							},
						}).pipe(
							filter(
								(
									value
								): value is affectedArtifactWarning<element> =>
									value !== undefined
							),
							map((value) => value.body)
						)
					: of(body)
			)
		);
	}

	openElementDialogById(body: `${number}`) {
		return of(body).pipe(
			take(1),
			filter((id: string | undefined): id is string => id !== undefined),
			switchMap((id) =>
				this.affectedArtifacts.getStructuresByElement(id)
			),
			switchMap((artifacts) =>
				artifacts.length > 1
					? this._listenToDialogEmission({
							data: {
								affectedArtifacts: artifacts,
								body: body,
								modifiedObjectType: 'Element',
								affectedObjectType: 'Structure',
							},
						}).pipe(
							filter(
								(
									value
								): value is affectedArtifactWarning<`${number}`> =>
									value !== undefined
							),
							map((value) => value.body)
						)
					: of(body)
			)
		);
	}

	openPlatformTypeDialog(body: Partial<PlatformType>) {
		return of(body.id).pipe(
			take(1),
			filter((id: string | undefined): id is string => id !== undefined),
			switchMap((id) => this.affectedArtifacts.getElementsByType(id)),
			switchMap((artifacts) =>
				artifacts.length > 1
					? this._listenToDialogEmission({
							data: {
								affectedArtifacts: artifacts,
								body: body,
								modifiedObjectType: 'Platform Type',
								affectedObjectType: 'Element',
							},
						}).pipe(
							filter(
								(
									value
								): value is affectedArtifactWarning<PlatformType> =>
									value !== undefined
							),
							map((value) => value.body)
						)
					: of(body)
			)
		);
	}
	openPlatformTypeDialogWithManifest(tx: {
		createArtifacts: legacyCreateArtifact[];
		modifyArtifacts: legacyModifyArtifact[];
		deleteRelations: legacyModifyRelation[];
	}) {
		const platformType = tx.modifyArtifacts?.pop();
		const enumSetIdArray: string[] = [];
		const enumSetArray: legacyModifyArtifact[] = [];
		if (tx.modifyArtifacts !== undefined && tx.modifyArtifacts.length > 1) {
			const enumSet = tx.modifyArtifacts?.pop();
			if (enumSet) {
				enumSetArray.push(enumSet);
				enumSetIdArray.push(enumSet.id);
			}
		}
		const platformTypeArray: string[] = [];
		if (platformType) {
			platformTypeArray.push(platformType.id);
		}

		return this.openEnumsDialogs(
			tx.modifyArtifacts !== undefined && tx.modifyArtifacts.length >= 1
				? tx.modifyArtifacts.map((v) => v.id)
				: [],
			enumSetIdArray,
			platformTypeArray
		).pipe(
			map((_) => {
				if (enumSetIdArray.length > 1) {
					tx.modifyArtifacts.push(...enumSetArray);
				}
				if (platformType) {
					tx.modifyArtifacts.push(platformType);
				}
				return tx;
			})
		);
	}

	private _getAffectedEnumSetsFromEnums(enums: string[]) {
		return of(enums).pipe(
			concatMap((enumeration) =>
				from(enumeration).pipe(
					switchMap((e) =>
						this.affectedArtifacts.getEnumSetsByEnums(e)
					),
					concatMap((arts) => from(arts))
				)
			),
			distinct((v) => v.id),
			scan((acc, curr) => [...acc, curr], [] as affectedArtifact[]),
			startWith([]),
			last()
		);
	}

	private _getAffectedPlatformTypesFromAffectedArtifacts(enumSets: string[]) {
		return of(enumSets).pipe(
			concatMap((enumSets) =>
				from(enumSets).pipe(
					switchMap((e) =>
						this.affectedArtifacts.getPlatformTypesByEnumSet(e)
					),
					concatMap((arts) => from(arts))
				)
			),
			distinct((v) => v.id),
			scan((acc, curr) => [...acc, curr], [] as affectedArtifact[]),
			startWith([]),
			last()
		);
	}

	private _getAffectedElementsFromAffectedArtifacts(platformTypes: string[]) {
		return of(platformTypes).pipe(
			concatMap((enumSets) =>
				from(enumSets).pipe(
					switchMap((e) =>
						this.affectedArtifacts.getElementsByType(e)
					),
					concatMap((arts) => from(arts))
				)
			),
			distinct((v) => v.id),
			scan((acc, curr) => [...acc, curr], [] as affectedArtifact[]),
			startWith([]),
			last()
		);
	}
	private _getAffectedStructuresFromAffectedArtifacts(elements: string[]) {
		return of(elements).pipe(
			concatMap((enumSets) =>
				from(enumSets).pipe(
					switchMap((e) =>
						this.affectedArtifacts.getStructuresByElement(e)
					),
					concatMap((arts) => from(arts))
				)
			),
			distinct((v) => v.id),
			scan((acc, curr) => [...acc, curr], [] as affectedArtifact[]),
			startWith([]),
			last()
		);
	}

	private _getAffectedSubmessagesFromAffectedArtifacts(structures: string[]) {
		return of(structures).pipe(
			concatMap((enumSets) =>
				from(enumSets).pipe(
					switchMap((e) =>
						this.affectedArtifacts.getSubMessagesByStructure(e)
					),
					concatMap((arts) => from(arts))
				)
			),
			distinct((v) => v.id),
			scan((acc, curr) => [...acc, curr], [] as affectedArtifact[]),
			startWith([]),
			last()
		);
	}
	private _getAffectedMessagesFromAffectedArtifacts(submessages: string[]) {
		return of(submessages).pipe(
			concatMap((enumSets) =>
				from(enumSets).pipe(
					switchMap((e) =>
						this.affectedArtifacts.getMessagesBySubMessage(e)
					),
					concatMap((arts) => from(arts))
				)
			),
			distinct((v) => v.id),
			scan((acc, curr) => [...acc, curr], [] as affectedArtifact[]),
			startWith([]),
			last()
		);
	}

	private _getAffectedConnectionsFromAffectedArtifacts(messages: string[]) {
		return of(messages).pipe(
			concatMap((enumSets) =>
				from(enumSets).pipe(
					switchMap((e) =>
						this.affectedArtifacts.getConnectionsByMessage(e)
					),
					concatMap((arts) => from(arts))
				)
			),
			distinct((v) => v.id),
			scan((acc, curr) => [...acc, curr], [] as affectedArtifact[]),
			startWith([]),
			last()
		);
	}
	private _getAffectedNodesFromAffectedArtifacts(connections: string[]) {
		return of(connections).pipe(
			concatMap((enumSets) =>
				from(enumSets).pipe(
					switchMap((e) =>
						this.affectedArtifacts.getNodesByConnection(e)
					),
					concatMap((arts) => from(arts))
				)
			),
			distinct((v) => v.id),
			scan((acc, curr) => [...acc, curr], [] as affectedArtifact[]),
			startWith([]),
			last()
		);
	}

	openEnumsDialogs(
		enums: string[],
		enumSets: string[],
		platformTypes: string[] = []
	) {
		return this._getAffectedEnumSetsFromEnums(enums).pipe(
			switchMap((artifacts) =>
				artifacts.length > 1
					? this._listenToDialogEmission({
							data: {
								affectedArtifacts: artifacts,
								body: artifacts,
								modifiedObjectType: 'Enum',
								affectedObjectType: 'Enum Set',
							},
						}).pipe(
							filter(
								(
									value
								): value is affectedArtifactWarning<
									affectedArtifact[]
								> => value !== undefined
							),
							map((value) => value.affectedArtifacts)
						)
					: of(artifacts)
			),
			switchMap((enumSetArtifacts) =>
				this._getAffectedPlatformTypesFromAffectedArtifacts([
					...enumSetArtifacts.map((v) => v.id),
					...enumSets,
				])
			),
			switchMap((artifacts) =>
				artifacts.length > 1
					? this._listenToDialogEmission({
							data: {
								affectedArtifacts: artifacts,
								body: artifacts,
								modifiedObjectType: 'Enum Set',
								affectedObjectType: 'Platform Type',
							},
						}).pipe(
							filter(
								(
									value
								): value is affectedArtifactWarning<
									affectedArtifact[]
								> => value !== undefined
							),
							map((value) => value.affectedArtifacts)
						)
					: of(artifacts)
			),
			switchMap((platformTypeArtifacts) =>
				this._getAffectedElementsFromAffectedArtifacts([
					...platformTypeArtifacts.map((v) => v.id),
					...platformTypes,
				])
			),
			switchMap((artifacts) =>
				artifacts.length > 1
					? this._listenToDialogEmission({
							data: {
								affectedArtifacts: artifacts,
								body: artifacts,
								modifiedObjectType: 'Platform Type',
								affectedObjectType: 'Element',
							},
						}).pipe(
							filter(
								(
									value
								): value is affectedArtifactWarning<
									affectedArtifact[]
								> => value !== undefined
							),
							map((value) => value.affectedArtifacts)
						)
					: of(artifacts)
			)
		);
	}
}

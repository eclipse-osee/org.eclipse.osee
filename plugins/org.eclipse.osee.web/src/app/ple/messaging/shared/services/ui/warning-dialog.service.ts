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
import { Injectable } from '@angular/core';
import {
	MatDialog,
	MatDialogConfig,
	MatDialogRef,
} from '@angular/material/dialog';
import { AffectedArtifactDialogComponent } from '@osee/messaging/shared/dialogs';
import {
	affectedArtifactWarning,
	element,
	structure,
	subMessage,
} from '@osee/messaging/shared/types';
import { of } from 'rxjs';
import { filter, map, switchMap, take, tap } from 'rxjs/operators';
import { BranchedAffectedArtifactService } from '../ui/branched-affected-artifact.service';

@Injectable({
	providedIn: 'any',
})
export class WarningDialogService {
	constructor(
		private dialog: MatDialog,
		private affectedArtifacts: BranchedAffectedArtifactService
	) {}
	private _openDialog<T>(
		config: MatDialogConfig<T>
	): MatDialogRef<AffectedArtifactDialogComponent<T>, T> {
		return this.dialog.open(AffectedArtifactDialogComponent<T>, config);
	}
	private _listenToDialogEmission<T>(config: MatDialogConfig<T>) {
		return this._openDialog(config).afterClosed().pipe(take(1));
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
}

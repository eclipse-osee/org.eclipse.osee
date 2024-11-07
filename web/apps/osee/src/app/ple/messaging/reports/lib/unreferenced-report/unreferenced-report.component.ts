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
import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import {
	PreferencesUIService,
	UnreferencedUiService,
	WarningDialogService,
} from '@osee/messaging/shared/services';
import { UiService } from '@osee/shared/services';
import { NamedIdWithGammas } from '@osee/shared/types';
import { TransactionService } from '@osee/transactions/services';
import { legacyTransaction } from '@osee/transactions/types';
import {
	concatMap,
	filter,
	from,
	map,
	of,
	reduce,
	switchMap,
	take,
	tap,
} from 'rxjs';
import { NamedIdTableComponent } from '../unreferenced-report/named-id-table/named-id-table.component';

const _warningDialogTypes = [
	'type',
	'element',
	'structure',
	'submessage',
	'message',
] as const;

type warningDialogTypes =
	(typeof _warningDialogTypes)[keyof typeof _warningDialogTypes];

@Component({
	selector: 'osee-unreferenced-report',
	standalone: true,
	imports: [NamedIdTableComponent],
	template: `
		<div class="tw-px-4 tw-py-2 tw-text-xl">Platform Types</div>
		<osee-named-id-table
			[content]="unReferencedUiService.types()"
			[(currentPage)]="unReferencedUiService.currentTypesPage"
			[size]="unReferencedUiService.typesCount()"
			(currentPageSize)="
				unReferencedUiService.currentTypesPageSize.set($event)
			"
			[(filter)]="unReferencedUiService.currentTypesFilter"
			[inEditMode]="inEditMode()"
			(itemsToDelete)="
				deleteArtifacts($event, 'type')
			"></osee-named-id-table>
		<div class="tw-px-4 tw-py-2 tw-text-xl">Elements</div>
		<osee-named-id-table
			[content]="unReferencedUiService.elements()"
			[(currentPage)]="unReferencedUiService.currentElementsPage"
			[size]="unReferencedUiService.elementsCount()"
			(currentPageSize)="
				unReferencedUiService.currentElementsPageSize.set($event)
			"
			[(filter)]="unReferencedUiService.currentElementsFilter"
			[inEditMode]="inEditMode()"
			(itemsToDelete)="
				deleteArtifacts($event, 'element')
			"></osee-named-id-table>
		<div class="tw-px-4 tw-py-2 tw-text-xl">Structures</div>
		<osee-named-id-table
			[content]="unReferencedUiService.structures()"
			[(currentPage)]="unReferencedUiService.currentStructuresPage"
			[size]="unReferencedUiService.structuresCount()"
			(currentPageSize)="
				unReferencedUiService.currentStructuresPageSize.set($event)
			"
			[(filter)]="unReferencedUiService.currentStructuresFilter"
			[inEditMode]="inEditMode()"
			(itemsToDelete)="
				deleteArtifacts($event, 'structure')
			"></osee-named-id-table>
		<div class="tw-px-4 tw-py-2 tw-text-xl">Submessages</div>
		<osee-named-id-table
			[content]="unReferencedUiService.submessages()"
			[(currentPage)]="unReferencedUiService.currentSubmessagesPage"
			[size]="unReferencedUiService.submessagesCount()"
			(currentPageSize)="
				unReferencedUiService.currentSubmessagesPageSize.set($event)
			"
			[(filter)]="unReferencedUiService.currentSubmessagesFilter"
			[inEditMode]="inEditMode()"
			(itemsToDelete)="
				deleteArtifacts($event, 'submessage')
			"></osee-named-id-table>
		<div class="tw-px-4 tw-py-2 tw-text-xl">Messages</div>
		<osee-named-id-table
			[content]="unReferencedUiService.messages()"
			[(currentPage)]="unReferencedUiService.currentMessagesPage"
			[size]="unReferencedUiService.messagesCount()"
			(currentPageSize)="
				unReferencedUiService.currentMessagesPageSize.set($event)
			"
			[(filter)]="unReferencedUiService.currentMessagesFilter"
			[inEditMode]="inEditMode()"
			(itemsToDelete)="
				deleteArtifacts($event, 'message')
			"></osee-named-id-table>
	`,
})
export class UnreferencedReportComponent {
	private _ui = inject(UiService);
	protected unReferencedUiService = inject(UnreferencedUiService);
	private route = inject(ActivatedRoute);

	private txService = inject(TransactionService);
	private preferencesService = inject(PreferencesUIService);

	private warningDialogService = inject(WarningDialogService);

	protected inEditMode = toSignal(this.preferencesService.inEditMode, {
		initialValue: false,
	});
	constructor() {
		this.route.paramMap.subscribe((params) => {
			this._ui.idValue = params.get('branchId') || '';
			this._ui.typeValue =
				(params.get('branchType') as 'working' | 'baseline' | '') || '';
		});
	}

	private getWarningDialog(
		value: NamedIdWithGammas | NamedIdWithGammas[],
		type: warningDialogTypes
	) {
		switch (type) {
			case 'type':
				if (Array.isArray(value)) {
					return from(value).pipe(
						concatMap((v) =>
							this.warningDialogService
								.openPlatformTypeDialog({ id: v.id })
								.pipe(map((_) => v))
						),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as NamedIdWithGammas[]
						)
					);
				} else {
					return this.warningDialogService
						.openPlatformTypeDialog(value)
						.pipe(map((_) => value));
				}
			case 'element':
				if (Array.isArray(value)) {
					return this.warningDialogService
						.openElementDialog(value)
						.pipe(map((_) => value));
				} else {
					return this.warningDialogService
						.openElementDialog([value])
						.pipe(map((_) => value));
				}
			case 'structure':
				if (Array.isArray(value)) {
					return from(value).pipe(
						concatMap((v) =>
							this.warningDialogService
								.openStructureDialog({ id: v.id })
								.pipe(map((_) => v))
						),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as NamedIdWithGammas[]
						)
					);
				} else {
					return this.warningDialogService
						.openStructureDialog({ id: value.id })
						.pipe(map((_) => value));
				}
			case 'submessage':
				if (Array.isArray(value)) {
					return from(value).pipe(
						concatMap((v) =>
							this.warningDialogService
								.openSubMessageDialog({ id: v.id })
								.pipe(map((_) => v))
						),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as NamedIdWithGammas[]
						)
					);
				} else {
					return this.warningDialogService
						.openSubMessageDialog(value)
						.pipe(map((_) => value));
				}
			case 'message':
				if (Array.isArray(value)) {
					return from(value).pipe(
						concatMap((v) =>
							this.warningDialogService
								.openMessageDialog({ id: v.id })
								.pipe(map((_) => v))
						),
						reduce(
							(acc, curr) => [...acc, curr],
							[] as NamedIdWithGammas[]
						)
					);
				} else {
					return this.warningDialogService
						.openMessageDialog(value)
						.pipe(map((_) => value));
				}
			default:
				return of(value);
		}
	}

	deleteArtifacts(
		value: NamedIdWithGammas | NamedIdWithGammas[],
		type: warningDialogTypes
	) {
		this._ui.id
			.pipe(
				take(1),
				filter((id) => id !== '' && id !== '-1' && id !== '0'),
				switchMap((id) =>
					this.getWarningDialog(value, type).pipe(map((_) => id))
				),
				switchMap((id) =>
					of<legacyTransaction>({
						branch: id,
						txComment: 'Deletion from Unreferenced Artifact Report',
						deleteArtifacts: Array.isArray(value)
							? value.map((v) => v.id)
							: [value.id],
					})
				),
				switchMap((tx) =>
					this.txService
						.performMutation(tx)
						.pipe(tap((_) => (this._ui.updated = true)))
				)
			)
			.subscribe();
	}
}
export default UnreferencedReportComponent;

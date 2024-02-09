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
import {
	Component,
	EventEmitter,
	Input,
	OnChanges,
	Output,
	SimpleChanges,
	ViewChild,
	computed,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
	BehaviorSubject,
	Observable,
	OperatorFunction,
	combineLatest,
	filter,
	map,
	of,
	switchMap,
	take,
	tap,
} from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { CreateChildArtifactDialogComponent } from '../create-child-artifact-dialog/create-child-artifact-dialog.component';
import { MatMenuModule } from '@angular/material/menu';
import {
	artifactToCreate,
	attribute,
} from '../../../types/artifact-explorer.data';
import { UiService } from '@osee/shared/services';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { TransactionService } from '@osee/shared/transactions';
import { RELATIONTYPEIDENUM } from '@osee/shared/types/constants';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatIconModule } from '@angular/material/icon';

@Component({
	selector: 'osee-artifact-options-context-menu',
	standalone: true,
	imports: [CommonModule, MatButtonModule, MatMenuModule, MatIconModule],
	templateUrl: './artifact-options-context-menu.component.html',
})
export class ArtifactOptionsContextMenuComponent implements OnChanges {
	@Input() artifactId!: `${number}`;
	private _artifactId = new BehaviorSubject<`${number}`>('0');

	branchId$ = this.uiService.id;

	private _branchType = toSignal(this.uiService.type, {
		initialValue: 'baseline',
	});
	protected _branchEditable = computed(
		() => this._branchType() === 'working'
	);

	ngOnChanges(changes: SimpleChanges): void {
		if (
			changes.artifactId !== undefined &&
			changes.artifactId.previousValue !==
				changes.artifactId.currentValue &&
			changes.artifactId.currentValue !== undefined
		) {
			this._artifactId.next(changes.artifactId.currentValue);
		}
	}

	// @todo - load artifact options in this file once options are added to artifact type constructor
	defaultOption$ = of([
		{
			name: 'Create Child Artifact',
			iconName: 'add',
			iconColor: 'tw-text-success',
		},
	]);

	constructor(
		public dialog: MatDialog,
		private transactionService: TransactionService,
		private uiService: UiService,
		private pathsService: ArtifactHierarchyPathService
	) {}

	selectOption(option: string) {
		if (this._branchEditable()) {
			switch (option) {
				case 'Create Child Artifact':
					combineLatest([this._artifactId, this.branchId$])
						.pipe(
							take(1),
							switchMap(([parentArtifactId, branchId]) =>
								this.dialog
									.open(CreateChildArtifactDialogComponent, {
										data: new artifactToCreate(
											parentArtifactId
										),
										minWidth: '60%',
									})
									.afterClosed()
									.pipe(
										filter(
											(data) =>
												data &&
												data?.name !== '' &&
												data?.artifactTypeId !== '0' &&
												data?.parentArtifactId !== '0'
										),
										switchMap((result) =>
											this.transactionService
												.performMutation({
													branch: branchId,
													txComment:
														'Creating artifact: ' +
														result?.name,
													createArtifacts: [
														{
															name: result?.name,
															typeId: result?.artifactTypeId,
															attributes:
																result?.attributes
																	.filter(
																		(
																			attr: attribute
																		) =>
																			attr.value !=
																			null
																	)
																	.map(
																		(
																			attr: attribute
																		) => ({
																			typeId: attr.typeId,
																			value: attr.value,
																		})
																	),
															relations: [
																{
																	typeId: RELATIONTYPEIDENUM.DEFAULT_HIERARCHICAL,
																	sideA: result?.parentArtifactId,
																},
															],
														},
													],
												})
												.pipe(
													take(1),
													map((txResult) => {
														if (
															txResult.results
																.success == true
														) {
															var firstId =
																txResult.results.ids?.at(
																	0
																);
															if (
																firstId !==
																undefined
															)
																this.pathsService.selectedArtifactId.next(
																	firstId
																);
														}
													})
												)
										)
									)
							)
						)
						.subscribe();
					break;
			}
		}
	}
}

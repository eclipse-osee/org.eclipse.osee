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
import { DragDropModule, CdkDragDrop } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { Component, OnChanges, Input, SimpleChanges } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { UiService } from '@osee/shared/services';
import {
	TransactionBuilderService,
	TransactionService,
} from '@osee/shared/transactions';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	switchMap,
	repeat,
	shareReplay,
	take,
	of,
	tap,
	iif,
} from 'rxjs';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { RelationDeleteDialogComponent } from '../relation-delete-dialog/relation-delete-dialog.component';
import {
	fetchIconFromDictionary,
	artifact,
	relationSide,
	relation,
} from '../../../types/artifact-explorer.data';

@Component({
	selector: 'osee-relations-editor-panel',
	standalone: true,
	imports: [
		CommonModule,
		MatExpansionModule,
		MatButtonModule,
		MatIconModule,
		MatListModule,
		MatDialogModule,
		DragDropModule,
	],
	templateUrl: './relations-editor-panel.component.html',
})
export class RelationsEditorPanelComponent implements OnChanges {
	@Input({ required: true }) artifactId!: string;
	@Input({ required: true }) branchId!: string;
	@Input({ required: true }) viewId!: string;
	@Input({ required: true }) editable!: boolean;

	private _artifactId = new BehaviorSubject<string>('');
	private _branchId = new BehaviorSubject<string>('');
	private _viewId = new BehaviorSubject<string>('');
	private _editable = new BehaviorSubject<boolean>(false);

	ngOnChanges(changes: SimpleChanges): void {
		if (
			changes.artifactId !== undefined &&
			changes.artifactId.previousValue !==
				changes.artifactId.currentValue &&
			changes.artifactId.currentValue !== undefined
		) {
			this._artifactId.next(changes.artifactId.currentValue);
		}
		if (
			changes.branchId !== undefined &&
			changes.branchId.previousValue !== changes.branchId.currentValue &&
			changes.branchId.currentValue !== undefined
		) {
			this._branchId.next(changes.branchId.currentValue);
		}
		if (
			changes.viewId !== undefined &&
			changes.viewId.previousValue !== changes.viewId.currentValue &&
			changes.viewId.currentValue !== undefined
		) {
			this._viewId.next(changes.viewId.currentValue);
		}
		if (
			changes.editable !== undefined &&
			changes.editable.previousValue !== changes.editable.currentValue &&
			changes.editable.currentValue !== undefined
		) {
			this._editable.next(changes.editable.currentValue);
		}
	}

	artWithRelation$ = combineLatest([
		this._branchId,
		this._viewId,
		this._artifactId,
	]).pipe(
		filter(([branch, view, artifact]) => branch != '' && artifact != ''),
		switchMap(([branch, view, artifact]) =>
			this.artExpHttpService
				.getDirectRelations(branch, artifact, view)
				.pipe(repeat({ delay: () => this.uiService.update }))
		),
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);

	fetchIcon(key: string): string {
		return fetchIconFromDictionary(key);
	}

	constructor(
		private artExpHttpService: ArtifactExplorerHttpService,
		private builder: TransactionBuilderService,
		private transaction: TransactionService,
		private uiService: UiService,
		public dialog: MatDialog
	) {}

	toggleExpand(identifier: string) {
		const newArray = this.dropdownsOpen.value;
		const index = newArray.indexOf(identifier);
		// If relation exists in relations dropdowns behavior subject, remove it
		if (index !== -1) {
			newArray.splice(index, 1);
			this.dropdownsOpen.next(newArray);
		} else {
			// Otherwise add the relation to the behavior subject
			this.dropdownsOpen.value.push(identifier);
		}
	}

	addRelationOnItemDropped(
		event: CdkDragDrop<artifact[]>,
		relation: relation,
		side: relationSide
	): void {
		const droppedArt: artifact = event.item.data;
		if (this._editable && droppedArt.editable) {
			// Build the transaction based on which side an artifact is dropped into
			this._branchId
				.pipe(
					take(1),
					filter((id) => id == this.uiService.id.value),
					switchMap((branchId) =>
						of(
							this.builder.addRelation(
								undefined,
								relation.relationTypeToken.id,
								side.isSideA ? droppedArt.id : this.artifactId,
								side.isSideA ? this.artifactId : droppedArt.id,
								'end',
								undefined,
								undefined,
								branchId,
								'Adding relation in OSEE web artifact explorer'
							)
						).pipe(
							take(1),
							switchMap((transaction) =>
								this.transaction.performMutation(transaction)
							),
							tap(() => (this.uiService.updated = true))
						)
					)
				)
				.subscribe();
		}
	}

	deleteRelation(otherArt: artifact, relation: relation, side: relationSide) {
		const branchId = this._branchId.pipe(
			take(1),
			filter((id) => id != '')
		);
		this.artWithRelation$
			.pipe(
				take(1),
				switchMap((art) =>
					this.dialog
						.open(RelationDeleteDialogComponent, {
							data: {
								sideAName: otherArt.name,
								sideBName: art.artName,
							},
						})
						.afterClosed()
						.pipe(
							take(1),
							switchMap((dialogResult: string) =>
								iif(
									() => dialogResult === 'ok',
									branchId.pipe(
										switchMap((id) =>
											of(
												this.builder.deleteRelation(
													undefined,
													relation.relationTypeToken
														.id,
													side.isSideA
														? otherArt.id
														: art.artId,
													side.isSideA
														? art.artId
														: otherArt.id,
													undefined,
													undefined,
													id,
													'Deleting relation in OSEE web artifact explorer'
												)
											).pipe(
												take(1),
												switchMap((transaction) =>
													this.transaction.performMutation(
														transaction
													)
												),
												tap(
													() =>
														(this.uiService.updated =
															true)
												)
											)
										)
									),
									of()
								)
							)
						)
				)
			)
			.subscribe();
	}

	// panel open/close state handling
	panelOpen = new BehaviorSubject<boolean>(false);
	togglePanel() {
		this.panelOpen.next(!this.panelOpen.value);
	}

	// track the state of the UI as relation dropdowns expand and collapse
	dropdownsOpen = new BehaviorSubject<string[]>([]);

	relationOpen(index: number, relation: relation) {
		return relation.relationTypeToken.id;
	}

	relationSideOpen(index: number, relationSide: relationSide) {
		return relationSide.name;
	}
}

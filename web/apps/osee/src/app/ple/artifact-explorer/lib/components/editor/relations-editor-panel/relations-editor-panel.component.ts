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
import { CdkDragDrop, CdkDropList } from '@angular/cdk/drag-drop';
import { AsyncPipe, NgClass } from '@angular/common';
import { Component, computed, inject, input } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatList } from '@angular/material/list';
import { ExpandIconComponent } from '@osee/shared/components';
import { UiService } from '@osee/shared/services';
import { CurrentTransactionService } from '@osee/transactions/services';
import { RELATIONTYPEID } from '@osee/shared/types/constants';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	iif,
	of,
	repeat,
	shareReplay,
	switchMap,
	take,
	tap,
} from 'rxjs';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { ArtifactIconService } from '../../../services/artifact-icon.service';
import {
	artifactWithRelations,
	artifactTypeIcon,
	artifactRelation,
	artifactRelationSide,
} from '@osee/artifact-with-relations/types';
import { RelationDeleteDialogComponent } from '../relation-delete-dialog/relation-delete-dialog.component';

@Component({
	selector: 'osee-relations-editor-panel',
	imports: [
		NgClass,
		AsyncPipe,
		MatIcon,
		CdkDropList,
		MatList,
		ExpandIconComponent,
	],
	templateUrl: './relations-editor-panel.component.html',
})
export class RelationsEditorPanelComponent {
	private artExpHttpService = inject(ArtifactExplorerHttpService);
	private tabService = inject(ArtifactExplorerTabService);
	private currentTxService = inject(CurrentTransactionService);
	private uiService = inject(UiService);
	private dialog = inject(MatDialog);
	private artifactIconService = inject(ArtifactIconService);

	artifactId = input.required<`${number}`>();
	branchId = input.required<string>();
	viewId = input.required<string>();
	editable = input.required<boolean>();

	private artifactId$ = toObservable(this.artifactId);
	private branchId$ = toObservable(this.branchId);
	private viewId$ = toObservable(this.viewId);

	private _hierarchyEditable = computed(() => this.editable());

	artWithRelation$ = combineLatest([
		this.branchId$,
		this.viewId$,
		this.artifactId$,
	]).pipe(
		filter(
			([branch, _view, artifact]) =>
				branch !== '-1' &&
				branch !== '0' &&
				branch !== '' &&
				artifact !== '-1'
		),
		switchMap(([branch, view, artifact]) =>
			this.artExpHttpService
				.getartifactWithRelations(branch, artifact, view, true)
				.pipe(
					repeat({
						delay: () =>
							this.uiService.updateArtifact.pipe(
								filter((id) => id === artifact)
							),
					})
				)
		),
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifactIconService.getIconClass(icon) +
			' ' +
			this.artifactIconService.getIconVariantClass(icon)
		);
	}

	toggleExpand(identifier: string) {
		const newArray = this.dropdownsOpen.value;
		const index = newArray.indexOf(identifier);
		if (index !== -1) {
			newArray.splice(index, 1);
			this.dropdownsOpen.next(newArray);
		} else {
			this.dropdownsOpen.value.push(identifier);
		}
	}

	addRelationOnItemDropped(
		event: CdkDragDrop<artifactWithRelations[]>,
		relation: artifactRelation,
		side: artifactRelationSide
	): void {
		const droppedArt: artifactWithRelations = event.item.data;
		if (this.editable() && this._hierarchyEditable()) {
			this.currentTxService
				.addRelationAndMutate('Adding relation in artifact explorer', {
					typeId: relation.relationTypeToken.id as RELATIONTYPEID,
					aArtId: side.isSideA ? droppedArt.id : this.artifactId(),
					bArtId: side.isSideA ? this.artifactId() : droppedArt.id,
					afterArtifact: 'end',
				})
				.pipe(
					take(1),
					tap(() => {
						this.uiService.updatedArtifact = this.artifactId();
					})
				)
				.subscribe();
		}
	}

	deleteRelation(
		otherArt: artifactWithRelations,
		relation: artifactRelation,
		side: artifactRelationSide
	) {
		this.artWithRelation$
			.pipe(
				take(1),
				switchMap((art) =>
					this.dialog
						.open(RelationDeleteDialogComponent, {
							data: {
								sideAName: otherArt.name,
								sideBName: art.name,
							},
						})
						.afterClosed()
						.pipe(
							take(1),
							switchMap((dialogResult: string) =>
								iif(
									() => dialogResult === 'ok',
									this.currentTxService
										.deleteRelationAndMutate(
											'Deleting relation in artifact explorer',
											{
												typeId: relation
													.relationTypeToken
													.id as RELATIONTYPEID,
												aArtId: side.isSideA
													? otherArt.id
													: art.id,
												bArtId: side.isSideA
													? art.id
													: otherArt.id,
											}
										)
										.pipe(
											take(1),
											tap(() => {
												this.uiService.updatedArtifact =
													this.artifactId();
											})
										),
									of()
								)
							)
						)
				)
			)
			.subscribe();
	}

	// track the state of the UI as relation dropdowns expand and collapse
	dropdownsOpen = new BehaviorSubject<string[]>([]);

	addTab(artifact: artifactWithRelations) {
		this.tabService.addArtifactTabOnBranch(
			{
				...artifact,
				editable: this.editable(),
			},
			this.branchId(),
			this.viewId()
		);
	}
}

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
import { Component, Input, computed, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	map,
	of,
	switchMap,
	take,
} from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { CreateChildArtifactDialogComponent } from '../create-child-artifact-dialog/create-child-artifact-dialog.component';
import { MatMenuModule } from '@angular/material/menu';
import {
	artifact,
	artifactContextMenuOption,
	artifactTypeIcon,
} from '../../../types/artifact-explorer.data';
import { UiService } from '@osee/shared/services';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { TransactionService } from '@osee/shared/transactions';
import { RELATIONTYPEIDENUM } from '@osee/shared/types/constants';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatIconModule } from '@angular/material/icon';
import { DeleteArtifactDialogComponent } from '../delete-artifact-dialog/delete-artifact-dialog.component';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { DEFUALT_ARTIFACT_CONTEXT_MENU_OPTIONS } from '../../../types/default-artifact-context-menu-options';
import { ArtifactIconService } from '../../../services/artifact-icon.service';
import { attribute } from '@osee/shared/types';

@Component({
	selector: 'osee-artifact-options-context-menu',
	standalone: true,
	imports: [CommonModule, MatButtonModule, MatMenuModule, MatIconModule],
	templateUrl: './artifact-options-context-menu.component.html',
})
export class ArtifactOptionsContextMenuComponent {
	artifactId = input.required<`${number}`>();
	parentArtifactId = input.required<`${number}`>();
	siblingArtifactId = input<`${number}`>('0');

	constructor(
		public dialog: MatDialog,
		private transactionService: TransactionService,
		private uiService: UiService,
		private pathsService: ArtifactHierarchyPathService,
		private artExpHttpService: ArtifactExplorerHttpService,
		private artifacticonService: ArtifactIconService
	) {}

	branchId$ = this.uiService.id;

	private _branchType = toSignal(this.uiService.type, {
		initialValue: 'baseline',
	});
	protected _branchEditable = computed(
		() => this._branchType() === 'working'
	);

	defaultOption$ = of(DEFUALT_ARTIFACT_CONTEXT_MENU_OPTIONS);

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifacticonService.getIconClass(icon) +
			' ' +
			this.artifacticonService.getIconVariantClass(icon)
		);
	}

	selectOption(option: artifactContextMenuOption) {
		if (this._branchEditable()) {
			switch (option.name) {
				case 'Create Child Artifact':
					this.createChildArtifact(option);
					break;
				case 'Delete Artifact':
					this.deleteArtifact(option);
					break;
			}
		}
	}

	private createChildArtifact(option: artifactContextMenuOption) {
		this.branchId$
			.pipe(
				take(1),
				switchMap((branchId) =>
					this.dialog
						.open(CreateChildArtifactDialogComponent, {
							data: {
								name: '',
								artifactTypeId: '0',
								parentArtifactId: this.artifactId(),
								attributes: [],
								option: option,
							},
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
												attributes: result?.attributes
													.filter(
														(attr: attribute) =>
															attr.value != null
													)
													.map((attr: attribute) => ({
														typeId: attr.typeId,
														value: attr.value,
													})),
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
												txResult.results.success == true
											) {
												var firstId =
													txResult.results.ids?.at(0);
												if (firstId !== undefined)
													this.pathsService.updatePaths(
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
	}

	private deleteArtifact(option: artifactContextMenuOption) {
		this.branchId$
			.pipe(
				take(1),
				switchMap((branchId) =>
					this.artExpHttpService
						.getArtifactForTab(branchId, this.artifactId())
						.pipe(
							switchMap((artifact) =>
								this.dialog
									.open(DeleteArtifactDialogComponent, {
										data: {
											artifact: artifact,
											option: option,
										},
										minWidth: '60%',
									})
									.afterClosed()
									.pipe(
										take(1),
										filter((result) => result === 'submit'),
										switchMap(() =>
											this.transactionService
												.performMutation({
													branch: branchId,
													txComment:
														'Deleting artifact: ' +
														this.artifactId(),
													deleteArtifacts: [
														this.artifactId(),
													],
												})
												.pipe(
													take(1),
													filter(
														(deleteArtResult) =>
															deleteArtResult
																.results
																.success ===
															true
													),
													map(() => {
														if (
															this.siblingArtifactId() !==
															'0'
														) {
															this.pathsService.updatePaths(
																this.siblingArtifactId()
															);
														} else {
															this.pathsService.updatePaths(
																this.parentArtifactId()
															);
														}
													})
												)
										)
									)
							)
						)
				)
			)
			.subscribe();
	}
}

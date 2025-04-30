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
import { Component, computed, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenuItem } from '@angular/material/menu';
import { UiService } from '@osee/shared/services';
import { TransactionService } from '@osee/transactions/services';
import { attribute } from '@osee/shared/types';
import { RELATIONTYPEIDENUM } from '@osee/shared/types/constants';
import { combineLatest, filter, map, switchMap, take, tap } from 'rxjs';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { ArtifactIconService } from '../../../services/artifact-icon.service';
import { CreateChildArtifactDialogComponent } from './dialogs/create-child-artifact-dialog/create-child-artifact-dialog.component';
import { DeleteArtifactDialogComponent } from './dialogs/delete-artifact-dialog/delete-artifact-dialog.component';
import {
	artifactTypeIcon,
	operationType,
} from '@osee/artifact-with-relations/types';
import { PublishArtifactDialogComponent } from './dialogs/publish-artifact-dialog/publish-artifact-dialog.component';

@Component({
	selector: 'osee-artifact-operations-context-menu',
	imports: [MatMenuItem, MatIcon],
	templateUrl: './artifact-operations-context-menu.component.html',
})
export class ArtifactOperationsContextMenuComponent {
	dialog = inject(MatDialog);
	private uiService = inject(UiService);
	private pathsService = inject(ArtifactHierarchyPathService);
	private artExpHttpService = inject(ArtifactExplorerHttpService);
	private artifacticonService = inject(ArtifactIconService);

	artifactId = input.required<string>();
	parentArtifactId = input.required<`${number}`>();
	siblingArtifactId = input<`${number}`>('0');
	operationTypes = input<operationType[]>([]);

	private transactionService = inject(TransactionService);

	branchId$ = this.uiService.id;
	viewId$ = this.uiService.viewId;

	private _branchType = toSignal(this.uiService.type, {
		initialValue: 'baseline',
	});
	protected _branchEditable = computed(
		() => this._branchType() === 'working'
	);

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifacticonService.getIconClass(icon) +
			' ' +
			this.artifacticonService.getIconVariantClass(icon)
		);
	}

	selectOption(operationType: operationType) {
		if (this._branchEditable()) {
			switch (operationType.id) {
				case '6996644113326307731':
					this.createChildArtifact(operationType);
					break;
				case '9075821926072512558':
					this.deleteArtifact(operationType);
					break;
				case '8972650019222132280':
					this.publishArtifact(operationType);
					break;
			}
		}
	}

	private publishArtifact(operationType: operationType) {
		combineLatest([this.branchId$, this.viewId$])
			.pipe(
				take(1),
				switchMap(([branchId, viewId]) =>
					this.dialog
						.open(PublishArtifactDialogComponent, {
							data: {
								templateId: '',
								operationType: operationType,
							},
							minWidth: '60%',
						})
						.afterClosed()
						.pipe(
							filter(
								(data) =>
									data &&
									data?.templateId !== '0' &&
									data?.templateId !== undefined
							),
							switchMap((data) => {
								const requestData = {
									artifactIds: [this.artifactId()],
									publishingRendererOptions: {
										Branch: {
											id: branchId,
											viewId: viewId,
										},
										PublishingFormat: {
											// formatIndicator overwritten by api
											formatIndicator: 'markdown',
										},
									},
									publishingTemplateRequest: {
										byOptions: false,
										// formatIndicator overwritten by api
										formatIndicator: 'markdown',
										templateId: data.templateId,
									},
								};

								let publishObservable;

								switch (data.extension) {
									case 'html':
										publishObservable =
											this.artExpHttpService.publishMarkdownAsHtml(
												{
													publishMarkdownAsHtmlRequestData:
														requestData,
												}
											);
										break;
									case 'md':
										publishObservable =
											this.artExpHttpService.publishMarkdown(
												{
													publishingRequestData:
														requestData,
												}
											);
										break;
									default:
										throw new Error('Invalid output type');
								}

								return publishObservable.pipe(
									take(1),
									map((response) => {
										// Extract file name from Content-Disposition header
										const contentDisposition =
											response.headers.get(
												'Content-Disposition'
											);
										// Default name
										let fileName = 'markdownPublish.zip';

										// Look for filename after "filename=" and before any trailing ";" in the Content-Disposition header
										if (contentDisposition) {
											const matches =
												/filename="?([^;"]+)"?/.exec(
													contentDisposition
												);
											if (matches != null && matches[1]) {
												fileName = matches[1];
											}
										}

										// Create a blob URL and trigger the download
										const blob = response.body as Blob;
										const link =
											document.createElement('a');
										link.href =
											window.URL.createObjectURL(blob);
										link.download = fileName;
										link.click();
										window.URL.revokeObjectURL(link.href);
									}),
									tap(() => (this.uiService.updated = true))
								);
							})
						)
				)
			)
			.subscribe();
	}

	private createChildArtifact(operationType: operationType) {
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
								operationType: operationType,
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
												const firstId =
													txResult.results.ids?.at(0);
												if (firstId !== undefined)
													this.pathsService.updatePaths(
														firstId
													);
											}
										}),
										tap(
											() =>
												(this.uiService.updated = true)
										)
									)
							)
						)
				)
			)
			.subscribe();
	}

	private deleteArtifact(operationType: operationType) {
		combineLatest([this.branchId$, this.uiService.viewId])
			.pipe(
				take(1),
				switchMap(([branchId, viewId]) =>
					this.artExpHttpService
						.getartifactWithRelations(
							branchId,
							this.artifactId(),
							viewId,
							false
						)
						.pipe(
							switchMap((artifact) =>
								this.dialog
									.open(DeleteArtifactDialogComponent, {
										data: {
											artifact: artifact,
											operationType: operationType,
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
													}),
													tap(
														() =>
															(this.uiService.updated =
																true)
													)
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

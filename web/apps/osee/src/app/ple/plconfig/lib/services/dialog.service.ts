/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { MatDialog } from '@angular/material/dialog';
import { combineLatest, iif, of, OperatorFunction, from } from 'rxjs';
import {
	take,
	switchMap,
	filter,
	mergeMap,
	reduce,
	map,
	tap,
} from 'rxjs/operators';
import { ConfigGroupDialogComponent } from '../dialogs/config-group-dialog/config-group-dialog.component';
import { EditConfigurationDialogComponent } from '../dialogs/edit-config-dialog/edit-config-dialog.component';
import { EditFeatureDialogComponent } from '../dialogs/edit-feature-dialog/edit-feature-dialog.component';
import { extendedFeature } from '../types/features/base';
import {
	PlConfigApplicUIBranchMapping,
	view,
} from '../types/pl-config-applicui-branch-mapping';
import { CfgGroupDialog } from '../types/pl-config-cfggroups';
import { configGroup } from '../types/pl-config-configurations';
import { modifyFeature, PLEditFeatureData } from '../types/pl-config-features';
import { PLEditConfigData } from '../types/pl-edit-config-data';
import { PlConfigCurrentBranchService } from './pl-config-current-branch.service';
import { CurrentBranchInfoService } from '@osee/shared/services';

@Injectable({
	providedIn: 'any',
})
export class DialogService {
	private _currentBranchService = inject(PlConfigCurrentBranchService);
	private _dialog = inject(MatDialog);
	private _branchInfoService = inject(CurrentBranchInfoService);

	/**
	 *
	 * @param configId MUST BE A VALID CoreArtifactTypes.BranchView
	 * @param editable
	 */
	openEditConfigDialog(configId: string, editable: boolean) {
		const cfgGroups =
			this._currentBranchService.getCfgGroupsForView(configId);
		const view = this._currentBranchService.getView(configId);
		const dialogData = combineLatest([cfgGroups, view]).pipe(
			take(1),
			map(
				([groups, view]) =>
					new PLEditConfigData(
						view,
						undefined,
						view.productApplicabilities,
						editable,
						groups
					)
			)
		);
		return dialogData.pipe(
			switchMap((data) => {
				const dialogRef = this._dialog.open<
					EditConfigurationDialogComponent,
					PLEditConfigData,
					PLEditConfigData
				>(EditConfigurationDialogComponent, {
					data: data,
					minWidth: '60%',
				});
				return dialogRef.afterClosed();
			}),
			take(1),
			filter(
				(response): response is PLEditConfigData =>
					response !== undefined
			),
			switchMap((dialogResponse) =>
				iif(
					() => dialogResponse && dialogResponse.editable,
					this._currentBranchService
						.editConfigurationDetails({
							...dialogResponse.currentConfig,
							copyFrom:
								(dialogResponse.copyFrom.id &&
									dialogResponse.copyFrom.id) ||
								'',
							configurationGroup: dialogResponse.group.map(
								(a) => a.id
							),
							productApplicabilities:
								dialogResponse.productApplicabilities || [],
						})
						.pipe(take(1)),
					of() // @todo replace with a false response
				)
			)
		);
	}

	openEditConfigGroupDialog(groupId: string, editable: boolean) {
		const group = this._currentBranchService.getCfgGroupDetail(groupId);
		const views = group.pipe(
			take(1),
			switchMap((group) =>
				this._currentBranchService.getViewsByIds(group.configurations)
			),
			take(1)
		);
		const data = combineLatest([group, views]).pipe(
			map(([_group, viewList]) => {
				return {
					editable: editable,
					configGroup: {
						id: _group.id,
						name: _group.name,
						description: _group.description,
						configurations: _group.configurations,
						views: viewList,
					},
				};
			})
		);
		const dialog$ = data.pipe(
			switchMap((d) => {
				const dialogRef = this._dialog.open(
					ConfigGroupDialogComponent,
					{
						data: d,
						minWidth: '60%',
					}
				);
				return dialogRef.afterClosed();
			})
		);
		return dialog$.pipe(
			take(1),
			filter(
				(dialogResult) => dialogResult !== undefined
			) as OperatorFunction<CfgGroupDialog | undefined, CfgGroupDialog>,
			switchMap((resp) =>
				iif(
					() => resp.editable,
					of(resp).pipe(
						switchMap((r) =>
							this._currentBranchService.updateConfigurationGroup(
								{
									id: r.configGroup.id,
									name: r.configGroup.name,
									description: r.configGroup.description,
									configurations: r.configGroup.views.map(
										(x) => x.id
									),
								}
							)
						)
					),
					of()
				)
			),
			take(1)
		);
	}
	displayFeatureDialog(featureId: string) {
		const branch = this._branchInfoService.currentBranch.pipe(take(1));
		const editable = branch.pipe(
			take(1),
			map((br) => br.branchType === '0')
		);
		const feature = this._currentBranchService
			.getFeatureById(featureId)
			.pipe(
				take(1),
				map((x) => new modifyFeature(x, '', ''))
			);
		const dialogData = combineLatest([branch, editable, feature]).pipe(
			take(1),
			map(([br, edit, ft]) => {
				return { currentBranch: br.id, editable: edit, feature: ft };
			})
		);
		const dialogRef = dialogData.pipe(
			take(1),
			switchMap((d) => {
				const ref = this._dialog.open(EditFeatureDialogComponent, {
					data: d,
					minWidth: '60%',
				});
				return ref.afterClosed();
			}),
			take(1),
			filter((val): val is PLEditFeatureData => val !== undefined)
		);
		return dialogRef.pipe(
			switchMap((dialogResponse) =>
				iif(
					() => dialogResponse && dialogResponse.editable,
					this._currentBranchService
						.modifyFeature(dialogResponse.feature)
						.pipe(take(1)),
					of()
				)
			)
		);
	}
}

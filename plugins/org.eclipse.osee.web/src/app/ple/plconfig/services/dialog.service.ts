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
import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { combineLatest, iif, of, OperatorFunction, from } from 'rxjs';
import { take, switchMap, filter, mergeMap, reduce } from 'rxjs/operators';
import { ConfigGroupDialogComponent } from '../components/config-group-dialog/config-group-dialog.component';
import { EditConfigurationDialogComponent } from '../components/edit-config-dialog/edit-config-dialog.component';
import { EditFeatureDialogComponent } from '../components/edit-feature-dialog/edit-feature-dialog.component';
import { extendedFeature } from '../types/features/base';
import { PlConfigApplicUIBranchMapping,view } from '../types/pl-config-applicui-branch-mapping';
import { CfgGroupDialog } from '../types/pl-config-cfggroups';
import { configGroup } from '../types/pl-config-configurations';
import { modifyFeature, PLEditFeatureData } from '../types/pl-config-features';
import { PLEditConfigData } from '../types/pl-edit-config-data';
import { PlConfigCurrentBranchService } from './pl-config-current-branch.service';

@Injectable({
  providedIn: 'any'
})
export class DialogService {

  constructor (private currentBranchService: PlConfigCurrentBranchService, public dialog: MatDialog) { }
  
  openConfigMenu(header: string, editable: string) {
    let isEditable = (editable === 'true');
    return combineLatest([this.currentBranchService.isACfgGroup(header), this.currentBranchService.findViewByName(header), this.currentBranchService.branchApplicability]).pipe(
      take(1),
      switchMap(([isCfgGroup, view, applicability]) => iif(
        () => isCfgGroup===false && view !== undefined,
        of<[PlConfigApplicUIBranchMapping, view | undefined]>([applicability, view]).pipe(
          take(1),
          filter(([app, view]) => (view as view) !== undefined) as OperatorFunction<[PlConfigApplicUIBranchMapping, view | undefined], [PlConfigApplicUIBranchMapping, view]>,
          switchMap(([app,view]) => this.dialog.open<EditConfigurationDialogComponent, PLEditConfigData, PLEditConfigData>(EditConfigurationDialogComponent, {
            data: new PLEditConfigData(app.branch.id, view, undefined, view.productApplicabilities, isEditable,app.groups.filter((a)=>a.configurations.includes(view.id))),
            minWidth: '60%'
          }).afterClosed()),
          filter((response): response is PLEditConfigData => response !== undefined),
          take(1),
          switchMap((dialogResponse) => iif(() => dialogResponse && dialogResponse.editable, this.currentBranchService.editConfigurationDetails({
            ...dialogResponse.currentConfig,
            copyFrom: dialogResponse.copyFrom.id && dialogResponse.copyFrom.id || '',
            configurationGroup: dialogResponse.group.map(a=>a.id),
            productApplicabilities: dialogResponse.productApplicabilities || []
          }).pipe(
            take(1)
          ),
          of() // @todo replace with a false response
          ))
        ),
        this.currentBranchService.findGroup(header).pipe(
          filter((group) => group !== undefined) as OperatorFunction<configGroup | undefined, configGroup>,
          take(1),
          switchMap((group) => of(group).pipe(
            take(1),
            mergeMap((findViews) => from(findViews.configurations).pipe(
              switchMap((config)=>this.currentBranchService.findViewById(config))
            )),
            filter((view) => view !== undefined) as OperatorFunction<view | undefined, view>,
            reduce((acc, curr) => { acc.views.push(curr); return {id:group.id,name:group.name,configurations:group.configurations,views:acc.views} }, {id:group.id,name:group.name,configurations:group.configurations,views:[]} as { id: string, name: string, configurations: string[], views: view[] }),
          )),
          switchMap((dialogPrep) => of({ editable: (editable === 'true'), configGroup: dialogPrep }).pipe(
            switchMap((group:CfgGroupDialog)=>this.dialog.open<ConfigGroupDialogComponent,CfgGroupDialog,CfgGroupDialog>(ConfigGroupDialogComponent,{
              data: group,
              minWidth: '60%'
            }).afterClosed().pipe(
              take(1),
              filter((dialogResult) => dialogResult !== undefined) as OperatorFunction<CfgGroupDialog | undefined, CfgGroupDialog>,
              switchMap((dialogResult) => iif(() => dialogResult.editable, from(dialogResult.configGroup.views).pipe(
                reduce((acc, curr) => [...acc, curr.id], [] as string[]),
                switchMap((cfgArray) => of(cfgArray).pipe(
                  take(1),
                  switchMap((array) => this.currentBranchService.updateConfigurationGroup({
                    id: dialogResult.configGroup.id,
                    name: dialogResult.configGroup.name,
                    configurations: cfgArray
                  }
                  ).pipe(take(1)))
                ))
              ),
              of() // @todo replace with a false response
              ))
            )
          ))
          )
        )))
    )
  }
  displayFeatureMenu(feature: extendedFeature) {
    const { configurations, ...selectedFeature } = feature;
    return this.currentBranchService.branchApplicability.pipe(
      take(1),
      switchMap((response) => of({ id: response.branch.id, editable: response.editable }).pipe(
        switchMap((responseData) => of({ currentBranch: responseData.id, editable: responseData.editable, feature: new modifyFeature(selectedFeature, "", "") }).pipe(
          switchMap((editFeatureData) => this.dialog.open<EditFeatureDialogComponent, any, PLEditFeatureData>(EditFeatureDialogComponent, {
            data: editFeatureData,
            minWidth: '60%'
          }).afterClosed())
        ))
      )),
      filter((val): val is PLEditFeatureData => val !== undefined),
      take(1),
      switchMap((dialogResponse) => iif(() => dialogResponse && dialogResponse.editable, this.currentBranchService.modifyFeature(dialogResponse.feature).pipe(
        take(1)
      ), of()))
    );
  }
}

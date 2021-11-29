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
import { AfterViewInit, Component, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSelectChange } from '@angular/material/select';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { combineLatest, from, iif, of, OperatorFunction, throwError } from 'rxjs';
import { distinct, filter, map, mergeMap, reduce, scan, share, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { extendedFeature, trackableFeature } from '../../types/features/base';
import { PlConfigApplicUIBranchMapping, view } from '../../types/pl-config-applicui-branch-mapping';
import { CfgGroupDialog } from '../../types/pl-config-cfggroups';
import { configGroup } from '../../types/pl-config-configurations';
import { modifyFeature, PLEditFeatureData } from '../../types/pl-config-features';
import { PLEditConfigData } from '../../types/pl-edit-config-data';
import { ConfigGroupDialogComponent } from '../config-group-dialog/config-group-dialog.component';
import { EditConfigurationDialogComponent } from '../edit-config-dialog/edit-config-dialog.component';
import { EditFeatureDialogComponent } from '../edit-feature-dialog/edit-feature-dialog.component';

@Component({
  selector: 'plconfig-applicability-table',
  templateUrl: './applicability-table.component.html',
  styleUrls: ['./applicability-table.component.sass']
})
export class ApplicabilityTableComponent implements OnInit, AfterViewInit, OnChanges {
  branchApplicability = this.currentBranchService.branchApplicability.pipe(share(),shareReplay({bufferSize:1,refCount:true}));
  dataSource = this.branchApplicability.pipe(
    tap((value) => {
      this.uiStateService.editableValue = value.editable;
      this.uiStateService.groupsString = value.groups.map(a => a.id);
    }),
    switchMap((applicability)=>of(new MatTableDataSource(applicability.features)))
  )

  topLevelHeaders = this.currentBranchService.topLevelHeaders;
  secondaryHeaders = this.currentBranchService.secondaryHeaders;
  secondaryHeaderLength = this.currentBranchService.secondaryHeaderLength;
  headers = this.currentBranchService.headers;
  errors = this.uiStateService.errors;
  viewCount = this.currentBranchService.viewCount;
  groupCount = this.currentBranchService.groupCount;
  groupList = this.currentBranchService.groupList;
  _editable = this.uiStateService.editable;;
  @ViewChild(MatSort, {static:false}) sort: MatSort;
  constructor(private uiStateService: PlConfigUIStateService, private currentBranchService: PlConfigCurrentBranchService, public dialog: MatDialog) {
    this.sort = new MatSort();
   }
  ngOnChanges(changes: SimpleChanges): void {
    this.uiStateService.editableValue = false;
  }

  ngOnInit(): void {
  }
  ngAfterViewInit() {
    this.dataSource.pipe(
      take(1),
      map((val) => val.sort = this.sort)
    ).subscribe();
  }
  @ViewChild(MatPaginator, {static: false})
  set paginator(value: MatPaginator) {
    this.dataSource.pipe(
      map((val) => { if (val) { val.paginator = value } })
    ).subscribe();
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.pipe(
      take(1),
      map((val) => {
        val.filter = filterValue.trim().toLowerCase(); if (val.paginator) {
          val.paginator.firstPage();
        }
      })
    ).subscribe();
  }
  log(value: any) {
    console.log(value);
  }
  valueTracker(index: any, item: any) {
    return index;
  }
  modifyProduct(configuration: string, feature: trackableFeature,event: MatSelectChange) {
    this.requestConfigurationChange(configuration, feature, event);
  }
  modifyConfiguration(configuration: string, feature: trackableFeature, event: MatSelectChange) {
    this.requestConfigurationChange(configuration, feature, event);
  }
  requestConfigurationChange(configuration: string, feature: trackableFeature, event: HTMLInputElement | MatSelectChange) {
    combineLatest([this.branchApplicability.pipe(
      take(1),
      switchMap((app) => of(app.features).pipe(
        map((features) => features.find((value) => value.id === feature.id)),
        filter((feature) => feature !== undefined) as OperatorFunction<extendedFeature | undefined, extendedFeature>,
        map(featureValue => featureValue.values)
      )),
    ), this.branchApplicability.pipe(
      take(1),
      switchMap((app) => of(app.views).pipe(
        map((views) => views.find((value) => value.name.toLowerCase() === configuration.toLowerCase())),
        filter((feature) => feature !== undefined) as OperatorFunction<view | undefined, view>,
        map((view) => view.id)
      )),
    ), this.groupList]).pipe(
      take(1),
      switchMap(([latestFeatures, latestViews, groupList]) => of(latestFeatures, latestViews, groupList).pipe(
        switchMap((latest) => iif(() => feature.multiValued, of(event.value as string[]), of([(event.value as string)])).pipe(
          switchMap((values) => of(values).pipe(
            mergeMap((values) => from(values).pipe(
              switchMap((value) => iif(() => latestFeatures.findIndex((v) => v.toLowerCase() === value.toLowerCase()) === -1, throwError(() => { this.uiStateService.error = "Error: " + value + " is not a valid value." }), of(value)))
            )),
          )),
        )),
        distinct(),
        reduce((acc, curr) => [...acc, curr], [] as string[]),
        switchMap((v) => this.currentBranchService.modifyConfiguration(latestViews, feature.name + " = " + v, groupList).pipe(take(1)))
      ))
    ).subscribe();  
  }
  isSticky(header:string) {
    return header === 'feature';
  }
  isCorrectConfiguration(configName: { name: string, value: string }, column: string) {
    return configName.name === column;
  }
  isACfgGroup(name: string) {
    return this.groupList.pipe(
      switchMap((response) => of(response).pipe(
        mergeMap((responseGrouping) => from(responseGrouping).pipe(
          switchMap((grouping)=>iif(()=>grouping.name===name,of(true),of(false)))
        ))
      )),
      scan((acc, curr) => { if (curr !== false) { acc = curr; } return acc; }, false as boolean),
    )
  }
  displayFeatureMenu(feature: extendedFeature) {
    const { configurations, ...selectedFeature } = feature;
    const dialogRef = this.branchApplicability.pipe(
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
      )))
    );
    dialogRef.subscribe();
  }

  openConfigMenu(header: string, editable: string) {
    let isEditable = (editable === 'true');
    const dialogRef= combineLatest([this.isACfgGroup(header), this.currentBranchService.findViewByName(header), this.branchApplicability]).pipe(
      take(1),
      switchMap(([isCfgGroup, view, applicability]) => iif(
        () => isCfgGroup===false && view !== undefined,
        of<[PlConfigApplicUIBranchMapping, view | undefined]>([applicability, view]).pipe(
          take(1),
          filter(([app, view]) => (view as view) !== undefined) as OperatorFunction<[PlConfigApplicUIBranchMapping, view | undefined], [PlConfigApplicUIBranchMapping, view]>,
          switchMap(([app,view]) => this.dialog.open<EditConfigurationDialogComponent, PLEditConfigData, PLEditConfigData>(EditConfigurationDialogComponent, {
            data: new PLEditConfigData(app.branch.id, view, undefined, view.productApplicabilities, isEditable),
            minWidth: '60%'
          }).afterClosed()),
          filter((response): response is PLEditConfigData => response !== undefined),
          take(1),
          switchMap((dialogResponse) => iif(() => dialogResponse && dialogResponse.editable, this.currentBranchService.editConfigurationDetails({
            ...dialogResponse.currentConfig,
            copyFrom: dialogResponse.copyFrom.id && dialogResponse.copyFrom.id || '',
            configurationGroup: dialogResponse.group && dialogResponse.group.id || '',
            productApplicabilities: dialogResponse.productApplicabilities || []
          }).pipe(
            take(1)
          )))
        ),
        this.currentBranchService.findGroup(header).pipe(
          filter((group) => group !== undefined) as OperatorFunction<configGroup | undefined, configGroup>,
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
              )))
            )
          ))
          )
        )))
    )
    dialogRef.subscribe()
  }
}

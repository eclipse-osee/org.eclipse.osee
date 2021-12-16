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
import { MatMenuTrigger } from '@angular/material/menu';
import { MatPaginator } from '@angular/material/paginator';
import { MatSelectChange } from '@angular/material/select';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { combineLatest, from, iif, of, OperatorFunction, throwError } from 'rxjs';
import { distinct, filter, map, mergeMap, reduce, share, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { DialogService } from '../../services/dialog.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { extendedFeature, trackableFeature } from '../../types/features/base';
import {  view, viewWithChanges } from '../../types/pl-config-applicui-branch-mapping';
import { configGroup, configGroupWithChanges } from '../../types/pl-config-configurations';

@Component({
  selector: 'plconfig-applicability-table',
  templateUrl: './applicability-table.component.html',
  styleUrls: ['./applicability-table.component.sass']
})
export class ApplicabilityTableComponent implements OnInit, AfterViewInit, OnChanges {
  branchApplicability = this.currentBranchService.branchApplicability.pipe(share(), shareReplay({refCount:true,bufferSize:1}));
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
  _editable = this.uiStateService.editable;
  @ViewChild(MatSort, { static: false }) sort: MatSort;
  menuPosition = {
    x: '0',
    y:'0'
  }
  @ViewChild('featureMenuTrigger', { static: true })
  featureTrigger!: MatMenuTrigger;
  @ViewChild('configMenuTrigger', { static: true })
  configTrigger!: MatMenuTrigger;
  @ViewChild('configGroupMenuTrigger', { static: true })
  configGroupTrigger!: MatMenuTrigger;
  @ViewChild('valueMenuTrigger', { static: true })
  valueTrigger!: MatMenuTrigger;
  constructor(private uiStateService: PlConfigUIStateService, private currentBranchService: PlConfigCurrentBranchService, public dialog: MatDialog, private dialogService: DialogService) {
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
    ), 
    this.branchApplicability.pipe(
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
  isAddedCfg(configName: string) {
    return this.currentBranchService.findViewByName(configName).pipe(
      filter((val) => val !== undefined) as OperatorFunction<view | viewWithChanges | undefined, view | viewWithChanges>,
      take(1),
      filter((val)=>(val as viewWithChanges)?.changes!==undefined) as OperatorFunction<view | viewWithChanges, viewWithChanges>,
      map((val) => val.added)
    )
  }

  isDeletedCfg(configName: string) {
    return this.currentBranchService.findViewByName(configName).pipe(
      filter((val) => val !== undefined) as OperatorFunction<view | viewWithChanges | undefined, view | viewWithChanges>,
      take(1),
      filter((val)=>(val as viewWithChanges)?.changes!==undefined) as OperatorFunction<view | viewWithChanges, viewWithChanges>,
      map((val) => val.deleted)
    )
  }

  hasChangesCfg(configName: string) {
    return this.currentBranchService.findViewByName(configName).pipe(
      filter((val) => val !== undefined) as OperatorFunction<view | viewWithChanges | undefined, view | viewWithChanges>,
      take(1),
      map((val) => (val as viewWithChanges).changes!==undefined)
    )
  }

  isDeletedCfgGroup(configName: string) {
    return this.currentBranchService.findGroup(configName).pipe(
      filter((val) => val !== undefined) as OperatorFunction<configGroup | configGroupWithChanges | undefined, configGroup | configGroupWithChanges>,
      take(1),
      filter((val)=>(val as configGroupWithChanges)?.changes!==undefined) as OperatorFunction<configGroup | configGroupWithChanges, configGroupWithChanges>,
      map((val) => val.deleted)
    )
  }

  isAddedCfgGroup(configName: string) {
    return this.currentBranchService.findGroup(configName).pipe(
      filter((val) => val !== undefined) as OperatorFunction<configGroup | configGroupWithChanges | undefined, configGroup | configGroupWithChanges>,
      take(1),
      filter((val)=>(val as configGroupWithChanges)?.changes!==undefined) as OperatorFunction<configGroup | configGroupWithChanges, configGroupWithChanges>,
      map((val) => val.added)
    )
  }

  hasChangesCfgGroup(configName: string) {
    return this.currentBranchService.findGroup(configName).pipe(
      filter((val) => val !== undefined) as OperatorFunction<configGroup | configGroupWithChanges | undefined, configGroup | configGroupWithChanges>,
      take(1),
      map((val) => (val as configGroupWithChanges).changes!==undefined)
    )
  }
  isACfgGroup(name: string) {
    return this.currentBranchService.isACfgGroup(name);
  }
  displayFeatureMenu(feature: extendedFeature) {
    this.dialogService.displayFeatureMenu(feature).subscribe();
  }

  openConfigMenu(header: string, editable: string) {
    this.dialogService.openConfigMenu(header, editable).subscribe();
  }

  openContextMenu<T>(event: MouseEvent, type: 'FEATURE' | 'GROUP' | 'CONFIG' | 'VALUE', data: T) {
    event.preventDefault();
    this.menuPosition.x = event.clientX + 'px';
    this.menuPosition.y = event.clientY + 'px';
    switch (type) {
      case 'FEATURE':
        this.featureTrigger.menuData = {
          feature:data
        }
        this.configTrigger.closeMenu();
        this.configGroupTrigger.closeMenu();
        this.valueTrigger.closeMenu();
        this.featureTrigger.openMenu();
        break;
      case 'CONFIG':
        this.configTrigger.menuData = {
          config: this.currentBranchService.findViewByName(data as unknown as string).pipe(
            filter((val)=>val!==undefined) as OperatorFunction<view|viewWithChanges|undefined,view|viewWithChanges>
          )
        }
        this.configTrigger.openMenu();
        this.configGroupTrigger.closeMenu();
        this.valueTrigger.closeMenu();
        this.featureTrigger.closeMenu();
        break;
      case 'GROUP':
        this.configGroupTrigger.menuData = {
          group: this.currentBranchService.findGroup(data as unknown as string).pipe(
            filter((val)=>val!==undefined) as OperatorFunction<configGroup|configGroupWithChanges|undefined,configGroup|configGroupWithChanges>
          )
        }
        this.configGroupTrigger.openMenu();
        this.configTrigger.closeMenu();
        this.valueTrigger.closeMenu();
        this.featureTrigger.closeMenu();
        break;
      case 'VALUE':
        this.valueTrigger.menuData = {
          value: data
        }
        this.configGroupTrigger.closeMenu();
        this.configTrigger.closeMenu();
        this.valueTrigger.openMenu();
        this.featureTrigger.closeMenu();
        break;
    
      default:
        break;
    }
  }

}

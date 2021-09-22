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
import { BehaviorSubject, config, Subject } from 'rxjs';
import { share, take } from 'rxjs/operators';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigUIStateService } from '../../services/pl-config-uistate.service';
import { extendedFeature, trackableFeature } from '../../types/features/base';
import { view } from '../../types/pl-config-applicui-branch-mapping';
import { CfgGroupDialog } from '../../types/pl-config-cfggroups';
import { editConfiguration } from '../../types/pl-config-configurations';
import { modifyFeature, PLEditFeatureData } from '../../types/pl-config-features';
import { response } from '../../types/pl-config-responses';
import { PLEditConfigData } from '../../types/pl-edit-config-data';
import { GroupViewSorter } from '../../util/GroupViewSort';
import { ConfigGroupDialogComponent } from '../config-group-dialog/config-group-dialog.component';
import { EditConfigurationDialogComponent } from '../edit-config-dialog/edit-config-dialog.component';
import { EditFeatureDialogComponent } from '../edit-feature-dialog/edit-feature-dialog.component';

@Component({
  selector: 'plconfig-applicability-table',
  templateUrl: './applicability-table.component.html',
  styleUrls: ['./applicability-table.component.sass']
})
export class ApplicabilityTableComponent implements OnInit, AfterViewInit, OnChanges {
  branchApplicability = this.currentBranchService.branchApplicability.pipe(share());
  dataSource:MatTableDataSource<extendedFeature> = new MatTableDataSource<extendedFeature>();
  headers: Subject<string[]> = new Subject<string[]>();
  secondaryHeaders: Subject<string[]> = new Subject<string[]>();
  secondaryHeaderLength: Subject<number[]> = new BehaviorSubject<number[]>([]);
  topLevelHeaders: BehaviorSubject<string[]> = new BehaviorSubject<string[]>([' ','Configurations','Groups']);
  views: view[] = [];
  featureMapping: trackableFeature[] = [];
  errors: BehaviorSubject<string>;
  sorter: GroupViewSorter = new GroupViewSorter();
  viewCount: BehaviorSubject<number> = new BehaviorSubject<number>(0);
  groupCount: BehaviorSubject<number> = new BehaviorSubject<number>(0);
  _editable: BehaviorSubject<string>;
  private _id:string=""
  @ViewChild(MatSort, {static:false}) sort: MatSort;
  constructor(private uiStateService: PlConfigUIStateService, private currentBranchService: PlConfigCurrentBranchService, public dialog: MatDialog) {
    this.sort = new MatSort();
    this._editable = this.uiStateService.editable;
    this.errors = this.uiStateService.errors;
    this.headers.next([]);
    this.secondaryHeaders.next([]);
    this.headers.pipe(share());
    this.branchApplicability.subscribe((response) => {
      this._id = response.branch.id;
      this.sorter.reset();
      this.sorter.syncGroups(response.groups);
      this.sorter.syncViews(response.views);
      this.uiStateService.editableBool = response.editable;
      this.uiStateService.groupsString = response.groups.map(a => a.id);
      this.dataSource.data = [];
      this.dataSource.data = response.features;
      this.sorter.sort();
      this.groupCount.next(this.sorter.viewObj.groupCount);
      if (this.sorter.viewObj.getGroupHeaders().length > 1) {
        this.topLevelHeaders.next([' ', 'Configurations', 'Groups']);
      } else {
        this.topLevelHeaders.next([' ', 'Configurations']);
      }
      this.viewCount.next(this.sorter.viewObj.viewCount);
      this.secondaryHeaders.next(['  ', ...this.makeHeaderUnique(this.sorter.viewObj.getGroupHeaders())])
      this.secondaryHeaderLength.next([1,...this.sorter.viewObj.getCounts()])
      this.headers.next(['feature', ...this.sorter.getSortedArrayOfConfigurations()])
      this.views = response.views;
      this.featureMapping = [];
      Object.entries(response.features).forEach((element) => {
        this.featureMapping.push(element[1]);
      })
    })
   }
  ngOnChanges(changes: SimpleChanges): void {
    this.uiStateService.editableBool = false;
  }

  ngOnInit(): void {
  }
  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
  }
  @ViewChild(MatPaginator, {static: false})
  set paginator(value: MatPaginator) {
    if (this.dataSource){
      this.dataSource.paginator = value;
    }
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
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
    let selectedFeatureIndex = this.featureMapping.findIndex((value) =>
      value.id === feature.id
    );
    let selectedFeatureValues = this.featureMapping[selectedFeatureIndex].values;
    let featureIndex=this.views.findIndex((value) =>
      value.name.toLowerCase() === configuration.toLowerCase()
    );
    
    let featureId = this.views[featureIndex].id;
    
   let values : string[] = [];
   if (feature.multiValued) {
      values = event.value;
    } else {
      values.push(event.value);
    }
    let callModify = true;
    values.forEach((element: string) => {
        if (selectedFeatureValues.findIndex((value) => value.toLowerCase() === element.toLowerCase()) === -1) {

          callModify = false;
          this.uiStateService.error="Error: "+ element + " is not a valid value."
        }
        
      });
    if (callModify) {
      let body = feature.name + " = " + event.value;
      this.currentBranchService.modifyConfiguration(featureId, body,this.sorter.groupList).pipe(take(1)).subscribe((responses: response[]) => {
          });
    }
    
  }
  isSticky(header:string) {
    return header === 'feature';
  }
  isCorrectConfiguration(configName: { name: string, value: string }, column: string) {
    return configName.name === column;
  }
  isACfgGroup(name: string): boolean {
    return (this.sorter.getGroupFromName(name) !==undefined)||false;
  }
  makeHeaderUnique(names: string[]) {
    let newArray: string[] = [];
    for (let name of names) {
      newArray.push(name+" ")
    }
    return newArray
  }
  displayFeatureMenu(feature: extendedFeature) {
    const { configurations, ...selectedFeature } = feature;
    let dialogData = {};
    this._editable.subscribe((val) => {
      dialogData = {
        currentBranch: this._id,
        editable: val,
        feature: new modifyFeature(selectedFeature, "","")
      }
    })
    const dialogRef = this.dialog.open(EditFeatureDialogComponent, {
      data: dialogData,
      minWidth: '60%'
    })
    dialogRef.afterClosed().subscribe((result: PLEditFeatureData) => {
      if (result && result.editable) {
        this.currentBranchService.modifyFeature(result.feature).pipe(take(1)).subscribe((response: response) => {
        })
      }
    })
  }
  openConfigMenu(header: string, editable:string) {
    if (!this.isACfgGroup(header) && this.sorter.viewObj.findView(header).name!=='Not Found') {
      let view = this.sorter.viewObj.findView(header);
      let isEditable = (editable === 'true');
      let dialogData: PLEditConfigData = new PLEditConfigData(this._id, view, undefined, view.productApplicabilities, isEditable);
      const dialogRef = this.dialog.open(EditConfigurationDialogComponent, {
        data: dialogData,
        minWidth: '60%'
      })
      dialogRef.afterClosed().subscribe((result) => {
        if (result && result.editable) {
          let body: editConfiguration = {
            ...result.currentConfig,
            copyFrom: result.copyFrom.id && result.copyFrom.id || '',
            configurationGroup: result.group && result.group || '',
            productApplicabilities:result.productApplicabilities||[]
          };
          this.currentBranchService.editConfigurationDetails(body).pipe(take(1)).subscribe((response) => { 
          })
        }
      })
    } else {
      let dialogData: CfgGroupDialog = {
        editable: (editable === 'true'),
        configGroup:this.sorter.viewObj.findGroup(header)
      }
      const dialogRef = this.dialog.open(ConfigGroupDialogComponent, {
        data: dialogData,
        minWidth: '60%'
      })
      dialogRef.afterClosed().subscribe((result:CfgGroupDialog) => {
        if (result && result.editable) {
          let cfgArray:string[]=[]
          result.configGroup.views.forEach((el) => {
            cfgArray.push(el.id);
          })
          this.currentBranchService.updateConfigurationGroup({
            id: result.configGroup.id,
            name: result.configGroup.name,
            configurations: cfgArray
          }).pipe(take(1)).subscribe();
        }
      })
    }
  }
}

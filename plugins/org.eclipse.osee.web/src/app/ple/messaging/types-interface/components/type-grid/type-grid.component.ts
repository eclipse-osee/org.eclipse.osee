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
import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout';
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { BehaviorSubject, combineLatest, of, OperatorFunction } from 'rxjs';
import { take, switchMap, filter, tap } from 'rxjs/operators';
import { ColumnPreferencesDialogComponent } from '../../../shared/components/dialogs/column-preferences-dialog/column-preferences-dialog.component';
import { applic } from '../../../../../types/applicability/applic';
import { CurrentTypesService } from '../../services/current-types.service';
import { PlMessagingTypesUIService } from '../../services/pl-messaging-types-ui.service';
import { enumeration } from '../../../shared/types/enum';
import { logicalTypefieldValue, newPlatformTypeDialogReturnData } from '../../types/newTypeDialogDialogData';
import { PlatformType } from '../../../shared/types/platformType';
import { NewTypeDialogComponent } from '../../../shared/components/dialogs/new-type-dialog/new-type-dialog.component';

@Component({
  selector: 'ple-messaging-types-type-grid',
  templateUrl: './type-grid.component.html',
  styleUrls: ['./type-grid.component.sass']
})
export class TypeGridComponent implements OnInit, OnChanges {
  @Input() filterValue: string = "";
  columnCount= this.uiService.columnCount;
  gutterSize: string = "";
  filteredData = this.typesService.typeData;
  rowHeight: string = "";
  inEditMode = this.typesService.inEditMode;
  
  constructor(private breakpointObserver: BreakpointObserver, private typesService: CurrentTypesService, private uiService: PlMessagingTypesUIService,public dialog: MatDialog) {
    this.uiService.filterString = this.filterValue;
    const breakpoint =this.breakpointObserver.observe(
      [
        Breakpoints.XSmall,
        Breakpoints.Small,
        Breakpoints.Medium,
        Breakpoints.Large,
        Breakpoints.XLarge,
        Breakpoints.Web,
      ]
    )
    const combined = combineLatest([breakpoint, this.uiService.singleLineAdjustment]).subscribe((result) => {
      this.updateColumnsCount(result);
    })
  }
  
  ngOnChanges(changes: SimpleChanges): void {
    this.uiService.filterString = this.filterValue;
  }

  ngOnInit(): void {
  }

  /**
   * Adjusts the layout of the page based on CDK Layout Observer
   * @param state Array containing the state of the page (i.e. what breakpoints) and whether or not to adjust the layout due to being on a single line
   */
  updateColumnsCount(state: [BreakpointState,number]) {
    if (state[0].matches) {
      if (state[0].breakpoints[Breakpoints.XSmall]) {
        this.uiService.columnCountNumber = 1;
        this.gutterSize = "16";
      }
      if (state[0].breakpoints[Breakpoints.Small]) {
        this.uiService.columnCountNumber = 2;
        this.gutterSize = "16";
      }
      if (state[0].breakpoints[Breakpoints.Medium]) {
        this.rowHeight = 45 + state[1] + "%";
        //this.rowHeight="30%"
        this.uiService.columnCountNumber = 3;
        this.gutterSize = "24";
      }
      if (state[0].breakpoints[Breakpoints.Large] && !state[0].breakpoints[Breakpoints.Medium]) {
        this.rowHeight = 45+state[1]+"%"; //37
        this.uiService.columnCountNumber = 4;
        this.gutterSize = "24";
      }
      if (state[0].breakpoints[Breakpoints.XLarge] && !state[0].breakpoints[Breakpoints.Large]) {
        //this.rowHeight = "45%";
        this.rowHeight = 45+state[1]+"%";
        this.uiService.columnCountNumber = 5;
        this.gutterSize = "24";
      }
      if (state[0].breakpoints[Breakpoints.Web]) {
        this.rowHeight = 45+state[1]+"%";
        this.uiService.columnCountNumber = 5;
        this.gutterSize = "24";
      }
    }
  }

  /**
   * Sets the filter value so the API can update the data on the page.
   * @param event Event containing user input from the filter
   */
  applyFilter(event: Event) {
    this.filterValue = (event.target as HTMLInputElement).value.trim().toLowerCase();
    this.uiService.filterString = this.filterValue;
  }

  openNewTypeDialog() {
    this.dialog.open(NewTypeDialogComponent, {
      id:'new-type-dialog',
      minHeight: "70vh",
      minWidth: "80vw"
    }).afterClosed().pipe(
      filter(x => x !== undefined) as OperatorFunction<newPlatformTypeDialogReturnData | undefined, newPlatformTypeDialogReturnData>,
      switchMap(({ fields, createEnum, ...enumData }) => this.mapTo(fields, createEnum, enumData).pipe(
      ))
    ).subscribe();
  }

  mapTo(results: logicalTypefieldValue[], newEnum: boolean, enumData: { enumSetId:string,enumSetName: string, enumSetDescription: string, enumSetApplicability: applic, enums: enumeration[] }) {
    let resultingObj: Partial<PlatformType> = {};
    results.forEach((el) => {
      let name = el.name.charAt(0).toLowerCase() + el.name.slice(1);
      (resultingObj as any)[name]=el.value
    })
    return this.typesService.createType(resultingObj,newEnum,enumData);
  }

  getWidthString() {
    return 100 / this.columnCount.getValue()+"% -"+this.gutterSize+'px';
  }
  getMarginString() {
    return this.gutterSize+'px'
  }

}

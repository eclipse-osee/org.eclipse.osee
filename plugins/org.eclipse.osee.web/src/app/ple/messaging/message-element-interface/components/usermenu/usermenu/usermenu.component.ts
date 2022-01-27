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
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { combineLatest, from, iif, of } from 'rxjs';
import { filter, map, mergeMap, reduce, share, shareReplay, switchMap, take } from 'rxjs/operators';
import { ColumnPreferencesDialogComponent } from 'src/app/ple/messaging/shared/components/dialogs/column-preferences-dialog/column-preferences-dialog.component';
import { HeaderService } from 'src/app/ple/messaging/shared/services/ui/header.service';
import { CurrentStateService } from '../../../services/current-state.service';
import { element } from '../../../types/element';
import { structure } from '../../../types/structure';

@Component({
  selector: 'app-usermenu',
  templateUrl: './usermenu.component.html',
  styleUrls: ['./usermenu.component.sass']
})
export class UsermenuComponent implements OnInit {
  preferences = this.structureService.preferences;
  isEditing = this.preferences.pipe(
    map((x) => x.inEditMode),
    share(),
    shareReplay(1)
  )
  currentElementHeaders = combineLatest([this.headerService.AllElementHeaders,this.preferences]).pipe(
    switchMap(([allHeaders, response]) => of(response.columnPreferences).pipe(
      mergeMap((r) => from(r).pipe(
        filter((column) => allHeaders.includes(column.name) && column.enabled),
        map((header) => header.name),
        reduce((acc, curr) => [...acc, curr], [] as (Extract<keyof element,string>)[])
      ))
    )),
    mergeMap((headers) => iif(() => headers.length !== 0, of(headers).pipe(
      map((array) => { array.push(array.splice(array.indexOf('applicability'), 1)[0]); return array; })
    ), of(['name',
      'platformTypeName2',
      'interfaceElementAlterable',
      'description',
      'notes',]))),
    share(),
    shareReplay(1),
  );
  currentStructureHeaders = combineLatest([this.headerService.AllStructureHeaders,this.preferences]).pipe(
    switchMap(([structureHeaders,response]) => of(response.columnPreferences).pipe(
      mergeMap((r) => from(r).pipe(
        filter((column) => structureHeaders.includes(column.name as Extract<keyof structure,string>) && column.enabled),
        map((header) => header.name as Extract<keyof structure,string>),
        reduce((acc, curr) => [...acc, curr], [] as (Extract<keyof structure,string>)[])
      ))
    )),
    mergeMap((headers) => iif(() => headers.length !== 0, of(headers), of(['name',
      'description',
      'interfaceMinSimultaneity',
      'interfaceMaxSimultaneity',
      'interfaceTaskFileType',
      'interfaceStructureCategory',]))),
    switchMap((finalHeaders) => of([' ', ...finalHeaders])),
    share(),
    shareReplay(1)
  )
  settingsDialog = combineLatest([this.structureService.BranchId, this.isEditing, this.currentElementHeaders, this.currentStructureHeaders,this.headerService.AllElementHeaders,this.headerService.AllStructureHeaders]).pipe(
    take(1),
    switchMap(([branch, edit, elements, structures,allElementHeaders,allStructureHeaders]) => this.dialog.open(ColumnPreferencesDialogComponent, {
      data: {
        branchId: branch,
        allHeaders2: allElementHeaders,
        allowedHeaders2: elements,
        allHeaders1: allStructureHeaders,
        allowedHeaders1: structures,
        editable: edit,
        headers1Label: 'Structure Headers',
        headers2Label: 'Element Headers',
        headersTableActive: true,
      }
    }).afterClosed().pipe(
      take(1),
      switchMap((result) => this.structureService.updatePreferences(result))))
  )
  constructor(public dialog: MatDialog,
    private structureService: CurrentStateService,
    private headerService: HeaderService,
  ) { }

  ngOnInit(): void {
  }

  openSettingsDialog() {
    this.settingsDialog.subscribe();
  }
}

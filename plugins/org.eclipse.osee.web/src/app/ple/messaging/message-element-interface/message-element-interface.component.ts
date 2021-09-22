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
import {
  trigger,
  state,
  style,
  transition,
  animate,
} from '@angular/animations';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { ColumnPreferencesDialogComponent } from '../shared/components/dialogs/column-preferences-dialog/column-preferences-dialog.component';
import { CurrentStateService } from './services/current-state.service';
import { settingsDialogData } from '../shared/types/settingsdialog';
import { structure } from './types/structure';
import { combineLatest, from, iif, of } from 'rxjs';
import { AddStructureDialogComponent } from './components/add-structure-dialog/add-structure-dialog.component';
import { AddStructureDialog } from './types/AddStructureDialog';
import { filter, first, map, mergeMap, reduce, share, shareReplay, switchMap, take } from 'rxjs/operators';

@Component({
  selector: 'ple-messaging-message-element-interface',
  templateUrl: './message-element-interface.component.html',
  styleUrls: ['./message-element-interface.component.sass'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '60vh', overflowY: 'auto' })),
      transition(
        'expanded <=> collapsed',
        animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')
      ),
    ]),
    trigger('expandButton', [
      state('closed', style({ transform: 'rotate(0)' })),
      state('open', style({ transform: 'rotate(-180deg)' })),
      transition('open => closed', animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
      transition('closed => open', animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
    ])
  ],
})
export class MessageElementInterfaceComponent implements OnInit {
  messageData = this.structureService.structures;
  dataSource: MatTableDataSource<structure> =
    new MatTableDataSource<structure>();
  truncatedSections: string[] = [];
  editableStructureHeaders: string[] = [
    'name',
    'description',
    'interfaceMaxSimultaneity',
    'interfaceMinSimultaneity',
    'interfaceTaskFileType',
    'interfaceStructureCategory',
  'applicability'];
  
  allStructureHeaders: string[] = [
    'name',
    'description',
    'interfaceMaxSimultaneity',
    'interfaceMinSimultaneity',
    'interfaceTaskFileType',
    'interfaceStructureCategory',
    'numElements',
    'sizeInBytes',
    'bytesPerSecondMinimum',
    'bytesPerSecondMaximum',
    'GenerationIndicator',
    'applicability'
  ];

  allElementHeaders: string[] = [
    'name',
    'platformTypeName2',
    'beginWord',
    'endWord',
    'beginByte',
    'endByte',
    'interfaceElementAlterable',
    'description',
    'notes',
    'applicability'
  ];
  expandedElement: Array<any> = [];
  filter: string = '';
  searchTerms: string = '';
  breadCrumb: string = '';
  preferences = this.structureService.preferences;
  isEditing = this.preferences.pipe(
    map((x) => x.inEditMode),
    share(),
    shareReplay(1)
  )
  currentElementHeaders = this.preferences.pipe(
    switchMap((response) => of(response.columnPreferences).pipe(
      mergeMap((r) => from(r).pipe(
        filter((column) => this.allElementHeaders.includes(column.name) && column.enabled),
        map((header) => header.name),
        reduce((acc, curr) => [...acc, curr], [] as string[])
      ))
    )),
    mergeMap((headers)=>iif(()=>headers.length!==0,of(headers),of(['name',
    'platformTypeName2',
    'interfaceElementAlterable',
    'description',
    'notes',]))),
    share(),
    shareReplay(1)
  );
  currentStructureHeaders = this.preferences.pipe(
    switchMap((response) => of(response.columnPreferences).pipe(
      mergeMap((r) => from(r).pipe(
        filter((column) => this.allStructureHeaders.includes(column.name) && column.enabled),
        map((header) => header.name),
        reduce((acc, curr) => [...acc, curr], [] as string[])
      ))
    )),
    mergeMap((headers)=>iif(()=>headers.length!==0,of(headers),of(['name',
    'description',
    'interfaceMaxSimultaneity',
    'interfaceMinSimultaneity',
    'interfaceTaskFileType',
    'interfaceStructureCategory',]))),
    share(),
    shareReplay(1)
  )

  settingsDialog = combineLatest([this.structureService.BranchId,this.isEditing, this.currentElementHeaders, this.currentStructureHeaders]).pipe(
    take(1),
    switchMap(([branch,edit, elements, structures]) => this.dialog.open(ColumnPreferencesDialogComponent, {
      data: {
        branchId: branch,
        allHeaders2: this.allElementHeaders,
        allowedHeaders2: elements,
        allHeaders1: this.allStructureHeaders,
        allowedHeaders1: structures,
        editable: edit,
        headers1Label: 'Structure Headers',
        headers2Label: 'Element Headers',
        headersTableActive: true,
      }
    }).afterClosed().pipe(
      take(1),
    switchMap((result)=>this.structureService.updatePreferences(result))))
  )

  structureDialog = this.structureService.SubMessageId.pipe(
    take(1),
    switchMap((submessage)=>this.dialog.open(AddStructureDialogComponent, {
      data: {
        id: submessage,
        name: this.breadCrumb,
        structure: {
          id: '-1',
          name: '',
          elements: [],
          description: '',
          interfaceMaxSimultaneity: '',
          interfaceMinSimultaneity: '',
          interfaceStructureCategory: '',
          interfaceTaskFileType:0
        }
      }
    }).afterClosed().pipe(
      filter((val) => val !== undefined),
      take(1),
      switchMap((value: AddStructureDialog) => iif(() => value.structure.id !== '-1' && value.structure.id.length > 0, this.structureService.relateStructure(value.structure.id), this.structureService.createStructure(value.structure))),
      first()
    ))
  )
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public dialog: MatDialog,
    private structureService: CurrentStateService
  ) {
    this.messageData.subscribe((value) => {
      this.dataSource.data = value;
    });
    this.truncatedSections = ['description', 'EnumsLiteralsDesc', 'Notes'];
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((values) => {
      this.breadCrumb = values.get('name') || '';
      this.structureService.BranchType = values.get('branchType') || '';
      this.structureService.branchId = values.get('branchId') || '';
      this.structureService.messageId = values.get('messageId') || '';
      this.structureService.subMessageId = values.get('subMessageId') || '';
      this.structureService.connection = values.get('connection') || '';
    });
  }

  valueTracker(index: any, item: any) {
    return index;
  }

  expandRow(value: any) {
    if (this.expandedElement.indexOf(value) === -1) {
      this.expandedElement.push(value);
    }
  }
  hideRow(value: any) {
    let index = this.expandedElement.indexOf(value);
    if (index > -1) {
      this.expandedElement.splice(index, 1);
    }
  }

  rowChange(value: any, type: boolean) {
    if (type) {
      this.expandRow(value);
    } else {
      this.hideRow(value);
    }
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.searchTerms = filterValue;
    this.filter = filterValue.trim().toLowerCase();
    this.structureService.filter=(event.target as HTMLInputElement).value;
  }
  isTruncated(value: string) {
    if (this.truncatedSections.find((x) => x === value)) {
      return true;
    }
    return false;
  }

  openSettingsDialog() {
    this.settingsDialog.subscribe();
  }
  openAddStructureDialog() {
    this.structureDialog.subscribe();
  }
}

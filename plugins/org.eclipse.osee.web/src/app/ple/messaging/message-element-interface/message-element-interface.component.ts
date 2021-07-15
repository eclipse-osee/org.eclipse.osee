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
import { branchStorage } from '../shared/types/branchstorage';
import { fromEvent, iif } from 'rxjs';
import { AddStructureDialogComponent } from './components/add-structure-dialog/add-structure-dialog.component';
import { AddStructureDialog } from './types/AddStructureDialog';
import { filter, first, switchMap } from 'rxjs/operators';

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
  headers: string[] = [];
  truncatedSections: string[] = [];
  allowedStructureHeaders: string[] = [];
  editableStructureHeaders: string[] = [
    'name',
    'description',
    'interfaceMaxSimultaneity',
    'interfaceMinSimultaneity',
    'interfaceTaskFileType',
    'interfaceStructureCategory',];
  
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
  ];

  allowedElementHeaders: string[] = [
    'name',
    'platformTypeName2',
    'interfaceElementAlterable',
    'description',
    'notes',
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
  ];
  expandedElement: Array<any> = [];
  filter: string = '';
  searchTerms: string = '';
  breadCrumb: string = '';
  editMode: boolean = false;
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public dialog: MatDialog,
    private structureService: CurrentStateService
  ) {
    this.messageData.subscribe((value) => {
      this.dataSource.data = value;
    });
    this.allowedStructureHeaders = [
      'name',
      'description',
      'interfaceMaxSimultaneity',
      'interfaceMinSimultaneity',
      'interfaceTaskFileType',
      'interfaceStructureCategory',
    ]; //, "TaskFileType"
    this.truncatedSections = ['description', 'EnumsLiteralsDesc', 'Notes'];
    //this.hiddenHeaders=["expandoButton"]
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((values) => {
      this.breadCrumb = values.get('name') || '';
      this.structureService.branchId = values.get('branchId') || '';
      this.structureService.messageId = values.get('messageId') || '';
      this.structureService.subMessageId = values.get('subMessageId') || '';
    });
    //@todo: remove when user preferences are available on backend
    let branchStorage = JSON.parse(
      localStorage.getItem(this.structureService.BranchId.getValue()) || '{}'
    ) as branchStorage;
    if (branchStorage?.mim?.editMode) {
      this.editMode = branchStorage.mim.editMode;
    }
    if (branchStorage?.mim?.StructureHeaders?.length > 0) {
      this.allowedStructureHeaders = branchStorage.mim.StructureHeaders;
    }
    if (branchStorage?.mim?.ElementHeaders?.length > 0) {
      this.allowedElementHeaders = branchStorage.mim.ElementHeaders;
    }
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
    let dialogData: settingsDialogData = {
      branchId: this.structureService.BranchId.getValue(),
      allHeaders2: this.allElementHeaders,
      allowedHeaders2: this.allowedElementHeaders,
      allHeaders1: this.allStructureHeaders,
      allowedHeaders1: this.allowedStructureHeaders,
      editable: this.editMode,
      headers1Label: 'Structure Headers',
      headers2Label: 'Element Headers',
      headersTableActive: true,
    };
    const dialogRef = this.dialog.open(ColumnPreferencesDialogComponent, {
      data: dialogData,
    });
    dialogRef.afterClosed().subscribe((result: settingsDialogData) => {
      this.allowedElementHeaders = result.allowedHeaders2;
      this.allowedStructureHeaders = result.allowedHeaders1;
      this.editMode = result.editable;
      //@todo: remove when user preferences are available on backend
      if (localStorage.getItem(this.structureService.BranchId.getValue())) {
        let branchStorage = JSON.parse(
          localStorage.getItem(this.structureService.BranchId.getValue()) ||
            '{}'
        ) as branchStorage;
        branchStorage.mim['editMode'] = result.editable;
        branchStorage.mim['StructureHeaders'] = result.allowedHeaders1;
        branchStorage.mim['ElementHeaders'] = result.allowedHeaders2;
        localStorage.setItem(
          this.structureService.BranchId.getValue(),
          JSON.stringify(branchStorage)
        );
      } else {
        localStorage.setItem(
          this.structureService.BranchId.getValue(),
          JSON.stringify({
            mim: {
              editMode: result.editable,
              StructureHeaders: result.allowedHeaders1,
              ElementHeaders: result.allowedHeaders2,
            },
          })
        );
      }
    });
  }
  openAddStructureDialog() {
    let dialogData:AddStructureDialog= {
      id: this.structureService.subMessageId,
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
    let dialogRef=this.dialog.open(AddStructureDialogComponent, {
      data:dialogData
    });
    dialogRef.afterClosed().pipe(
      filter((val) => val !== undefined),
      switchMap((value: AddStructureDialog) => iif(() => value.structure.id !== '-1' && value.structure.id.length > 0, this.structureService.relateStructure(value.structure.id), this.structureService.createStructure(value.structure))),
      first()
    ).subscribe(() => { },()=>{});
  }
}

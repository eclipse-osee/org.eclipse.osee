import { trigger, state, style, transition, animate } from '@angular/animations';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { ColumnPreferencesDialogComponent } from '../../../shared/components/dialogs/column-preferences-dialog/column-preferences-dialog.component';
import { settingsDialogData } from '../../../shared/types/settingsdialog';
import { CurrentMessagesService } from '../../services/current-messages.service';
import { message } from '../../types/messages';
import { branchStorage } from '../../../shared/types/branchstorage'
import { AddMessageDialogComponent } from './add-message-dialog/add-message-dialog.component';
import { AddMessageDialog } from '../../types/AddMessageDialog';
import { filter, first, switchMap } from 'rxjs/operators';

@Component({
  selector: 'ple-messaging-message-table',
  templateUrl: './message-table.component.html',
  styleUrls: ['./message-table.component.sass'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
      transition('collapsed <=> expanded', animate('225ms cubic-bezier(0.2, 1, 0.4, 0.0)'))
    ]),
    trigger('expandButton', [
      state('closed', style({ transform: 'rotate(0)' })),
      state('open', style({ transform: 'rotate(-180deg)' })),
      transition('open => closed', animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
      transition('closed => open', animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
    ])
  ]
})
export class MessageTableComponent implements OnInit {
  messageData = this.messageService.messages;
  dataSource: MatTableDataSource<message> = new MatTableDataSource<message>();
  headers: string[] = [];
  expandedElement: Array<any> = [];
  filter: string = "";
  searchTerms: string = "";
  editMode: boolean = false;
  constructor (private messageService: CurrentMessagesService,public dialog: MatDialog) {
    this.messageData.subscribe((value) => {
      this.dataSource.data = value;
    })
    this.headers = ["name","description","interfaceMessageNumber","interfaceMessagePeriodicity","interfaceMessageRate","interfaceMessageWriteAccess","interfaceMessageType",'applicability'];
   }

  ngOnInit(): void {
    let branchStorage = JSON.parse(
      localStorage.getItem(this.messageService.BranchId.getValue()) || '{}'
    ) as branchStorage;
    if (branchStorage?.mim?.editMode) {
      this.editMode = branchStorage.mim.editMode;
    }
  }
  expandRow(value: any) {
    if (this.expandedElement.indexOf(value)===-1) {
      this.expandedElement.push(value);
    }
  }
  hideRow(value: any) {
    let index = this.expandedElement.indexOf(value);
    if (index > -1) {
      this.expandedElement.splice(index,1)
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
    this.messageService.filter = filterValue.trim().toLowerCase();
    this.filter = filterValue.trim().toLowerCase();
  }
  valueTracker(index: any, item: any) {
    return index;
  }
  
  openSettingsDialog() {
    let dialogData: settingsDialogData = {
      branchId: this.messageService.BranchId.getValue(),
      allHeaders2: [],
      allowedHeaders2: [],
      allHeaders1: [],
      allowedHeaders1: [],
      editable: this.editMode,
      headers1Label: 'Structure Headers',
      headers2Label: 'Element Headers',
      headersTableActive: false,
    };
    const dialogRef = this.dialog.open(ColumnPreferencesDialogComponent, {
      data: dialogData,
    });
    dialogRef.afterClosed().subscribe((result: settingsDialogData) => {
      //this.allowedElementHeaders = result.allowedHeaders2;
      //this.allowedStructureHeaders = result.allowedHeaders1;
      this.editMode = result.editable;
      //@todo: remove when user preferences are available on backend
      if (localStorage.getItem(this.messageService.BranchId.getValue())) {
        let branchStorage = JSON.parse(
          localStorage.getItem(this.messageService.BranchId.getValue()) ||
            '{}'
        ) as branchStorage;
        branchStorage.mim['editMode'] = result.editable;
        localStorage.setItem(
          this.messageService.BranchId.getValue(),
          JSON.stringify(branchStorage)
        );
      } else {
        localStorage.setItem(
          this.messageService.BranchId.getValue(),
          JSON.stringify({
            mim: {
              editMode: result.editable,
            },
          })
        );
      }
    });
  }
  openNewMessageDialog() {
    let dialogData: Partial<AddMessageDialog> = {
      name: '',
      description: '',
      interfaceMessageNumber: '',
      interfaceMessagePeriodicity: '',
      interfaceMessageRate: '',
      interfaceMessageType: '',
      interfaceMessageWriteAccess: ''
    };
    const dialogRef = this.dialog.open(AddMessageDialogComponent, {
      data: dialogData
    });
    dialogRef.afterClosed().pipe(
      filter((val) => val !== undefined),
      switchMap((val) => this.messageService.createMessage(val)),
      first()
    ).subscribe();
  }
}

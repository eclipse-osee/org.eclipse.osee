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
import { trigger, state, style, transition, animate } from '@angular/animations';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { ColumnPreferencesDialogComponent } from '../../../shared/components/dialogs/column-preferences-dialog/column-preferences-dialog.component';
import { settingsDialogData } from '../../../shared/types/settingsdialog';
import { CurrentMessagesService } from '../../services/current-messages.service';
import { message } from '../../types/messages';
import { AddMessageDialogComponent } from './add-message-dialog/add-message-dialog.component';
import { AddMessageDialog } from '../../types/AddMessageDialog';
import { filter, first, map, share, shareReplay, switchMap, take } from 'rxjs/operators';
import { combineLatest } from 'rxjs';

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
  expandedElement: string[] = [];
  filter: string = "";
  searchTerms: string = "";
  preferences = this.messageService.preferences;
  inEditMode = this.preferences.pipe(
    map((r) => r.inEditMode),
    share(),
    shareReplay(1)
  );
  constructor (private messageService: CurrentMessagesService,public dialog: MatDialog) {
    this.messageData.subscribe((value) => {
      this.dataSource.data = value;
    })
    this.headers = ["name","description","interfaceMessageNumber","interfaceMessagePeriodicity","interfaceMessageRate","interfaceMessageWriteAccess","interfaceMessageType",'applicability'];
   }

  ngOnInit(): void {}
  expandRow(value: string) {
    if (this.expandedElement.indexOf(value)===-1) {
      this.expandedElement.push(value);
    }
  }
  hideRow(value: string) {
    let index = this.expandedElement.indexOf(value);
    if (index > -1) {
      this.expandedElement.splice(index,1)
    }
  }

  rowChange(value: string, type: boolean) {
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
    combineLatest([this.inEditMode, this.messageService.BranchId]).pipe(
      take(1),
      switchMap(([edit, branch]) => this.dialog.open(ColumnPreferencesDialogComponent, {
        data: {
          branchId: branch,
          allHeaders2: [],
          allowedHeaders2: [],
          allHeaders1: [],
          allowedHeaders1: [],
          editable: edit,
          headers1Label: 'Structure Headers',
          headers2Label: 'Element Headers',
          headersTableActive: false,
        }
      }).afterClosed().pipe(
        take(1),
        switchMap((result) => this.messageService.updatePreferences(result))))
    ).subscribe();
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

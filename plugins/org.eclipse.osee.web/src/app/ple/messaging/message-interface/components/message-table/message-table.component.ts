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
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { ColumnPreferencesDialogComponent } from '../../../shared/components/dialogs/column-preferences-dialog/column-preferences-dialog.component';
import { CurrentMessagesService } from '../../services/current-messages.service';
import { message } from '../../types/messages';
import { AddMessageDialogComponent } from './add-message-dialog/add-message-dialog.component';
import { AddMessageDialog } from '../../types/AddMessageDialog';
import { filter, first, map, share, shareReplay, switchMap, take, takeUntil } from 'rxjs/operators';
import { combineLatest, iif, of } from 'rxjs';
import { MatMenuTrigger } from '@angular/material/menu';
import { RemoveMessageDialogComponent } from '../dialogs/remove-message-dialog/remove-message-dialog.component';
import { DeleteMessageDialogComponent } from '../dialogs/delete-message-dialog/delete-message-dialog.component';
import { EditViewFreeTextFieldDialogComponent } from '../../../shared/components/dialogs/edit-view-free-text-field-dialog/edit-view-free-text-field-dialog.component';
import { EditViewFreeTextDialog } from '../../../shared/types/EditViewFreeTextDialog';
import { HeaderService } from '../../../shared/services/ui/header.service';

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
  messageData = this.messageService.messages.pipe(
    switchMap((data)=>of(new MatTableDataSource<message>(data))),
    takeUntil(this.messageService.done));
  // dataSource: MatTableDataSource<message> = new MatTableDataSource<message>();
  headers = this.headerService.AllMessageHeaders;
  expandedElement: string[] = [];
  filter: string = "";
  searchTerms: string = "";
  preferences = this.messageService.preferences.pipe(takeUntil(this.messageService.done));
  inEditMode = this.preferences.pipe(
    map((r) => r.inEditMode),
    share(),
    shareReplay(1),
    takeUntil(this.messageService.done)
  );
  menuPosition = {
    x: '0',
    y:'0'
  }
  @ViewChild(MatMenuTrigger, { static: true })
  matMenuTrigger!: MatMenuTrigger;
  constructor (private messageService: CurrentMessagesService,public dialog: MatDialog, private headerService:HeaderService) {}

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

  openMenu(event: MouseEvent, message: message) {
    event.preventDefault();
    this.menuPosition.x = event.clientX + 'px';
    this.menuPosition.y = event.clientY + 'px';
    this.matMenuTrigger.menuData = {
      message: message,
    }
    this.matMenuTrigger.openMenu();
  }
  removeMessage(message: message) {
    //open dialog, iif result ==='ok' messageservice. removemessage
    this.dialog.open(RemoveMessageDialogComponent, {
      data: { message: message }
    }).afterClosed().pipe(
      take(1),
      switchMap((dialogResult: string) => iif(() => dialogResult === 'ok',
        this.messageService.removeMessage(message.id),
        of()
      ))
    ).subscribe();
  }

  deleteMessage(message: message) {
    this.dialog.open(DeleteMessageDialogComponent, {
      data: { message: message }
    }).afterClosed().pipe(
      take(1),
      switchMap((dialogResult: string) => iif(() => dialogResult === 'ok',
        this.messageService.deleteMessage(message.id),
        of()
      ))
    ).subscribe();
  }

  openDescriptionDialog(description: string,messageId:string) {
    this.dialog.open(EditViewFreeTextFieldDialogComponent, {
      data: {
        original: JSON.parse(JSON.stringify(description)) as string,
        type: 'Description',
        return: description
      },
      minHeight: '60%',
      minWidth:'60%'
    }).afterClosed().pipe(
      take(1),
      switchMap((response: EditViewFreeTextDialog | string) => iif(() => response === 'ok' || response === 'cancel'|| response === undefined,
      //do nothing
      of(),
      //change description
      this.messageService.partialUpdateMessage({id:messageId,description:(response as EditViewFreeTextDialog).return})
      ))
    ).subscribe();
  }

  getHeaderByName(value: string) {
    return this.headerService.getHeaderByName(value,'message');
  }
}

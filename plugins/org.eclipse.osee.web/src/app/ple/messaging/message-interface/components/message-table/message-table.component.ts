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
import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuTrigger } from '@angular/material/menu';
import { MatTableDataSource } from '@angular/material/table';
import { iif, of } from 'rxjs';
import { filter, first, map, share, shareReplay, switchMap, take, takeUntil } from 'rxjs/operators';
import { difference } from 'src/app/types/change-report/change-report';
import { applic } from '../../../../../types/applicability/applic';
import { EditViewFreeTextFieldDialogComponent } from '../../../shared/components/dialogs/edit-view-free-text-field-dialog/edit-view-free-text-field-dialog.component';
import { HeaderService } from '../../../shared/services/ui/header.service';
import { EditViewFreeTextDialog } from '../../../shared/types/EditViewFreeTextDialog';
import { CurrentMessagesService } from '../../services/current-messages.service';
import { AddMessageDialog } from '../../types/AddMessageDialog';
import { message, messageChanges, messageWithChanges } from '../../types/messages';
import { DeleteMessageDialogComponent } from '../dialogs/delete-message-dialog/delete-message-dialog.component';
import { RemoveMessageDialogComponent } from '../dialogs/remove-message-dialog/remove-message-dialog.component';
import { AddMessageDialogComponent } from './add-message-dialog/add-message-dialog.component';

@Component({
  selector: 'ple-messaging-message-table',
  templateUrl: './message-table.component.html',
  styleUrls: ['./message-table.component.sass'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({maxHeight: '0vh', overflowY: 'hidden' })),
      state('expanded', style({maxHeight: '60vh', overflowY: 'auto'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.42, 0.0, 0.58, 1)')),
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
    switchMap((data)=>of(new MatTableDataSource<message|messageWithChanges>(data))),
    takeUntil(this.messageService.done));
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
  sideNav = this.messageService.sideNavContent;
  sideNavOpened = this.sideNav.pipe(
    map((value)=>value.opened)
  )
  inDiffMode = this.messageService.isInDiff.pipe(
    switchMap((val) => iif(() => val, of('true'), of('false'))),
  );
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
      first(),
      filter((val) => val !== undefined),
      switchMap((val) => this.messageService.createMessage(val)),
    ).subscribe();
  }

  openMenu(event: MouseEvent, message: message, field: string|boolean|applic, header: string) {
    event.preventDefault();
    this.menuPosition.x = event.clientX + 'px';
    this.menuPosition.y = event.clientY + 'px';
    this.matMenuTrigger.menuData = {
      message: message,
      field: field,
      header:header
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

  viewDiff(open: boolean, value: difference, header: string) {
    this.messageService.sideNav = { opened: open, field: header, currentValue: value.currentValue as string | number | applic, previousValue: value.previousValue as string | number | applic | undefined,transaction:value.transactionToken };
  }

  hasChanges(value: message | messageWithChanges): value is messageWithChanges {
    return (value as messageWithChanges).changes !== undefined;
  }
  changeExists(value:messageWithChanges,header: keyof messageChanges): header is keyof messageChanges{
    return (value as messageWithChanges).changes[header] !== undefined;
  }

}

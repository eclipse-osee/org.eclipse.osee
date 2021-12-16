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
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuTrigger } from '@angular/material/menu';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, iif, of } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';
import { EditViewFreeTextFieldDialogComponent } from '../../../shared/components/dialogs/edit-view-free-text-field-dialog/edit-view-free-text-field-dialog.component';
import { HeaderService } from '../../../shared/services/ui/header.service';
import { EditViewFreeTextDialog } from '../../../shared/types/EditViewFreeTextDialog';
import { applic } from '../../../../../types/applicability/applic';
import { CurrentMessagesService } from '../../services/current-messages.service';
import { AddSubMessageDialog } from '../../types/AddSubMessageDialog';
import { message, messageWithChanges } from '../../types/messages';
import { subMessage, subMessageWithChanges } from '../../types/sub-messages';
import { DeleteSubmessageDialogComponent } from '../dialogs/delete-submessage-dialog/delete-submessage-dialog.component';
import { RemoveSubmessageDialogComponent } from '../dialogs/remove-submessage-dialog/remove-submessage-dialog.component';
import { AddSubMessageDialogComponent } from './add-sub-message-dialog/add-sub-message-dialog.component';
import { LocationStrategy } from '@angular/common';
import { difference } from 'src/app/types/change-report/change-report';

@Component({
  selector: 'ple-messaging-sub-message-table',
  templateUrl: './sub-message-table.component.html',
  styleUrls: ['./sub-message-table.component.sass']
})
export class SubMessageTableComponent implements OnInit, OnChanges {
  @Input() data: subMessage[] = [];
  @Input() dataSource: MatTableDataSource<subMessage> = new MatTableDataSource<subMessage>();
  @Input() filter: string = "";
  
  @Input() element!: message;
  @Input() editMode: boolean = false;
  @Output() expandRow = new EventEmitter();
  headers = this.headerService.AllSubMessageHeaders.pipe(
    switchMap(([name,description,number,applicability])=>of([name,description,number," ",applicability]))
  );
  _messageRoute = combineLatest([this.messageService.initialRoute, this.messageService.endOfRoute]).pipe(
    switchMap(([initial,end])=>of({beginning:initial,end:end}))
  )
  menuPosition = {
    x: '0',
    y:'0'
  }
  @ViewChild(MatMenuTrigger, { static: true })
  matMenuTrigger!: MatMenuTrigger;
  constructor(public dialog: MatDialog,private route: ActivatedRoute, private router: Router,private messageService: CurrentMessagesService, private headerService: HeaderService, private angLocation:LocationStrategy) {
    this.dataSource.data = this.data;
  }
  ngOnChanges(changes: SimpleChanges): void {
    this.dataSource.data = this.data;
    if (this.filter !== "") {
      if (this.dataSource.filteredData.length > 0) {
        this.expandRow.emit(this.element);
      } 
    }
  }

  ngOnInit(): void {
  }
  valueTracker(index: any, item: any) {
    return index;
  }

  openMenu(event: MouseEvent, message: message, submessage: subMessage, location: string,field:string|applic,header:string) {
    event.preventDefault();
    this.menuPosition.x = event.clientX + 'px';
    this.menuPosition.y = event.clientY + 'px';
    this.matMenuTrigger.menuData = {
      message: message,
      submessage: submessage,
      location: location,
      field: field,
      header:header
    }
    this.matMenuTrigger.openMenu();
  }

  createNewSubMessage() {
    this.dialog.open(AddSubMessageDialogComponent, {
      data: {
        name:this.element.name,
        id: this.element.id,
        subMessage: {
          name: '',
          description: '',
          interfaceSubMessageNumber:''
        }
      }
    }).afterClosed().pipe(
      take(1),
      filter((val)=>val!==undefined),
      switchMap((z: AddSubMessageDialog) => iif(() => z != undefined && z.subMessage != undefined && z.subMessage.id != undefined && z?.subMessage?.id.length > 0 && z.subMessage.id!=='-1', this.messageService.relateSubMessage(z.id, z?.subMessage?.id || '-1'), this.messageService.createSubMessage(z.subMessage, z.id)))
    ).subscribe();
  }

  removeSubMessage(submessage:subMessage,message:message) {
    this.dialog.open(RemoveSubmessageDialogComponent, {
      data: { submessage: submessage, message: message }
    }).afterClosed().pipe(
      take(1),
      switchMap((dialogResult: string) => iif(() => dialogResult === 'ok',
        this.messageService.removeSubMessage(submessage?.id || '', message.id),
        of()
      ))
    ).subscribe();
  }
  deleteSubMessage(submessage: subMessage) {
    this.dialog.open(DeleteSubmessageDialogComponent, {
      data: { submessage: submessage }
    }).afterClosed().pipe(
      take(1),
      switchMap((dialogResult: string) => iif(() => dialogResult === 'ok',
        this.messageService.deleteSubMessage(submessage.id || ''),
        of()))
    ).subscribe();
  }

  openDescriptionDialog(description: string,submessageId:string,messageId:string) {
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
      this.messageService.partialUpdateSubMessage({id:submessageId,description:(response as EditViewFreeTextDialog).return},messageId)
      ))
    ).subscribe();
  }

  getHeaderByName(value: string) {
    return this.headerService.getHeaderByName(value,'submessage');
  }

  viewDiff(open: boolean, value: difference, header: string) {
    this.messageService.sideNav = { opened: open, field: header, currentValue: value?.currentValue || '', previousValue: value?.previousValue || '', transaction: value.transactionToken };
  }
}

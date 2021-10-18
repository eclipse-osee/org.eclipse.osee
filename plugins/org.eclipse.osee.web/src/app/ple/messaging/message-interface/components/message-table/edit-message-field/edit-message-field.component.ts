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
import { Component, Input, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { share, debounceTime, distinctUntilChanged, map, tap, switchMap, takeUntil } from 'rxjs/operators';
import { EnumsService } from 'src/app/ple/messaging/shared/services/http/enums.service';
import { applic } from 'src/app/ple/messaging/shared/types/NamedId.applic';
import { CurrentMessagesService } from '../../../services/current-messages.service';

interface message {
  id: string,
  name: string,
  description: string ,
  interfaceMessageRate: string ,
  interfaceMessagePeriodicity: string ,
  interfaceMessageWriteAccess: boolean ,
  interfaceMessageType: string ,
  interfaceMessageNumber:string 
}

@Component({
  selector: 'osee-messaging-edit-message-field',
  templateUrl: './edit-message-field.component.html',
  styleUrls: ['./edit-message-field.component.sass']
})
export class EditMessageFieldComponent implements OnInit {
  @Input() messageId!: string;
  @Input() header: string = '';
  @Input() value: string|applic = '';
  private _value: Subject<string|applic> = new Subject();
  _message: Partial<message> = {
    id:this.messageId
  };
  private _sendValue = this._value.pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    map((x: any) => this._message[this.header as keyof message] = x),
    tap(() => {
      this._message.id = this.messageId;
    }),
    switchMap(val=>this.messageService.partialUpdateMessage(this._message))
  )

  rates = this.enumService.rates.pipe(takeUntil(this.messageService.done));
  types = this.enumService.types.pipe(takeUntil(this.messageService.done));
  applics = this.messageService.applic.pipe(takeUntil(this.messageService.done));
  periodicities = this.enumService.periodicities.pipe(takeUntil(this.messageService.done));
  constructor (private messageService: CurrentMessagesService, private enumService: EnumsService) {
    this._sendValue.subscribe();
   }

  ngOnInit(): void {
  }
  updateMessage(header: string, value: string|applic) {
    this._value.next(value);
  }

  compareApplics(o1:any,o2:any) {
    return o1.id === o2.id && o1.name === o2.name;
  }
}

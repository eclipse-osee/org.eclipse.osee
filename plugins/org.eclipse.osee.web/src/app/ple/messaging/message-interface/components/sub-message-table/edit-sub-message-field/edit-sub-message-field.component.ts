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
import { share, debounceTime, distinctUntilChanged, map, switchMap, tap, takeUntil } from 'rxjs/operators';
import { applic } from 'src/app/types/applicability/applic';
import { CurrentMessagesService } from '../../../services/current-messages.service';
import { subMessage } from '../../../types/sub-messages';

@Component({
  selector: 'osee-messaging-edit-sub-message-field',
  templateUrl: './edit-sub-message-field.component.html',
  styleUrls: ['./edit-sub-message-field.component.sass']
})
export class EditSubMessageFieldComponent implements OnInit {

  @Input() messageId!: string;
  @Input() subMessageId!: string ;
  @Input() header: string = '';
  @Input() value: string|applic = '';
  private _value: Subject<string|applic> = new Subject();
  _subMessage: Partial<subMessage> = {
    id:this.subMessageId
  };
  private _sendValue = this._value.pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    map((x: any) => this._subMessage[this.header as keyof subMessage] = x),
    tap(() => {
      this._subMessage.id = this.subMessageId;
    }),
    switchMap(val=>this.messageService.partialUpdateSubMessage(this._subMessage,this.messageId))
  )

  applics = this.messageService.applic.pipe(takeUntil(this.messageService.done));
  constructor (private messageService: CurrentMessagesService) {
    this._sendValue.subscribe();
  }

  ngOnInit(): void {
  }

  updateSubMessage(header: string, value: string|applic) {
    this._value.next(value);
  }
  compareApplics(o1:any,o2:any) {
    return o1.id === o2.id && o1.name === o2.name;
  }
}

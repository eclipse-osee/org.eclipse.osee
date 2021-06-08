import { Component, Input, OnInit } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { share, debounceTime, distinctUntilChanged, map, switchMap, tap } from 'rxjs/operators';
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
  @Input() value: string = '';
  private _value: Subject<string> = new Subject();
  _subMessage: Partial<subMessage> = {
    id:this.subMessageId
  };
  private _sendValue = this._value.pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    map((x: string) => this._subMessage[this.header as keyof subMessage] = x),
    tap(() => {
      this._subMessage.id = this.subMessageId;
    }),
    switchMap(val=>this.messageService.partialUpdateSubMessage(this._subMessage,this.messageId))
  )
  constructor (private messageService: CurrentMessagesService) {
    this._sendValue.subscribe();
  }

  ngOnInit(): void {
  }

  updateSubMessage(header: string, value: string) {
    this._value.next(value);
  }
}

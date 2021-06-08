import { Component, Input, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { share, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';
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
  @Input() value: string = '';
  private _value: Subject<string> = new Subject();
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
  constructor (private messageService: CurrentMessagesService) {
    this._sendValue.subscribe();
   }

  ngOnInit(): void {
  }
  updateMessage(header: string, value: string) {
    this._value.next(value);
  }
}

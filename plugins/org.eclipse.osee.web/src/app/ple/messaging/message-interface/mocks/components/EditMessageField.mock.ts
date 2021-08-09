import { Component, Input } from "@angular/core";

@Component({
    selector: 'osee-messaging-edit-message-field',
    template:'<button mat-raised-button>Ok</button>'
})
export class EditMessageFieldComponentMock{
    @Input() messageId!: string;
    @Input() header!: string;
    @Input() value!: string;
}
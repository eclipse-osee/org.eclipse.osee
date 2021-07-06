import { Component, Input } from "@angular/core";

@Component({
    selector: 'osee-messaging-edit-structure-field',
    template:'<p>Dummy</p>'
})
export class EditStructureFieldComponentMock{
    @Input() structureId!: string ;
    @Input() header!: string;
    @Input() value!: string;
  }
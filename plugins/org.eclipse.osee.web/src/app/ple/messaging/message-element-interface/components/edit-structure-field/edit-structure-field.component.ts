import { Component, Input, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { share, debounceTime, distinctUntilChanged, map, tap, switchMap } from 'rxjs/operators';
import { EnumsService } from '../../../shared/services/http/enums.service';
import { CurrentStateService } from '../../services/current-state.service';

interface structure {
  id: string,
  name: string,
  description: string,
  interfaceMaxSimultaneity: string,
  interfaceMinSimultaneity: string,
  interfaceTaskFileType: number,
  interfaceStructureCategory: string
}
@Component({
  selector: 'osee-messaging-edit-structure-field',
  templateUrl: './edit-structure-field.component.html',
  styleUrls: ['./edit-structure-field.component.sass']
})
export class EditStructureFieldComponent implements OnInit {
  
  @Input() structureId!: string ;
  @Input() header: string = '';
  @Input() value: string = '';
  private _value: Subject<string> = new Subject();
  _structure: Partial<structure> = {
    id:this.structureId
  };
  private _sendValue = this._value.pipe(
    share(),
    debounceTime(500),
    distinctUntilChanged(),
    map((x: any) => this._structure[this.header as keyof structure] = x),
    tap(() => {
      this._structure.id = this.structureId;
    }),
    switchMap(val=>this.structureService.partialUpdateStructure(this._structure))
  )
  categories= this.enumService.categories;
  constructor (private structureService: CurrentStateService, private enumService: EnumsService) {
    this._sendValue.subscribe();
   }

  ngOnInit(): void {
  }

  updateStructure(header: string, value: string) {
    this._value.next(value);
  }

}

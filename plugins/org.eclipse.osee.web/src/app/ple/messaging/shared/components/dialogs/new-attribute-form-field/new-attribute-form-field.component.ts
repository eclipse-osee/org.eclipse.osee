/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { logicalTypeFieldInfo } from '../../../types/logicaltype';

@Component({
  selector: 'osee-new-attribute-form-field',
  templateUrl: './new-attribute-form-field.component.html',
  styleUrls: ['./new-attribute-form-field.component.sass']
})
export class NewAttributeFormFieldComponent implements OnInit {

  @Input() form!: logicalTypeFieldInfo;
  @Input() units: string[] = [];
  @Output() formChanged = new EventEmitter<logicalTypeFieldInfo>();
  constructor() { }

  ngOnInit(): void {
    this.setDefaultValue();
  }
  setDefaultValue() {
    if (!this.form.editable) {
      const value = this.form.value!==undefined?JSON.parse(JSON.stringify(this.form.value)):"";
      this.form.value = this.form.defaultValue;
      if (value !== this.form.defaultValue) {
        this.change(); 
      } 
    }
  }
  change() {
    this.formChanged.emit(this.form);
  }
}

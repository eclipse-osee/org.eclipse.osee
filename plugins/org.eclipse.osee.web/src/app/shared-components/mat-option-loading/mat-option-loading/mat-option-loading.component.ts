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
import {
    AfterViewInit,
    Component,
    Host,
    Input,
    OnInit,
    QueryList,
    ViewChildren,
} from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatSelect } from '@angular/material/select';
import { Observable } from 'rxjs';

@Component({
    selector: 'osee-mat-option-loading',
    templateUrl: './mat-option-loading.component.html',
    styleUrls: ['./mat-option-loading.component.sass'],
})
export class MatOptionLoadingComponent<T=unknown> implements OnInit, AfterViewInit {
    @Input('options') _options!: Observable<T[]>;
    @Input() objectName = 'options';
  @ViewChildren(MatOption) protected options!: QueryList<MatOption>;
  
  @Input() disableSelect = false;
    constructor(@Host() private select: MatSelect) {}
    ngAfterViewInit(): void {
        this.options.changes.subscribe((options) =>
            this.registerSelectOptions(this.select, options)
        );
        this.registerSelectOptions(this.select, this.options);
    }

    ngOnInit(): void {}

    protected registerSelectOptions(
        select: MatSelect,
        options: QueryList<MatOption>
    ): void {
        select.options.reset([
            ...select.options.toArray(),
            ...options.toArray(),
        ]);
        select.options.notifyOnChanges();
    }
  
  setDisabled(value: boolean) {
    if (this.disableSelect) {
      this.select.disabled = value; 
    }
  }
}

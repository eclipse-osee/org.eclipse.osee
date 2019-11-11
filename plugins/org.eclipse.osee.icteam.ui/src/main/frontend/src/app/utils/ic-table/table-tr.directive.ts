/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
import { Directive, Input, ContentChild } from '@angular/core';

@Directive({
  selector: 'app-table-tr'
})
export class TableTrDirective {

  @Input() header: string;
  @Input() property: Array<string>;
  @Input() sortable: Boolean = false;
  @Input() filter: Boolean = false;

  @ContentChild('dataTableCell') cellTemplate;

  constructor() { }

}

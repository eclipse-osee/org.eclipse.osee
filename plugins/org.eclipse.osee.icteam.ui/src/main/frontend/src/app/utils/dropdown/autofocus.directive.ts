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
import { Directive, ElementRef, Host, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';

@Directive({
  selector: '[ssAutofocus]'
})
export class AutofocusDirective implements OnInit, OnChanges {

  /**
   * Will set focus if set to falsy value or not set at all
   */
  @Input() ssAutofocus: boolean;

  get element(): { focus?: Function } {
    return this.elemRef.nativeElement;
  }

  constructor(
    @Host() private elemRef: ElementRef,
  ) { }

  ngOnInit() {
    this.focus();
  }

  ngOnChanges(changes: SimpleChanges) {
    const ssAutofocusChange = changes.ssAutofocus;

    if (ssAutofocusChange && !ssAutofocusChange.isFirstChange()) {
      this.focus();
    }
  }

  focus() {
    if (this.ssAutofocus) {
      return;
    }

    this.element.focus && this.element.focus();
  }

}

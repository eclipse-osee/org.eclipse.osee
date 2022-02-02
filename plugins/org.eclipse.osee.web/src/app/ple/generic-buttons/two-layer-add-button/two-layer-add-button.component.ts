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
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { addButtonHoverIconTransition, addButtonIconTransition, slidingAddButtonAnim } from './two-layer-add-button.animation'

@Component({
  selector: 'two-layer-add-button',
  templateUrl: './two-layer-add-button.component.html',
  styleUrls: ['./two-layer-add-button.component.sass'],
  animations:[slidingAddButtonAnim,addButtonIconTransition, addButtonHoverIconTransition]
})
export class TwoLayerAddButtonComponent<T extends string=any, R extends {id:string,name:string}=any> implements OnInit {
  @Input() baseLevel: T = {} as T;
  @Input() nestedLevel: R[] = [] as R[];
  @Input() nestedLevelPrefix = "";
  @Input() firstOptionDisabled = false;
  @Input() baseIcon: string = "add";
  @Input() nestedIcon: string = "add"
  @Input() openDirection:string="UP" //@todo implement later when this is needed, default should be UP
  defaultValue: R = {id:'-1',name:''} as R;
  hoveredElements: string[] = [];

  @Output() normalClick=new EventEmitter<string|undefined>()
  @Output() nestedClick = new EventEmitter<R>();
  isOpen = new BehaviorSubject<boolean>(false);

  constructor() { }

  ngOnInit(): void {
  }

  mainClick() {
    this.isOpen.next(!this.isOpen.getValue())
  }
  removeHover(element:R) {
    this.hoveredElements=this.hoveredElements.filter((item)=>item!==element.id)
  }
}

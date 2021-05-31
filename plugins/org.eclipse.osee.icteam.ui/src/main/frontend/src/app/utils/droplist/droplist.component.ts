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
import {
  Component,
  OnInit,
  Input,
  Output,
  EventEmitter,
  ElementRef,
} from "@angular/core";

@Component({
  selector: "app-droplist",
  templateUrl: "./droplist.component.html",
  styleUrls: ["./droplist.component.scss"],
})
export class DroplistComponent implements OnInit {
  private selectedItems: any;
  @Input() selectedValue;
  @Input() itemList;
  @Output() statusUpdate = new EventEmitter();

  showlist: Boolean;
  constructor(private element: ElementRef) {}

  ngOnInit() {
    console.log(this.itemList);
    console.log(this.selectedValue);

    this.showlist = false;
  }

  openlist(): void {
    console.log("OpenClicked");
    this.showlist = !this.showlist;
  }

  changeStatus(status: any): void {
    this.statusUpdate.emit(status);
    this.showlist = false;
  }
  close(event) {
    if (this.showlist) {
      this.showlist = false;
    }
  }
}

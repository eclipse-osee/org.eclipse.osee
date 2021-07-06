import { Component, EventEmitter, Input, Output } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";

import { message } from "../../types/messages";
import { subMessage } from "../../types/sub-messages";

@Component({
    selector: 'ple-messaging-sub-message-table',
    template:'<p>Dummy</p>'
})
export class SubMessageTableComponentMock{
    @Input() data!: subMessage[];
    @Input() dataSource!: MatTableDataSource<subMessage>;
    @Input() filter!: string;
  
  @Input() element!: message;
    @Input() editMode!: boolean;
    @Output() expandRow: EventEmitter<boolean> = new EventEmitter();
}
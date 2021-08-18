import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { map, mergeAll, mergeMap,scan, share} from 'rxjs/operators';
import { CurrentMessagesService } from '../../../services/current-messages.service';
import { AddSubMessageDialog } from '../../../types/AddSubMessageDialog';
import { subMessage } from '../../../types/sub-messages';

@Component({
  selector: 'app-add-sub-message-dialog',
  templateUrl: './add-sub-message-dialog.component.html',
  styleUrls: ['./add-sub-message-dialog.component.sass']
})
export class AddSubMessageDialogComponent implements OnInit {

  availableSubMessages = this.messageService.allMessages.pipe(
    mergeMap(x=>x),
    map(x => x?.subMessages),
    mergeAll(),
    scan((acc, curr) => [...acc, curr], [] as subMessage[]),
    map((y) => y.sort((a, b) => { return a.id!=undefined && b.id!=undefined && a.id > b.id ? -1 : 1 })),
    map((y) => y.filter((val, index, array) => {
      return !index || val.id != array[index - 1].id;
    })),
    map((y) => y.sort((a, b) => { return a.name > b.name ? -1 : 1 })),
    share()
  )
  storedId: string = '-1';
  constructor (public dialogRef: MatDialogRef<AddSubMessageDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: AddSubMessageDialog, private messageService: CurrentMessagesService) {
  }

  ngOnInit(): void {
  }

  moveToStep(index: number, stepper: MatStepper) {
    stepper.selectedIndex = index - 1;
  }

  createNew() {
    this.data.subMessage.id = '-1';
  }

  storeId(value: subMessage) {
    this.storedId = value.id || '-1';
  }

  moveToReview(stepper: MatStepper) {
    this.data.subMessage.id = this.storedId;
    this.moveToStep(3, stepper);
  }
}
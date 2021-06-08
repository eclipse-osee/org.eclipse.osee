import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class HttpLoadingService {

  private _isLoading: BehaviorSubject<String> = new BehaviorSubject<String>("true");
  constructor () { }
  
  get isLoading() {
    return this._isLoading;
  }

  set loading(value: boolean) {
    this._isLoading.next(value.toString());
  }

}

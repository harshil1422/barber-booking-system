import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../../../core/config/api.config';
import { Slot } from '../model/slot.model';

@Injectable({
  providedIn: 'root'
})
export class SlotService {

  constructor(private http: HttpClient) {}

  getSlotsByBarber(barberId: number): Observable<Slot[]> {
    return this.http.get<Slot[]>(
      `${API_CONFIG.SLOT_SERVICE}/barber/${barberId}`
    );
  }

  lockSlot(slotId: number): Observable<void> {
    return this.http.post<void>(
      `${API_CONFIG.SLOT_SERVICE}/${slotId}/lock`,
      {}
    );
  }
}

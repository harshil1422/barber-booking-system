import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../../../core/config/api.config';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {

  constructor(private http: HttpClient) {}

  createAppointment(payload: {
    barberId: number;
    slotId: number;
    userId: number;
  }): Observable<any> {
    return this.http.post(
      API_CONFIG.APPOINTMENT_SERVICE,
      payload
    );
  }
}

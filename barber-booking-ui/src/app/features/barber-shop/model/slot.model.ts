export type SlotStatus = 'AVAILABLE' | 'BOOKED' | 'LOCKED';

export interface Slot {
  id: number;
  barberId: number;
  date: string;      // yyyy-MM-dd
  startTime: string; // HH:mm
  endTime: string;   // HH:mm
  status: SlotStatus;
}

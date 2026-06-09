export enum ChairLiveStatus {
  ACTIVE  = 'ACTIVE',
  IDLE    = 'IDLE',
  BREAK   = 'BREAK',
  WALK_IN = 'WALK_IN',
}
 
export interface ChairBreak {
  id: string;
  chairId: string;
  startsAt: string;
  endsAt: string;
  label: string;
}
 
export interface Chair {
  id: string;
  shopId: string;
  name: string;
  assignedBarber: string;
  isActive: boolean;
  walkInActive: boolean;
  liveStatus: ChairLiveStatus;
  breaks: ChairBreak[];
}
 
export interface WalkInQueue {
  id: string;
  chairId: string;
  customerName: string;
  customerPhone: string;
  queuePosition: number;
  estimatedWaitMinutes: number;
  status: string;
  joinedAt: string;
}
import { createActionGroup,props } from '@ngrx/store'

export const DashboardActions = createActionGroup({
    source: 'Dashboard',
    events: {
        'Switch Role': props<{ role: 'USER' | 'BARBER' }>(),
    },
})
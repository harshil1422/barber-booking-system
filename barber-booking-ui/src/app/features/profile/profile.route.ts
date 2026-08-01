import {Routes} from '@angular/router'

export const profileRoute:Routes =[
    {
        path:'/user',
        loadComponent: () =>import('../profile/user-profile/user-profile.component').then(m=>m.UserProfileComponent)
    }
]
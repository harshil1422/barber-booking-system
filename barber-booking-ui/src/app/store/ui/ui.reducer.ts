import { createReducer, on } from '@ngrx/store';
import { createActionGroup, props, emptyProps } from '@ngrx/store';
import { UserRole } from '@core/models/auth.models';

export const UiActions = createActionGroup({
  source: 'UI',
  events: {
    'Set Active Role':     props<{ role: UserRole }>(),
    'Toggle Sidenav':      emptyProps(),
    'Set Sidenav Open':    props<{ open: boolean }>(),
    'Set Global Loading':  props<{ loading: boolean }>(),
  }
});

export interface UiState {
  activeRole:    UserRole;
  sidenavOpen:   boolean;
  globalLoading: boolean;
}

const initialState: UiState = {
  activeRole:    'USER',
  sidenavOpen:   true,
  globalLoading: false
};

export const uiReducer = createReducer(
  initialState,
  on(UiActions.setActiveRole,    (s, { role }) => ({ ...s, activeRole: role })),
  on(UiActions.toggleSidenav,    s => ({ ...s, sidenavOpen: !s.sidenavOpen })),
  on(UiActions.setSidenavOpen,   (s, { open }) => ({ ...s, sidenavOpen: open })),
  on(UiActions.setGlobalLoading, (s, { loading }) => ({ ...s, globalLoading: loading }))
);

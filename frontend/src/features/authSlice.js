import { createSlice } from "@reduxjs/toolkit";

const STORAGE_KEY = "vrs_auth";

const initial = (() => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
})();

const slice = createSlice({
  name: "auth",
  initialState: {
    accessToken: initial?.accessToken || null,
    refreshToken: initial?.refreshToken || null,
    userId: initial?.userId || null,
    email: initial?.email || null,
    fullName: initial?.fullName || null,
    roles: initial?.roles || [],
  },
  reducers: {
    setCredentials: (state, action) => {
      const { accessToken, refreshToken, userId, email, fullName, roles } = action.payload;
      state.accessToken = accessToken;
      state.refreshToken = refreshToken;
      state.userId = userId;
      state.email = email;
      state.fullName = fullName;
      state.roles = roles || [];
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    },
    logout: (state) => {
      state.accessToken = null;
      state.refreshToken = null;
      state.userId = null;
      state.email = null;
      state.fullName = null;
      state.roles = [];
      localStorage.removeItem(STORAGE_KEY);
    },
  },
});

export const { setCredentials, logout } = slice.actions;
export default slice.reducer;

export const selectIsAuthenticated = (state) => Boolean(state.auth.accessToken);
export const selectIsAdmin = (state) => (state.auth.roles || []).includes("ROLE_ADMIN");
export const selectUser = (state) => state.auth;

import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./features/authSlice.js";
import { api } from "./api/api.js";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    [api.reducerPath]: api.reducer,
  },
  middleware: (gDM) => gDM().concat(api.middleware),
});

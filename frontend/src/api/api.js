import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { logout, setCredentials } from "../features/authSlice.js";

const baseQuery = fetchBaseQuery({
  baseUrl: "/api",
  prepareHeaders: (headers, { getState }) => {
    const token = getState().auth.accessToken;
    if (token) headers.set("Authorization", `Bearer ${token}`);
    headers.set("Content-Type", "application/json");
    return headers;
  },
});

const baseQueryWithReauth = async (args, apiCtx, extraOptions) => {
  let result = await baseQuery(args, apiCtx, extraOptions);
  if (result.error && result.error.status === 401) {
    const refreshToken = apiCtx.getState().auth.refreshToken;
    if (refreshToken) {
      const refreshResult = await baseQuery(
        { url: "/auth/refresh", method: "POST", body: { refreshToken } },
        apiCtx,
        extraOptions
      );
      if (refreshResult.data) {
        apiCtx.dispatch(setCredentials(refreshResult.data));
        result = await baseQuery(args, apiCtx, extraOptions);
      } else {
        apiCtx.dispatch(logout());
      }
    } else {
      apiCtx.dispatch(logout());
    }
  }
  return result;
};

export const api = createApi({
  reducerPath: "api",
  baseQuery: baseQueryWithReauth,
  tagTypes: ["Vehicle", "Booking", "Payment", "Notification", "User"],
  endpoints: (builder) => ({
    // Auth
    login: builder.mutation({
      query: (body) => ({ url: "/auth/login", method: "POST", body }),
    }),
    register: builder.mutation({
      query: (body) => ({ url: "/auth/register", method: "POST", body }),
    }),
    me: builder.query({ query: () => "/auth/me" }),

    // Vehicles
    listVehicles: builder.query({
      query: (params) => ({ url: "/vehicles", params }),
      providesTags: ["Vehicle"],
    }),
    getVehicle: builder.query({
      query: (id) => `/vehicles/${id}`,
      providesTags: ["Vehicle"],
    }),
    checkAvailability: builder.query({
      query: ({ id, pickupAt, dropoffAt }) => ({
        url: `/vehicles/${id}/availability`,
        params: { pickupAt, dropoffAt },
      }),
    }),
    createVehicle: builder.mutation({
      query: (body) => ({ url: "/vehicles", method: "POST", body }),
      invalidatesTags: ["Vehicle"],
    }),
    updateVehicle: builder.mutation({
      query: ({ id, body }) => ({ url: `/vehicles/${id}`, method: "PUT", body }),
      invalidatesTags: ["Vehicle"],
    }),
    deleteVehicle: builder.mutation({
      query: (id) => ({ url: `/vehicles/${id}`, method: "DELETE" }),
      invalidatesTags: ["Vehicle"],
    }),
    updateVehicleStatus: builder.mutation({
      query: ({ id, status }) => ({
        url: `/vehicles/${id}/status`,
        method: "PATCH",
        params: { status },
      }),
      invalidatesTags: ["Vehicle"],
    }),

    // Bookings
    createBooking: builder.mutation({
      query: (body) => ({ url: "/bookings", method: "POST", body }),
      invalidatesTags: ["Booking"],
    }),
    myBookings: builder.query({
      query: (params) => ({ url: "/bookings/me", params }),
      providesTags: ["Booking"],
    }),
    getBooking: builder.query({
      query: (id) => `/bookings/${id}`,
      providesTags: ["Booking"],
    }),
    cancelBooking: builder.mutation({
      query: ({ id, reason }) => ({ url: `/bookings/${id}/cancel`, method: "POST", body: { reason } }),
      invalidatesTags: ["Booking"],
    }),
    allBookings: builder.query({
      query: (params) => ({ url: "/bookings", params }),
      providesTags: ["Booking"],
    }),

    // Payments
    createOrder: builder.mutation({
      query: (body) => ({ url: "/payments/order", method: "POST", body }),
    }),
    verifyPayment: builder.mutation({
      query: (body) => ({ url: "/payments/verify", method: "POST", body }),
      invalidatesTags: ["Booking", "Payment"],
    }),
    failPayment: builder.mutation({
      query: ({ id, reason }) => ({ url: `/payments/${id}/fail`, method: "POST", body: { reason } }),
      invalidatesTags: ["Booking", "Payment"],
    }),

    // Notifications
    myNotifications: builder.query({
      query: (params) => ({ url: "/notifications/me", params }),
      providesTags: ["Notification"],
    }),
    unreadCount: builder.query({
      query: () => "/notifications/me/unread-count",
      providesTags: ["Notification"],
    }),
    markAllRead: builder.mutation({
      query: () => ({ url: "/notifications/me/read-all", method: "POST" }),
      invalidatesTags: ["Notification"],
    }),
  }),
});

export const {
  useLoginMutation,
  useRegisterMutation,
  useMeQuery,
  useListVehiclesQuery,
  useGetVehicleQuery,
  useLazyCheckAvailabilityQuery,
  useCreateVehicleMutation,
  useUpdateVehicleMutation,
  useDeleteVehicleMutation,
  useUpdateVehicleStatusMutation,
  useCreateBookingMutation,
  useMyBookingsQuery,
  useGetBookingQuery,
  useCancelBookingMutation,
  useAllBookingsQuery,
  useCreateOrderMutation,
  useVerifyPaymentMutation,
  useFailPaymentMutation,
  useMyNotificationsQuery,
  useUnreadCountQuery,
  useMarkAllReadMutation,
} = api;

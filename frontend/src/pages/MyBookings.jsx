import { useState } from "react";
import { Link } from "react-router-dom";
import toast from "react-hot-toast";
import { useMyBookingsQuery, useCancelBookingMutation } from "../api/api.js";
import { Calendar, MapPin, CreditCard, X, Eye } from "lucide-react";

const STATUS_STYLES = {
  PENDING_PAYMENT: "bg-yellow-100 text-yellow-800",
  CONFIRMED: "bg-green-100 text-green-800",
  CANCELLED: "bg-red-100 text-red-800",
  COMPLETED: "bg-blue-100 text-blue-800",
  PAYMENT_FAILED: "bg-red-100 text-red-800",
};

export default function MyBookings() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useMyBookingsQuery({ page, size: 10, sort: "createdAt,desc" });
  const [cancel, { isLoading: canceling }] = useCancelBookingMutation();

  const handleCancel = async (id) => {
    if (!confirm("Cancel this booking?")) return;
    try {
      await cancel({ id, reason: "User cancelled" }).unwrap();
      toast.success("Booking cancelled");
    } catch (err) {
      toast.error(err?.data?.message || "Cancel failed");
    }
  };

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold mb-6">My Bookings</h1>
      {isLoading ? (
        <div className="text-center py-12 text-gray-500">Loading...</div>
      ) : data?.content?.length === 0 ? (
        <div className="card p-12 text-center">
          <p className="text-gray-600">You have no bookings yet.</p>
          <Link to="/vehicles" className="btn-primary mt-4">Browse Vehicles</Link>
        </div>
      ) : (
        <div className="space-y-4">
          {data?.content.map((b) => (
            <div key={b.id} className="card p-5">
              <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
                <div>
                  <div className="flex items-center gap-3">
                    <h3 className="font-semibold text-lg">{b.vehicleName}</h3>
                    <span className={`badge ${STATUS_STYLES[b.status] || "bg-gray-100 text-gray-700"}`}>
                      {b.status.replace("_", " ")}
                    </span>
                  </div>
                  <p className="text-sm text-gray-500 mt-1">Booking #{b.id} &middot; ₹{Number(b.totalAmount).toFixed(0)} &middot; {b.rentalDays} day(s)</p>
                  <div className="mt-2 grid grid-cols-1 md:grid-cols-2 gap-2 text-sm text-gray-600">
                    <div className="inline-flex items-center gap-2"><Calendar className="w-4 h-4" />Pickup: {new Date(b.pickupAt).toLocaleString()}</div>
                    <div className="inline-flex items-center gap-2"><Calendar className="w-4 h-4" />Drop-off: {new Date(b.dropoffAt).toLocaleString()}</div>
                    <div className="inline-flex items-center gap-2"><MapPin className="w-4 h-4" />From: {b.pickupLocation}</div>
                    <div className="inline-flex items-center gap-2"><MapPin className="w-4 h-4" />To: {b.dropoffLocation}</div>
                  </div>
                </div>
                <div className="flex md:flex-col gap-2 md:w-48">
                  {b.status === "PENDING_PAYMENT" && (
                    <Link to={`/payment/${b.id}`} className="btn-primary w-full justify-center">
                      <CreditCard className="w-4 h-4" /> Pay Now
                    </Link>
                  )}
                  {(b.status === "PENDING_PAYMENT" || b.status === "CONFIRMED") && (
                    <button onClick={() => handleCancel(b.id)} disabled={canceling} className="btn-danger w-full justify-center">
                      <X className="w-4 h-4" /> Cancel
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
          {data?.totalPages > 1 && (
            <div className="flex justify-center gap-2 mt-6">
              <button className="btn-secondary" disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
              <span className="px-4 py-2 text-sm">Page {page + 1} of {data.totalPages}</span>
              <button className="btn-secondary" disabled={page + 1 >= data.totalPages} onClick={() => setPage(page + 1)}>Next</button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

import { useState } from "react";
import { useAllBookingsQuery } from "../api/api.js";

const STATUSES = ["", "PENDING_PAYMENT", "CONFIRMED", "CANCELLED", "COMPLETED", "PAYMENT_FAILED"];

const STATUS_STYLES = {
  PENDING_PAYMENT: "bg-yellow-100 text-yellow-800",
  CONFIRMED: "bg-green-100 text-green-800",
  CANCELLED: "bg-red-100 text-red-800",
  COMPLETED: "bg-blue-100 text-blue-800",
  PAYMENT_FAILED: "bg-red-100 text-red-800",
};

export default function AdminBookings() {
  const [status, setStatus] = useState("");
  const [page, setPage] = useState(0);
  const params = { page, size: 20, sort: "createdAt,desc" };
  if (status) params.status = status;
  const { data, isLoading } = useAllBookingsQuery(params);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold mb-6">All Bookings</h1>
      <div className="card p-4 mb-4 flex items-center gap-3">
        <span className="text-sm text-gray-600">Filter by status:</span>
        <select className="input max-w-xs" value={status} onChange={(e) => { setStatus(e.target.value); setPage(0); }}>
          {STATUSES.map((s) => <option key={s} value={s}>{s || "All"}</option>)}
        </select>
        <span className="ml-auto text-sm text-gray-500">Total: {data?.totalElements ?? "..."}</span>
      </div>

      <div className="card overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-left text-xs uppercase text-gray-500">
            <tr>
              <th className="p-3">ID</th><th className="p-3">Vehicle</th><th className="p-3">User</th>
              <th className="p-3">Pickup</th><th className="p-3">Drop-off</th><th className="p-3">Total</th><th className="p-3">Status</th>
            </tr>
          </thead>
          <tbody>
            {isLoading ? <tr><td colSpan="7" className="p-6 text-center text-gray-500">Loading...</td></tr> :
              data?.content.map((b) => (
                <tr key={b.id} className="border-t border-gray-100">
                  <td className="p-3 font-medium">#{b.id}</td>
                  <td className="p-3">{b.vehicleName}</td>
                  <td className="p-3 text-xs">{b.userEmail}</td>
                  <td className="p-3 text-xs">{new Date(b.pickupAt).toLocaleString()}</td>
                  <td className="p-3 text-xs">{new Date(b.dropoffAt).toLocaleString()}</td>
                  <td className="p-3">₹{Number(b.totalAmount).toFixed(0)}</td>
                  <td className="p-3"><span className={`badge ${STATUS_STYLES[b.status] || "bg-gray-100 text-gray-700"}`}>{b.status.replace("_", " ")}</span></td>
                </tr>
              ))}
          </tbody>
        </table>
      </div>

      {data?.totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-4">
          <button className="btn-secondary" disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
          <span className="px-4 py-2 text-sm">Page {page + 1} of {data.totalPages}</span>
          <button className="btn-secondary" disabled={page + 1 >= data.totalPages} onClick={() => setPage(page + 1)}>Next</button>
        </div>
      )}
    </div>
  );
}

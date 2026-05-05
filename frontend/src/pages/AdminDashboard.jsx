import { Link } from "react-router-dom";
import { useListVehiclesQuery, useAllBookingsQuery } from "../api/api.js";
import { Car, Calendar, Users, ShieldCheck } from "lucide-react";

export default function AdminDashboard() {
  const { data: vehicles } = useListVehiclesQuery({ size: 1 });
  const { data: bookings } = useAllBookingsQuery({ size: 1 });
  const { data: confirmed } = useAllBookingsQuery({ size: 1, status: "CONFIRMED" });
  const { data: pending } = useAllBookingsQuery({ size: 1, status: "PENDING_PAYMENT" });

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold mb-6 flex items-center gap-2">
        <ShieldCheck className="w-6 h-6 text-brand-600" /> Admin Dashboard
      </h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <Stat icon={Car} color="bg-blue-100 text-blue-700" label="Total Vehicles" value={vehicles?.totalElements ?? "..."} />
        <Stat icon={Calendar} color="bg-green-100 text-green-700" label="Total Bookings" value={bookings?.totalElements ?? "..."} />
        <Stat icon={Calendar} color="bg-emerald-100 text-emerald-700" label="Confirmed" value={confirmed?.totalElements ?? "..."} />
        <Stat icon={Calendar} color="bg-yellow-100 text-yellow-700" label="Pending Payment" value={pending?.totalElements ?? "..."} />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Link to="/admin/vehicles" className="card p-6 hover:shadow-lg transition-shadow">
          <Car className="w-8 h-8 text-brand-600" />
          <h3 className="mt-2 font-semibold text-lg">Manage Vehicles</h3>
          <p className="text-sm text-gray-600">Create, edit, or remove fleet vehicles.</p>
        </Link>
        <Link to="/admin/bookings" className="card p-6 hover:shadow-lg transition-shadow">
          <Calendar className="w-8 h-8 text-brand-600" />
          <h3 className="mt-2 font-semibold text-lg">All Bookings</h3>
          <p className="text-sm text-gray-600">Review every booking across users.</p>
        </Link>
      </div>
    </div>
  );
}

function Stat({ icon: Icon, color, label, value }) {
  return (
    <div className="card p-5">
      <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${color}`}>
        <Icon className="w-5 h-5" />
      </div>
      <div className="mt-3 text-2xl font-bold">{value}</div>
      <div className="text-sm text-gray-600">{label}</div>
    </div>
  );
}

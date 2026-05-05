import { Link, useParams } from "react-router-dom";
import { useGetVehicleQuery } from "../api/api.js";
import { useSelector } from "react-redux";
import { selectIsAuthenticated } from "../features/authSlice.js";
import { Users, Fuel, Settings, MapPin, Calendar, Star, ArrowLeft } from "lucide-react";

export default function VehicleDetail() {
  const { id } = useParams();
  const { data: v, isLoading } = useGetVehicleQuery(id);
  const authed = useSelector(selectIsAuthenticated);

  if (isLoading) return <div className="text-center py-20 text-gray-500">Loading...</div>;
  if (!v) return <div className="text-center py-20 text-gray-500">Vehicle not found.</div>;

  return (
    <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <Link to="/vehicles" className="inline-flex items-center gap-1 text-sm text-brand-600 hover:text-brand-700 dark:text-brand-400 dark:hover:text-brand-300 mb-4">
        <ArrowLeft className="w-4 h-4" /> Back to Vehicles
      </Link>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div className="card overflow-hidden">
          <img src={v.imageUrl || "https://placehold.co/800x500?text=No+Image"} alt={v.name} className="w-full aspect-[4/3] object-cover" />
        </div>
        <div className="space-y-6">
          <div>
            <div className="flex items-start justify-between">
              <div>
                <h1 className="text-3xl font-bold">{v.name}</h1>
                <p className="text-gray-600">{v.brand} &middot; {v.yearMade} &middot; {v.type}</p>
              </div>
              <span className="badge bg-yellow-100 text-yellow-800 inline-flex items-center gap-1 text-sm">
                <Star className="w-4 h-4 fill-current" /> {v.rating}
              </span>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Spec icon={Users} label="Seats" value={v.seats} />
            <Spec icon={Settings} label="Transmission" value={v.transmission} />
            <Spec icon={Fuel} label="Fuel" value={v.fuel} />
            <Spec icon={MapPin} label="Location" value={v.location} />
          </div>

          {v.description && (
            <div className="card p-4">
              <h3 className="font-semibold mb-1">About</h3>
              <p className="text-sm text-gray-600">{v.description}</p>
            </div>
          )}

          <div className="card p-6 bg-gradient-to-br from-brand-50 to-white border-brand-100">
            <div className="flex justify-between items-end">
              <div>
                <p className="text-sm text-gray-600">Per day</p>
                <div className="text-3xl font-extrabold text-brand-700">&#8377;{Number(v.pricePerDay).toFixed(0)}</div>
              </div>
              <span className={`badge ${v.status === "AVAILABLE" ? "bg-green-100 text-green-800" : "bg-gray-100 text-gray-700"}`}>
                {v.status}
              </span>
            </div>
            {v.status === "AVAILABLE" ? (
              authed ? (
                <Link to={`/book/${v.id}`} className="btn-primary w-full mt-4 justify-center">
                  <Calendar className="w-4 h-4" /> Book Now
                </Link>
              ) : (
                <Link to="/login" className="btn-primary w-full mt-4 justify-center">Login to Book</Link>
              )
            ) : (
              <button disabled className="btn-secondary w-full mt-4 justify-center">Currently Unavailable</button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

function Spec({ icon: Icon, label, value }) {
  return (
    <div className="card p-3 flex items-center gap-3">
      <Icon className="w-5 h-5 text-brand-600" />
      <div>
        <div className="text-xs text-gray-500">{label}</div>
        <div className="font-medium">{value}</div>
      </div>
    </div>
  );
}

import { Link } from "react-router-dom";
import { Users, Fuel, Settings, MapPin, Star } from "lucide-react";

export default function VehicleCard({ v }) {
  return (
    <Link to={`/vehicles/${v.id}`} className="card overflow-hidden hover:shadow-lg transition-shadow group">
      <div className="aspect-[16/10] bg-gray-100 overflow-hidden">
        <img
          src={v.imageUrl || "https://placehold.co/640x400?text=No+Image"}
          alt={v.name}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform"
        />
      </div>
      <div className="p-4 space-y-2">
        <div className="flex justify-between items-start">
          <div>
            <h3 className="font-semibold text-gray-900">{v.name}</h3>
            <p className="text-xs text-gray-500">{v.brand} &middot; {v.yearMade}</p>
          </div>
          <span className="badge bg-yellow-100 text-yellow-800 inline-flex items-center gap-1">
            <Star className="w-3 h-3 fill-current" />
            {v.rating}
          </span>
        </div>
        <div className="flex flex-wrap gap-3 text-xs text-gray-600">
          <span className="inline-flex items-center gap-1"><Users className="w-3.5 h-3.5" /> {v.seats}</span>
          <span className="inline-flex items-center gap-1"><Settings className="w-3.5 h-3.5" /> {v.transmission}</span>
          <span className="inline-flex items-center gap-1"><Fuel className="w-3.5 h-3.5" /> {v.fuel}</span>
          <span className="inline-flex items-center gap-1"><MapPin className="w-3.5 h-3.5" /> {v.location}</span>
        </div>
        <div className="flex items-center justify-between pt-2 border-t border-gray-100">
          <div>
            <span className="text-xl font-bold text-brand-700">&#8377;{Number(v.pricePerDay).toFixed(0)}</span>
            <span className="text-xs text-gray-500"> /day</span>
          </div>
          <span className={`badge ${v.status === "AVAILABLE" ? "bg-green-100 text-green-800" : "bg-gray-100 text-gray-700"}`}>
            {v.status}
          </span>
        </div>
      </div>
    </Link>
  );
}

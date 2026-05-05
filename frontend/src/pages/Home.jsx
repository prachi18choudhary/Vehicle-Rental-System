import { Link } from "react-router-dom";
import { useListVehiclesQuery } from "../api/api.js";
import VehicleCard from "../components/VehicleCard.jsx";
import { Search, ShieldCheck, Zap, CreditCard } from "lucide-react";

export default function Home() {
  const { data, isLoading } = useListVehiclesQuery({ size: 6 });
  const vehicles = data?.content || [];

  return (
    <>
      <section className="bg-gradient-to-br from-brand-700 via-brand-600 to-indigo-500 text-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 lg:py-28">
          <div className="max-w-2xl">
            <h1 className="text-4xl lg:text-5xl font-extrabold leading-tight">
              Drive your way. <br />
              Rent any vehicle, anywhere.
            </h1>
            <p className="mt-4 text-lg text-brand-100">
              Browse hundreds of cars, bikes, and SUVs. Book in seconds, pay securely, and hit the road.
            </p>
            <div className="mt-8 flex gap-3">
              <Link to="/vehicles" className="btn bg-white text-brand-700 hover:bg-gray-100 font-semibold px-6">
                <Search className="w-4 h-4" /> Browse Vehicles
              </Link>
              <Link to="/register" className="btn bg-white/10 text-white border border-white/30 hover:bg-white/20 px-6">
                Sign Up Free
              </Link>
            </div>
          </div>
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {[
            { icon: Zap, title: "Instant Booking", desc: "Reserve any vehicle in seconds with our streamlined flow." },
            { icon: ShieldCheck, title: "Secure Payments", desc: "Razorpay-powered checkout. Your data is always safe." },
            { icon: CreditCard, title: "Flexible Plans", desc: "Hourly, daily, or weekly — pay only for what you need." },
          ].map((f, i) => (
            <div key={i} className="card p-6">
              <f.icon className="w-8 h-8 text-brand-600" />
              <h3 className="mt-3 font-semibold text-lg">{f.title}</h3>
              <p className="mt-1 text-sm text-gray-600">{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-16">
        <div className="flex justify-between items-end mb-6">
          <div>
            <h2 className="text-2xl font-bold">Featured Vehicles</h2>
            <p className="text-gray-600 text-sm">Top-rated rides ready for you.</p>
          </div>
          <Link to="/vehicles" className="text-brand-600 hover:text-brand-700 text-sm font-medium">View all &rarr;</Link>
        </div>
        {isLoading ? (
          <div className="text-center text-gray-500 py-12">Loading...</div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {vehicles.map((v) => <VehicleCard key={v.id} v={v} />)}
          </div>
        )}
      </section>
    </>
  );
}

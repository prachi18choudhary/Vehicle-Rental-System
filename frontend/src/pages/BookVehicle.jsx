import { useState, useEffect } from "react";
import { useNavigate, useParams, Link } from "react-router-dom";
import DatePicker from "react-datepicker";
import { useGetVehicleQuery, useLazyCheckAvailabilityQuery, useCreateBookingMutation } from "../api/api.js";
import toast from "react-hot-toast";
import { Calendar, MapPin, ArrowLeft } from "lucide-react";

function toIso(date) {
  if (!date) return null;
  const tz = -date.getTimezoneOffset() * 60000;
  return new Date(date.getTime() + tz).toISOString().slice(0, 19);
}

export default function BookVehicle() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { data: vehicle } = useGetVehicleQuery(id);
  const [checkAvail, { data: avail, isFetching: checking }] = useLazyCheckAvailabilityQuery();
  const [createBooking, { isLoading: creating }] = useCreateBookingMutation();

  const tomorrow = new Date(Date.now() + 24 * 3600 * 1000);
  const dayAfter = new Date(Date.now() + 48 * 3600 * 1000);
  const [pickupAt, setPickupAt] = useState(tomorrow);
  const [dropoffAt, setDropoffAt] = useState(dayAfter);
  const [pickupLocation, setPickupLocation] = useState("");
  const [dropoffLocation, setDropoffLocation] = useState("");

  useEffect(() => {
    if (vehicle) {
      setPickupLocation(vehicle.location);
      setDropoffLocation(vehicle.location);
    }
  }, [vehicle]);

  useEffect(() => {
    if (id && pickupAt && dropoffAt && dropoffAt > pickupAt) {
      checkAvail({ id, pickupAt: toIso(pickupAt), dropoffAt: toIso(dropoffAt) });
    }
  }, [id, pickupAt, dropoffAt, checkAvail]);

  const submit = async (e) => {
    e.preventDefault();
    try {
      const booking = await createBooking({
        vehicleId: Number(id),
        pickupAt: toIso(pickupAt),
        dropoffAt: toIso(dropoffAt),
        pickupLocation,
        dropoffLocation,
      }).unwrap();
      toast.success("Booking created! Proceed to payment.");
      navigate(`/payment/${booking.id}`);
    } catch (err) {
      toast.error(err?.data?.message || "Booking failed");
    }
  };

  if (!vehicle) return <div className="text-center py-20 text-gray-500">Loading...</div>;

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <Link to={`/vehicles/${id}`} className="inline-flex items-center gap-1 text-sm text-brand-600 hover:text-brand-700 dark:text-brand-400 dark:hover:text-brand-300 mb-4">
        <ArrowLeft className="w-4 h-4" /> Back to Vehicle
      </Link>
      <h1 className="text-2xl font-bold mb-6 flex items-center gap-2">
        <Calendar className="w-6 h-6 text-brand-600" /> Book {vehicle.name}
      </h1>
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <form onSubmit={submit} className="lg:col-span-2 card p-6 space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="text-sm font-medium text-gray-700">Pickup Date &amp; Time</label>
              <DatePicker
                selected={pickupAt}
                onChange={setPickupAt}
                showTimeSelect
                dateFormat="Pp"
                minDate={new Date()}
                className="input mt-1"
              />
            </div>
            <div>
              <label className="text-sm font-medium text-gray-700">Drop-off Date &amp; Time</label>
              <DatePicker
                selected={dropoffAt}
                onChange={setDropoffAt}
                showTimeSelect
                dateFormat="Pp"
                minDate={pickupAt}
                className="input mt-1"
              />
            </div>
          </div>
          <div>
            <label className="text-sm font-medium text-gray-700">Pickup Location</label>
            <div className="relative mt-1">
              <MapPin className="w-4 h-4 absolute left-3 top-3 text-gray-400" />
              <input className="input pl-9" required value={pickupLocation} onChange={(e) => setPickupLocation(e.target.value)} />
            </div>
          </div>
          <div>
            <label className="text-sm font-medium text-gray-700">Drop-off Location</label>
            <div className="relative mt-1">
              <MapPin className="w-4 h-4 absolute left-3 top-3 text-gray-400" />
              <input className="input pl-9" required value={dropoffLocation} onChange={(e) => setDropoffLocation(e.target.value)} />
            </div>
          </div>
          <button type="submit" className="btn-primary w-full" disabled={creating || !avail?.available}>
            {creating ? "Creating booking..." : "Continue to Payment"}
          </button>
        </form>

        <aside className="card p-6 h-fit">
          <h3 className="font-semibold mb-3">Price Summary</h3>
          {checking ? (
            <p className="text-sm text-gray-500">Checking availability...</p>
          ) : avail ? (
            avail.available ? (
              <div className="space-y-2">
                <Row label="Per day" value={`₹${Number(avail.pricePerDay).toFixed(0)}`} />
                <Row label="Days" value={avail.days} />
                <hr />
                <Row label="Total" value={`₹${Number(avail.totalAmount).toFixed(0)}`} bold />
                <p className="text-xs text-green-600 mt-2">Available for selected dates.</p>
              </div>
            ) : (
              <p className="text-sm text-red-600">{avail.reason || "Not available"}</p>
            )
          ) : (
            <p className="text-sm text-gray-500">Pick dates to see total.</p>
          )}
        </aside>
      </div>
    </div>
  );
}

function Row({ label, value, bold }) {
  return (
    <div className="flex justify-between text-sm">
      <span className="text-gray-600">{label}</span>
      <span className={bold ? "font-bold text-lg" : ""}>{value}</span>
    </div>
  );
}

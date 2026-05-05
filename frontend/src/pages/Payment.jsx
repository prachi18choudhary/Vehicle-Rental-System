import { useEffect, useState } from "react";
import { useNavigate, useParams, Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import toast from "react-hot-toast";
import { useGetBookingQuery, useCreateOrderMutation, useVerifyPaymentMutation, useFailPaymentMutation, api } from "../api/api.js";
import { selectUser } from "../features/authSlice.js";
import { CreditCard, ShieldCheck, ArrowLeft } from "lucide-react";

export default function Payment() {
  const { bookingId } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const user = useSelector(selectUser);
  const { data: booking, isLoading } = useGetBookingQuery(bookingId);
  const [createOrder, { isLoading: creating }] = useCreateOrderMutation();
  const [verifyPayment] = useVerifyPaymentMutation();
  const [failPayment] = useFailPaymentMutation();
  const [paying, setPaying] = useState(false);

  const pay = async () => {
    if (!booking) return;
    if (!window.Razorpay) {
      toast.error("Razorpay SDK not loaded");
      return;
    }
    try {
      setPaying(true);
      const order = await createOrder({ bookingId: Number(bookingId), amount: booking.totalAmount }).unwrap();
      const options = {
        key: order.razorpayKeyId,
        amount: Math.round(Number(booking.totalAmount) * 100),
        currency: order.currency,
        name: "Vehicle Rental System",
        description: `Booking #${bookingId}`,
        order_id: order.razorpayOrderId,
        prefill: { email: user.email, name: user.fullName },
        theme: { color: "#4f46e5" },
        handler: async (resp) => {
          try {
            await verifyPayment({
              razorpayOrderId: resp.razorpay_order_id,
              razorpayPaymentId: resp.razorpay_payment_id,
              razorpaySignature: resp.razorpay_signature,
            }).unwrap();
            toast.success("Payment successful! Booking confirmed.");
            dispatch(api.util.invalidateTags(["Booking"]));
            navigate("/my-bookings");
          } catch (err) {
            toast.error(err?.data?.message || "Verification failed");
          }
        },
        modal: {
          ondismiss: () => {
            failPayment({ id: order.paymentId, reason: "User cancelled checkout" });
            toast("Payment cancelled.", { icon: "ℹ️" });
            setPaying(false);
          },
        },
      };
      const rzp = new window.Razorpay(options);
      rzp.on("payment.failed", (resp) => {
        failPayment({ id: order.paymentId, reason: resp?.error?.description || "Payment failed" });
        toast.error("Payment failed");
        setPaying(false);
      });
      rzp.open();
    } catch (err) {
      toast.error(err?.data?.message || "Could not initiate payment");
      setPaying(false);
    }
  };

  if (isLoading) return <div className="text-center py-20 text-gray-500">Loading...</div>;
  if (!booking) return <div className="text-center py-20 text-gray-500">Booking not found.</div>;

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <Link to="/my-bookings" className="inline-flex items-center gap-1 text-sm text-brand-600 hover:text-brand-700 dark:text-brand-400 dark:hover:text-brand-300 mb-4">
        <ArrowLeft className="w-4 h-4" /> Back to My Bookings
      </Link>
      <h1 className="text-2xl font-bold mb-6 flex items-center gap-2">
        <CreditCard className="w-6 h-6 text-brand-600" /> Complete Payment
      </h1>
      <div className="card p-6 space-y-3">
        <h3 className="font-semibold text-lg">Booking #{booking.id}</h3>
        <Row label="Vehicle" value={booking.vehicleName} />
        <Row label="Pickup" value={new Date(booking.pickupAt).toLocaleString()} />
        <Row label="Drop-off" value={new Date(booking.dropoffAt).toLocaleString()} />
        <Row label="Days" value={booking.rentalDays} />
        <hr />
        <Row label="Total" value={`₹${Number(booking.totalAmount).toFixed(0)}`} bold />
        <div className="pt-4">
          <button onClick={pay} disabled={paying || creating || booking.status !== "PENDING_PAYMENT"} className="btn-primary w-full justify-center">
            {paying || creating ? "Processing..." : `Pay ₹${Number(booking.totalAmount).toFixed(0)} with Razorpay`}
          </button>
          <p className="text-xs text-gray-500 mt-2 inline-flex items-center gap-1">
            <ShieldCheck className="w-3 h-3" /> Secure test payment via Razorpay. Use test card: 4111 1111 1111 1111
          </p>
        </div>
      </div>
    </div>
  );
}

function Row({ label, value, bold }) {
  return (
    <div className="flex justify-between text-sm">
      <span className="text-gray-600">{label}</span>
      <span className={bold ? "font-bold text-lg" : "font-medium"}>{value}</span>
    </div>
  );
}

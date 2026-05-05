import { Routes, Route, Navigate } from "react-router-dom";
import { useEffect } from "react";
import { useSelector } from "react-redux";
import { selectIsAuthenticated, selectIsAdmin, selectUser } from "./features/authSlice.js";
import Navbar from "./components/Navbar.jsx";
import Footer from "./components/Footer.jsx";
import Home from "./pages/Home.jsx";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import Vehicles from "./pages/Vehicles.jsx";
import VehicleDetail from "./pages/VehicleDetail.jsx";
import BookVehicle from "./pages/BookVehicle.jsx";
import Payment from "./pages/Payment.jsx";
import MyBookings from "./pages/MyBookings.jsx";
import Notifications from "./pages/Notifications.jsx";
import AdminDashboard from "./pages/AdminDashboard.jsx";
import AdminVehicles from "./pages/AdminVehicles.jsx";
import AdminBookings from "./pages/AdminBookings.jsx";
import { connectStomp, disconnectStomp } from "./ws/stompClient.js";
import toast from "react-hot-toast";
import { api } from "./api/api.js";
import { useDispatch } from "react-redux";

function ProtectedRoute({ children }) {
  const authed = useSelector(selectIsAuthenticated);
  return authed ? children : <Navigate to="/login" replace />;
}

function AdminRoute({ children }) {
  const authed = useSelector(selectIsAuthenticated);
  const admin = useSelector(selectIsAdmin);
  if (!authed) return <Navigate to="/login" replace />;
  if (!admin) return <Navigate to="/" replace />;
  return children;
}

export default function App() {
  const user = useSelector(selectUser);
  const dispatch = useDispatch();

  useEffect(() => {
    if (user.accessToken && user.userId) {
      connectStomp({
        token: user.accessToken,
        userId: user.userId,
        onMessage: (n) => {
          toast.success(`${n.title}: ${n.message}`, { duration: 5000 });
          dispatch(api.util.invalidateTags(["Notification", "Booking"]));
        },
      });
    }
    return () => disconnectStomp();
  }, [user.accessToken, user.userId, dispatch]);

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/vehicles" element={<Vehicles />} />
          <Route path="/vehicles/:id" element={<VehicleDetail />} />
          <Route path="/book/:id" element={<ProtectedRoute><BookVehicle /></ProtectedRoute>} />
          <Route path="/payment/:bookingId" element={<ProtectedRoute><Payment /></ProtectedRoute>} />
          <Route path="/my-bookings" element={<ProtectedRoute><MyBookings /></ProtectedRoute>} />
          <Route path="/notifications" element={<ProtectedRoute><Notifications /></ProtectedRoute>} />
          <Route path="/admin" element={<AdminRoute><AdminDashboard /></AdminRoute>} />
          <Route path="/admin/vehicles" element={<AdminRoute><AdminVehicles /></AdminRoute>} />
          <Route path="/admin/bookings" element={<AdminRoute><AdminBookings /></AdminRoute>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
      <Footer />
    </div>
  );
}

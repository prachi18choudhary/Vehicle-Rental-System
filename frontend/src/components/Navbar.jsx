import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { logout, selectIsAuthenticated, selectIsAdmin, selectUser } from "../features/authSlice.js";
import { Car, LogOut, User, ShieldCheck, Sun, Moon, Menu, X } from "lucide-react";
import NotificationBell from "./NotificationBell.jsx";
import useTheme from "../hooks/useTheme.js";

export default function Navbar() {
  const authed = useSelector(selectIsAuthenticated);
  const admin = useSelector(selectIsAdmin);
  const user = useSelector(selectUser);
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { isDark, toggle: toggleTheme } = useTheme();
  const [mobileOpen, setMobileOpen] = useState(false);

  const closeMobile = () => setMobileOpen(false);
  const handleLogout = () => { dispatch(logout()); navigate("/"); closeMobile(); };

  const navLinks = (
    <>
      <Link to="/" className="hover:text-brand-600 dark:hover:text-brand-400" onClick={closeMobile}>Home</Link>
      <Link to="/vehicles" className="hover:text-brand-600 dark:hover:text-brand-400" onClick={closeMobile}>Browse Vehicles</Link>
      {authed && <Link to="/my-bookings" className="hover:text-brand-600 dark:hover:text-brand-400" onClick={closeMobile}>My Bookings</Link>}
      {admin && <Link to="/admin" className="hover:text-brand-600 dark:hover:text-brand-400 inline-flex items-center gap-1" onClick={closeMobile}><ShieldCheck className="w-4 h-4" />Admin</Link>}
    </>
  );

  return (
    <header className="sticky top-0 z-50 bg-white/90 backdrop-blur border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Logo */}
        <Link to="/" className="flex items-center gap-2 font-bold text-lg text-brand-700 dark:text-brand-400">
          <Car className="w-6 h-6" />
          <span>VRS</span>
        </Link>

        {/* Desktop nav */}
        <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-gray-700">
          {navLinks}
        </nav>

        {/* Right side */}
        <div className="flex items-center gap-2">
          {/* Theme toggle */}
          <button
            onClick={toggleTheme}
            className="p-2 rounded-full hover:bg-gray-100 transition-colors"
            title={isDark ? "Switch to light mode" : "Switch to dark mode"}
          >
            {isDark ? <Sun className="w-5 h-5 text-yellow-400" /> : <Moon className="w-5 h-5 text-gray-600" />}
          </button>

          {authed ? (
            <>
              <NotificationBell />
              <div className="hidden md:flex items-center gap-2 px-3 py-1.5 bg-gray-100 rounded-lg text-sm">
                <User className="w-4 h-4 text-gray-500" />
                <span className="font-medium">{user.fullName || user.email}</span>
              </div>
              <button onClick={handleLogout} className="hidden md:inline-flex btn-secondary text-sm" title="Logout">
                <LogOut className="w-4 h-4" />
              </button>
            </>
          ) : (
            <div className="hidden md:flex items-center gap-2">
              <Link to="/login" className="btn-secondary text-sm">Login</Link>
              <Link to="/register" className="btn-primary text-sm">Sign Up</Link>
            </div>
          )}

          {/* Mobile hamburger */}
          <button
            onClick={() => setMobileOpen(!mobileOpen)}
            className="md:hidden p-2 rounded-lg hover:bg-gray-100 transition-colors"
            aria-label="Toggle menu"
          >
            {mobileOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>
      </div>

      {/* Mobile menu panel */}
      {mobileOpen && (
        <>
          {/* Backdrop */}
          <div className="fixed inset-0 top-16 bg-black/30 z-40 md:hidden" onClick={closeMobile} />

          {/* Panel */}
          <div className="absolute top-16 left-0 right-0 z-50 md:hidden bg-white border-b border-gray-200 shadow-lg">
            <nav className="flex flex-col gap-1 p-4 text-sm font-medium text-gray-700">
              {navLinks}
            </nav>
            <div className="border-t border-gray-200 p-4 flex flex-col gap-2">
              {authed ? (
                <>
                  <div className="flex items-center gap-2 px-3 py-2 bg-gray-100 rounded-lg text-sm">
                    <User className="w-4 h-4 text-gray-500" />
                    <span className="font-medium">{user.fullName || user.email}</span>
                  </div>
                  <button onClick={handleLogout} className="btn-danger text-sm w-full">
                    <LogOut className="w-4 h-4" /> Logout
                  </button>
                </>
              ) : (
                <>
                  <Link to="/login" className="btn-secondary text-sm w-full justify-center" onClick={closeMobile}>Login</Link>
                  <Link to="/register" className="btn-primary text-sm w-full justify-center" onClick={closeMobile}>Sign Up</Link>
                </>
              )}
            </div>
          </div>
        </>
      )}
    </header>
  );
}

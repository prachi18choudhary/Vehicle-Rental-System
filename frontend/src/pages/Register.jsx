import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import toast from "react-hot-toast";
import { useRegisterMutation } from "../api/api.js";
import { setCredentials } from "../features/authSlice.js";
import { UserPlus, ChevronDown, Eye, EyeOff } from "lucide-react";

const COUNTRIES = [
  { code: "+91", country: "IN", name: "India", maxLen: 10 },
  { code: "+1", country: "US", name: "United States", maxLen: 10 },
  { code: "+1", country: "CA", name: "Canada", maxLen: 10 },
  { code: "+44", country: "GB", name: "United Kingdom", maxLen: 10 },
  { code: "+61", country: "AU", name: "Australia", maxLen: 9 },
  { code: "+49", country: "DE", name: "Germany", maxLen: 11 },
  { code: "+33", country: "FR", name: "France", maxLen: 9 },
  { code: "+81", country: "JP", name: "Japan", maxLen: 10 },
  { code: "+86", country: "CN", name: "China", maxLen: 11 },
  { code: "+82", country: "KR", name: "South Korea", maxLen: 10 },
  { code: "+55", country: "BR", name: "Brazil", maxLen: 11 },
  { code: "+7", country: "RU", name: "Russia", maxLen: 10 },
  { code: "+27", country: "ZA", name: "South Africa", maxLen: 9 },
  { code: "+971", country: "AE", name: "UAE", maxLen: 9 },
  { code: "+966", country: "SA", name: "Saudi Arabia", maxLen: 9 },
  { code: "+65", country: "SG", name: "Singapore", maxLen: 8 },
  { code: "+60", country: "MY", name: "Malaysia", maxLen: 10 },
  { code: "+62", country: "ID", name: "Indonesia", maxLen: 12 },
  { code: "+39", country: "IT", name: "Italy", maxLen: 10 },
  { code: "+34", country: "ES", name: "Spain", maxLen: 9 },
  { code: "+52", country: "MX", name: "Mexico", maxLen: 10 },
  { code: "+234", country: "NG", name: "Nigeria", maxLen: 10 },
  { code: "+254", country: "KE", name: "Kenya", maxLen: 9 },
  { code: "+977", country: "NP", name: "Nepal", maxLen: 10 },
  { code: "+94", country: "LK", name: "Sri Lanka", maxLen: 9 },
  { code: "+880", country: "BD", name: "Bangladesh", maxLen: 10 },
  { code: "+92", country: "PK", name: "Pakistan", maxLen: 10 },
];

export default function Register() {
  const [form, setForm] = useState({ fullName: "", email: "", phone: "", password: "" });
  const [countryIdx, setCountryIdx] = useState(0); // default India
  const [errors, setErrors] = useState({});
  const [showPw, setShowPw] = useState(false);
  const [register, { isLoading }] = useRegisterMutation();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const selectedCountry = COUNTRIES[countryIdx];

  // --- Name: only letters, numbers, spaces ---
  const handleNameChange = (e) => {
    const cleaned = e.target.value.replace(/[^a-zA-Z0-9 ]/g, "");
    setForm({ ...form, fullName: cleaned });
    if (cleaned.trim().length < 2) {
      setErrors((prev) => ({ ...prev, fullName: "Name must be at least 2 characters" }));
    } else {
      setErrors((prev) => { const { fullName, ...rest } = prev; return rest; });
    }
  };

  // --- Email: validated on change ---
  const handleEmailChange = (e) => {
    const value = e.target.value;
    setForm({ ...form, email: value });
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (value && !emailRegex.test(value)) {
      setErrors((prev) => ({ ...prev, email: "Enter a valid email address" }));
    } else {
      setErrors((prev) => { const { email, ...rest } = prev; return rest; });
    }
  };

  // --- Phone: digits only ---
  const handlePhoneChange = (e) => {
    const digits = e.target.value.replace(/\D/g, "");
    const maxLen = selectedCountry.maxLen;
    const trimmed = digits.slice(0, maxLen);
    setForm({ ...form, phone: trimmed });
    if (trimmed && trimmed.length < 6) {
      setErrors((prev) => ({ ...prev, phone: "Phone number is too short" }));
    } else {
      setErrors((prev) => { const { phone, ...rest } = prev; return rest; });
    }
  };

  const handleCountryChange = (e) => {
    const idx = Number(e.target.value);
    setCountryIdx(idx);
    // Trim phone to new country's max length
    const maxLen = COUNTRIES[idx].maxLen;
    setForm((prev) => ({ ...prev, phone: prev.phone.slice(0, maxLen) }));
  };

  const handle = async (e) => {
    e.preventDefault();

    // Final validation before submit
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailRegex.test(form.email)) {
      toast.error("Please enter a valid email address");
      return;
    }
    if (form.fullName.trim().length < 2) {
      toast.error("Name must be at least 2 characters");
      return;
    }

    // Build phone with country code
    const fullPhone = form.phone ? `${selectedCountry.code}${form.phone}` : "";

    try {
      const result = await register({ ...form, phone: fullPhone }).unwrap();
      dispatch(setCredentials(result));
      toast.success("Account created!");
      navigate("/");
    } catch (err) {
      toast.error(err?.data?.message || "Registration failed");
    }
  };

  const hasErrors = Object.keys(errors).length > 0;

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4">
      <div className="card p-8 w-full max-w-md">
        <h1 className="text-2xl font-bold flex items-center gap-2"><UserPlus className="w-6 h-6 text-brand-600" /> Create your account</h1>
        <p className="text-gray-600 text-sm mt-1">Start renting in just a minute</p>
        <form onSubmit={handle} className="mt-6 space-y-4">
          {/* Full Name */}
          <div>
            <label className="text-sm font-medium text-gray-700">Full Name</label>
            <input
              className={`input mt-1 ${errors.fullName ? "border-red-400 focus:ring-red-400" : ""}`}
              required
              value={form.fullName}
              onChange={handleNameChange}
              placeholder="Letters and numbers only"
              autoComplete="name"
            />
            {errors.fullName && <p className="text-xs text-red-500 mt-1">{errors.fullName}</p>}
          </div>

          {/* Email */}
          <div>
            <label className="text-sm font-medium text-gray-700">Email</label>
            <input
              className={`input mt-1 ${errors.email ? "border-red-400 focus:ring-red-400" : ""}`}
              type="email"
              required
              value={form.email}
              onChange={handleEmailChange}
              placeholder="you@example.com"
              autoComplete="email"
            />
            {errors.email && <p className="text-xs text-red-500 mt-1">{errors.email}</p>}
          </div>

          {/* Phone with Country Code */}
          <div>
            <label className="text-sm font-medium text-gray-700">Phone</label>
            <div className="flex gap-2 mt-1">
              <select
                className="input w-auto min-w-[140px] pr-8 appearance-none bg-white"
                value={countryIdx}
                onChange={handleCountryChange}
              >
                {COUNTRIES.map((c, i) => (
                  <option key={`${c.country}-${i}`} value={i}>
                    {c.country} {c.code}
                  </option>
                ))}
              </select>
              <div className="relative flex-1">
                <input
                  className={`input ${errors.phone ? "border-red-400 focus:ring-red-400" : ""}`}
                  inputMode="numeric"
                  pattern="[0-9]*"
                  value={form.phone}
                  onChange={handlePhoneChange}
                  placeholder={`${"X".repeat(selectedCountry.maxLen)}`}
                  maxLength={selectedCountry.maxLen}
                  autoComplete="tel-national"
                />
              </div>
            </div>
            {errors.phone && <p className="text-xs text-red-500 mt-1">{errors.phone}</p>}
            {form.phone && !errors.phone && (
              <p className="text-xs text-gray-400 mt-1">
                Will be saved as {selectedCountry.code}{form.phone}
              </p>
            )}
          </div>

          {/* Password */}
          <div>
            <label className="text-sm font-medium text-gray-700">Password</label>
            <div className="relative mt-1">
              <input className="input pr-10" type={showPw ? "text" : "password"} required minLength={6} value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="Minimum 6 characters" autoComplete="new-password" />
              <button type="button" onClick={() => setShowPw(!showPw)} className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600" tabIndex={-1}>
                {showPw ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </div>

          <button className="btn-primary w-full" disabled={isLoading || hasErrors}>
            {isLoading ? "Creating..." : "Sign Up"}
          </button>
        </form>
        <p className="text-center text-sm text-gray-600 mt-6">
          Already have an account? <Link to="/login" className="text-brand-600 font-medium">Sign in</Link>
        </p>
      </div>
    </div>
  );
}

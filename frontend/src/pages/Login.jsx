import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import toast from "react-hot-toast";
import { useLoginMutation } from "../api/api.js";
import { setCredentials } from "../features/authSlice.js";
import { LogIn, Eye, EyeOff } from "lucide-react";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPw, setShowPw] = useState(false);
  const [login, { isLoading }] = useLoginMutation();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handle = async (e) => {
    e.preventDefault();
    try {
      const result = await login({ email, password }).unwrap();
      dispatch(setCredentials(result));
      toast.success("Welcome back!");
      navigate("/");
    } catch (err) {
      toast.error(err?.data?.message || "Login failed");
    }
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4">
      <div className="card p-8 w-full max-w-md">
        <h1 className="text-2xl font-bold flex items-center gap-2"><LogIn className="w-6 h-6 text-brand-600" /> Welcome back</h1>
        <p className="text-gray-600 text-sm mt-1">Sign in to manage your bookings</p>
        <form onSubmit={handle} className="mt-6 space-y-4">
          <div>
            <label className="text-sm font-medium text-gray-700">Email</label>
            <input className="input mt-1" type="email" required value={email} onChange={(e) => setEmail(e.target.value)} />
          </div>
          <div>
            <label className="text-sm font-medium text-gray-700">Password</label>
            <div className="relative mt-1">
              <input className="input pr-10" type={showPw ? "text" : "password"} required value={password} onChange={(e) => setPassword(e.target.value)} />
              <button type="button" onClick={() => setShowPw(!showPw)} className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600" tabIndex={-1}>
                {showPw ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </div>
          <button className="btn-primary w-full" disabled={isLoading}>{isLoading ? "Signing in..." : "Sign In"}</button>
        </form>
        <p className="text-center text-sm text-gray-600 mt-6">
          New here? <Link to="/register" className="text-brand-600 font-medium">Create an account</Link>
        </p>
        <div className="mt-4 p-3 bg-gray-50 rounded-lg text-xs text-gray-600">
          <strong>Default Admin:</strong> admin@vrs.com / Admin@123
        </div>
      </div>
    </div>
  );
}

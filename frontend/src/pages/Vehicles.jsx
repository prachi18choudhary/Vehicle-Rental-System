import { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useListVehiclesQuery } from "../api/api.js";
import VehicleCard from "../components/VehicleCard.jsx";
import { Search, Clock, X } from "lucide-react";

const TYPES = ["", "SEDAN", "SUV", "HATCHBACK", "LUXURY", "CONVERTIBLE", "BIKE", "SCOOTER", "TRUCK", "VAN"];
const RECENT_KEY = "vrs-recent-searches";
const MAX_RECENT = 5;

function getRecentSearches() {
  try { return JSON.parse(localStorage.getItem(RECENT_KEY)) || []; } catch { return []; }
}
function saveRecentSearch(query) {
  if (!query || query.trim().length < 2) return;
  const trimmed = query.trim();
  const existing = getRecentSearches().filter((s) => s !== trimmed);
  const updated = [trimmed, ...existing].slice(0, MAX_RECENT);
  localStorage.setItem(RECENT_KEY, JSON.stringify(updated));
}
function removeRecentSearch(query) {
  const updated = getRecentSearches().filter((s) => s !== query);
  localStorage.setItem(RECENT_KEY, JSON.stringify(updated));
}

export default function Vehicles() {
  const navigate = useNavigate();
  const [filters, setFilters] = useState({ q: "", type: "", location: "", minPrice: "", maxPrice: "" });
  const [page, setPage] = useState(0);

  // Autocomplete state
  const [searchInput, setSearchInput] = useState("");
  const [debouncedQuery, setDebouncedQuery] = useState("");
  const [showDropdown, setShowDropdown] = useState(false);
  const [recentSearches, setRecentSearches] = useState(getRecentSearches);
  const searchRef = useRef(null);

  // Debounce the search input (300ms)
  useEffect(() => {
    if (searchInput.length < 2) { setDebouncedQuery(""); return; }
    const timer = setTimeout(() => setDebouncedQuery(searchInput), 300);
    return () => clearTimeout(timer);
  }, [searchInput]);

  // Suggestion query — only fires when we have 2+ chars
  const { data: suggestions } = useListVehiclesQuery(
    { q: debouncedQuery, status: "AVAILABLE", size: 5 },
    { skip: debouncedQuery.length < 2 }
  );

  // Close dropdown on click outside
  useEffect(() => {
    const handler = (e) => {
      if (searchRef.current && !searchRef.current.contains(e.target)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  // Main grid query params
  const params = {
    ...Object.fromEntries(Object.entries(filters).filter(([_, v]) => v !== "")),
    status: "AVAILABLE",
    page,
    size: 12,
    sort: "createdAt,desc",
  };
  const { data, isLoading } = useListVehiclesQuery(params);
  const update = (k) => (e) => { setFilters({ ...filters, [k]: e.target.value }); setPage(0); };

  // Handle search submission (Enter key or selecting a filter)
  const commitSearch = (query) => {
    const q = query ?? searchInput;
    setFilters({ ...filters, q });
    setPage(0);
    setShowDropdown(false);
    saveRecentSearch(q);
    setRecentSearches(getRecentSearches());
  };

  const handleSearchKeyDown = (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      commitSearch();
    }
    if (e.key === "Escape") {
      setShowDropdown(false);
    }
  };

  const handleSuggestionClick = (vehicleId) => {
    saveRecentSearch(searchInput);
    setRecentSearches(getRecentSearches());
    setShowDropdown(false);
    navigate(`/vehicles/${vehicleId}`);
  };

  const handleRecentClick = (query) => {
    setSearchInput(query);
    commitSearch(query);
  };

  const handleRemoveRecent = (e, query) => {
    e.stopPropagation();
    removeRecentSearch(query);
    setRecentSearches(getRecentSearches());
  };

  const suggestionResults = suggestions?.content || [];
  const showSuggestions = showDropdown && debouncedQuery.length >= 2 && suggestionResults.length > 0;
  const showRecent = showDropdown && searchInput.length === 0 && recentSearches.length > 0;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold mb-2">Browse Vehicles</h1>
      <p className="text-gray-600 mb-6">{data?.totalElements || 0} vehicles available</p>

      <div className="card p-4 mb-6">
        <div className="grid grid-cols-1 md:grid-cols-5 gap-3">
          {/* Search with autocomplete */}
          <div className="md:col-span-2 relative" ref={searchRef}>
            <Search className="w-4 h-4 absolute left-3 top-3 text-gray-400 z-10" />
            <input
              className="input pl-9"
              placeholder="Search by name or brand..."
              value={searchInput}
              onChange={(e) => { setSearchInput(e.target.value); setShowDropdown(true); }}
              onFocus={() => setShowDropdown(true)}
              onKeyDown={handleSearchKeyDown}
              autoComplete="off"
            />

            {/* Suggestions dropdown */}
            {showSuggestions && (
              <div className="absolute top-full left-0 right-0 mt-1 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-600 rounded-lg shadow-lg z-50 overflow-hidden">
                {suggestionResults.map((v) => (
                  <button
                    key={v.id}
                    onClick={() => handleSuggestionClick(v.id)}
                    className="w-full flex items-center gap-3 px-4 py-2.5 hover:bg-gray-50 dark:hover:bg-gray-700 text-left transition-colors"
                  >
                    <img src={v.imageUrl || "https://placehold.co/40"} className="w-8 h-8 rounded object-cover flex-shrink-0" alt="" />
                    <div className="flex-1 min-w-0">
                      <div className="font-medium text-sm truncate">{v.name}</div>
                      <div className="text-xs text-gray-500">{v.brand} · {v.type} · ₹{Number(v.pricePerDay).toFixed(0)}/day</div>
                    </div>
                  </button>
                ))}
              </div>
            )}

            {/* Recent searches dropdown */}
            {showRecent && (
              <div className="absolute top-full left-0 right-0 mt-1 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-600 rounded-lg shadow-lg z-50 overflow-hidden">
                <div className="px-4 py-2 text-xs font-medium text-gray-400 uppercase tracking-wide">Recent Searches</div>
                {recentSearches.map((q) => (
                  <button
                    key={q}
                    onClick={() => handleRecentClick(q)}
                    className="w-full flex items-center gap-3 px-4 py-2 hover:bg-gray-50 dark:hover:bg-gray-700 text-left transition-colors"
                  >
                    <Clock className="w-3.5 h-3.5 text-gray-400 flex-shrink-0" />
                    <span className="text-sm flex-1">{q}</span>
                    <button
                      onClick={(e) => handleRemoveRecent(e, q)}
                      className="p-0.5 rounded hover:bg-gray-200 dark:hover:bg-gray-600"
                      title="Remove"
                    >
                      <X className="w-3 h-3 text-gray-400" />
                    </button>
                  </button>
                ))}
              </div>
            )}
          </div>

          <select className="input" value={filters.type} onChange={update("type")}>
            {TYPES.map((t) => <option key={t} value={t}>{t || "All Types"}</option>)}
          </select>
          <input className="input" placeholder="Location" value={filters.location} onChange={update("location")} />
          <div className="flex gap-2">
            <input className="input" placeholder="Min" type="number" value={filters.minPrice} onChange={update("minPrice")} />
            <input className="input" placeholder="Max" type="number" value={filters.maxPrice} onChange={update("maxPrice")} />
          </div>
        </div>
      </div>

      {isLoading ? (
        <div className="text-center text-gray-500 py-12">Loading...</div>
      ) : data?.content?.length === 0 ? (
        <div className="text-center text-gray-500 py-12">No vehicles match your filters.</div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {data?.content.map((v) => <VehicleCard key={v.id} v={v} />)}
          </div>
          {data?.totalPages > 1 && (
            <div className="flex justify-center gap-2 mt-8">
              <button className="btn-secondary" disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
              <span className="px-4 py-2 text-sm text-gray-600">Page {page + 1} of {data.totalPages}</span>
              <button className="btn-secondary" disabled={page + 1 >= data.totalPages} onClick={() => setPage(page + 1)}>Next</button>
            </div>
          )}
        </>
      )}
    </div>
  );
}

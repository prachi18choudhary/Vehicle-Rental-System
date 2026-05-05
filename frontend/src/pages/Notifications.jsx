import { useState } from "react";
import { useMyNotificationsQuery, useMarkAllReadMutation } from "../api/api.js";
import { Bell, Check } from "lucide-react";

export default function Notifications() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useMyNotificationsQuery({ page, size: 20 });
  const [markAll, { isLoading: marking }] = useMarkAllReadMutation();

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold flex items-center gap-2"><Bell className="w-6 h-6 text-brand-600" /> Notifications</h1>
        <button onClick={() => markAll()} disabled={marking} className="btn-secondary text-sm">
          <Check className="w-4 h-4" /> Mark all read
        </button>
      </div>
      {isLoading ? (
        <div className="text-center py-12 text-gray-500">Loading...</div>
      ) : data?.content?.length === 0 ? (
        <div className="card p-12 text-center text-gray-500">No notifications yet.</div>
      ) : (
        <div className="space-y-3">
          {data?.content.map((n) => (
            <div key={n.id} className={`card p-4 ${!n.read ? "border-l-4 border-brand-500" : ""}`}>
              <div className="flex justify-between items-start gap-4">
                <div className="flex-1">
                  <div className="flex items-center gap-2">
                    <h3 className="font-semibold">{n.title}</h3>
                    {!n.read && <span className="badge bg-brand-100 text-brand-700">New</span>}
                  </div>
                  <p className="text-sm text-gray-600 mt-1">{n.message}</p>
                  <p className="text-xs text-gray-400 mt-2">{new Date(n.createdAt).toLocaleString()}</p>
                </div>
                <span className="badge bg-gray-100 text-gray-600 text-[10px]">{n.type}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

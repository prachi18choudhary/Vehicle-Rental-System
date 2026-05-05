import { Bell } from "lucide-react";
import { Link } from "react-router-dom";
import { useUnreadCountQuery } from "../api/api.js";

export default function NotificationBell() {
  const { data } = useUnreadCountQuery(undefined, { pollingInterval: 30000 });
  const count = data?.count || 0;
  return (
    <Link to="/notifications" className="relative p-2 rounded-full hover:bg-gray-100" title="Notifications">
      <Bell className="w-5 h-5 text-gray-700" />
      {count > 0 && (
        <span className="absolute -top-0.5 -right-0.5 bg-red-500 text-white text-[10px] font-bold rounded-full min-w-[18px] h-[18px] flex items-center justify-center px-1">
          {count > 9 ? "9+" : count}
        </span>
      )}
    </Link>
  );
}

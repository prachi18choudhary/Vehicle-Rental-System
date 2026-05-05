import { useState, useRef } from "react";
import toast from "react-hot-toast";
import { useListVehiclesQuery, useCreateVehicleMutation, useUpdateVehicleMutation, useDeleteVehicleMutation, useUpdateVehicleStatusMutation } from "../api/api.js";
import { Plus, Edit, Trash2, X } from "lucide-react";

const TYPES = ["SEDAN", "SUV", "HATCHBACK", "LUXURY", "CONVERTIBLE", "BIKE", "SCOOTER", "TRUCK", "VAN"];
const FUELS = ["PETROL", "DIESEL", "ELECTRIC", "HYBRID", "CNG"];
const TRANS = ["MANUAL", "AUTOMATIC"];
const STATUSES = ["AVAILABLE", "RENTED", "MAINTENANCE", "INACTIVE"];

const empty = {
  name: "", brand: "", type: "SEDAN", transmission: "MANUAL", fuel: "PETROL",
  seats: 5, pricePerDay: "", location: "", imageUrl: "", description: "", licensePlate: "", yearMade: 2024,
  status: "AVAILABLE",
};

export default function AdminVehicles() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useListVehiclesQuery({ page, size: 10 });
  const [create] = useCreateVehicleMutation();
  const [update] = useUpdateVehicleMutation();
  const [del] = useDeleteVehicleMutation();
  const [updateStatus] = useUpdateVehicleStatusMutation();
  const [editing, setEditing] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState(empty);
  const originalStatus = useRef(null);

  const open = (v) => {
    if (v) {
      setEditing(v.id);
      setForm({ ...v });
      originalStatus.current = v.status;
    } else {
      setEditing(null);
      setForm(empty);
      originalStatus.current = null;
    }
    setShowModal(true);
  };
  const close = () => { setShowModal(false); setEditing(null); setForm(empty); };

  const submit = async (e) => {
    e.preventDefault();
    try {
      const { status, ...rest } = form;
      const body = { ...rest, seats: Number(rest.seats), yearMade: Number(rest.yearMade), pricePerDay: Number(rest.pricePerDay) };
      if (editing) {
        await update({ id: editing, body }).unwrap();
        if (status !== originalStatus.current) {
          await updateStatus({ id: editing, status }).unwrap();
        }
        toast.success("Vehicle updated");
      } else {
        await create(body).unwrap();
        toast.success("Vehicle created");
      }
      close();
    } catch (err) {
      toast.error(err?.data?.message || "Save failed");
    }
  };

  const remove = async (id) => {
    if (!confirm("Delete this vehicle?")) return;
    try {
      await del(id).unwrap();
      toast.success("Deleted");
    } catch (err) {
      toast.error(err?.data?.message || "Delete failed");
    }
  };

  const update_ = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Manage Vehicles</h1>
        <button onClick={() => open(null)} className="btn-primary"><Plus className="w-4 h-4" /> Add Vehicle</button>
      </div>

      <div className="card overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 text-left text-xs uppercase text-gray-500">
            <tr>
              <th className="p-3">Vehicle</th><th className="p-3">Type</th><th className="p-3">Location</th>
              <th className="p-3">Price/day</th><th className="p-3">Status</th><th className="p-3"></th>
            </tr>
          </thead>
          <tbody>
            {isLoading ? <tr><td colSpan="6" className="p-6 text-center text-gray-500">Loading...</td></tr> :
              data?.content.map((v) => (
                <tr key={v.id} className="border-t border-gray-100">
                  <td className="p-3">
                    <div className="flex items-center gap-3">
                      <img src={v.imageUrl || "https://placehold.co/40"} className="w-10 h-10 object-cover rounded" />
                      <div>
                        <div className="font-medium">{v.name}</div>
                        <div className="text-xs text-gray-500">{v.brand}</div>
                      </div>
                    </div>
                  </td>
                  <td className="p-3">{v.type}</td>
                  <td className="p-3">{v.location}</td>
                  <td className="p-3">₹{Number(v.pricePerDay).toFixed(0)}</td>
                  <td className="p-3"><span className="badge bg-gray-100 text-gray-700">{v.status}</span></td>
                  <td className="p-3 flex gap-2">
                    <button onClick={() => open(v)} className="btn-secondary text-xs"><Edit className="w-3 h-3" /></button>
                    <button onClick={() => remove(v.id)} className="btn-danger text-xs"><Trash2 className="w-3 h-3" /></button>
                  </td>
                </tr>
              ))}
          </tbody>
        </table>
      </div>

      {data?.totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-4">
          <button className="btn-secondary" disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
          <span className="px-4 py-2 text-sm">Page {page + 1} of {data.totalPages}</span>
          <button className="btn-secondary" disabled={page + 1 >= data.totalPages} onClick={() => setPage(page + 1)}>Next</button>
        </div>
      )}

      {showModal && (
        <div className="fixed inset-0 z-50 bg-black/40 flex items-center justify-center p-4">
          <div className="bg-white rounded-xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
            <div className="p-5 border-b flex justify-between items-center">
              <h2 className="font-bold text-lg">{editing ? "Edit Vehicle" : "New Vehicle"}</h2>
              <button onClick={close}><X className="w-5 h-5 text-gray-500" /></button>
            </div>
            <form onSubmit={submit} className="p-5 grid grid-cols-1 md:grid-cols-2 gap-4">
              <Field label="Name"><input className="input" required value={form.name} onChange={update_("name")} /></Field>
              <Field label="Brand"><input className="input" required value={form.brand} onChange={update_("brand")} /></Field>
              <Field label="Type"><select className="input" value={form.type} onChange={update_("type")}>{TYPES.map((t) => <option key={t}>{t}</option>)}</select></Field>
              <Field label="Transmission"><select className="input" value={form.transmission} onChange={update_("transmission")}>{TRANS.map((t) => <option key={t}>{t}</option>)}</select></Field>
              <Field label="Fuel"><select className="input" value={form.fuel} onChange={update_("fuel")}>{FUELS.map((f) => <option key={f}>{f}</option>)}</select></Field>
              <Field label="Seats"><input type="number" min="1" className="input" required value={form.seats} onChange={update_("seats")} /></Field>
              <Field label="Year"><input type="number" className="input" value={form.yearMade} onChange={update_("yearMade")} /></Field>
              <Field label="Price per day (₹)"><input type="number" step="0.01" className="input" required value={form.pricePerDay} onChange={update_("pricePerDay")} /></Field>
              <Field label="Location"><input className="input" required value={form.location} onChange={update_("location")} /></Field>
              <Field label="License Plate"><input className="input" value={form.licensePlate} onChange={update_("licensePlate")} /></Field>
              <Field label="Image URL" full><input className="input" value={form.imageUrl} onChange={update_("imageUrl")} placeholder="https://..." /></Field>
              <Field label="Description" full><textarea rows="2" className="input" value={form.description} onChange={update_("description")} /></Field>
              {editing && (
                <Field label="Status"><select className="input" value={form.status} onChange={update_("status")}>{STATUSES.map((s) => <option key={s}>{s}</option>)}</select></Field>
              )}
              <div className="md:col-span-2 flex gap-2 justify-end pt-2 border-t">
                <button type="button" onClick={close} className="btn-secondary">Cancel</button>
                <button type="submit" className="btn-primary">Save</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

function Field({ label, children, full }) {
  return (
    <div className={full ? "md:col-span-2" : ""}>
      <label className="text-sm font-medium text-gray-700 mb-1 block">{label}</label>
      {children}
    </div>
  );
}

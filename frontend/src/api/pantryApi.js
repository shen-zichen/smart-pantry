import api from "./client";

const pantryApi = {
  // GET /api/pantry — get all pantry items
  getAll: () => api.get("/pantry").then((res) => res.data),

  // GET /api/pantry/:id — get one pantry item
  getById: (id) => api.get(`/pantry/${id}`).then((res) => res.data),

  // POST /api/pantry — add a new pantry item
  create: (data) => api.post("/pantry", data).then((res) => res.data),

  // PUT /api/pantry/:id/consume?amount=X — consume from an item
  consume: (id, amount) =>
    api.put(`/pantry/${id}/consume?amount=${amount}`).then((res) => res.data),

  // PUT /api/pantry/:id/restock?amount=X — restock an item
  restock: (id, amount) =>
    api.put(`/pantry/${id}/restock?amount=${amount}`).then((res) => res.data),

  // DELETE /api/pantry/:id — delete an item
  delete: (id) => api.delete(`/pantry/${id}`),

  // GET /api/pantry/search?name=X — search by name
  search: (name) =>
    api.get(`/pantry/search?name=${name}`).then((res) => res.data),
};

export default pantryApi;

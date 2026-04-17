import api from "./client";

const twinRecipeApi = {
  getAll: () => api.get("/twin-recipes").then((res) => res.data),

  getById: (id) => api.get(`/twin-recipes/${id}`).then((res) => res.data),

  create: (data) => api.post("/twin-recipes", data).then((res) => res.data),

  swap: (id) => api.put(`/twin-recipes/${id}/swap`).then((res) => res.data),

  delete: (id) => api.delete(`/twin-recipes/${id}`),
};

export default twinRecipeApi;

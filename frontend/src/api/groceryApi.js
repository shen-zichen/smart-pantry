import api from "./client";

const groceryApi = {
  getList: (planId) =>
    api.get(`/grocery?planId=${planId}`).then((res) => res.data),

  markBought: (items) =>
    api.post("/grocery/bought", items).then((res) => res.data),
};

export default groceryApi;

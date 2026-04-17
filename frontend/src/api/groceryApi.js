import api from "./client";

const groceryApi = {
  getList: (planId) =>
    api.get(`/grocery?planId=${planId}`).then((res) => res.data),
};

export default groceryApi;

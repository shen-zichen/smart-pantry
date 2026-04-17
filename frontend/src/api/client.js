import axios from "axios";

// Create a reusable axios instance with default settings
const api = axios.create({
  baseURL: "/api", // proxy forwards this to localhost:8080/api
  headers: {
    "Content-Type": "application/json",
  },
});

// Automatically handle errors in one place
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Pull out the error message from our backend's JSON format
    const message =
      error.response?.data?.message || error.message || "Something went wrong";
    console.error("API Error:", message);
    return Promise.reject(error);
  },
);

export default api;

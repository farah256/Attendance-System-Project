import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";


const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem("token");

  if (!token) {
    return <Navigate to="/" replace />;
  }

  try {
    // Décode le token pour vérifier s'il est valide
    const decodedToken = jwtDecode(token);
    const currentTime = Date.now() / 1000;

    // Si le token est expiré, redirige vers la page de connexion
    if (decodedToken.exp < currentTime) {
      localStorage.removeItem("token");
      return <Navigate to="/" replace />;
    }

    // Si le token est valide, on affiche le composant enfant
    return children;
  } catch (error) {
    localStorage.removeItem("token");
    return <Navigate to="/" replace />;
  }
};

export default ProtectedRoute;

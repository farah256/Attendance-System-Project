
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import React from 'react'
import Login from './Components/Login'
import Dashboard from "./pages/Dashboard";
import ListeClasses from "./pages/ListeClasses";
import NotesPage from "./pages/NotesPage";
import AjouterEtudiant from "./pages/AjouterEtudiant";
import EditEtudiant from "./pages/EditEtudiant";
import JustificationPage from "./pages/JustificationPage.jsx";
import Absences from "./pages/Absences.jsx";
import AlertesAbsences from "./pages/Alertes.jsx";





import ProtectedRoute from "./Components/ProtectedRoute";
import './App.css'
import Etudiants from "./pages/Etudiants.jsx";


const App = () => {
  return (
    <Router>
      <Routes>
        {/* Page publique */}
        <Route path="/" element={<Login />} />
        <Route path="/justifications/:studentId" element={<JustificationPage />} />

        {/* Pages protégées
        <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />

          */
        }
        <Route path="/dashboard" element={<Dashboard  />} />
        <Route path="/classes" element={<ListeClasses />} />

        <Route path="/notes" element={<NotesPage />} />
        <Route path="/etudiants" element={<Etudiants />} />

        <Route path="/etudiants/ajouter" element={<AjouterEtudiant />} />
        <Route path="/etudiants/:id" element={<EditEtudiant />} />
        <Route path="/absences" element={<Absences />} />
        <Route path="/alertes" element={<AlertesAbsences />} />


      </Routes>
  </Router>
  );
};

export default App
